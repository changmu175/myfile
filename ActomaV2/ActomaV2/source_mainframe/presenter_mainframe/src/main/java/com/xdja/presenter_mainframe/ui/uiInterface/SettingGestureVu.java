package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.SettingGestureCommand;
import com.xdja.presenter_mainframe.widget.LockPatternView;

/**
 * Created by licong on 2016/11/25.
 */
public interface SettingGestureVu  extends ActivityVu<SettingGestureCommand> {
    LockPatternView getLockPatternView();
    void setText();
}
