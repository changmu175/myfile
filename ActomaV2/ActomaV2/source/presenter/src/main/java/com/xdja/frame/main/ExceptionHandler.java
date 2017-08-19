package com.xdja.frame.main;

import android.support.annotation.Nullable;

import com.xdja.dependence.exeptions.OkException;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.frame.main</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:14:16</p>
 */
public interface ExceptionHandler {

    /**
     * 处理OkException异常
     *
     * @param okCode  错误码
     * @param userMsg 用户信息
     * @param ex      错误完整信息
     * @param mark    业务标识
     * @return 是否继续进行默认处理
     */
    boolean handleOkException(@Nullable String okCode,
                              @Nullable String userMsg,
                              @Nullable OkException ex,
                              @Nullable String mark);

    /**
     * OkException的默认处理方法
     *
     * @param code    错误码
     * @param userMsg 用户信息
     * @param ex      错误完整信息
     * @param mark    业务标识
     */
    void defaultOkException(@Nullable String code,
                            @Nullable String userMsg,
                            @Nullable OkException ex,
                            @Nullable String mark);

    /**
     * 处理Throwable错误
     *
     * @param throwable Throwable对象
     * @param mark      业务标识
     * @return 是否继续进行默认处理
     */
    boolean handlerThrowable(@Nullable Throwable throwable,@Nullable String mark);

    /**
     * 处理Throwable错误
     *
     * @param throwable Throwable对象
     * @param mark      业务标识
     */
    void defaultThrowable(@Nullable Throwable throwable,@Nullable String mark);

}
