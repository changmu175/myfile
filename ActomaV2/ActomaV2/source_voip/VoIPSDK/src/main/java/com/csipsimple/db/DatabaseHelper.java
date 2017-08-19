/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 * <p/>
 * CSipSimple is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * If you own a pjsip commercial license you can also redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License
 * as an android library.
 * <p/>
 * CSipSimple is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.csipsimple.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CallLog;
import android.util.Log;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.models.Filter;
import com.csipsimple.models.MissedCall;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.util.ContactUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Modify by mb on 2016-07-26.
 * 加入openWritableDatabase()、closeDatabase()，统计打开、关闭数据库操作，避免异步数据库打开关闭crash。
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public final String TAG = DatabaseHelper.class.getName();
    private static final int DATABASE_VERSION = 43; // database version +1

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static String mDatabaseName = "";
    private static DatabaseHelper mInstance;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if(mInstance == null){
            initDatabaseHelper(context, mDatabaseName);
        }
        return mInstance;
    }

    public static void initDatabaseHelper(Context context, String account){

        // 解决account可能传入null的crash 根源是获取account方法有问题
        // mengbo@xdja.com 2016-08-03 add. fix bug 2383
        if(account == null || account.equals("")){
            account = ContactUtils.getCurrentAccount();
        }
        // mengbo@xdja.com 2016-08-03 add. fix bug 2383

        //20170224-mengbo : 动态获取URI
        //String databaseName = account + "_" + SipManager.AUTHORITY;
        String databaseName = account + "_" + DBProvider.getAuthority(context);
        setVoipDatabaseName(databaseName);
        mInstance = new DatabaseHelper(context, databaseName);
    }

    public synchronized SQLiteDatabase openWritableDatabase(){
        if(mOpenCounter.incrementAndGet() == 1){
            Log.d("mb","Open voip database");
            mDatabase = mInstance.getWritableDatabase();
        }

        if(mDatabase == null){
            Log.d("mb","Open voip database");
            mDatabase = mInstance.getWritableDatabase();
            mOpenCounter.set(1);
        }

        Log.d("mb","open voip database current count:" + mOpenCounter.get());

        return mDatabase;
    }

    public synchronized void closeDatabase(){
        if(mOpenCounter.decrementAndGet() == 0 && mDatabase != null && mDatabase.isOpen()){
            Log.d("mb","Close voip database");
            mDatabase.close();
        }

        Log.d("mb","close voip database current count:" + mOpenCounter.get());
    }

    // Creation sql command
    private static final String TABLE_ACCOUNT_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SipProfile.ACCOUNTS_TABLE_NAME
            + " ("
            + SipProfile.FIELD_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"

            // Application relative fields
            + SipProfile.FIELD_ACTIVE
            + " INTEGER,"
            + SipProfile.FIELD_WIZARD
            + " TEXT,"
            + SipProfile.FIELD_DISPLAY_NAME
            + " TEXT,"

            // Here comes pjsua_acc_config fields
            + SipProfile.FIELD_PRIORITY
            + " INTEGER,"
            + SipProfile.FIELD_ACC_ID
            + " TEXT NOT NULL,"
            + SipProfile.FIELD_REG_URI
            + " TEXT,"
            + SipProfile.FIELD_MWI_ENABLED
            + " BOOLEAN,"
            + SipProfile.FIELD_PUBLISH_ENABLED
            + " INTEGER,"
            + SipProfile.FIELD_REG_TIMEOUT
            + " INTEGER,"
            + SipProfile.FIELD_KA_INTERVAL
            + " INTEGER,"
            + SipProfile.FIELD_PIDF_TUPLE_ID
            + " TEXT,"
            + SipProfile.FIELD_FORCE_CONTACT
            + " TEXT,"
            + SipProfile.FIELD_ALLOW_CONTACT_REWRITE
            + " INTEGER,"
            + SipProfile.FIELD_CONTACT_REWRITE_METHOD
            + " INTEGER,"
            + SipProfile.FIELD_CONTACT_PARAMS
            + " TEXT,"
            + SipProfile.FIELD_CONTACT_URI_PARAMS
            + " TEXT,"
            + SipProfile.FIELD_TRANSPORT
            + " INTEGER,"
            + SipProfile.FIELD_DEFAULT_URI_SCHEME
            + " TEXT,"
            + SipProfile.FIELD_USE_SRTP
            + " INTEGER,"
            + SipProfile.FIELD_USE_ZRTP
            + " INTEGER,"

            // Proxy infos
            + SipProfile.FIELD_PROXY
            + " TEXT,"
            + SipProfile.FIELD_REG_USE_PROXY
            + " INTEGER,"

            // And now cred_info since for now only one cred info can be managed
            // In future release a credential table should be created
            + SipProfile.FIELD_REALM
            + " TEXT,"
            + SipProfile.FIELD_SCHEME
            + " TEXT,"
            + SipProfile.FIELD_USERNAME
            + " TEXT,"
            + SipProfile.FIELD_DATATYPE
            + " INTEGER,"
            + SipProfile.FIELD_DATA
            + " TEXT,"
            + SipProfile.FIELD_AUTH_INITIAL_AUTH
            + " INTEGER,"
            + SipProfile.FIELD_AUTH_ALGO
            + " TEXT,"

            + SipProfile.FIELD_SIP_STACK
            + " INTEGER,"
            + SipProfile.FIELD_VOICE_MAIL_NBR
            + " TEXT,"
            + SipProfile.FIELD_REG_DELAY_BEFORE_REFRESH
            + " INTEGER,"

            + SipProfile.FIELD_TRY_CLEAN_REGISTERS
            + " INTEGER,"

            + SipProfile.FIELD_USE_RFC5626
            + " INTEGER DEFAULT 1,"
            + SipProfile.FIELD_RFC5626_INSTANCE_ID
            + " TEXT,"
            + SipProfile.FIELD_RFC5626_REG_ID
            + " TEXT,"

            + SipProfile.FIELD_VID_IN_AUTO_SHOW
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_VID_OUT_AUTO_TRANSMIT
            + " INTEGER DEFAULT -1,"

            + SipProfile.FIELD_RTP_PORT
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_RTP_ENABLE_QOS
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_RTP_QOS_DSCP
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_RTP_BOUND_ADDR
            + " TEXT,"
            + SipProfile.FIELD_RTP_PUBLIC_ADDR
            + " TEXT,"
            + SipProfile.FIELD_ANDROID_GROUP
            + " TEXT,"
            + SipProfile.FIELD_ALLOW_VIA_REWRITE
            + " INTEGER DEFAULT 0,"
            + SipProfile.FIELD_ALLOW_SDP_NAT_REWRITE
            + " INTEGER  DEFAULT 0,"
            + SipProfile.FIELD_SIP_STUN_USE
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_MEDIA_STUN_USE
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_ICE_CFG_USE
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_ICE_CFG_ENABLE
            + " INTEGER DEFAULT 0,"
            + SipProfile.FIELD_TURN_CFG_USE
            + " INTEGER DEFAULT -1,"
            + SipProfile.FIELD_TURN_CFG_ENABLE
            + " INTEGER DEFAULT 0,"
            + SipProfile.FIELD_TURN_CFG_SERVER
            + " TEXT,"
            + SipProfile.FIELD_TURN_CFG_USER
            + " TEXT,"
            + SipProfile.FIELD_TURN_CFG_PASSWORD
            + " TEXT,"
            + SipProfile.FIELD_IPV6_MEDIA_USE
            + " INTEGER DEFAULT 0,"
            + SipProfile.FIELD_WIZARD_DATA + " TEXT"

            + ");";

    private final static String TABLE_CALLLOGS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SipManager.CALLLOGS_TABLE_NAME
            + " ("
            + CallLog.Calls._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CallLog.Calls.CACHED_NAME
            + " TEXT,"
            + CallLog.Calls.CACHED_NUMBER_LABEL
            + " TEXT,"
            + CallLog.Calls.CACHED_NUMBER_TYPE
            + " INTEGER,"
            + CallLog.Calls.DATE
            + " INTEGER,"
            + CallLog.Calls.DURATION
            + " INTEGER,"
            + CallLog.Calls.NEW
            + " INTEGER,"
            + CallLog.Calls.NUMBER
            + " TEXT,"
            + CallLog.Calls.TYPE
            + " INTEGER,"
            + SipManager.CALLLOG_PROFILE_ID_FIELD
            + " INTEGER,"
            + SipManager.CALLLOG_STATUS_CODE_FIELD
            + " INTEGER,"
            + SipManager.CALLLOG_NICKNAME
            + " TEXT,"
            + SipManager.CALLLOG_NICKNAME_PYF
            + " TEXT,"
            + SipManager.CALLLOG_NICKNAME_PY
            + " TEXT,"
            + SipManager.CALLLOG_AVATAR_URL
            + " TEXT,"
            + SipManager.CALLLOG_STATUS_TEXT_FIELD + " TEXT" + ");";

    private static final String TABLE_FILTERS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SipManager.FILTERS_TABLE_NAME
            + " ("
            + Filter._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Filter.FIELD_PRIORITY
            + " INTEGER,"
            // Foreign key to account
            + Filter.FIELD_ACCOUNT
            + " INTEGER,"
            // Match/replace
            + Filter.FIELD_MATCHES
            + " TEXT,"
            + Filter.FIELD_REPLACE
            + " TEXT,"
            + Filter.FIELD_ACTION + " INTEGER" + ");";

    private final static String TABLE_MISSED_CALLS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + MissedCall.MISSED_CALLS_TABLE_NAME
            + " ("
            + MissedCall.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + MissedCall.FIELD_ACC + " INTEGER UNIQUE,"
            + MissedCall.FIELD_CALLER + " TEXT,"
            + MissedCall.FIELD_TIME + " TEXT,"
            + MissedCall.FIELD_MISSED_CALL_COUNT + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
