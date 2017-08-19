package com.xdja.imsdk.util;

import com.xdja.imsdk.ImSdkTestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * 项目名称：IMPresenter
 * 类描述：校验类单元测试
 * 创建人：liming
 * 创建时间：2016/6/30 10:15
 * 修改人：liming
 * 修改时间：2016/6/30 10:15
 * 修改备注：
 */
public class ValidateUtilsTest extends ImSdkTestCase {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsValidateInt() throws Exception {
        String value;
        boolean result = ValidateUtils.isValidateInt(null, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "0";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "1";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "01";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "0000";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "11111";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "1qq";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "1.0";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "0.111";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "111.00";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "-121";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertFalse("Valid int result should be false", result);

        value = "12";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertTrue("Valid int result should be true", result);

        value = "10";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertTrue("Valid int result should be true", result);

        value = "200";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertTrue("Valid int result should be true", result);

        value = "3300";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertTrue("Valid int result should be true", result);

        value = "259200";
        result = ValidateUtils.isValidateInt(value, 6, 8);
        assertTrue("Valid int result should be true", result);

        value = "111";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertTrue("Valid int result should be true", result);

        value = "1111";
        result = ValidateUtils.isValidateInt(value, 2, 4);
        assertTrue("Valid int result should be true", result);
    }

    @Test
    public void testIsValidateLong() throws Exception {

    }

    @Test
    public void testIsValidatePath() throws Exception {

    }
}