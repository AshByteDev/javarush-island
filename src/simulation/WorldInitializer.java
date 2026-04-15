package simulation;

import config.AnimalConfig;
import config.AnimalType;
import config.Configuration;
import entity.AnimalFactory;
import entity.Plant;
import model.Island;
import model.Location;
import util.Randomizer;

public class WorldInitializer {
    private final Configuration configuration;
    private final AnimalFactory animalFactory;

    public WorldInitializer(Configuration configuration, AnimalFactory animalFactory) {
        this.configuration = configuration;
        this.animalFactory = animalFactory;
    }

    public void populate(Island island) {
        int plantsOnStart = configuration.getSimulationConfig().getStartPlantsPerLocation();
        populatePlants(island, plantsOnStart);
        populateAnimalsPerCell(island);
    }

    public void populatePlants(Island island, int plantsPerLocation) {
        AnimalConfig plantConfig = getPlantConfig();
        if (plantConfig == null) {
            return;
        }
        double plantWeight = plantConfig.getWeight();

        for (Island.LocationPoint point : island.allPoints()) {
            Location location = point.getLocation();
            for (int i = 0; i < plantsPerLocation; i++) {
                location.addPlant(new Plant(plantWeight));
            }
        }
    }

    public void growPlants(Island island, int growthPerLocation) {
        AnimalConfig plantConfig = getPlantConfig();
        if (plantConfig == null) {
            return;
        }

        int maxPlants = plantConfig.getMaxPerCell();
        double plantWeight = plantConfig.getWeight();

        for (Island.LocationPoint point : island.allPoints()) {
            Location location = point.getLocation();
            growPlantsInLocation(location, growthPerLocation, maxPlants, plantWeight);
        }
    }

    /**
     * На каждой клетке для каждого вида создаётся случайное число особей от 0 до max_per_cell (включительно).
     */
    private void populateAnimalsPerCell(Island island) {
        for (Island.LocationPoint point : island.allPoints()) {
            Location location = point.getLocation();
            populateAnimalsInLocation(location);
        }
    }

    private void populateAnimalsInLocation(Location location) {
        for (AnimalType type : AnimalType.values()) {
            if (type == AnimalType.PLANT) {
                continue;
            }

            AnimalConfig animalConfig = configuration.getAnimalConfig(type);
            if (animalConfig == null) {
                continue;
            }

            int maxPerCell = animalConfig.getMaxPerCell();
            int count = Randomizer.nextIntInclusive(maxPerCell);
            addAnimals(location, type, count);
        }
    }

    private void addAnimals(Location location, AnimalType type, int count) {
        for (int i = 0; i < count; i++) {
            location.addAnimal(animalFactory.create(type));
        }
    }

    private void growPlantsInLocation(Location location, int growthPerLocation, int maxPlants, double plantWeight) {
        int canGrow = maxPlants - location.getPlantsCount();
        int growCount = Math.min(growthPerLocation, Math.max(0, canGrow));
        for (int i = 0; i < growCount; i++) {
            location.addPlant(new Plant(plantWeight));
        }
    }

    private AnimalConfig getPlantConfig() {
        return configuration.getAnimalConfig(AnimalType.PLANT);
    }
}
