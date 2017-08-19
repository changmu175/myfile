package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.ChooseAccountCommand;

/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
public interface VuChooseAccount extends ActivityVu<ChooseAccountCommand> {
    void setAtAccount(String account);
    void removeSetAccountSelfText();
    void modifyCertainButton(String msg);
}
