package com.xdja.presenter_mainframe.ui.uiInterface;


import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.NoDisturbCommand;

/**
 * Created by chenbing on 2015/7/7.
 */
public interface NoDisturbVu extends ActivityVu<NoDisturbCommand> {
    /**
     * 设置勿扰模式是否开启
     *
     * @param isOn 是否开启
     */
    void setNoDistrub(boolean isOn);

    /**
     * 设置勿扰模式开始时间
     *
     * @param beginTime 勿扰模式开始时间
     */
    void setBeginTime(String beginTime);

    /**
     * 设置勿扰模式结束时间
     *
     * @param endTime 勿扰模式结束时间
     */
    void setEndTime(String endTime);
}
