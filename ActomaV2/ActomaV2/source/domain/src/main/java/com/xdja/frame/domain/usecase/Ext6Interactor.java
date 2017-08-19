package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

/**
 * Description:
 * Company    : 信大捷安
 * Author     : wxf@xdja.com
 * Date       : 2016/11/3 13:49
 */
public interface Ext6Interactor <P, P1, P2, P3, P4, P5, T> extends Interactor<T> {
    Ext6Interactor<P, P1, P2, P3, P4, P5, T> fill(@Nullable P p, @Nullable P1 p1, @Nullable P2 p2, @Nullable P3 p3, @Nullable P4 p4, @Nullable P4 p5);
}
