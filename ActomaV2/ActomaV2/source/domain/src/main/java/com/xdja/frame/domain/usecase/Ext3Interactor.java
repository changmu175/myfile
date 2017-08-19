package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 * 需要三个入参的对外业务接口定义
 */
public interface Ext3Interactor<P,P1,P2,T> extends Interactor<T> {

    Ext3Interactor<P,P1,P2,T> fill(@Nullable P p,@Nullable P1 p1,@Nullable P2 p2);
}


