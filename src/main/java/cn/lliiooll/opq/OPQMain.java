package cn.lliiooll.opq;

import cn.lliiooll.opq.core.OPQ;
import cn.lliiooll.opq.core.OPQBuilder;

public class OPQMain {


    public static String command = "#";

    /**
     * 已知bug: json消息识别为图片消息
     *
     * @param args
     */
    public static void main(String[] args) {
        OPQ iotqq = OPQBuilder.builder()
                .setURL("127.0.0.1:8888")
                .setQQ(3483706632L)// 机器人QQ号
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
