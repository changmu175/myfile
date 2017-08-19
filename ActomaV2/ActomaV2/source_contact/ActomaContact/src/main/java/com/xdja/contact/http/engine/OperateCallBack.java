package com.xdja.contact.http.engine;


import com.xdja.contact.http.wrap.IHttpParams;

/**
 * 批量循环业务执行回调
 * Created by hkb.
 * 2015/3/2.
 */
public abstract class OperateCallBack implements OnOperateListener {

    @Override
    public void onTaskStart(Object tag) {

    }

    /**
     * 业务执行结束
     * @param tag 业务标识
     */
    @Override
    public void onTaskEnd(Object tag) {

    }

    /**
     * 是否需要继续增加执行子业务,
     * 如果需要返回下次业务所需要请求参数,
     * 返回空则结束循环请求
     * @param lastSuccessData 上一次执行成功数据
     * @param position 当前执行索引
     * @return 下次执行网络参数
     */
    @Override
    public abstract IHttpParams isNeedNext(int position,String lastSuccessData);

    /**
     * 所有操作成功完成
     * @param result 业务处理结果
     */
    @Override
    public abstract void onTaskSuccess(Result result) ;

    /**
     * 操作失败,可能是子业务,或者是整个业务
     * @param result 业务处理结果
     */
    @Override
    public abstract void onTaskFailed(Result result);
}
