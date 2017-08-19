package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.command.IFriendRemarkCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by wanghao on 2015/7/23.
 */
public interface IFriendRemarkVu extends ActivityVu<IFriendRemarkCommand> {

    void dismissLoading();

    String getRemark();
}


