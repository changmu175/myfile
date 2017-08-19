package com.securevoip.voip;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipProfileState;
import com.csipsimple.api.SipUri;
import com.csipsimple.db.DBProvider;
import com.csipsimple.service.SipService;
import com.csipsimple.utils.PreferencesProviderWrapper;
import com.csipsimple.utils.PreferencesWrapper;
import com.securevoip.voip.bean.IncomingInfo;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

/**
 * 为外部提供接口，调用电话功能使用
 *
 * @author mleidong
 */

public class PhoneManager implements IphoneCall {


     private long lastClickTime;

     private volatile static PhoneManager phoneManager = null;

     private static final String THIS_FILE = "PhoneManager";

     public static final String AUTHORITY = "com.xdja.contacts.db";
     public static final String INSERT_CONTENT = "insert/contacts";
     public static final Uri INSERT_CONTACTS = Uri.parse(ContentResolver.SCHEME_CONTENT + "://"
             + AUTHORITY + "/" + INSERT_CONTENT);


     private IncomingInfo info;
     //private Context context;

     private Toast toast;

     //todo mengbo
     public static String name = null;
     public static String ticket = null;

     // 单例模式,构造方法私有
     private PhoneManager() {
     }

     public static PhoneManager getInstance() {
          if (phoneManager == null) {
               synchronized (PhoneManager.class) {
                    if (phoneManager == null) {
                         phoneManager = new PhoneManager();
                    }
               }
          }
          return phoneManager;
     }

     /**
      * 普通拨打电话
      */

