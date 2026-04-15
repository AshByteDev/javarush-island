package simulation;

import entity.Animal;
import entity.AnimalActionContext;
import entity.AnimalFactory;
import config.Configuration;
import model.Island;
import model.Location;

import java.util.List;

public class MovementService {
    private final Configuration configuration;
    private final AnimalFactory animalFactory;

    public MovementService(Configuration configuration, AnimalFactory animalFactory) {
        this.configuration = configuration;
        this.animalFactory = animalFactory;
    }

    public void processRange(Island island, int yStart, int yEndExclusive, int tick) {
        for (int y = yStart; y < yEndExclusive; y++) {
            for (int x = 0; x < island.getWidth(); x++) {
                Location from = island.getLocation(x, y);
                List<Animal> animals = from.getAnimalsSnapshot();
                for (Animal animal : animals) {
                    if (!animal.isAlive()) {
                        continue;
                    }
                    animal.setActionContext(new AnimalActionContext(
                            island,
                            from,
                            x,
                            y,
                            tick,
                            configuration,
                            animalFactory
                    ));
                    animal.move();
                }
            }
        }
    }
}
