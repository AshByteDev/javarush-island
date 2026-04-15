import config.Configuration;
import config.IslandConfig;
import entity.AnimalFactory;
import model.Island;
import simulation.DeathService;
import simulation.FeedingService;
import simulation.MovementService;
import simulation.ReproductionService;
import simulation.SimulationEngine;
import simulation.StatisticsService;
import simulation.WorldInitializer;

public class SimulationStarter {

    public SimulationEngine create(Configuration configuration) {
        IslandConfig islandConfig = configuration.getIslandConfig();
        Island island = new Island(islandConfig.getWidth(), islandConfig.getHeight());

        AnimalFactory animalFactory = new AnimalFactory(configuration);
        WorldInitializer worldInitializer = new WorldInitializer(configuration, animalFactory);
        FeedingService feedingService = new FeedingService(configuration, animalFactory);
        MovementService movementService = new MovementService(configuration, animalFactory);
        ReproductionService reproductionService = new ReproductionService(configuration, animalFactory);
        DeathService deathService = new DeathService(configuration, animalFactory);
        StatisticsService statisticsService = new StatisticsService();

        return new SimulationEngine(
                configuration,
                island,
                worldInitializer,
                feedingService,
                movementService,
                reproductionService,
                deathService,
                statisticsService
        );
    }
}
