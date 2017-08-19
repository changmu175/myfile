package com.csipsimple.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import com.csipsimple.api.SipManager;
import com.csipsimple.db.DBProvider;

public class MissedCall {

    /**
     * Primary key id.
     * 
     * @see Long
     */
    public static final String FIELD_ID = "id";
    /**
     * Account
     * 
     * @see String
     */
    public static final String FIELD_ACC = "account";
    /**
     * Caller
     * 
     * @see String
     */
    public static final String FIELD_CALLER = "caller";
    /**
     * Time 
     * 
     * @see String
     */
    public static final String FIELD_TIME = "time";

    public static final String FIELD_MISSED_CALL_COUNT = "miss_call_count";

    public static final String MISSED_CALLS_TABLE_NAME = "missed_calls";

    /**
     * Content type for missed calls.
     */
    public static final String MISSED_CALLS_CONTENT_TYPE = SipManager.BASE_DIR_TYPE + ".missed_call";
    /**
     * Item type for missed call.
     */
    public static final String MISSED_CALLS_CONTENT_ITEM_TYPE = SipManager.BASE_ITEM_TYPE + ",missed_call";

    //public static final Uri MISSED_CALLS_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://"
    //        + SipManager.AUTHORITY + "/" + MISSED_CALLS_TABLE_NAME);

    //public static final Uri MISSED_CALLS_ID_URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://"
    //        + SipManager.AUTHORITY + "/" + MISSED_CALLS_TABLE_NAME + "/");

    private static Uri baseMissedCallsIdUri = null;

    private int acc_id;
    private String caller;
    private String time;
    
    public static final int XDJAMSG_HDR_MISSED_CALLS = 1;
    
    public static final int MISSED_CALLS_HDR_TYPE_CLIENT_REQUEST = 0;
    public static final int MISSED_CALLS_HDR_TYPE_CLIENT_NOTIFY = 1;
    public static final int MISSED_CALLS_HDR_TYPE_SERVER_RESPONSE = 2;
    public static final int MISSED_CALLS_HDR_TYPE_SERVER_OK = 4;
    
    public MissedCall(int aAcc_id, String aCaller, String aTime) {
    	acc_id = aAcc_id;
    	caller = aCaller;
    	time = aTime;
    }

    public MissedCall(Cursor c) {
        ContentValues args = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(c, args);
        createFromContentValue(args);
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_ACC, acc_id);
        cv.put(FIELD_CALLER, caller);
        cv.put(FIELD_TIME, time);
        return cv;
    }
    
    public final void createFromContentValue(ContentValues args) {
        String tmp_s;
        Integer tmp_i;
        
        tmp_i = args.getAsInteger(FIELD_ACC);
        if(tmp_i != null) {
            acc_id = tmp_i;
        }
        tmp_s = args.getAsString(FIELD_CALLER);
        if(tmp_s != null) {
            caller = tmp_s;
        }
        tmp_s = args.getAsString(FIELD_TIME);
        if(tmp_s != null) {
            time = tmp_s;
        }
    }

    public int getAccountId() {
        return acc_id;
    }

    public String getCaller() {
        return caller;
    }
    
    public String getTime() {
        return time;
    }

    public static Uri getBaseMissedCallsIdUri(Context context) {
        if(baseMissedCallsIdUri == null || baseMissedCallsIdUri.equals(Uri.EMPTY)){
            if(context != null){
                baseMissedCallsIdUri = Uri.parse(ContentResolver.SCHEME_CONTENT
                        + "://" + DBProvider.getAuthority(context) + "/" + MISSED_CALLS_TABLE_NAME + "/");
            }else{
                baseMissedCallsIdUri = Uri.EMPTY;
            }
        }
        return baseMissedCallsIdUri;
    }

}
