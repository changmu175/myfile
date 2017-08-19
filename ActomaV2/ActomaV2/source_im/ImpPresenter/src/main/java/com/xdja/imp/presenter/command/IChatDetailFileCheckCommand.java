package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.FileInfo;

/**
 * Created by guorong on 2016/11/30.
 */
public interface IChatDetailFileCheckCommand extends Command {
    //下载文件
    void startDownloadFile();

    //暂停下载
    void pauseDownloadFile();

    //恢复下载
    void resumeDownloadFile();

    void downLoadFile(boolean flag);

    FileInfo getFileInfo();

    void openFile();

    int getIcon();

    String getToolbarTitle();

    void forward();

    void back();
}
