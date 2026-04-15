package entity;

import config.AnimalType;
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

public final class AnimalTypeResolver {

    private AnimalTypeResolver() {
    }

    public static AnimalType resolve(Animal animal) {
        if (animal instanceof Wolf) {
            return AnimalType.WOLF;
        }
        if (animal instanceof Boa) {
            return AnimalType.BOA;
        }
        if (animal instanceof Fox) {
            return AnimalType.FOX;
        }
        if (animal instanceof Bear) {
            return AnimalType.BEAR;
        }
        if (animal instanceof Eagle) {
            return AnimalType.EAGLE;
        }
        if (animal instanceof Horse) {
            return AnimalType.HORSE;
        }
        if (animal instanceof Deer) {
            return AnimalType.DEER;
        }
        if (animal instanceof Rabbit) {
            return AnimalType.RABBIT;
        }
        if (animal instanceof Mouse) {
            return AnimalType.MOUSE;
        }
        if (animal instanceof Goat) {
            return AnimalType.GOAT;
        }
        if (animal instanceof Sheep) {
            return AnimalType.SHEEP;
        }
        if (animal instanceof Boar) {
            return AnimalType.BOAR;
        }
        if (animal instanceof Buffalo) {
            return AnimalType.BUFFALO;
        }
        if (animal instanceof Duck) {
            return AnimalType.DUCK;
        }
        if (animal instanceof Caterpillar) {
            return AnimalType.CATERPILLAR;
        }
        throw new IllegalArgumentException("Unsupported animal class: " + animal.getClass().getName());
    }
}
