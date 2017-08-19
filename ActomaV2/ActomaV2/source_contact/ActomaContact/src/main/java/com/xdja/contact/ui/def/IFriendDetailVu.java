package com.xdja.contact.ui.def;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.presenter.command.IFriendDetailCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by yangpeng on 2015/7/16.
 */
public interface IFriendDetailVu extends ActivityVu<IFriendDetailCommand> {

    /**
     * 获得传过来的friend对象
     * @return
     */
    void setFriendData(Friend friend);

    void showProgressDialog(String msg);

    void dissmissProgressDialog();

}
