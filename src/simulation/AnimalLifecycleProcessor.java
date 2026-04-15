package simulation;

import model.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AnimalLifecycleProcessor {
    private final Island island;
    private final ExecutorService workerPool;
    private final int workerThreads;
    private final FeedingService feedingService;
    private final MovementService movementService;
    private final ReproductionService reproductionService;
    private final DeathService deathService;

    public AnimalLifecycleProcessor(Island island,
                                    ExecutorService workerPool,
                                    int workerThreads,
                                    FeedingService feedingService,
                                    MovementService movementService,
                                    ReproductionService reproductionService,
                                    DeathService deathService) {
        this.island = island;
        this.workerPool = workerPool;
        this.workerThreads = Math.max(1, workerThreads);
        this.feedingService = feedingService;
        this.movementService = movementService;
        this.reproductionService = reproductionService;
        this.deathService = deathService;
    }

    public void processTick(int tick) {
        runEatPhase(tick);
        runMovePhase(tick);
        runReproducePhase(tick);
        runDeathPhase(tick);
    }

    private void runEatPhase(int tick) {
        runPhase(PhaseType.EAT, tick);
    }

    private void runMovePhase(int tick) {
        runPhase(PhaseType.MOVE, tick);
    }

    private void runReproducePhase(int tick) {
        runPhase(PhaseType.REPRODUCE, tick);
    }

    private void runDeathPhase(int tick) {
        runPhase(PhaseType.DEATH, tick);
    }

    private void runPhase(PhaseType phaseType, int tick) {
        int tasks = Math.min(workerThreads, island.getHeight());
        List<Future<Void>> futures = new ArrayList<Future<Void>>(tasks);

        for (int taskIndex = 0; taskIndex < tasks; taskIndex++) {
            final int yStart = taskIndex * island.getHeight() / tasks;
            final int yEnd = (taskIndex + 1) * island.getHeight() / tasks;
            Callable<Void> callable = () -> {
                runPhaseRange(phaseType, yStart, yEnd, tick);
                return null;
            };
            futures.add(workerPool.submit(callable));
        }

        waitAll(futures);
    }

    private void runPhaseRange(PhaseType phaseType, int yStart, int yEndExclusive, int tick) {
        if (phaseType == PhaseType.EAT) {
            feedingService.processRange(island, yStart, yEndExclusive, tick);
            return;
        }
        if (phaseType == PhaseType.MOVE) {
            movementService.processRange(island, yStart, yEndExclusive, tick);
            return;
        }
        if (phaseType == PhaseType.REPRODUCE) {
            reproductionService.processRange(island, yStart, yEndExclusive, tick);
            return;
        }
        deathService.processRange(island, yStart, yEndExclusive, tick);
    }

    private void waitAll(List<Future<Void>> futures) {
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new IllegalStateException("Worker phase execution failed", e);
            }
        }
    }

    private enum PhaseType {
        EAT,
        MOVE,
        REPRODUCE,
        DEATH
    }
}
