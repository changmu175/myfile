package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.adapter.LocalSearchMoreAdapter;
import com.xdja.contact.presenter.command.IFriendSearchMoreCommand;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by wanghao on 2015/10/23.
 */
public interface IFriendSearchMoreVu extends ActivityVu<IFriendSearchMoreCommand> {

    void setAdapter(LocalSearchMoreAdapter adapter);

    void setKeyWord(String keyWord);

    String getKeyword();

}
