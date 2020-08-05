package cn.lliiooll.iotqq.core.managers.event.data;

import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.managers.event.Event;
import cn.lliiooll.iotqq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 群组管理变更
 */
public class GroupAdminEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final long id;
    @Getter
    private final long inviteId;
    @Getter
    private final Group group;
    @Getter
    private final String name;

    public GroupAdminEvent(long inviteId, long id, Group group, String name) {
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
