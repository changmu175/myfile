package com.xdja.comm.https;

/**
 * Created by THZ on 2015/7/8.
 * 网路操作回调
 */
public interface IHttpResult {
    /**
     * 错误回调
     * @param errorBean 错误信息结构bean
     */
    void onFail(HttpErrorBean errorBean);

    /**
     * 请求返回的数据
     * @param body 返回消息体
     */
    void onSuccess(String body);

    /**
     * 异常错误
     */
    void onErr();
}
