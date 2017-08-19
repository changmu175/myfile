package com.xdja.comm.uitl;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;

import java.util.List;
import java.util.Locale;

/**
 * Created by yangpeng on 2015/4/14.
 */
public class CommonUtils {
    private static final String TAG = "CommonUtils";
    public static boolean isServiceRunning(Context context , String serviceName){
        boolean isRunning = false;
        if (context == null || serviceName == null) return isRunning;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> arrayList = activityManager.getRunningServices(100);
        if (arrayList == null || arrayList.size() == 0) return isRunning;
        for (int i = 0, n = arrayList.size(); i < n; i++) {
            if (serviceName.equals(arrayList.get(i).service.getClassName().toString())) {
                isRunning = true;
                break;
            }
        }
        Log.v(TAG, "H>>> ServiceRunning : " + serviceName + " , " + isRunning);
        return isRunning;
    }

    private static long lastClickTime;
    public static boolean isFastDoubleClick(){
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if(0 < timeD && timeD < 400){
            return true;
        }
        lastClickTime = time;
        return  false;
    }

    public static boolean isZH(Context context){
        if (context == null) return true;
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return TextUtils.isEmpty(language) || language.endsWith("zh");// modified by ycm for lint 2017/02/13
    }

    /**
     * 检测集团通讯录是否变更
     * @param curAccount        当前账号
     * @param curCompanyCode    当前的集团码
     *                          add by xnn @20170308
     */
    public static void checkCompanyCodeChanged(String curAccount, String curCompanyCode) {
        String preCompanyCode = getCommpanyCode(curAccount);
        if (!TextUtils.isEmpty(preCompanyCode) && !TextUtils.isEmpty(curCompanyCode)) {
            if (curCompanyCode.equals(preCompanyCode)) {
                AccountServer.accountBeanCompany.setCompanyCodeChanged(false);
            } else {
                AccountServer.accountBeanCompany.setCompanyCodeChanged(true);
            }
        } else {
            AccountServer.accountBeanCompany.setCompanyCodeChanged(true);
        }
    }

    public static void setCompanyCode(String account, String companyCode){
        LogUtil.getUtils().d(TAG+"0328 setCompanyCode account "+account+" companyCode "+companyCode);
        PreferencesServer.getWrapper(ActomaController.getApp()).
                setPreferenceStringValue("CompanyCode"+account,companyCode);
    }

    public static String getCommpanyCode(String account){
        return PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("CompanyCode" + account);
    }

    //返回集团通讯录是否变更
    public static boolean isCompanyCodeChanged() {
        return AccountServer.accountBeanCompany.isCompanyCodeChanged();
    }
}