//          LogUtil.d(TAG, "开始创建电话数据库");
        db.execSQL(TABLE_ACCOUNT_CREATE);
        db.execSQL(TABLE_CALLLOGS_CREATE);
        db.execSQL(TABLE_FILTERS_CREATE);
        db.execSQL(TABLE_MISSED_CALLS_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.getUtils().d(TAG+ " 开始升级电话数据库, 上个数据库版本是" + oldVersion);
        if (oldVersion < 1) {
            db.execSQL("DROP TABLE IF EXISTS " + SipProfile.ACCOUNTS_TABLE_NAME);
        }
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_KA_INTERVAL + " INTEGER");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS " + SipManager.FILTERS_TABLE_NAME);
        }
        if (oldVersion < 10) {
            try {
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_ALLOW_CONTACT_REWRITE
                        + " INTEGER");
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_CONTACT_REWRITE_METHOD
                        + " INTEGER");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 13) {
            try {
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_TRANSPORT + " INTEGER");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TRANSPORT + "="
                        + SipProfile.TRANSPORT_UDP + " WHERE prevent_tcp=1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TRANSPORT + "="
                        + SipProfile.TRANSPORT_TCP + " WHERE use_tcp=1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TRANSPORT + "="
                        + SipProfile.TRANSPORT_AUTO
                        + " WHERE use_tcp=0 AND prevent_tcp=0");
            } catch (SQLiteException e) {
            }

        }
        if (oldVersion < 17) {
            try {
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_KA_INTERVAL + "=0");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 18) {
            try {
                // As many users are crying... remove auto transport and force
                // udp
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TRANSPORT + "="
                        + SipProfile.TRANSPORT_UDP + " WHERE "
                        + SipProfile.FIELD_TRANSPORT + "="
                        + SipProfile.TRANSPORT_AUTO);
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 22) {
            try {
                // Add use proxy row
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_REG_USE_PROXY + " INTEGER");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_REG_USE_PROXY + "=3");
                // Add stack field
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_SIP_STACK + " INTEGER");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_SIP_STACK + "=0");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 23) {
            try {
                // Add use zrtp row
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_USE_ZRTP + " INTEGER");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_USE_ZRTP + "=-1");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 24) {
            try {
                // Add voice mail row
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_VOICE_MAIL_NBR + " TEXT");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_VOICE_MAIL_NBR + "=''");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 25) {
        }
        if (oldVersion < 26) {
            try {
                // Add reg delay before refresh row
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_REG_DELAY_BEFORE_REFRESH,
                        "INTEGER DEFAULT -1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_REG_DELAY_BEFORE_REFRESH + "=-1");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 27) {
            try {
                // Add reg delay before refresh row
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_TRY_CLEAN_REGISTERS,
                        "INTEGER DEFAULT 0");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TRY_CLEAN_REGISTERS + "=0");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 28) {
            try {
                // Add call log profile id
                addColumn(db, SipManager.CALLLOGS_TABLE_NAME,
                        SipManager.CALLLOG_PROFILE_ID_FIELD, "INTEGER");
                // Add call log status code
                addColumn(db, SipManager.CALLLOGS_TABLE_NAME,
                        SipManager.CALLLOG_STATUS_CODE_FIELD, "INTEGER");
                db.execSQL("UPDATE " + SipManager.CALLLOGS_TABLE_NAME + " SET "
                        + SipManager.CALLLOG_STATUS_CODE_FIELD + "=200");
                // Add call log status text
                addColumn(db, SipManager.CALLLOGS_TABLE_NAME,
                        SipManager.CALLLOG_STATUS_TEXT_FIELD, "TEXT");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 30) {
            try {
                // Add reg delay before refresh row
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_USE_RFC5626, "INTEGER DEFAULT 1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_RFC5626_INSTANCE_ID, "TEXT");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_RFC5626_REG_ID, "TEXT");

                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_VID_IN_AUTO_SHOW, "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_VID_OUT_AUTO_TRANSMIT,
                        "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_RTP_PORT, "INTEGER DEFAULT -1");

                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_RTP_ENABLE_QOS, "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_RTP_QOS_DSCP, "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_RTP_PUBLIC_ADDR, "TEXT");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_RTP_BOUND_ADDR, "TEXT");

            } catch (SQLiteException e) {
            }
        }
        // Nightly build bug -- restore mime type field to mime_type
        if (oldVersion == 30) {
        }

        if (oldVersion < 32) {
            try {
                // Add android group for buddy list
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_ANDROID_GROUP, "TEXT");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 33) {
            try {
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_ALLOW_VIA_REWRITE, "INTEGER DEFAULT 0");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_ALLOW_VIA_REWRITE + "=0");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 34) {
            try {
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_SIP_STUN_USE, "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_MEDIA_STUN_USE, "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_ICE_CFG_USE, "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_ICE_CFG_ENABLE, "INTEGER DEFAULT 0");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_TURN_CFG_USE, "INTEGER DEFAULT -1");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_TURN_CFG_ENABLE, "INTEGER DEFAULT 0");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_TURN_CFG_SERVER, "TEXT");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_TURN_CFG_USER, "TEXT");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_TURN_CFG_PASSWORD, "TEXT");

                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_SIP_STUN_USE + "=-1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_MEDIA_STUN_USE + "=-1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_ICE_CFG_USE + "=-1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_ICE_CFG_ENABLE + "=0");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TURN_CFG_USE + "=-1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TURN_CFG_ENABLE + "=0");

            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 35) {
            try {
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_IPV6_MEDIA_USE, "INTEGER DEFAULT 0");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_IPV6_MEDIA_USE + "=0");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 36) {
            try {
                // Enable try to clean register for all but ones that doesn't
                // support contact rewrite normal (legacy)
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TRY_CLEAN_REGISTERS + "=1 WHERE 1");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_TRY_CLEAN_REGISTERS + "=0 WHERE "
                        + SipProfile.FIELD_CONTACT_REWRITE_METHOD + "=1");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 37) {
            try {
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_AUTH_INITIAL_AUTH, "INTEGER DEFAULT 0");
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_AUTH_ALGO, "TEXT");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 38) {
            try {
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_WIZARD_DATA, "TEXT");
            } catch (SQLiteException e) {
            }
        }
        if (oldVersion < 39) {
            try {
                db.execSQL("ALTER TABLE " + SipProfile.ACCOUNTS_TABLE_NAME
                        + " ADD " + SipProfile.FIELD_DEFAULT_URI_SCHEME
                        + " TEXT");
            } catch (SQLiteException e) {
            }

        }
        if (oldVersion < 40) {
            try {
                addColumn(db, SipProfile.ACCOUNTS_TABLE_NAME,
                        SipProfile.FIELD_ALLOW_SDP_NAT_REWRITE,
                        "INTEGER DEFAULT 0");
                db.execSQL("UPDATE " + SipProfile.ACCOUNTS_TABLE_NAME + " SET "
                        + SipProfile.FIELD_ALLOW_SDP_NAT_REWRITE + "=0");
            } catch (SQLiteException e) {
            }
        }

        // Clear all old data. Cause account dont ignore case now. Modify by xjq, 2015/5/15
        if (oldVersion < 41) {
            try {
                db.execSQL("DELETE FROM " + MissedCall.MISSED_CALLS_TABLE_NAME);
                db.execSQL("DELETE FROM " + SipProfile.ACCOUNTS_TABLE_NAME);
                db.execSQL("DELETE FROM " + SipManager.CALLLOGS_TABLE_NAME);
                db.execSQL("DELETE FROM " + SipManager.FILTERS_TABLE_NAME);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 42) {
            try {
                db.execSQL("ALTER TABLE" + MissedCall.MISSED_CALLS_TABLE_NAME + " add miss_call_count INTEGER NOT NULL DEFAULT 0;");
            } catch (Exception e) {
                LogUtil.getUtils(TAG).e(" 数据库升级出错");
                e.printStackTrace();
            }
        }

        onCreate(db);
    }

    private static void addColumn(SQLiteDatabase db, String table,
                                  String field, String type) {
        db.execSQL("ALTER TABLE " + table + " ADD " + field + " " + type);
    }

    private static void setVoipDatabaseName(String databaseName){
        mDatabaseName = databaseName;
    }

    private static String getVoipDatabaseName(){
        return mDatabaseName;
    }
}


