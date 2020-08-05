package cn.lliiooll.iotqq;

import cn.lliiooll.iotqq.core.IOTQQ;
import cn.lliiooll.iotqq.core.IOTQQBuilder;

public class IOTQQMain {


    public static String command = "#";

    /**
     * 已知bug: json消息识别为图片消息
     *
     * @param args
     */
    public static void main(String[] args) {
        IOTQQ iotqq = IOTQQBuilder.builder()
                .setURL("IOTQQ地址")
                .setQQ(1234567890L)// 机器人QQ号
                .build();
        iotqq.init("cn.lliiooll");// 开发包名, 用于自动注册指令和监听器用
    }

    /**
     * 调试用
     *
     * @return
     */
    public static boolean isDebug() {
        return false;
    }
}
