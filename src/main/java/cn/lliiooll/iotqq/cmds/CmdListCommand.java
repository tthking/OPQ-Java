package cn.lliiooll.iotqq.cmds;


import cn.lliiooll.iotqq.OPQMain;
import cn.lliiooll.iotqq.core.OPQGlobal;
import cn.lliiooll.iotqq.core.data.message.MessageChain;
import cn.lliiooll.iotqq.core.data.message.MessageFrom;
import cn.lliiooll.iotqq.core.data.message.data.TextMessage;
import cn.lliiooll.iotqq.core.data.user.Friend;
import cn.lliiooll.iotqq.core.managers.cmd.CommandExecutor;
import cn.lliiooll.iotqq.core.managers.cmd.CommandManager;
import cn.lliiooll.iotqq.core.managers.cmd.CommandResult;
import cn.lliiooll.iotqq.core.managers.cmd.annotations.Command;

import java.util.Arrays;
import java.util.Set;

@Command(name = "cmdlist", description = "查看指令列表", alias = {"help", "帮助"}, usage = "help")
public class CmdListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandResult result) {

        StringBuilder msg = new StringBuilder();
        if (result.getArgs().size() < 1) {
            Set<Class<?>> executors = CommandManager.usages.keySet();
            for (Class<?> executor : executors) {
                Command c = executor.getAnnotation(Command.class);
                msg.append("指令:")
                        .append(c.name())
                        .append(" 用法:")
                        .append(c.usage())
                        .append(" 别称:")
                        .append(Arrays.toString(c.alias()))
                        .append(" 描述:")
                        .append(c.description())
                        .append("\n");
            }
        } else {
            String label = result.getArgs().get(0);
            if (CommandManager.executors.containsKey(label)) {
                Command c = CommandManager.executors.get(label).getAnnotation(Command.class);
                msg.append("指令:")
                        .append(c.name())
                        .append(" 用法:")
                        .append(c.usage())
                        .append(" 别称:")
                        .append(Arrays.toString(c.alias()))
                        .append(" 描述:")
                        .append(c.description())
                        .append("\n");
            } else {
                msg.append("不存在的指令。");
            }
        }
        if (result.type == MessageFrom.GROUP)
            OPQGlobal.sendGroupMessage(MessageChain.newCall(new TextMessage("未知的指令.请使用 " + OPQMain.command + "cmdlist 来获得指令列表")), result.group);
        else
            OPQGlobal.sendFriendMessage(MessageChain.newCall(new TextMessage("未知的指令.请使用 " + OPQMain.command + "cmdlist 来获得指令列表")), (Friend) result.sender);
        return true;
    }
}
