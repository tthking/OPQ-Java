package cn.lliiooll.opq.core.data.message;


import cn.lliiooll.opq.core.data.message.data.Message;
import cn.lliiooll.opq.core.data.message.data.TextMessage;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;

public class MessageChain {

    public final List<Message> list = Lists.newLinkedList();

    public MessageChain(Message message) {
        list.add(message);
    }

    public static MessageChain newCall(Message message) {
        return new MessageChain(message);
    }

    public static MessageChain newCall(String message) {
        return new MessageChain(new TextMessage(message));
    }

    public MessageChain put(Message message) {
        list.add(message);
        return this;
    }

    public void forEach(Consumer<Message> action) {
        list.forEach(action);
    }
}
