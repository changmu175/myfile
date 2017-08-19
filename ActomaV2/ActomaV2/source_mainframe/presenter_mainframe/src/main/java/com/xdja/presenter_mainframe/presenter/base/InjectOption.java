package com.xdja.presenter_mainframe.presenter.base;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xdja-fanjiandong on 2016/3/10.
 * 注入选项
 */
public class InjectOption {
    /**
     * 当前Activity只需要ActivityComponent即可注入
     */
    public static final int OPTION_ACTIVITY = 0;
    /**
     * 当前Activity需要ActivityPreUseCaseComponent即可注入
     */
    public static final int OPTION_PRECACHEDUSER = 1;
    /**
     * 当前Activity需要ActivityPostUseCaseComponent即可注入
     */
    public static final int OPTION_POSTCACHEDUSER = 2;

    @IntDef(value = {OPTION_ACTIVITY, OPTION_PRECACHEDUSER, OPTION_POSTCACHEDUSER})
    public @interface Op {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Options {
        @Op int value() default OPTION_ACTIVITY;
    }
}

