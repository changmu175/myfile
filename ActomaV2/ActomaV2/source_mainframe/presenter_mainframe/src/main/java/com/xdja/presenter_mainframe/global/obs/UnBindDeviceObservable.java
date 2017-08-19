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
 * <p>Summary:解绑设备事件的可观察者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:19:23</p>
 */
public class UnBindDeviceObservable extends ExtObservable<Map<String, String>, UnBindDeviceObservable.UnBindDeviceEvent> {


    Ext0Interactor<Map<String, String>> unBindDevice;

    @Inject
    public UnBindDeviceObservable(BusProvider busProvider,
                                  @InteractorSpe(DomainConfig.QUERY_UN_BIND_DEVICE_NOTICE)
                                  Ext0Interactor<Map<String, String>> unBindDevice) {
        super(busProvider);
        this.unBindDevice = unBindDevice;
    }

    @Override
    @Nullable
    public Interactor<Map<String, String>> generatePullAction() {
        return unBindDevice.fill();
    }

    @Subscribe
    @Override
    public void onReceiveEvent(UnBindDeviceEvent event) {
        super.onReceiveEvent(event);
    }

    @SuppressWarnings({"EmptyClass"})
    public static class UnBindDeviceEvent {

    }

    @SuppressWarnings({"EmptyClass"})
    public static class UnBindMobileEvent {

    }
}
