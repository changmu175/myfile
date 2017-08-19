package com.xdja.imp.domain.interactor.def;

/**
 * <p>Summary:获取指定会话的未读消息数量</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:11:02</p>
 */
public interface GetMissedCount extends Interactor<Integer> {
    /**
     * 置顶会话ID
     * @param id 会话ID
     * @return 用例对象
     */
    GetMissedCount get(String id);
}
