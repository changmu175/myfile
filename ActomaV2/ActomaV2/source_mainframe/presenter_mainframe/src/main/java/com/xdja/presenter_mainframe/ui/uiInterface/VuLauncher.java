package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.LauncherCommand;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VuLauncher extends ActivityVu<LauncherCommand> {

    /**
     * 设置导航按钮显示与否
     *
     * @param visibilety 显示与否
     */
    void setNavigateBtnVisibilety(int visibilety ,int type);

    void setAgainBtnVisible(boolean isVisible);
    //[S]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
    void showDownloadDialog();

    void hideDownloadDialog();

    void showCkmsInstallFailToast();
    //[E]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
}
