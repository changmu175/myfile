package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkListBean;

/**
 * <p>Summary:增加自定义会话的接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:11:14</p>
 */
public interface AddCustomTalk extends Interactor<TalkListBean> {
    AddCustomTalk add(TalkListBean talkListBean);
}
