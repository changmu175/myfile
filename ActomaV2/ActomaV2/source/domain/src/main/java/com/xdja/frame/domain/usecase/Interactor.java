package com.xdja.frame.domain.usecase;

import android.support.annotation.NonNull;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * <p>Summary:基本业务用例接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/11</p>
 * <p>Time:13:49</p>
 */
public interface Interactor<T> {

    void execute(@NonNull Subscriber<T> useCaseSubscriber);

    void execute(@NonNull Action1<T> action1);

    void unSubscribe();


}
