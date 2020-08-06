package cn.lliiooll.iotqq.websocket;


import cn.lliiooll.iotqq.core.data.message.MessageFactory;
import cn.lliiooll.iotqq.core.data.message.MessageFrom;
import cn.lliiooll.iotqq.core.keeplive.AsyncKeepLive;
import cn.lliiooll.iotqq.core.managers.event.EventFactory;
import cn.lliiooll.iotqq.core.managers.event.EventManager;
import cn.lliiooll.iotqq.core.managers.event.data.CheckLoginQrcodeEvent;
import cn.lliiooll.iotqq.core.managers.event.data.LoginSuccessEvent;
import cn.lliiooll.iotqq.utils.TaskUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.ExecutorService;


public class OPQClient extends WebSocketClient {

    static Logger log = LogManager.getLogger();
    private static ExecutorService main = TaskUtils.create("MessageTask-%d");

    public OPQClient(String uri) {
        super(URI.create(uri));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("Websocket连接成功");
        //send("42[\"GetWebConn\",\"" + IOTQQMain.qq + "\"]");
        //send("42[\"GetQQUserList\",\"3557265016\"]");
    }

    @Override
    public void onMessage(String message) {
        //log.info(message);
        main.execute(() -> {
            if (message.startsWith("0")) {
                String json = message.replace("0", "");
                JSONObject j = JSON.parseObject(json);
                log.info("连接IOTQQ成功~");
                log.info("连接id: " + j.getString("sid"));
            } else if (message.length() > 2) {
                //int code = Integer.parseInt(message.substring(0, 2));
                String pack = message.substring(2);
                JSONArray ja = JSONArray.parseArray(pack);
                String data = ja.getString(1);
                String string = ja.getString(0);
                if ("OnGroupMsgs".equals(string)) {// 群消息
                    MessageFactory.execute(data, MessageFrom.GROUP);
                } else if ("OnFriendMsgs".equals(string)) {// 好友消息
                    MessageFactory.execute(data, MessageFrom.FRIEND);
                } else if ("OnEvents".equals(string)) {// 事件
                    EventFactory.execute(JSONObject.parseObject(data));
                } else if ("OnCheckLoginQrcode".equals(string)) {// 扫码成功
                    EventManager.invoke(new CheckLoginQrcodeEvent());
                } else if ("OnLoginSuccess".equals(string)) {// 登陆成功
                    EventManager.invoke(new LoginSuccessEvent());
                }
            } else {
                int code = Integer.parseInt(message);
                if (code == 3) {
                    AsyncKeepLive.replay(System.currentTimeMillis());
                    log.info("心跳成功，耗时 " + AsyncKeepLive.getTimeout());
                }
            }
        });

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        main.shutdown();
        log.info("连接已经关闭");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}

