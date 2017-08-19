package com.xdja.presenter_mainframe.di.modules;

import android.support.v4.app.Fragment;

import dagger.Module;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
@Module
public class SubFragmentModule {

    private Fragment fragment;

    public SubFragmentModule(Fragment fragment){
        this.fragment = fragment;
    }
}
