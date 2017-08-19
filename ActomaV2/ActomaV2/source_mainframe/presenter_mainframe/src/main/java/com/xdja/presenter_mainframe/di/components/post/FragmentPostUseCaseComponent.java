package com.xdja.presenter_mainframe.di.components.post;

import com.xdja.dependence.annotations.PerFragment;
import com.xdja.frame.di.modules.FragmentModule;
import com.xdja.presenter_mainframe.di.modules.SubFragmentModule;
import com.xdja.presenter_mainframe.presenter.fragement.SettingFragementPresenter;

import dagger.Subcomponent;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@PerFragment
@Subcomponent(
        modules = {FragmentModule.class, SubFragmentModule.class}
)
public interface FragmentPostUseCaseComponent {
    void inject(SettingFragementPresenter settingFragementPresenter);
}
