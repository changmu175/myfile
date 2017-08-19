package com.xdja.dependence.exeptions.matcher;

import android.support.annotation.Nullable;

/**
 * Created by xdja-fanjiandong on 2016/3/17.
 * 异常处理接口定义
 */
public interface ExceptionAction {
    /**
     * 异常处理动作
     *
     * @param code        异常错误码
     * @param message     异常信息
     * @param userMessage 异常用户信息
     */
    void action(@Nullable String code,@Nullable String message,@Nullable String userMessage);
}
