package com.xdja.comm.https;

import com.xdja.comm.https.Property.HttpResultSate;

/**
 * Created by THZ on 2015/7/18.
 * 网络请求返回的数据结构
 * 用于同步的返回
 */
public class HttpsRequstResult {

    /**
     * 0,失败
     * 1，成功
     */
    public HttpResultSate result;

    /**
     * 返回的错误信息
     */
    public HttpErrorBean httpErrorBean;

    /**
     * 数据body
     */
    public String body;
}
