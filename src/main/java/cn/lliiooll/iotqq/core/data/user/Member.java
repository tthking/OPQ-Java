package cn.lliiooll.iotqq.core.data.user;

import cn.lliiooll.iotqq.core.data.group.Group;
import lombok.Data;

@Data
public class Member implements User{
    public long id;
    public String name;
    public Group fromGroup;
}
