package com.securevoip.presenter.fragment;


import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterFragment;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

/**
 * <p>Summary:AT+业务相关的Fragment的Presenter</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.fragment</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/9</p>
 * <p>Time:19:18</p>
 */
public abstract class FragmentPresenter<T extends Command, V extends FragmentVu> extends BasePresenterFragment<T, V> {

}
