package com.xdja.contact.presenter.activity;


import android.os.Bundle;

import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;
import com.xdja.frame.presenter.mvp.view.ActivityVu;

/**
 * <p>Summary:AT+业务相关的Activity的Presenter</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:10:30</p>
 */
public abstract class ActivityPresenter<P extends Command, V extends ActivityVu> extends BasePresenterActivity<P,V> {
    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        /*[s]modify by tangsha@20161206 for multi language*/
        UniversalUtil.changeLanguageConfig(this);
        /*[E]modify by tangsha@20161206 for multi language*/
    }
}
