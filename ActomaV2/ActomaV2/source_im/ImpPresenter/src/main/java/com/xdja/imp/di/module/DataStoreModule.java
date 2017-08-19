package com.xdja.imp.di.module;

import android.content.Context;

import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp.data.di.annotation.UserScope;
import com.xdja.imp.data.repository.datasource.CloudDataStore;
import com.xdja.imp.data.repository.datasource.CloudDataStoreImp;
import com.xdja.imp.data.repository.datasource.DiskDataStore;
import com.xdja.imp.data.repository.datasource.DiskDataStoreImp;
import com.xdja.imsdk.ImClient;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/6</p>
 * <p>Time:11:28</p>
 */
@Module
public class DataStoreModule {
    @UserScope
    @Provides
    CloudDataStore privideCloudDataStore(CloudDataStoreImp cloudDataStoreImp) {
        return cloudDataStoreImp;
    }

    @UserScope
    @Provides
    DiskDataStore privideDiskDataStore(DiskDataStoreImp diskDataStoreImp) {
        return diskDataStoreImp;
    }

    @UserScope
    @Provides
    ImClient provideIMClient(@Scoped(DiConfig.CONTEXT_SCOPE_APP) Context context){
        return ImClient.getInstance(context);
    }
}
