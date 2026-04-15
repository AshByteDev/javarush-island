package entity;

import config.AnimalType;
import config.AnimalConfig;
import entity.actions.Eat;
import entity.actions.Reproduction;
import entity.actions.SelectMove;
import model.Island;
import model.Location;
import util.Randomizer;

import java.util.List;
import java.util.Map;

public abstract class Animal implements Eat, Reproduction, SelectMove {

    protected final double weight;
    protected final int maxCount;
    protected final int speed;
    protected final double foodNeeded;
    protected final double maxSatiety;
    protected double foodEaten = 0;
    protected double satiety;
    protected boolean alive = true;
    private AnimalActionContext actionContext;
    // Guards from repeated phase execution in same tick after moving between cells.
    private final Object tickGuard = new Object();
    private int eatProcessedTick = -1;
    private int moveProcessedTick = -1;
    private int reproduceProcessedTick = -1;
    private int deathProcessedTick = -1;

    protected Animal(AnimalConfig config) {
        this.weight = config.getWeight();
        this.maxCount = config.getMaxPerCell();
        this.speed = config.getSpeed();
        this.foodNeeded = config.getFoodNeeded();
        this.maxSatiety = config.getFoodNeeded();
        this.satiety = maxSatiety;
    }

    public double getWeight() {
        return weight;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getSpeed() {
        return speed;
    }

    public double getFoodNeeded() {
        return foodNeeded;
    }

    public double getFoodEaten() {
        return foodEaten;
    }

    public double getSatiety() {
        return satiety;
    }

    public boolean isAlive() {
        return alive;
    }

    public void addFood(double foodWeight) {
        if (!alive) {
            return;
        }
        foodEaten += foodWeight;
    }

    public void markDead() {
        alive = false;
    }

    public void finishTick() {
        if (!alive) {
            foodEaten = 0;
            return;
        }

        satiety += foodEaten - foodNeeded;
        if (satiety > maxSatiety) {
            satiety = maxSatiety;
        }
        if (satiety < 0) {
            markDead();
        }
        foodEaten = 0;
    }

    public void setActionContext(AnimalActionContext actionContext) {
        this.actionContext = actionContext;
    }

    public void eat() {
        if (!alive) {
            return;
        }

        AnimalActionContext context = requireContext();
        int tick = context.getTick();
        synchronized (tickGuard) {
            if (eatProcessedTick == tick) {
                return;
            }
            eatProcessedTick = tick;
        }

        AnimalType eaterType = AnimalTypeResolver.resolve(this);
        Map<AnimalType, Integer> preyMap = context.getConfiguration().getEatProbabilities().get(eaterType);
        if (preyMap == null || preyMap.isEmpty()) {
            return;
        }

        boolean atePlant = tryEatPlant(preyMap, context.getLocation());
        if (!atePlant) {
            tryEatAnimal(preyMap, context.getLocation(), eaterType);
        }
    }

    public void move() {
        if (!alive || speed <= 0) {
            return;
        }

        AnimalActionContext context = requireContext();
        int tick = context.getTick();
        synchronized (tickGuard) {
            if (moveProcessedTick == tick) {
                return;
            }
            moveProcessedTick = tick;
        }

        Island island = context.getIsland();
        int dx = Randomizer.nextSignedDelta(speed);
        int dy = Randomizer.nextSignedDelta(speed);
        if (dx == 0 && dy == 0) {
            return;
        }

        int targetX = clamp(context.getX() + dx, 0, island.getWidth() - 1);
        int targetY = clamp(context.getY() + dy, 0, island.getHeight() - 1);
        if (targetX == context.getX() && targetY == context.getY()) {
            return;
        }

        Location from = context.getLocation();
        Location to = island.getLocation(targetX, targetY);
        AnimalType type = AnimalTypeResolver.resolve(this);
        if (to.countAliveAnimalsByType(type) >= maxCount) {
            return;
        }
        if (from.removeAnimal(this)) {
            to.addAnimal(this);
        }
    }

    public void reproduce() {
        if (!alive) {
            return;
        }

        AnimalActionContext context = requireContext();
        int tick = context.getTick();
        synchronized (tickGuard) {
            if (reproduceProcessedTick == tick) {
                return;
            }
            reproduceProcessedTick = tick;
        }

        Location location = context.getLocation();
        AnimalType type = AnimalTypeResolver.resolve(this);

        int sameTypeCount = location.countAliveAnimalsByType(type);
        if (sameTypeCount < 2 || sameTypeCount >= maxCount) {
            return;
        }

        int offspringCount = context.getConfiguration().getOffspringCount(type);
        int possible = Math.min(offspringCount, Math.max(0, maxCount - sameTypeCount));
        for (int i = 0; i < possible; i++) {
            location.addAnimal(context.getAnimalFactory().create(type));
        }
    }

    public void dead() {
        AnimalActionContext context = requireContext();
        int tick = context.getTick();
        synchronized (tickGuard) {
            if (deathProcessedTick == tick) {
                return;
            }
            deathProcessedTick = tick;
        }

        finishTick();
        if (!alive) {
            context.getLocation().removeAnimal(this);
        }
    }

    private AnimalActionContext requireContext() {
        if (actionContext == null) {
            throw new IllegalStateException("AnimalActionContext is not set for " + getClass().getSimpleName());
        }
        return actionContext;
    }

    private boolean tryEatPlant(Map<AnimalType, Integer> preyMap, Location location) {
        Integer probability = preyMap.get(AnimalType.PLANT);
        if (probability == null || probability <= 0) {
            return false;
        }

        List<Plant> plants = location.getPlantsSnapshot();
        if (plants.isEmpty()) {
            return false;
        }

        if (isSuccessful(probability)) {
            Plant plant = plants.get(0);
            if (location.removePlant(plant)) {
                addFood(plant.getWeight());
                return true;
            }
        }
        return false;
    }

    private boolean tryEatAnimal(Map<AnimalType, Integer> preyMap, Location location, AnimalType eaterType) {
        for (Map.Entry<AnimalType, Integer> entry : preyMap.entrySet()) {
            AnimalType preyType = entry.getKey();
            if (preyType == AnimalType.PLANT || preyType == eaterType) {
                continue;
            }
            int probability = entry.getValue();
            if (probability <= 0) {
                continue;
            }

            Animal prey = findAlivePrey(location, preyType);
            if (prey == null) {
                continue;
            }

            if (isSuccessful(probability)) {
                prey.markDead();
                addFood(prey.getWeight());
                return true;
            }
        }
        return false;
    }

    private Animal findAlivePrey(Location location, AnimalType preyType) {
        List<Animal> animals = location.getAnimalsSnapshot();
        for (Animal animal : animals) {
            if (animal != this && animal.isAlive() && AnimalTypeResolver.resolve(animal) == preyType) {
                return animal;
            }
        }
        return null;
    }

    private boolean isSuccessful(int probability) {
        return Randomizer.chance(probability);
    }

    private int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}