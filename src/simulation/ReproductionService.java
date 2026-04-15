package simulation;

import config.AnimalType;
import entity.Animal;
import entity.AnimalActionContext;
import entity.AnimalFactory;
import entity.AnimalTypeResolver;
import config.Configuration;
import model.Island;
import model.Location;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ReproductionService {
    private final Configuration configuration;
    private final AnimalFactory animalFactory;

    public ReproductionService(Configuration configuration, AnimalFactory animalFactory) {
        this.configuration = configuration;
        this.animalFactory = animalFactory;
    }

    public void processRange(Island island, int yStart, int yEndExclusive, int tick) {
        for (int y = yStart; y < yEndExclusive; y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                reproduceInLocation(island, x, y, tick, island.getLocation(x, y));
            }
        }
    }

    private void reproduceInLocation(Island island, int x, int y, int tick, Location location) {
        Map<AnimalType, Integer> aliveByType = new EnumMap<AnimalType, Integer>(AnimalType.class);
        Map<AnimalType, Animal> representativeByType = new EnumMap<AnimalType, Animal>(AnimalType.class);
        List<Animal> animals = location.getAnimalsSnapshot();

        for (Animal animal : animals) {
            if (!animal.isAlive()) {
                continue;
            }
            AnimalType type = AnimalTypeResolver.resolve(animal);
            Integer count = aliveByType.get(type);
            aliveByType.put(type, count == null ? 1 : count + 1);
            if (!representativeByType.containsKey(type)) {
                representativeByType.put(type, animal);
            }
        }

        for (Map.Entry<AnimalType, Integer> entry : aliveByType.entrySet()) {
            if (entry.getValue() >= 2) {
                Animal representative = representativeByType.get(entry.getKey());
                representative.setActionContext(new AnimalActionContext(
                        island,
                        location,
                        x,
                        y,
                        tick,
                        configuration,
                        animalFactory
                ));
                representative.reproduce();
            }
        }
    }
}
