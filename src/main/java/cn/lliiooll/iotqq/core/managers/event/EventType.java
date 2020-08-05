package cn.lliiooll.iotqq.core.managers.event;

public enum EventType {
    /**
     * 成功添加好友
     */
    ON_EVENT_NOTIFY_PUSHADDFRD,
    /**
     * 好友请求
     */
    ON_EVENT_FRIEND_ADDED,
    /**
     * 好友请求
     */
    ON_EVENT_FRIEND_ADD,
    /**
     * 主动退群
     */
    ON_EVENT_GROUP_EXIT_SUCC,
    /**
     * 好友撤回消息
     */
    ON_EVENT_FRIEND_REVOKE,
    /**
     * 删好友
     */
    ON_EVENT_FRIEND_DELETE,
    /**
     * 群禁言,userId=0为全员禁言
     */
    ON_EVENT_GROUP_SHUT,
    /**
     * 群成员撤回消息
     */
    ON_EVENT_GROUP_REVOKE,
    /**
     * 群成员加入事件
     */
    ON_EVENT_GROUP_JOIN,
    /**
     * 群管理员变更 	Flag 1升管理 0降管理
     */
    ON_EVENT_GROUP_ADMIN,
    /**
     * 群成员退出事件
     */
    ON_EVENT_GROUP_EXIT,
    /**
     * 主动进群成功事件
     */
    ON_EVENT_GROUP_JOIN_SUCC,
    /**
     * 群管理系统消息
     * 邀请加群等通知
     */
    ON_EVENT_GROUP_ADMINSYSNOTIFY
}



