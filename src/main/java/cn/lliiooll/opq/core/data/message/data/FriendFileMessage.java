package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.data.message.data.BaseMessage;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.User;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FriendFileMessage extends BaseMessage {
    private final String fileId;
    private final String fileName;
    private final long fileSize;

    public FriendFileMessage(JSONObject jsonObject, long msgid, long random, long time, Friend sender) {
        super(msgid, random, time, sender);
        this.fileId = jsonObject.getString("FileID");
        this.fileName = jsonObject.getString("FileName");
        this.fileSize = jsonObject.getLongValue("FileSize");
    }

    @Override
    public String messageToString() {
        return "[好友文件]";
    }
}
