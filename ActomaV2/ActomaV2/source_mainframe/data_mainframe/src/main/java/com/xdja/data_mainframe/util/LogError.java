package com.xdja.data_mainframe.util;

import com.xdja.dependence.uitls.LogUtil;

import rx.Subscriber;

/**
 * Created by ldy on 16/5/19.
 * 用于处理只需要简单打出异常的订阅者
 */
public class LogError extends Subscriber<Object> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        LogUtil.getUtils().e(e);
    }
    @Override
    public void onNext(Object o) {

    }
}
