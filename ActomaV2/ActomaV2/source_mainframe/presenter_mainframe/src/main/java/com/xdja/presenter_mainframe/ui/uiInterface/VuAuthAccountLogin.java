package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.AuthAccountLoginCommand;

/**
 * Created by ldy on 16/5/12.
 */
public interface VuAuthAccountLogin extends ActivityVu<AuthAccountLoginCommand> {
    void setAccountAndCardNo(String account,String cardNo);
}
