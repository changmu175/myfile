package com.xdja.contact.callback;

/**
 * 批量网络操作界面业务回调
 * @author hkb.
 * @since 2015/7/30/0030.
 */
public abstract class OnBatchTaskListener<Success,Fail> {

    public void onBatchTaskStart(){

    }

    public void onNext(long total,int progress){

    }

    public void onBatchTaskEnd(){}

    public abstract void onBatchTaskSuccess(Success result);

    public abstract void onBatchTaskFailed(Fail result);
}
