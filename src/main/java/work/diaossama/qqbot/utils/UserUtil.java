package work.diaossama.qqbot.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class UserUtil {
    private Long qqid;
    private long timestamp;
    private Calendar calendar;
    private int drawnum;

    public UserUtil(Long qqid) {
        this.qqid = qqid;

        // 获取数据库内容
        this.getUserInfo();

        // 初始化格式化时间
        Date date = new Date(timestamp);
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(date);
    }

    private void getUserInfo() {
        DBUtil db = new DBUtil();
        ResultSet res = db.query("select * from user where qqid=" + qqid);
        try {
            res.next();
            this.timestamp = res.getLong("timestamp");
            this.drawnum = res.getInt("draw_num");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.drawnum = 0;
            this.timestamp = Calendar.getInstance().getTimeInMillis() - 86400000;
            db.insert("insert into user (qqid, draw_num, timestamp) values (" + qqid + ", 0, " + this.timestamp + ")");
        }
        finally {
            db.close();
        }
    }

    // 判断距上次更新是否为新的一天
    public boolean isNewDay() {
        Calendar nowcalendar = Calendar.getInstance();
        // long nowtime = nowcalendar.getTimeInMillis();
        int nowyear = nowcalendar.get(Calendar.YEAR);
        int nowmonth = nowcalendar.get(Calendar.MONTH);
        int nowday = nowcalendar.get(Calendar.DATE);
        int lastyear = this.calendar.get(Calendar.YEAR);
        int lastmonth = this.calendar.get(Calendar.MONTH);
        int lastday = this.calendar.get(Calendar.DATE);

        if (nowday > lastday) {
            return true;
        }
        else if (nowmonth > lastmonth) {
            return true;
        }
        else return nowyear > lastyear;
    }

    public String testGetInfo() {
        return "QQId: " + qqid + "\n" + "draw_num: " + drawnum + "\n" + "last_update: " + timestamp;
    }

    // 抽签
    public String draw() {
        DrawUtil drawUtil = new DrawUtil();
        String resp;
        if (isNewDay()) {
            long nowtime = Calendar.getInstance().getTimeInMillis();
            Random r = new Random(nowtime);
            for (int i = 0; i < (qqid % 1000); i++) {
                r.nextInt();
            }
            int draw = r.nextInt(384) + 1;
            DBUtil db = new DBUtil();
            db.update("update user set draw_num=" + draw + ",timestamp=" + nowtime + " where qqid=" + qqid);
            db.close();
            resp = drawUtil.getDraw(draw) + "\n" + "解签请发送\"解签\"";
        }
        else {
            resp = "您今天已经抽过签了。\n" + drawUtil.getDraw(drawnum) + "\n" + "解签请发送\"解签\"";
        }
        return resp;
    }

    // 解签
    public String dealDraw() {
        String resp;
        if (isNewDay()) {
            resp = "您今天还未抽签\n请发送\"抽签\"进行抽签";
        }
        else {
            DrawUtil drawUtil = new DrawUtil();
            resp = drawUtil.dealDraw(drawnum);
        }
        return resp;
    }
}
