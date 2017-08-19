package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;

/**
 * <p>Summary:恢复文件发送业务接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:15:09</p>
 */
public interface ResumeSendFile extends Interactor<Integer> {
    /**
     * 设置待恢复传输的消息
     *
     * @param fileInfo 待恢复传输的文件对象
     */
    ResumeSendFile resume(FileInfo fileInfo);

}
