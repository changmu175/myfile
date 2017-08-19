package com.xdja.dependence.rx;

import android.support.annotation.Nullable;

import rx.functions.Func1;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
public abstract class ExtFunc1<T,R,P> implements Func1<T,R> {
    private P p;

    public ExtFunc1(@Nullable P param){
        this.p = param;
    }

    @Nullable
    public P getP() {
        return p;
    }

    public void setP(@Nullable P p) {
        this.p = p;
    }
}
