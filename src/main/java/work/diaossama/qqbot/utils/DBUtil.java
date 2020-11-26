package work.diaossama.qqbot.utils;

import java.sql.*;


public class DBUtil {
    private Connection conn;

    public DBUtil() {
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

    public ResultSet query(String sql) {
        try {
            Statement stmt = this.conn.createStatement();
            return stmt.executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insert(String sql) {
        try {
            Statement stmt = this.conn.createStatement();
            int res = stmt.executeUpdate(sql);
            return res > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(String sql) {
        try {
            Statement stmt = this.conn.createStatement();
            int res = stmt.executeUpdate(sql);
            return res > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String sql) {
        try {
            Statement stmt = this.conn.createStatement();
            int res = stmt.executeUpdate(sql);
            return res > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            this.conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
