package com.xdja.imsdk.constant;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：消息状态定义       <br>
 * 创建时间：2016/11/16 16:02  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class MsgState {
    /**
     * 消息状态，发送失败
     */
    public static final int MSG_STATE_FAIL = -1;

    /**
     * 消息状态，正在发送中，为默认状态
     */
    public static final int MSG_STATE_DEFAULT = 0;

    /**
     * 消息状态，已发送
     */
    public static final int MSG_STATE_SEND = 1;

    /**
     * 消息状态，已接收（接收方）/已送达（发送方）
     */
    public static final int MSG_STATE_REC = 2;

    /**
     * 消息状态，已阅读
     */
    public static final int MSG_STATE_READ = 3;

    /**
     * 消息状态， 已销毁
     */
    public static final int MSG_STATE_BOMB = 4;

}
