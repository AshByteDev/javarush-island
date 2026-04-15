package simulation;

import config.Configuration;
import entity.Animal;
import entity.AnimalActionContext;
import entity.AnimalFactory;
import model.Island;
import model.Location;

import java.util.List;

public class DeathService {
    private final Configuration configuration;
    private final AnimalFactory animalFactory;

    public DeathService(Configuration configuration, AnimalFactory animalFactory) {
        this.configuration = configuration;
        this.animalFactory = animalFactory;
    }

    public void processRange(Island island, int yStart, int yEndExclusive, int tick) {
        for (int y = yStart; y < yEndExclusive; y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                Location location = island.getLocation(x, y);
                List<Animal> animals = location.getAnimalsSnapshot();
                for (Animal animal : animals) {
                    animal.setActionContext(new AnimalActionContext(
                            island,
                            location,
                            x,
                            y,
                            tick,
                            configuration,
                            animalFactory
                    ));
                    animal.dead();
                }
                location.removeDeadAnimals();
            }
        }
    }
}
