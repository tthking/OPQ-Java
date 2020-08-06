package cn.lliiooll.opq.core.data.message;

import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.data.message.data.*;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.Member;
import cn.lliiooll.opq.core.managers.event.EventManager;
import cn.lliiooll.opq.core.managers.event.data.FriendMessageEvent;
import cn.lliiooll.opq.core.managers.event.data.GroupMessageEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MessageFactory {
    public static void execute(String dataText, MessageFrom type) {
        JSONObject d = JSON.parseObject(dataText);
        JSONObject data = d.getJSONObject("CurrentPacket").getJSONObject("Data");
        long qq = d.getLong("CurrentQQ");
        long msgid = data.getLongValue("MsgSeq");
        String msg = data.getString("Content");
        MessageType messageType = MessageType.valueOf(data.getString("MsgType"));
        if (type == MessageFrom.GROUP) {
            String senderName = data.getString("FromNickName");
            String groupName = data.getString("FromGroupName");
            long senderId = data.getLongValue("FromUserId");
            long groupId = data.getLongValue("FromGroupId");
            //long time = data.getLongValue("MsgTime");
            //long random = data.getLongValue("MsgRandom");
            Group group = new Group();
            group.setId(groupId);
            group.setName(groupName);
            Member member = new Member();
            member.setFromGroup(group);
            member.setId(senderId);
            member.setName(senderName);
            Message message = executeMessage(msg, messageType);
            EventManager.invoke(new GroupMessageEvent(message, group, member));
        } else if (type == MessageFrom.FRIEND) {
            long senderId = data.getLongValue("FromUin");
            //long getterId = data.getLongValue("ToUin");
            Friend friend = new Friend();
            friend.setId(senderId);
            Message message = executeMessage(msg, messageType);
            EventManager.invoke(new FriendMessageEvent(message, friend));
        }
    }

    private static Message executeMessage(String msg, MessageType messageType) {
        switch (messageType) {
            case TextMsg:
                return new TextMessage(msg);
            case VoiceMsg:
                return new VoiceMessage(JSON.parseObject(msg));
            case JsonMsg:
                return new XmlMessage(JSON.parseObject(msg).getString("Content"));
            case PicMsg:
                return new PicMessage(JSON.parseObject(msg));
            case AtMsg:
                JSONObject data = JSON.parseObject(msg);
                return new AtMessage(data.getJSONArray("UserID"), data.getString("Content"));
            case XmlMsg:
                return new JsonMessage(msg);
            case ReplayMsg:
                return new ReplayMessage(JSON.parseObject(msg));
            case VideoMsg:
                return new VideoMessage(JSON.parseObject(msg));
        }
        return new UnkonwMessage();
    }
}
