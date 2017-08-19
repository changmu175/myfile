package com.xdja.comm.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyao on 2015/7/25.
 * 应用市场-应用详情Dao
 */
public class AppInfoDao extends FrameDao {

    private static AppInfoDao instance;

    public static AppInfoDao instance() {
        if (instance == null) {
            instance = new AppInfoDao();
        }
        return instance;
    }

    private SQLiteDatabase database;

    private static final String TABLE_NAME = "t_app_info";

    public static final String FIELD_ID = "id";
    public static final String FIELD_APPID = "AppId";
    public static final String FIELD_PACKAGENAME = "packageName";
    public static final String FIELD_VERSIONNAME = "versionName";
    public static final String FIELD_VERSIONCODE = "versionCode";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_ISHAVEAPK = "isHaveApk";
    public static final String FIELD_ISDOWNNOW = "isDownNow";
    public static final String FIELD_DOWNSIZE = "downSize";
    public static final String FIELD_FILENAME = "fileName";
    public static final String FIELD_APPSIZE = "appSize";
    public static final String FIELD_DOWNLOADURL = "downloadUrl";

    @StringDef(value = {FIELD_ID, FIELD_APPID, FIELD_PACKAGENAME, FIELD_VERSIONNAME,
            FIELD_VERSIONCODE, FIELD_STATE, FIELD_ISHAVEAPK, FIELD_ISDOWNNOW, FIELD_DOWNSIZE,
            FIELD_FILENAME, FIELD_APPSIZE, FIELD_DOWNLOADURL})
    public @interface APPINFO_FIELD {
    }

    public static final String SQL_CREATE_TABLE_APPINFO =
            "CREATE TABLE " + TABLE_NAME
                    + " (" + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + FIELD_APPID + " TEXT,"
                    + FIELD_PACKAGENAME + " TEXT,"
                    + FIELD_VERSIONNAME + " TEXT,"
                    + FIELD_VERSIONCODE + " TEXT,"
                    + FIELD_STATE + " TEXT,"
                    + FIELD_ISHAVEAPK + " TEXT,"
                    + FIELD_ISDOWNNOW + " TEXT,"
                    + FIELD_DOWNSIZE + " TEXT,"
                    + FIELD_FILENAME + " TEXT,"
                    + FIELD_DOWNLOADURL + " TEXT,"
                    + FIELD_APPSIZE + " TEXT)";

    public AppInfoDao() {
        super();
    }

    @Override
    public synchronized AppInfoDao open() {
        super.open();
        database = helper.getWritableDatabase();
        return this;
    }

    /**
     * 将应用信息保存到数据库表中
     *
     * @param appInfoBean 要保存的应用信息对象
     * @return 插入状态
     */
    public synchronized boolean insert(@Nullable AppInfoBean appInfoBean) {
        if (appInfoBean == null || TextUtils.isEmpty(appInfoBean.getAppId())) {
            return false;
        }
        AppInfoBean bean = query(appInfoBean.getAppId());
        if (bean == null) {
            ContentValues values = new ContentValues();
            values.put(FIELD_APPID, appInfoBean.getAppId());
            values.put(FIELD_PACKAGENAME, appInfoBean.getPackageName());
            values.put(FIELD_VERSIONNAME, appInfoBean.getVersionName());
            values.put(FIELD_VERSIONCODE, appInfoBean.getVersionCode());
            values.put(FIELD_STATE, appInfoBean.getState());
            values.put(FIELD_DOWNLOADURL, appInfoBean.getDownloadUrl());
            values.put(FIELD_ISHAVEAPK, appInfoBean.getIsHaveApk());
            values.put(FIELD_ISDOWNNOW, appInfoBean.getIsDownNow());
            values.put(FIELD_DOWNSIZE, appInfoBean.getDownSize());
            values.put(FIELD_FILENAME, appInfoBean.getFileName());
            values.put(FIELD_APPSIZE, appInfoBean.getAppSize());
            database.insert(TABLE_NAME, null, values);
        } else {
            update(appInfoBean);
        }
        return true;
    }

    /**
     * 删除appid所对应的应用信息
     *
     * @param appId 要删除的应用信息的id
     * @return 删除结果
     */
    public synchronized boolean delete(@Nullable String appId) {
        if (appId == null || TextUtils.isEmpty(appId))
            return false;
        database.delete(TABLE_NAME, FIELD_APPID + "= ?",
                new String[]{appId});
        return true;
    }

    /**
     * 清空应用信息表
     *
     * @return 清空结果
     */
    public synchronized boolean clear() {
        database.execSQL("delete from " + TABLE_NAME);
        return true;
    }

