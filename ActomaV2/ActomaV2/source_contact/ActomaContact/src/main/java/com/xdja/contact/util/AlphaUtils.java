package com.xdja.contact.util;

import com.xdja.comm.uitl.ObjectUtil;

import java.util.regex.Pattern;

/**
 * 快速下滑栏工具
 */
public class AlphaUtils {

    public static final String JING = "#";

    public static final String XING = "*";//add by wangalei for 1018

    public static String getAlpha(String key) {

        if(ObjectUtil.stringIsEmpty(key)) return JING;

        char c = key.trim().substring(0, 1).charAt(0);

        Pattern pattern = Pattern.compile("^[A-Za-z]+$");

        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        }

        return JING;
    }
}
