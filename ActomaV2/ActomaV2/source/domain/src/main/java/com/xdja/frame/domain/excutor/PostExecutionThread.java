package com.xdja.frame.domain.excutor;

import android.support.annotation.NonNull;

import rx.Scheduler;

/**
 * <p>Summary:主线程接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.hysel.picker.excutor</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/6/5</p>
 * <p>Time:15:09</p>
 */
public interface PostExecutionThread {
    @NonNull
    Scheduler getScheduler();
}
