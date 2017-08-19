package com.xdja.imsdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xdja.imsdk.logger.Logger;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：ImSdk数据库实例   <br>
 * 创建时间：2016/11/18 16:08  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class DbHelper {
    private static final String DATABASE_NAME = "imsdk.db";
    private static DbHelper dbHelper;
    private ImSdkDatabaseHelper imSdkDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    /**
     * 获取一个数据库帮助类单例
     * @return 数据库帮助类的实例对象
     */
    public static DbHelper getInstance() {
        if (dbHelper == null) {
            synchronized (DbHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DbHelper();
                }
            }
        }
        return dbHelper;
    }

    /**
     * 构造方法
     */
    private DbHelper() {

    }

    public void initDatabase(Context context, String account) {
        if(context == null || TextUtils.isEmpty(account) ){
            throw new RuntimeException("database init exception...");
        }
        Logger.getLogger().i("init database...");
        this.imSdkDatabaseHelper = new ImSdkDatabaseHelper(new ImSdkContext(context, account), DATABASE_NAME);
        this.imSdkDatabaseHelper.setAccount(account);
    }

    public synchronized SQLiteDatabase getDatabase() {
        if (imSdkDatabaseHelper == null) {
            throw new RuntimeException("database already closed...");
        }

        if (sqLiteDatabase == null) {
            sqLiteDatabase = imSdkDatabaseHelper.getReadableDatabase();
        }
        return sqLiteDatabase;
    }

    public synchronized void close() {
        Logger.getLogger().i("close database...");
        if (dbHelper != null && imSdkDatabaseHelper != null) {
            imSdkDatabaseHelper.close();
        }
        imSdkDatabaseHelper = null;
        dbHelper = null;
    }
}
