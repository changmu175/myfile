package com.xdja.data_mainframe.db.encrypt;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xdja.comm.server.ActomaController;


/**
 * Created by geyao on 2015/8/31.
 * 第三方加密数据库helper
 */
public class EncryptDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "encdec.db";

    private static final int DATABASE_VERSION = 2;


    public EncryptDBHelper() {
        super(ActomaController.getApp(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*第三方加密重构 新增第三方加密支持的应用列表*/
        sqLiteDatabase.execSQL(EncryptAppsDao.SQL_CREATE_TABLE_ENCRYPT_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                /*第三方加密重构 新增第三方加密支持的应用列表*/
                db.execSQL(EncryptAppsDao.SQL_CREATE_TABLE_ENCRYPT_LIST);
                break;
        }
    }
}