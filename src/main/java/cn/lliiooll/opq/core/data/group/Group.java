package cn.lliiooll.opq.core.data.group;

import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.message.MessageChain;
import lombok.Data;

@Data
public class Group {
    public long id;
    public String name;

    public void sendMessage(MessageChain messageChain) {
        OPQGlobal.sendGroupMessage(messageChain, this);
    }
}
