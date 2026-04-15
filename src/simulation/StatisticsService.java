package simulation;

import config.AnimalType;
import entity.Animal;
import entity.AnimalTypeResolver;
import model.Island;
import model.Location;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class StatisticsService {

    public void printTickStats(Island island, int tick) {
        StatsData statsData = collectStats(island);

        System.out.println("Tick #" + tick
                + " | Total animals: " + statsData.totalAnimals
                + " | Plants: " + statsData.plantCount);
        printAnimalsByType(statsData.animalsByType);
    }

    public int countTotalAliveAnimals(Island island) {
        return collectStats(island).totalAnimals;
    }

    private StatsData collectStats(Island island) {
        StatsData data = new StatsData();

        for (Island.LocationPoint point : island.allPoints()) {
            Location location = point.getLocation();
            data.plantCount += location.getPlantsCount();

            List<Animal> animals = location.getAnimalsSnapshot();
            for (Animal animal : animals) {
                if (!animal.isAlive()) {
                    continue;
                }
                AnimalType type = AnimalTypeResolver.resolve(animal);
                increaseCount(data.animalsByType, type);
                data.totalAnimals++;
            }
        }
        return data;
    }

    private void printAnimalsByType(Map<AnimalType, Integer> animalsByType) {
        for (Map.Entry<AnimalType, Integer> entry : animalsByType.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private void increaseCount(Map<AnimalType, Integer> animalsByType, AnimalType type) {
        if (!animalsByType.containsKey(type)) {
            animalsByType.put(type, 0);
        }
        animalsByType.put(type, animalsByType.get(type) + 1);
    }

    private static class StatsData {
        private final Map<AnimalType, Integer> animalsByType = new EnumMap<AnimalType, Integer>(AnimalType.class);
        private int plantCount;
        private int totalAnimals;
    }
}
