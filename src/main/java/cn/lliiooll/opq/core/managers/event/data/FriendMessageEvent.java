package cn.lliiooll.opq.core.managers.event.data;

import cn.lliiooll.opq.core.data.message.data.BaseMessage;
import cn.lliiooll.opq.core.data.message.data.Message;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.managers.event.Event;
import cn.lliiooll.opq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 好友消息事件
 */
public class FriendMessageEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final BaseMessage message;
    @Getter
    private final Friend sender;

    public FriendMessageEvent(BaseMessage message, Friend sender) {
        this.message = message;
        this.sender = sender;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
