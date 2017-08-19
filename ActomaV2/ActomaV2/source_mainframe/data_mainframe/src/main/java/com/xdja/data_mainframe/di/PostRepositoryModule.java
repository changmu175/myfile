package com.xdja.data_mainframe.di;

import com.xdja.comm_mainframe.annotations.UserScope;
import com.xdja.data_mainframe.repository.AccountRepImp;
import com.xdja.data_mainframe.repository.DeviceAuthRepImp;
import com.xdja.data_mainframe.repository.DownloadRepImp;
import com.xdja.data_mainframe.repository.UserInfoRepImp;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.domain_mainframe.repository.DownloadRepository;
import com.xdja.domain_mainframe.repository.UserInfoRepository;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/5</p>
 * <p>Time:14:10</p>
 */
@Module
public class PostRepositoryModule {

    @UserScope
    @Provides
    AccountRepository.PostAccountRepository provideAccountRepository(AccountRepImp.PostAccountRepImp repImp) {
        return repImp;
    }

    @UserScope
    @Provides
    DeviceAuthRepository.PostDeviceAuthRepository provideDeviceAuthRepository(DeviceAuthRepImp.PostDeviceAuthRepImp repImp) {
        return repImp;
    }

    @UserScope
    @Provides
    UserInfoRepository provideUserInfoRepository(UserInfoRepImp repImp) {
        return repImp;
    }

    @UserScope
    @Provides
    DownloadRepository provideDownloadRepository(DownloadRepImp repImp) {
        return repImp;
    }

    //[S] add by licong,for safeLock Server
    /*@UserScope
    @Provides
    SafeLockRepository provideSafeLockRepository(SafeLockRepImp repImp) {
        return repImp;
    }*/
    //[E] add by licong,for safeLock Server

}
