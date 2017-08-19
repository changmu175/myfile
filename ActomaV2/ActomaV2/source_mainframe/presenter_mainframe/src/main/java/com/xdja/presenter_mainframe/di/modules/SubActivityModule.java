package com.xdja.presenter_mainframe.di.modules;

import android.app.Activity;

import dagger.Module;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
@Module
public class SubActivityModule {

    private Activity activity;

    public SubActivityModule(Activity activity){
        this.activity = activity;
    }
}
