package entity.vegetarians;

import config.AnimalConfig;

public class Horse extends VegetarianAnimal {

    public Horse(AnimalConfig config) {
        super(config);
    }

    @Override
    public void eat() {
        super.eat();
    }

    @Override
    public void move() {
        super.move();
    }

    @Override
    public void reproduce() {
        super.reproduce();
    }

    @Override
    public void dead() {
        super.dead();
    }
}
