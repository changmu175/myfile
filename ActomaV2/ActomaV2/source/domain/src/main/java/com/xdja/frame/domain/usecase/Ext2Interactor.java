package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 * 需要两个入参的对外业务接口定义
 */
public interface Ext2Interactor<P,P1,T> extends Interactor<T> {

    Ext2Interactor<P,P1,T> fill(@Nullable P p,@Nullable P1 p1);
}


