package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.adapter.GroupListAdapter;
import com.xdja.contact.presenter.command.IGroupListCommand;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

/**
 * Created by XDJA_XA on 2015/7/17.
 */
public interface IGroupListVu extends FragmentVu<IGroupListCommand> {
    /**
     * 设置群组列表适配器
     *
     * @param adapter 群组列表适配器
     */
    void setAdapter(GroupListAdapter adapter);

    void stopRefresh();

    void setListEmpty();

    void setGroupCount(int count);
}
