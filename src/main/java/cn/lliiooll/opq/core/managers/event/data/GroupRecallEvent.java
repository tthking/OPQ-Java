package cn.lliiooll.opq.core.managers.event.data;

import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.managers.event.Event;
import cn.lliiooll.opq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 群组撤回
 */
public class GroupRecallEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final long id;
    @Getter
    private final long msgId;
    @Getter
    private final Group group;
    @Getter
    private final long adminId;

    public GroupRecallEvent(long id, long msgId, Group group, long adminId) {
        this.id = id;
        this.msgId = msgId;
        this.group = group;
        this.adminId = adminId;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
