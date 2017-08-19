package com.xdja;

import android.app.Application;

import com.xdja.comm.server.ActomaController;
import com.xdja.frame.MockApplication;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/19</p>
 * <p>Time:15:42</p>
 */
public class CommonApplication extends MockApplication {
    private static Application mApplication;
    @Override
    public void onCreate(Application application) {
        mApplication = application;
        super.onCreate(application);

        ActomaController.setApp(application);
        ActomaController.setFlag(0);
    }

    public static Application getApplication(){
        return mApplication;
    }

}
