package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 */
public abstract class Ext4UseCase<P,P1,P2,P3,T> extends UseCase<T> implements Ext4Interactor<P,P1,P2,P3,T> {
    @Nullable
    protected P p;
    @Nullable
    protected P1 p1;
    @Nullable
    protected P2 p2;
    @Nullable
    protected P3 p3;

    public Ext4UseCase(ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread){
        super(threadExecutor,postExecutionThread);
    }

    @Override
    public Ext4Interactor<P, P1,P2,P3,T> fill(@Nullable P p,@Nullable P1 p1,@Nullable P2 p2,@Nullable P3 p3) {
        this.p = p;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        return this;
    }
}
