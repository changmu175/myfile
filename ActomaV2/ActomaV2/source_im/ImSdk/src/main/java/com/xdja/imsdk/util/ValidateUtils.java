package com.xdja.imsdk.util;

import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkConfig;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/11/28 11:15                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ValidateUtils {
    /**
     * m位到n位的正整数校验："^[1-9][0-9]{m,n}$"
     * @param value 需要校验的字符串
     * @param m 正整数最低位数
     * @param n 正整数最高位数
     */
    public static boolean isValidateInt(String value, int m, int n) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        // 由于第一位为1到9之间，所以位数限制减掉第一位
        m = m - 1;
        n = n - 1;
        String regex = "^[1-9][0-9]{" + m + "," + n + "}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if(matcher.matches()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 正整数校验："^[1-9]\\d*$"
     * @param value 需要校验的字符串
     */
    public static boolean isValidateLong(Long value) {
        if (value == null || TextUtils.isEmpty(value + "")) {
            return false;
        }
        String regex = "^[1-9]\\d*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value + "");
        if(matcher.matches()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 文件路径有效性校验：
     * "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$"
     * @param value 需要校验的字符串
     * @return boolean
     */
    public static boolean isValidatePath(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        if (value.length() > 255) {
            return false;
        }
        String regex = "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$";
        if(value.matches(regex)){
            File file = new File(value);
            return file.exists();
        }else{
            return false;
        }
    }


    // TODO: 2016/12/9 liming
    public static boolean verifyCustomConfig(String key, String value) {
//        if (ImSdkConfigKey.IM_SERVER.equals(key)) {
//            // ImServer是否正确，暂不做校验
//            return true;
//        }

//        if (ImSdkConfigKey.HAVE_MS.equals(key)
//                || ImSdkConfigKey.SLAST_MSG_SC.equals(key)
//                || ImSdkConfigKey.NO_SESSION.equals(key)
//                || ImSdkConfigKey.CREATE_THU.equals(key)) {
//            // 状态消息，最后一条消息状态回调，会话管理，是否创建缩略图 （yes，no）
//            return ImSdkConfigValue.CONFIG_VALUE_YES.equalsIgnoreCase(value)
//                    || ImSdkConfigValue.CONFIG_VALUE_NO.equalsIgnoreCase(value);
//        }

//        if (ImSdkConfigKey.ROAM_PERIOD.equals(key)
//                || ImSdkConfigKey.SYNC_PERIOD.equals(key)) {
//            // 消息漫游、同步周期校验，需要6到8位正整数
//            return ValidateUtils.isValidateInt(value, 6, 8);
//        }

//        if (ImSdkConfigKey.THU_SIZE_W.equals(key)
//                || ImSdkConfigKey.THU_SIZE_H.equals(key)) {
//            // 缩略图宽度、高度校验,需要2到4位正整数
//            return ValidateUtils.isValidateInt(value, 2, 4);
//        }

//        if (ImSdkConfigKey.LFILE_PATH.equals(key)) {
//            // 文件保存路径校验，暂时不做
//            return true;
//        }

        // 文件传输控制百分比控制校验，1到99的正整数
//        return !ImSdkConfig.K_PER.equals(key) || ValidateUtils.isValidateInt(value, 1, 2);
        return true;

    }
}