    /**
     * 修改应用信息
     *
     * @param appInfoBean 将要修改的应用信息Bean
     * @return 修改结果
     */
    public synchronized boolean update(@Nullable AppInfoBean appInfoBean) {
        if (appInfoBean == null || TextUtils.isEmpty(appInfoBean.getAppId()))
            return false;
        ContentValues values = new ContentValues();
        values.put(FIELD_APPID, appInfoBean.getAppId());
        values.put(FIELD_PACKAGENAME, appInfoBean.getPackageName());
        values.put(FIELD_VERSIONNAME, appInfoBean.getVersionName());
        values.put(FIELD_VERSIONCODE, appInfoBean.getVersionCode());
        values.put(FIELD_STATE, appInfoBean.getState());
        values.put(FIELD_ISHAVEAPK, appInfoBean.getIsHaveApk());
        values.put(FIELD_ISDOWNNOW, appInfoBean.getIsDownNow());
        values.put(FIELD_DOWNSIZE, appInfoBean.getDownSize());
        values.put(FIELD_FILENAME, appInfoBean.getFileName());
        values.put(FIELD_APPSIZE, appInfoBean.getAppSize());
        values.put(FIELD_DOWNLOADURL, appInfoBean.getDownloadUrl());
        database.update(TABLE_NAME, values, FIELD_APPID + "= ?",
                new String[]{appInfoBean.getAppId()});
        return true;
    }

    /**
     * 更新应用的一个字段
     *
     * @param appId 应用id
     * @param field 字段名称
     * @param value 字段值
     * @return 是否成功
     */
    public synchronized boolean updateField(@NonNull String appId, @APPINFO_FIELD String field, @Nullable String value) {
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(field)) {
            return false;
        }
        AppInfoBean appInfoBean = query(appId);
        if (appInfoBean == null) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(field, value);
        database.update(TABLE_NAME, values, FIELD_APPID + "= ?", new String[]{appId});
        return true;
    }

    /**
     * 查询所有的应用信息数据集合
     *
     * @return 所有的应用信息数据集合
     */
    @Nullable
    public synchronized List<AppInfoBean> queryAll() {
        Cursor cursor  = null;
        if (helper == null)
            return null;
        try{
            cursor = database.query(TABLE_NAME,
                    null, null, null, null, null, null);
            if (cursor == null)
                return null;
            if (cursor.moveToFirst()) {
                List<AppInfoBean> list = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    String downSize = cursor.getString(cursor.getColumnIndex(FIELD_DOWNSIZE));
                    String appSize = cursor.getString(cursor.getColumnIndex(FIELD_APPSIZE));
                    //计算应用下载百分比
                    int percentage = (int) (Double.parseDouble(downSize) / Double.parseDouble(appSize) * 100);

                    AppInfoBean appInfoBean = new AppInfoBean(
                            cursor.getString(cursor.getColumnIndex(FIELD_APPID)),
                            cursor.getString(cursor.getColumnIndex(FIELD_DOWNLOADURL)),
                            cursor.getString(cursor.getColumnIndex(FIELD_PACKAGENAME)),
                            cursor.getString(cursor.getColumnIndex(FIELD_VERSIONNAME)),
                            cursor.getString(cursor.getColumnIndex(FIELD_VERSIONCODE)),
                            cursor.getString(cursor.getColumnIndex(FIELD_STATE)),
                            cursor.getString(cursor.getColumnIndex(FIELD_ISHAVEAPK)),
                            cursor.getString(cursor.getColumnIndex(FIELD_ISDOWNNOW)),
                            downSize,
                            cursor.getString(cursor.getColumnIndex(FIELD_FILENAME)),
                            appSize,
                            String.valueOf(percentage));
                    list.add(appInfoBean);
                    cursor.moveToNext();
                }
                return list;
            }
        }catch (Exception e){
            LogUtil.getUtils().e("AppInfoDao queryAll exception:"+e.getMessage());
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询数据库中对应appid的应用信息数据
     *
     * @param appId 要查询的应用在服务器端的应用id
     * @return 对应appid的应用信息数据
     */
    @Nullable
    public synchronized AppInfoBean query(@NonNull String appId) {
        AppInfoBean appInfoBean = null;
        Cursor cursor = null;
        if (helper == null) {
            return null;
        }
        try{
            cursor = database.query(
                    TABLE_NAME,
                    null,
                    FIELD_APPID + "= ?",
                    new String[]{appId}, null, null, null);
            if (cursor == null) {
                return null;
            } else {
                if (cursor.moveToFirst()) {
                    String downSize = cursor.getString(cursor.getColumnIndex(FIELD_DOWNSIZE));
                    String appSize = cursor.getString(cursor.getColumnIndex(FIELD_APPSIZE));
                    //计算应用下载百分比
                    int percentage = (int) (Double.parseDouble(downSize) / Double.parseDouble(appSize) * 100);
                    appInfoBean = new AppInfoBean(
                            cursor.getString(cursor.getColumnIndex(FIELD_APPID)),
                            cursor.getString(cursor.getColumnIndex(FIELD_DOWNLOADURL)),
                            cursor.getString(cursor.getColumnIndex(FIELD_PACKAGENAME)),
                            cursor.getString(cursor.getColumnIndex(FIELD_VERSIONNAME)),
                            cursor.getString(cursor.getColumnIndex(FIELD_VERSIONCODE)),
                            cursor.getString(cursor.getColumnIndex(FIELD_STATE)),
                            cursor.getString(cursor.getColumnIndex(FIELD_ISHAVEAPK)),
                            cursor.getString(cursor.getColumnIndex(FIELD_ISDOWNNOW)),
                            downSize,
                            cursor.getString(cursor.getColumnIndex(FIELD_FILENAME)),
                            appSize,
                            String.valueOf(percentage));
                }
                return appInfoBean;
            }
        }catch (Exception e){
            LogUtil.getUtils().e("AppInfoDao query exception:"+e.getMessage());
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return null;
    }
}
