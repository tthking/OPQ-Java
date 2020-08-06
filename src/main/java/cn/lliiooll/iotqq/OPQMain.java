package cn.lliiooll.iotqq;

import cn.lliiooll.iotqq.core.OPQ;
import cn.lliiooll.iotqq.core.OPQBuilder;

public class OPQMain {


    public static String command = "#";

    /**
     * 已知bug: json消息识别为图片消息
     *
     * @param args
     */
    public static void main(String[] args) {
        OPQ opq = OPQBuilder.builder()
                .setURL("OPQ地址,例如127.0.0.1:8888,不带http")
                .setQQ(1234567890L)// 机器人QQ号
                .build();
        opq.init("cn.lliiooll");// 开发包名, 用于自动注册指令和监听器用
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
