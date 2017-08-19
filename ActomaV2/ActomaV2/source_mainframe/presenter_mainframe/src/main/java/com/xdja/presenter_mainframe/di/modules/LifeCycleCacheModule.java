package com.xdja.presenter_mainframe.di.modules;

import com.xdja.comm.blade.accountLifeCycle.AccountLifeCycle;
import com.xdja.comm.blade.accountLifeCycle.CommonAccountLifeCycle;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ldy on 16/6/2.
 */
@Module
public class LifeCycleCacheModule {

    @Provides(type = Provides.Type.SET)
    @Singleton
    Set<AccountLifeCycle> provideAccountLifeCycle(){
        Set<AccountLifeCycle> set = new HashSet<>();
        set.add(new CommonAccountLifeCycle());
        return set;
    }
}
