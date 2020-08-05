package cn.lliiooll.iotqq.core.data.message.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class ReplayMessage implements Message {

    public final String content;
    private final long msgId;
    private final long time = 1L;
    private final long userId = 1L;

    public ReplayMessage(JSONObject data) {
        this.content = data.getString("Content");
        this.msgId = data.getLongValue("MsgSeq");
    }

    @Override
    public String messageToString() {
        return "[回复]";
    }
}
