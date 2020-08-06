package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JsonMessage extends BaseMessage {

    public String msg;

    public JsonMessage(String msg, long msgid, long random) {
        super(msgid, random, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.msg = msg;
    }

    @Override
    public String messageToString() {
        return msg;
    }
}
