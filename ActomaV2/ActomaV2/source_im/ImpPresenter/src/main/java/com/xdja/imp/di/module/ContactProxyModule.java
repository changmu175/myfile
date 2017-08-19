package com.xdja.imp.di.module;

import com.xdja.contactopproxy.ContactBusinessProxy;
import com.xdja.contactopproxy.ContactService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:全局依赖提供者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:14:29</p>
 */
@Module
public class ContactProxyModule {


    @Provides
    @Singleton
    ContactService provideContactService(ContactBusinessProxy contactBusinessProxy) {
        return contactBusinessProxy;
    }
}
