package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;

/**
 * <p>Summary:暂停文件接收业务接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:18:37</p>
 */
public interface PauseReceiveFile extends Interactor<Integer> {
    /**
     * 暂停文件接收
     *
     * @param fileInfo 待暂停的文件对象
     * @return 业务接口
     */
    PauseReceiveFile pause(FileInfo fileInfo);
}
