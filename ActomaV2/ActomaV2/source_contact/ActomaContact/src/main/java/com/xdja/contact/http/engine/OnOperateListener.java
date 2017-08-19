package com.xdja.contact.http.engine;


import com.xdja.contact.http.wrap.IHttpParams;

/**
 * 业务执行监听类
 *
 * @作者 hkb
 * @创建时间 2014年12月23日
 */
public interface OnOperateListener {

	/**
	 * 业务开始执行  （在主线程内）  
	 * @since 2015年3月2日 下午3:25:02
	 * @作者 hkb
	 * @param tag  业务标识
	 */
    void onTaskStart(Object tag);

    /**
     * 业务执结束  （在主线程中）  
     * @since 2015年3月2日 下午3:25:02
     * @作者 hkb
     * @param tag 业务标识
     */
    void onTaskEnd(Object tag);


    /**
     * 是否需要继续增加执行子业务,
     * 如果需要返回下次业务所需要请求参数,
     * 返回空则结束循环请求
     * @param lastSuccessData 上一次执行成功数据
     * @param position 当前执行索引
     * @return 下次执行网络参数
     */
    IHttpParams isNeedNext(int position, String lastSuccessData);

    /**
     * 业务执行成功 （在主线程中）  
     * @since 2015年3月2日 下午3:25:02
     * @作者 hkb
     * @param result 业务处理结果
     */
    void onTaskSuccess(Result result);
    
    /**
     * 业务执行失败  （在主线程中）  
     * @since 2015年3月2日 下午3:25:02
     * @作者 hkb
     * @param result 业务处理结果
     */
    void onTaskFailed(Result result);
}
