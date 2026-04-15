package config;

public enum AnimalType {
    WOLF("wolf"),
    BOA("boa"),
    FOX("fox"),
    BEAR("bear"),
    EAGLE("eagle"),
    HORSE("horse"),
    DEER("deer"),
    RABBIT("rabbit"),
    MOUSE("mouse"),
    GOAT("goat"),
    SHEEP("sheep"),
    BOAR("boar"),
    BUFFALO("buffalo"),
    DUCK("duck"),
    CATERPILLAR("caterpillar"),
    PLANT("plant");

    private final String yamlKey;

    AnimalType(String yamlKey) {
        this.yamlKey = yamlKey;
    }

    public String yamlKey() {
        return yamlKey;
    }

    public String characteristicsSectionKey() {
        return yamlKey + "_char";
    }

    public String eatProbabilitySectionKey() {
        return yamlKey + "_var_eat";
    }

    public static AnimalType fromYamlKey(String yamlKey) {
        for (AnimalType type : values()) {
            if (type.yamlKey.equals(yamlKey)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown animal type key: " + yamlKey);
    }
}
