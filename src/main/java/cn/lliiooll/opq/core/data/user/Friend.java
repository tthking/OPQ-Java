package cn.lliiooll.opq.core.data.user;

import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.message.MessageChain;
import lombok.Data;

@Data
public class Friend implements User {
    public String nick = "";
    public long id;

    public Friend(long qq) {
        this.id = qq;
    }

    public Friend() {
    }

    public void sendMessage(MessageChain messageChain) {
        OPQGlobal.sendFriendMessage(messageChain, this);
    }
}
