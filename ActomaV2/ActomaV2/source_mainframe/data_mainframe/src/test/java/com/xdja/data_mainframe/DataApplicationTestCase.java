package com.xdja.data_mainframe;

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
@RunWith(DataApplicationTestRunner.class)
@Config(constants = BuildConfig.class,sdk = Build.VERSION_CODES.JELLY_BEAN)
public abstract class DataApplicationTestCase {

}
