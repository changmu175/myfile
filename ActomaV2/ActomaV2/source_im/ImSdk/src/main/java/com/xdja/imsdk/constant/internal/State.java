package com.xdja.imsdk.constant.internal;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：ImSdk消息状态值                    <br>
 * 创建时间：2016/12/14 10:46                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class State {
    /**
     * 消息校验失败，一般为文件大小过大
     */
    public static final int FAIL_VALID = -11;

    /**
     * 消息时间同步失败，发送失败的消息在发送成功后，更新时间为服务器时间
     */
    public static final int FAIL_TIME = -10;// TODO: 2016/12/20

    /**
     * 不支持的消息类型
     */
    public static final int UN_SUPPORT = -9;

    /**
     * 文件上传失败
     */
    public static final int UP_FAIL = -8;

    /**
     * 文件下载失败
     */
    public static final int DOWN_FAIL = -7;

    /**
     * 不是好友关系
     */
    public static final int NON_FRIENDS = -6;

    /**
     * 消息发送到IM Server失败
     */
    public static final int SENT_FAIL = -5;

    /**
     * 消息加密成功，未开始发送
     */
    public static final int SENT_NON = -4;

    /**
     * 消息或文件加密失败
     */
    public static final int ENCRYPT_FAIL = -3;

    /**
     * 检测版本号异常，对方版本未知
     */
    public static final int CHECK_ERROR = -2;

    /**
     * 校验失败，对方不支持此功能
     */
    public static final int CHECK_FAIL = -1;

    /**
     * 消息状态，正在发送中，为默认状态，未加密，未发送
     */
    public static final int DEFAULT = 0;

    /**
     * 消息状态，已发送
     */
    public static final int SENT = 1;

    /**
     * 消息状态，已接收（接收方）/已送达（发送方）
     */
    public static final int REC = 2;

    /**
     * 消息状态，已阅读
     */
    public static final int READ = 3;

    /**
     * 消息状态，已销毁
     */
    public static final int BOMB = 4;

}
