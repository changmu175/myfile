package com.xdja.imp;

import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:19:11</p>
 */
@RunWith(ImpApplicationTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.JELLY_BEAN,
        packageName = "com.xdja.imp"/*,
        application = AndroidApplication.class*/)
public abstract class ImpApplicationTestCase {

}
