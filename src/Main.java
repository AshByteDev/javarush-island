import config.Configuration;
import config.LoadConfig;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Configuration configuration = LoadConfig.load();
        System.out.println(configuration.data.get("wolf_char"));
    }
}