package work.diaossama.qqbot.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DrawUtil {
    private Connection conn;
    // TODO: 加载签位信息，回显对应签位
    public DrawUtil() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            YamlUtil yaml = new YamlUtil("setting.yml");
            String URL = "jdbc:mysql://" + yaml.get("mysql", "host") + ":" + yaml.get("mysql", "port")
                    + "/" + yaml.get("mysql", "database")
                    + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8";
            this.conn = DriverManager.getConnection(
                    URL,
                    yaml.get("mysql", "user"),
                    yaml.get("mysql", "password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDraw(int id) {
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet res = stmt.executeQuery("select draw from draw where id=" + id);
            res.next();
            // this.conn.close();
            return "签位: " + id + "\n" + res.getString("draw");
        }
        catch (Exception e) {
            e.printStackTrace();
            return "抽签出现错误，请重试或联系管理员。";
        }
        finally {
            try {
                this.conn.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //return "签位: " + id + "\n天门一挂榜，预定夺标人，马嘶芳草地，秋高听鹿鸣。";
    }

    public String dealDraw(int id) {
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet res = stmt.executeQuery("select deal_draw from draw where id=" + id);
            res.next();
            return "解签: " + id + "\n" + res.getString("deal_draw");
        }
        catch (Exception e) {
            e.printStackTrace();
            return "解签出现错误，请重试或联系管理员。";
        }
        finally {
            try {
                this.conn.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //return "解签: " + id + "\n大吉，事业、财运、健康、婚姻均顺遂。";
    }
}
