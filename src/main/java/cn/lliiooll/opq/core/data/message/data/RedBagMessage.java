package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.data.user.User;

public class RedBagMessage extends BaseMessage {
    private final String msg;

    public RedBagMessage(String content, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        this.msg = content;
    }

    @Override
    public String messageToString() {
        return "[红包]";
    }
}
