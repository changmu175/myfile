package com.xdja.imp.domain.interactor.def;

import java.util.List;

/**
 * <p>Summary:删除消息业务接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/23</p>
 * <p>Time:13:47</p>
 */
public interface DeleteMsg extends Interactor<Integer> {
    /**
     * 删除消息
     * @param ids 待删除的消息
     * @return 用例对象
     */
    DeleteMsg delete(List<Long> ids);

}
