package com.xdja.data_mainframe.di;

import com.xdja.comm_mainframe.annotations.UserScope;
import com.xdja.data_mainframe.entities.cache.UserCache;
import com.xdja.data_mainframe.entities.cache.UserCacheImp;
import com.xdja.domain_mainframe.model.Account;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/4</p>
 * <p>Time:15:20</p>
 */
@Module
public class UserCacheModule {

    private Account account;

    private String ticket;

    public UserCacheModule(Account account, String ticket) {
        this.account = account;
        this.ticket = ticket;
    }

    @UserScope
    @Provides
    UserCache provideUserCache() {
        UserCacheImp userCacheImp = new UserCacheImp(this.account, this.ticket);
        return userCacheImp;
    }

}
