import java.util.Calendar;

public class TestCalender {
    public static void main(String[] args) throws Exception{
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.DATE));
        System.out.println(calendar.getTimeInMillis());
        Thread.sleep(1000);
        System.out.println(calendar.getTimeInMillis());
    }
}
