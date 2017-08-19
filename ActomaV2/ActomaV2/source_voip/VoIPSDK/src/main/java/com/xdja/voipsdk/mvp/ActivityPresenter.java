package com.xdja.voipsdk.mvp;


import android.os.Bundle;

import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * Created by guoyaxin on 2016/1/7.
 */
public abstract class ActivityPresenter<P extends Command,V extends ActivityVu> extends BasePresenterActivity<P,V> {

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        /*[s]modify by tangsha@20161206 for multi language*/
        UniversalUtil.changeLanguageConfig(this);
        /*[E]modify by tangsha@20161206 for multi language*/
    }
}
