package com.xdja.contact.ui.def;

import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.presenter.command.IAccountDetailCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by wanghao on 2015/8/15.
 */
public interface IAccountDetailVu extends ActivityVu<IAccountDetailCommand> {

    void setActomAccount(ResponseActomaAccount actomaAccount);

    void loadingDialogContorler(boolean open);


}
