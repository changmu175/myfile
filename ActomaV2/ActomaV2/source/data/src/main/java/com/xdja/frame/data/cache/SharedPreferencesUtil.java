package com.xdja.frame.data.cache;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by THZ on 2016/5/23.
 * sharedpreference工具类
 */
public class SharedPreferencesUtil {
    private static final String TAG = "SharedPreferencesUtil";

    private static final String FILE_NAME = "sa_data";

    private static final String TICKET = "ticket";

    private static final String NORMAL_STOP_SERVICE = "normal_stop_service";

    /**
     * 设置ticket
     * @param context
     * @param ticket
     * @return
     */
    public static boolean setTicket(Context context, String ticket) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TICKET, ticket);
        return editor.commit();
    }


    public static void setNormalStopService(Context context , boolean isNormalStopService){
        if (context == null){
            Log.v(TAG , "H>>> Set context is null return");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        if (sharedPreferences == null){
            Log.v(TAG , "H>>> Set sharedPreferences is null return");
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.edit() == null) {
            Log.v(TAG, "H>>> Set sharedPreferences.edit() is null return");
            return;
        }
        editor.putBoolean(NORMAL_STOP_SERVICE, isNormalStopService);
        Log.v(TAG, "H>>> Set NormalStopService : " + isNormalStopService);
        editor.apply();
    }

    public static boolean getNormalStopService(Context context) {
        if (context == null) {
            Log.v(TAG , "H>>> Get context is null return");
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            Log.v(TAG , "H>>> Get sharedPreferences is null return");
            return false;
        }
        boolean isNormal = sharedPreferences.getBoolean(NORMAL_STOP_SERVICE, false);
        Log.v(TAG , "H>>> Get normal : " + isNormal);
        return isNormal;
    }

    /**
     * 读取ticket
     * @param context
     * @return
     */
    public static String getTicket(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(TICKET, "");
    }
}
