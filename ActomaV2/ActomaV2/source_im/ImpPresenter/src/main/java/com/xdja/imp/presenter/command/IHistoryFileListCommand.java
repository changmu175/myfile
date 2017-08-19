package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;

/**
 * 项目名称：Blade
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/8 17:06
 * 修改人：xdjaxa
 * 修改时间：2016/12/8 17:06
 * 修改备注：
 */
public interface IHistoryFileListCommand extends Command {

    /**
     * 下载文件
     */
    void downloadFiles();

    /**
     * 转发
     */
    void transmitFiles();

    /**
     * 删除
     */
    void removeFiles();

    void refreshUI(boolean show);

    int getDataSize();
}
