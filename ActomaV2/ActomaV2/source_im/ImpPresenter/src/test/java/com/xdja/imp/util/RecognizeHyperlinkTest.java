package com.xdja.imp.util;

import android.text.util.Linkify;

import com.xdja.imp.domain.model.HyperLinkBean;

import junit.framework.Assert;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 项目名称：ActomaV2
 * 类描述：RecognizeHyperlink的单元测试类
 * 创建人：yuchangmu
 * 创建时间：2016/12/6.
 * 修改人：yuchangmu
 * 修改时间：2016/12/7
 * 修改备注：
 * 1)Task 2812 2814 modified for testing RecognizeHyperlink by ycm 2016/12/07.
 */
public class RecognizeHyperlinkTest {
    private String[] webPrefix = new String[]{"http://", "https://", "rtsp://"};
    private String[] emailPrefix = new String[]{"mailto:"};
    private final int LINK_WEB = 0x01;
    private final int LINK_EMAIL = 0x02;
    private final int LINK_PHONE = 0x04;
    public final int ALL = LINK_WEB | LINK_EMAIL | LINK_PHONE;
    public static final String GOOD_IRI_CHAR =
            "a-zA-Z0-9\uF900-\uFDCF\uFDF0-\uFFEF";
    public static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");
    private static final String GOOD_GTLD_CHAR =
            "a-zA-Z\uF900-\uFDCF\uFDF0-\uFFEF";
    private static final String GTLD = "[" + GOOD_GTLD_CHAR + "]{2,63}";
    private static final String IRI
            = "[" + GOOD_IRI_CHAR + "]([" + GOOD_IRI_CHAR + "\\-]{0,61}[" + GOOD_IRI_CHAR + "]){0,1}";
    private static final String HOST_NAME = "(" + IRI + "\\.)+" + GTLD;
    public static final Pattern DOMAIN_NAME
            = Pattern.compile("(" + HOST_NAME + "|" + IP_ADDRESS + ")");
    public static final Pattern WEB_URL = Pattern.compile(
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "(?:" + DOMAIN_NAME + ")"
                    + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)");
    public static final Pattern EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
            + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    public static final Pattern PHONE = Pattern.compile(
                      "(\\+[0-9]+[\\- \\.]*)?"
                    + "(\\([0-9]+\\)[\\- \\.]*)?"
                    + "([0-9][0-9\\- \\.]+[0-9])");

    @Test
    public void testCreateLink() throws Exception {
        String group1 = "www.baidu.com";
        String group2 = "http://www.baidu.com";
        String expect = "http://www.baidu.com";
        RecognizeHyperlink hyperlink = new RecognizeHyperlink();
        Class cls = hyperlink.getClass();
        Method mCreateLink = cls.getDeclaredMethod("createLink", String.class, String[].class);
        mCreateLink.setAccessible(true);
        String result1 = (String) mCreateLink.invoke(hyperlink, group1, webPrefix);
        Assert.assertEquals(expect, result1);

        String result2 = (String) mCreateLink.invoke(hyperlink, group2, webPrefix);
        Assert.assertEquals(expect, result2);

    }

    public static final Linkify.MatchFilter sUrlMatchFilter = new Linkify.MatchFilter() {
        public final boolean acceptMatch(CharSequence s, int start, int end) {
            if (start == 0) {
                return true;
            }

            if (s.charAt(start - 1) == '@') {
                return false;
            }

            return true;
        }
    };

