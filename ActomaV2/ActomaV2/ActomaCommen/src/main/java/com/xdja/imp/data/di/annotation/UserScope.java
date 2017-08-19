package com.xdja.imp.data.di.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by gbc on 2016/9/3.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface UserScope {}