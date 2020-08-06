package cn.lliiooll.opq.core.data.message;

public enum MessageType {
    /**
     * 纯文本消息
     */
    TextMsg,
    /**
     * 群成员的消息
     */
    AtMsg,
    /**
     * 图片消息
     */
    PicMsg,
    /**
     * 大表情消息
     */
    BigFaceMsg,
    /**
     * 红包消息
     */
    RedBagMsg,
    /**
     * 语音消息
     */
    VoiceMsg,
    /**
     * Json格式的复杂消息
     */
    JsonMsg,
    /**
     * Xml格式的复杂消息
     */
    XmlMsg,
    /**
     * 群文件消息
     */
    GroupFileMsg,
    /**
     * 视频消息
     */
    VideoMsg,
    /**
     * 未知
     */
    TempSessionMsg,
    /**
     * 回复消息
     */
    ReplayMsg
}
