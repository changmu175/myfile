package com.xdja.imp.data.error;

import android.support.annotation.Nullable;

/**
 * <p>Summary:错误匹配器接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:14:05</p>
 */
public interface OkMatcher<T extends OkException> {
    /**
     * 根据错误码匹配响应的用户错误信息
     *
     * @param exception 异常对象
     * @return 用户错误信息
     */
    @Nullable
    String match(@Nullable T exception);
}
