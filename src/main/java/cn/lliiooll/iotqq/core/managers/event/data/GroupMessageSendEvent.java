package cn.lliiooll.iotqq.core.managers.event.data;

import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.data.message.data.Message;
import cn.lliiooll.iotqq.core.data.user.Member;
import cn.lliiooll.iotqq.core.managers.event.Event;
import cn.lliiooll.iotqq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 群消息事件
 */
public class GroupMessageSendEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final Message message;
    @Getter
    private final Group group;


    public GroupMessageSendEvent(Message message, Group group) {
        this.message = message;
        this.group = group;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
