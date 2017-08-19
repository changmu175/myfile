package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.VerifyLoginCommand;

/**
 * Created by ldy on 16/4/15.
 */
public interface VuVerifyLogin extends ActivityVu<VerifyLoginCommand> {
    void isHaveMobile(boolean isHaveMobile);
}
