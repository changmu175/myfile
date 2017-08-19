package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by xdjaxa on 2016/6/16.
 */
public interface IPictureSelectCommand extends Command {

    /**
     * 发送已经选择的图片
     */
    void sendPictureMessage();


    /**
     * 预览按钮功能，预览已选图片
     */
    void startToPreviewPictures();

    int getSelectedCount();

}
