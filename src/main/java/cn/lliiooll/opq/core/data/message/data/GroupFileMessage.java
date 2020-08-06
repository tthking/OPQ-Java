package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.data.message.data.BaseMessage;
import cn.lliiooll.opq.core.data.message.data.Message;
import cn.lliiooll.opq.core.data.user.Member;
import cn.lliiooll.opq.core.data.user.User;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupFileMessage extends BaseMessage {
    private final String fileId;
    private final String fileName;
    private final long fileSize;

    public GroupFileMessage(JSONObject jsonObject, long msgid, long random, long time, Member sender) {
        super(msgid, random, time, sender);
        this.fileId = jsonObject.getString("FileID");
        this.fileName = jsonObject.getString("FileName");
        this.fileSize = jsonObject.getLongValue("FileSize");
    }

    @Override
    public String messageToString() {
        return "[群文件]";
    }
}
