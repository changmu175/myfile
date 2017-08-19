package com.xdja.imp.domain.interactor.def;

/**
 * <p>Summary:清空未读消息业务接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:18:47</p>
 */
public interface ClearUnReadMsg extends Interactor<Integer> {
    /**
     * 清空未读消息
     *
     * @param talkId 待清空的会话ID
     * @return 用例接口
     */
    ClearUnReadMsg clear(String talkId);
//    /**
//     * 清空未读消息
//     *
//     * @param talkId 待清空的会话ID
//     * @return 用例接口
//     */
//    ClearUnReadMsg clear(int talkId);
}
