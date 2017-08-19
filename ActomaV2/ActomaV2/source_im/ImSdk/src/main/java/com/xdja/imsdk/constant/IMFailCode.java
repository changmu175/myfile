package com.xdja.imsdk.constant;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：消息失败码，消息收发失败原因描述     <br>
 * 创建时间：2016/12/5 11:47                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class IMFailCode {
    /**
     * 成功的消息
     */
    public static final int NOT_FAIL = 0;

    /**
     * 默认失败错误码，发送消息失败默认值
     */
    public static final int FAIL_DEFAULT = -1;

    /**
     * 发送失败，对方版本不支持此功能
     */
    public static final int CHECK_FAIL = -2;

    /**
     * 消息加密失败
     */
    public static final int ENCRYPT_FAIL = -3;

    /**
     * 消息发送到IM Server失败
     */
    public static final int SENT_FAIL = -4;

    /**
     * 不是好友关系
     */
    public static final int NON_FRIENDS = -5;

    /**
     * 文件上传下载失败
     */
    public static final int FILE_FAIL = -6;

    /**
     * 不支持的消息类型
     */
    public static final int UN_SUPPORT = -7;
}
