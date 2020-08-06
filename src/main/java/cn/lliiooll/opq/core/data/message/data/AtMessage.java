package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.User;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;

@EqualsAndHashCode(callSuper = true)
@Data
public class AtMessage extends BaseMessage {

    public final JSONArray id;
    public String content = "[AT]";

    public AtMessage(JSONArray id) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.id = id;
    }

    public AtMessage(JSONArray id, String content, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        this.id = id;
        this.content = content;
    }

    public AtMessage(long[] id, String content) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.id = new JSONArray(Collections.singletonList(id));
        this.content = content;
    }

    @Override
    public String messageToString() {
        return content;
    }
}
