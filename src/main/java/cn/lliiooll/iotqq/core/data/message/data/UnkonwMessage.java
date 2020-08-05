package cn.lliiooll.iotqq.core.data.message.data;

public class UnkonwMessage implements Message {
    @Override
    public String messageToString() {
        return "[不支持的消息类型]";
    }
}
