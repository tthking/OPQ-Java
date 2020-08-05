package cn.lliiooll.iotqq.core.data.user;

import cn.lliiooll.iotqq.core.IOTGlobal;
import cn.lliiooll.iotqq.core.data.message.MessageChain;
import lombok.Data;

@Data
public class Friend implements User {
    public String nick = "";
    public long id;

    public void sendMessage(MessageChain messageChain) {
        IOTGlobal.sendFriendMessage(messageChain, this);
    }
}
