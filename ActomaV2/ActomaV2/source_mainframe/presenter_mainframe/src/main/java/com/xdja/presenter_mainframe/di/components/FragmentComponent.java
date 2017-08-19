package com.xdja.presenter_mainframe.di.components;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xdja.dependence.annotations.BundleSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.PerFragment;
import com.xdja.frame.di.modules.FragmentModule;
import com.xdja.presenter_mainframe.di.modules.SubFragmentModule;

import dagger.Component;

/**
 * Created by xdja-fanjiandong on 2016/3/22.
 */
@PerFragment
@Component(
        modules = {FragmentModule.class, SubFragmentModule.class}
)
public interface FragmentComponent {
    Fragment fragment();

    @BundleSpe(DiConfig.BUNDLE_FRAGMENT)
    Bundle bundle();
}
