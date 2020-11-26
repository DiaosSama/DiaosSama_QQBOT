package work.diaossama.qqbot.utils

import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.MessageReceipt

// Done: 接收开播消息人改为配置文件配置
// Done: 接收开播消息可以在群聊和个人间变更
// TODO: 配置多接收人支持

class BiliLiveHandler (private var bot: Bot) {
    private var startFlag:Boolean = false
    private var rooms = initRoom()
    private var chatType = initChatType()
    private var number = initNumber()
    // 标识常量
    @JvmField val GROUP = 0
    @JvmField val FRIEND = 1
    //private var room_url = "https://api.live.bilibili.com/room/v1/Room/get_info?room_id="
    //private var user_url = "https://api.bilibili.com/x/space/acc/info?mid="

    // 直播间监听函数，对外接口
    suspend fun listenLiveRoom() {
        while(true) {
            if (startFlag) {
                // bot.getFriend(1315816879L).sendMessage(getLiveRoomStatus())
                getLiveRoomStatus()
                // 轮询间隔
                delay(60000L)
            }
            else {
                delay(60000L)
            }
        }
    }

    /*
    监听具体实现
    ChangLog:
    Debug v2.4: 修改直播间状态判断逻辑（新增轮播状态判断）
     */
    private suspend fun getLiveRoomStatus() {
        var new_room:BiliInfoUtil
        for (room in rooms) {
            new_room = BiliInfoUtil(room.key.toString())
            // 直播状态改变
            if (!new_room.online.equals(room.value["status"])) {
                // 开播
                if (new_room.online.equals("1")) {
                    sendMessage("您关注的房间已开播!\n" +
                            "房间号: " + room.key + "\n" +
                            "标题: " + new_room.title + "\n" +
                            "主播: " + new_room.name
                            )
                    room.value["title"] = new_room.title
                    room.value["name"] = new_room.name
                    room.value["status"] = "1"
                }
                // 下播
                else if (new_room.online.equals("0")) {
                    sendMessage("您关注的房间已下播!\n" +
                            "房间号: " + room.key + "\n" +
                            "标题: " + new_room.title + "\n" +
                            "主播: " + new_room.name
                    )
                    room.value["title"] = new_room.title
                    room.value["name"] = new_room.name
                    room.value["status"] = "0"
                }
                // 轮播
                else if (new_room.online.equals("2")) {
                    sendMessage("您关注的房间开启轮播!\n" +
                            "房间号: " + room.key + "\n" +
                            "标题: " + new_room.title + "\n" +
                            "主播: " + new_room.name
                    )
                    room.value["title"] = new_room.title
                    room.value["name"] = new_room.name
                    room.value["status"] = "2"
                }
                // 更新数据库信息
                var db = DBUtil()
                db.update("update bili_live set title=" + room.value["title"] +
                        ",name=" + room.value["name"] +
                        " where room_id=" + room.key)
                db.close()
            }
            // 直播标题发生改变
            if (!new_room.title.equals(room.value["title"])){
                sendMessage("直播间标题变更!\n" +
                        "房间号: " + room.key + "\n" +
                        "标题: " + new_room.title + "\n" +
                        "主播: " + new_room.name
                )
                room.value["title"] = new_room.title
                room.value["name"] = new_room.name
                // 更新数据库信息
                var db = DBUtil()
                db.update("update bili_live set title=" + room.value["title"] +
                        ",name=" + room.value["name"] +
                        " where room_id=" + room.key)
                db.close()
            }
        }
    }

    suspend fun startBiliLiveListener() {
        sendMessage("启动B站直播间监听")
        startFlag = true
    }

    suspend fun stopBiliLiveListener() {
        sendMessage("关闭B站直播监听")
        startFlag = false
    }

    /*
    从数据库初始化需要监听的直播间信息
    */
    private fun initRoom():HashMap<Long, HashMap<String, String>> {
        val db = DBUtil()
        var result = db.query("select * from bili_live;")
        var outter_hashmap = HashMap<Long, HashMap<String, String>>()
        while(result.next()){

            // 直播间号
            var room_id = result.getLong("room_id")
            // 直播间标题
            var title = result.getString("title")
            // 直播间主播uid
            var uid = result.getLong("uid")
            // 直播间主播昵称
            var name = result.getString("name")
            var inner = HashMap<String, String>()
            inner["uid"] = uid.toString()
            inner["title"] = title
            inner["name"] = name
            // 直播间状态
            inner["status"] = "-1"
            outter_hashmap[room_id] = inner
        }
        db.close()
        return outter_hashmap
    }

    /*
    从配置文件获取接收消息主体类型（群聊或个人）
     */
    private fun initChatType():Int {
        val yaml = YamlUtil("setting.yml")
        return yaml.get("bili_live", "chatType").toInt()
    }

    private fun initNumber():Long {
        val yaml = YamlUtil("setting.yml")
        return java.lang.Long.valueOf(yaml.get("bili_live", "number"))
    }

    /*
    增加需要监听的直播间
     */
    suspend fun addRoom(room_id:Long) {
        var info = BiliInfoUtil(room_id.toString())
        if(info.isStatus) {
            var inner = HashMap<String, String>()
            inner["uid"] = info.uid
            inner["title"] = info.title
            inner["name"] = info.name
            inner["status"] = "0"
            // 写入数据库
            var db = DBUtil()
            db.insert("insert into bili_live (room_id, title, uid, name) values (" +
                    room_id + ", " +
                    info.title + "," +
                    java.lang.Long.valueOf(info.uid) + "," +
                    info.name + ")"
            )
            db.close()
            // 发送反馈消息
            this.rooms[room_id] = inner
            sendMessage("直播间监听添加成功\n" +
                    "房间号: " + room_id + "\n" +
                    "标题: " + info.title + "\n" +
                    "主播: " + info.name
            )
        }
        else {
            sendMessage("直播间监听添加失败")
        }
    }

    /*
    删除监听列表中的直播间
     */
    suspend fun removeRoom(room_id:Long) {
        if(rooms.containsKey(room_id)) {
            var rm_room = rooms.remove(room_id)
            var db = DBUtil()
            db.delete("delete from bili_live where room_id=$room_id")
            db.close()
            sendMessage("直播间监听删除成功\n" +
                    "房间号: " + room_id + "\n" +
                    "标题: " + (rm_room?.get("title") ?: "null") + "\n" +
                    "主播: " + (rm_room?.get("name") ?: "null")
            )
        }
        else {
            sendMessage("监听列表中不存在该房间")
        }
    }

    suspend fun sendMessage(message:String) {
        if (this.chatType == GROUP) {
            bot.getGroup(this.number).sendMessage(message)
        }
        else if (this.chatType == FRIEND) {
            bot.getFriend(this.number).sendMessage(message)
        }
    }
}