package com.xdja.imp.frame.mvp.view;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.frame.presenter.mvp.view.SuperView;


/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.frame</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/9</p>
 * <p>Time:18:45</p>
 */
public class FragmentSuperView<T extends Command> extends SuperView<T> implements FragmentVu<T> {

    private Fragment fragment;

    @Override
    public <A extends Fragment> void setFragment(A fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public void onCreated() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void showCommonProgressDialog(String msg) {

    }

    @Override
    public void showCommonProgressDialog(@StringRes int resId) {

    }

    @Override
    public void dismissCommonProgressDialog() {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showToast(@StringRes int resId) {

    }
}
