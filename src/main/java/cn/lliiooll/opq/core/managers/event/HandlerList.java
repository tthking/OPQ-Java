package cn.lliiooll.opq.core.managers.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class HandlerList {

    public Map<Method, Class<?>> listeners = Maps.newHashMap();
    public Map<Integer, Method> orders = Maps.newHashMap();
    public List<Integer> order = Lists.newArrayList();
}
