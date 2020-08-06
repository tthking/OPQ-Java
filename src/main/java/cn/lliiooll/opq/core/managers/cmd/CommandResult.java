package cn.lliiooll.opq.core.managers.cmd;

import cn.lliiooll.opq.core.data.group.Group;
import cn.lliiooll.opq.core.data.message.MessageFrom;
import cn.lliiooll.opq.core.data.message.data.Message;
import cn.lliiooll.opq.core.data.user.User;
import lombok.Data;

import java.util.List;

@Data
public class CommandResult {
    /**
     * 发送者
     */
    public User sender;
    /**
     * 后缀
     */
    public List<String> args;
    /**
     * 原来的消息
     */
    public Message source;
    /**
     * 群
     */
    public Group group;
    /**
     * 来源类型
     */
    public MessageFrom type;
}
