package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.FileInfo;

/**
 * <p>Summary:发送文件接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:19:43</p>
 */
public interface SendFileMsg extends SendMessage {
    /**
     * 发送文件
     *
     * @return 用例接口
     */
    SendMessage send(String to, boolean isShan, boolean isGroup, FileInfo fileInfo);
}
