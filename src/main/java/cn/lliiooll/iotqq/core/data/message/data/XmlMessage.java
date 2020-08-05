package cn.lliiooll.iotqq.core.data.message.data;

import lombok.Data;

@Data
public class XmlMessage implements Message {

    public String msg;

    public XmlMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public String messageToString() {
        return msg;
    }
}
