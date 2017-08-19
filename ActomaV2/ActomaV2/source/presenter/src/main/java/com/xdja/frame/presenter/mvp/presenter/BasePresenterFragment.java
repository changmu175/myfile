package com.xdja.frame.presenter.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.frame.presenter.R;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.frame.widget.XDialog;


/**
 * <p>Summary:通用Fragment相关的Presenter</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:10:30</p>
 */
public abstract class BasePresenterFragment<P extends Command, V extends FragmentVu> extends Fragment {


    private V vu;

    public V getVu() {
        return vu;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            preBindView(savedInstanceState);

            if (getVuClass() != null) {
                //初始化View
                vu = getVuClass().newInstance();
                //设置view对业务的调用句柄
                vu.setCommand(getCommand());
                vu.setFragment(this);
                //设置和View关联的Activity
                vu.setActivity(getActivity());
                vu.init(inflater, null);
                vu.onCreated();
                return vu.getView();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onBindView(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vu != null) vu.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (vu != null) vu.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (vu != null) vu.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (vu != null) vu.onStop();
    }


    @Override
    public void onDestroyView() {
        if (vu != null) vu.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (vu != null) vu.onCreateOptionsMenu(menu, inflater);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (vu != null) vu.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    protected abstract Class<? extends V> getVuClass();

    @NonNull
    protected abstract P getCommand();

    protected void preBindView(Bundle savedInstanceState) {

    }

    protected void onBindView(Bundle savedInstanceState) {

    }




}
