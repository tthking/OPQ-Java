package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.OPQ;
import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextMessage extends BaseMessage {

    public String msg = "[消息]";

    public TextMessage(String msg, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        this.msg = msg;
    }

    public TextMessage(String msg) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.qq));
        this.msg = msg;
    }

    @Override
    public String messageToString() {
        return msg;
    }
}
