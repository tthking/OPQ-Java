package cn.lliiooll.opq.core.managers.event.data;

import cn.lliiooll.opq.core.managers.event.Event;
import cn.lliiooll.opq.core.managers.event.HandlerList;

/**
 * 二维码
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
