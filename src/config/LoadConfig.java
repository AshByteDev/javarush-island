package config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class LoadConfig {

    public static Configuration load() {
        Yaml yaml = new Yaml();

        InputStream input = LoadConfig.class
                .getClassLoader()
                .getResourceAsStream("resources/config_data.yaml");

        Map<String, Object> data = yaml.load(input);

        Configuration config = new Configuration();
        config.data = data;

        return config;
    }
}