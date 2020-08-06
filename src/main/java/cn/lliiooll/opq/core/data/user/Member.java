package cn.lliiooll.opq.core.data.user;

import cn.lliiooll.opq.core.data.group.Group;
import lombok.Data;

@Data
public class Member implements User{
    public long id;
    public String name;
    public Group fromGroup;
}
