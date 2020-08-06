package cn.lliiooll.opq.event;

import cn.lliiooll.opq.OPQMain;
import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.data.message.MessageChain;
import cn.lliiooll.opq.core.data.message.MessageFrom;
import cn.lliiooll.opq.core.data.message.data.PicMessage;
import cn.lliiooll.opq.core.data.message.data.VoiceMessage;
import cn.lliiooll.opq.core.managers.cmd.CommandManager;
import cn.lliiooll.opq.core.managers.event.EventHandler;
import cn.lliiooll.opq.core.managers.event.data.FriendMessageEvent;
import cn.lliiooll.opq.core.managers.event.data.GroupMessageEvent;
import org.apache.logging.log4j.LogManager;

public class CommandListener {

    @EventHandler(order = -1)
    public void onGroup(GroupMessageEvent event) {
        if (event.getMessage().messageToString().startsWith(OPQMain.command)) {
            LogManager.getLogger().info("触发群指令");
            CommandManager.call(event.getMessage(), event.getSender(), event.getGroup(), MessageFrom.GROUP);
        }
    }

    @EventHandler(order = -1)
    public void onPrivate(FriendMessageEvent event) {
        if (event.getMessage().messageToString().startsWith(OPQMain.command)) {
            LogManager.getLogger().info("触发私聊指令");
            CommandManager.call(event.getMessage(), event.getSender(), new Group(), MessageFrom.FRIEND);
        }
    }
}
