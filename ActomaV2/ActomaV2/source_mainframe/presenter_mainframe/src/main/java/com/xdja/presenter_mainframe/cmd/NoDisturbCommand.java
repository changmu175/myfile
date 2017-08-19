package com.xdja.presenter_mainframe.cmd;

import com.xdja.domain_mainframe.usecase.settings.GetNoDistrubSettingUseCase;
import com.xdja.frame.presenter.mvp.Command;


public interface NoDisturbCommand extends Command {
    /**
     * 设置勿扰模式是否开启
     * @param isOpen
     */
    void setNoDistrubOpen(boolean isOpen);

    /**
     * 设置勿扰模式开始时间
     * @param hour 开始时间小时
     * @param minu 开始时间分钟
     */
    void setNoDistrubBeginTime(int hour, int minu);

    /**
     * 设置勿扰模式结束时间
     * @param hour 结束时间小时
     * @param minu 结束时间分钟
     */
    void setNoDistrubEndTime(int hour, int minu);

    /**
     * 获取勿扰模式相关数据
     */
    GetNoDistrubSettingUseCase.NoDistrubBean getNodistrubBean();

    /**
     * 设置完勿扰模式执行保存
     */
    void saveNoDisturbSettng();
}
