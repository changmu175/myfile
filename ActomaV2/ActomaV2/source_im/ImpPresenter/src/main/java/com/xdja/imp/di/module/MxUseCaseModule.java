package com.xdja.imp.di.module;

import com.xdja.imp.data.di.annotation.PerActivity;
import com.xdja.imp.domain.interactor.def.AddNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.AddUserAccount;
import com.xdja.imp.domain.interactor.def.DeleteAllDraft;
import com.xdja.imp.domain.interactor.def.DeleteNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.DeleteSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.DeleteTopSetting;
import com.xdja.imp.domain.interactor.def.GetSessionConfigs;
import com.xdja.imp.domain.interactor.def.GetRoamSetting;
import com.xdja.imp.domain.interactor.def.GetSingleSessionConfig;
import com.xdja.imp.domain.interactor.def.MatchSessionConfig;
import com.xdja.imp.domain.interactor.def.QueryUserAccount;
import com.xdja.imp.domain.interactor.def.SaveDraft;
import com.xdja.imp.domain.interactor.def.SaveRoamSetting;
import com.xdja.imp.domain.interactor.def.SaveTopSetting;
import com.xdja.imp.domain.interactor.mx.AddNoDisturbSettingUseCase;
import com.xdja.imp.domain.interactor.mx.AddUserAccountUseCase;
import com.xdja.imp.domain.interactor.mx.DeleteAllDraftUseCase;
import com.xdja.imp.domain.interactor.mx.DeleteNoDisturbSettingUseCase;
import com.xdja.imp.domain.interactor.mx.DeleteSingleSessionConfigUseCase;
import com.xdja.imp.domain.interactor.mx.DeleteTopSettingUseCase;
import com.xdja.imp.domain.interactor.mx.GetSessionConfigsUseCase;
import com.xdja.imp.domain.interactor.mx.GetRoamSettingUseCase;
import com.xdja.imp.domain.interactor.mx.GetSingleSessionConfigUseCase;
import com.xdja.imp.domain.interactor.mx.GetUserAccountUseCase;
import com.xdja.imp.domain.interactor.mx.MatchSessionConfigUseCase;
import com.xdja.imp.domain.interactor.mx.SaveDraftUseCase;
import com.xdja.imp.domain.interactor.mx.SaveTopSettingUseCase;
import com.xdja.imp.domain.interactor.mx.SetRoamSettingUseCase;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/30</p>
 * <p>Time:16:29</p>
 */
@Module
public class MxUseCaseModule {

    public MxUseCaseModule() {
    }

    @Provides
    @PerActivity
    SaveRoamSetting provideSaveRoamSetting(SetRoamSettingUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    GetRoamSetting provideGetRoamSetting(GetRoamSettingUseCase getRoamSettingUseCase) {
        return getRoamSettingUseCase;
    }

    @Provides
    @PerActivity
    AddNoDisturbSetting provideAddNoDisturbSetting(AddNoDisturbSettingUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    DeleteNoDisturbSetting provideDeleteNoDisturbSetting(DeleteNoDisturbSettingUseCase useCase) {
        return useCase;
    }

    /*@Provides
    @PerActivity
    SaveTopSetting provideSaveSettingTopSetting(SaveTopSettingUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    DeleteTopSetting provideDeleteSettingTopSetting(DeleteTopSettingUseCase useCase) {
        return useCase;
    }*/


    @Provides
    @PerActivity
    GetSessionConfigs provideGetNoDisturbSetting(GetSessionConfigsUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    GetSingleSessionConfig provideGetSingleSessionConfig(GetSingleSessionConfigUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    SaveDraft provideSaveDraft(SaveDraftUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    DeleteAllDraft provideDeleteAllDraft(DeleteAllDraftUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    SaveTopSetting provideSaveTopSetting(SaveTopSettingUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    DeleteTopSetting provideDeleteTopSetting(DeleteTopSettingUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    MatchSessionConfig provideMatchSessionConfig(MatchSessionConfigUseCase useCase){
        return useCase;
    }


    @Provides
    @PerActivity
    AddUserAccount provideAddUserAccount(AddUserAccountUseCase userAccountUseCase){
        return userAccountUseCase;
    }

    @Provides
    @PerActivity
    QueryUserAccount provideQueryUserAccount(GetUserAccountUseCase userAccountUseCase){
        return userAccountUseCase;
    }

    @Provides
    @PerActivity
    DeleteSingleSessionConfig provideDeleteSingleSessionConfig(DeleteSingleSessionConfigUseCase useCase){
        return useCase;
    }
}
