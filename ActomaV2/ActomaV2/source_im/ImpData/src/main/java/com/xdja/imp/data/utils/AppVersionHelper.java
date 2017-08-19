package com.xdja.imp.data.utils;

import android.text.TextUtils;

import com.xdja.comm.cust.CustInfo;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.report.reportClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/27 15:03   </br>
 * <p>Package: com.xdja.imp.data.utils</br>
 * <p>Description: App版本号处理帮助类   </br>
 */
public class AppVersionHelper {

    /**
     * 支持IM文件
     */
    public static final int IS_OK = 0;

    /**
     * 不支持IM文件
     */
    public static final int IS_FAIL = 1;

    /**
     * 操作失败，包括参数错误或者请求IO错误
     */
    public static final int IS_ERROR = 2;


    //P2.1.1.20161226
    public static final String IM_FILE_SUPPORT_VERSION = "P2.1.01.20161228";

    /**
     * 网络请求数据JSon字段
     */
    public static final String PARAM_RESULT = "result";
    public static final String PARAM_ERROR = "error";

    private MemoryCache mCache;

    private Pattern pattern = Pattern.compile("[^0-9]");

    private static class singletonInstance {
        private static AppVersionHelper mInstance = new AppVersionHelper();
    }

    private AppVersionHelper() {
        mCache = new MemoryCache();
    }

    public static AppVersionHelper getHelper() {
        return singletonInstance.mInstance;
    }

    /**
     * 请求用户对应的版本号
     *
     * @param account
     */
    public int requestAppVersion(final String account, String ticket) {
        return IS_OK; // 内网环境 调试不查询版本
//        //私有化环境，不进行版本判断
//        if (CustInfo.isCustom()) {
//            return IS_OK;
//        }
//        if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(account)) {
//            return IS_ERROR;
//        }
//
//        //先从缓存中获取账号对应的版本号
//        String cacheVersion = getAppVersion(account);
//        if (!TextUtils.isEmpty(cacheVersion)) {
//            if (isSupportFile(cacheVersion)) {
//                //支持IM文件
//                return IS_OK;
//            } else {
//                //不支持
//                return IS_FAIL;
//            }
//        }
//        try {
//            Response response = reportClientMessage.reportClientMessage_queryAppVersion(ticket, account);
//            if (null != response && response.isSuccessful()) {
//                String body = response.body().string();
//                String versionName = parseResult(body);
//                saveAppVersion(account, versionName);
//
//                if (isSupportFile(versionName)) {
//                    //支持IM文件
//                    return IS_OK;
//                } else {
//                    //不支持
//                    return IS_FAIL;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return IS_ERROR;
    }

    /**
     * 解析当前的版本号
     *
     * @return
     */
    private String parseResult(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject != null && jsonObject.has(PARAM_RESULT)) {

                //result字段
                JSONObject resultObj = jsonObject.getJSONObject(PARAM_RESULT);
                if (resultObj != null) {
                    return resultObj.getString("version");
                }

            } else if (null != jsonObject && jsonObject.has(PARAM_ERROR)) {

                //error字段
                JSONObject errorObj = jsonObject.getJSONObject(PARAM_ERROR);
                if (errorObj != null) {
                    LogUtil.getUtils().e("Version failed:" + errorObj.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 保存当前用户对应的版本号
     *
     * @param account
     * @param version
     */
    private void saveAppVersion(String account, String version) {
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(account)) {
            return;
        }

        if (mCache != null) {
            mCache.put(account, version);
        }
    }

    /**
     * 获取当前用户的版本号
     *
     * @param account
     * @return
     */
    private String getAppVersion(String account) {

        if (mCache != null) {
            return mCache.get(account);
        }
        return null;
    }

    /**
     * 当前版本是否支持IM文件收发
     *
     * @param versionName 当前接收方的版本号
     * @return true 支持文件  false 不支持
     */
    private boolean isSupportFile(String versionName) {
        if (TextUtils.isEmpty(versionName)) {
            return false;
        }

        return getVersionCode(versionName.substring(versionName.lastIndexOf("."), versionName.length()))
                >= getVersionCode(IM_FILE_SUPPORT_VERSION.substring(IM_FILE_SUPPORT_VERSION.lastIndexOf("."),
                IM_FILE_SUPPORT_VERSION.length()));
    }


    /**
     * 返回对应的版本号。由VersionName转化而来，只适用于本文件
     *
     * @return versionCode
     */
    private long getVersionCode(String versionName) {
        Matcher m = pattern.matcher(versionName);

        String versionCode = m.replaceAll("").replaceAll("\\.", "").trim();
        LogUtil.getUtils().e("versionCode:" + versionCode);
        try {
            return Long.valueOf(versionCode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*************************************************************************************
     * 自定义MemoryCache
     *************************************************************************************/

    public class MemoryCache {

        /**
         * 最大缓存个数
         */
        public static final int MAX_COUNT = 20;

        /**
         * 存活时间
         */
        public static final int TIME_HOUR = 60 * 60 * 1000;
        public static final int MAX_LIVE_TIME = 2 * TIME_HOUR;

        /**
         * 数据缓存区
         */
        private Map<String, Value> mCache = Collections
                .synchronizedMap(new LinkedHashMap<String, Value>());

        /**
         * 添加进内存缓存区
         *
         * @param key
         * @param value
         */
        public void put(String key, String value) {
            int size = mCache.size();
            if (size + 1 > MAX_COUNT) {
                removeNext();
            }
            long lastModifiedTime = System.currentTimeMillis();
            mCache.put(key, new Value(value, lastModifiedTime));
        }

        /**
         * 移除内存缓存区
         *
         * @param key
         * @return
         */
        public String get(String key) {
            Value value = mCache.get(key);
            if (value != null) {
                long lastModifiedTime = value.getLastModifiedTime();
                long currentTime = System.currentTimeMillis();
                //已过期
                if (currentTime - lastModifiedTime < MAX_LIVE_TIME) {
                    return value.getValue();
                } else {
                    mCache.remove(key);
                }
            }
            return null;
        }

        /**
         * 移除最久的一个
         */
        private void removeNext() {
            long time = 0;
            String removedKey = null;
            for (String key : mCache.keySet()) {
                Value value = mCache.get(key);
                if (time < value.getLastModifiedTime()) {
                    removedKey = value.getValue();
                    time = value.getLastModifiedTime();
                }
            }
            mCache.remove(removedKey);
        }

        public class Value {
            private String value;
            private long LastModifiedTime;

            public Value(String value, long lastModifiedTime) {
                this.value = value;
                LastModifiedTime = lastModifiedTime;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public long getLastModifiedTime() {
                return LastModifiedTime;
            }

            public void setLastModifiedTime(long lastModifiedTime) {
                LastModifiedTime = lastModifiedTime;
            }
        }
    }


}
