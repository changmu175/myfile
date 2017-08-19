package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.presenter_mainframe.cmd.EmpowerDeviceLoginCommand;

/**
 * Created by ldy on 16/4/15.
 */
public interface VuEmpowerDeviceLogin extends VuLoginResult<EmpowerDeviceLoginCommand> {
    void setAuthorizeId(String authorizeId);
}
