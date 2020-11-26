import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class TestJson {
    public static void main(String[] args) {
        try {
            HttpURLConnection conn;
            URL url = new URL("https://api.live.bilibili.com/room/v1/Room/get_info?room_id=11372166");
            conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String res = buffer.readLine();
            System.out.println(res);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(res, JsonNode.class);
            JsonNode dataNode = jsonNode.get("data");
            System.out.println(dataNode.get("uid"));
            System.out.println(dataNode.get("title"));
            System.out.println(!dataNode.get("online").toString().equals("0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
