package cn.lliiooll.iotqq.core.managers.event.data;

import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.managers.event.Event;
import cn.lliiooll.iotqq.core.managers.event.HandlerList;
import lombok.Getter;

/**
 * 群组主动退出
 */
public class GroupExitSuccEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    @Getter
    private final long id;
    @Getter
    private final long inviteId;
    @Getter
    private final Group group;
    @Getter
    private final String name;

    public GroupExitSuccEvent(long inviteId, long id, Group group, String name) {
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
