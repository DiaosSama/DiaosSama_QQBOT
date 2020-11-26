import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jdk.internal.org.objectweb.asm.TypeReference
import work.diaossama.qqbot.utils.UserUtil
import work.diaossama.qqbot.utils.DBUtil
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

fun main() {
    /*
    var db = DBUtil()
    var result = db.query("select * from draw_test.user;")
    while(!result.isLast){
        result.next()
        var qqid = result.getLong("qqid")
        var draw_num = result.getInt("draw_num")
        var timestamp = result.getLong("timestamp")
        println("qqid: $qqid")
        println("draw_num: $draw_num")
        println("timestamp: $timestamp")
    }
     */
    var conn: HttpURLConnection ?= null
    try {
        conn = URL("https://api.bilibili.com/x/space/acc/info?mid=8258766").openConnection() as HttpURLConnection
        conn.connect()
        conn.inputStream.use { input ->
            val data:String = input.bufferedReader().readText()
            println(data)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        conn?.disconnect()
    }
}