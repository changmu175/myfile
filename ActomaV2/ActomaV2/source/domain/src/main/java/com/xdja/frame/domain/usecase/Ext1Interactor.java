package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 * 需要一个入参的对外业务接口定义
 */
public interface Ext1Interactor<P,T> extends Interactor<T> {

    Ext1Interactor<P,T> fill(@Nullable P p);
}


