package com.xdja.imp.data.persistent;

import android.app.Application;

import com.xdja.imp.ImpDataApplicationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

/**
 * <p>Summary:SharePreference单元测试</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.persistent</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/14</p>
 * <p>Time:16:34</p>
 */
public class PreferencesUtilTest extends ImpDataApplicationTestCase {

    PreferencesUtil preferencesUtil;

    Application application;

    @Before
    public void setUp() throws Exception {
        application = RuntimeEnvironment.application;
        preferencesUtil = new PreferencesUtil(application);
    }

    @Test
    public void testSetPreferenceStringValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceStringValue("testKey", "testValue");
        Assert.assertTrue(b);
    }

    @Test
    public void testSetPreferenceBooleanValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceBooleanValue("testKey1", true);
        Assert.assertTrue(b);
    }

    @Test
    public void testSetPreferenceFloatValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceFloatValue("testKey2", 1.0f);
        Assert.assertTrue(b);
    }

    @Test
    public void testSetPreferenceIntValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceIntValue("testKey3", 1);
        Assert.assertTrue(b);
    }

    @Test
    public void testGPrefStringValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceStringValue("testKey", "testValue");
        Assert.assertTrue(b);

        String testKey = preferencesUtil.gPrefStringValue("testKey");
        Assert.assertNotNull(testKey);
        Assert.assertEquals("testValue",testKey);
    }

    @Test
    public void testGPrefBooleanValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceBooleanValue("testKey1", true);
        Assert.assertTrue(b);

        Boolean testKey1 = preferencesUtil.gPrefBooleanValue("testKey1",false);
        Assert.assertNotNull(testKey1);
        Assert.assertTrue(testKey1);
    }

    @Test
    public void testGPrefIntValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceFloatValue("testKey2", 1.0f);
        Assert.assertTrue(b);

        float testKey2 = preferencesUtil.gPrefFloatValue("testKey2");
        Assert.assertEquals(1.0f,testKey2);

    }

    @Test
    public void testGPrefFloatValue() throws Exception {
        boolean b = preferencesUtil.setPreferenceIntValue("testKey3", 1);
        Assert.assertTrue(b);

        int testKey3 = preferencesUtil.gPrefIntValue("testKey3");
        Assert.assertEquals(1,testKey3);
    }
}