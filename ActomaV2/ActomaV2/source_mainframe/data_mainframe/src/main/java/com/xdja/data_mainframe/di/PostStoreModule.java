package com.xdja.data_mainframe.di;

import com.xdja.comm_mainframe.annotations.UserScope;
import com.xdja.data_mainframe.repository.datastore.AccountCloudStore;
import com.xdja.data_mainframe.repository.datastore.AccountDiskStore;
import com.xdja.data_mainframe.repository.datastore.AccountStore;
import com.xdja.data_mainframe.repository.datastore.CkmsCloudStore;
import com.xdja.data_mainframe.repository.datastore.CkmsDiskStore;
import com.xdja.data_mainframe.repository.datastore.CkmsStore;
import com.xdja.data_mainframe.repository.datastore.DeviceAuthCloudStore;
import com.xdja.data_mainframe.repository.datastore.DeviceAuthDiskStore;
import com.xdja.data_mainframe.repository.datastore.DeviceAuthStore;
import com.xdja.data_mainframe.repository.datastore.DownloadCloudStore;
import com.xdja.data_mainframe.repository.datastore.DownloadDiskStore;
import com.xdja.data_mainframe.repository.datastore.DownloadStore;
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
public class PostStoreModule {

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    AccountStore.PostAccountStore provideAccountDiskStore(AccountDiskStore diskStore){
        return diskStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    AccountStore.PostAccountStore provideAccountCloudStore(AccountCloudStore.PostAccountCloudStore cloudStore){
        return cloudStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    UserInfoStore provideUserInfoDiskStore(UserInfoDiskStore diskStore){
        return diskStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    UserInfoStore provideUserInfoCloudStore(UserInfoCloudStore cloudStore){
        return cloudStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    DeviceAuthStore.PostDeviceAuthStore provideDeviceAuthDiskStore(DeviceAuthDiskStore diskStore){
        return diskStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    DeviceAuthStore.PostDeviceAuthStore provideDeviceAuthCloudStore(DeviceAuthCloudStore.PostDeviceAuthCloudStore cloudStore){
        return cloudStore;
    }

    /*[S]add by tangsha@20160704 for ckms*/
    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    CkmsStore provideCkmsDiskStore(CkmsDiskStore diskStore){
        return diskStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    CkmsStore provideCkmsCloudStore(CkmsCloudStore cloudStore){
        return cloudStore;
    }
    /*[E]add by tangsha@20160704 for ckms*/

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    DownloadStore provideDownloadDiskStore(DownloadDiskStore diskStore){
        return diskStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    DownloadStore provideDownloadCloudStore(DownloadCloudStore cloudStore){
        return cloudStore;
    }

    //[S] add by licong,for safeLock Server
   /* @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_CLOUD)
    SafeLockStore provideSafeLockCloudStore (SafeLockCloudStore safeLockCloudStore) {
        return safeLockCloudStore;
    }

    @UserScope
    @Provides
    @StoreSpe(DiConfig.TYPE_DISK)
    SafeLockStore provideSafeLockDiskStore(SafeLockDiskStore safeLockCloudStore) {
        return safeLockCloudStore;
    }*/
    //[E] add by licong,for safeLock Server
}
