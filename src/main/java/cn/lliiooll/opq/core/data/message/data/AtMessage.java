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

    public final Long[] id;
    public String content = "[AT]";

    public AtMessage(JSONArray id) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.id = id.toArray(new Long[0]);
    }

    public AtMessage(JSONArray id, String content, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        this.id = id.toArray(new Long[0]);
        this.content = content;
    }

    public AtMessage(Long[] id) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.id = id;
    }

    @Override
    public String messageToString() {
        return content;
    }
}
