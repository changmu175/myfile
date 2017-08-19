package com.xdja.presenter_mainframe.di.components.pre;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.dependence.annotations.PerLife;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.di.modules.ActivityModule;
import com.xdja.frame.di.modules.FragmentModule;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext4Interactor;
import com.xdja.frame.domain.usecase.Ext6Interactor;
import com.xdja.presenter_mainframe.di.modules.SubActivityModule;
import com.xdja.presenter_mainframe.di.modules.SubFragmentModule;

import java.util.List;

import dagger.Subcomponent;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@PerLife
@Subcomponent(
        modules = {PreUseCaseModule.class}
)
public interface PreUseCaseComponent {
    ActivityPreUseCaseComponent plus(ActivityModule activityModule, SubActivityModule subActivityModule);

    FragmentPreUseCaseComponent plus(FragmentModule fragmentModule, SubFragmentModule subFragmentModule);



    //For Test
    @InteractorSpe(DomainConfig.ACCOUNT_REGIST)
    Ext4Interactor<String, String, String, String, MultiResult<String>> registAccountUseCase();

    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. Start
    @InteractorSpe(DomainConfig.CKMS_INIT)
    Ext1Interactor<Boolean,MultiResult<Object>> ckmsInitUseCase();
    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. End
    //[S]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.
    @InteractorSpe(DomainConfig.CKMS_RELEASE)
    Ext0Interactor<Integer> ckmsReleaseUseCase();
    //[E]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.

   /* @InteractorSpe(DomainConfig.UPDATE_STRATEGYS)
    Ext4Interactor<String, String, Integer, Integer, Integer> updateStrategys();*/

    @InteractorSpe(DomainConfig.QUERY_STRATEGYBYMOBILE)
    Ext6Interactor<String, String,String, String, Integer, Integer, Integer> queryStrategyByMobile();

    @InteractorSpe(DomainConfig.QUERY_STRATEGYS)
    Ext0Interactor<List<EncryptAppBean>> queryStrategys();

    @InteractorSpe(DomainConfig.DETECT_INIT)
    Ext0Interactor<MultiResult<Object>> detectUseCase();

    @InteractorSpe(DomainConfig.USER_INFO_INIT)
    Ext0Interactor<MultiResult<Object>> launcherGetUserInfoUseCase();
}
