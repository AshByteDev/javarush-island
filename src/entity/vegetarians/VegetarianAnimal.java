package entity.vegetarians;

import config.AnimalConfig;
import entity.Animal;

public abstract class VegetarianAnimal extends Animal {

    protected VegetarianAnimal(AnimalConfig config) {
        super(config);
    }

}