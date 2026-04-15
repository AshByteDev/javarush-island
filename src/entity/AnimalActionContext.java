package entity;

import config.Configuration;
import model.Island;
import model.Location;

public class AnimalActionContext {
    private final Island island;
    private final Location location;
    private final int x;
    private final int y;
    private final int tick;
    private final Configuration configuration;
    private final AnimalFactory animalFactory;

    public AnimalActionContext(Island island,
                               Location location,
                               int x,
                               int y,
                               int tick,
                               Configuration configuration,
                               AnimalFactory animalFactory) {
        this.island = island;
        this.location = location;
        this.x = x;
        this.y = y;
        this.tick = tick;
        this.configuration = configuration;
        this.animalFactory = animalFactory;
    }

    public Island getIsland() {
        return island;
    }

    public Location getLocation() {
        return location;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTick() {
        return tick;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public AnimalFactory getAnimalFactory() {
        return animalFactory;
    }
}
