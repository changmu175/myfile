package com.xdja.imp.data.error;

import android.support.annotation.Nullable;

/**
 * <p>Summary:错误处理接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:14:21</p>
 */
public interface OkHandler<T extends OkException> {
    /**
     * 错误处理方法
     * @param exception 待处理的错误
     */
    void handle(@Nullable T exception);
}
