package com.xdja.contact.ui.def;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.presenter.command.IAnTongComeInCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by yangpeng on 2015/8/10.
 */
public interface IAnTongComeInVu extends ActivityVu<IAnTongComeInCommand> {

    void setAnTongFtiendData(Friend ftiend);
}
