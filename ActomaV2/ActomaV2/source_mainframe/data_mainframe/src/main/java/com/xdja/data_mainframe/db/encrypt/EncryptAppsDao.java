package com.xdja.data_mainframe.db.encrypt;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.xdja.comm.encrypt.EncryptAppBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyao on 2015/8/31.
 * 重构-第三方加密应用列表数据库操作dao
 */
public class EncryptAppsDao extends EncryptFrameDao {

    private static EncryptAppsDao instance;

    public static EncryptAppsDao instance() {
        if (instance == null) {
            instance = new EncryptAppsDao();
        }
        return instance;
    }

    private SQLiteDatabase database;

    private static final String TABLE_NAME = "t_encrypt_list";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_APPNAME = "appName";
    public static final String FIELD_PACKAGENAME = "packageName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_SUPPORTTYPE = "supportType";
    public static final String FIELD_SUPPORTVERTION = "supportVertion";
    public static final String FIELD_ISOPENENC = "isOpenEnc";

    @StringDef(value = {FIELD_APPNAME, FIELD_PACKAGENAME, FIELD_DESCRIPTION, FIELD_SUPPORTTYPE,
            FIELD_SUPPORTVERTION, FIELD_ISOPENENC})
    public @interface ENCDEC_FIELD {
    }

    public static final String SQL_CREATE_TABLE_ENCRYPT_LIST =
            "CREATE TABLE " + TABLE_NAME
                    + " (" + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + FIELD_APPNAME + " TEXT,"
                    + FIELD_PACKAGENAME + " TEXT,"
                    + FIELD_DESCRIPTION + " TEXT,"
                    + FIELD_SUPPORTTYPE + " TEXT,"
                    + FIELD_SUPPORTVERTION + " TEXT,"
                    + FIELD_ISOPENENC + " TEXT)";

    public EncryptAppsDao() {
        super();
    }

    @Override
    public synchronized EncryptAppsDao open() {
        super.open();
        database = helper.getWritableDatabase();
        return this;
    }


    /**
     * 查询数据库中对应条件的数据
     *
     * @param field     条件字段
     * @param condition 条件
     * @return 对应的结果数据
     */
    @Nullable
    public synchronized EncryptAppBean query(@ENCDEC_FIELD String field,
                                             @NonNull String condition) {
        if (helper == null) {
            return null;
        }
        Cursor cursor = database.query(
                TABLE_NAME,
                null,
                field + "= ?",
                new String[]{condition}, null, null, null);
        if (cursor == null) {
            return null;
        } else {
            if (cursor.moveToFirst()) {
                EncryptAppBean result = new EncryptAppBean();
                result.setAppName(cursor.getString(cursor.getColumnIndex(FIELD_APPNAME)));
                result.setPackageName(cursor.getString(cursor.getColumnIndex(FIELD_PACKAGENAME)));
                result.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
                result.setSupportType(cursor.getString(cursor.getColumnIndex(FIELD_SUPPORTTYPE)));
                result.setSupportVertion(cursor.getString(cursor.getColumnIndex(FIELD_SUPPORTVERTION)));
                result.setOpen(Boolean.parseBoolean(
                        cursor.getString(cursor.getColumnIndex(FIELD_ISOPENENC))));
                cursor.close();
                return result;
            } else {
                return null;
            }
        }
    }

    /**
     * 将支持的应用信息类插入到表中
     *
     * @param bean 数据对象
     * @return 执行结果
     */
    public synchronized boolean insert(@Nullable EncryptAppBean bean) {
        if (bean == null) {
            return false;
        }
        EncryptAppBean result = query(FIELD_PACKAGENAME, bean.getPackageName());
        if (result == null) {
            ContentValues values = new ContentValues();
            values.put(FIELD_APPNAME, bean.getAppName());
            values.put(FIELD_PACKAGENAME, bean.getPackageName());
            values.put(FIELD_DESCRIPTION, bean.getDescription());
            values.put(FIELD_SUPPORTTYPE, bean.getSupportType());
            values.put(FIELD_SUPPORTVERTION, bean.getSupportVertion());
            values.put(FIELD_ISOPENENC, String.valueOf(bean.isOpen()));
            database.insert(TABLE_NAME, null, values);
        } else {
            bean.setOpen(result.isOpen());
            update(FIELD_PACKAGENAME, bean.getPackageName(), bean);
        }
        return true;
    }

    /**
     * 修改支持的应用信息
     *
     * @param field     条件字段
     * @param condition 条件值
     * @param bean      数据对象
     * @return 修改结果
     */
    public synchronized boolean update(@ENCDEC_FIELD String field,
                                       @Nullable String condition,
                                       @Nullable EncryptAppBean bean) {
        if (bean == null)
            return false;
        ContentValues values = new ContentValues();
        values.put(FIELD_APPNAME, bean.getAppName());
        values.put(FIELD_PACKAGENAME, bean.getPackageName());
        values.put(FIELD_DESCRIPTION, bean.getDescription());
        values.put(FIELD_SUPPORTTYPE, bean.getSupportType());
        values.put(FIELD_SUPPORTVERTION, bean.getSupportVertion());
        values.put(FIELD_ISOPENENC, String.valueOf(bean.isOpen()));
        database.update(TABLE_NAME, values, field + "= ?",
                new String[]{condition});
        return true;
    }

    /**
     * 删除packageName所对应的支持的应用信息
     *
     * @param field     条件字段
     * @param condition 条件值
     * @return 删除结果
     */
    public synchronized boolean delete(@ENCDEC_FIELD String field, @Nullable String condition) {
        if (TextUtils.isEmpty(condition))
            return false;
        database.delete(TABLE_NAME, field + "= ?",
                new String[]{condition});
        return true;
    }

    /**
     * 清空支持的应用信息表
     *
     * @return 清空结果
     */
    public synchronized boolean clear() {
        database.execSQL("delete from " + TABLE_NAME);
        return true;
    }


    /**
     * 查询所有的支持的应用信息数据集合
     *
     * @return 所有的支持的应用信息数据集合
     */
    @Nullable
    public synchronized List<EncryptAppBean> queryAll() {
        if (helper == null)
            return null;
        Cursor cursor = database.query(TABLE_NAME,
                null, null, null, null, null, null);
        if (cursor == null)
            return null;
        if (cursor.moveToFirst()) {
            List<EncryptAppBean> list = new ArrayList<>();
            EncryptAppBean bean;
            while (!cursor.isAfterLast()) {
                bean = new EncryptAppBean();
                bean.setAppName(cursor.getString(cursor.getColumnIndex(FIELD_APPNAME)));
                bean.setPackageName(cursor.getString(cursor.getColumnIndex(FIELD_PACKAGENAME)));
                bean.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
                bean.setSupportType(cursor.getString(cursor.getColumnIndex(FIELD_SUPPORTTYPE)));
                bean.setSupportVertion(cursor.getString(cursor.getColumnIndex(FIELD_SUPPORTVERTION)));
                String isOpen = cursor.getString(cursor.getColumnIndex(FIELD_ISOPENENC));
                bean.setOpen(Boolean.parseBoolean(isOpen == null ? "true" : isOpen));
                list.add(bean);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        }
        return null;
    }
}
