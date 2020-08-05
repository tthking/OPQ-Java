package cn.lliiooll.iotqq.core.managers.event.data;

import cn.lliiooll.iotqq.core.managers.event.Event;
import cn.lliiooll.iotqq.core.managers.event.HandlerList;

/**
 * 群组加入
 */
public class CheckLoginQrcodeEvent extends Event {
    private static HandlerList handlers = new HandlerList();


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
