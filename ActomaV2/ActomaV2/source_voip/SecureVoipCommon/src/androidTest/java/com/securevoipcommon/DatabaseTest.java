package com.securevoipcommon;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.CallLog;

import com.csipsimple.api.SipManager;
import com.xdja.dependence.uitls.LogUtil;

import junit.framework.Assert;

/**
 * Created by zjc on 2015/7/27.
 */
public class DatabaseTest extends ApplicationTest {

    public static String TAG = "数据库单元测试";

    public void testInsertCallLog() {
        addCallLog();
        deleteCallLog();
    }

    public void addCallLog() {

        ContentValues cv = new ContentValues();
        //name
        cv.put(CallLog.Calls.CACHED_NAME, "xrj123456");
        //number type
        cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        //date
        cv.put(CallLog.Calls.DATE, 0);
        //duration
        cv.put(CallLog.Calls.DURATION, 0);
        //new
        cv.put(CallLog.Calls.NEW, 0);
        //number
        cv.put(CallLog.Calls.NUMBER, "number");
        //type
        cv.put(CallLog.Calls.TYPE, "type");
        //account_id
        cv.put("account_id", 17);
        //statues_code
        cv.put(SipManager.CALLLOG_STATUS_CODE_FIELD, 404);
        //statues_text
        cv.put(SipManager.CALLLOG_STATUS_TEXT_FIELD, "对方不在线");

//        CallLogHelper.addCallLog(getContext(), cv);

        searchCallLog();

    }

    public void deleteCallLog() {

        //int wrong = getContext().getContentResolver().delete(SipManager.CALLLOG_URI, "number = ?", new String[]{"zhang"});

        //int right = getContext().getContentResolver().delete(SipManager.CALLLOG_URI, "number = ?", new String[]{"zheng"});

        //ContentResolver cr = getContext().getContentResolver();

        //实际应该是0
//        Assert.assertEquals(-1, wrong);
//        LogUtil.getUtils(TAG).e("错误的值" + wrong);
//        Assert.assertEquals(-1, right);
//        LogUtil.getUtils(TAG).e("正确的值" + right);

    }

    public void searchCallLog() {

        /*ContentResolver cr = getContext().getContentResolver();

        Cursor cursor = cr.query(SipManager.CALLLOG_URI, null, null, null, "type desc");

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            int numberType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int date = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DATE));
            int news = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            int accountId = cursor.getInt(cursor.getColumnIndex("account_id"));
            int statuesCode = cursor.getInt(cursor.getColumnIndex(SipManager.CALLLOG_STATUS_CODE_FIELD));
            String statuesTexct = cursor.getString(cursor.getColumnIndex(SipManager.CALLLOG_STATUS_TEXT_FIELD));

            //Assert.assertEquals(false, true);
            Assert.assertEquals("xrj123456", name);
            LogUtil.getUtils(TAG).e(name);
            Assert.assertEquals(0, numberType);
            LogUtil.getUtils(TAG).e( numberType + "");
            Assert.assertEquals(0, duration);
            LogUtil.getUtils(TAG).e( duration + "");
            Assert.assertEquals(0, date);
            LogUtil.getUtils(TAG).e( date + "");
            Assert.assertEquals(0, news);
            LogUtil.getUtils(TAG).e( news + "'");
            Assert.assertEquals("number", number);
            LogUtil.getUtils(TAG).e( number);
            Assert.assertEquals(type, 0);
            LogUtil.getUtils(TAG).e( type + "");
            Assert.assertEquals(404, statuesCode);
            LogUtil.getUtils(TAG).e( statuesCode + "");
            Assert.assertEquals("对方不在线", statuesTexct);
            LogUtil.getUtils(TAG).e(statuesTexct + "");
            Assert.assertEquals(17, accountId);
        }*/

    }


}
