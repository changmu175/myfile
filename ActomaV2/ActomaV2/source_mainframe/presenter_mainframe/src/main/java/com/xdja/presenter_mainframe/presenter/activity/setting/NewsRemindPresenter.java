package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Context;
import android.os.Bundle;

import com.xdja.comm.data.SettingBean;
import com.xdja.comm.server.SettingServer;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.presenter_mainframe.cmd.NewsRemindCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewNewsRemind;
import com.xdja.presenter_mainframe.ui.uiInterface.NewsRemindVu;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;

/**
 * Created by chenbing on 2015/7/7.
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class NewsRemindPresenter extends PresenterActivity<NewsRemindCommand, NewsRemindVu> implements NewsRemindCommand {
    /*============================用于操作界面UI(CheckBox状态)===========================*/
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

    @Inject
    @InteractorSpe(DomainConfig.GET_NEWREMIND_SETTINGS)
    Lazy<Ext1Interactor<Context,SettingBean[]>> getNewsRemindSettingUseCase;

    @Inject
    @InteractorSpe(DomainConfig.SET_NEWREMIND_SETTINGS)
    Lazy<Ext2Interactor<Context, SettingBean[], Boolean[]>> saveNewsRemindSettingUseCase;
    /*============================用于操作界面UI(CheckBox状态)===========================*/

    @Override
    protected Class<? extends NewsRemindVu> getVuClass() {
        return ViewNewsRemind.class;
    }

    @Override
    protected NewsRemindCommand getCommand() {
        return this;
    }


    /**
     * 新消息通知
     *
     * @param isOn 是否打开
     */
    @Override
    public void newsRemind(boolean isOn) {
        newsRemindisOn = isOn;
        SettingServer.newsRemind = String.valueOf(isOn);
    }

    /**
     * 新消息通知-声音
     *
     * @param isOn 是否打开
     */
    @Override
    public void newsRemindByRing(boolean isOn) {
        newsRemindByRingisOn = isOn;
        SettingServer.newsRemindRing = String.valueOf(isOn);
    }


    /**
     * 新消息通知-振动
     *
     * @param isOn 是否打开
     */
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
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取新消息通知全部状态
        getNewsRemindSettingUseCase.get().fill(this).execute(new Action1<SettingBean[]>() {
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
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //实例化新消息通知信息用于保存
        SettingBean bean1 = new SettingBean();
        bean1.setKey(SettingBean.NEWSREMIND);
        bean1.setValue(String.valueOf(newsRemindisOn));
        //实例化新消息通知-声音信息用于保存
        SettingBean bean2 = new SettingBean();
        bean2.setKey(SettingBean.NEWSREMIND_RING);
        bean2.setValue(String.valueOf(newsRemindByRingisOn));
        //实例化新消息通知-振动信息用于保存
        SettingBean bean3 = new SettingBean();
        bean3.setKey(SettingBean.NEWSREMIND_SHAKE);
        bean3.setValue(String.valueOf(newsRemindByShakeisOn));
        //执行保存操作
        saveNewsRemindSettingUseCase.get().fill(this, new SettingBean[]{bean1, bean2, bean3})
                .execute(new Action1<Boolean[]>() {
            @Override
            public void call(Boolean[] booleans) {
                //查询新消息通知信息
                SettingBean newsRemind = SettingServer.querySetting(SettingBean.NEWSREMIND);
                //修改供其他模块调用的新消息通知状态值
                if (newsRemind != null) {
                    SettingServer.newsRemind = newsRemind.getValue();
                }
                //查询新消息通知-声音信息信息
                SettingBean newsRemindRing = SettingServer.querySetting(SettingBean.NEWSREMIND_RING);
                //修改供其他模块调用的新消息通知-声音状态值
                if (newsRemindRing != null) {
                    SettingServer.newsRemindRing = newsRemindRing.getValue();
                }
                //查询新消息通知-振动信息
                SettingBean newsRemindShake = SettingServer.querySetting(SettingBean.NEWSREMIND_SHAKE);
                //修改供其他模块调用的新消息通知-振动状态值
                if (newsRemindShake != null) {
                    SettingServer.newsRemindShake = newsRemindShake.getValue();
                }
            }
        });
    }

}
