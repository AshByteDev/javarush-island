package model;

import java.util.ArrayList;
import java.util.List;

public class Island {
    private final int width;
    private final int height;
    private final Location[][] locations;

    public Island(int width, int height) {
        this.width = width;
        this.height = height;
        this.locations = new Location[height][width];
        initLocations();
    }

    private void initLocations() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                locations[y][x] = new Location();
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Location getLocation(int x, int y) {
        if (!isInside(x, y)) {
            throw new IllegalArgumentException("Coordinates out of island bounds: x=" + x + ", y=" + y);
        }
        return locations[y][x];
    }

    public List<LocationPoint> allPoints() {
        List<LocationPoint> points = new ArrayList<LocationPoint>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                points.add(new LocationPoint(x, y, locations[y][x]));
            }
        }
        return points;
    }

    public static class LocationPoint {
        private final int x;
        private final int y;
        private final Location location;

        public LocationPoint(int x, int y, Location location) {
            this.x = x;
            this.y = y;
            this.location = location;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Location getLocation() {
            return location;
        }
    }
}
