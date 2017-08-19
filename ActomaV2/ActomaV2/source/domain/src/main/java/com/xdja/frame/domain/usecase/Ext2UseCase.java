package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 */
public abstract class Ext2UseCase<P,P1,T> extends UseCase<T> implements Ext2Interactor<P,P1,T> {
    @Nullable
    protected P p;
    @Nullable
    protected P1 p1;

    public Ext2UseCase(ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread){
        super(threadExecutor,postExecutionThread);
    }

    @Override
    public Ext2Interactor<P, P1, T> fill(@Nullable P p,@Nullable P1 p1) {
        this.p = p;
        this.p1 = p1;
        return this;
    }
}
