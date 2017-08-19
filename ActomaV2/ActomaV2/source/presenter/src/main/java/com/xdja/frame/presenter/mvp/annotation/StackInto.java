package com.xdja.frame.presenter.mvp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xdja-fanjiandong on 2016/3/10.
 * 定义Activity是否入栈的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StackInto {
    boolean value() default true;
}
