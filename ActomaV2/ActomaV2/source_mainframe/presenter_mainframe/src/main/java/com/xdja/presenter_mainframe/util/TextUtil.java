package com.xdja.presenter_mainframe.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.xdja.presenter_mainframe.R;


/**
 * Created by geyao on 2015/9/15.
 */
public class TextUtil {

    private static final int PASSWORD_LENGHT = 20;
    private static final int NICKNAME_LENGHT = 16;

    /**
     * 图片资源区分
     */
    public static class ActomaImage {
        /**
         * 设置-安通+版本号加号图片
         */
        public final static int IMAGE_VERSION = 0;
        /**
         * ToolBar加号图片
         */
        public final static int IMAGE_TITLE = 1;
        /**
         * 列表加号图片(消息-安通+团队 好友-安通+团队)
         */
        public final static int IMAGE_LIST = 2;
        /**
         * 用在锁屏提示处的加号图片(大图)
         */
        public final static int IMAGE_NOTIFICATION_BIG = 3;
        /**
         * 用在锁屏提示处的加号图片(小图)
         */
        public final static int IMAGE_NOTIFICATION_SMALL = 4;
        /**
         * 好友-安通+团队详细资料加号图片
         */
        public final static int IMAGE_INFO = 5;
        /**
         * 关于安通+版本号加号图片
         */
        public final static int IMAGE_VERSION_BIG = 6;
        /**
         * Toast加号图片
         */
        public final static int IMAGE_TOAST = 7;
        /**
         * 登陆界面的加好
         */
        public static final int IMAGE_LOGIN = 8;

        /**
         * 关于安通+版本号加号图片
         */
        public final static int IMAGE_VERSION_BIG_RED = 9;

        /**
         * 关于安通+版本号加号图片
         */
        public final static int IMAGE_INPUT_HINT = 10;

        /**
         * 安通+加号图片类型
         */
        @IntDef({IMAGE_VERSION, IMAGE_TITLE, IMAGE_LIST, IMAGE_NOTIFICATION_BIG
                , IMAGE_NOTIFICATION_SMALL, IMAGE_INFO, IMAGE_VERSION_BIG
                , IMAGE_TOAST, IMAGE_LOGIN , IMAGE_VERSION_BIG_RED , IMAGE_INPUT_HINT})
        public @interface ActomaImageType {
        }
    }

    /**
     * 在字符串中间添加图片
     *
     * @param context        上下文句柄
     * @param imageType      图片类型
     * @param imageMarginTop 图片距离顶部
     * @param imageWidth     图片宽度
     * @param imageHeight    图片高度
     * @param content        所需显示文字
     * @return 修改后的字符串
     */
    @SuppressWarnings({"MethodWithTooManyParameters", "ReturnOfNull"})
    public static Spanned getActomaText(final Context context,
                                        @ActomaImage.ActomaImageType int imageType,
                                        final int imageMarginTop,
                                        final int imageWidth,
                                        final int imageHeight,
                                        String content) {
        if (context == null || TextUtils.isEmpty(content)) {
            return null;
        }
        if (!content.contains("+")) {
            return Html.fromHtml(content);
        }
        //要显示的图片资源
        int image;
        //区分所需显示的图片资源
        switch (imageType) {
            case ActomaImage.IMAGE_VERSION:
                image = R.mipmap.at_version;
                break;
            case ActomaImage.IMAGE_TITLE:
                image = R.mipmap.at_title;
                break;
            case ActomaImage.IMAGE_LIST:
                image = R.mipmap.at_list;
                break;
            case ActomaImage.IMAGE_NOTIFICATION_BIG:
                image = R.mipmap.at_notification_big;
                break;
            case ActomaImage.IMAGE_NOTIFICATION_SMALL:
                image = R.mipmap.at_notification_small;
                break;
            case ActomaImage.IMAGE_INFO:
                image = R.mipmap.at_info;
                break;
            case ActomaImage.IMAGE_VERSION_BIG:
                image = R.mipmap.at_version_big;
                break;
            case ActomaImage.IMAGE_TOAST:
                image = R.mipmap.at_toast;
                break;
            case ActomaImage.IMAGE_LOGIN:
                image = R.mipmap.antong_login;
                break;
            case ActomaImage.IMAGE_VERSION_BIG_RED:
                image = R.mipmap.at_version_big_red;
                break;
            case ActomaImage.IMAGE_INPUT_HINT:
                image = R.mipmap.at_input_hint;
                break;

            default:
                image = R.mipmap.at_list;
                break;
        }
        //实例化ImageGetter
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            @SuppressWarnings("deprecation")
            @Override
            public Drawable getDrawable(String source) {
                int id = Integer.parseInt(source);
                Drawable d = context.getResources().getDrawable(id);
                d.setBounds(0, imageMarginTop,
                        imageWidth == 0 ? d.getIntrinsicWidth() : imageWidth,
                        imageHeight == 0 ? d.getMinimumHeight() : imageHeight);
                return d;
            }
        };
        //最后所需显示的字符串
        String source = "";
        //加号图片的字符串
        String imageString = "<img src=\"" + image + "\">";
        //以+号拆分传入的字符串
        String[] split = content.split("\\+");
        //判断传入的字符串是否以+号开始 若以加号开始则追加加号图片字符串
        if (content.startsWith("+")) {
            source = imageString;
        }
        //循环从数组中拿取字符拼接 中间追加加号图片字符串
        for (int i = 0; i < split.length; i++) {
            if (i != split.length - 1) {
                source = source + split[i] + imageString;
            } else {
                source = source + split[i];
            }
        }
        //判断传入的字符串是否以+号结尾 若以加号结尾则追加加号图片字符串
        if (content.endsWith("+")) {
            source = source + imageString;
        }
        //返回修改后的Spanned
        return Html.fromHtml(source, imageGetter, null);
    }

    public static boolean isRulePassword(String password) {
        if (password == null) {
            return false;
        }
        int length = password.length();
        if (length >= 6 && length <= PASSWORD_LENGHT)
            return true;
        else
            return false;
    }

    public static boolean isRuleNickname(String nickname) {
        if (nickname == null) {
            return false;
        }
        nickname = nickname.trim();
        if (!nickname.isEmpty() && nickname.length() <= NICKNAME_LENGHT)
            return true;
        else
            return false;
    }
    public static boolean isRuleAuthorizeId(String authorizeId) {
        if (authorizeId == null) {
            return false;
        }
        int length = authorizeId.length();
        //modify by tangsha from length == 6 to this for add ckms authId
        if (length >= 7) {
            return true;
        } else
            return false;
    }

    /**
     * 手机号码的匹配规则
     */
    private static final String PHONE_NUMBER_REG = "1\\d{10}";
    public static boolean isRulePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        if (phoneNumber.matches(PHONE_NUMBER_REG)) {
            return true;
        } else
            return false;
    }
    /**
     * 验证码的匹配规则
     */
    private static final String VERIFY_CODE_REG = "\\d{6}";
    public static boolean isRuleVerifyCode(String verifyCode) {
        if (verifyCode == null) {
            return false;
        }
        if (verifyCode.matches(VERIFY_CODE_REG)) {
            return true;
        } else
            return false;
    }

    /**
     * 账号匹配规则
     */
    private static final String ACCOUNT_REG = "[A-Za-z][A-Za-z0-9_-]{5,19}";
    public static boolean isRuleAccount(String account) {
        if (account == null) {
            return false;
        }
        if (account.matches(ACCOUNT_REG)) {
            return true;
        } else
            return false;
    }
}
