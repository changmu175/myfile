package com.xdja.data_mainframe.di;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.data_mainframe.repository.datastore.AccountCloudStore;
import com.xdja.data_mainframe.repository.datastore.AccountDiskStore;
import com.xdja.data_mainframe.repository.datastore.AccountStore;
import com.xdja.data_mainframe.repository.datastore.CkmsCloudStore;
import com.xdja.data_mainframe.repository.datastore.CkmsDiskStore;
import com.xdja.data_mainframe.repository.datastore.CkmsStore;
import com.xdja.data_mainframe.repository.datastore.DeviceAuthCloudStore;
import com.xdja.data_mainframe.repository.datastore.DeviceAuthDiskStore;
import com.xdja.data_mainframe.repository.datastore.DeviceAuthStore;
import com.xdja.data_mainframe.repository.datastore.PwdCloudStore;
import com.xdja.data_mainframe.repository.datastore.PwdDiskStore;
import com.xdja.data_mainframe.repository.datastore.PwdStore;
import com.xdja.data_mainframe.repository.datastore.UserInfoCloudStore;
import com.xdja.data_mainframe.repository.datastore.UserInfoDiskStore;
import com.xdja.data_mainframe.repository.datastore.UserInfoStore;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@Module
public class PreStoreModule {

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    AccountStore.PreAccountStore provideAccountDiskStore(AccountDiskStore diskStore){
        return diskStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    AccountStore.PreAccountStore provideAccountCloudStore(AccountCloudStore.PreAccountCloudStore cloudStore){
        return cloudStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    UserInfoStore.PreUserInfoStore provideUserInfoDiskStore(UserInfoDiskStore diskStore){
        return diskStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    UserInfoStore.PreUserInfoStore provideUserInfoCloudStore(UserInfoCloudStore.PreUserInfoCloudStore cloudStore){
        return cloudStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    PwdStore providePwdDiskStore(PwdDiskStore diskStore){
        return diskStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    PwdStore providePwdCloudStore(PwdCloudStore cloudStore){
        return cloudStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    DeviceAuthStore provideDeviceAuthDiskStore(DeviceAuthDiskStore diskStore){
        return diskStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    DeviceAuthStore provideDeviceAuthCloudStore(DeviceAuthCloudStore cloudStore){
        return cloudStore;
    }

    /*[S]add by tangsha@20160704 for ckms*/
    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    CkmsStore provideCkmsDiskStore(CkmsDiskStore diskStore){
        return diskStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    CkmsStore provideCkmsCloudStore(CkmsCloudStore cloudStore){
        return cloudStore;
    }
    /*[E]add by tangsha@20160704 for ckms*/


    /*[S]add by licong@20161216 for safeLock*/
   /* @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    SafeLockStore.PreSafeLockStore provideSafeLockDiskStore(SafeLockDiskStore diskStore){
        return diskStore;
    }

    @AppScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    SafeLockStore.PreSafeLockStore provideSafeLockCloudStore(SafeLockCloudStore.PreSafeLockCloudStore cloudStore){
        return cloudStore;
    }*/
    /*[E]add by licong@20161216 for safeLock*/
}