     @Override
     @SuppressLint("ResourceType")
     public void phoneCall(IncomingInfo info, Context context) {
          // TODO Auto-generated method stub
          if (!TextUtils.isEmpty(info.getPhoneNum())) {

               Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + info.getPhoneNum()));

               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               context.startActivity(intent);
          } else {
               Toast.makeText(context, context.getText(R.string.inputPhone).toString(), Toast.LENGTH_SHORT).show();
          }
     }

     /**
      * 拨打加密电话，仅供第三方应用调用，本地直接调用service远程接口。Modify by xjq, 2015/4/23
      */
     @Override
     public void encryptPhoneCall(IncomingInfo info, Context context) {

          // TODO Auto-generated method stub
          if (getLineState(info, context)) {
               Intent intent = new Intent(SipManager.ACTION_SIP_ACTOMA_CALLING); // 安通+呼叫使用唯一的action标识 xjq 2015-11-12
               intent.putExtra(SipManager.CALL_NUM, info.getPhoneNum());
               intent.putExtra(SipManager.ACCOUNT_ID, Integer.parseInt(info.getUserId()));
               context.sendBroadcast(intent);
          }
     }

     /**
      * 获取电话状态
      */
     @Override
     public boolean getLineState(IncomingInfo info, Context context) {
          // TODO Auto-generated method stub
          //context = _context;
          boolean isValidForCall = false;
          SipProfileState ps = null;
          PreferencesProviderWrapper prefsWrapper = new PreferencesProviderWrapper(context);
          boolean valid = prefsWrapper.isValidConnectionForIncoming();
          if (!valid) {
               //主框架依赖
//            XToast.show(_context, "网络连接不可用,请检查网络设置");
               return valid;
          }
          //判断账户是否在线
          //20170224-mengbo : 动态获取URI
          //Cursor c = context.getContentResolver().query(SipProfile.ACCOUNT_STATUS_URI, null, null, null, null);
          Cursor c = context.getContentResolver().query(SipProfile.getAccountStatusUri(context), null, null, null, null);
          if (c != null) {
               try {
                    if (c.getCount() > 0) {
                         c.moveToFirst();
                         do {
                              ps = new SipProfileState(c);
                              if (ps.isValidForCall()) {
                                   isValidForCall = true;
                              }
                         } while (c.moveToNext());
                    }
               } catch (Exception e) {
               } finally {
                    if (c != null) {
                         c.close();
                    }
               }
          }

          if (!isValidForCall) {
               //主框架依赖
//            XToast.show(_context, "无法连接服务器，请稍后重试");
               ContentValues cv = new ContentValues();
               if (ps != null) {
                    cv.put(SipProfile.FIELD_ACTIVE, true);
                    //20170224-mengbo : 动态获取URI
                    //context.getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, ps.getAccountId()), cv, null, null);
                    context.getContentResolver().update(ContentUris.withAppendedId(SipProfile.getBaseAccountIdUri(context), ps.getAccountId()), cv, null, null);
               } else {
                    //重新添加默认账号 xjq 2015-09-12
                    Intent publishIntent = new Intent(SipManager.ACTION_SIP_READD_DEFAULT_ACCOUNT);
                    context.sendBroadcast(publishIntent);
               }
          }
          return isValidForCall;
     }


     @Override
     public int online(Context context) {
          String accName = null;
          String accTicket = null;

          //20170224-mengbo : 动态获取URI
          //Cursor c = context.getContentResolver().query(SipManager.CUST_ACC_URI
          //        , DBProvider.CUST_ACC_FULL_PROJECTION, null, null, null);
          Cursor c = context.getContentResolver().query(SipManager.getCustAccUri(context)
                  , DBProvider.CUST_ACC_FULL_PROJECTION, null, null, null);

          if (c != null) {
               try {
                    if (c.getCount() == 1) {
                         c.moveToFirst();
                         accName = c.getString(c.getColumnIndex(SipManager.CUST_ACC_NAME));
                         accTicket = c.getString(c.getColumnIndex(SipManager.CUST_ACC_TICKET));
                    }
               } finally {
                    c.close();
               }
          }

          if (accName == null || accTicket == null)
               return IphoneCall.ACC_INVALID;


          return IphoneCall.SUCCESS;
     }

     @Override
     public void offLine(Context _context) {

     }

     /**
      * 停止VoIP协议栈 xjq 2015-12-11
      *
      * @param _context
      */
     @Override
     public void stop(Context _context) {
          // TODO Auto-generated method stub
          PreferencesWrapper prefProviderWrapper = new PreferencesWrapper(_context);
          prefProviderWrapper.setPreferenceBooleanValue(PreferencesWrapper.HAS_BEEN_QUIT, true);
          Intent intent = new Intent(SipManager.ACTION_OUTGOING_UNREGISTER);
          intent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, new ComponentName(_context, _context.getClass()));
          _context.sendBroadcast(intent);
     }

     @Override
     public synchronized void addAccount(Context _context, String userId, String displayName, String passwd) {
          // TODO Auto-generated method stub
          delAccount(_context);

          if (TextUtils.isEmpty(userId)) {
               Toast.makeText(_context, _context.getText(R.string.getUserIdErr), Toast.LENGTH_SHORT).show();
          } else {
               //saveAccount(userId, displayName, _context, voipConfig.sip_server_port, passwd);
               //saveAccount(userId, displayName, _context,"192.168.200.120:5060", passwd);
               //安通+内网 写死
               //saveAccount(userId, displayName, _context,"11.12.110.144:5061", passwd);
               //120.194.4.154:5061 安通+外网测试
//			saveAccount(userId, displayName, _context,"11.12.110.151:5060", passwd);

               //从主框架读取上线地址
               //主框架依赖
//			String url = "";
//			String url = ConfigurationServer.getDefaultConfig(_context).read("voipUrl", "", String.class);
               //zjc 20150908 账号和ticket变动才上线，否则不上线
//			saveAccount(userId, displayName, _context, url, passwd);

          }

     }


     @Override
     @SuppressLint("AccessStaticViaInstance")
     public int initAccount(Context context, String accName, String ticket) {

          if (accName == null || ticket == null) {
               return ACC_INVALID;
          }
          //todo mengbo
          this.name = accName;
          this.ticket = ticket;

          ContentValues cv = new ContentValues();
          cv.put(SipManager.CUST_ACC_NAME, accName);
          cv.put(SipManager.CUST_ACC_TICKET, ticket);

          //20170224-mengbo : 动态获取URI
          //context.getContentResolver().insert(SipManager.CUST_ACC_URI, cv);
          context.getContentResolver().insert(SipManager.getCustAccUri(context), cv);

          //20170224-mengbo : 动态获取URI
          //Cursor c = context.getContentResolver().query(SipManager.CUST_ACC_URI
          //        , DBProvider.CUST_ACC_FULL_PROJECTION, null, null, null);
          Cursor c = context.getContentResolver().query(SipManager.getCustAccUri(context)
                  , DBProvider.CUST_ACC_FULL_PROJECTION, null, null, null);

          if (c != null) {
               try {
                    if (c.getCount() == 1) {
                         c.moveToFirst();
                         String s1 = c.getString(c.getColumnIndex(SipManager.CUST_ACC_NAME));
                         String s2 = c.getString(c.getColumnIndex(SipManager.CUST_ACC_TICKET));
                    }
               } finally {
                    c.close();
               }
          }

          return IphoneCall.SUCCESS;
     }


     @Override
     public SipProfile buildCustAccount(Context context, String serverAddr) {

          String accName = null;
          String accTicket = null;

          //20170224-mengbo : 动态获取URI
          //Cursor c = context.getContentResolver().query(SipManager.CUST_ACC_URI
          //        , DBProvider.CUST_ACC_FULL_PROJECTION, null, null, null);
          Cursor c = context.getContentResolver().query(SipManager.getCustAccUri(context)
                  , DBProvider.CUST_ACC_FULL_PROJECTION, null, null, null);

          if (c != null) {
               try {
                    if (c.getCount() == 1) {
                         c.moveToFirst();
                         accName = c.getString(c.getColumnIndex(SipManager.CUST_ACC_NAME));
                         LogUtil.getUtils(THIS_FILE).d("pjsip xjq account name get from provider is " + accName);
                         accTicket = c.getString(c.getColumnIndex(SipManager.CUST_ACC_TICKET));
                         LogUtil.getUtils(THIS_FILE).d("pjsip xjq account name get from provider is " + accTicket);
                    }
               } finally {
                    c.close();
               }
          }

          if (accName == null || accTicket == null) {
               AccountBean accountBean = AccountServer.getAccount();
               accName = accountBean.getAccount();
               accTicket = PreferencesServer.getWrapper(context).gPrefStringValue("ticket");
          }

          String displayName = accName;
//		String sipServerIP = "192.168.1.108:5060";

          SipProfile account = new SipProfile();
          if (displayName == null || TextUtils.isEmpty(displayName)) {
               account.display_name = accName;
          } else {
               account.display_name = displayName;
          }

          if (serverAddr == null || TextUtils.isEmpty(serverAddr)) {
               return null;
          }

          String[] serverParts = serverAddr.split(":");
          account.acc_id = displayName + " <sip:" + SipUri.encodeUser(accName) + "@" + serverParts[0].trim() + ">";
//		account.acc_id = displayName+"<sip:" + SipUri.encodeUser(phoneNumber) + "@"+sipServerIP+">";

          String regUri = "sip:" + serverAddr;
          account.reg_uri = regUri;
          account.proxies = new String[]{regUri};


          account.realm = "*";
          account.username = accName;
//		account.data = phoneNumber.substring(5);
          account.data = accTicket;
          account.scheme = SipProfile.CRED_SCHEME_DIGEST;
          account.datatype = SipProfile.CRED_DATA_PLAIN_PASSWD;
          //By default auto transport
          account.transport = SipProfile.TRANSPORT_TCP;
          account.wizard = "BASIC"; // wizard is basic xjq 2015-12-12
          account.id = 1;

          return account;
     }


     /**
      * 删除电话账户
      */
     @Override
     public synchronized int delAccount(Context context) {
          // TODO Auto-generated method stub
          int ret = -1;
          //20170224-mengbo : 动态获取URI
          //ret = context.getContentResolver().delete(SipProfile.ACCOUNT_URI, null, null);
          ret = context.getContentResolver().delete(SipProfile.getAccountUri(ActomaController.getApp()), null, null);
          if (ret > 0) {
//			Log.d(THIS_FILE,"删除账户成功！");
          } else {
//			Log.e(THIS_FILE,"删除账户失败！");
          }
          return ret;
     }

     /**
      * 启动电话服务
      */
     @Override
     public void startSipService(final Context context) {
          // TODO Auto-generated method stub
          Intent sip_service_intent = new Intent(context, SipService.class);
          context.startService(sip_service_intent);
     }

     /**
      * 发送明文短信
      */
     @Override
     public void sendSMS(Context context, String phoneNumber) {
          // TODO Auto-generated method stub
          Uri smsToUri = Uri.parse("smsto:");
          Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
          sendIntent.putExtra("address", phoneNumber);   //电话号码，这行去掉的话，默认就没有电话
          sendIntent.setType("vnd.android-dir/mms-sms");
          context.startActivity(sendIntent);
     }

} 