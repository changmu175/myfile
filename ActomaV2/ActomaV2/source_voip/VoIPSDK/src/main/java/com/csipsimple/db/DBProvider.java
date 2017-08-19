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

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.provider.CallLog;
import android.support.v4.database.DatabaseUtilsCompat;
import android.text.TextUtils;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipProfileState;
import com.csipsimple.models.Filter;
import com.csipsimple.models.MissedCall;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class DBProvider extends ContentProvider {
    private static final String UNKNOWN_URI_LOG = "Unknown URI ";

    public final String QUERY_UNIQUE_SQL2 = buildTestSQL2();

    private String buildTestSQL2() {
        return "select t.*,max(t.date),t1.count from calllogs t left JOIN (select count(0) count,name from calllogs where new = 1 group by name) t1 on t1.name = t.name  GROUP BY (t.name) order by date desc";
    }

    // Ids for matcher
    private static final int ACCOUNTS = 1, ACCOUNTS_ID = 2;
    private static final int ACCOUNTS_STATUS = 3, ACCOUNTS_STATUS_ID = 4;
    private static final int CALLLOGS = 5, CALLLOGS_ID = 6;
    private static final int FILTERS = 7, FILTERS_ID = 8;
    private static final int CUST_CALLLOGS = 9;

    // 账户体系（随着第三方应用有不同) xjq 2015-12-10
    private static final int CUST_ACC = 10;

    private static final int MISSED_CALLS = 13, MISSED_CALLS_ID = 14;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private final static String AUTHORITY_SUFFIX = ".dbprovider";
    private static String authority = "";

    public final static String[] ACCOUNT_FULL_PROJECTION = {
            SipProfile.FIELD_ID,
            // Application relative fields
            SipProfile.FIELD_ACTIVE, SipProfile.FIELD_WIZARD, SipProfile.FIELD_DISPLAY_NAME,
            // Custom datas
            SipProfile.FIELD_WIZARD_DATA,

            // Here comes pjsua_acc_config fields
            SipProfile.FIELD_PRIORITY, SipProfile.FIELD_ACC_ID, SipProfile.FIELD_REG_URI,
            SipProfile.FIELD_MWI_ENABLED, SipProfile.FIELD_PUBLISH_ENABLED, SipProfile.FIELD_REG_TIMEOUT, SipProfile.FIELD_KA_INTERVAL,
            SipProfile.FIELD_PIDF_TUPLE_ID,
            SipProfile.FIELD_FORCE_CONTACT, SipProfile.FIELD_ALLOW_CONTACT_REWRITE, SipProfile.FIELD_CONTACT_REWRITE_METHOD,
            SipProfile.FIELD_ALLOW_VIA_REWRITE, SipProfile.FIELD_ALLOW_SDP_NAT_REWRITE,
            SipProfile.FIELD_CONTACT_PARAMS, SipProfile.FIELD_CONTACT_URI_PARAMS,
            SipProfile.FIELD_TRANSPORT, SipProfile.FIELD_DEFAULT_URI_SCHEME, SipProfile.FIELD_USE_SRTP, SipProfile.FIELD_USE_ZRTP,
            SipProfile.FIELD_REG_DELAY_BEFORE_REFRESH,

            // RTP config
            SipProfile.FIELD_RTP_PORT, SipProfile.FIELD_RTP_PUBLIC_ADDR, SipProfile.FIELD_RTP_BOUND_ADDR,
            SipProfile.FIELD_RTP_ENABLE_QOS, SipProfile.FIELD_RTP_QOS_DSCP,

            // Proxy infos
            SipProfile.FIELD_PROXY, SipProfile.FIELD_REG_USE_PROXY,

            // And now cred_info since for now only one cred info can be managed
            // In future release a credential table should be created
            SipProfile.FIELD_REALM, SipProfile.FIELD_SCHEME, SipProfile.FIELD_USERNAME, SipProfile.FIELD_DATATYPE,
            SipProfile.FIELD_DATA,

            SipProfile.FIELD_AUTH_INITIAL_AUTH, SipProfile.FIELD_AUTH_ALGO,

            // CSipSimple specific
            SipProfile.FIELD_SIP_STACK, SipProfile.FIELD_VOICE_MAIL_NBR,
            SipProfile.FIELD_TRY_CLEAN_REGISTERS, SipProfile.FIELD_ANDROID_GROUP,

            // RFC 5626
            SipProfile.FIELD_USE_RFC5626, SipProfile.FIELD_RFC5626_INSTANCE_ID, SipProfile.FIELD_RFC5626_REG_ID,

            // Video
            SipProfile.FIELD_VID_IN_AUTO_SHOW, SipProfile.FIELD_VID_OUT_AUTO_TRANSMIT,

            // STUN, ICE, TURN
            SipProfile.FIELD_SIP_STUN_USE, SipProfile.FIELD_MEDIA_STUN_USE,
            SipProfile.FIELD_ICE_CFG_USE, SipProfile.FIELD_ICE_CFG_ENABLE,
            SipProfile.FIELD_TURN_CFG_USE, SipProfile.FIELD_TURN_CFG_ENABLE, SipProfile.FIELD_TURN_CFG_SERVER, SipProfile.FIELD_TURN_CFG_USER, SipProfile.FIELD_TURN_CFG_PASSWORD,

            SipProfile.FIELD_IPV6_MEDIA_USE,
    };
    public final static Class<?>[] ACCOUNT_FULL_PROJECTION_TYPES = {
            Long.class,

            Integer.class, String.class, String.class,

            String.class,

            Integer.class, String.class, String.class,
            Boolean.class, Integer.class, Integer.class, Integer.class,
            String.class,
            String.class, Integer.class, Integer.class,
            Integer.class, Integer.class,
            String.class, String.class,
            Integer.class, String.class, Integer.class, Integer.class,
            Integer.class,


            // RTP config
            Integer.class, String.class, String.class,
            Integer.class, Integer.class,

            // Proxy infos
            String.class, Integer.class,

            // Credentials
            String.class, String.class, String.class, Integer.class,
            String.class,

            Boolean.class, String.class,

            // CSipSimple specific
            Integer.class, String.class,
            Integer.class, String.class,

            // RFC 5626
            Integer.class, String.class, String.class,

            // Video
            Integer.class, Integer.class,

            // STUN, ICE, TURN
            Integer.class, Integer.class,
            Integer.class, Integer.class,
            Integer.class, Integer.class, String.class, String.class, String.class,

            // IPV6
            Integer.class
    };

    private static final String[] CALL_LOG_FULL_PROJECTION = new String[]{
            CallLog.Calls._ID,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.CACHED_NUMBER_LABEL,
            CallLog.Calls.CACHED_NUMBER_TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.NEW,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            SipManager.CALLLOG_NICKNAME,
            SipManager.CALLLOG_NICKNAME_PYF,
            SipManager.CALLLOG_NICKNAME_PY,
            SipManager.CALLLOG_AVATAR_URL,
            SipManager.CALLLOG_PROFILE_ID_FIELD,
            SipManager.CALLLOG_STATUS_CODE_FIELD,
            SipManager.CALLLOG_STATUS_TEXT_FIELD
    };

    private static final String[] MISSED_CALLS_FULL_PROJECTION = new String[]{
            MissedCall.FIELD_ID,
            MissedCall.FIELD_ACC,
            MissedCall.FIELD_CALLER,
            MissedCall.FIELD_TIME
    };

    private static final String[] FILTERS_FULL_PROJECTION = new String[]{
            Filter._ID,
            Filter.FIELD_PRIORITY,
            Filter.FIELD_ACCOUNT,
            Filter.FIELD_MATCHES,
            Filter.FIELD_REPLACE,
            Filter.FIELD_ACTION,
    };

    private static final String THIS_FILE = "DBProvider";

    // Map active account id (id for sql settings database) with SipProfileState that contains stack id and other status infos
    @SuppressLint("AndroidLintUseSparseArrays")
    private final Map<Long, ContentValues> profilesStatus = new HashMap<>();


    // 第三方账户体系，自定义数据类型，现有安通+中为账户名和ticket
    public static final String[] CUST_ACC_FULL_PROJECTION = new String[]{
            SipManager.CUST_ACC_NAME,
            SipManager.CUST_ACC_TICKET,
    };

    // save custom account xjq 2015-12-10
    private static String accName = null;
    private static String accTicket = null;


    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ACCOUNTS:
                return SipProfile.ACCOUNT_CONTENT_TYPE;
            case ACCOUNTS_ID:
                return SipProfile.ACCOUNT_CONTENT_ITEM_TYPE;
            case ACCOUNTS_STATUS:
                return SipProfile.ACCOUNT_STATUS_CONTENT_TYPE;
            case ACCOUNTS_STATUS_ID:
                return SipProfile.ACCOUNT_STATUS_CONTENT_ITEM_TYPE;
            case CALLLOGS:
                return SipManager.CALLLOG_CONTENT_TYPE;
            case CALLLOGS_ID:
                return SipManager.CALLLOG_CONTENT_ITEM_TYPE;
            case FILTERS:
                return SipManager.FILTER_CONTENT_TYPE;
            case FILTERS_ID:
                return SipManager.FILTER_CONTENT_ITEM_TYPE;
            case MISSED_CALLS:
                return MissedCall.MISSED_CALLS_CONTENT_TYPE;
            case MISSED_CALLS_ID:
                return MissedCall.MISSED_CALLS_CONTENT_ITEM_TYPE;
            // 第三方账户体系uri type xjq 2016-01-14
            case CUST_ACC:
                return SipManager.CUST_ACC_TYPE;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }
    }

    DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        // Assumes that any failures will be reported by a thrown exception.

        URI_MATCHER.addURI(getAuthority(getContext()), SipProfile.ACCOUNTS_TABLE_NAME, ACCOUNTS);
        URI_MATCHER.addURI(getAuthority(getContext()), SipProfile.ACCOUNTS_TABLE_NAME + "/#", ACCOUNTS_ID);
        URI_MATCHER.addURI(getAuthority(getContext()), SipProfile.ACCOUNTS_STATUS_TABLE_NAME, ACCOUNTS_STATUS);
        URI_MATCHER.addURI(getAuthority(getContext()), SipProfile.ACCOUNTS_STATUS_TABLE_NAME + "/#", ACCOUNTS_STATUS_ID);
        URI_MATCHER.addURI(getAuthority(getContext()), SipManager.CALLLOGS_TABLE_NAME, CALLLOGS);
        URI_MATCHER.addURI(getAuthority(getContext()), SipManager.CALLLOGS_TABLE_NAME + "/#", CALLLOGS_ID);
        URI_MATCHER.addURI(getAuthority(getContext()), SipManager.FILTERS_TABLE_NAME, FILTERS);
        URI_MATCHER.addURI(getAuthority(getContext()), SipManager.FILTERS_TABLE_NAME + "/#", FILTERS_ID);
        URI_MATCHER.addURI(getAuthority(getContext()), MissedCall.MISSED_CALLS_TABLE_NAME, MISSED_CALLS);
        URI_MATCHER.addURI(getAuthority(getContext()), MissedCall.MISSED_CALLS_TABLE_NAME + "/#", MISSED_CALLS_ID);
        URI_MATCHER.addURI(getAuthority(getContext()), SipManager.CALLLOGS, CUST_CALLLOGS);
        URI_MATCHER.addURI(getAuthority(getContext()), SipManager.CUST_ACC, CUST_ACC);

        return true;
    }

    public static String getAuthority(Context context){
        if(TextUtils.isEmpty(authority)){
            if(context != null){
                authority = context.getPackageName() + AUTHORITY_SUFFIX;
            }else{
                authority = "";
            }
        }
        return authority;
    }


    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;
        int count = 0;
        int matched = URI_MATCHER.match(uri);
        Uri regUri = uri;

        List<String> possibles = getPossibleFieldsForType(matched);
        checkSelection(possibles, where);

        ArrayList<Long> oldRegistrationsAccounts = null;

        switch (matched) {
            case ACCOUNTS:
                count = db.delete(SipProfile.ACCOUNTS_TABLE_NAME, where, whereArgs);
                break;
            case ACCOUNTS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(SipProfile.FIELD_ID + " = " + ContentUris.parseId(uri), where);
                count = db.delete(SipProfile.ACCOUNTS_TABLE_NAME, finalWhere, whereArgs);
                break;
            case CALLLOGS:
                count = db.delete(SipManager.CALLLOGS_TABLE_NAME, where, whereArgs);
                break;
            case CALLLOGS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(CallLog.Calls._ID + " = " + ContentUris.parseId(uri), where);
                count = db.delete(SipManager.CALLLOGS_TABLE_NAME, finalWhere, whereArgs);
                break;
            case CUST_CALLLOGS:
                count = db.delete(SipManager.CALLLOGS_TABLE_NAME, where, whereArgs);
                break;

            // 删除帐号的操作，将账户名与ticket置空 xjq 2016-01-14
            case CUST_ACC:
                accName = null;
                accTicket = null;
                break;

            case FILTERS:
                count = db.delete(SipManager.FILTERS_TABLE_NAME, where, whereArgs);
                break;
            case FILTERS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(Filter._ID + " = " + ContentUris.parseId(uri), where);
                count = db.delete(SipManager.FILTERS_TABLE_NAME, finalWhere, whereArgs);
                break;
            /** Begin:add by xjq 未接来电表项删除 20141111 **/
            case MISSED_CALLS:
                count = db.delete(MissedCall.MISSED_CALLS_TABLE_NAME, where, whereArgs);
                break;
            case MISSED_CALLS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(MissedCall.FIELD_ID + " = " + ContentUris.parseId(uri), where);
                count = db.delete(MissedCall.MISSED_CALLS_TABLE_NAME, finalWhere, whereArgs);
                break;
            /** End:add by xjq 未接来电表项删除 20141111 **/
            case ACCOUNTS_STATUS: //修改用户状态存储位置，目前存储在内存中。
                oldRegistrationsAccounts = new ArrayList<>();
                synchronized (profilesStatus) {
                    for (Long accId : profilesStatus.keySet()) {
                        oldRegistrationsAccounts.add(accId);
                    }
                    profilesStatus.clear();
                }//修改用户状态存储位置，目前存储在内存中。
                break;
            case ACCOUNTS_STATUS_ID:
                long id = ContentUris.parseId(uri);
                synchronized (profilesStatus) {
                    profilesStatus.remove(id);
                }
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        getContext().getContentResolver().notifyChange(regUri, null);

        if (matched == ACCOUNTS_ID || matched == ACCOUNTS_STATUS_ID) {
            long rowId = ContentUris.parseId(uri);
            if (rowId >= 0) {
                if (matched == ACCOUNTS_ID) {
                    broadcastAccountDelete(rowId);
                } else if (matched == ACCOUNTS_STATUS_ID) {
                    broadcastRegistrationChange(rowId);
                }
            }
        }
        if (matched == FILTERS || matched == FILTERS_ID) {
            Filter.resetCache();
        }
        if (matched == ACCOUNTS_STATUS && oldRegistrationsAccounts != null) {
            for (Long accId : oldRegistrationsAccounts) {
                if (accId != null) {
                    broadcastRegistrationChange(accId);
                }
            }
        }

        return count;
    }


    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        int matched = URI_MATCHER.match(uri);
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        String matchedTable = null;
        Uri baseInsertedUri = null;

        switch (matched) {
            case ACCOUNTS:
            case ACCOUNTS_ID:
                matchedTable = SipProfile.ACCOUNTS_TABLE_NAME;
                //20170224-mengbo : 动态获取URI
                //baseInsertedUri = SipProfile.ACCOUNT_ID_URI_BASE;
                baseInsertedUri = SipProfile.getBaseAccountIdUri(ActomaController.getApp());
                break;
            case CALLLOGS:
            case CALLLOGS_ID:
            case CUST_CALLLOGS:
                matchedTable = SipManager.CALLLOGS_TABLE_NAME;
                //20170224-mengbo : 动态获取URI
                //baseInsertedUri = SipManager.CALLLOG_ID_URI_BASE;
                baseInsertedUri = SipManager.getBaseCalllogIdUri(ActomaController.getApp());
                break;
            case FILTERS:
            case FILTERS_ID:
                matchedTable = SipManager.FILTERS_TABLE_NAME;
                //20170224-mengbo : 动态获取URI
                //baseInsertedUri = SipManager.FILTER_ID_URI_BASE;
                baseInsertedUri = SipManager.getBaseFilterIdUri(ActomaController.getApp());
                break;
            /** Begin:add by xjq 未接来电表查询 20141111 **/
            case MISSED_CALLS:
            case MISSED_CALLS_ID:
                matchedTable = MissedCall.MISSED_CALLS_TABLE_NAME;
//                baseInsertedUri = MissedCall.MISSED_CALLS_ID_URI_BASE;
                baseInsertedUri = MissedCall.getBaseMissedCallsIdUri(ActomaController.getApp());
                break;
            /** End:add by xjq 未接来电表查询 20141111 **/
            case ACCOUNTS_STATUS_ID:
                long id = ContentUris.parseId(uri);
                synchronized (profilesStatus) {
                    SipProfileState ps = new SipProfileState();
                    if (profilesStatus.containsKey(id)) {
                        ContentValues currentValues = profilesStatus.get(id);
                        ps.createFromContentValue(currentValues);
                    }
                    ps.createFromContentValue(initialValues);
                    ContentValues cv = ps.getAsContentValue();
                    cv.put(SipProfileState.ACCOUNT_ID, id);
                    profilesStatus.put(id, cv);
                    LogUtil.getUtils(THIS_FILE).d("Added " + cv);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return uri;

            // 存储安通帐号与Ticket xjq
            case CUST_ACC:
                accName = initialValues.getAsString(SipManager.CUST_ACC_NAME);
                accTicket = initialValues.getAsString(SipManager.CUST_ACC_TICKET);
                return uri;


            default:
                break;
        }

        if (matchedTable == null) {
            throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = db.insert(matchedTable, null, values);

        // If the insert succeeded, the row ID exists.
        if (rowId >= 0) {
            // TODO : for inserted account register it here

            Uri retUri = ContentUris.withAppendedId(baseInsertedUri, rowId);
            getContext().getContentResolver().notifyChange(retUri, null);

            if (matched == ACCOUNTS || matched == ACCOUNTS_ID) {
                broadcastAccountChange(rowId);
            }
            if (matched == CALLLOGS || matched == CALLLOGS_ID) {
                db.delete(SipManager.CALLLOGS_TABLE_NAME, CallLog.Calls._ID + " IN " +
                        "(SELECT " + CallLog.Calls._ID + " FROM " + SipManager.CALLLOGS_TABLE_NAME + " ORDER BY " +
                        CallLog.Calls.DEFAULT_SORT_ORDER + " LIMIT -1 OFFSET 500)", null);
            }
            if (matched == ACCOUNTS_STATUS || matched == ACCOUNTS_STATUS_ID) {
                broadcastRegistrationChange(rowId);
            }
            if (matched == FILTERS || matched == FILTERS_ID) {
                Filter.resetCache();
            }
            return retUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        //构造一个新的查询生成器并设置它的表名
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String finalSortOrder = sortOrder;
        String[] finalSelectionArgs = selectionArgs;
        String finalGrouping = null;
        String finalHaving = null;
        int type = URI_MATCHER.match(uri);

        Uri regUri = uri;

        // Security check to avoid data retrieval from outside
        //安全检查，以避免从外部数据检索
        int remoteUid = Binder.getCallingUid();
        int selfUid = android.os.Process.myUid();
        if (remoteUid != selfUid) {
            if (type == ACCOUNTS || type == ACCOUNTS_ID) {
                for (String proj : projection) {
                    if (proj.toLowerCase(Locale.getDefault()).contains(SipProfile.FIELD_DATA) || proj.toLowerCase().contains("*")) {
                        throw new SecurityException("Password not readable from external apps");
                    }
                }
            }
        }
        // Security check to avoid project of invalid fields or lazy projection
        //安全检查，以避免无效的字段或懒惰的投影项目
        List<String> possibles = getPossibleFieldsForType(type);
        if (possibles == null) {
            throw new SecurityException("You are asking wrong values " + type);
        }
        checkProjection(possibles, projection);
        checkSelection(possibles, selection);

        Cursor c;
        long id;

        switch (type) {
            case ACCOUNTS:
                qb.setTables(SipProfile.ACCOUNTS_TABLE_NAME);
                if (sortOrder == null) {
                    finalSortOrder = SipProfile.FIELD_PRIORITY + " ASC";
                }
                break;
            case ACCOUNTS_ID:
                qb.setTables(SipProfile.ACCOUNTS_TABLE_NAME);
                qb.appendWhere(SipProfile.FIELD_ID + "=?");
                finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{uri.getLastPathSegment()});
                break;
            case CALLLOGS:
                qb.setTables(SipManager.CALLLOGS_TABLE_NAME);
                if (sortOrder == null) {
                    finalSortOrder = CallLog.Calls.DATE + " DESC";
                }
                break;
            case CALLLOGS_ID:
                qb.setTables(SipManager.CALLLOGS_TABLE_NAME);
                qb.appendWhere(CallLog.Calls._ID + "=?");
                finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{uri.getLastPathSegment()});
                break;
            case CUST_CALLLOGS:
                //mengbo@xdja.com 2016-09-01 start modify. 解决通话历史记录对方昵称修改后，显示不正问题
                c = createQueryCallLogsCursor();

                /**origin code:
                    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    c = db.rawQuery(QUERY_UNIQUE_SQL2, null);
                 */
                //mengbo@xdja.com 2016-09-01 end
                c.setNotificationUri(getContext().getContentResolver(), regUri);
                return c;
            case FILTERS:
                qb.setTables(SipManager.FILTERS_TABLE_NAME);
                if (sortOrder == null) {
                    finalSortOrder = Filter.DEFAULT_ORDER;
                }
                break;
            case FILTERS_ID:
                qb.setTables(SipManager.FILTERS_TABLE_NAME);
                qb.appendWhere(Filter._ID + "=?");
                finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{uri.getLastPathSegment()});
                break;
            /** Begin:add by xjq 未接来电表条件查询 20141111 **/
            case MISSED_CALLS:
                qb.setTables(MissedCall.MISSED_CALLS_TABLE_NAME);
                if (sortOrder == null) {
                    finalSortOrder = MissedCall.FIELD_TIME + " DESC";
                }
                break;

            case MISSED_CALLS_ID:
                qb.setTables(MissedCall.MISSED_CALLS_TABLE_NAME);
                qb.appendWhere(MissedCall.FIELD_ID + "=?");
                finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{uri.getLastPathSegment()});
                break;

            // 第三方帐号体系xjq 2015-12-11
            case CUST_ACC:
                ContentValues[] accCvs = {new ContentValues()};
                accCvs[0].put(SipManager.CUST_ACC_NAME, accName);
                accCvs[0].put(SipManager.CUST_ACC_TICKET, accTicket);
                c = getCursor(accCvs);
                return c;

            /** End:add by xjq 未接来电表查询 20141111 **/
            case ACCOUNTS_STATUS:
                synchronized (profilesStatus) {
                    ContentValues[] cvs = new ContentValues[profilesStatus.size()];
                    int i = 0;
                    for (ContentValues ps : profilesStatus.values()) {
                        cvs[i] = ps;
                        i++;
                    }
                    c = getCursor(cvs);
                }
                if (c != null) {
                    c.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return c;
            case ACCOUNTS_STATUS_ID:
                id = ContentUris.parseId(uri);
                synchronized (profilesStatus) {
                    ContentValues cv = profilesStatus.get(id);
                    if (cv == null) {
                        return null;
                    }
                    c = getCursor(new ContentValues[]{cv});
                }
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        c = qb.query(db, projection, selection, finalSelectionArgs,
                finalGrouping, finalHaving, finalSortOrder);

        c.setNotificationUri(getContext().getContentResolver(), regUri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;
        int matched = URI_MATCHER.match(uri);
        LogUtil.getUtils(THIS_FILE).d("update matched:" + matched);
        List<String> possibles = getPossibleFieldsForType(matched);
        checkSelection(possibles, where);

        switch (matched) {
            case ACCOUNTS:
                count = db.update(SipProfile.ACCOUNTS_TABLE_NAME, values, where, whereArgs);
                break;
            case ACCOUNTS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(SipProfile.FIELD_ID + " = " + ContentUris.parseId(uri), where);
                count = db.update(SipProfile.ACCOUNTS_TABLE_NAME, values, finalWhere, whereArgs);
                LogUtil.getUtils(THIS_FILE).d("update count:" + count);
                break;
            case CALLLOGS:
                count = db.update(SipManager.CALLLOGS_TABLE_NAME, values, where, whereArgs);
                LogUtil.getUtils(THIS_FILE).d("Updated  count" + count);
                break;
            case CALLLOGS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(CallLog.Calls._ID + " = " + ContentUris.parseId(uri), where);
                count = db.update(SipManager.CALLLOGS_TABLE_NAME, values, finalWhere, whereArgs);
                break;
            case FILTERS:
                count = db.update(SipManager.FILTERS_TABLE_NAME, values, where, whereArgs);
                break;
            case FILTERS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(Filter._ID + " = " + ContentUris.parseId(uri), where);
                count = db.update(SipManager.FILTERS_TABLE_NAME, values, finalWhere, whereArgs);
                break;
            /** Begin:add by xjq 未接来电表数据更新 20141111 **/
            case MISSED_CALLS:
                count = db.update(MissedCall.MISSED_CALLS_TABLE_NAME, values, where, whereArgs);
                break;
            case MISSED_CALLS_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(MissedCall.FIELD_ID + " = " + ContentUris.parseId(uri), where);
                count = db.update(MissedCall.MISSED_CALLS_TABLE_NAME, values, where, whereArgs);
                break;
            /** End:add by xjq 未接来电表数据更新 20141111 **/

            // 第三方账户体系 2015-12-11
            case CUST_ACC:
                accName = values.getAsString(SipManager.CUST_ACC_NAME);
                accTicket = values.getAsString(SipManager.CUST_ACC_TICKET);
                count = 1;
                break;

            case ACCOUNTS_STATUS_ID:
                long id = ContentUris.parseId(uri);
                synchronized (profilesStatus) {
                    SipProfileState ps = new SipProfileState();
                    if (profilesStatus.containsKey(id)) {
                        ContentValues currentValues = profilesStatus.get(id);
                        ps.createFromContentValue(currentValues);
                    }
                    ps.createFromContentValue(values);
                    ContentValues cv = ps.getAsContentValue();
                    cv.put(SipProfileState.ACCOUNT_ID, id);
                    profilesStatus.put(id, cv);
                    LogUtil.getUtils(THIS_FILE).d("Updated " + cv);
                }
                count = 1;
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        long rowId = -1;
        if (matched == ACCOUNTS_ID || matched == ACCOUNTS_STATUS_ID) {
            rowId = ContentUris.parseId(uri);
        }
        if (rowId >= 0) {
            if (matched == ACCOUNTS_ID) {
                // Don't broadcast if we only changed wizard or only changed priority
                //如果我们只改变向导或仅更改的优先级不发送广播
                boolean doBroadcast = true;
                if (values.size() == 1) {
                    if (values.containsKey(SipProfile.FIELD_WIZARD)) {
                        doBroadcast = false;
                    } else if (values.containsKey(SipProfile.FIELD_PRIORITY)) {
                        doBroadcast = false;
                    }
                }
                if (doBroadcast) {
                    broadcastAccountChange(rowId);
                }
            } else if (matched == ACCOUNTS_STATUS_ID) {
                broadcastRegistrationChange(rowId);
            }
        }
        if (matched == FILTERS || matched == FILTERS_ID) {
            Filter.resetCache();
        }

        return count;
    }


    /**
     * Build a {@link Cursor} with a single row that contains all values
     * provided through the given {@link ContentValues}.
     */
    private Cursor getCursor(ContentValues[] contentValues) {
        if (contentValues != null && contentValues.length > 0) {
            final Set<Entry<String, Object>> valueSet = contentValues[0].valueSet();
            int colSize = valueSet.size();
            final String[] keys = new String[colSize];

            int i = 0;
            for (Entry<String, Object> entry : valueSet) {
                keys[i] = entry.getKey();
                i++;
            }

            final MatrixCursor cursor = new MatrixCursor(keys);
            for (ContentValues cv : contentValues) {
                final Object[] values = new Object[colSize];
                i = 0;
                for (Entry<String, Object> entry : cv.valueSet()) {
                    values[i] = entry.getValue();
                    i++;
                }
                cursor.addRow(values);
            }
            return cursor;
        }
        return null;
    }

    /**
     * Broadcast the fact that account config has changed
     *
     * @param accountId
     */
    private void broadcastAccountChange(long accountId) {
        Intent publishIntent = new Intent(SipManager.ACTION_SIP_ACCOUNT_CHANGED);
        publishIntent.putExtra(SipProfile.FIELD_ID, accountId);
        getContext().sendBroadcast(publishIntent);
    }

    /**
     * Broadcast the fact that account config has been deleted
     *
     * @param accountId
     */
    private void broadcastAccountDelete(long accountId) {
        Intent publishIntent = new Intent(SipManager.ACTION_SIP_ACCOUNT_DELETED);
        publishIntent.putExtra(SipProfile.FIELD_ID, accountId);
        getContext().sendBroadcast(publishIntent);
    }

    /**
     * Broadcast the fact that registration / adding status changed
     *
     * @param accountId the id of the account
     */
    private void broadcastRegistrationChange(long accountId) {
        Intent publishIntent = new Intent(SipManager.ACTION_SIP_REGISTRATION_CHANGED);
        publishIntent.putExtra(SipProfile.FIELD_ID, accountId);
        getContext().sendBroadcast(publishIntent, SipManager.PERMISSION_USE_SIP);

    }

    private static List<String> getPossibleFieldsForType(int type) {
        List<String> possibles = null;
        switch (type) {
            case ACCOUNTS:
            case ACCOUNTS_ID:
                possibles = Arrays.asList(ACCOUNT_FULL_PROJECTION);
                break;
            case CALLLOGS:
            case CALLLOGS_ID:
            case CUST_CALLLOGS:
                possibles = Arrays.asList(CALL_LOG_FULL_PROJECTION);
                break;
            case FILTERS:
            case FILTERS_ID:
                possibles = Arrays.asList(FILTERS_FULL_PROJECTION);
                break;
            /** Begin:add by xjq 增加未接来电数据库字段 20141223 **/
            case MISSED_CALLS:
            case MISSED_CALLS_ID:
                possibles = Arrays.asList(MISSED_CALLS_FULL_PROJECTION);
                break;
            /** End:add by xjq 增加未接来电数据库字段 20141223 **/
            case ACCOUNTS_STATUS:
            case ACCOUNTS_STATUS_ID:
                possibles = new ArrayList<>();
                break;
            case CUST_ACC:// 第三方账户体系 xjq 2015-12-11
                possibles = Arrays.asList(CUST_ACC_FULL_PROJECTION);
                break;
            default:
        }
        return possibles;
    }

    private static void checkSelection(List<String> possibles, String selection) {
        if (selection != null) {
            String cleanSelection = selection.toLowerCase(Locale.getDefault());
            for (String field : possibles) {
                cleanSelection = cleanSelection.replace(field, "");
            }
            cleanSelection = cleanSelection.replaceAll(" in \\([0-9 ,]+\\)", "");
            cleanSelection = cleanSelection.replaceAll(" and ", "");
            cleanSelection = cleanSelection.replaceAll(" or ", "");
            cleanSelection = cleanSelection.replaceAll("[0-9]+", "");
            cleanSelection = cleanSelection.replaceAll("[=? ]", "");
            /**Begin:xjq 添加未接来电查询过滤字段 20141224**/
            cleanSelection = cleanSelection.replaceAll(".*@*", "");
            /**End:xjq 添加未接来电查询过滤字段 20141224**/
            if (cleanSelection.length() > 0) {
                throw new SecurityException("You are selecting wrong thing " + cleanSelection);
            }
        }
    }

    private static void checkProjection(List<String> possibles, String[] projection) {
        if (projection != null) {
            // Ensure projection is valid
            for (String proj : projection) {
                proj = proj.replaceAll(" AS [a-zA-Z0-9_]+$", "");
                if (!possibles.contains(proj)) {
                    throw new SecurityException("You are asking wrong values " + proj);
                }
            }
        }
    }


    //mengbo@xdja.com 2016-09-05 start modify. 解决通话历史记录对方昵称修改后，显示不正问题
    private Cursor createQueryCallLogsCursor() {
//        List<Member> members = new MemberService().getAllMembers();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY_UNIQUE_SQL2, null);

//        Cursor resultCursor = null;
//        ContentValues[] cvs = null;
//        if(cursor != null){
//
//            Log.e("mb","DBProvider--createQueryCallLogsCursor--cursor.getCount():"+cursor.getCount());
//
//            if(cursor.getCount() >0){
//                cvs = new ContentValues[cursor.getCount()];
//            }
//
//            int i = 0;
//            while(cursor.moveToNext()){
//                Log.e("mb","DBProvider--createQueryCallLogsCursor--cursor.getPosition():"+cursor.getPosition());
//
//                String _id = cursor.getString(cursor.getColumnIndex("_id"));
//                String account = cursor.getString(TableConstant.NAME_INDEX);
//                long dateTime = cursor.getLong(TableConstant.DATE_INDEX);
//                int type = cursor.getInt(TableConstant.TYPE_INDEX);
//                String nickname = cursor.getString(TableConstant.NICKNAME_INDEX);
//                String imageUrl = cursor.getString(TableConstant.AVATAR_INDEX);
//                int missedCallCount = cursor.getInt(cursor.getColumnIndex("count"));
//
//                Log.e("mb","DBProvider--createQueryCallLogsCursor--nickname--11:"+nickname);
//
//                if(members != null){
//                    for(Member memeber : members){
//                        if(memeber.getAccount() != null && memeber.getAccount().equals(account)){
//                            if(memeber.getName() != null){
////                                nickname = memeber.getName();
//
//                                Log.e("mb","DBProvider--createQueryCallLogsCursor--nickname--22:"+nickname);
//
//                            }
//                            break;
//                        }
//                    }
//                }
//
//                cvs[i] = new ContentValues();
//                cvs[i].put("_id", _id);
//                cvs[i].put(CallLog.Calls.CACHED_NAME, account);
//                cvs[i].put(CallLog.Calls.DATE, dateTime);
//                cvs[i].put(CallLog.Calls.TYPE, type);
//                cvs[i].put(SipManager.CALLLOG_NICKNAME, nickname);
//                cvs[i].put(SipManager.CALLLOG_AVATAR_URL, imageUrl);
//                cvs[i].put("count", missedCallCount);
//
//                i++;
//            }
//
//            if(cvs != null && cvs.length>0){
//                Log.e("mb","DBProvider--createQueryCallLogsCursor--cvs.length:"+cvs.length);
//
//                cursor.close();
//                resultCursor = getCursor(cvs);
//            }else{
//                Log.e("mb","DBProvider--createQueryCallLogsCursor--cvs.length--cursor.getCount():"+cursor.getCount());
//
//                resultCursor = cursor;
//            }
//        }
//        return resultCursor;

        return cursor;
    }
    //mengbo@xdja.com 2016-09-05 end
}
