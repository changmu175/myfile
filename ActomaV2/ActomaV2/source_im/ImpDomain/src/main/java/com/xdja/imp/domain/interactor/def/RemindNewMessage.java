package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;

/**
 * <p>Summary:新消息提醒接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/19</p>
 * <p>Time:16:57</p>
 */
public interface RemindNewMessage {
    /**
     * 消息提醒
     * @param talkMessageBean 目标消息
     */
    void remindMessage(TalkListBean listBean, List<TalkMessageBean> talkMessageBean);

    /**
     * 清空所有消息提醒
     */
    void clearAllMessage();

}
