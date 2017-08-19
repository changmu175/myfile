package com.xdja.imp.presenter.command;

import android.view.View;

import com.xdja.frame.presenter.mvp.Command;

/**
 * <p>Author: leiliangliang </p>
 * <p>Date: 2016/11/29 10:45</p>
 * <p>Package: com.xdja.imp.presenter.command</p>
 * <p>Description: </p>
 */
public interface IFileExplorerCommand extends Command {

    /**
     * 最近文件tab标签点击事件
     *
     * @param view
     */
    void onLastFileTabClick(View view);

    /**
     * 本地文件tab标签点击事件
     *
     * @param view
     */
    void onLocalFileTabClick(View view);

    /**
     * 发送按钮点击事件
     *
     * @param view
     */
    void onSendBtnClick(View view);

}
