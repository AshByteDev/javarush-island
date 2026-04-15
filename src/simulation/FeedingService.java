package simulation;

import config.Configuration;
import entity.Animal;
import entity.AnimalActionContext;
import entity.AnimalFactory;
import model.Island;
import model.Location;

import java.util.List;

public class FeedingService {
    private final Configuration configuration;
    private final AnimalFactory animalFactory;

    public FeedingService(Configuration configuration, AnimalFactory animalFactory) {
        this.configuration = configuration;
        this.animalFactory = animalFactory;
    }

    public void processRange(Island island, int yStart, int yEndExclusive, int tick) {
        for (int y = yStart; y < yEndExclusive; y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                processLocation(island, x, y, tick, island.getLocation(x, y));
            }
        }
    }

    private void processLocation(Island island, int x, int y, int tick, Location location) {
        List<Animal> animals = location.getAnimalsSnapshot();
        for (Animal animal : animals) {
            if (!animal.isAlive()) {
                continue;
            }
            animal.setActionContext(new AnimalActionContext(
                    island,
                    location,
                    x,
                    y,
                    tick,
                    configuration,
                    animalFactory
            ));
            animal.eat();
        }
    }
}
