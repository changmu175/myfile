package com.xdja.frame.domain.usecase;

import android.support.annotation.Nullable;

import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;

/**
 * Description:
 * Company    : 信大捷安
 * Author     : wxf@xdja.com
 * Date       : 2016/11/3 13:53
 */
public abstract class Ext6UseCase<P, P1, P2, P3, P4, P5, T> extends UseCase<T> implements Ext6Interactor<P, P1, P2, P3, P4, P5, T> {
    @Nullable
    protected P p;
    @Nullable
    protected P1 p1;
    @Nullable
    protected P2 p2;
    @Nullable
    protected P3 p3;
    @Nullable
    protected P4 p4;
    @Nullable
    protected P4 p5;


    public Ext6UseCase(ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    public Ext6Interactor<P, P1, P2, P3, P4, P5, T> fill(@Nullable P p, @Nullable P1 p1, @Nullable P2 p2, @Nullable P3 p3, @Nullable P4 p4,@Nullable P4 p5) {
        this.p = p;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        return this;
    }
}
