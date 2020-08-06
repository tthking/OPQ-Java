package cn.lliiooll.opq.core;

import cn.lliiooll.opq.core.keeplive.AsyncKeepLive;
import cn.lliiooll.opq.core.managers.cmd.CommandManager;
import cn.lliiooll.opq.core.managers.event.EventManager;
import cn.lliiooll.opq.core.queue.IQueue;
import cn.lliiooll.opq.utils.JarUtils;
import cn.lliiooll.opq.websocket.OPQClient;
import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

public class OPQ {

    @Getter
    private final String url;
    @Getter
    private final long qq;
    private final Logger log = LogManager.getLogger();
    @Getter
    private OPQClient client;

    public OPQ(String url, long qq) {
        this.url = url;
        this.qq = qq;
    }

    public void connect() {
        OPQClient client = new OPQClient("ws://" + this.url + "/socket.io/" + URI.create("http://" + this.url + "/").getQuery());
        this.client = client;
        client.setConnectionLostTimeout(-1);
        client.connect();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!client.isClosed()) {
                    log.info("心跳中...");
                    AsyncKeepLive.waitReplay(System.currentTimeMillis());
                    client.send("2");
                }
            }
        }, "AsyncKeepLive").start();
    }

    /**
     * 初始化机器人
     *
     * @param pack
     */
    public void init(String pack) {
        JarUtils.scanByPackage(pack);
        EventManager.init();
        CommandManager.init();
        connect();
        OPQGlobal.init(this.getQq(), this.getUrl());
        IQueue.init();
        listen();
    }

    @SneakyThrows
    private void listen() {
        LogManager.getLogger().info("正在连接。。。");
        Thread.sleep(5000);
        JSONArray ja = new JSONArray();
        ja.add("GetWebConn");
        ja.add("" + this.getQq());
        this.client.send("42" + ja.toJSONString());
    }
}
