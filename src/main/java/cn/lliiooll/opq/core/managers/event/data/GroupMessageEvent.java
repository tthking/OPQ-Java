package cn.lliiooll.opq.core.managers.event.data;

import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.data.message.data.BaseMessage;
import cn.lliiooll.opq.core.data.message.data.Message;
import cn.lliiooll.opq.core.data.user.Member;
import cn.lliiooll.opq.core.managers.event.Event;
import cn.lliiooll.opq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 群消息事件
 */
public class GroupMessageEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final BaseMessage message;
    @Getter
    private final Group group;
    @Getter
    private final Member sender;


    public GroupMessageEvent(BaseMessage message, Group group, Member sender) {
        this.message = message;
        this.group = group;
        this.sender = sender;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
