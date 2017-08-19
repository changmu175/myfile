package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;

/**
 * <p>Summary:恢复文件接收业务用例接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:18:38</p>
 */
public interface ResumeReceiveFile extends Interactor<Integer>  {
    /**
     * 恢复文件接收
     * @param fileInfo 待恢复的文件对象
     * @return  业务用例接口
     */
    ResumeReceiveFile resume(FileInfo fileInfo);
}
