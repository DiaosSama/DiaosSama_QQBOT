package work.diaossama.qqbot

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.MemberMuteEvent
import net.mamoe.mirai.join
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.sendAsImageTo
import work.diaossama.qqbot.utils.*
import java.io.File

@Suppress("UNUSED_VARIABLE")
suspend fun main() {
    // TODO: 从yaml文件加载账户名密码
    val yaml = YamlUtil("setting.yml")
    val qq = java.lang.Long.valueOf(yaml.get("qq", "number"))
    val password = yaml.get("qq", "password")
    val bot = Bot(
        qq, password
    ) {
        fileBasedDeviceInfo("device.json")
    }.alsoLogin()
    val handler = BiliLiveHandler(bot)

    // 初始化管理员账户
    val admin = initAdmin()
    // 启动信息处理协程
    bot.messageHandle(handler, admin)
    biliLiveHandle(handler)

    bot.join()
}

fun Bot.messageHandle(handler: BiliLiveHandler, admin: Long) {
    subscribeGroupMessages{
        "你好" reply "你好！"
        case("帮助") {
            reply(At(sender as Member) + "命令指南\n" +
                    "1. PC涩图: PC分辨率涩图\n" +
                    "2. 手机涩图: 手机分辨率涩图\n" +
                    "3. 抽签: 就是抽签\n" +
                    "4. 解签: 就是解签")
        }
        /*
        (contains("舔") or contains("DiaosSama")) {
            reply("DiaosSama太强了")
        }
         */

        (contains("DiaosSama")) {
            reply("DiaosSama太强了")
        }

        // 复读mode
        startsWith("/repeat") {
            var reMess = message.toString()
            reMess = reMess.substring(reMess.indexOfFirst { it == ' ' }+1)
            reply(reMess)
        }

        // base64加密
        startsWith("/b64e") {
            var reMess = message.toString()
            reMess = reMess.substring(reMess.indexOfFirst { it == ' ' }+1)
            reMess = Base64Util.b64Encode(reMess)
            reply(At(sender as Member) + reMess)
        }

        // base64解密
        startsWith("/b64d") {
            var reMess = message.toString()
            reMess = reMess.substring(reMess.indexOfFirst { it == ' ' }+1)
            reMess = Base64Util.b64Decode(reMess)
            reply(At(sender as Member) + reMess)
        }

        case("抽签") {
            var user = UserUtil(sender.id)
            reply(At(sender as Member) + user.draw())
        }

        case("解签") {
            var user = UserUtil(sender.id)
            reply(At(sender as Member) + user.dealDraw())
        }

        // 发涩图（不是
        // 使用v2ex老哥提供的接口（有空再自己重写随机接口
        // https://www.v2ex.com/t/727134
        startsWith("PC涩图") {
            // var pictype = message.toString()
            // pictype = pictype.substring(pictype.indexOfFirst { it == ' ' }+1)
            val url = "https://open.pixivic.net/wallpaper/pc/random?size=large&domain=https://i.pixiv.cat&webp=0&detail=1"
            val urlpath = PicUtil.getLocation(url)
            if (urlpath == "") {
                quoteReply("获取涩图失败，联系DiaosSama查看日志")
            }
            else {
                val filename = PicUtil.downloadPic(urlpath)
                File(filename).sendAsImage()
                quoteReply("图片URL: $urlpath\n如果有链接无图说明被鹅吞了QAQ")
            }
        }

        startsWith("手机涩图") {
            // var pictype = message.toString()
            // pictype = pictype.substring(pictype.indexOfFirst { it == ' ' }+1)
            val url = "https://open.pixivic.net/wallpaper/mobile/random?size=large&domain=https://i.pixiv.cat&webp=0&detail=1"
            val urlpath = PicUtil.getLocation(url)
            if (urlpath == "") {
                quoteReply("获取涩图失败，联系DiaosSama查看日志")
            }
            else {
                val filename = PicUtil.downloadPic(urlpath)
                File(filename).sendAsImage()
                quoteReply("图片URL: $urlpath\n如果有链接无图说明被鹅吞了QAQ")
            }
        }
    }

    subscribeAlways<MemberMuteEvent> {
        it.group.sendMessage(PlainText("恭喜老哥 ${it.member.nameCardOrNick} 喜提禁言套餐一份"))
    }


    subscribeFriendMessages {
        // 启动b站直播监听
        startsWith("/bililivestart") {
            if(sender.id == admin) {
                handler.startBiliLiveListener()
            }
        }

        // 停止b站直播监听
        startsWith("/bililivestop") {
            if(sender.id == admin) {
                handler.stopBiliLiveListener()
            }
        }

        // 增加b站监听房间号
        startsWith("/bililiveadd") {
            if(sender.id == admin) {
                var reMess = message.toString()
                reMess = reMess.substring(reMess.indexOfFirst { it == ' ' }+1)
                var room_id:Long = java.lang.Long.valueOf(reMess)
                handler.addRoom(room_id)
            }
        }

        // 删除b站监听房间号
        startsWith("/bililiverm") {
            if (sender.id == admin) {
                var reMess = message.toString()
                reMess = reMess.substring(reMess.indexOfFirst { it == ' ' } + 1)
                var room_id: Long = java.lang.Long.valueOf(reMess)
                handler.removeRoom(room_id)
            }
        }
    }

    // 开播下播推送
    //listenBiliLive()
}

fun biliLiveHandle(handler: BiliLiveHandler) {
    // var user = this.getFriend(1315816879L)
    // var handler = BiliLiveHandler(this)
    GlobalScope.launch {
        handler.listenLiveRoom()
    }
}

/*
初始化管理员QQ号码
 */
fun initAdmin(): Long {
    val yaml = YamlUtil("setting.yml")
    return java.lang.Long.valueOf(yaml.get("admin", "number"))
}
