package com.xdja.imp.data.di.annotation;

import android.support.annotation.NonNull;

import com.xdja.imp.data.di.DiConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di.annotation</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/28</p>
 * <p>Time:10:08</p>
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Scoped {
    @NonNull @DiConfig.ContextScope String value() default DiConfig.CONTEXT_SCOPE_APP;
}
