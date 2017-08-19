package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.presenter_mainframe.bean.DeviceInfoBean;

/**
 * Created by ldy on 16/4/29.
 */
public interface DeviceManagerCommand extends Command {
    void authDeviceLogin();
    void deleteDevice(DeviceInfoBean deviceInfoBean);
    void modifyDeviceName(DeviceInfoBean deviceInfoBean,String newName);
}
