package config;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Configuration {
    private final IslandConfig islandConfig;
    private final SimulationConfig simulationConfig;
    private final Map<AnimalType, AnimalConfig> animalConfigs;
    private final Map<AnimalType, Map<AnimalType, Integer>> eatProbabilities;
    private final Map<AnimalType, Integer> offspringConfig;

    public Configuration(IslandConfig islandConfig,
                         SimulationConfig simulationConfig,
                         Map<AnimalType, AnimalConfig> animalConfigs,
                         Map<AnimalType, Map<AnimalType, Integer>> eatProbabilities,
                         Map<AnimalType, Integer> offspringConfig) {
        this.islandConfig = islandConfig;
        this.simulationConfig = simulationConfig;
        this.animalConfigs = Collections.unmodifiableMap(new EnumMap<AnimalType, AnimalConfig>(animalConfigs));
        this.eatProbabilities = wrapEatProbabilities(eatProbabilities);
        this.offspringConfig = Collections.unmodifiableMap(new EnumMap<AnimalType, Integer>(offspringConfig));
    }

    public IslandConfig getIslandConfig() {
        return islandConfig;
    }

    public SimulationConfig getSimulationConfig() {
        return simulationConfig;
    }

    public AnimalConfig getAnimalConfig(AnimalType animalType) {
        return animalConfigs.get(animalType);
    }

    public Map<AnimalType, Map<AnimalType, Integer>> getEatProbabilities() {
        return eatProbabilities;
    }

    public int getOffspringCount(AnimalType type) {
        Integer value = offspringConfig.get(type);
        if (value == null) {
            return simulationConfig.getDefaultOffspringCount();
        }
        return value;
    }

    private Map<AnimalType, Map<AnimalType, Integer>> wrapEatProbabilities(
            Map<AnimalType, Map<AnimalType, Integer>> source) {
        Map<AnimalType, Map<AnimalType, Integer>> result =
                new EnumMap<AnimalType, Map<AnimalType, Integer>>(AnimalType.class);

        for (Map.Entry<AnimalType, Map<AnimalType, Integer>> entry : source.entrySet()) {
            result.put(entry.getKey(),
                    Collections.unmodifiableMap(new EnumMap<AnimalType, Integer>(entry.getValue())));
        }

        return Collections.unmodifiableMap(result);
    }

}