    @Test
    public void testGetWebOrEmailLink() throws Exception {
        RecognizeHyperlink hyperlink = new RecognizeHyperlink();
        List<HyperLinkBean> expect = new ArrayList<>();
        String content = "手机号:13488231051,网址: www.baidu.com,邮箱:569293437@qq.com";
        HyperLinkBean phone = new HyperLinkBean();
        phone.setHyperlink("13488231051");
        phone.setStartPosition(0);
        phone.setEndPosition(15);
        phone.setLinkType(LINK_PHONE);
        HyperLinkBean web = new HyperLinkBean();
        web.setHyperlink("www.baidu.com");
        web.setStartPosition(20);
        web.setEndPosition(33);
        web.setLinkType(LINK_WEB);
        HyperLinkBean email = new HyperLinkBean();
        email.setHyperlink("569293437@qq.com");
        email.setStartPosition(37);
        email.setEndPosition(53);
        email.setLinkType(LINK_WEB);
//        expect.add(phone);
        expect.add(web);
//        expect.add(email);
        List<HyperLinkBean> result = new ArrayList<>();
        Class cls = hyperlink.getClass();
        Method mGetWebOrEmailLink = cls.getDeclaredMethod(
                "getWebOrEmailLink",
                List.class,
                String.class,
                Pattern.class,
                String[].class,
                Linkify.MatchFilter.class);
        mGetWebOrEmailLink.setAccessible(true);
        mGetWebOrEmailLink.invoke(hyperlink, result, content, WEB_URL, webPrefix, sUrlMatchFilter);
        Assert.assertEquals(webPrefix[0] + expect.get(0).getHyperlink(), result.get(0).getHyperlink());
        Assert.assertEquals(expect.get(0).getStartPosition(), result.get(0).getStartPosition());
        Assert.assertEquals(expect.get(0).getEndPosition(), result.get(0).getEndPosition());

        expect.clear();
        expect.add(email);
        result.clear();
        mGetWebOrEmailLink.invoke(hyperlink, result, content, EMAIL_ADDRESS, emailPrefix, null);
        Assert.assertEquals(emailPrefix[0] + expect.get(0).getHyperlink(), result.get(0).getHyperlink());
        Assert.assertEquals(expect.get(0).getStartPosition(), result.get(0).getStartPosition());
        Assert.assertEquals(expect.get(0).getEndPosition(), result.get(0).getEndPosition());
    }

