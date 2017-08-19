package com.xdja.contact.ui.def;


import com.xdja.contact.presenter.activity.ChooseContactPresenter;
import com.xdja.contact.presenter.adapter.CustomViewPagerAdapter;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * @author hkb.
 * @since 2015/7/22/0022.
 */
public interface IChooseContactVu extends ActivityVu<ChooseContactPresenter> {

    void setViewPageAdapter(CustomViewPagerAdapter viewPageAdapter);

    void showCommonProgressDialog(String msg);

    void dismissCommonProgressDialog();

    void setClickEnable(boolean flag);

    void setSearchHeaderVisibility(int isVisible);

    //task 2632
    void updateConfirmAndTitle(int selectCount);
}
