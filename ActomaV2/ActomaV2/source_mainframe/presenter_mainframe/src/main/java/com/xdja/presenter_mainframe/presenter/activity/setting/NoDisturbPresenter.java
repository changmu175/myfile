package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.usecase.settings.GetNoDistrubSettingUseCase;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.presenter_mainframe.cmd.NoDisturbCommand;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewNoDisturb;
import com.xdja.presenter_mainframe.ui.uiInterface.NoDisturbVu;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Subscriber;

/**
 * Created by chenbing on 2015/7/7.
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class NoDisturbPresenter extends PresenterActivity<NoDisturbCommand, NoDisturbVu>
        implements NoDisturbCommand {

    GetNoDistrubSettingUseCase.NoDistrubBean t_noDistrubBean = new GetNoDistrubSettingUseCase.NoDistrubBean();

    @Inject
    @InteractorSpe(DomainConfig.GET_NODISTRUB_SETTINGS)
    Lazy<Ext1Interactor<Context,GetNoDistrubSettingUseCase.NoDistrubBean>> getNoDisturbSettingUseCase;

    @Inject
    @InteractorSpe(DomainConfig.SET_NODISTRUB_SETTINGS)
    Lazy<Ext2Interactor<Context, GetNoDistrubSettingUseCase.NoDistrubBean, Boolean>> saveNoDisturbSettingUseCase;

    @Override
    protected Class<? extends NoDisturbVu> getVuClass() {
        return ViewNoDisturb.class;
    }

    @Override
    protected NoDisturbCommand getCommand() {
        return this;
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
        getNoDisturbSettingUseCase.get().fill(this).execute(new Subscriber<GetNoDistrubSettingUseCase.NoDistrubBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.getUtils().e("获取勿扰模式数据出现错误");
            }

            @Override
            public void onNext(GetNoDistrubSettingUseCase.NoDistrubBean noDistrubBean) {
                if (noDistrubBean != null) {
                    t_noDistrubBean = noDistrubBean;
                    if (!TextUtils.isEmpty(noDistrubBean.getBeginTime())) {
                        getVu().setBeginTime(noDistrubBean.getBeginTime());
                    }
                    if (!TextUtils.isEmpty(noDistrubBean.getEndTime())) {
                        getVu().setEndTime(noDistrubBean.getEndTime());
                    }
                    getVu().setNoDistrub(noDistrubBean.isOpen());
                }
            }
        });
    }

    @Override
    public void setNoDistrubOpen(boolean isOpen) {
        if (this.t_noDistrubBean != null) {
            this.t_noDistrubBean.setIsOpen(isOpen);
        }
    }

    @Override
    public void setNoDistrubBeginTime(int hour, int minu) {
        if (this.t_noDistrubBean != null) {
            this.t_noDistrubBean.setBeginHour(hour);
            this.t_noDistrubBean.setBeginMinu(minu);
        }
    }

    @Override
    public void setNoDistrubEndTime(int hour, int minu) {
        if (this.t_noDistrubBean != null) {
            this.t_noDistrubBean.setEndHour(hour);
            this.t_noDistrubBean.setEndMinu(minu);
        }
    }

    @Override
    public GetNoDistrubSettingUseCase.NoDistrubBean getNodistrubBean() {
        return this.t_noDistrubBean;
    }

    @Override
    public void saveNoDisturbSettng() {
        saveNoDisturbSettingUseCase.get().fill(this,getCommand().getNodistrubBean())
                .execute(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.getUtils().i("勿扰模式信息保存结果出错");
            }

            @Override
            public void onNext(Boolean aBoolean) {
                LogUtil.getUtils().i("勿扰模式信息保存结果为 ： " + aBoolean);
            }
        });
    }

}
