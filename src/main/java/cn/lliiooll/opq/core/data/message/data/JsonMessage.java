package cn.lliiooll.opq.core.data.message.data;

import lombok.Data;

@Data
public class JsonMessage implements Message {

    public String msg;

    public JsonMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public String messageToString() {
        return msg;
    }
}
