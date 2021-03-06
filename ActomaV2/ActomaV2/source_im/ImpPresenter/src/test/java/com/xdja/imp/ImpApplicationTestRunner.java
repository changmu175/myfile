package com.xdja.imp;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/13</p>
 * <p>Time:18:43</p>
 */
public class ImpApplicationTestRunner extends RobolectricGradleTestRunner{

    private static final String ANDROID_MANIFEST_PATH = "/src/main/AndroidManifest.xml";
    private static final String ANDROID_MANIFEST_RES_PATH = "/src/main/res";

    public ImpApplicationTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
}
