package com.xdja.frame.di.modules;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xdja.dependence.annotations.BundleSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.PerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
@Module
public class FragmentModule {

    private Fragment fragment;

    public FragmentModule(Fragment fragment){
        this.fragment = fragment;
    }

    @PerFragment
    @Provides
    Fragment provideFragment(){
        return this.fragment;
    }

    @PerFragment
    @Provides
    @BundleSpe(DiConfig.BUNDLE_FRAGMENT)
    Bundle provideBundle(){
        return this.fragment.getArguments();
    }

}
