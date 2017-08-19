package com.xdja.contact.ui.def;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.presenter.adapter.FriendListAdapter;
import com.xdja.contact.presenter.command.IFriendListCommand;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

import java.util.List;

/**
 * Created by wanghao on 2015/7/20.
 */
public interface IFriendListVu extends FragmentVu<IFriendListCommand> {

    void setAdapter(FriendListAdapter adapter);

    void setDataSource(List<Friend> dataSource, int friendSize);

    void stopRefush();

    int friendListCount();
}
