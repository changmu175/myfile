package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.adapter.LocalSearchAdapter;
import com.xdja.contact.presenter.command.IFriendSearchCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by wanghao on 2015/7/23.
 */
public interface IFriendSearchVu extends ActivityVu<IFriendSearchCommand> {

    void setAdapter(LocalSearchAdapter adapter);

    void showNonDataView(boolean isShow);

    void dismissLoading();

    void showLoading();
}
