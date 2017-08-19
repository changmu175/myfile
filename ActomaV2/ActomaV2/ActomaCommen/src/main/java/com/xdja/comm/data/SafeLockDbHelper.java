package com.xdja.comm.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;


/**
 * Created by licong on 2017/1/2.
 */
public class SafeLockDbHelper extends SQLiteOpenHelper {

    private static  String DATABASE_NAME = "_safelock.db";

    private static final int DATABASE_VERSION = 4;

    private static SafeLockDbHelper instance;


    public SafeLockDbHelper() {
        super(ActomaController.getApp(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SafeLockDao.SQL_CREATE_TABLE_SETTING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public synchronized static SafeLockDbHelper getInstance(String account){
        int startIndex = DATABASE_NAME.indexOf("_");
        String prefix = DATABASE_NAME.substring(0,startIndex);
        if(!TextUtils.isEmpty(account) && account.equals(prefix)){
            LogUtil.getUtils().i("用户帐号一致--不再重新创建helper对象, account="+account);
            if(instance == null){
                instance = new SafeLockDbHelper();
            }
        }else{
            if(TextUtils.isEmpty(account)){
                LogUtil.getUtils().e("创建数据库，但是当前登录用户为空");
                throw new NullPointerException("创建数据库，但是当前登录用户为空");
            }else{
                LogUtil.getUtils().i("用户切换了帐号--重新创建数据库helper对象, account="+account+", prefix="+prefix);
                DATABASE_NAME = account + DATABASE_NAME.substring(startIndex , DATABASE_NAME.length());
                instance = new SafeLockDbHelper();
            }
        }
        return instance;
    }

}
