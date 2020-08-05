package cn.lliiooll.iotqq.core.managers.event;

import cn.lliiooll.iotqq.IOTQQMain;
import cn.lliiooll.iotqq.utils.JarUtils;
import cn.lliiooll.iotqq.utils.TaskUtils;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

public class EventManager {

    private static ExecutorService main = TaskUtils.create("EventTask-%d");

    public static void init() {
        long time = System.currentTimeMillis();
        long count = 0;
        for (Class<?> clazz : JarUtils.getAllLoadClasses()) {
            for (Method method : clazz.getDeclaredMethods()) {
                method.setAccessible(true);
                //LogManager.getLogger().info("是否监听器 " + method.getName() + " => " + xxx);
                //LogManager.getLogger().info("扫描方法 " + method.getName());
                if (method.getAnnotation(EventHandler.class) != null) {
                    if (method.getParameterCount() == 1) {
                        Class<?> eventC = method.getParameterTypes()[0];
                        if (eventC.getSuperclass() != Event.class) {
                            LogManager.getLogger().info("事件 " + eventC.getName() + " 无效: 不是一个标准事件");
                        } else {
                            try {
                                eventC.getMethod("getHandlers");
                                Method q = eventC.getMethod("getHandlerList");
                                q.setAccessible(true);
                                HandlerList list = (HandlerList) q.invoke(null);
                                EventHandler eh = method.getAnnotation(EventHandler.class);
                                addOrder(eh.order(), method, list);
                                list.listeners.put(method, clazz);
                                Field f = eventC.getDeclaredField("handlers");
                                Collections.sort(list.order);
                                f.setAccessible(true);
                                f.set(null, list);
                                if (IOTQQMain.isDebug())
                                    LogManager.getLogger().info("监听器 " + method.getName() + " 注册到 " + eventC.getName() + " 完毕");
                                count++;
                            } catch (Exception e) {
                                LogManager.getLogger().info("事件 " + eventC.getName() + " 无效: 不是一个标准事件");
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // 有多余的参数
                        LogManager.getLogger().info("监听器 " + method.getName() + " 无效: 参数不合法");
                    }
                }
            }
        }
        LogManager.getLogger().info("加载了 " + count + " 个监听器，用时 " + (System.currentTimeMillis() - time) + "ms");
    }

    private static void addOrder(int order, Method method, HandlerList list) {
        if (list.orders.containsKey(order)) {
            addOrder(order + 1, method, list);
        } else {
            list.orders.put(order, method);
            list.order.add(order);
        }
    }


    public static void invoke(Event event) {
        //LogManager.getLogger().info(event.getClass().getName());
        main.execute(() -> {
            try {
                Class<?> eventC = event.getClass();
                Method q = eventC.getMethod("getHandlerList");
                q.setAccessible(true);
                HandlerList list = (HandlerList) q.invoke(null);
                //Collections.sort(list.order);
                for (Integer order : list.order) {
                    Method m = list.orders.get(order);
                    Class<?> clazz = list.listeners.get(m);
                    m.invoke(clazz.newInstance(), event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //LogManager.getLogger().info("事件成功~");
        });
    }
}
