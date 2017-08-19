package com.xdja.imsdk.constant.internal;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  HTTP请求参数常量                              <br>
 * 创建时间：2016/11/27 下午6:40                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class HttpApiConstant {
    /**
     * HTTP请求头
     */
    public static final String HTTP = "http://";

    /**
     * HTTPS请求头
     */
    public static final String HTTPS = "https://";

    /**
     * URL后缀
     */
    public static final String URL_SUFFIX = "/webrelay/api";

    /**
     * 请求参数RPC
     */
    public static final String JSONRPC = "2.0";

    /**
     * 请求参数id
     */
    public static final String ID = "1";

    /**
     * 网络请求配置ticket
     */
    public static final String HTTP_CONFIG_TICKET = "ticket";

    /**
     * 用户登录im server 进行验证
     */
    public static final String MSG_LOGIN  ="login";

    /**
     * 用户退出与im server 进行验证
     */
    public static final String MSG_LOGOUT  ="logout";

    /**
     * 发送消息接口
     */
    public static final String MSG_SEND = "sendimmsg";

    /**
     * 批量更新消息状态接口
     */
    public static final String MSG_SEND_BATCH = "updateimstatmsg";

    /**
     * 拉取消息接口
     */
    public static final String MSG_GET = "getimmsg";

    /**
     * 拉取消息时的默认空字符串
     */
    public static final String STRING_DEFAULT_EMPTY = "";

    /**
     * 拉取消息大小的底数
     */
    public static final int POWER_BASE_PULL = 4;

    /**
     * 一次拉取消息时的最大消息数量
     */
    public static final int MAX_PULL_MSG_COUNT = 256;

    /**
     * HTTP请求成功
     */
    public static final int HTTP_OK = 40000;

    /**
     * HTTP请求无连接错误码
     */
    public static final int HTTP_NO = 40004;

    /**
     * HTTP请求超时错误码
     */
    public static final int HTTP_TIMEOUT = 40007;

    /**
     * 保存用户上次pull完成后的所有消息的最大消息Id（不一定是第一个，因为后台不能保证消息id的顺序，
     * 但一般第一个id是最大），即在pull完成后的maxCycleId，默认为0。
     */
    public static final String MAX = "max";

    /**
     * 保存用户上次pull完成后的所有消息的最后一个消息id（不一定是最小，因为后台不能保证消息id的顺序，
     * 但一般最后一个id是最小），即在pull完成后的lastCycleId，默认为0。
     */
    public static final String LAST = "last";

    /**
     * 保存用户已处理的消息id，默认为0。
     */
    public static final String PROCESS = "process";

    /**
     * 上次拉取消息结束的状态,0表示正常，1表示不正常
     */
    public static final String STATE = "state";

    public static final long LONG_VALUE_0 = 0;
    public static final long LONG_VALUE_1 = 1;

    public enum ParseType {
        LOGIN,
        PULL,
        NORMAL,
        STATE
    }
}
