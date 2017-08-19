package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * <p>Summary:增加自定义消息</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:13:37</p>
 */
public interface AddCustomMsg extends Interactor<TalkMessageBean> {
    /**
     * 添加自定义消息接口
     *
     * @param talkMessageBean 待添加的消息对象
     * @return 用力对象
     */
    AddCustomMsg add(TalkMessageBean talkMessageBean);
}
