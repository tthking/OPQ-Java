package cn.lliiooll.iotqq.core.data.group;

import cn.lliiooll.iotqq.core.IOTGlobal;
import cn.lliiooll.iotqq.core.data.message.MessageChain;
import lombok.Data;

@Data
public class Group {
    public long id;
    public String name;

    public void sendMessage(MessageChain messageChain) {
        IOTGlobal.sendGroupMessage(messageChain, this);
    }
}
