package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.data.user.User;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReplayMessage extends BaseMessage {

    public final String content;
    private final long msgId;
    private final long time = 1L;
    private final long userId = 1L;

    public ReplayMessage(JSONObject data, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        this.content = data.getString("Content");
        this.msgId = data.getLongValue("MsgSeq");
    }

    @Override
    public String messageToString() {
        return "[回复]";
    }
}
