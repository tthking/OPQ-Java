package cn.lliiooll.opq.core.managers.cmd;

import cn.lliiooll.opq.OPQMain;
import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.data.message.MessageChain;
import cn.lliiooll.opq.core.data.message.MessageFrom;
import cn.lliiooll.opq.core.data.message.data.Message;
import cn.lliiooll.opq.core.data.message.data.TextMessage;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.User;
import cn.lliiooll.opq.core.managers.cmd.annotations.Command;
import cn.lliiooll.opq.utils.JarUtils;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandManager {

    private static ExecutorService cmds = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat("CommandTask-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    public static Map<String, Class<?>> executors = Maps.newHashMap();
    public static Map<Class<?>, String> usages = Maps.newHashMap();

    @SneakyThrows
    public static void init() {
        long start = System.currentTimeMillis();
        int load = 0;
        Set<Class<?>> classes = JarUtils.getAllLoadClasses();
        for (Class<?> loadClass : classes) {
            if (loadClass.getAnnotation(Command.class) != null) {
                Command c = loadClass.getAnnotation(Command.class);
                executors.put(c.name(), loadClass);// 注册主要指令
                if (OPQMain.isDebug())
                    LogManager.getLogger().info("注册指令 " + loadClass.getName() + "(" + c.name() + ")");
                for (String alia : c.alias()) {
                    executors.put(alia, loadClass);
                    if (OPQMain.isDebug())
                        LogManager.getLogger().info("注册别称 " + loadClass.getName() + "(" + alia + ")");
                }
                usages.put(loadClass, c.usage());
                load++;
            }
        }
        LogManager.getLogger().info("加载了 " + load + " 个指令，耗时 " + (System.currentTimeMillis() - start) + "(ms).");
    }

    static long last = System.currentTimeMillis();

    public static void call(Message messages, User sender, Group group, MessageFrom type) {
        if (System.currentTimeMillis() - last < 3000) {
            if (type == MessageFrom.GROUP)
                OPQGlobal.sendGroupMessage(MessageChain.newCall(new TextMessage("指令发送太快了哦", msgid, random, time, sender)), group);
            else
                OPQGlobal.sendFriendMessage(MessageChain.newCall(new TextMessage("指令发送太快了哦", msgid, random, time, sender)), (Friend) sender);
        } else {
            cmds.execute(() -> callA(messages, sender, group, type));
        }
        last = System.currentTimeMillis();
    }

    @SneakyThrows
    private static void callA(Message messages, User sender, Group group, MessageFrom type) {
        String msg = messages.messageToString().replaceFirst(OPQMain.command, "");
        while (msg.contains("  ")) {
            msg = msg.replace("  ", " ");// 吧两个空格替换为一个
        }
        String[] sourceCmd = msg.split(" ");// 通过空格来切割
        if (sourceCmd.length > 0) {// 保证不为空
            String label = sourceCmd[0];
            boolean q = has(label);
            LogManager.getLogger().info("====================");
            LogManager.getLogger().info(q);
            LogManager.getLogger().info(label);
            LogManager.getLogger().info(executors.keySet());
            LogManager.getLogger().info("====================");
            if (q) {
                String[] args = new String[sourceCmd.length - 1];
                System.arraycopy(sourceCmd, 1, args, 0, sourceCmd.length - 1);
                Class<?> executor = executors.get(label);
                CommandResult cr = new CommandResult();
                cr.setArgs(Arrays.asList(args));
                cr.setSender(sender);
                cr.setSource(messages);
                cr.setType(type);
                cr.setGroup(type == MessageFrom.GROUP ? group : new Group());
                if (!(Arrays.asList(executor.getInterfaces()).contains(CommandExecutor.class) && (boolean) executor.getMethod("onCommand", CommandResult.class).invoke(executor.newInstance(), cr))) {
                    if (type == MessageFrom.GROUP)
                        OPQGlobal.sendGroupMessage(MessageChain.newCall(new TextMessage("指令执行失败。用法：" + usages.get(executor), msgid, random, time, sender)), group);
                    else
                        OPQGlobal.sendFriendMessage(MessageChain.newCall(new TextMessage("指令执行失败。用法：" + usages.get(executor), msgid, random, time, sender)), (Friend) sender);
                }
            } else {
                if (type == MessageFrom.GROUP)
                    group.sendMessage(MessageChain.newCall(new TextMessage("未知的指令.请使用 " + OPQMain.command + "cmdlist 来获得指令列表", msgid, random, time, sender)));
                else
                    OPQGlobal.sendFriendMessage(MessageChain.newCall(new TextMessage("未知的指令.请使用 " + OPQMain.command + "cmdlist 来获得指令列表", msgid, random, time, sender)), (Friend) sender);
            }
        }
    }

    public static boolean has(String s) {
        return executors.containsKey(s);
    }
}
