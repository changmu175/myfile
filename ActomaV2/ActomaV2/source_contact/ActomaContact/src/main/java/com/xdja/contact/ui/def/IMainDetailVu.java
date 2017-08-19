package com.xdja.contact.ui.def;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.presenter.command.IMainDetailCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by yangpeng on 2015/7/18.
 */
public interface IMainDetailVu extends ActivityVu<IMainDetailCommand> {
    /**
     * 获得传过来的friend对象
     * @return
     */
    void setData(Member memberData, Friend friend, ResponseActomaAccount actomaAccount);

    void loadingDialogControler(boolean open, String msg);

    void showProgressDialog(String msg);

    void dissmissProgressDialog();
}
