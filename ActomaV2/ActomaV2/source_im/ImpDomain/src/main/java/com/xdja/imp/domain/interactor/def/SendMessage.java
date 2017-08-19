package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * <p>Summary:发送消息用例接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:19:20</p>
 */
public interface SendMessage extends Interactor<TalkMessageBean> {
    /**
     * 发送消息
     *
     * @param talkMessageBean      待发送消息对象
     * @return 用例接口
     */
    SendMessage send(TalkMessageBean talkMessageBean);
}
