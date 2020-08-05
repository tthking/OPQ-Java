package cn.lliiooll.iotqq.core.managers.event;

import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.data.user.Friend;
import cn.lliiooll.iotqq.core.managers.event.data.*;
import com.alibaba.fastjson.JSONObject;

public class EventFactory {
    public static void execute(JSONObject data) {
        JSONObject d = data.getJSONObject("CurrentPacket").getJSONObject("Data");
        EventType type = EventType.valueOf(d.getString("EventName"));
        JSONObject eData = d.getJSONObject("EventData");
        JSONObject eMsg = d.getJSONObject("EventMsg");
        switch (type) {
            case ON_EVENT_GROUP_EXIT:
            case ON_EVENT_GROUP_JOIN:
                Group g1 = new Group();
                g1.setName("");
                g1.setId(eMsg.getLongValue("FromUin"));
                EventManager.invoke(new GroupJoinEvent(eData.getLongValue("InviteUin"), eData.getLongValue("UserID"), g1, eData.getString("UserName")));
            case ON_EVENT_GROUP_SHUT:
            case ON_EVENT_GROUP_ADMIN:
            case ON_EVENT_FRIEND_ADDED:
                Group group = new Group();
                group.setName(eData.getString("FromGroupName"));
                group.setId(eData.getLongValue("FromGroupId"));
                EventManager.invoke(new FriendRequestEvent(eData.getLongValue("UserID"), eData.getString("Content"), eData.getLongValue("FromGroupId") == 0, group));
            case ON_EVENT_GROUP_REVOKE:
                Group g = new Group();
                g.setName("");
                g.setId(eData.getLongValue("GroupID"));
                EventManager.invoke(new GroupRecallEvent(eData.getLongValue("UserID"), eData.getLongValue("MsgSeq"), g, eData.getLongValue("AdminUserID")));
            case ON_EVENT_FRIEND_DELETE:
                EventManager.invoke(new FriendDeleteEvent(eData.getLongValue("UserID")));
            case ON_EVENT_FRIEND_REVOKE:
                EventManager.invoke(new FriendRecallEvent(eData.getLongValue("UserID"), eData.getLongValue("MsgSeq")));
            case ON_EVENT_GROUP_EXIT_SUCC:
            case ON_EVENT_GROUP_JOIN_SUCC:
            case ON_EVENT_NOTIFY_PUSHADDFRD:
                Friend friend = new Friend();
                friend.setId(eData.getLongValue("UserID"));
                friend.setNick(eData.getString("NickName"));
                EventManager.invoke(new FriendAddedEvent(friend));
            case ON_EVENT_GROUP_ADMINSYSNOTIFY:
        }
    }
}
