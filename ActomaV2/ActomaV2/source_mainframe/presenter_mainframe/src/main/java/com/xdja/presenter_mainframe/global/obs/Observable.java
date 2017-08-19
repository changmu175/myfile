package com.xdja.presenter_mainframe.global.obs;

import rx.Subscriber;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.observable</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/26</p>
 * <p>Time:20:12</p>
 */
public interface Observable<T> {

    void subscribe(Subscriber<T> subscriber);

    void unSubscribe(Subscriber<T> subscriber);

    void release();

}
