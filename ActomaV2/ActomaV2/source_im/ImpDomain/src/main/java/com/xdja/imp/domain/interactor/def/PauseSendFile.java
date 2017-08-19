package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;

/**
 * <p>Summary:暂停文件上传业务接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:18:32</p>
 */
public interface PauseSendFile extends Interactor<Integer> {
    /**
     * 暂停发送文件
     * @param fileInfo 待暂停的文件对象
     * @return  业务用例
     */
    PauseSendFile pause(FileInfo fileInfo);
}
