package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;

/**
 * <p>Summary:更改消息状态</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:11:27</p>
 */
public interface ChangeMsgState extends Interactor<Integer> {
    /**
     * 设置消息状态
     *
     * @param message 消息
     * @param state   消息状态
     * @return 用例对象
     */
    ChangeMsgState change(TalkMessageBean message, @ConstDef.MsgState int state);

}
