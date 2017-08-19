package com.xdja.presenter_mainframe.di.components.pre;

import com.xdja.dependence.annotations.PerFragment;
import com.xdja.frame.di.modules.FragmentModule;
import com.xdja.presenter_mainframe.di.modules.SubFragmentModule;

import dagger.Subcomponent;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@PerFragment
@Subcomponent(
        modules = {FragmentModule.class, SubFragmentModule.class}
)
public interface FragmentPreUseCaseComponent {
//    void inject(Test1Presenter test1Presenter);
//    void inject(Test2Presenter test2Presenter);
}
