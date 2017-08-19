package com.xdja.contact.ui.def;

import com.xdja.contact.presenter.activity.MultiDelPresenter;
import com.xdja.contact.presenter.adapter.GroupDelMulMemberAdapter;
import com.xdja.contact.presenter.adapter.SearchGroupMemberAdapter;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by xdjaxa on 2016/11/1.
 */
public interface IMultiDelVu extends ActivityVu<MultiDelPresenter> {
    void updateTitle(int selectCount);
    void setMultiDelAdapter(GroupDelMulMemberAdapter adapter);
    void setSearchGroupMemberAdapter(SearchGroupMemberAdapter adapter);
    void setClickState(boolean state);
    void setToolBarTitle(int count);
    void setSearchEtContent(String msg);
}
