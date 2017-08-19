package com.xdja.comm.server;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.data.AppInfoBean;
import com.xdja.comm.data.AppInfoDao;
import com.xdja.dependence.uitls.LogUtil;

import java.util.List;

/**
 * Created by geyao on 2015/7/25.
 */
public class AppInfoServer {
    /**
     * 插入应用信息
     *
     * @param appInfoBean 要插入的应用信息
     * @return 新增结果
     */
    public static boolean insertAppInfo(@Nullable AppInfoBean appInfoBean) {
        AppInfoDao appInfoDao = null;
        try {
            appInfoDao = AppInfoDao.instance().open();
            return appInfoDao.insert(appInfoBean);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (appInfoDao != null) {
                appInfoDao.close();
            }
        }
    }

    /**
     * 删除appId对应的应用信息
     *
     * @param appId   要删除的应用所对应的appId
     * @return 删除结果
     */
    public static boolean deleteAppInfo(@Nullable String appId) {
        AppInfoDao appInfoDao = null;
        try {
            appInfoDao = AppInfoDao.instance().open();
            return appInfoDao.delete(appId);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (appInfoDao != null) {
                appInfoDao.close();
            }
        }
    }

    /**
     * 修改应用信息
     *
     * @param appInfoBean 要修改的应用信息Bean
     * @return 修改结果
     */
    public static boolean updateAppInfo(@Nullable AppInfoBean appInfoBean) {
        AppInfoDao appInfoDao = null;
        try {
            appInfoDao = AppInfoDao.instance().open();
            return appInfoDao.update(appInfoBean);
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return false;
        } finally {
            if (appInfoDao != null) {
                appInfoDao.close();
            }
        }
    }

    /**
     * 更新应用某个字段
     *
     * @param appId   应用id
     * @param field   字段名称
     * @param value   字段值
     * @return 是否成功
     */
    public static boolean updateAppInfoField(@NonNull String appId, @AppInfoDao.APPINFO_FIELD String field,  String value) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(appId)) {
            LogUtil.getUtils().i("参数不合法");
            return false;
        }
        AppInfoDao appInfoDao = null;
        try {
            appInfoDao = AppInfoDao.instance().open();
            return appInfoDao.updateField(appId, field, value);
        } catch (Exception ex) {
            LogUtil.getUtils().i(ex.getMessage());
            return false;
        } finally {
            if (appInfoDao != null) {
                appInfoDao.close();
            }
        }

    }

    /**
     * 获取全部应用信息
     *
     * @return 应用信息集合
     */
    @Nullable
    public static List<AppInfoBean> queryAllAppInfo() {
        List<AppInfoBean> list;
        AppInfoDao appInfoDao = null;
        try {
            appInfoDao = AppInfoDao.instance().open();
            list = appInfoDao.queryAll();
            return list;
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return null;
        } finally {
            if (appInfoDao != null) {
                appInfoDao.close();
            }
        }
    }

    /**
     * 获取appId对应的应用信息
     *
     * @param appId   要获取的应用信息对应的appId
     * @return 设置信息
     */
    public static AppInfoBean queryAppInfo(@Nullable String appId) {
        AppInfoBean appInfoBean;
        AppInfoDao appInfoDao = null;

        if (appId == null) {// add by ycm for lint 2017/02/13
            return null;
        }

        try {
            appInfoDao = AppInfoDao.instance().open();
            appInfoBean = appInfoDao.query(appId);
            return appInfoBean;
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return null;
        } finally {
            if (appInfoDao != null) {
                appInfoDao.close();
            }
        }
    }
}
