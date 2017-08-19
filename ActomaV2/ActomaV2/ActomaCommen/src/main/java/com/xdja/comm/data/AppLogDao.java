package com.xdja.comm.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;


/**
 * Created by xrj on 2015-12-24 11:48:10
 * 系统日志Dao
 */
public class AppLogDao extends FrameDao {

    private static AppLogDao instance;

    public static AppLogDao instance() {
        if (instance == null) {
            instance = new AppLogDao();
        }
        return instance;
    }

    private SQLiteDatabase database;

    /**
     * 数据库表名
     */
    private static final String TABLE_NAME = "t_appLog";
    /**
     * _id
     */
    public static final String FIELD_ID = "_id";
    /**
     * 应用标识
     */
    public static final String FIELD_APP_ID = "appId";
    /**
     * 应用版本
     */
    public static final String FIELD_APP_VERSION = "appVersion";
    /**
     * 操作系统类型
     */
    public static final String FIELD_OS_TYPE = "osType";
    /**
     * 设备名称
     */
    public static final String FIELD_DEVICE_NAME = "deviceName";
    /**
     * 设备IMEI
     */
    public static final String FIELD_IMEI = "imei";
    /**
     * 操作系统版本
     */
    public static final String FIELD_OS_VERSION = "osVersion";
    /**
     * 账户名
     */
    public static final String FIELD_ACCOUNT = "account";
    /**
     * 卡ID
     */
    public static final String FIELD_CARD_ID = "cardId";
    /**
     * 模块名称
     */
    public static final String FIELD_APP_MOUDULE = "appModule";
    /**
     * 网络类型
     */
    public static final String FIELD_NET_TYPE = "netType";
    /**
     * 日志级别
     */
    public static final String FIELD_LEVEL = "level";
    /**
     * 日志内容
     */
    public static final String FIELD_CONTENT = "content";
    /**
     * 日志代码
     */
    public static final String FIELD_LOG_CODE = "logCode";
    /**
     * 日志发生时间时间戳
     */
    public static final String FIELD_CRASH_TIME = "crashTime";

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


    public static final String SQL_CREATE_TABLE_SETTING =
            "CREATE TABLE " + TABLE_NAME
                    + " (" + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + FIELD_APP_ID + " TEXT,"
                    + FIELD_APP_VERSION + " TEXT,"
                    + FIELD_OS_TYPE + " TEXT,"
                    + FIELD_DEVICE_NAME + " TEXT,"
                    + FIELD_IMEI + " TEXT,"
                    + FIELD_OS_VERSION + " TEXT,"
                    + FIELD_ACCOUNT + " TEXT,"
                    + FIELD_CARD_ID + " TEXT,"
                    + FIELD_APP_MOUDULE + " TEXT,"
                    + FIELD_NET_TYPE + " TEXT,"
                    + FIELD_LEVEL + " TEXT,"
                    + FIELD_CONTENT + " TEXT,"
                    + FIELD_LOG_CODE + " TEXT,"
                    + FIELD_CRASH_TIME + " TEXT,"
                    + PREPARE_COLUMN1 + " TEXT,"
                    + PREPARE_COLUMN2 + " TEXT,"
                    + PREPARE_COLUMN3 + " TEXT,"
                    + PREPARE_COLUMN4 + " TEXT,"
                    + PREPARE_COLUMN5 + " TEXT)";

    public AppLogDao() {
        super();
    }

    @Override
    public synchronized AppLogDao open() {
        super.open();
        database = helper.getWritableDatabase();
        return this;
    }

    /**
     * 将设置信息保存到数据库设置表中
     *
     * @return 插入状态
     */
    public synchronized long insert(@Nullable UpLoadLogRequestBean upLoadLogRequestBean) {
        long insertResult = -1;
        ContentValues values = new ContentValues();

        if (upLoadLogRequestBean == null) {// add by ycm for lint 2017/02/13
            return insertResult;
        }

        //设备和应用信息
        values.put(FIELD_APP_ID, upLoadLogRequestBean.getAppId());
        values.put(FIELD_APP_VERSION, upLoadLogRequestBean.getAppVersion());
        values.put(FIELD_OS_TYPE, upLoadLogRequestBean.getOsType());
        values.put(FIELD_DEVICE_NAME, upLoadLogRequestBean.getDeviceName());
        values.put(FIELD_IMEI, upLoadLogRequestBean.getImei());
        values.put(FIELD_OS_VERSION, upLoadLogRequestBean.getOsVersion());
        values.put(FIELD_CARD_ID, upLoadLogRequestBean.getCardId());

        //账户名称
        values.put(FIELD_ACCOUNT, upLoadLogRequestBean.getAccount());

        //日志信息
        if (upLoadLogRequestBean.getLogList() != null && upLoadLogRequestBean.getLogList().size() > 0) {
            LogInfoBean logInfoBean = upLoadLogRequestBean.getLogList().get(0);
            values.put(FIELD_APP_MOUDULE, logInfoBean.getAppModule());
            values.put(FIELD_NET_TYPE, logInfoBean.getNetType());
            values.put(FIELD_LEVEL, logInfoBean.getLevel());
            values.put(FIELD_CONTENT, logInfoBean.getContent());
            values.put(FIELD_LOG_CODE, logInfoBean.getLogCode());
            values.put(FIELD_CRASH_TIME, logInfoBean.getCrashTime());
        }

        try {
            if (database == null) {
                open();
            }
            insertResult = database.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return insertResult;
        }
        return insertResult;
    }

