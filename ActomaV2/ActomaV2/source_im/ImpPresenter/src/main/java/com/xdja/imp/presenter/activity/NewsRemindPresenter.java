package com.xdja.imp.presenter.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.presenter.command.INewsRemindCommand;
import com.xdja.imp.ui.NewsRemindVu;
import com.xdja.imp.ui.vu.INewsRemindVu;

import rx.functions.Action1;

/**
 * Created by wanghao on 2015/12/7.
 */
public class NewsRemindPresenter extends IMActivityPresenter<INewsRemindCommand,INewsRemindVu> implements INewsRemindCommand{

    /**
     * 新消息通知
     */
    private static boolean newsRemindisOn = true;
    /**
     * 新消息通知-声音
     */
    private static boolean newsRemindByRingisOn = true;
    /**
     * 新消息通知-振动
     */
    private static boolean newsRemindByShakeisOn = true;
    /*============================用于操作界面UI(CheckBox状态)===========================*/

    @NonNull
    @Override
    protected Class<? extends INewsRemindVu> getVuClass() {
        return NewsRemindVu.class;
    }

    @NonNull
    @Override
    protected INewsRemindCommand getCommand() {
        return this;
    }

    @Override
    public void newsRemind(boolean isOn) {
        newsRemindisOn = isOn;
        SettingServer.newsRemind = String.valueOf(isOn);
    }

    @Override
    public void newsRemindByRing(boolean isOn) {
        newsRemindByRingisOn = isOn;
        SettingServer.newsRemindRing = String.valueOf(isOn);
    }

    @Override
    public void newsRemindByShake(boolean isOn) {
        newsRemindByShakeisOn = isOn;
        SettingServer.newsRemindShake = String.valueOf(isOn);
    }

    /**
     * 初始化完成之后
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        //获取新消息通知全部状态
        //调用接口 获取当前状态
        /*new GetNewsRemindSettingUseCase(this).execute(new Action1<SettingBean[]>() {
            @Override
            public void call(SettingBean[] settingBeans) {
                for (int i = 0; i < settingBeans.length; i++) {
                    //若key对应新消息通知
                    if (settingBeans[i].getKey().equals(SettingBean.NEWSREMIND)) {
                        getVu().setNewsRemind(Boolean.valueOf(settingBeans[i].getValue()));
                        newsRemindisOn = Boolean.valueOf(settingBeans[i].getValue());
                        SettingServer.newsRemind = settingBeans[i].getValue();
                    }
                    //若key对应新消息通知-声音
                    if (settingBeans[i].getKey().equals(SettingBean.NEWSREMIND_RING)) {
                        getVu().setNewsRemindByRing(Boolean.valueOf(settingBeans[i].getValue()));
                        newsRemindByRingisOn = Boolean.valueOf(settingBeans[i].getValue());
                        SettingServer.newsRemindRing = settingBeans[i].getValue();
                    }
                    //若key对应新消息通知-振动
                    if (settingBeans[i].getKey().equals(SettingBean.NEWSREMIND_SHAKE)) {
                        getVu().setNewsRemindByShake(Boolean.valueOf(settingBeans[i].getValue()));
                        newsRemindByShakeisOn = Boolean.valueOf(settingBeans[i].getValue());
                        SettingServer.newsRemindShake = settingBeans[i].getValue();
                    }
                }
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //实例化新消息通知信息用于保存
        //保存设置消息的状态
    }
}
