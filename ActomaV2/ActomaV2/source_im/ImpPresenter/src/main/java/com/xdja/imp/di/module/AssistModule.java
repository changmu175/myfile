package com.xdja.imp.di.module;

import com.xdja.imp.data.di.annotation.UserScope;
import com.xdja.imp.util.MsgDisplay;
import com.xdja.imp.util.XToast;

import dagger.Module;
import dagger.Provides;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/8</p>
 * <p>Time:10:48</p>
 */
@Module
public class AssistModule {
    @UserScope
    @Provides
    MsgDisplay provideMsgDisplay(XToast toast){
        return toast;
    }
}
