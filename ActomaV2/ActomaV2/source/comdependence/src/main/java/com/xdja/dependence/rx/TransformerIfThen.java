package com.xdja.dependence.rx;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.usecase</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/12</p>
 * <p>Time:11:47</p>
 */
public class TransformerIfThen<T, R> implements Observable.Transformer<T, R> {

    private Func1<T, Boolean> condition;

    private Func1<T, Observable<R>> then, orElse;

    public TransformerIfThen(Func1<T, Boolean> condition, Func1<T, Observable<R>> then, Func1<T, Observable<R>> orElse) {
        this.condition = condition;
        this.then = then;
        this.orElse = orElse;
    }

    @Override
    public Observable<R> call(Observable<T> tObservable) {
        return tObservable.flatMap(
                new Func1<T, Observable<R>>() {
                    @Override
                    public Observable<R> call(T t) {
                        return condition.call(t) ?
                                then.call(t) :
                                orElse.call(t);
                    }
                }
        );
    }
}