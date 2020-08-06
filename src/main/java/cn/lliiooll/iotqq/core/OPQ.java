package cn.lliiooll.iotqq.core;

import cn.lliiooll.iotqq.core.keeplive.AsyncKeepLive;
import cn.lliiooll.iotqq.core.managers.cmd.CommandManager;
import cn.lliiooll.iotqq.core.managers.event.EventManager;
import cn.lliiooll.iotqq.core.queue.IQueue;
import cn.lliiooll.iotqq.utils.JarUtils;
import cn.lliiooll.iotqq.websocket.OPQClient;
import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
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
                log.info("心跳中...");
                AsyncKeepLive.waitReplay(System.currentTimeMillis());
                client.send("2");
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
        listen();
        OPQGlobal.init(this.getQq(), this.getUrl());
        IQueue.init();
    }

    private void listen() {
        if (!this.client.isClosed() && !this.client.isClosing()) {
            JSONArray ja = new JSONArray();
            ja.add("GetWebConn");
            ja.add("" + this.getQq());
            this.client.send("42" + ja.toJSONString());
        }
    }
}
