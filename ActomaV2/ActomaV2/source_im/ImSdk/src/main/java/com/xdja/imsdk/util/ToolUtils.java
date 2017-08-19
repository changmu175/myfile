package com.xdja.imsdk.util;

import android.os.SystemClock;
import android.text.TextUtils;

import com.xdja.imsdk.constant.IMSessionType;
import com.xdja.imsdk.constant.internal.Constant;
import com.xdja.imsdk.db.helper.SqlBuilder;
import com.xdja.imsdk.model.IMMessage;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：                 <br>
 * 创建时间：2016/11/16 17:29  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ToolUtils {

    /**
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    /**
     * 根据会话双方的账号和会话的类型生成唯一标识
     * @param account 账号
     * @param sessionType
     *        1：一对一会话<br>
     *        2：群组会话<br>
     *        100：自定义会话
     * @return 标识
     */
    public static String getSessionTag(String account, int sessionType) {
        return account + "_" + sessionType;
    }

    /**
     *
     * @param tag
     * @param message
     * @return
     */
    public static String getSessionTag(String tag, IMMessage message) {
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        int sessionType = IMSessionType.SESSION_SINGLE;
        if (message.isGroupIMMessage()) {
            sessionType = IMSessionType.SESSION_GROUP;
        }
        return getSessionTag(message.getTo(), sessionType);
    }

    /**
     * 根据指定分隔符分割字符串，返回最后一段字符串。<br>
     * 处理规则：<br>
     * 若输入为null，则返回null；<br>
     * 否则若输入为空字符串，则返回""；<br>
     * 否则若分隔符为null或空字符串，则返回包含""；<br>
     * 否则若输入不包含分隔符，则返回""；<br>
     * @param input 输入字符串
     * @param separator 分隔符
     * @return 结果字符串（注意：包括空字符串）
     */
    public static String getLastString(String input, String separator) {
        if (input == null)
            return null;
        if (input.equals(""))
            return "";
        if (separator == null || "".equals(separator))
            return "";

        int cursor = 0; // 游标
        int lastPos = 0; // 指向上一个分隔符后第一个字符
        ArrayList<String> list = new ArrayList<String>();

        while ((cursor = input.indexOf(separator, cursor)) != -1) {
            String token = input.substring(lastPos, cursor);
            list.add(token);
            lastPos = cursor + separator.length();
            cursor = lastPos;
        }

        if (lastPos == 0) {
            return "";
        }

        if (lastPos < input.length()) {
            list.add(input.substring(lastPos));
        }

        if (list.size() == 0) {
            return "";
        }
        return list.get(list.size() - 1);
    }

    /**
     * 指定字符所在位置
     * @param s s
     * @param separator separator
     * @return int
     */
    public static int getPos(String s, String separator) {
        if (TextUtils.isEmpty(s)) {
            return 0;
        }
        
        if (TextUtils.isEmpty(separator)) {
            return 0;
        }

        int cursor = 0; // 游标
        int lastPos = 0; // 指向上一个分隔符后第一个字符
        ArrayList<String> list = new ArrayList<String>();

        while ((cursor = s.indexOf(separator, cursor)) != -1) {
            String token = s.substring(lastPos, cursor);
            list.add(token);
            lastPos = cursor + separator.length();
            cursor = lastPos;
        }

        return lastPos;
    }
    
    public static String subString(String s, String sub) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        
        if (TextUtils.isEmpty(sub)) {
            return s;
        }
        
        String last = getLastString(s, ".");
        int pos = getPos(s, ".");
        if (sub.equals(last)) {
            if (pos > 0) {
                return s.substring(0, pos - 1);
            }
        }
        return s;
    }

    public static String toMD5(String content) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
            if (hash == null || hash.length == 0) {
                return content;
            }

            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b:hash) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return content;
        }
    }

    /**
     * 将字符串转化为int
     *
     * @param s
     * @return
     */
    public static int getInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 将字符串转化为long
     *
     * @param s
     * @return
     */
    public static long getLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }

    public static long getLong(Long id) {
        if (id == null) {
            return -1;
        }
        return id;
    }

    public static long getTimeDistance(long currentTime) {
        return currentTime * Constant.TIME_MULTIPLE - SystemClock.elapsedRealtimeNanos();
    }

    public static boolean isImageSuffix(String suffix) {
        if (suffix.equalsIgnoreCase("jpg") ||
                suffix.equalsIgnoreCase("jpeg") ||
                suffix.equalsIgnoreCase("png") ||
                suffix.equalsIgnoreCase("bmp")) {
            return true;
        }
        return false;
    }
}
