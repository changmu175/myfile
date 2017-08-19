package com.xdja.comm.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyao on 2015/7/17.
 * 系统设置Dao
 */
public class SettingDao extends FrameDao {

    private static SettingDao instance;

    public static SettingDao instance() {
        if (instance == null) {
            instance = new SettingDao();
        }
        return instance;
    }

    private SQLiteDatabase database;

    /**
     * 数据库表名
     */
    private static final String TABLE_NAME = "t_setting";
    /**
     * id
     */
    public static final String FIELD_ID = "id";
    /**
     * 设置表key
     */
    public static final String FIELD_KEY = "setting_key";
    /**
     * 设置表value
     */
    public static final String FIELD_VALUE = "setting_value";

    /**
     * =================预留扩展字段==================
     */
    public static final String PREPARE_COLUMN1 = "preparecolumn1";
    public static final String PREPARE_COLUMN2 = "preparecolumn2";
    public static final String PREPARE_COLUMN3 = "preparecolumn3";
    public static final String PREPARE_COLUMN4 = "preparecolumn4";
    public static final String PREPARE_COLUMN5 = "preparecolumn5";

    /**
     * =================预留扩展字段==================
     */


    @StringDef(value = {FIELD_ID, FIELD_KEY, FIELD_VALUE, PREPARE_COLUMN1, PREPARE_COLUMN2,
            PREPARE_COLUMN3, PREPARE_COLUMN4, PREPARE_COLUMN5})
    public @interface SETTING_FIELD {
    }

    public static final String SQL_CREATE_TABLE_SETTING =
            "CREATE TABLE " + TABLE_NAME
                    + " (" + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + FIELD_KEY + " TEXT,"
                    + FIELD_VALUE + " TEXT,"
                    + PREPARE_COLUMN1 + " TEXT,"
                    + PREPARE_COLUMN2 + " TEXT,"
                    + PREPARE_COLUMN3 + " TEXT,"
                    + PREPARE_COLUMN4 + " TEXT,"
                    + PREPARE_COLUMN5 + " TEXT)";

    public SettingDao() {
        super();
    }

    @Override
    public synchronized SettingDao open() {
        super.open();
        database = helper.getWritableDatabase();
        return this;
    }

    /**
     * 将设置信息保存到数据库设置表中
     *
     * @param settingBean 将保存的设置Bean
     * @return 插入状态
     */
    public synchronized boolean insert(@Nullable SettingBean settingBean) {
        if (settingBean == null || TextUtils.isEmpty(settingBean.getKey())) {
            return false;
        }
        SettingBean bean = query(settingBean.getKey());
        if (bean == null) {
            ContentValues values = new ContentValues();
            values.put(FIELD_KEY, settingBean.getKey());
            values.put(FIELD_VALUE, settingBean.getValue());
            values.put(PREPARE_COLUMN1, settingBean.getColumn1());
            values.put(PREPARE_COLUMN2, settingBean.getColumn2());
            values.put(PREPARE_COLUMN3, settingBean.getColumn3());
            values.put(PREPARE_COLUMN4, settingBean.getColumn4());
            values.put(PREPARE_COLUMN5, settingBean.getColumn5());
            database.insert(TABLE_NAME, null, values);
        } else {
            update(settingBean);
        }
        return true;
    }

    /**
     * 删除key所对应的信息
     *
     * @param key 要删除的key
     * @return 删除结果
     */
    public synchronized boolean delete(@Nullable String key) {
        if (key == null || TextUtils.isEmpty(key))
            return false;
        database.delete(TABLE_NAME, FIELD_KEY + "= ?",
                new String[]{key});
        return true;
    }

    /**
     * 修改设置
     *
     * @param settingBean 将要修改的设置Bean
     * @return 修改结果
     */
    public synchronized boolean update(@Nullable SettingBean settingBean) {
        if (settingBean == null || TextUtils.isEmpty(settingBean.getKey()))
            return false;
        ContentValues values = new ContentValues();
        values.put(FIELD_KEY, settingBean.getKey());
        values.put(FIELD_VALUE, settingBean.getValue());
        values.put(PREPARE_COLUMN1, settingBean.getColumn1());
        values.put(PREPARE_COLUMN2, settingBean.getColumn2());
        values.put(PREPARE_COLUMN3, settingBean.getColumn3());
        values.put(PREPARE_COLUMN4, settingBean.getColumn4());
        values.put(PREPARE_COLUMN5, settingBean.getColumn5());
        database.update(TABLE_NAME, values, FIELD_KEY + "= ?",
                new String[]{settingBean.getKey()});
        return true;
    }

    /**
     * 查询key对应的设置信息
     *
     * @param key 要查询的key
     * @return 设置信息集合
     */
    @Nullable
    public synchronized SettingBean query(@Nullable String key) {
        SettingBean settingbean = null;
        if (helper == null) {
            return null;
        }
        Cursor cursor = database.query(
                TABLE_NAME,
                null,
                FIELD_KEY + "= ?",
                new String[]{key}, null, null, null);
        if (cursor == null) {
            return null;
        } else {
            if (cursor.moveToFirst()) {
                settingbean = new SettingBean();
                settingbean.setKey(cursor.getString(cursor.getColumnIndex(FIELD_KEY)));
                settingbean.setValue(cursor.getString(cursor.getColumnIndex(FIELD_VALUE)));
                settingbean.setColumn1(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN1)));
                settingbean.setColumn2(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN2)));
                settingbean.setColumn3(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN3)));
                settingbean.setColumn4(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN4)));
                settingbean.setColumn5(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN5)));
            }

            cursor.close();
            cursor = null;
            return settingbean;
        }
    }

    /**
     * 查询所有设置的信息
     *
     * @return 设置信息集合
     */
    @Nullable
    public synchronized List<SettingBean> queryAll() {
        if (helper == null)
            return null;
        Cursor cursor = database.query(
                TABLE_NAME,
                null,
                null, null, null, null, null);
        if (cursor == null) {
            return null;
        } else {
            if (cursor.moveToFirst()) {
                List<SettingBean> settingbeans = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    SettingBean settingbean = new SettingBean();
                    settingbean.setKey(cursor.getString(cursor.getColumnIndex(FIELD_KEY)));
                    settingbean.setValue(cursor.getString(cursor.getColumnIndex(FIELD_VALUE)));
                    settingbean.setColumn1(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN1)));
                    settingbean.setColumn2(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN2)));
                    settingbean.setColumn3(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN3)));
                    settingbean.setColumn4(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN4)));
                    settingbean.setColumn5(cursor.getString(cursor.getColumnIndex(PREPARE_COLUMN5)));
                    settingbeans.add(settingbean);
                    cursor.moveToNext();
                }
                cursor.close();
                cursor = null;
                return settingbeans;
            }

            cursor.close();
            cursor = null;
        }
        return null;
    }
}
