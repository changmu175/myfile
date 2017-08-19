package com.xdja.contact.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xdja.dependence.uitls.LogUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wanghao on 2015/10/26.
 */
@Deprecated
public final class DatabaseManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static DatabaseManager instance;

    private SQLiteOpenHelper mDatabaseHelper;

    private SQLiteDatabase mDatabase;

    private DatabaseManager(SQLiteOpenHelper helper) {
        mDatabaseHelper = helper;
    }

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager(helper);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            LogUtil.getUtils().e("Actoma contact DatabaseManager getInstance:未初始化, getInstance必须优先被调用.");
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " 未初始化, getInstance必须优先被调用.");
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();
        }
    }

    public SQLiteDatabase readableDatabase(){
        if (mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mDatabaseHelper.getReadableDatabase();
            //mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

}
