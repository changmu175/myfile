package com.xdja.contact.ui.def;

import android.widget.Adapter;

import com.xdja.contact.presenter.adapter.ChooseFriendAdapter;
import com.xdja.contact.presenter.adapter.SearchFriendsAdapter;
import com.xdja.contact.presenter.fragment.ChooseFriendsPresenter;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

/**
 * Created by yangpeng on 2015/7/25.
 */
public interface IChooseFriendsVu extends FragmentVu<ChooseFriendsPresenter> {

    void setChooseFriendsAdapter(ChooseFriendAdapter chooseFriendsAdapter);

    void setSearchResultAdapter(SearchFriendsAdapter searchFriendsAdapter);

    Adapter getListAdapter();// modified by ycm 2016/12/22

    String getSearchText();

    void setListSelection(int position);

    void setSelectedNum(int num);
}
