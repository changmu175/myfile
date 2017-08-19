package com.xdja.frame.presenter.mvp.view;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.Vu;


/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.frame</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/9</p>
 * <p>Time:18:43</p>
 */
public interface FragmentVu<P extends Command> extends Vu<P> {
    /**
     * 设置和view绑定的Fragment
     *
     * @param fragment 目标Fragment
     */
    <A extends Fragment> void setFragment(A fragment);

    void onCreated();

    void onPause();

    void onResume();

    void onStart();

    void onStop();

    void onDestroyView();

    boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater);

    boolean onOptionsItemSelected(MenuItem item);

    void showCommonProgressDialog(String msg);

    void showCommonProgressDialog(@StringRes int resId);

    void dismissCommonProgressDialog();

    void showToast(String msg);

    void showToast(@StringRes int resId);
}
