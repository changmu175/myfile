package com.xdja.domain_mainframe.di.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by xdja-fanjiandong on 2016/3/28.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InteractorSpe {
    @DomainConfig.UseCaseType String value() default "";
}
