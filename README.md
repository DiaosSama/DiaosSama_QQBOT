# Simple_QQBot

## 介绍

不知道为什么心血来潮想要写一个QQ机器人，8月初萌生的想法，结果搜了搜相关资料发现刚好碰上 tx 在处理QQ机器人（害怕），然后发现市面上大多数成熟的作为商业产品的QQ机器人纷纷收到律师函纷纷跑路。

本着我写QQ机器人就是为了实现一些没什么所谓的功能，顺便练练手，应该不至于出事的思想指导，我找到了 [mirai](https://github.com/mamoe/mirai) 这个实现好的 QQ 协议库（感谢封装这个协议库的各位大佬），利用它封装好的协议写了一些无聊的功能。

以上。

## 功能

- [x] Base64 编码/解码
- [x] 无聊的抽签（使用诸葛神签的签位）
- [x] B站直播间监听（可动态添加/删除房间号）
- [x] 涩图抽奖（不是
- [ ] 图片上传至图床并返回链接
- [ ] 远程主机执行特定命令

## 使用

1. Base64 编码/解码

   目前Base64编解码在群聊中执行（有空再改回个人聊天窗口）

   编码命令以`/b64e`开头，如

   ```
   /b64e 测试base64编码
   ```

   解码命令以`/b64d` 开头

2. 无聊的抽签

   该功能在群聊中执行，在任意群聊输入“抽签”即可进行抽签，一天可抽一次，每天0点刷新。抽签后输入“解签”即可解签

3. B站直播间监听

   该功能目前只面向单一个体（一个群聊或一个好友，在`setting.yml`中配置），只有管理员（在`setting.yml`中配置）私聊可以添加或删除需要监听的房间

   - `/bililivestart`：启动B站直播间监听
   - `/bililivestop`：关闭B站直播间监听
   - `/bililiveadd`：增加监听的B站直播间号码
   - `/bililiverm`：删除监听的B站直播间号码
   
4. 涩图抽奖（不是

   该功能使用了V2EX老哥 [随机二次元图片接口](https://www.v2ex.com/t/727134) 的一个随机二次元图片接口，先谢谢这位老哥（有空再自己开发一个，一定不鸽
   
   本质是借用了 i.pixiv.cat 做的反向代理
   
   相关命令如下：
   
   - `PC涩图`：抽取宽屏分辨率的涩图
   - `手机涩图`：抽取竖屏分辨率的涩图
   
   机器人会回复图片以及对应的镜像链接，图片源文件会放置在机器人所在目录的`./Picture`文件夹下（记得定时清理一下

## 部署

### 环境准备

1. Java 8 及更新的版本

   Linux 和 Windows 环境下 Java 的安装都可以左转求助**度娘**或者**谷歌娘**

2. MySQL 安装

   同上

3. 数据库文件导入

   首先将根目录中的`database.sql`文件路径复制下来，下面会用到

   首先进入MySQL终端，执行以下命令

   ```
   mysql> create database qqbot;
   mysql> use qqbot;
   mysql> set names utf-8;
   mysql> source [database.sql的路径];
   ```

   终端提示导入成功即可

4. 配置文件`setting.yml`填写

   根据注释填写即可，mysql 部分一般只需修改 mysql 用户及密码为本机 MySQL 的用户及密码

### 启动机器人


1. 编译/Release下载 Jar 文件
  若采用编译方式，在 IDEA 中打开本项目，在根目录下的`build.gradle`中找到`shadowJar {`开头的那一行，点击左边的绿色启动键，待执行完成后可以在`/build/libs`文件夹下找到编译好的 Jar 文件

2. 确认环境配置

  保证机器人主程序 `xxx.jar` 与 `setting.yml`处于同一目录下

3. 启动机器人

   打开命令行窗口，执行

   ```
   java -jar xxx.jar
   ```

   

