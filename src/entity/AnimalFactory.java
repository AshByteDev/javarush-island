package entity;

import config.AnimalConfig;
import config.AnimalType;
import config.Configuration;
import entity.predators.Bear;
import entity.predators.Boa;
import entity.predators.Eagle;
import entity.predators.Fox;
import entity.predators.Wolf;
import entity.vegetarians.Boar;
import entity.vegetarians.Buffalo;
import entity.vegetarians.Caterpillar;
import entity.vegetarians.Deer;
import entity.vegetarians.Duck;
import entity.vegetarians.Goat;
import entity.vegetarians.Horse;
import entity.vegetarians.Mouse;
import entity.vegetarians.Rabbit;
import entity.vegetarians.Sheep;

public class AnimalFactory {
    private final Configuration configuration;

    public AnimalFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public Animal create(AnimalType type) {
        AnimalConfig config = configuration.getAnimalConfig(type);
        if (config == null) {
            throw new IllegalStateException("Animal config is not found for type: " + type);
        }

        switch (type) {
            case WOLF:
                return new Wolf(config);
            case BOA:
                return new Boa(config);
            case FOX:
                return new Fox(config);
            case BEAR:
                return new Bear(config);
            case EAGLE:
                return new Eagle(config);
            case HORSE:
                return new Horse(config);
            case DEER:
                return new Deer(config);
            case RABBIT:
                return new Rabbit(config);
            case MOUSE:
                return new Mouse(config);
            case GOAT:
                return new Goat(config);
            case SHEEP:
                return new Sheep(config);
            case BOAR:
                return new Boar(config);
            case BUFFALO:
                return new Buffalo(config);
            case DUCK:
                return new Duck(config);
            case CATERPILLAR:
                return new Caterpillar(config);
            default:
                throw new IllegalArgumentException("Unsupported animal type for factory: " + type);
        }
    }
}
