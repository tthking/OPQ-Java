package cn.lliiooll.opq.core.managers.event.data;

import cn.lliiooll.opq.core.data.message.data.Message;
import cn.lliiooll.opq.core.managers.event.Event;
import cn.lliiooll.opq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 好友消息事件
 */
public class FriendMessageSendEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final Message message;

    public FriendMessageSendEvent(Message message) {
        this.message = message;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
