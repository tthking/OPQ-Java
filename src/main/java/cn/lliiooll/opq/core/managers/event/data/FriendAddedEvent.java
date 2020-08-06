package cn.lliiooll.opq.core.managers.event.data;

import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.managers.event.Event;
import cn.lliiooll.opq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 好友添加完毕事件
 */
public class FriendAddedEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final Friend sender;


    public FriendAddedEvent(Friend sender) {
        this.sender = sender;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
