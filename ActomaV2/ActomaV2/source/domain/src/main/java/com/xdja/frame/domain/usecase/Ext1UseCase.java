package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 */
public abstract class Ext1UseCase<P,T> extends UseCase<T> implements Ext1Interactor<P,T> {
    @Nullable
    protected P p;

    public Ext1UseCase(ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread){
        super(threadExecutor,postExecutionThread);
    }

    @Override
    public Ext1Interactor<P, T> fill(@Nullable P p) {
        this.p = p;
        return this;
    }
}
