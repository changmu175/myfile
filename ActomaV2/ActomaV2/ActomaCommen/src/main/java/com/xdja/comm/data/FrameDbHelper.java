package com.xdja.comm.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xdja.comm.server.ActomaController;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.data</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/7</p>
 * <p>Time:20:02</p>
 */
public class FrameDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "atframe.db";

    private static final int DATABASE_VERSION = 4;


    public FrameDbHelper() {
        super(ActomaController.getApp(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AccountDao.SQL_CREATE_TABLE_ACCOUNT);
        sqLiteDatabase.execSQL(SettingDao.SQL_CREATE_TABLE_SETTING);
        sqLiteDatabase.execSQL(AppInfoDao.SQL_CREATE_TABLE_APPINFO);
//        /*新版本添加新表-快速开启第三方应用表*/
//        upGrade_1_2(sqLiteDatabase);

        //添加日志缓存表
        upGrade_2_3(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
//                upGrade_1_2(sqLiteDatabase);
                upGrade_2_3(sqLiteDatabase);
                upGrade_3_4(sqLiteDatabase);
                break;
            case 2:
                upGrade_2_3(sqLiteDatabase);
                upGrade_3_4(sqLiteDatabase);
                break;
            case 3:
                upGrade_3_4(sqLiteDatabase);
                break;
        }
    }

//    /**
//     * 数据库版本从1升到2
//     * @param sqLiteDatabase
//     */
//    private void upGrade_1_2(SQLiteDatabase sqLiteDatabase){
//       sqLiteDatabase.execSQL(QuickOpenAppDao.SQL_CREATE_TABLE_QUICK_OPEN_OTHER_APP);
//    }

    /**
     * 数据库版本从2升到3
     * @param sqLiteDatabase
     */
    private void upGrade_2_3(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(AppLogDao.SQL_CREATE_TABLE_SETTING);
    }

    /**
     * 数据库版本从3升到4，t_account表添加alias字段
     * @param db
     */
    private void upGrade_3_4(SQLiteDatabase db) {
        if (!checkColumnExists(db, AccountDao.TABLE_NAME, AccountDao.FIELD_ALIAS)) {
            db.execSQL(AccountDao.SQL_UPDATE_4_FROM_3);
        }
    }

    private boolean checkColumnExists(SQLiteDatabase db, String table, String column) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?",
                    new String[]{table, "%"+column+"%"});
            result = (cursor != null && cursor.moveToFirst());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

}
