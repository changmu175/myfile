package com.xdja.presenter_mainframe.global.obs;

import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.xdja.dependence.event.BusProvider;
import com.xdja.frame.domain.usecase.Interactor;

import javax.inject.Inject;

/**
 * <p>Summary:设备被绑定事件的可观察者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.observable</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/27</p>
 * <p>Time:17:22</p>
 */
public class BindDeviceObservable extends ExtObservable<Void, BindDeviceObservable.BindDeviceEvent> {

    @Inject
    public BindDeviceObservable(BusProvider busProvider) {
        super(busProvider);
    }

    @Nullable
    @Override
    public Interactor<Void> generatePullAction() {
        return null;
    }

    @Subscribe
    @Override
    public void onReceiveEvent(BindDeviceEvent event) {
        super.onReceiveEvent(event);
    }

    @SuppressWarnings({"EmptyClass"})
    public static class BindDeviceEvent {

    }
}
