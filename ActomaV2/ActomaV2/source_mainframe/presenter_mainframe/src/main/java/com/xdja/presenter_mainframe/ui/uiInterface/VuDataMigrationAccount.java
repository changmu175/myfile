package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.DataMigrationAccountCommand;

/**
 * Created by ALH on 2016/8/30.
 */
public interface VuDataMigrationAccount extends ActivityVu<DataMigrationAccountCommand> {
    void setAccount(String account);
}
