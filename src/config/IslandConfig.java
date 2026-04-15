package config;

public class IslandConfig {
    private final int width;
    private final int height;

    public IslandConfig(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
