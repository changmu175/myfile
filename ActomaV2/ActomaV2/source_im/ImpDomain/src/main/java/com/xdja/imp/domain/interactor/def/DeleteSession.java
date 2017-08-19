package com.xdja.imp.domain.interactor.def;

import java.util.List;

/**
 * <p>Summary:删除会话</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:10:49</p>
 */
public interface DeleteSession extends Interactor<Integer> {
    /**
     * 指定要删除的会话的ID
     * @param ids 待删除的会话ID
     */
    DeleteSession delete(List<String> ids);

    /**
     * 删除指定类型的会话
     * @param talkerId 会话对象
     * @param type 会话类型，1 单聊，2 群聊
     * @return
     */
    DeleteSession delete(String talkerId, int type);
}