    private static final int PHONE_NUMBER_MINIMUM_DIGITS = 5;
    public static final Linkify.MatchFilter sPhoneNumberMatchFilter = new Linkify.MatchFilter() {
        public final boolean acceptMatch(CharSequence s, int start, int end) {
            int digitCount = 0;

            for (int i = start; i < end; i++) {
                if (Character.isDigit(s.charAt(i))) {
                    digitCount++;
                    if (digitCount >= PHONE_NUMBER_MINIMUM_DIGITS) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    @Test
    public void testGetPhoneLink() throws Exception {
        RecognizeHyperlink hyperlink = new RecognizeHyperlink();
        List<HyperLinkBean> expect = new ArrayList<>();
        String content = "手机号:13488231051,网址: www.baidu.com,邮箱:569293437@qq.com";
        HyperLinkBean phone = new HyperLinkBean();
        phone.setHyperlink("13488231051");
        phone.setStartPosition(4);
        phone.setEndPosition(15);
        phone.setLinkType(LINK_PHONE);
        expect.add(phone);
        List<HyperLinkBean> result = new ArrayList<>();
        Class cls = hyperlink.getClass();
        Method mGetPhoneLink = cls.getDeclaredMethod(
                "getPhoneLink",
                List.class,
                String.class,
                Pattern.class,
                Linkify.MatchFilter.class);
        mGetPhoneLink.setAccessible(true);
        mGetPhoneLink.invoke(hyperlink, result, content, PHONE, sPhoneNumberMatchFilter);
        Assert.assertEquals(expect.get(0).getHyperlink(), result.get(0).getHyperlink());
        Assert.assertEquals(expect.get(0).getStartPosition(), result.get(0).getStartPosition());
        Assert.assertEquals(expect.get(0).getEndPosition(), result.get(0).getEndPosition());

    }

    @Test
    public void testParseContent() throws Exception {
        int LINK_WEB = 0x01;
        int LINK_EMAIL = 0x02;
        int LINK_PHONE = 0x04;
        String content = "www.baidu.com";
        String content2 = "123456@11.com";
        List<HyperLinkBean> linkBeen = new ArrayList<>();
        HyperLinkBean web = new HyperLinkBean();
        web.setHyperlink(content);
        web.setStartPosition(0);
        web.setEndPosition(13);
        web.setLinkType(LINK_WEB);
        linkBeen.add(web);
        RecognizeHyperlink hyperlink = new RecognizeHyperlink();
        List<HyperLinkBean> result = hyperlink.parseContent(content, LINK_WEB);
        Assert.assertEquals(webPrefix[0] + linkBeen.get(0).getHyperlink(), result.get(0).getHyperlink());
        Assert.assertEquals(linkBeen.get(0).getStartPosition(), result.get(0).getStartPosition());
        Assert.assertEquals(linkBeen.get(0).getEndPosition(), result.get(0).getEndPosition());
        Assert.assertEquals(linkBeen.get(0).getLinkType(), result.get(0).getLinkType());


//        HyperLinkBean email = new HyperLinkBean();
//        email.setHyperlink(content2);
//        email.setStartPosition(0);
//        email.setEndPosition(13);
//        email.setLinkType(LINK_EMAIL);
//        linkBeen.clear();
//        linkBeen.add(email);
//        List<HyperLinkBean> result2 = hyperlink.parseContent(content, LINK_EMAIL);
//        Assert.assertEquals(emailPrefix[0]+linkBeen.get(0).getHyperlink(), result2.get(0).getHyperlink());
//        Assert.assertEquals(linkBeen.get(0).getStartPosition(), result2.get(0).getStartPosition());
//        Assert.assertEquals(linkBeen.get(0).getEndPosition(), result2.get(0).getEndPosition());
//        Assert.assertEquals(linkBeen.get(0).getLinkType(), result2.get(0).getLinkType());
    }

    @Test
    public void testHyperlinkFilter() throws Exception {
        String content = "手机13488231051网址www.baidu.com邮箱569293437@qq.com";
        RecognizeHyperlink hyperlink = new RecognizeHyperlink();
        HyperLinkBean web = new HyperLinkBean();
        HyperLinkBean email = new HyperLinkBean();
        HyperLinkBean phone1 = new HyperLinkBean();
        HyperLinkBean phone2 = new HyperLinkBean();
        web.setStartPosition(17);
        web.setEndPosition(30);
        web.setLinkType(LINK_WEB);
        web.setHyperlink("http://www.baidu.com");
        email.setStartPosition(33);
        email.setEndPosition(49);
        email.setLinkType(LINK_EMAIL);
        email.setHyperlink("569293437@qq.com");
        phone1.setStartPosition(3);
        phone1.setEndPosition(14);
        phone1.setLinkType(LINK_PHONE);
        phone1.setHyperlink("13488231051");
        phone2.setStartPosition(33);
        phone2.setEndPosition(42);
        phone2.setLinkType(LINK_PHONE);
        phone2.setHyperlink("5692934327");
        List<HyperLinkBean> linkBeen = new ArrayList<>();
        linkBeen.add(web);
        linkBeen.add(email);
        linkBeen.add(phone1);
        linkBeen.add(phone2);
        List<HyperLinkBean> expect = new ArrayList<>();
        expect.add(phone1);
        expect.add(web);
        expect.add(email);
        Class cls = hyperlink.getClass();
        Method mHyperlinkFilter = cls.getDeclaredMethod("hyperlinkFilter", List.class);
        mHyperlinkFilter.setAccessible(true);
        List<HyperLinkBean> result = (List<HyperLinkBean>) mHyperlinkFilter.invoke(hyperlink, linkBeen);
        Assert.assertEquals(expect.get(0).getStartPosition(), result.get(0).getStartPosition());
        Assert.assertEquals(expect.get(0).getEndPosition(), result.get(0).getEndPosition());
        Assert.assertEquals(expect.get(0).getHyperlink(), result.get(0).getHyperlink());
        Assert.assertEquals(expect.get(0).getLinkType(), result.get(0).getLinkType());
        Assert.assertEquals(expect.get(1).getStartPosition(), result.get(1).getStartPosition());
        Assert.assertEquals(expect.get(1).getEndPosition(), result.get(1).getEndPosition());
        Assert.assertEquals(expect.get(1).getHyperlink(), result.get(1).getHyperlink());
        Assert.assertEquals(expect.get(1).getLinkType(), result.get(1).getLinkType());
        Assert.assertEquals(expect.get(2).getStartPosition(), result.get(2).getStartPosition());
        Assert.assertEquals(expect.get(2).getEndPosition(), result.get(2).getEndPosition());
        Assert.assertEquals(expect.get(2).getHyperlink(), result.get(2).getHyperlink());
        Assert.assertEquals(expect.get(2).getLinkType(), result.get(2).getLinkType());
    }

    @Test
    public void testSortAllLink() throws Exception {
        String content = "手机13488231051网址www.baidu.com邮箱569293437@qq.com";
        RecognizeHyperlink hyperlink = new RecognizeHyperlink();
        HyperLinkBean web = new HyperLinkBean();
        HyperLinkBean email = new HyperLinkBean();
        HyperLinkBean phone1 = new HyperLinkBean();
        HyperLinkBean phone2 = new HyperLinkBean();
        web.setStartPosition(17);
        web.setEndPosition(30);
        web.setLinkType(LINK_WEB);
        web.setHyperlink("http://www.baidu.com");
        email.setStartPosition(33);
        email.setEndPosition(49);
        email.setLinkType(LINK_EMAIL);
        email.setHyperlink("569293437@qq.com");
        phone1.setStartPosition(3);
        phone1.setEndPosition(14);
        phone1.setLinkType(LINK_PHONE);
        phone1.setHyperlink("13488231051");
        phone2.setStartPosition(33);
        phone2.setEndPosition(42);
        phone2.setLinkType(LINK_PHONE);
        phone2.setHyperlink("5692934327");
        List<HyperLinkBean> linkBeen = new ArrayList<>();
        linkBeen.add(web);
        linkBeen.add(email);
        linkBeen.add(phone1);
        linkBeen.add(phone2);

        List<HyperLinkBean> expect = new ArrayList<>();
        expect.add(phone1);
        expect.add(web);
        expect.add(phone2);
        expect.add(email);
        Class cls = hyperlink.getClass();
        Method mSortAllLink = cls.getDeclaredMethod("sortAllLink", List.class);
        mSortAllLink.setAccessible(true);
        List<HyperLinkBean> result = (List<HyperLinkBean>) mSortAllLink.invoke(hyperlink, linkBeen);
        Assert.assertEquals(expect.get(0).getStartPosition(), result.get(0).getStartPosition());
        Assert.assertEquals(expect.get(0).getEndPosition(), result.get(0).getEndPosition());
        Assert.assertEquals(expect.get(0).getHyperlink(), result.get(0).getHyperlink());
        Assert.assertEquals(expect.get(0).getLinkType(), result.get(0).getLinkType());
        Assert.assertEquals(expect.get(1).getStartPosition(), result.get(1).getStartPosition());
        Assert.assertEquals(expect.get(1).getEndPosition(), result.get(1).getEndPosition());
        Assert.assertEquals(expect.get(1).getHyperlink(), result.get(1).getHyperlink());
        Assert.assertEquals(expect.get(1).getLinkType(), result.get(1).getLinkType());
        Assert.assertEquals(expect.get(2).getStartPosition(), result.get(2).getStartPosition());
        Assert.assertEquals(expect.get(2).getEndPosition(), result.get(2).getEndPosition());
        Assert.assertEquals(expect.get(2).getHyperlink(), result.get(2).getHyperlink());
        Assert.assertEquals(expect.get(2).getLinkType(), result.get(2).getLinkType());
        Assert.assertEquals(expect.get(3).getStartPosition(), result.get(3).getStartPosition());
        Assert.assertEquals(expect.get(3).getEndPosition(), result.get(3).getEndPosition());
        Assert.assertEquals(expect.get(3).getHyperlink(), result.get(3).getHyperlink());
        Assert.assertEquals(expect.get(3).getLinkType(), result.get(3).getLinkType());
    }

    public void testSetLinkSpan() throws Exception {

    }


}
