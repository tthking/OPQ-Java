package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.data.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class XmlMessage extends BaseMessage {

    public String msg;

    public XmlMessage(String msg, long msgid, long random, long time, User sender) {
        super(msgid,random, time, sender);
        this.msg = msg;
    }

    @Override
    public String messageToString() {
        return msg;
    }
}
