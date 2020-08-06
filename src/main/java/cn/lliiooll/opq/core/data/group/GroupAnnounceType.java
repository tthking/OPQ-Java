package cn.lliiooll.opq.core.data.group;

import lombok.Getter;

public enum GroupAnnounceType {
    /**
     * 弹窗公告
     */
    POPUP(10),
    SENDNEW(20);

    @Getter
    private final int type;

    GroupAnnounceType(int i) {
        this.type = i;
    }
}
