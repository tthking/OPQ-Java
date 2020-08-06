package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.data.user.User;
import lombok.Data;

@Data
public class BaseMessage implements Message {
    private long time;
    private User sender;
    private long msgid;
    private long random;

    public BaseMessage(long msgid, long random, long time, User sender) {
        this.msgid = msgid;
        this.random = random;
        this.time = (time + "").length() >= (System.currentTimeMillis() + "").length() ? time / 1000 : time;
        this.sender = sender;
    }

    public BaseMessage() {

    }

    @Override
    public String messageToString() {
        return "[不支持的消息类型]";
    }
}
