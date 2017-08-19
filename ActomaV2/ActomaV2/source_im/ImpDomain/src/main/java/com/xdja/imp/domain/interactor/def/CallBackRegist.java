package com.xdja.imp.domain.interactor.def;

/**
 * <p>Summary:回调句柄注册</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.def</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/12</p>
 * <p>Time:11:34</p>
 */
public interface CallBackRegist extends Interactor<Integer> {
    /**
     * 注册消息回调
     *
     * @return 用例接口
     */
    CallBackRegist registMessageCallBack();

    /**
     * 注册会话回调
     *
     * @return 用例接口
     */
    CallBackRegist registSessionCallBack();

    /**
     * 注册文件回调
     *
     * @return 用例接口
     */
    CallBackRegist registFileCallBack();

    /**
     * 注册所有回调
     *
     * @return 用例接口
     */
    CallBackRegist registAllCallBack();
}
