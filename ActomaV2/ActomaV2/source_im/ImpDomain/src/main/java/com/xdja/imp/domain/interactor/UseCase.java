package com.xdja.imp.domain.interactor;


import android.support.annotation.NonNull;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * <p>Summary:业务用例基类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/30</p>
 * <p>Time:16:20</p>
 */
public abstract class UseCase<T> implements Interactor<T>{
    private ThreadExecutor executor;

    private PostExecutionThread mainThread;

    private Subscription subscription = Subscriptions.empty();

    protected UseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread){
        this.executor = threadExecutor;
        this.mainThread = postExecutionThread;
    }

    /**
     * 构建业务处理事件流
     * @return 目标事件流
     */
    public abstract Observable<T> buildUseCaseObservable();

    /**
     * 业务执行入口
     *
     * @param useCaseSubscriber 监听处理类
     */
    @Override
    public void execute(@NonNull Subscriber<T> useCaseSubscriber) {
        //获取业务处理事件流
        this.subscription = this.buildUseCaseObservable()
                //异步执行业务
                .subscribeOn(Schedulers.from(executor))
                //在主线程监听
                .observeOn(mainThread.getScheduler())
                //订阅事件流
                .subscribe(useCaseSubscriber);
    }

    /**
     * 业务执行入口
     * @param action1 监听处理类（只监听onNext方法的返回值）
     */
    @Override
    public void execute(@NonNull Action1<T> action1){
        this.subscription = this.buildUseCaseObservable()
                //异步执行业务
                .subscribeOn(Schedulers.from(executor))
                //在主线程监听
                .observeOn(mainThread.getScheduler())
                //订阅事件流
                .subscribe(action1);
    }

    /**
     * 取消业务处理监听
     */
    @Override
    public void unSubscribe() {
        if (!this.subscription.isUnsubscribed())
            this.subscription.unsubscribe();
    }
}
