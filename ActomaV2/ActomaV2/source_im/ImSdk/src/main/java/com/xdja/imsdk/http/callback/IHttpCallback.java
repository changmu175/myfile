package com.xdja.imsdk.http.callback;

import org.json.JSONObject;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  数据请求回调                                             <br>
 * 创建时间：2016/11/27 下午4:09                           <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public interface IHttpCallback {
    /**
     * 返回成功结果
     * @param jsonObject 返回请求JSON对象
     */
    void onSuccess(JSONObject jsonObject);

    /**
     * 返回请求错误结果
     * @param code 错误码
     * @param jsonObject 错误JSON对象
     */
    void onFailed(int code , JSONObject jsonObject);


    /**
     * 返回网络状态变换状态码
     * @param code 状态码
     * @param jsonObject JSON对象
     */
    void onNetChanged(int code, JSONObject jsonObject);
}
