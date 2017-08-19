package com.xdja.imp.data.excutor;

import com.xdja.imp.domain.excutor.PostExecutionThread;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * <p>Summary:android程序的UI线程</p>
 * <p>Description:</p>
 * <p>Package:com.hysel.picker.excutor</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/6/5</p>
 * <p>Time:15:11</p>
 */
public class UIThreadImp implements PostExecutionThread {

    @Inject
    public UIThreadImp(){}

    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
