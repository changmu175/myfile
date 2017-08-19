package com.xdja.presenter_mainframe.global.obs;

import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.xdja.dependence.event.BusProvider;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Interactor;

import java.util.Map;

import javax.inject.Inject;

/**
 * <p>Summary:强制下线事件的可观察者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.observable</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/27</p>
 * <p>Time:12:02</p>
 */
public class ForceLogoutObservable extends ExtObservable<Map<String, String>, ForceLogoutObservable.ForceLogoutEvent> {

    private Ext0Interactor<Map<String, String>> forceLogout;

    @Inject
    public ForceLogoutObservable(BusProvider busProvider,
                                 @InteractorSpe(DomainConfig.QUERY_FORCE_LOGOUT_NOTICE)
                                 Ext0Interactor<Map<String, String>> forceLogout) {
        super(busProvider);
        this.forceLogout = forceLogout;
    }

    @Override
    @Nullable
    public Interactor<Map<String, String>> generatePullAction() {
        return this.forceLogout.fill();
    }

    @Subscribe
    @Override
    public void onReceiveEvent(ForceLogoutEvent event) {
        super.onReceiveEvent(event);
    }


    @SuppressWarnings({"EmptyClass"})
    public static class ForceLogoutEvent {

    }
}
