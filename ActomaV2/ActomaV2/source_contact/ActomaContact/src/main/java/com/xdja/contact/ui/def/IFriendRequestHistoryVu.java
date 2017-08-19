package com.xdja.contact.ui.def;

import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.presenter.adapter.FriendRequestHistoryAdapter;
import com.xdja.contact.presenter.command.IFriendRequestCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

import java.util.List;

/**
 * Created by wanghao on 2015/7/21.
 */
public interface IFriendRequestHistoryVu extends ActivityVu<IFriendRequestCommand> {

    void setAdapter(FriendRequestHistoryAdapter adapter);

    void setDataSource(List<FriendRequestHistory> dataSource);

    int historyListCount();



}
