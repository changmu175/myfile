package com.xdja.contact.ui.def;

import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.presenter.command.IContactFriendApplyCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

import java.util.List;

/**
 * Created by yangpeng on 2015/7/16.
 */
public interface IAcceptFriendApplyVu extends ActivityVu<IContactFriendApplyCommand> {

    void setFriendData(String account, List<AuthInfo> friendRequestInfos);

    void showDialog();

    void dismissDialog();

}
