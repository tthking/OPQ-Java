package cn.lliiooll.iotqq.core.managers.cmd;

import cn.lliiooll.iotqq.IOTQQMain;
import cn.lliiooll.iotqq.core.IOTGlobal;
import cn.lliiooll.iotqq.core.data.group.Group;
import cn.lliiooll.iotqq.core.data.message.MessageChain;
import cn.lliiooll.iotqq.core.data.message.MessageFrom;
import cn.lliiooll.iotqq.core.data.message.data.Message;
import cn.lliiooll.iotqq.core.data.message.data.TextMessage;
import cn.lliiooll.iotqq.core.data.user.Friend;
import cn.lliiooll.iotqq.core.data.user.Member;
import cn.lliiooll.iotqq.core.data.user.User;
import cn.lliiooll.iotqq.core.managers.cmd.annotations.Command;
import cn.lliiooll.iotqq.utils.JarUtils;
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
                if (IOTQQMain.isDebug())
                    LogManager.getLogger().info("注册指令 " + loadClass.getName() + "(" + c.name() + ")");
                for (String alia : c.alias()) {
                    executors.put(alia, loadClass);
                    if (IOTQQMain.isDebug())
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
                IOTGlobal.sendGroupMessage(MessageChain.newCall(new TextMessage("指令发送太快了哦")), group);
            else
                IOTGlobal.sendFriendMessage(MessageChain.newCall(new TextMessage("指令发送太快了哦")), (Friend) sender);
        } else {
            //cmds.execute(() -> callA(messages, sender));
            callA(messages, sender, group, type);
        }
        last = System.currentTimeMillis();
    }

    @SneakyThrows
    private static void callA(Message messages, User sender, Group group, MessageFrom type) {
        String msg = messages.toString().replaceFirst(IOTQQMain.command, "");
        while (msg.contains("  ")) {
            msg = msg.replace("  ", " ");// 吧两个空格替换为一个
        }
        String[] sourceCmd = msg.split(" ");// 通过空格来切割
        if (sourceCmd.length > 0) {// 保证不为空
            String label = sourceCmd[0];
            if (executors.containsKey(label)) {
                String[] args = new String[sourceCmd.length - 1];
                System.arraycopy(sourceCmd, 1, args, 0, sourceCmd.length - 1);
                Class<?> executor = executors.get(label);
                CommandResult cr = new CommandResult();
                cr.setArgs(Arrays.asList(args));
                cr.setSender(sender);
                cr.setSource(messages);
                cr.setGroup(type == MessageFrom.GROUP ? group : new Group());
                if (!(Arrays.asList(executor.getInterfaces()).contains(CommandExecutor.class) && (boolean) executor.getMethod("onCommand", CommandResult.class).invoke(executor.newInstance(), cr))) {
                    if (type == MessageFrom.GROUP)
                        IOTGlobal.sendGroupMessage(MessageChain.newCall(new TextMessage("指令执行失败。用法：" + usages.get(executor))), group);
                    else
                        IOTGlobal.sendFriendMessage(MessageChain.newCall(new TextMessage("指令执行失败。用法：" + usages.get(executor))), (Friend) sender);
                }
            } else {
                if (type == MessageFrom.GROUP)
                    IOTGlobal.sendGroupMessage(MessageChain.newCall(new TextMessage("未知的指令.请使用 " + IOTQQMain.command + "cmdlist 来获得指令列表")), group);
                else
                    IOTGlobal.sendFriendMessage(MessageChain.newCall(new TextMessage("未知的指令.请使用 " + IOTQQMain.command + "cmdlist 来获得指令列表")), (Friend) sender);
            }
        }
    }

    public static boolean has(String s) {
        return executors.containsKey(s);
    }
}
