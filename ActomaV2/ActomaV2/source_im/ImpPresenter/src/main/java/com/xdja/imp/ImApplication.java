package com.xdja.imp;

import android.app.Application;

import com.xdja.contactopproxy.ContactCallBack;
import com.xdja.frame.ApplicationLifeCycle;
import com.xdja.frame.MockApplication;
import com.xdja.imp.di.HasComponent;
import com.xdja.imp.di.component.ApplicationComponent;
import com.xdja.imp.di.component.DaggerApplicationComponent;
import com.xdja.imp.di.module.ApplicationModule;
import javax.inject.Inject;

/**
 * <p>Summary:Application基类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:11:38</p>
 */
public class ImApplication extends MockApplication implements ApplicationLifeCycle,
        HasComponent<ApplicationComponent> {

    public static final float FACE_ITEM_NORMAL_VALUE = 1.1f;

    public static final float FACE_ITEM_SMALL_VALUE = 0.7f;

    private ApplicationComponent applicationComponent;

    public static ImApplication androidApplication;

    @Inject
    ContactCallBack contactCallBack;


    @Override
    public ApplicationComponent getComponent() {
        return this.applicationComponent;
    }

    @Override
    public void onCreate(Application application) {
//        super.onCreate(application);
        androidApplication = this;
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(application))
                .build();
        applicationComponent.inject(this);
    }


    public interface OnActivityStatusListener{
        void onCreate();
    }

    private  OnActivityStatusListener mOnActivityCreateListener;

    public OnActivityStatusListener getActivityStatusListener(){
        return mOnActivityCreateListener;
    }

    public void setOnActivityStatusListener(OnActivityStatusListener listener){
        mOnActivityCreateListener = listener;
    }

}
