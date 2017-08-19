package com.xdja.imsdk.volley.toolbox;

import com.xdja.imsdk.volley.error.VolleyError;

/**
 * 请求回调方法
 *
 * @author Administrator
 */
public interface HttpCallback {

    /**
     * 请求开始
     */
    void onStart();

    /**
     * 请求结束
     */
    void onFinish();

    /**
     * 请求返回结果
     *
     * @param string 返回结果信息
     */
    void onResult(String string);

    /**
     * 请求错误信息
     *
     * @param error    错误信息
     */
    void onError(VolleyError error);

    /**
     * 请求过程中，网络发生变化
     *
     * @param code    网络发生变化错误码
     * @param message 错误信息
     */
    void onNetChanged(int code, String message);

    /**
     * 请求取消
     */
    void onCanceled();

    /**
     * 请求进度回调
     *
     * @param count 文件总大小
     * @param translateSize 已经传输文件大小
     * @param percent  已经传输文件百分比
     */
    void onLoading(long count, long translateSize, int percent);
}