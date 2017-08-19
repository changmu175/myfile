package com.xdja.data_mainframe.di;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xdja.comm_mainframe.annotations.AppScope;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.exeptions.SafeCardException;
import com.xdja.dependence.uitls.DeviceUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.data.persistent.PreferencesUtil;

import dagger.Module;
import dagger.Provides;
import dagger.mapkeys.StringKey;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.data_mainframe.di</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/14</p>
 * <p>Time:23:48</p>
 */
@Module
public class CacheModule {

    public static final String KEY_DEVICEID = "deviceId";
    public static final String KEY_UNBIND_USER = "unBindUser";
    public static final String KEY_CLIENT_VERSION = "clientVersion";
    public static final String KEY_CLIENT_RESOURCE = "clientResource";
    public static final String KEY_PN_TOKEN = "pnToken";
    public static final String KEY_CLIENT_TYPE = "clientType";
    public static final String KEY_LOGIN_TYPE = "loginType";
    public static final String KEY_DEVICE_MODEL = "deviceModel";
    public static final String KEY_OS_NAME = "osName";
    public static final String KEY_OS_VERSION = "osVersion";

    public static final String KEY_PRE_TICKET = "preTicket";
    public static final String KEY_PRE_CHIPID = "preChipId";
    /**
     * 登录前使用，上一次的登录账号
     */
    public static final String KEY_PRE_ACCOUNT_IN_PRE_LOGIN = "preAccount";
    /**
     * 登陆后使用，上一次登录账号
     */
    // TODO: 2016/6/4 这个静态变量的作用是？
    public static final String KEY_PRE_ACCOUNT_IN_AFTER_LOGIN = "preAccountInAfterLogin";
    public static final String KEY_LOGIN_STATE = "loginState";

    public static final String KEY_PRE_LOGIN_DATA = "preLoginData";

    public static final int DEFAULT_CLIENTTYPE = 1;

    public static final int DEFATUL_LOGINTYPE = 1;

    /**
     * 已登录
     */
    public static final int LOGIN_STATE_POS = 1;

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_DEVICEID)
    String provideDeviceId(TFCardManager tfCardManager) {
        try {
            return tfCardManager.getDeviceId();
        } catch (SafeCardException e) {
            LogUtil.getUtils().e(e.getMessage());
            return "";
        }
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_CLIENT_RESOURCE)
    String provideClientResource(TFCardManager tfCardManager) {
        return provideDeviceId(tfCardManager);
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_PN_TOKEN)
    String providePnToken(TFCardManager tfCardManager) {
        return "xdja/d/" + provideDeviceId(tfCardManager);
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_PRE_CHIPID)
    String providePreChipId(PreferencesUtil preferencesUtil){
        return preferencesUtil.gPrefStringValue(KEY_PRE_CHIPID);
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_PRE_TICKET)
    String providePreTicket(PreferencesUtil preferencesUtil){
        return preferencesUtil.gPrefStringValue(KEY_PRE_TICKET);
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_PRE_ACCOUNT_IN_PRE_LOGIN)
    String providePreAccount(PreferencesUtil preferencesUtil){
        return preferencesUtil.gPrefStringValue(KEY_PRE_ACCOUNT_IN_PRE_LOGIN);
    }

    @AppScope
    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_CLIENT_VERSION)
    String provideClientVersion(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.getUtils().e(e.getMessage());
            return "";
        }
    }

    @AppScope
    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_DEVICE_MODEL)
    String provideDeviceName() {
        return DeviceUtil.getOSModel();
    }

    @AppScope
    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_OS_NAME)
    String provideOsName() {
        return "1";
    }

    @AppScope
    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_OS_VERSION)
    String provideOsVersion() {
        return DeviceUtil.getOsVersion();
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_PRE_LOGIN_DATA)
    String providePreLoginData(PreferencesUtil util) {
        return util.gPrefStringValue(KEY_PRE_LOGIN_DATA);
    }

    @AppScope
    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_CLIENT_TYPE)
    Integer provideClientType() {
        return DEFAULT_CLIENTTYPE;
    }

    @AppScope
    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_LOGIN_TYPE)
    Integer provideLoginType() {
        return DEFATUL_LOGINTYPE;
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(KEY_LOGIN_STATE)
    Integer provideLoginState(PreferencesUtil util) {

        return util.gPrefIntValue(KEY_LOGIN_STATE);
    }

}
