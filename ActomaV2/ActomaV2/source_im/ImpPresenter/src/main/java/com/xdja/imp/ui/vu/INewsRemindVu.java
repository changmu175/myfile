package com.xdja.imp.ui.vu;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.INewsRemindCommand;

/**
 * Created by wanghao on 2015/12/7.
 */
public interface INewsRemindVu extends ActivityVu<INewsRemindCommand> {

    /**
     * 设置新消息通知是否开启
     *
     * @param isOn 是否开启
     */
    void setNewsRemind(boolean isOn);

    /**
     * 设置新消息通知-声音是否开启
     *
     * @param isOn 是否开启
     */
    void setNewsRemindByRing(boolean isOn);

    /**
     * 设置新消息通知-振动是否开启
     *
     * @param isOn 是否开启
     */
    void setNewsRemindByShake(boolean isOn);

}
