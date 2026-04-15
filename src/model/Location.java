package model;

import config.AnimalType;
import entity.Animal;
import entity.AnimalTypeResolver;
import entity.Plant;

import java.util.ArrayList;
import java.util.List;

public class Location {
    private final List<Animal> animals = new ArrayList<Animal>();
    private final List<Plant> plants = new ArrayList<Plant>();

    public synchronized void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public synchronized boolean removeAnimal(Animal animal) {
        return animals.remove(animal);
    }

    public synchronized void addPlant(Plant plant) {
        plants.add(plant);
    }

    public synchronized boolean removePlant(Plant plant) {
        return plants.remove(plant);
    }

    public synchronized List<Animal> getAnimalsSnapshot() {
        return new ArrayList<Animal>(animals);
    }

    public synchronized List<Plant> getPlantsSnapshot() {
        return new ArrayList<Plant>(plants);
    }

    public synchronized int getPlantsCount() {
        return plants.size();
    }

    public synchronized boolean containsAnimal(Animal animal) {
        return animals.contains(animal);
    }

    public synchronized int countAliveAnimalsByType(AnimalType animalType) {
        int count = 0;
        for (Animal animal : animals) {
            if (animal.isAlive() && AnimalTypeResolver.resolve(animal) == animalType) {
                count++;
            }
        }
        return count;
    }

    public synchronized void removeDeadAnimals() {
        animals.removeIf(animal -> !animal.isAlive());
    }
}
