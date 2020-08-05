package cn.lliiooll.iotqq.core.managers.event.data;

import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.managers.event.Event;
import cn.lliiooll.iotqq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 好友请求事件
 */
public class FriendRequestEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    public final long id;
    @Getter
    public final String question;
    @Getter
    public final boolean isFromGroup;
    @Getter
    public final Group fromGroup;


    public FriendRequestEvent(long id, String question, boolean isFromGroup, Group fromGroup) {
        this.id = id;
        this.question = question;
        this.isFromGroup = isFromGroup;
        this.fromGroup = fromGroup;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
