package com.xdja.imp.domain.interactor.def;

import android.app.Activity;

/**
 * <p>Summary:发送自定义文本消息用例接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/21</p>
 * <p>Time:12:00</p>
 */
public interface SendCustomTextMsg extends SendMessage {
    /**
     * 设置消息收发双发数据
     *
     * @param to      接收方
     * @param content 消息内容
     * @param isGroup 是否为群组
     * @return 业务用例
     */
    SendMessage send(Activity context, String to, String content, boolean isGroup);
}
