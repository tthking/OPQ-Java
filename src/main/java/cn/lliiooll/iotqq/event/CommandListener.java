package cn.lliiooll.iotqq.event;

import cn.lliiooll.iotqq.IOTQQMain;
import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.data.message.MessageFrom;
import cn.lliiooll.iotqq.core.managers.cmd.CommandManager;
import cn.lliiooll.iotqq.core.managers.event.EventHandler;
import cn.lliiooll.iotqq.core.managers.event.data.FriendMessageEvent;
import cn.lliiooll.iotqq.core.managers.event.data.GroupMessageEvent;

public class CommandListener {

    @EventHandler
    public void onGroup(GroupMessageEvent event) {
        if (event.getMessage().toString().startsWith(IOTQQMain.command)) {
            CommandManager.call(event.getMessage(), event.getSender(), event.getGroup(), MessageFrom.GROUP);
        }
    }

    @EventHandler
    public void onPrivate(FriendMessageEvent event) {
        if (event.getMessage().toString().startsWith(IOTQQMain.command)) {
            CommandManager.call(event.getMessage(), event.getSender(), new Group(), MessageFrom.FRIEND);
        }
    }
}
