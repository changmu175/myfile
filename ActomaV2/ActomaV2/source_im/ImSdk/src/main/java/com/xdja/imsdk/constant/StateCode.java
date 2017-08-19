package com.xdja.imsdk.constant;

import com.xdja.imsdk.constant.internal.Constant;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：ImSdk状态码                      <br>
 * 创建时间：2016/11/21 18:30                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class StateCode {
    /**
     * 状态码,ImSdk服务启动成功
     */
    public static final int SDK_SERVICE_OK= Constant.CODE_BEGIN +10;

    /**
     * 状态码,ImSdk服务断开
     */
    public static final int SDK_SERVICE_FAIL = Constant.CODE_BEGIN + 11;

    /**
     * 状态码,ImSdk内部数据库异常
     */
    public static final int DATABASE_ERROR = Constant.CODE_BEGIN + 12;

    /**
     * 状态码,和IM服务器连接成功
     */
    public static final int SDK_CONNECTED_OK = Constant.CODE_BEGIN + 20;

    /**
     * 状态码,和IM服务器连接失败
     */
    public static final int SDK_CONNECTED_FAIL = Constant.CODE_BEGIN + 21;

    /**
     * 状态码,APushSdk长链接断开
     */
    public static final int APUSH_DISCONNECTED = Constant.CODE_BEGIN + 22;

    /**
     * 状态码,未知错误
     */
    public static final int SDK_UNKNOWN = Constant.CODE_BEGIN + 23;

    /**
     * 状态码，ticket过期
     */
    public static final int SDK_TICKET_EXPIRE = Constant.CODE_BEGIN + 24;
}