    /**
     * 删除key所对应的信息
     *
     * @param id 要删除的记录Id
     * @return 删除结果
     */
    public synchronized boolean delete(long id) {
        String sql = "delete from " + TABLE_NAME
                + " where " + FIELD_ID + " = '" + id + "'";

        try {
            if (database == null) {
                open();
            }
            database.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
//            Log.d("Test", "AppLogDao");
//            Log.d("Test", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 删除key所对应的信息
     *
     * @param idList 要删除的记录Id集合
     * @return 删除结果
     */
    /*public synchronized boolean delete(@Nullable ArrayList<String> idList) {
        if (idList == null || idList.size() == 0)
            return false;
        String sql = "delete from " + TABLE_NAME
                + " where " + FIELD_ID + " in(";
        boolean isFirst = true;
        for (String id : idList) {
            if (!isFirst) {
                sql += ",";
            } else {
                isFirst = false;
            }

            sql += "'" + id + "'";
        }
        sql += ")";

        try {
            if (database == null) {
                open();
            }
            database.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
//            Log.d("Test", "AppLogDao");
//            Log.d("Test", e.getMessage());
            return false;
        }
        return true;
    }*/

    /**
     * 查询key对应的设置信息
     *
     * @return 日志信息集合
     */
   /* @Nullable
    public synchronized UpLoadLogsBean queryLogs() {
        try {
            if (database == null) {
                open();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selectTopSql = "select  * from " + TABLE_NAME + " limit 0,1 ";
        Cursor cursor = database.rawQuery(selectTopSql, null);
        if (cursor == null) {
            return null;
        } else {
            UpLoadLogsBean upLoadLogsBean = new UpLoadLogsBean();
            UpLoadLogRequestBean logRequestBean = new UpLoadLogRequestBean();
            ArrayList<String> idList = null;
            if (cursor.moveToFirst()) {
                logRequestBean.setAppId(cursor.getString(cursor.getColumnIndex(FIELD_APP_ID)));
                logRequestBean.setAppVersion(cursor.getString(cursor.getColumnIndex(FIELD_APP_VERSION)));
                logRequestBean.setOsType(cursor.getInt(cursor.getColumnIndex(FIELD_OS_TYPE)));
                logRequestBean.setDeviceName(cursor.getString(cursor.getColumnIndex(FIELD_DEVICE_NAME)));
                logRequestBean.setImei(cursor.getString(cursor.getColumnIndex(FIELD_IMEI)));
                logRequestBean.setOsVersion(cursor.getString(cursor.getColumnIndex(FIELD_OS_VERSION)));
                logRequestBean.setCardId(cursor.getString(cursor.getColumnIndex(FIELD_CARD_ID)));
                logRequestBean.setAccount(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT)));
            }
            cursor.close();
            cursor = null;

            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("select ");
            sqlBuffer.append(FIELD_ID).append(" , ");
            sqlBuffer.append(FIELD_APP_MOUDULE).append(" , ");
            sqlBuffer.append(FIELD_NET_TYPE).append(" , ");
            sqlBuffer.append(FIELD_LEVEL).append(" , ");
            sqlBuffer.append(FIELD_CONTENT).append(" , ");
            sqlBuffer.append(FIELD_LOG_CODE).append(" , ");
            sqlBuffer.append(FIELD_CRASH_TIME);
            sqlBuffer.append(" from ").append(TABLE_NAME);
            sqlBuffer.append(" where ");
            sqlBuffer.append(FIELD_APP_ID).append(" = '").append(logRequestBean.getAppId()).append("'");
            sqlBuffer.append(" and ").append(FIELD_APP_VERSION).append(" = '").append(logRequestBean.getAppVersion()).append("'");
            sqlBuffer.append(" and ").append(FIELD_OS_TYPE).append(" = '").append(logRequestBean.getOsType()).append("'");
            sqlBuffer.append(" and ").append(FIELD_DEVICE_NAME).append(" = '").append(logRequestBean.getDeviceName()).append("'");
            sqlBuffer.append(" and ").append(FIELD_IMEI).append(" = '").append(logRequestBean.getImei()).append("'");
            sqlBuffer.append(" and ").append(FIELD_OS_VERSION).append(" = '").append(logRequestBean.getOsVersion()).append("'");
            sqlBuffer.append(" and ").append(FIELD_CARD_ID).append(" = '").append(logRequestBean.getCardId()).append("'");
            sqlBuffer.append(" and ").append(FIELD_ACCOUNT).append(" = '").append(logRequestBean.getAccount()).append("'");
            sqlBuffer.append(" limit 0,100 ");
            cursor = database.rawQuery(sqlBuffer.toString(), null);
            if (cursor != null) {
                idList = new ArrayList<>();

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    LogInfoBean logInfoBean = new LogInfoBean();
                    logInfoBean.setAppModule(cursor.getString(cursor.getColumnIndex(FIELD_APP_MOUDULE)));
                    logInfoBean.setNetType(cursor.getInt(cursor.getColumnIndex(FIELD_NET_TYPE)));
                    logInfoBean.setLevel(cursor.getInt(cursor.getColumnIndex(FIELD_LEVEL)));
                    logInfoBean.setContent(cursor.getString(cursor.getColumnIndex(FIELD_CONTENT)));
                    logInfoBean.setLogCode(cursor.getInt(cursor.getColumnIndex(FIELD_LOG_CODE)));
                    logInfoBean.setCrashTime(cursor.getLong(cursor.getColumnIndex(FIELD_CRASH_TIME)));

                    logRequestBean.addLogInfo(logInfoBean);

                    idList.add(cursor.getString(cursor.getColumnIndex(FIELD_ID)));
                    cursor.moveToNext();
                }

                cursor.close();
                cursor = null;
            }

            upLoadLogsBean.setUpLoadLogRequestBean(logRequestBean);
            upLoadLogsBean.setIdList(idList);
            return upLoadLogsBean;
        }
    }*/
}
