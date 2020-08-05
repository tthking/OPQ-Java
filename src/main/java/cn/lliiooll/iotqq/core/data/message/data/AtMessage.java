package cn.lliiooll.iotqq.core.data.message.data;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class AtMessage implements Message {

    public final JSONArray id;
    public String content = "[AT]";

    public AtMessage(JSONArray id) {
        this.id = id;
    }

    public AtMessage(JSONArray id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String messageToString() {
        return content;
    }
}
