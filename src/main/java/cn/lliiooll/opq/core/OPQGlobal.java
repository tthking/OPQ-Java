package cn.lliiooll.opq.core;

import cn.lliiooll.opq.core.data.group.GroupAnnounceType;
import cn.lliiooll.opq.core.data.message.MessageChain;
import cn.lliiooll.opq.core.data.message.data.*;
import cn.lliiooll.opq.core.data.user.Member;
import cn.lliiooll.opq.core.managers.event.EventManager;
import cn.lliiooll.opq.core.managers.event.data.FriendMessageSendEvent;
import cn.lliiooll.opq.core.managers.event.data.GroupMessageSendEvent;
import cn.lliiooll.opq.core.queue.IQueue;
import cn.lliiooll.opq.core.queue.RequestBuilder;
import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.utils.TaskUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class OPQGlobal {
    @Getter
    public static long qq;

    private static String url;
    private static Logger log = LogManager.getLogger();
    @Getter
    public static Map<Long, Friend> friends = Maps.newHashMap();
    @Getter
    public static Map<Long, Group> groups = Maps.newHashMap();

    private static ExecutorService main = TaskUtils.create("DownloadTask-%d");

    public static void init(long qq, String url) {
        OPQGlobal.qq = qq;
        OPQGlobal.url = url;
        refreshFriendList();
        refreshGroupList();
    }

    private static void refreshGroupList() {
        IQueue.sendRequest(RequestBuilder.builder()
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("NextToken", "");
                }}).toJSONString())
                .setUrl("http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=GetGroupList&timeout=10")
                .setAction(result -> {
                    JSONObject replay = JSON.parseObject(result);
                    long time = System.currentTimeMillis();
                    for (JSONObject json : replay.getJSONArray("TroopList").toArray(new JSONObject[0])) {
                        Group group = new Group();
                        group.setId(json.getLongValue("GroupID"));
                        group.setName(json.getString("GroupName"));
                        groups.put(group.getId(), group);
                    }
                    log.info("群获取完毕，总计: " + (groups.size() - 1) + "个，耗时 " + (System.currentTimeMillis() - time) + "ms");
                }).build());
    }

    private static void refreshFriendList() {
        IQueue.sendRequest(RequestBuilder.builder()
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("StartIndex", 0);
                }}).toJSONString())
                .setUrl("http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=GetQQUserList&timeout=10")
                .setAction(result -> {
                    long time = System.currentTimeMillis();
                    JSONObject replay = JSON.parseObject(result);
                    for (JSONObject j : replay.getJSONArray("Friendlist").toArray(new JSONObject[0])) {
                        Friend friend = new Friend();
                        friend.setNick(j.getString("NickName"));
                        friend.setId(j.getLongValue("FriendUin"));
                        friends.put(friend.getId(), friend);
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
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=SendMsg&timeout=10";
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
                .setAction(c -> EventManager.invoke(new FriendMessageSendEvent(message.list.get(0)))).build());
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
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=SendMsg&timeout=10";
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
                .setAction(c -> EventManager.invoke(new FriendMessageSendEvent(message.list.get(0)))).build());
    }

    /**
     * 发送群组消息
     *
     * @param message 消息
     * @param group   群
     */
    public static void sendGroupMessage(MessageChain message, Group group) {
        JSONObject json = new JSONObject();
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=SendMsg&timeout=10";
        json.put("toUser", group.getId());
        json.put("sendToType", 2);
        json.put("groupid", 0);
        json.put("atUser", 0);
        json.put("sendMsgType", "TextMsg");
        final StringBuilder msg = new StringBuilder();
        final StringBuilder ats = new StringBuilder().append("[ATUSER(");
        message.forEach(m -> {
            if (m instanceof TextMessage) {
                msg.append(((TextMessage) m).msg);
            } else if (m instanceof AtMessage) {
                //json.put("atUser", ((AtMessage) m).getId());
                for (Long id : ((AtMessage) m).getId()) {
                    ats.append(Strings.isNullOrEmpty(ats.toString().replace("[ATUSER(", "")) ? id : "," + id);
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
        ats.append(")]");
        if (!Strings.isNullOrEmpty(ats.toString().replace("[ATUSER(", "").replace(")]", ""))) {
            msg.append(ats.toString());
        }
        json.put("content", msg.toString());
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(json.toJSONString())
                .setAction(c -> EventManager.invoke(new GroupMessageSendEvent(message.list.get(0), group))).build());
    }

    /**
     * 撤回消息
     *
     * @param message
     */
    public static void recallGroup(BaseMessage message, Group group) {
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=PbMessageSvc.PbMsgWithDraw&timeout=10";
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("GroupID", group.getId());
                    put("MsgSeq", message.getMsgid());
                    put("MsgRandom", message.getRandom());
                }}).toJSONString())
                .setAction(m -> {

                }).build());
    }

    /**
     * 设置全体禁言
     *
     * @param group
     * @param set   true为开启，false为关闭
     */
    public static void setAllMuteOfGroup(Group group, boolean set) {
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=OidbSvc.0x89a_0&timeout=10";
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("GroupID", group.getId());
                    put("Switch", set ? 1 : 0);
                }}).toJSONString())
                .setAction(m -> {

                }).build());
    }

    /**
     * 设置群员禁言
     *
     * @param group
     * @param member 要禁言的群员
     * @param set    要禁言的时长，0为取消。单位: 分钟
     */
    public static void setMuteOfGroupMember(Group group, Member member, long set) {
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=OidbSvc.0x570_8&timeout=10";
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("GroupID", group.getId());
                    put("ShutUpUserID", member.getId());
                    put("ShutTim", set);
                }}).toJSONString())
                .setAction(m -> {

                }).build());
    }


    /**
     * 下载好友文件
     *
     * @param fileId 文件id
     * @param file   保存路径
     */
    public static void downloadFriendFile(String fileId, File file) {
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=OfflineFilleHandleSvr.pb_ftn_CMD_REQ_APPLY_DOWNLOAD-1200&timeout=10";
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("FileID", fileId);
                }}).toJSONString())
                .setAction(m -> {
                    JSONObject j = JSON.parseObject(m);
                    OPQGlobal.main.execute(() -> {
                        try {
                            FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(new URL(url).openConnection().getInputStream()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }).build());
    }

    /**
     * 下载群文件
     *
     * @param group  群
     * @param fileId 文件id
     * @param file   保存路径
     */
    public static void downloadGroupFile(Group group, String fileId, File file) {
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=OidbSvc.0x6d6_2&timeout=10";
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("FileID", fileId);
                    put("GroupID", group.getId());
                }}).toJSONString())
                .setAction(m -> {
                    JSONObject j = JSON.parseObject(m);
                    OPQGlobal.main.execute(() -> {
                        try {
                            FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(new URL(url).openConnection().getInputStream()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }).build());
    }

    /**
     * 发送一个群公告
     *
     * @param group  群
     * @param title  标题
     * @param body   内容
     * @param pinned 是否顶置
     * @param type   类型
     */
    public static void sendGroupAnnounce(Group group,
                                         String title,
                                         String body,
                                         boolean pinned,
                                         GroupAnnounceType type
    ) {
        String url = "http://" + OPQGlobal.url + "/v1/Group/Announce?qq=" + OPQGlobal.qq;
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(new JSONObject(new HashMap<String, Object>() {{
                    put("GroupID", group.getId());
                    put("Title", title);
                    put("Text", body);
                    put("Pinned", pinned ? 1 : 0);
                    put("Type", type.getType());
                }}).toJSONString())
                .setAction(m -> {

                }).build());
    }

    /**
     * at全体成员
     */
    public static void atAll(Group group) {
        JSONObject json = new JSONObject();
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=SendMsg&timeout=10";
        json.put("toUser", group.getId());
        json.put("sendToType", 2);
        json.put("groupid", 0);
        json.put("atUser", 0);
        json.put("sendMsgType", "TextMsg");
        json.put("content", "");
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(json.toJSONString())
                .setAction(c -> EventManager.invoke(new GroupMessageSendEvent(new AtMessage(new Long[]{0L}), group))).build());
    }

    /**
     * 回复消息
     *
     * @param message 要回复的消息
     * @param msgs    要发送的消息
     */
    public static void reply(BaseMessage message, MessageChain msgs) {
        JSONObject json = new JSONObject();
        String url = "http://" + OPQGlobal.url + "/v1/LuaApiCaller?qq=" + OPQGlobal.qq + "&funcname=SendMsg&timeout=10";
        json.put("groupid", 0);
        json.put("atUser", 0);
        json.put("sendMsgType", "ReplayMsg");
        if (message.getSender() instanceof Member) {
            json.put("toUser", ((Member) message.getSender()).getFromGroup().getId());
            json.put("sendToType", 2);
        } else {
            json.put("toUser", ((Friend) message.getSender()).getId());
            json.put("sendToType", 1);
        }

        final StringBuilder msg = new StringBuilder();
        final StringBuilder ats = new StringBuilder().append("[ATUSER(");
        msgs.forEach(m -> {
            if (m instanceof TextMessage) {
                msg.append(((TextMessage) m).msg);
            } else if (m instanceof AtMessage) {
                //json.put("atUser", ((AtMessage) m).getId());
                for (Long id : ((AtMessage) m).getId()) {
                    ats.append(Strings.isNullOrEmpty(ats.toString().replace("[ATUSER(", "")) ? id : "," + id);
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
        ats.append(")]");
        if (!Strings.isNullOrEmpty(ats.toString().replace("[ATUSER(", "").replace(")]", ""))) {
            msg.append(ats.toString());
        }
        json.put("content", msg.toString());
        json.put("replayInfo", new JSONObject(new HashMap<String, Object>() {{
            put("MsgSeq", message.getMsgid());
            put("MsgTime", message.getTime());
            put("UserID", message.getSender() instanceof Member ? ((Member) message.getSender()).getId() : ((Friend) message.getSender()).getId());
            put("RawContent", message.messageToString());
        }}));
        IQueue.sendRequest(RequestBuilder.builder()
                .setUrl(url)
                .setRequest(json.toJSONString())
                .setAction(q -> {
                })
                .build());
    }

    /**
     * 取消群员禁言
     *
     * @param group
     * @param member 要解除禁言的群员
     */
    public static void cancelMuteOfGroupMember(Group group, Member member) {
        setMuteOfGroupMember(group, member, 0);
    }

    /**
     * 通过id获取当前qq的群
     *
     * @param id
     * @return
     */
    public static Group getGroup(long id) {
        return groups.containsKey(id) ? groups.get(id) : new Group();
    }

    /**
     * 通过id获取当前qq的好友
     *
     * @param id
     * @return
     */
    public static Friend getFriend(long id) {
        return friends.containsKey(id) ? friends.get(id) : new Friend();
    }

    /**
     * 通过id获取当前qq的群
     *
     * @param id
     * @return
     */

    public static Group getGroupOrNull(long id) {
        return groups.containsKey(id) ? groups.get(id) : null;
    }

    /**
     * 通过id获取当前qq的好友
     *
     * @param id
     * @return
     */
    public static Friend getFriendOrNull(long id) {
        return friends.containsKey(id) ? friends.get(id) : null;
    }

}
