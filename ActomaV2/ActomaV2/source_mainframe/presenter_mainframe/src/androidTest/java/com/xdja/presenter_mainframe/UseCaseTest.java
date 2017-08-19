package com.xdja.presenter_mainframe;

import android.test.ApplicationTestCase;

import com.xdja.data_mainframe.di.CacheModule;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.di.PreStoreModule;
import com.xdja.domain_mainframe.di.PreUseCaseModule;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.di.components.ApplicationComponent;
import com.xdja.frame.di.components.DaggerApplicationComponent;
import com.xdja.frame.di.modules.ApplicationModule;
import com.xdja.frame.di.modules.EventModule;
import com.xdja.frame.di.modules.ExecutorModule;
import com.xdja.frame.di.modules.NetworkModule;
import com.xdja.frame.di.modules.UtilModule;
import com.xdja.frame.domain.usecase.Ext4Interactor;
import com.xdja.presenter_mainframe.di.components.pre.AppComponent;
import com.xdja.presenter_mainframe.di.components.pre.DaggerAppComponent;
import com.xdja.presenter_mainframe.di.components.pre.PreUseCaseComponent;
import com.xdja.presenter_mainframe.di.modules.AppModule;

import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/19</p>
 * <p>Time:16:21</p>
 */
public class UseCaseTest extends ApplicationTestCase<ActomaApplication> {

    public UseCaseTest() {
        super(ActomaApplication.class);
    }

    PreUseCaseComponent preUseCaseComponent;

    AppComponent appComponent;

    ApplicationComponent applicationComponent;

    @Before
    public void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        createApplication();
        this.applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(getApplication()))
                .eventModule(new EventModule())
                .executorModule(new ExecutorModule())
                .networkModule(new NetworkModule())
                .utilModule(new UtilModule())
                .build();
        this.appComponent = DaggerAppComponent
                .builder()
                .applicationComponent(this.applicationComponent)
                .appModule(new AppModule(getApplication()))
                .cacheModule(new CacheModule())
                .preRepositoryModule(new PreRepositoryModule())
                .preStoreModule(new PreStoreModule())
                .build();
        this.preUseCaseComponent = this.appComponent.plus(new PreUseCaseModule());
    }

    @Test
    public void testRegistAccount() {
        Ext4Interactor<String, String, String, String, MultiResult<String>>
                registAccountUseCase = this.preUseCaseComponent.registAccountUseCase();

        TestSubscriber<MultiResult<String>> testSubscriber = new TestSubscriber<>();

        registAccountUseCase
                .fill("nickName", "pwd", "avt", "thumb")
                .execute(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
    }

    @Test
    public void testAccountPwdLogin() {
//        Ext2Interactor<String, String, MultiResult<Object>> accountPwdLogin
//                = this.postUseCaseComponent.accountPwdLogin();
//
//        TestSubscriber<MultiResult<Object>> testSubscriber = new TestSubscriber<>();
//
//        accountPwdLogin.fill("testAccount", "testPwd").execute(testSubscriber);
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertError(Throwable.class);
    }

    @Test
    public void testUserInfoModifyNickName() {
//        Ext1UseCase<String, Map<String,String>> accountPwdLogin
//                = (Ext1UseCase<String, Map<String, String>>) this.postUseCaseComponent.modifyNickName();
//
//        TestSubscriber<Map<String,String>> testSubscriber = new TestSubscriber<>();
//
//        accountPwdLogin.fill("testNickName").execute(testSubscriber);
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertError(Throwable.class);
    }

}
