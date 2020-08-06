package cn.lliiooll.iotqq.core.data.group;

import cn.lliiooll.iotqq.core.OPQGlobal;
import cn.lliiooll.iotqq.core.data.message.MessageChain;
import lombok.Data;

@Data
public class Group {
    public long id;
    public String name;

    public void sendMessage(MessageChain messageChain) {
        OPQGlobal.sendGroupMessage(messageChain, this);
    }
}
