package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.command.IFriendRequestInfoCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by wanghao on 2015/8/3.
 */
public interface IFriendRequestInfoVu extends ActivityVu<IFriendRequestInfoCommand> {

    void dismissLoading();

    String getVerificationInfo();
}
