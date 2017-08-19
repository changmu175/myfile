package com.xdja.imp.di.module;

import com.xdja.imp.data.excutor.ThreadExecutorImp;
import com.xdja.imp.data.excutor.UIThreadImp;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/7</p>
 * <p>Time:15:33</p>
 */
@Module
public class ExecutorModule {

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(ThreadExecutorImp imp) {
        return imp;
    }

    @Singleton
    @Provides
    PostExecutionThread providePostExecutionThread(UIThreadImp imp) {
        return imp;
    }
}
