package cn.lliiooll.iotqq.core.managers.event.data;

import cn.lliiooll.iotqq.core.managers.event.Event;
import cn.lliiooll.iotqq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 好友撤回
 */
public class FriendRecallEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final long id;
    @Getter
    private final long msgId;

    public FriendRecallEvent(long id, long msgId) {
        this.id = id;
        this.msgId = msgId;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
