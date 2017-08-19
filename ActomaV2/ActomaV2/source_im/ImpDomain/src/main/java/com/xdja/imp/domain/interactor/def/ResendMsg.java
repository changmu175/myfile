package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * <p>Summary:重发消息接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:11:33</p>
 */
public interface ResendMsg extends Interactor<Integer> {
    /**
     * 是指重发的消息对象
     * @param talkMessageBean 消息对象
     * @return 用例对象
     */
    ResendMsg setChatMsg(TalkMessageBean talkMessageBean);
}
