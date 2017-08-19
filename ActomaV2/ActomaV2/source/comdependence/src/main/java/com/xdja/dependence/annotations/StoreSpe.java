package com.xdja.dependence.annotations;

import android.support.annotation.NonNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.di.annotation</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/4</p>
 * <p>Time:10:53</p>
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreSpe {
    @NonNull @DiConfig.StoreType String value() default DiConfig.TYPE_DISK;
}
