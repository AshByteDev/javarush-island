package entity.predators;

import config.AnimalConfig;
import entity.Animal;

public abstract class PredatorAnimal extends Animal {

    protected PredatorAnimal(AnimalConfig config) {
        super(config);
    }

}
