package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.bean.DeviceInfoBean;
import com.xdja.presenter_mainframe.cmd.DeviceManagerCommand;

import java.util.List;

/**
 * Created by ldy on 16/4/29.
 */
public interface VuDeviceManager extends ActivityVu<DeviceManagerCommand> {
    void setDeviceInfoList(List<DeviceInfoBean> deviceInfoList);
}
