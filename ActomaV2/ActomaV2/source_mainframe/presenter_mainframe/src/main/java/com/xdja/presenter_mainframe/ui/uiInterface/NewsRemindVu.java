package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.NewsRemindCommand;

/**
 * Created by chenbing on 2015/7/7.
 */
public interface NewsRemindVu extends ActivityVu<NewsRemindCommand> {
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
