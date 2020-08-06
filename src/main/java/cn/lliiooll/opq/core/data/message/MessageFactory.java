package cn.lliiooll.opq.core.data.message;

import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.data.message.data.*;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.Member;
import cn.lliiooll.opq.core.data.user.User;
import cn.lliiooll.opq.core.managers.event.EventManager;
import cn.lliiooll.opq.core.managers.event.data.FriendMessageEvent;
import cn.lliiooll.opq.core.managers.event.data.GroupMessageEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;

public class MessageFactory {
    public static void execute(String dataText, MessageFrom type) {
        JSONObject d = JSON.parseObject(dataText);
        JSONObject data = d.getJSONObject("CurrentPacket").getJSONObject("Data");
        //long qq = d.getLong("CurrentQQ");
        long msgid = data.getLongValue("MsgSeq");
        String msg = data.getString("Content");
        MessageType messageType = MessageType.valueOf(data.getString("MsgType"));
        long time = System.currentTimeMillis();
        if (type == MessageFrom.GROUP) {
            String senderName = data.getString("FromNickName");
            String groupName = data.getString("FromGroupName");
            long senderId = data.getLongValue("FromUserId");
            if (senderId != OPQGlobal.getQq()) {
                long groupId = data.getLongValue("FromGroupId");
                //long time = data.getLongValue("MsgTime");
                long random = data.getLongValue("MsgRandom");
                Group group = new Group();
                group.setId(groupId);
                group.setName(groupName);
                Member member = new Member();
                member.setFromGroup(group);
                member.setId(senderId);
                member.setName(senderName);
                Message message = executeMessage(msgid, random, msg, time, member, messageType);
                EventManager.invoke(new GroupMessageEvent(message, group, member));
            }
        } else if (type == MessageFrom.FRIEND) {
            long senderId = data.getLongValue("FromUin");
            if (senderId != OPQGlobal.getQq()) {
                Friend friend = new Friend();
                friend.setId(senderId);
                Message message = executeMessage(msgid, 0, msg, time, friend, messageType);
                EventManager.invoke(new FriendMessageEvent(message, friend));
            }
            //long getterId = data.getLongValue("ToUin");
        }
    }

    private static Message executeMessage(long msgid, long random, String msg, long time, User sender, MessageType messageType) {
        switch (messageType) {
            case TextMsg:
                return new TextMessage(msg, msgid, random, time, sender);
            case VoiceMsg:
                return new VoiceMessage(JSON.parseObject(msg), msgid, random, time, sender);
            case JsonMsg:
                return new XmlMessage(JSON.parseObject(JSON.parseObject(msg).getString("Content")).getString("Content"), msgid, random, time, sender);
            case PicMsg:
                return new PicMessage(JSON.parseObject(msg), msgid, random, time, sender);
            case AtMsg:
                JSONObject data = JSON.parseObject(msg);
                return new AtMessage(data.getJSONArray("UserID"), data.getString("Content"), msgid, random, time, sender);
            case XmlMsg:
                return new JsonMessage(msg, msgid, random);
            case ReplayMsg:
                return new ReplayMessage(JSON.parseObject(msg), msgid, random, time, sender);
            case VideoMsg:
                return new VideoMessage(JSON.parseObject(msg), msgid, random, time, sender);
            case GroupFileMsg:
                return new GroupFileMessage(JSON.parseObject(msg), msgid, random, time, (Member) sender);
            case FriendFileMsg:
                //LogManager.getLogger().info(msg);
                return new FriendFileMessage(JSON.parseObject(msg), msgid, random, time, (Friend) sender);
            case RedBagMsg:
                return new RedBagMessage(JSON.parseObject(msg).getString("Content"), msgid, random, time, sender);
            case BigFaceMsg:
                LogManager.getLogger().info(msg);
            case TempSessionMsg:
                LogManager.getLogger().info(msg);
        }
        return new BaseMessage(msgid, random, time, sender);
    }
}
