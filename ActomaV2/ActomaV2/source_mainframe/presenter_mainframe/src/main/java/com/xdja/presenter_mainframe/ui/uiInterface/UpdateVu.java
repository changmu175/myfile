package com.xdja.presenter_mainframe.ui.uiInterface;


import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by chenbing on 2015/8/5.
 */
public interface UpdateVu extends ActivityVu<Command> {
    /**
     * 展示等待框
     */
    void showProgress();

    /**
     * 隐藏等待框
     */
    void hideProgress();
}
