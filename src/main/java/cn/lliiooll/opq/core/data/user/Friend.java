package cn.lliiooll.opq.core.data.user;

import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.message.MessageChain;
import lombok.Data;

@Data
public class Friend implements User {
    public String nick = "";
    public long id;

    public void sendMessage(MessageChain messageChain) {
        OPQGlobal.sendFriendMessage(messageChain, this);
    }
}
