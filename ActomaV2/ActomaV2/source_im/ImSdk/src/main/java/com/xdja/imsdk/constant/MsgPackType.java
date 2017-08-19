package com.xdja.imsdk.constant;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：消息组合类型                       <br>
 * 创建时间：2016/11/21 20:58                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class MsgPackType {
    /**
     * 普通单聊文本消息
     */
    public static final int NORMAL_PP_TEXT = 1;

    /**
     * 普通单聊文件消息
     */
    public static final int NORMAL_PP_FILE = 2;

    /**
     * 普通群聊文本消息
     */
    public static final int NORMAL_PG_TEXT = 5;

    /**
     * 普通群聊文件消息
     */
    public static final int NORMAL_PG_FILE = 6;

    /**
     * 闪信单聊文本消息
     */
    public static final int BOMB_PP_TEXT = 9;

    /**
     * 闪信单聊文件消息
     */
    public static final int BOMB_PP_FILE = 10;

    /**
     * 闪信群聊文本消息
     */
    public static final int BOMB_PG_TEXT = 13;

    /**
     * 闪信群聊文件消息
     */
    public static final int BOMB_PG_FILE = 14;

    /**
     * 单聊提示消息
     */
    public static final int NOTICE_PP_TEXT = 129;

    /**
     * 群聊提示消息
     */
    public static final int NOTICE_PG_TEXT = 133;
}
