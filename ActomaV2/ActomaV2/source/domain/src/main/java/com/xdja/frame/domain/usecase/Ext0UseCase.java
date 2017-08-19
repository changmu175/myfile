package com.xdja.frame.domain.usecase;

import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 */
public abstract class Ext0UseCase<T> extends UseCase<T> implements Ext0Interactor<T> {

    public Ext0UseCase(ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread){
        super(threadExecutor,postExecutionThread);
    }

    @Override
    public Ext0Interactor<T> fill() {
        return this;
    }
}
