package com.xdja.imp.domain.interactor.def;

import com.xdja.imp.domain.model.TalkListBean;

import java.util.List;

/**
 * <p>Summary:获取会话消息集合用例接口定义</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/20</p>
 * <p>Time:15:46</p>
 */
public interface GetSessionList extends Interactor<List<TalkListBean>> {
    /**
     * 设置开始位置
     *
     * @param begin 开始位置
     * @return 用例对象
     */
    GetSessionList setBegin(String begin);

    /**
     * 设置集合大小
     *
     * @param size 集合大小
     * @return 用例对象
     */
    GetSessionList setSize(int size);

    /**
     * 设置相关参数
     * @param begin 开始位置
     * @param size 集合大小
     * @return 用例对象
     */
    GetSessionList setParam(String begin,int size);
}
