package com.xdja.imp.domain.interactor.def;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/29</p>
 * <p>Time:20:26</p>
 */
public interface ClearAllMsgInTalk extends Interactor<Integer> {
    /**
     * 清空会话中的消息
     *
     * @param talkId 会话ID
     * @return 业务接口
     */
    ClearAllMsgInTalk clear(String talkId);
}
