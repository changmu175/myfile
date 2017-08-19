package com.xdja.data_mainframe.di;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.data_mainframe.repository.AccountRepImp;
import com.xdja.data_mainframe.repository.ChipRepImp;
import com.xdja.data_mainframe.repository.CkmsRepImp;
import com.xdja.data_mainframe.repository.DeviceAuthRepImp;
import com.xdja.data_mainframe.repository.PwdRepImp;
import com.xdja.data_mainframe.repository.UserInfoRepImp;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.domain_mainframe.repository.ChipRepository;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.domain_mainframe.repository.PwdRepository;
import com.xdja.domain_mainframe.repository.UserInfoRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@Module
public class PreRepositoryModule {

    public static final String NAMED_ERRORSTATUS = "errorStatus";

    @Provides(type = Provides.Type.SET_VALUES)
    @Named(NAMED_ERRORSTATUS)
    static Set<Integer> provideErrorStatus(){
        return new HashSet<>(Arrays.asList(AccountRepImp.HTTP_400_ERROR_CODE, AccountRepImp.HTTP_401_ERROR_CODE, AccountRepImp.HTTP_500_ERROR_CODE));
    }

    @AppScope
    @Provides
    AccountRepository.PreAccountRepository provideAccountRepository(AccountRepImp.PreAccountRepImp repImp) {
        return repImp;
    }

    @AppScope
    @Provides
    ChipRepository provideChipRepository(ChipRepImp repImp) {
        return repImp;
    }

    /*[S]add by tangsha@20160629 for ckms*/
    @AppScope
    @Provides
    CkmsRepository provideCkmsRepository(CkmsRepImp repImp) {
        return repImp;
    }
    /*[S]add by tangsha@20160629 for ckms*/

    @AppScope
    @Provides
    DeviceAuthRepository provideDeviceAuthRepository(DeviceAuthRepImp repImp) {
        return repImp;
    }

    @AppScope
    @Provides
    PwdRepository providePwdRepository(PwdRepImp repImp) {
        return repImp;
    }

    @AppScope
    @Provides
    UserInfoRepository.PreUserInfoRepository provideUserInfoRepository(UserInfoRepImp.PreUserInfoRepImp repImp) {
        return repImp;
    }

    //[S] add by licong,for safeLock Server
   /* @AppScope
    @Provides
    SafeLockRepository.PreSafeLockRepository provideSafeLockRepository(SafeLockRepImp.PreSafeLockRepImp repImp) {
       return repImp;
   }*/
    //[E] add by licong,for safeLock Server

}
