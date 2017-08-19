package com.xdja.imsdk;

import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

/**
 * 项目名称：IMPresenter
 * 类描述：
 * 创建人：liming
 * 创建时间：2016/6/30 11:30
 * 修改人：liming
 * 修改时间：2016/6/30 11:30
 * 修改备注：
 */
@RunWith(ImSdkTestRunner.class)
@Config(constants = BuildConfig.class,sdk = Build.VERSION_CODES.JELLY_BEAN)
public abstract class ImSdkTestCase {
}
