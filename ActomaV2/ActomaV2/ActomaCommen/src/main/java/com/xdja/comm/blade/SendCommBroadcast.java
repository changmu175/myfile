package com.xdja.comm.blade;

import android.app.Application;
import android.content.Intent;

import com.xdja.CommonApplication;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;

/**
 * Created by ldy on 16/5/30.
 * 发送广播以调用不能调用的部分（例如其余模块需要通知主框架但又没有依赖主框架时需要发送广播）
 */
public class SendCommBroadcast {
    /**
     * 发送程序退出广播
     */
    public static void sendApplicationExitBroadcast(){
        Application application = CommonApplication.getApplication();
        application.sendBroadcast(new Intent(BasePresenterActivity.ACTION_APPLICATION_EXIT));
    }
}
