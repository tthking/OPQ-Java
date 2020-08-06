package cn.lliiooll.opq.core.data.message.data;

import lombok.Data;

@Data
public class TextMessage implements Message {

    public String msg;

    public TextMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public String messageToString() {
        return msg;
    }
}
