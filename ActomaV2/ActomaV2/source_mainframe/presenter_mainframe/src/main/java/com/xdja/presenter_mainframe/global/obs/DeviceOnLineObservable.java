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
 * <p>Summary:设备上线事件的可观察者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.observable</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/27</p>
 * <p>Time:14:53</p>
 */
public class DeviceOnLineObservable extends ExtObservable<Map<String, String>, DeviceOnLineObservable.DeviceOnLineEvent> {

    private Ext0Interactor<Map<String, String>> queryOnlineNotice;

    @Inject
    public DeviceOnLineObservable(BusProvider busProvider,
                                  @InteractorSpe(DomainConfig.QUERY_ONLINE_NOTICE)
                                  Ext0Interactor<Map<String, String>> queryOnlineNotice) {
        super(busProvider);
        this.queryOnlineNotice = queryOnlineNotice;
    }

    @Override
    @Nullable
    public Interactor<Map<String, String>> generatePullAction() {
        return this.queryOnlineNotice.fill();
    }


    @Subscribe
    @Override
    public void onReceiveEvent(DeviceOnLineEvent event) {
        super.onReceiveEvent(event);
    }

    @SuppressWarnings({"EmptyClass"})
    public static class DeviceOnLineEvent {

    }
}
