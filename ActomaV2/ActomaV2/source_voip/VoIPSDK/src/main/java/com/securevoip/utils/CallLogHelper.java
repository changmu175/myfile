package com.securevoip.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CallLog;
import android.text.TextUtils;
import android.util.Log;

import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipUri;
import com.csipsimple.db.DatabaseHelper;
import com.csipsimple.models.CallerInfo;
import com.csipsimple.models.Filter;
import com.csipsimple.models.MissedCall;
import com.securevoip.contacts.CustContacts;
import com.securevoip.voip.MissedCallOttoPost;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.contactcommon.dto.ContactDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.CallLogUtil;
import webrelay.bean.CallSession;
import webrelay.bean.Role;
import webrelay.bean.StatusCode;

/**
 * Created by gbc on 2015/7/2.
 *
 * Modify by mb on 2016-07-26.
 * 替换getWritableDatabase()、db.close()为openWritableDatabase()、closeDatabase()，避免异步数据库打开关闭crash。
 *
 */

public class CallLogHelper {

     private static final String TAG = CallLogHelper.class.getCanonicalName();
     private static final String THIS_FILE = TAG;
     public static final String EXTRA_SIP_PROVIDER = "provider";

     /**
      * @param cv
      * @return
      */
     public static Uri addCallLog(ContentValues cv) {
          long rowId = 0;
          SQLiteDatabase db = null;
          Uri retUri;
          try {
               String matchedTable = SipManager.CALLLOGS_TABLE_NAME;
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               rowId = db.insert(matchedTable, null, cv);
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               //20170224-mengbo : 动态获取URI
               //retUri = ContentUris.withAppendedId(SipManager.CALLLOG_ID_URI_BASE, rowId);
               //ActomaController.getApp().getContentResolver().notifyChange(SipManager.CUST_CALLLOG_URI, null);
               retUri = ContentUris.withAppendedId(SipManager.getBaseCalllogIdUri(ActomaController.getApp()), rowId);
               ActomaController.getApp().getContentResolver().notifyChange(SipManager.getCustCalllogUri(ActomaController.getApp()), null);
               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
		  insertUpdateMissedCallCount(cv.getAsString("name"));
          return retUri;
     }

     public static Uri addCallLog(ContentValues cv, ContentValues cvEx) {
          String matchedTable = SipManager.CALLLOGS_TABLE_NAME;
          long rowId = 0;
          SQLiteDatabase db = null;
          Uri retUri;
          try {
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               rowId = db.insert(matchedTable, null, cv);
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               //20170224-mengbo : 动态获取URI
               //retUri = ContentUris.withAppendedId(SipManager.CALLLOG_ID_URI_BASE, rowId);
               retUri = ContentUris.withAppendedId(SipManager.getBaseCalllogIdUri(ActomaController.getApp()), rowId);
               ActomaController.getApp().getContentResolver().notifyChange(retUri, null);
               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
          return retUri;
     }

     /**
      * 删除与某人所有的通话记录
      *
      * @param actomaAccount 安通账号
      */
     public static void removeCallLogs(String actomaAccount) {
          SQLiteDatabase db = null;
          try {
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               db.delete(SipManager.CALLLOGS_TABLE_NAME, "name = ?", new String[]{actomaAccount});
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               //20170224-mengbo : 动态获取URI
               //ActomaController.getApp().getContentResolver().notifyChange(SipManager.CUST_CALLLOG_URI, null);
               ActomaController.getApp().getContentResolver().notifyChange(SipManager.getCustCalllogUri(ActomaController.getApp()), null);

               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
     }

     /**
      * 清除与某人所有的未接来电
      *
      * @param actomaAccount 安通账号
      */
     public static void clearMissedCall(String actomaAccount) {
          ContentValues args = new ContentValues();
          args.put(CallLog.Calls.NEW, "0");

          SQLiteDatabase db = null;
          try {
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               int count;
               count = db.update(SipManager.CALLLOGS_TABLE_NAME, args, "name = ? and new = ?", new String[]{actomaAccount, "1"});
               Log.d(THIS_FILE, "clearMissedCall Updated  count" + count +" thread "+Thread.currentThread().getName());
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               //20170224-mengbo : 动态获取URI
               //ActomaController.getApp().getContentResolver().notifyChange(SipManager.CUST_CALLLOG_URI, null);
               ActomaController.getApp().getContentResolver().notifyChange(SipManager.getCustCalllogUri(ActomaController.getApp()), null);

               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
     }

     /**
      * 获取所有未接来电的总数 - 用于设置主框架的小红点
      *
      * @return
      */
     public static int getMissedCallCount() {
          SQLiteDatabase db = null;
          int count = 0;
          Cursor cursor = null;
          try {
               SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
               qb.setTables(SipManager.CALLLOGS_TABLE_NAME);
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               cursor = qb.query(db, null, "new = ?", new String[]{"1"},
                       null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
               count = cursor.getCount();
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               if (cursor != null) {
                    cursor.close();
               }
               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
          return count;
     }

     /**
      * 更新通话记录数据库的联系人字段信息 - 在收到联系人模块发送的更新广播之后
      *
      * @param account  安通账号
      * @param nickname 更新后的名称（可以是昵称、备注或者集团通讯录的名称）
      */
     public static void updateCallLogNickName(String account, String nickname) {
          ContentValues cv = new ContentValues();
          cv.put(SipManager.CALLLOG_NICKNAME, nickname);

          SQLiteDatabase db = null;

          try {
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               int count;
               count = db.update(SipManager.CALLLOGS_TABLE_NAME, cv, "name = ?", new String[]{account});
               Log.d(THIS_FILE, "updateCallLogNickName Updated  count" + count+" threadName "+Thread.currentThread().getName());
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               //20170224-mengbo : 动态获取URI
               //ActomaController.getApp().getContentResolver().notifyChange(SipManager.CUST_CALLLOG_URI, null);
               ActomaController.getApp().getContentResolver().notifyChange(SipManager.getCustCalllogUri(ActomaController.getApp()), null);

               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
     }

     /**
      * 构建好友更新的contentvalues
      *
      * @param nickname
      * @param nicknameFullPY
      * @param nicknamePY
      * @param photoUrl
      * @return
      */
     public static ContentValues updateCallLogFields(String nickname, String nicknameFullPY, String nicknamePY, String photoUrl) {
          ContentValues cv = new ContentValues();
          buildContentValues(cv, SipManager.CALLLOG_NICKNAME, nickname);
          buildContentValues(cv, SipManager.CALLLOG_NICKNAME_PY, nicknamePY);
          buildContentValues(cv, SipManager.CALLLOG_NICKNAME_PYF, nicknameFullPY);
          buildContentValues(cv, SipManager.CALLLOG_AVATAR_URL, photoUrl);
          return cv;
     }

     /**
      * 更新通话记录数据库所有联系人的字段 - 在联系人模块下拉刷新之后
      *
      * @param accountList
      */
	  //[S]modify by tangsha@20161101 for 5748
     public static void updateWithCV(ArrayList<String> accountList) {

          /** 20160909-mengbo-start: 优化此方法，降低频繁操作数据库 **/
          SQLiteDatabase db = null;
          try {
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               Cursor cursor = db.query(SipManager.CALLLOGS_TABLE_NAME, null, null, null, null, null, null);
               if (cursor != null) {
                    int callLogCount = cursor.getCount();
                    cursor.close();
                    Log.d(THIS_FILE, "updateWithCV Updated  callLogCount " + callLogCount);
                    if (callLogCount > 0) {
                         for (int i = 0; i < accountList.size(); i++) {
                              String account = accountList.get(i);
                              ContactDto contactDto = ContactModuleProxy.getContactInfo(account);
                              String friendName = "";
                              String nicknamePY = "";
                              String nicknameFullPY = "";
                              String friendThumbNailPhoto = "";
                              if (!TextUtils.isEmpty(contactDto.getName())) {
                                   friendName = contactDto.getName();
                              } else {
                                   friendName = account;
                              }
                              if (!TextUtils.isEmpty(contactDto.getNamePY())) {
                                   nicknamePY = contactDto.getNamePY();
                              }
                              if (!TextUtils.isEmpty(contactDto.getNamePinYin())) {
                                   nicknameFullPY = contactDto.getNamePinYin();
                              }
                              if (!TextUtils.isEmpty(contactDto.getAvatarUrl())) {
                                   friendThumbNailPhoto = contactDto.getThumbnailUrl();
                              }
                              ContentValues cv = updateCallLogFields(friendName, nicknamePY, nicknameFullPY, friendThumbNailPhoto);

                              //ContentValues cv = updateCallLogFields(
                              //        CustContacts.getFriendName(account),
                              //        CustContacts.getNicknamePY(account),
                              //        CustContacts.getNicknameFullPY(account),
                              //        CustContacts.getFriendThumbNailPhoto(account));
                              /** 20160909-mengbo-end **/
                              int count;
                              count = db.update(SipManager.CALLLOGS_TABLE_NAME, cv, "name = ?", new String[]{account});
                              Log.d(THIS_FILE, "updateWithCV Updated  count" + count + " threadName " + Thread.currentThread());
                         }
                    }
               }
			   //[E]modify by tangsha@20161101 for 5748
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               //20170224-mengbo : 动态获取URI
               //ActomaController.getApp().getContentResolver().notifyChange(SipManager.CUST_CALLLOG_URI, null);
               ActomaController.getApp().getContentResolver().notifyChange(SipManager.getCustCalllogUri(ActomaController.getApp()), null);

               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
     }

     /**
      * ContentValues存储
      *
      * @param cv
      * @param key
      * @param value
      * @return
      */
     private static ContentValues buildContentValues(ContentValues cv, String key, String value) {
          if (!TextUtils.isEmpty(value)) {
               cv.put(key, value);
          }
          return cv;
     }

     /**
      * 根据SipCallSession对象构造通话记录
      *
      * @param call
      * @param callStart
      * @return
      */
     public static ContentValues logValuesForCall(SipCallSession call, long callStart) {
          ContentValues cv = new ContentValues();
          String remoteContact = call.getRemoteContact();
          Log.e(THIS_FILE, "通话记录生成并存储的remoteContact" + remoteContact);
          cv.put(CallLog.Calls.NUMBER, remoteContact);
          Pattern p = Pattern.compile("^(?:\")?([^<\"]*)(?:\")?[ ]*(?:<)?sip(?:s)?:([^@]*@[^>]*)(?:>)?", Pattern.CASE_INSENSITIVE);
          Matcher m = p.matcher(remoteContact);
          String number = remoteContact;
          if (m.matches()) {
               number = m.group(2);
          }
          //时间戳作为日期
          cv.put(CallLog.Calls.DATE, (callStart > 0) ? callStart : System.currentTimeMillis());
          //默认为去电
          int type = CallLog.Calls.OUTGOING_TYPE;
          //是否知晓本次来电请求，0代表知道，1代表不知道(就是未接来电)
          int nonAcknowledge = 0;
          //1来电 2去电 3未接来电
          if (call.isIncoming()) {
               type = CallLog.Calls.MISSED_TYPE;
               nonAcknowledge = 1;
               if (call.isMissed()) {
                    type = CallLog.Calls.MISSED_TYPE;
                    nonAcknowledge = 1;
               } else if (callStart > 0) {
                    // Has started on the remote side, so not missed call
                    type = CallLog.Calls.INCOMING_TYPE;
                    nonAcknowledge = 0;
               } else if (call.getLastStatusCode() == SipCallSession.StatusCode.DECLINE ||
                       call.getLastStatusCode() == SipCallSession.StatusCode.BUSY_HERE ||
                       call.getLastReasonCode() == 200) {
                    // We have intentionally declined this call or replied elsewhere
                    type = CallLog.Calls.INCOMING_TYPE;
                    nonAcknowledge = 0;
               }
          }
          int hasBeenAutoanswered = Filter.isAutoAnswerNumber(ActomaController.getApp(), call.getAccId(), number, null);
          if (hasBeenAutoanswered == call.getLastStatusCode()) {
               nonAcknowledge = 0;
          }
          cv.put(CallLog.Calls.TYPE, type);

          cv.put(CallLog.Calls.NEW, nonAcknowledge);
          cv.put(CallLog.Calls.DURATION,
                  (callStart > 0) ? (System.currentTimeMillis() - callStart) / 1000 : 0);
          cv.put(SipManager.CALLLOG_PROFILE_ID_FIELD, call.getAccId());
          cv.put(SipManager.CALLLOG_STATUS_CODE_FIELD, call.getLastStatusCode());
          cv.put(SipManager.CALLLOG_STATUS_TEXT_FIELD, call.getLastStatusComment());

          //TODO 加注释
          CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(remoteContact);
          SipUri.ParsedSipContactInfos psinfo = SipUri.parseSipContact(remoteContact);
          cv.put(CallLog.Calls.CACHED_NAME, psinfo.userName);
          if (callerInfo != null) {
               //zjc 20150906 现在使用numberType字段作为主叫和被叫的标识，1是主叫，0是被叫
               if (call != null) {
                    if (call.getRole() == SipCallSession.PJSIP_ROLE_UAC) {
                         callerInfo.numberType = 1;
//                    LogUtil.e(THIS_FILE, "呼叫类型是主叫，numberType是1");
                    } else if (call.getRole() == SipCallSession.PJSIP_ROLE_UAS) {
                         callerInfo.numberType = 0;
//                    LogUtil.e(THIS_FILE, "呼叫类型是被叫，numberType是0");
                    }
               }
               cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, callerInfo.numberLabel);
               cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, callerInfo.numberType);
          }

          cv.put(SipManager.CALLLOG_NICKNAME, CustContacts.getFriendName(psinfo.userName));
          cv.put(SipManager.CALLLOG_NICKNAME_PYF, CustContacts.getNicknameFullPY(psinfo.userName));
          cv.put(SipManager.CALLLOG_NICKNAME_PY, CustContacts.getNicknamePY(psinfo.userName));
          cv.put(SipManager.CALLLOG_AVATAR_URL, CustContacts.getFriendThumbNailPhoto(psinfo.userName));

          return cv;
     }

     /**
      * 根据推送CallSession对象构造通话记录
      *
      * @param call
      * @param callStart
      * @return
      */
     public static ContentValues logValuesForCall(CallSession call, long callStart) {
          ContentValues cv = new ContentValues();
          String remoteContact = call.getUser();
          cv.put(CallLog.Calls.NUMBER, remoteContact);

          String number = remoteContact;

          //时间戳作为日期
          cv.put(CallLog.Calls.DATE, System.currentTimeMillis());
          //默认为去电
          int type = CallLog.Calls.OUTGOING_TYPE;
          Role role = call.getRole();
          int nonAcknowledge = 0;
          if (role == Role.CALLEE) {
               int statusCode = Integer.valueOf(call.getCode());
               if (statusCode == StatusCode.CALLEE_REJECT ||
                       statusCode == StatusCode.SUCCESS) {
                    nonAcknowledge = 0;
                    type = CallLog.Calls.INCOMING_TYPE;

               } else {
                    nonAcknowledge = 1;
                    type = CallLog.Calls.MISSED_TYPE;
               }

          }

          cv.put(CallLog.Calls.TYPE, type);

          cv.put(CallLog.Calls.NEW, nonAcknowledge);
          long realDuration = 1;
          if (callStart > 0) {
               realDuration = (SystemClock.elapsedRealtime() - callStart) / 1000;
               if (realDuration <= 0) {
                    realDuration = 1;
               }
          }
          cv.put(CallLog.Calls.DURATION,
                  (callStart > 0) ? realDuration : 0);

          cv.put(SipManager.CALLLOG_PROFILE_ID_FIELD, 1);
          cv.put(SipManager.CALLLOG_STATUS_CODE_FIELD, call.getCode());
          cv.put(SipManager.CALLLOG_STATUS_TEXT_FIELD, "");

          cv.put(CallLog.Calls.CACHED_NAME, remoteContact);
          cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
          cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, call.getRole() == Role.CALLER ? 1 : 0);

          /**Begin：ZhengJunchen 通话记录记录名称、名称拼音、名称简拼、头像url**/
          cv.put(SipManager.CALLLOG_NICKNAME, CustContacts.getFriendName(number));
          cv.put(SipManager.CALLLOG_NICKNAME_PYF, CustContacts.getNicknameFullPY(number));
          cv.put(SipManager.CALLLOG_NICKNAME_PY, CustContacts.getNicknamePY(number));
          cv.put(SipManager.CALLLOG_AVATAR_URL, CustContacts.getFriendThumbNailPhoto(number));
          /**End:ZhengJunchen**/

          return cv;
     }

     /**
      * 构造未接来电的通话记录
      *
      * @param call
      * @return
      */
     public static ContentValues logValuesForCall(MissedCall call) {
          ContentValues cv = new ContentValues();
          long date = Long.parseLong(call.getTime());
          String remoteContact = call.getCaller();
          cv.put(CallLog.Calls.NUMBER, remoteContact);
          cv.put(CallLog.Calls.DATE, (date > 0) ? date * 1000 : System.currentTimeMillis());
          cv.put(CallLog.Calls.TYPE, CallLog.Calls.MISSED_TYPE);
          cv.put(CallLog.Calls.NEW, 1);
          cv.put(CallLog.Calls.DURATION, 0);
          cv.put(SipManager.CALLLOG_PROFILE_ID_FIELD, call.getAccountId());
          cv.put(SipManager.CALLLOG_STATUS_CODE_FIELD, SipCallSession.StatusCode.NOT_FOUND);
          cv.put(SipManager.CALLLOG_STATUS_TEXT_FIELD, SipCallSession.StatusCommentReplace.ReplaceStatusComment(SipCallSession.StatusCode.NOT_FOUND));

          CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(remoteContact);
          SipUri.ParsedSipContactInfos psinfo = SipUri.parseSipContact(remoteContact);
          cv.put(CallLog.Calls.CACHED_NAME, psinfo.userName);
          if (callerInfo != null) {
               cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, callerInfo.numberLabel);
               cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, callerInfo.numberType);
          }

          ContactDto contact = ContactModuleProxy.getContactInfo(psinfo.userName);
          if (null != contact) {
               cv.put(SipManager.CALLLOG_NICKNAME, contact.getName());
               cv.put(SipManager.CALLLOG_NICKNAME_PYF, contact.getNamePinYin());
               cv.put(SipManager.CALLLOG_NICKNAME_PY, contact.getNamePY());
               cv.put(SipManager.CALLLOG_AVATAR_URL, contact.getAvatarUrl());
          }
          return cv;
     }

     public static void insertRandomCallLog(int peopleCount, int countPerPeople) {
          String matchedTable = SipManager.CALLLOGS_TABLE_NAME;
          SQLiteDatabase db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
          db.beginTransaction();
          for (int i = 0; i < peopleCount; i++) {
               List<ContentValues> callLogList = CallLogHelper.randomCallLogs(countPerPeople);
               for (int j = 0; j < callLogList.size(); j++) {
                    db.insert(matchedTable, null, callLogList.get(j));
               }
          }
          //这个一定要有，否则会自动回滚不提交
          db.setTransactionSuccessful();
          db.endTransaction();
          DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          //20170224-mengbo : 动态获取URI
          //ActomaController.getApp().getContentResolver().notifyChange(SipManager.CUST_CALLLOG_URI, null);
          ActomaController.getApp().getContentResolver().notifyChange(SipManager.getCustCalllogUri(ActomaController.getApp()), null);

     }

     public static List<ContentValues> randomCallLogs(int countPerPeople) {
//          int count = new Random().nextInt(30);
          List<ContentValues> calllogs = new ArrayList<>();
          int actomaAccount = new Random().nextInt(999999);
          for (int i = 0; i < countPerPeople; i++) {
               calllogs.add(CallLogUtil.randomCallLog(actomaAccount));
          }
          return calllogs;
     }

     /**
      * 删除所有通话记录
      */
     public static boolean removeAllCallLogs() {
          int success = -1;
          try {
               //20170224-mengbo : 动态获取URI
               //success = ActomaController.getApp().getContentResolver().delete(SipManager.CUST_CALLLOG_URI, null, null);
               success = ActomaController.getApp().getContentResolver().delete(SipManager.getCustCalllogUri(ActomaController.getApp()), null, null);
          } catch (Exception e) {

          } finally {
               //20170224-mengbo : 动态获取URI
               //ActomaController.getApp().getContentResolver().notifyChange(SipManager.CUST_CALLLOG_URI, null);
               ActomaController.getApp().getContentResolver().notifyChange(SipManager.getCustCalllogUri(ActomaController.getApp()), null);

               BusProvider.getMainProvider().post(MissedCallOttoPost.missedCallEvent());
          }
          //有通话记录时，删除成功后success大于0；没有通话记录时，删除后success是0
//          if (success >= 0) {
//               return true;
//          } else {
//               return false;
//          }
          /**2017-2-28 -wangzhen modify.Change the if way Simplifly**/
          return (success >= 0);
     }

     /**
      * 与某人未接来电的数量
      *
      * @param
      * @param
      * @return
      */
     @SuppressLint("StringBufferReplaceableByStringBuilder")
     public static int getFiendMissedCallCount(String actomaAccount) {
          SQLiteDatabase db;
          int count = 0;
          try {
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
               //SELECT count(*) from calllogs where name = ? and new = 1
               StringBuffer sb = new StringBuffer();
               sb.append("SELECT name FROM calllogs WHERE NAME = ");
               sb.append(actomaAccount);
               sb.append(" AND new = 1");

               Cursor cursor = db.rawQuery(sb.toString(), null);
               count = cursor.getCount();
               cursor.close();
          } catch (Exception e) {
               e.printStackTrace();
          } finally {
               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
          return count;
     }
     @SuppressLint("StringBufferReplaceableByStringBuilder")
     private static void insertUpdateMissedCallCount(String actomaAccount) {
          int missedCallCount = getFiendMissedCallCount(actomaAccount);
          SQLiteDatabase db;
          try {
               db = DatabaseHelper.getInstance(ActomaController.getApp()).openWritableDatabase();
//             示例：db.execSQL("replace into missed_calls(account, caller, time, miss_call_count) values (888888, null, null, 300)");
               StringBuffer sb = new StringBuffer();
               sb.append("REPLACE INTO ");
               sb.append(MissedCall.MISSED_CALLS_TABLE_NAME);
               sb.append("(");
               sb.append(MissedCall.FIELD_ACC);
               sb.append(", ");
               sb.append(MissedCall.FIELD_CALLER);
               sb.append(", ");
               sb.append(MissedCall.FIELD_TIME);
               sb.append(", ");
               sb.append(MissedCall.FIELD_MISSED_CALL_COUNT);
               sb.append(") ");
               sb.append("values (");
               sb.append(actomaAccount);
               sb.append(", ");
               sb.append("null, ");
               sb.append("null, ");
               sb.append(missedCallCount);
               sb.append(")");
               db.execSQL(sb.toString());
          } catch (Exception e) {
               LogUtil.getUtils().d(THIS_FILE + "插入更新未接来电数据库出错");
               e.printStackTrace();
          } finally {
               DatabaseHelper.getInstance(ActomaController.getApp()).closeDatabase();
          }
     }
}


