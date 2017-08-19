package com.xdja.imp.presenter.command;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by xdjaxa on 2016/6/16.
 */
public interface IPicturePreviewCommand extends Command {

    /**
     * 发送已经选择的图片
     */
    void sendPictureMessage();

    /**
     * 是否是从拍照界面跳转
     * */
    boolean isFromTakePhoto();
}
