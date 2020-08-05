package cn.lliiooll.iotqq.core.managers.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    /**
     * 顺序，数字越小优先级越高
     *
     * @return 数字
     */
    int order() default 0;
}