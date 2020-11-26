package work.diaossama.qqbot.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {

    public static String b64Encode(String msg) {
        try {
            return Base64.getEncoder().encodeToString(msg.getBytes("utf-8"));
        }
        catch (UnsupportedEncodingException e){
            System.err.println("Error:" + e.getMessage());
            return "Base64加密失败: " + e.getMessage();
        }
    }

    public static String b64Decode(String msg) {
        return new String(Base64.getDecoder().decode(msg));
    }
}
