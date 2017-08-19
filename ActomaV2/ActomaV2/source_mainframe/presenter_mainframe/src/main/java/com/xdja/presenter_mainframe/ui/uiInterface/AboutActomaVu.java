package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.AboutActomaCommand;


/**
 * Created by geyao on 2015/7/7.
 */
public interface AboutActomaVu extends ActivityVu<AboutActomaCommand> {
    /**
     * 隐藏版本更新new字样提示
     */
    void hideUpdatePrompt();

    /**
     * 显示版本更新new字样提示
     */
    void showUpdatePrompt();

    /**
     * 设置安通版本号
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * 根据是否有更新刷新界面new展示
     * @param isHaveNew
     */
    void freshUpdateNew(boolean isHaveNew);

    /**
     * 隐藏“帮助与反馈”菜单
     */
    void updateHelpFeedBack(boolean isHaveHelpFeedBack);
}
