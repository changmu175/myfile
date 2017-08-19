package com.xdja.presenter_mainframe.global.obs;

import android.support.annotation.Nullable;

import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.domain.usecase.Interactor;

import rx.Subscriber;
import rx.subjects.PublishSubject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.observable</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:20:14</p>
 */
public abstract class ExtObservable<T, E> implements Observable<T> {

    private BusProvider busProvider;
    private PublishSubject<T> publishSubject;

    public ExtObservable(BusProvider busProvider) {
        this.busProvider = busProvider;
        this.publishSubject = PublishSubject.create();
        this.busProvider.register(this);
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        this.publishSubject.subscribe(subscriber);
    }

    @Override
    public void unSubscribe(Subscriber<T> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    @Override
    public void release() {
        this.busProvider.unregister(this);
    }

    public void onReceiveEvent(E event) {
        Interactor<T> tInteractor = generatePullAction();
        LogUtil.getUtils().d("onReceiveEvent ForceLogoutObservable.ForceLogoutEvent tInteractor = " + tInteractor);
        if (tInteractor == null) {
            publishSubject.onNext(null);
            return;
        }

        tInteractor.execute(
                new Subscriber<T>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.getUtils().d("tInteractor.execute : onCompleted");
                        publishSubject.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().d("tInteractor.execute : onError e : " + e);
                        publishSubject.onError(e);
                    }

                    @Override
                    public void onNext(T t) {
                        LogUtil.getUtils().d("tInteractor.execute : onNext t : " + t);
                        publishSubject.onNext(t);
                    }
                }
        );
    }

    @Nullable
    public abstract Interactor<T> generatePullAction();
}
