package config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class LoadConfig {

    private static final String ISLAND_CONFIG_SECTION = "island_config";
    private static final String SIMULATION_CONFIG_SECTION = "simulation_config";
    private static final String OFFSPRING_SECTION = "offspring_config";
    private static final String WIDTH_FIELD = "width";
    private static final String HEIGHT_FIELD = "height";
    private static final String WEIGHT_FIELD = "weight";
    private static final String MAX_PER_CELL_FIELD = "max_per_cell";
    private static final String SPEED_FIELD = "speed";
    private static final String FOOD_NEEDED_FIELD = "food_needed";
    private static final String TICK_DURATION_MS_FIELD = "tick_duration_ms";
    private static final String MAX_TICKS_FIELD = "max_ticks";
    private static final String STOP_WHEN_NO_ANIMALS_FIELD = "stop_when_no_animals";
    private static final String SCHEDULED_POOL_SIZE_FIELD = "scheduled_pool_size";
    private static final String WORKER_POOL_SIZE_FIELD = "worker_pool_size";
    private static final String PLANT_GROWTH_PER_TICK_FIELD = "plant_growth_per_tick";
    private static final String START_PLANTS_PER_LOCATION_FIELD = "start_plants_per_location";
    private static final String DEFAULT_OFFSPRING_COUNT_FIELD = "default_offspring_count";

    private LoadConfig() {
    }

    public static Configuration load() {
        Map<String, Object> rawData = readRawYaml();
        IslandConfig islandConfig = parseIslandConfig(rawData);
        SimulationConfig simulationConfig = parseSimulationConfig(rawData);
        Map<AnimalType, AnimalConfig> animalConfigs = parseAnimalConfigs(rawData);
        Map<AnimalType, Map<AnimalType, Integer>> eatProbabilities = parseEatProbabilities(rawData);
        Map<AnimalType, Integer> offspringConfig = parseIntMapByAnimalType(rawData, OFFSPRING_SECTION);

        return new Configuration(
                islandConfig,
                simulationConfig,
                animalConfigs,
                eatProbabilities,
                offspringConfig
        );
    }

    private static Map<String, Object> readRawYaml() {
        Yaml yaml = new Yaml();
        InputStream input = getConfigInputStream();
        return yaml.load(input);
    }

    private static InputStream getConfigInputStream() {
        InputStream input = LoadConfig.class.getClassLoader().getResourceAsStream("config_data.yaml");
        if (input == null) {
            input = LoadConfig.class.getClassLoader().getResourceAsStream("resources/config_data.yaml");
        }
        if (input == null) {
            throw new IllegalStateException("Configuration file config_data.yaml not found in classpath");
        }
        return input;
    }

    @SuppressWarnings("unchecked")
    private static IslandConfig parseIslandConfig(Map<String, Object> rawData) {
        Map<String, Object> islandSection = getRequiredSection(rawData, ISLAND_CONFIG_SECTION);
        int width = toInt(islandSection.get(WIDTH_FIELD));
        int height = toInt(islandSection.get(HEIGHT_FIELD));
        return new IslandConfig(width, height);
    }

    @SuppressWarnings("unchecked")
    private static Map<AnimalType, AnimalConfig> parseAnimalConfigs(Map<String, Object> rawData) {
        Map<AnimalType, AnimalConfig> animalConfigs = new EnumMap<AnimalType, AnimalConfig>(AnimalType.class);

        for (AnimalType type : AnimalType.values()) {
            String sectionKey = type.characteristicsSectionKey();
            Map<String, Object> animalSection = (Map<String, Object>) rawData.get(sectionKey);
            if (animalSection == null) {
                continue;
            }

            AnimalConfig animalConfig = new AnimalConfig(
                    toDouble(animalSection.get(WEIGHT_FIELD)),
                    toInt(animalSection.get(MAX_PER_CELL_FIELD)),
                    toInt(animalSection.get(SPEED_FIELD)),
                    toDouble(animalSection.get(FOOD_NEEDED_FIELD))
            );
            animalConfigs.put(type, animalConfig);
        }

        return animalConfigs;
    }

    @SuppressWarnings("unchecked")
    private static SimulationConfig parseSimulationConfig(Map<String, Object> rawData) {
        Map<String, Object> section = getRequiredSection(rawData, SIMULATION_CONFIG_SECTION);

        long tickDurationMs = toLong(requireRequiredField(section, TICK_DURATION_MS_FIELD, SIMULATION_CONFIG_SECTION));
        int maxTicks = toInt(requireRequiredField(section, MAX_TICKS_FIELD, SIMULATION_CONFIG_SECTION));
        boolean stopWhenNoAnimals =
                toBoolean(requireRequiredField(section, STOP_WHEN_NO_ANIMALS_FIELD, SIMULATION_CONFIG_SECTION));
        int scheduledPoolSize =
                toInt(requireRequiredField(section, SCHEDULED_POOL_SIZE_FIELD, SIMULATION_CONFIG_SECTION));
        int workerPoolSize =
                toInt(requireRequiredField(section, WORKER_POOL_SIZE_FIELD, SIMULATION_CONFIG_SECTION));
        int plantGrowthPerTick =
                toInt(requireRequiredField(section, PLANT_GROWTH_PER_TICK_FIELD, SIMULATION_CONFIG_SECTION));
        int startPlantsPerLocation =
                toInt(requireRequiredField(section, START_PLANTS_PER_LOCATION_FIELD, SIMULATION_CONFIG_SECTION));
        int defaultOffspringCount =
                toInt(requireRequiredField(section, DEFAULT_OFFSPRING_COUNT_FIELD, SIMULATION_CONFIG_SECTION));

        return new SimulationConfig(
                tickDurationMs,
                maxTicks,
                stopWhenNoAnimals,
                scheduledPoolSize,
                workerPoolSize,
                plantGrowthPerTick,
                startPlantsPerLocation,
                defaultOffspringCount
        );
    }

    @SuppressWarnings("unchecked")
    private static Map<AnimalType, Map<AnimalType, Integer>> parseEatProbabilities(Map<String, Object> rawData) {
        Map<AnimalType, Map<AnimalType, Integer>> probabilities =
                new EnumMap<AnimalType, Map<AnimalType, Integer>>(AnimalType.class);

        for (AnimalType predator : AnimalType.values()) {
            String sectionKey = predator.eatProbabilitySectionKey();
            Map<String, Object> eatSection = (Map<String, Object>) rawData.get(sectionKey);
            if (eatSection == null) {
                continue;
            }

            Map<AnimalType, Integer> preyProbabilities = new EnumMap<AnimalType, Integer>(AnimalType.class);
            for (Map.Entry<String, Object> entry : eatSection.entrySet()) {
                AnimalType prey = AnimalType.fromYamlKey(entry.getKey());
                preyProbabilities.put(prey, toInt(entry.getValue()));
            }
            probabilities.put(predator, preyProbabilities);
        }

        return probabilities;
    }

    @SuppressWarnings("unchecked")
    private static Map<AnimalType, Integer> parseIntMapByAnimalType(Map<String, Object> rawData, String sectionKey) {
        Map<AnimalType, Integer> result = new EnumMap<AnimalType, Integer>(AnimalType.class);
        Map<String, Object> section = (Map<String, Object>) rawData.get(sectionKey);
        if (section == null) {
            return result;
        }

        for (Map.Entry<String, Object> entry : section.entrySet()) {
            AnimalType type = AnimalType.fromYamlKey(entry.getKey());
            result.put(type, toInt(entry.getValue()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getRequiredSection(Map<String, Object> rawData, String sectionName) {
        Map<String, Object> section = (Map<String, Object>) rawData.get(sectionName);
        if (section == null) {
            throw new IllegalStateException("Section '" + sectionName + "' not found");
        }
        return section;
    }

    private static int toInt(Object value) {
        return ((Number) value).intValue();
    }

    private static long toLong(Object value) {
        return ((Number) value).longValue();
    }

    private static boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private static double toDouble(Object value) {
        return ((Number) value).doubleValue();
    }

    private static Object requireRequiredField(Map<String, Object> section, String fieldName, String sectionName) {
        Object value = section.get(fieldName);
        if (value == null) {
            throw new IllegalStateException("Required field '" + fieldName
                    + "' is missing in section '" + sectionName + "'");
        }
        return value;
    }
}