package com.xdja.presenter_mainframe.di.components.post;

import com.xdja.dependence.annotations.PerLife;
import com.xdja.domain_mainframe.di.PostUseCaseModule;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.di.modules.ActivityModule;
import com.xdja.frame.di.modules.FragmentModule;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.di.modules.SubActivityModule;
import com.xdja.presenter_mainframe.di.modules.SubFragmentModule;

import java.util.Map;

import dagger.Subcomponent;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@PerLife
@Subcomponent(
        modules = {PostUseCaseModule.class, PreUseCaseModule.class}
)
public interface PostUseCaseComponent {
    ActivityPostUseCaseComponent plus(ActivityModule activityModule, SubActivityModule subActivityModule);
    FragmentPostUseCaseComponent plus(FragmentModule fragmentModule, SubFragmentModule subFragmentModule);

    @InteractorSpe(value = DomainConfig.LOGOUT)
    Ext0Interactor<Void> logout();

    @InteractorSpe(value = DomainConfig.REFRESH_TICKET)
    Ext1Interactor<String, Map<String,Object>>  refreshTicket();
}
