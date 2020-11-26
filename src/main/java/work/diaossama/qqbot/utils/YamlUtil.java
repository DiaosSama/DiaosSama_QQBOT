package work.diaossama.qqbot.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class YamlUtil {
    private Yaml yaml;
    Map<String, Map<String, Object>> obj;

    public YamlUtil(String filename) {
        try {
            this.yaml = new Yaml();
            File file = new File(filename);
            FileInputStream fileInputStream = new FileInputStream(file);
            //InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filename);
            this.obj = yaml.load(fileInputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key1, String key2) {
        return this.obj.get(key1).get(key2).toString();
    }
}
