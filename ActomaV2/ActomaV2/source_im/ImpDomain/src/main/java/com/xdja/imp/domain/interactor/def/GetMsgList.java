package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkMessageBean;

import java.util.List;

/**
 * <p>Summary:根据会话ID获取指定数量的消息集合的接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:13:53</p>
 */
public interface GetMsgList extends Interactor<List<TalkMessageBean>> {
    /**
     * 设置参数
     * @param talkId 会话ID
     * @param begin 开始位置
     * @param size  集合大小
     * @return  用例对象
     */
    GetMsgList get(String talkId, long begin, int size);
}
