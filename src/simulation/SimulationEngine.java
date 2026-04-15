package simulation;

import config.Configuration;
import config.SimulationConfig;
import model.Island;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SimulationEngine {
    private final Island island;
    private final WorldInitializer worldInitializer;
    private final AnimalLifecycleProcessor lifecycleProcessor;
    private final StatisticsService statisticsService;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService workerPool;
    private final ReentrantLock worldLock = new ReentrantLock();
    private volatile boolean running;
    private int completedTicks;
    private int printedTicks;
    private final int maxTicks;
    private final boolean stopWhenNoAnimals;
    private final int plantGrowthPerTick;
    private final long tickDurationMs;

    public SimulationEngine(Configuration configuration,
                            Island island,
                            WorldInitializer worldInitializer,
                            FeedingService feedingService,
                            MovementService movementService,
                            ReproductionService reproductionService,
                            DeathService deathService,
                            StatisticsService statisticsService) {
        this.island = island;
        this.worldInitializer = worldInitializer;
        this.statisticsService = statisticsService;

        SimulationConfig simulationConfig = configuration.getSimulationConfig();
        this.workerPool = Executors.newFixedThreadPool(simulationConfig.getWorkerPoolSize());
        this.scheduler = Executors.newScheduledThreadPool(simulationConfig.getScheduledPoolSize());
        this.lifecycleProcessor = new AnimalLifecycleProcessor(
                island,
                workerPool,
                simulationConfig.getWorkerPoolSize(),
                feedingService,
                movementService,
                reproductionService,
                deathService
        );

        this.maxTicks = simulationConfig.getMaxTicks();
        this.stopWhenNoAnimals = simulationConfig.isStopWhenNoAnimals();
        this.plantGrowthPerTick = simulationConfig.getPlantGrowthPerTick();
        this.tickDurationMs = simulationConfig.getTickDurationMs();
    }

    public void initializeWorld() {
        worldInitializer.populate(island);
    }

    public void run() {
        running = true;
        completedTicks = 0;
        printedTicks = 0;

        long period = Math.max(100L, tickDurationMs);
        long phaseShift = Math.max(10L, period / 3L);

        scheduler.scheduleAtFixedRate(this::runPlantGrowthTask, 0L, period, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::runAnimalLifecycleTask, phaseShift, period, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::runStatisticsTask, phaseShift * 2L, period, TimeUnit.MILLISECONDS);

        waitForStop();
    }

    private void runPlantGrowthTask() {
        if (!running) {
            return;
        }
        worldLock.lock();
        try {
            worldInitializer.growPlants(island, plantGrowthPerTick);
        } finally {
            worldLock.unlock();
        }
    }

    private void runAnimalLifecycleTask() {
        if (!running) {
            return;
        }
        worldLock.lock();
        try {
            int nextTick = completedTicks + 1;
            if (nextTick > maxTicks) {
                stop("max ticks reached");
                return;
            }

            lifecycleProcessor.processTick(nextTick);
            completedTicks = nextTick;

            if (stopWhenNoAnimals && statisticsService.countTotalAliveAnimals(island) == 0) {
                stop("no animals alive");
            }
        } finally {
            worldLock.unlock();
        }
    }

    private void runStatisticsTask() {
        if (!running) {
            return;
        }
        worldLock.lock();
        try {
            int tick = completedTicks;
            if (tick <= 0 || tick == printedTicks) {
                return;
            }
            statisticsService.printTickStats(island, tick);
            printedTicks = tick;
        } finally {
            worldLock.unlock();
        }
    }

    private void stop(String reason) {
        if (running) {
            running = false;
            System.out.println("Simulation stopped: " + reason);
            scheduler.shutdown();
        }
    }

    private void waitForStop() {
        try {
            while (running) {
                Thread.sleep(100L);
            }
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            workerPool.shutdown();
        }
    }
}
