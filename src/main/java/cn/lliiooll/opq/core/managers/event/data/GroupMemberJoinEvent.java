package cn.lliiooll.opq.core.managers.event.data;

import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.managers.event.Event;
import cn.lliiooll.opq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 群组主动加入
 */
public class GroupMemberJoinEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final long id;
    @Getter
    private final long inviteId;
    @Getter
    private final Group group;
    @Getter
    private final String name;

    public GroupMemberJoinEvent(long inviteId, long id, Group group, String name) {
        this.id = id;
        this.inviteId = inviteId;
        this.group = group;
        this.name = name;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
