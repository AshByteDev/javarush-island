import config.Configuration;
import config.LoadConfig;
import simulation.SimulationEngine;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = LoadConfig.load();
        SimulationStarter starter = new SimulationStarter();
        SimulationEngine engine = starter.create(configuration);
        engine.initializeWorld();
        engine.run();
    }
}