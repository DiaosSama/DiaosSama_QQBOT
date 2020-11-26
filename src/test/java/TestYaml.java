import org.yaml.snakeyaml.*;

import java.io.InputStream;
import java.util.Map;

class Test {
    public void testYaml1() {
        Yaml setting = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("setting.yml");
        Map<String, Map<String, Object>> obj = setting.load(inputStream);
        System.out.println(obj.get("qq").get("number"));
        System.out.println(obj);
    }
}

public class TestYaml {
    public static void main(String[] args) {
        Test test = new Test();
        test.testYaml1();
    }
}
