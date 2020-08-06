package cn.lliiooll.iotqq.event;

import cn.lliiooll.iotqq.OPQMain;
import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.data.message.MessageFrom;
import cn.lliiooll.iotqq.core.managers.cmd.CommandManager;
import cn.lliiooll.iotqq.core.managers.event.EventHandler;
import cn.lliiooll.iotqq.core.managers.event.data.FriendMessageEvent;
import cn.lliiooll.iotqq.core.managers.event.data.GroupMessageEvent;
import org.apache.logging.log4j.LogManager;

public class CommandListener {

    @EventHandler
    public void onGroup(GroupMessageEvent event) {
        if (event.getMessage().messageToString().startsWith(OPQMain.command)) {
            LogManager.getLogger().info("触发群指令");
            CommandManager.call(event.getMessage(), event.getSender(), event.getGroup(), MessageFrom.GROUP);
        }
    }

    @EventHandler
    public void onPrivate(FriendMessageEvent event) {
        if (event.getMessage().messageToString().startsWith(OPQMain.command)) {
            LogManager.getLogger().info("触发私聊指令");
            CommandManager.call(event.getMessage(), event.getSender(), new Group(), MessageFrom.FRIEND);
        }
    }
}
