package cn.lliiooll.iotqq.core;

import cn.lliiooll.iotqq.core.data.message.MessageChain;
import cn.lliiooll.iotqq.core.data.message.data.*;
import cn.lliiooll.iotqq.core.queue.IQueue;
import cn.lliiooll.iotqq.core.queue.RequestBuilder;
import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.data.user.Friend;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class IOTGlobal {
    @Getter
    public static long qq;

    private static String url;
    private static Logger log = LogManager.getLogger();
    @Getter
    public static List<Friend> friends = Lists.newArrayList();
    @Getter
    public static List<Group> groups = Lists.newArrayList();

    public static void init(long qq, String url) {
        IOTGlobal.qq = qq;
        IOTGlobal.url = url;
        refreshFriendList();
        refreshGroupList();
    }

    private static void refreshGroupList() {
        IQueue.sendRequest(RequestBuilder.builder()
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("NextToken", "");
                }}).toJSONString())
                .setUrl("http://" + IOTGlobal.url + "/v1/LuaApiCaller?qq=" + IOTGlobal.qq + "&funcname=GetGroupList&timeout=10")
                .setAction(result -> {
                    JSONObject replay = JSON.parseObject(result);
                    long time = System.currentTimeMillis();
                    for (JSONObject json : replay.getJSONArray("TroopList").toArray(new JSONObject[0])) {
                        Group group = new Group();
                        group.setId(json.getLongValue("GroupID"));
                        group.setName(json.getString("GroupName"));
                        groups.add(group);
                    }
                    log.info("群获取完毕，总计: " + (groups.size() - 1) + "个，耗时 " + (System.currentTimeMillis() - time) + "ms");
                }).build());
    }

    private static void refreshFriendList() {
        IQueue.sendRequest(RequestBuilder.builder()
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("StartIndex", 0);
                }}).toJSONString())
                .setUrl("http://" + IOTGlobal.url + "/v1/LuaApiCaller?qq=" + IOTGlobal.qq + "&funcname=GetQQUserList&timeout=10")
                .setAction(result -> {
                    long time = System.currentTimeMillis();
                    JSONObject replay = JSON.parseObject(result);
                    for (JSONObject j : replay.getJSONArray("Friendlist").toArray(new JSONObject[0])) {
                        Friend friend = new Friend();
                        friend.setNick(j.getString("NickName"));
                        friend.setId(j.getLongValue("FriendUin"));
                        friends.add(friend);
                    }
                    log.info("好友获取完毕，总计: " + (friends.size() - 1) + "个，耗时 " + (System.currentTimeMillis() - time) + "ms");
                }).build());
    }

    /**
     * 给好友发送消息
     *
     * @param message 使用MessageChain构建的消息链
     * @param friend  要发送的好友
     */
    public static void sendFriendMessage(MessageChain message, Friend friend) {
        JSONObject json = new JSONObject();
        String url = "http://" + IOTGlobal.url + "/v1/LuaApiCaller?qq=" + IOTGlobal.qq + "&funcname=SendMsg&timeout=10";
        json.put("toUser", friend.getId());
        json.put("sendToType", 1);
        json.put("groupid", 0);
        json.put("atUser", 0);
        json.put("sendMsgType", "TextMsg");
        final StringBuilder msg = new StringBuilder();
        message.forEach(m -> {
            if (m instanceof TextMessage) {
                msg.append(((TextMessage) m).msg);
            } else if (m instanceof PicMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "PicMsg");
                    PicMessage picMessage = (PicMessage) m;
                    String u = picMessage.getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("picUrl", u);
                    } else {
                        json.put("picBase64Buf", Base64.getEncoder().encode(picMessage.img));
                        json.put("fileMd5", picMessage.md5);
                    }
                }
            } else if (m instanceof FlashPicMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "PicMsg");
                    json.put("flashPic", true);
                    FlashPicMessage picMessage = (FlashPicMessage) m;
                    String u = picMessage.getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("picUrl", u);
                    } else {
                        json.put("picBase64Buf", Base64.getEncoder().encode(picMessage.img));
                        json.put("fileMd5", picMessage.md5);
                    }
                }
            } else if (m instanceof VoiceMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "VoiceMsg");
                    String u = ((VoiceMessage) m).getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("voiceUrl", u);
                    } else {
                        json.put("voiceBase64Buf", Base64.getEncoder().encode(((VoiceMessage) m).voice));
                    }
                }
            } else if (m instanceof JsonMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "JsonMsg");
                    msg.append(((JsonMessage) m).getMsg());

                }
            } else if (m instanceof XmlMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "XmlMsg");
                    msg.append(((XmlMessage) m).getMsg());

                }
            }
        });
        json.put("content", msg.toString());
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(json.toJSONString())
                .setAction(c -> {
                }).build());
    }

    /**
     * 发送临时消息
     *
     * @param message 要发送的消息
     * @param id      接收者的qq
     * @param group   机器人和接收者同在的群
     */
    public static void sendPrivateMessage(MessageChain message, long id, Group group) {
        JSONObject json = new JSONObject();
        String url = "http://" + IOTGlobal.url + "/v1/LuaApiCaller?qq=" + IOTGlobal.qq + "&funcname=SendMsg&timeout=10";
        json.put("toUser", id);
        json.put("sendToType", 3);
        json.put("groupid", group.getId());
        json.put("atUser", 0);
        json.put("sendMsgType", "TextMsg");
        final StringBuilder msg = new StringBuilder();
        message.forEach(m -> {
            if (m instanceof TextMessage) {
                msg.append(((TextMessage) m).msg);
            } else if (m instanceof PicMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "PicMsg");
                    PicMessage picMessage = (PicMessage) m;
                    String u = picMessage.getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("picUrl", u);
                    } else {
                        json.put("picBase64Buf", Base64.getEncoder().encode(picMessage.img));
                        json.put("fileMd5", picMessage.md5);
                    }
                }
            } else if (m instanceof FlashPicMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "PicMsg");
                    json.put("flashPic", true);
                    FlashPicMessage picMessage = (FlashPicMessage) m;
                    String u = picMessage.getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("picUrl", u);
                    } else {
                        json.put("picBase64Buf", Base64.getEncoder().encode(picMessage.img));
                        json.put("fileMd5", picMessage.md5);
                    }
                }
            } else if (m instanceof VoiceMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "VoiceMsg");
                    String u = ((VoiceMessage) m).getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("voiceUrl", u);
                    } else {
                        json.put("voiceBase64Buf", Base64.getEncoder().encode(((VoiceMessage) m).voice));
                    }
                }
            } else if (m instanceof JsonMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "JsonMsg");
                    msg.append(((JsonMessage) m).getMsg());

                }
            } else if (m instanceof XmlMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "XmlMsg");
                    msg.append(((XmlMessage) m).getMsg());

                }
            }
        });
        json.put("content", msg.toString());
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(json.toJSONString())
                .setAction(c -> {
                }).build());
    }

    /**
     * 发送群组消息
     *
     * @param message 消息
     * @param group   群
     */
    public static void sendGroupMessage(MessageChain message, Group group) {
        JSONObject json = new JSONObject();
        String url = "http://" + IOTGlobal.url + "/v1/LuaApiCaller?qq=" + IOTGlobal.qq + "&funcname=SendMsg&timeout=10";
        json.put("toUser", group.getId());
        json.put("sendToType", 2);
        json.put("groupid", 0);
        json.put("atUser", 0);
        json.put("sendMsgType", "TextMsg");
        final StringBuilder msg = new StringBuilder();
        message.forEach(m -> {
            if (m instanceof TextMessage) {
                msg.append(((TextMessage) m).msg);
            } else if (m instanceof AtMessage) {
                json.put("atUser", ((AtMessage) m).getId());
            } else if (m instanceof FlashPicMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "PicMsg");
                    json.put("flashPic", true);
                    FlashPicMessage picMessage = (FlashPicMessage) m;
                    String u = picMessage.getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("picUrl", u);
                    } else {
                        json.put("picBase64Buf", Base64.getEncoder().encode(picMessage.img));
                        json.put("fileMd5", picMessage.md5);
                    }
                }
            } else if (m instanceof PicMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "PicMsg");
                    PicMessage picMessage = (PicMessage) m;
                    String u = picMessage.getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("picUrl", u);
                    } else {
                        json.put("picBase64Buf", Base64.getEncoder().encode(picMessage.img));
                        json.put("fileMd5", picMessage.md5);
                    }
                }
            } else if (m instanceof VoiceMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "VoiceMsg");
                    String u = ((VoiceMessage) m).getUrl();
                    if (!Strings.isNullOrEmpty(u)) {
                        json.put("voiceUrl", u);
                    } else {
                        json.put("voiceBase64Buf", Base64.getEncoder().encode(((VoiceMessage) m).voice));
                    }
                }
            } else if (m instanceof JsonMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("XmlMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "JsonMsg");
                    msg.append(((JsonMessage) m).getMsg());

                }
            } else if (m instanceof XmlMessage) {
                if (json.containsKey("sendMsgType") && !(json.getString("sendMsgType").equalsIgnoreCase("VoiceMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("PicMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("JsonMsg") ||
                        json.getString("sendMsgType").equalsIgnoreCase("VideoMsg"))) {
                    json.put("sendMsgType", "XmlMsg");
                    msg.append(((XmlMessage) m).getMsg());

                }
            }
        });
        json.put("content", msg.toString());
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(json.toJSONString())
                .setAction(c -> {
                }).build());
    }

}
