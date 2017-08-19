package com.xdja.comm.uitl;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;


import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;

import java.util.Locale;

/**
 * <p>Summary:全局统一工具类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.uitl</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/9/15</p>
 * <p>Time:19:19</p>
 */
public class UniversalUtil {
    /**
     * Xposed存在状态的默认值
     */
    private static final int STATE_XPOSED_DEFAULT = -1;
    /**
     * Xposed存在
     */
    private static final int STATE_XPOSED_EXIST = 1;
    /**
     * Xposed不存在
     */
    private static final int STATE_XPOSED_DESEXIST = 0;
    /**
     * 标识系统是否含有Xposed框架
     */
    private static int xposedExitState = STATE_XPOSED_DEFAULT;
    /**
     * 每次判断的检测次数
     */
    private final static int CHECK_COUNT = 3;

    private static int C_CHECK_COUNT = CHECK_COUNT;

    private static final String ACTION_TRIGGER_UPDATE = "com.xdja.actoma.singleupdate";

    /**
     * 启动升级模块
     */
    public static void updateCall(Context context) {
        Intent intent = new Intent(ACTION_TRIGGER_UPDATE);
        context.sendBroadcast(intent);
    }

    /**
     * 判断系统是否集成了Xposed框架
     *
     * @return 判断结果
     */
	 //[S]modify by tangsha@20161214 for HookService update strage change
    public static final String XPOSED_SERVICE_NAME = "user.xposed.system";
    public static final String XDJA_XPOSED_SERVICE_NAME = "user.xdjaposed.system";
    private static String currentXposedServiceName = "";

    public static String getCurrentXposeServiceName(){
      if(xposedExitState == STATE_XPOSED_EXIST){
           return currentXposedServiceName;
      }
      return "";
    }

    public static boolean isXposed() {
        //私有化环境版本不支持第三方加密
        if (CustInfo.isCustom()) {
            return false;
        }
        //如果为初始状态，开始检测Xposed的存在状态
        if (xposedExitState < 0) {
            checkServiceByName(XPOSED_SERVICE_NAME);
            if(xposedExitState != STATE_XPOSED_EXIST){
                checkServiceByName(XDJA_XPOSED_SERVICE_NAME);
            }
        }
        return xposedExitState == STATE_XPOSED_EXIST;
    }

    @SuppressWarnings("ConstantConditions")
    private static void checkServiceByName(String serviceName){
        //为保证检测结果，多次检测
        while (C_CHECK_COUNT > 0) {
            //根据是否能获取到Ibinder来判断
            IBinder binder = ServiceManager.getService(serviceName);
            if (binder != null) {
                xposedExitState = STATE_XPOSED_EXIST;
                currentXposedServiceName = serviceName;
                LogUtil.getUtils().e("UniversalUtil currentName compare old "+currentXposedServiceName.compareTo(XPOSED_SERVICE_NAME));
                break;
            } else {
                xposedExitState = STATE_XPOSED_DESEXIST;
                C_CHECK_COUNT--;
            }
        }
        C_CHECK_COUNT = CHECK_COUNT;
    }
	//[E]modify by tangsha@20161214 for HookService update strage change

    //[S]add by tangsha@2016-10-12 for change language.
    /**
     * 记录预言类别;
     * LANGUAGE_DEFAULT:跟随系统;
     * LANGUAGE_CH_SIMPLE:简体中文；
     * LANGUAGE_EN：英文；
     */
    public static final int LANGUAGE_DEFAULT = 0;
    public static final int LANGUAGE_CH_SIMPLE = 1;
    public static final int LANGUAGE_EN = 2;
    public static int getLanguageType(Context context){
        String currentLocal = context.getResources().getConfiguration().locale.getLanguage();
        if(currentLocal.compareTo(Locale.CHINESE.getLanguage()) == 0) {
            return LANGUAGE_CH_SIMPLE;
        }else if(currentLocal.compareTo(Locale.ENGLISH.getLanguage()) == 0){
            return LANGUAGE_EN;
        }
        return LANGUAGE_DEFAULT;
    }

    private static final String LANGUAGE_TYPE = "language";
    public static int getLanguage(Context context) {
        return PreferencesServer.getWrapper(context).gPrefIntValue(LANGUAGE_TYPE);
    }

    public static void setLanguage(Context context,int languageType) {
        PreferencesServer.getWrapper(context).setPreferenceIntValue(LANGUAGE_TYPE, languageType);
    }

    public static void changeLanguageConfig(Context context){
        int language = getLanguage(context);
        Resources resources = context.getResources();
        DisplayMetrics display = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale toLocal = config.locale;
        switch(language){
            case LANGUAGE_DEFAULT:
                toLocal = Locale.getDefault();
                break;
            case LANGUAGE_EN:
                toLocal = Locale.ENGLISH;
                break;
            case LANGUAGE_CH_SIMPLE:
                toLocal = Locale.CHINA;
                break;
        }
        if(!toLocal.equals(config.locale)) {
            LogUtil.getUtils().e("UniversalUtil changeLanguageConfig language is "+language+" toLocal "+toLocal.toString()+" configLocal "+config.locale);
            config.locale = toLocal;
            resources.updateConfiguration(config, display);
        }
    }
    // [E]add by tangsha@2016-10-12 for change language.

    //[s]add by xienana for multi language change web url @20161115 [review by tangsha]
    /**
     * 改变多语言本地网页的url
     */
    public static String changeLanLocalWebUrl(Context context, String url){
        String loadUrl = "";
        if (!TextUtils.isEmpty(url) && url.lastIndexOf(".") > -1) {
            if (getLanguageType(context) == LANGUAGE_CH_SIMPLE) {
                loadUrl = url;
            } else if (getLanguageType(context) == LANGUAGE_EN){
                loadUrl = url.substring(0, url.lastIndexOf(".")) + "-en.html";
            }else {
                loadUrl = url.substring(0, url.lastIndexOf(".")) + "-en.html";
            }
        }
        return loadUrl;
    }

    /**
     * 改变多语言Server端网页的url
     */
    public static String changeLanServerWebUrl(Context context,String url){
        String loadUrl = "";
        if (!TextUtils.isEmpty(url) && url.lastIndexOf("/") > -1) {
            if (getLanguageType(context) == LANGUAGE_CH_SIMPLE) {
                loadUrl = url;
            } else if (getLanguageType(context) == LANGUAGE_EN) {
                loadUrl = url.substring(0,url.lastIndexOf("/")+1)+"English"+url.substring(url.lastIndexOf("/"),url.length());
            } else {
                loadUrl = url.substring(0,url.lastIndexOf("/")+1)+"English"+url.substring(url.lastIndexOf("/"),url.length());
            }
        }
        return loadUrl;
    }
    //[e]add by xienana for multi language change web url @20161115 [review by tangsha]
}
