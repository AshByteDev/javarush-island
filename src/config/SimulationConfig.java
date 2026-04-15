package config;

public class SimulationConfig {
    private final long tickDurationMs;
    private final int maxTicks;
    private final boolean stopWhenNoAnimals;
    private final int scheduledPoolSize;
    private final int workerPoolSize;
    private final int plantGrowthPerTick;
    private final int startPlantsPerLocation;
    private final int defaultOffspringCount;

    public SimulationConfig(long tickDurationMs,
                            int maxTicks,
                            boolean stopWhenNoAnimals,
                            int scheduledPoolSize,
                            int workerPoolSize,
                            int plantGrowthPerTick,
                            int startPlantsPerLocation,
                            int defaultOffspringCount) {
        this.tickDurationMs = tickDurationMs;
        this.maxTicks = maxTicks;
        this.stopWhenNoAnimals = stopWhenNoAnimals;
        this.scheduledPoolSize = scheduledPoolSize;
        this.workerPoolSize = workerPoolSize;
        this.plantGrowthPerTick = plantGrowthPerTick;
        this.startPlantsPerLocation = startPlantsPerLocation;
        this.defaultOffspringCount = defaultOffspringCount;
    }

    public long getTickDurationMs() {
        return tickDurationMs;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    public boolean isStopWhenNoAnimals() {
        return stopWhenNoAnimals;
    }

    public int getScheduledPoolSize() {
        return scheduledPoolSize;
    }

    public int getWorkerPoolSize() {
        return workerPoolSize;
    }

    public int getPlantGrowthPerTick() {
        return plantGrowthPerTick;
    }

    public int getStartPlantsPerLocation() {
        return startPlantsPerLocation;
    }

    public int getDefaultOffspringCount() {
        return defaultOffspringCount;
    }
}
