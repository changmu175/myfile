package com.xdja.presenter_mainframe.presenter.activity;

import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.NotifyStrategysEvent;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.presenter_mainframe.cmd.NewEncryptCommand;
import com.xdja.presenter_mainframe.enc3rd.utils.StrategysUtils;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewNewEncrypt;
import com.xdja.presenter_mainframe.ui.uiInterface.NewEncryptVu;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscriber;

/**
 * Created by geyao on 2015/11/23.
 * 重构-第三方应用列表页面
 */
@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class NewEncryptPresenter extends PresenterActivity<NewEncryptCommand, NewEncryptVu>
        implements NewEncryptCommand {

    @Inject
    @InteractorSpe(DomainConfig.QUERY_STRATEGYS)
    Lazy<Ext0Interactor<List<EncryptAppBean>>> queryEncryptAppsUseCase;

    @Override
    protected Class<? extends NewEncryptVu> getVuClass() {
        return ViewNewEncrypt.class;
    }

    @Override
    protected NewEncryptCommand getCommand() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getMainProvider().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setParams();
    }

    /**
     * View初始化之前
     *
     * @param savedInstanceState
     */
    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }

    /**
     * View初始化之后
     *
     * @param savedInstanceState
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        BusProvider.getMainProvider().register(this);
        if (getActivityPreUseCaseComponent() != null) {
            getActivityPreUseCaseComponent().inject(this);
        }
        initView();
        //用于修改设置页面第三方应用加密服务是否开启
        setResult(RESULT_OK);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        queryEncryptAppsUseCase.get().fill().execute(new Subscriber<List<EncryptAppBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<EncryptAppBean> encryptAppBeen) {
                getVu().initView(encryptAppBeen);
            }
        });
    }

    /**
     * 设置本地服务小开关状态
     */
    public void setParams() {
        StrategysUtils.queryStrategys(NewEncryptPresenter.this);
    }

    /**
     * 接收事件刷新列表
     *
     * @param list
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void notifyList(NotifyStrategysEvent list) {
        queryEncryptAppsUseCase.get().fill().execute(new Subscriber<List<EncryptAppBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<EncryptAppBean> encryptAppBeen) {
                getVu().setListAdapter(encryptAppBeen);
            }
        });
    }
}
