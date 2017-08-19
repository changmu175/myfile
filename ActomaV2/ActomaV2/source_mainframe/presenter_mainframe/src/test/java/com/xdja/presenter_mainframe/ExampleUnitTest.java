package com.xdja.presenter_mainframe;

import com.xdja.presenter_mainframe.util.TextUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void isRuleAccount() throws Exception {
        assertEquals(true,TextUtil.isRuleAccount("q12345_-1234567890121"));
        assertEquals(false,TextUtil.isRuleAccount("q1234"));
    }
    @Test
    public void testHideMobileMid(){
        String mobile = "15093389642";
        String mid4 = mobile.substring(3, 7);
        String[] split = mobile.split(mid4);
        assertEquals("150****9642",split[0]+"****"+split[1]);
    }
    @Test
    public void testStr2Low(){
        String str = "123ABC";
        assertEquals("123abc",str.toLowerCase());
    }
}