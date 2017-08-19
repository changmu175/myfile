package com.xdja.dependence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dev.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/10/26</p>
 * <p>Time:14:46</p>
 */
@Scope @Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {}
