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

package com.csipsimple.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.csipsimple.api.SipUri;
import com.csipsimple.api.SipUri.ParsedSipContactInfos;
import com.securevoip.contacts.CustContacts;
import com.securevoip.presenter.activity.CallDetailActivityPresenter;
import com.securevoip.utils.CallLogHelper;
import com.xdja.comm.http.OkHttpsClient;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.NotificationIntent;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import webrelay.VOIPManager;
import webrelay.bean.CallSession;
import webrelay.bean.State;


public class SipNotifications {

     private final NotificationManager notificationManager;
     /**
      * 没有头像的未接来电通知
      */
     private Builder mNotificationBuilder;

     //这个一定要静态，不然每次集合都是空
     public static List<String> missedCallTag;

     //[S]remove by xienana for notification to NotifiParamUtil @2016/10/11 [review by tangsha]
    /* public static final int REGISTER_NOTIF_ID = 10000;
     public static final int CALL_NOTIF_ID = REGISTER_NOTIF_ID + 1;
     public static final int CALLLOG_NOTIF_ID = REGISTER_NOTIF_ID + 2;
     public static final int MESSAGE_NOTIF_ID = REGISTER_NOTIF_ID + 3;
     public static final int VOICEMAIL_NOTIF_ID = REGISTER_NOTIF_ID + 4;
     public static final int MISSED_CALL_NOTIF_ID = REGISTER_NOTIF_ID + 5;*/
     //[S]remove by xienana for notification to NotifiParamUtil @2016/10/11 [review by tangsha]

     private static boolean isInit = false;

     public SipNotifications() {
          notificationManager = (NotificationManager) ActomaController.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
          if (missedCallTag == null) {
               missedCallTag = new ArrayList<>();
          }
          if (!isInit) {
               cancelAll();
               cancelCalls();
               isInit = true;
          }
     }

     private static final String THIS_FILE = "Notifications";

     private Object[] mSetForegroundArgs = new Object[1];
     private Object[] mStartForegroundArgs = new Object[2];
     private Object[] mStopForegroundArgs = new Object[1];

     private void invokeMethod(Method method, Object[] args) {
          try {
               method.invoke(ActomaController.getApp(), args);
          } catch (InvocationTargetException e) {
               // Should not happen.
               LogUtil.getUtils(THIS_FILE).e("Unable to invoke method", e);
          } catch (IllegalAccessException e) {
               // Should not happen.
               LogUtil.getUtils(THIS_FILE).e("Unable to invoke method", e);
          }
     }

     private boolean isServiceWrapper = false;

     public void onServiceDestroy() {
          // Make sure our notification is gone.
          cancelAll();
          cancelCalls();
     }

     // Announces

     /**
      * 将完整的账号地址解析出安通账号
      *
      * @param remoteContact
      * @return
      */
     public static String formatRemoteContactString(String remoteContact) {
          /**Begin:sunyulei 去掉通话中 状态栏中 <sip:107@voip.sscp.safecenter.com> 显示**/
          ParsedSipContactInfos psci = SipUri.parseSipContact(remoteContact);
          return psci.userName;
          /**End:sunyulei 去掉通话中 状态栏中 <sip:107@voip.sscp.safecenter.com> 显示**/
     }

     /**
      * Format the notification title for a call info
      *
      * @param title
      * @return
      */
     private String formatNotificationTitle(int title, long accId) {
          StringBuilder notifTitle = new StringBuilder(ActomaController.getApp().getText(title));
          return notifTitle.toString();
     }

     /**
      * 通知栏头像是否加载
      * 因为网络请求加载图片是异步的，因此有可能出现先cancel但是通知还未生成的情况，在此情况下，就会出现正在通话通知无法清除的情况
      */
     private boolean shouldShow = true;

     /**
      * 显示正在通话的通知 增加头像显示
      */
     public void showNotificationForCall2(final Class target, final String account) {

          shouldShow = true;
          String imageUrl = CustContacts.getFriendThumbNailPhoto(account);

          Glide.get(ActomaController.getApp()).register(GlideUrl.class,
                  InputStream.class,
                  new OkHttpUrlLoader.Factory(OkHttpsClient.getInstance(ActomaController.getApp()).getOkHttpClient()));
          //modified by wxf@xdja.com 2016-07-22
          //Glide.with(ActomaController.getApp()).load(BuildImageUrl.getGlideUrl(imageUrl)).asBitmap().into(new SimpleTarget<Bitmap>() {
          Glide.with(ActomaController.getApp()).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {

               @Override
               public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    if (shouldShow) {
                         showNotificationForCall(target, account, null);
                    }
               }

               @Override
               public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Bitmap bitmap = resource;
                    if (shouldShow) {
                         showNotificationForCall(target, account, bitmap);
                    }
               }
          });
     }


     /**
      * 显示未接来电的通知
      */
     public void showNotificationForMissedCall(final String account) {

          final String imageUrl = ContactModuleProxy.getContactInfo(account).getThumbnailUrl();

          Glide.get(ActomaController.getApp()).register(GlideUrl.class,
                  InputStream.class,
                  new OkHttpUrlLoader.Factory(OkHttpsClient.getInstance(ActomaController.getApp()).getOkHttpClient()));
          //modified by wxf@xdja.com 2016-07-22
          //Glide.with(ActomaController.getApp()).load(BuildImageUrl.getGlideUrl(imageUrl)).asBitmap().into(new SimpleTarget<Bitmap>() {
          Glide.with(ActomaController.getApp()).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
               @Override
               public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    showNotificationForMissedCall(account, null);
               }

               @Override
               public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Bitmap bitmap = resource;
                    showNotificationForMissedCall(account, bitmap);
               }
          });
     }

     /**
      * 正在通话的通知 - 有头像的
      *
      * @param bitmap
      */
     private void showNotificationForCall(Class target, String account, Bitmap bitmap) {
          Bitmap largeIcon = null;
          cancelCalls();

          //20160729 mb 通过主框架启动activity
          NotificationIntent notificationIntent = new NotificationIntent(target.getName());
          PendingIntent contentIntent = notificationIntent.buildPendingIntent(ActomaController.getApp(), ((int) System.currentTimeMillis()));
//          Intent notificationIntent = new Intent(ActomaController.getApp(), target);
//          PendingIntent contentIntent = PendingIntent.getActivity(ActomaController.getApp(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

          /** 20160911-mengbo-start: 重新构建对象 **/
          //if (mNotificationBuilder == null) {
          mNotificationBuilder = new NotificationCompat.Builder(ActomaController.getApp());
          //}
          /** 20160911-mengbo-end **/

          //切割成圆形
          if (bitmap == null || bitmap.isRecycled()) {
               largeIcon = BitmapFactory.decodeResource(ActomaController.getApp().getResources(), R.drawable.ic_contact);

          } else {
               largeIcon = splitImage(bitmap);
          }

          mNotificationBuilder.setWhen(System.currentTimeMillis());
          mNotificationBuilder.setLargeIcon(largeIcon);
          mNotificationBuilder.setSmallIcon(R.drawable.af_abs_ic_logo);
          //wxf@xdja.com 2016-08-08 add. fix bug 2343 . review by mengbo. Start
          CallSession session = VOIPManager.getInstance().getCurSession();
          if (session != null && session.getState() == State.INCOMING) {
               mNotificationBuilder.setContentTitle(ActomaController.getApp().getString(R.string.ON_CALL_COMMING));
          }else{
               mNotificationBuilder.setContentTitle(ActomaController.getApp().getString(R.string.CONFIRMED));
          }
          //wxf@xdja.com 2016-08-08 add. fix bug 2343 . review by mengbo. End
          mNotificationBuilder.setContentText(CustContacts.getFriendName(account));
          mNotificationBuilder.setContentIntent(contentIntent);
          mNotificationBuilder.setAutoCancel(false);

          /** 20160911-mengbo-start: 正在通话的通知不可清除 **/
          mNotificationBuilder.setOngoing(true);
          /** 20160911-mengbo-end **/

          notificationManager.notify(NotifiParamUtil.CALL_NOTIF_ID, mNotificationBuilder.build());

     }

     /**
      *
      */
     public void cancelNotificationForCall() {
          notificationManager.cancel(NotifiParamUtil.CALL_NOTIF_ID);
     }


     /**
      * 未接来电通知 - 有头像的
      *
      * @param bitmap
      */
     public void showNotificationForMissedCall(String account, Bitmap bitmap) {
          Bitmap largeIcon = null;
          //显示的名称
          String displayName = CustContacts.getFriendName(account);
          //未接来电数量
          int missedCallCount = CallLogHelper.getFiendMissedCallCount(account);

         /** 20161204-mengbo-start: 可能是来电超时的异常导致未接来电数量为0，加入数量判断，防止低概率通知栏未接来电显示0 **/
          if(missedCallCount <= 0){
              return;
          }
         /** 20161204-mengbo-end **/

          CharSequence tickerText = ActomaController.getApp().getText(R.string.missed_call);
          long when = System.currentTimeMillis();

          /** 20160911-mengbo-start: 重新构建对象 **/
          //if (mNotificationBuilder == null) {
          mNotificationBuilder = new NotificationCompat.Builder(ActomaController.getApp());
          //}
          /** 20160911-mengbo-end **/

          /**20160616-mengbo-start:修复通知栏的未接来电消息点击无反应，未跳转进入通话详情页面**/
          NotificationIntent notificationIntent = new NotificationIntent(CallDetailActivityPresenter.class.getName());
          /**20160818-mengbo-start:加入更新Activity标记，点击通知栏更新通话记录详情界面**/
          notificationIntent.putExtra(NotificationIntent.EXTRA_UPDATE_ACTIVITY, true);
          /**20160616-mengbo-end**/
          notificationIntent.putExtra(CallDetailActivityPresenter.CONTACT_ID_FLAG, account);
          notificationIntent.putExtra(CallDetailActivityPresenter.ACCOUNT_NAME, account);
          PendingIntent contentIntent = notificationIntent.buildPendingIntent(ActomaController.getApp()
                          , ((int) System.currentTimeMillis()));

          /**original code:
               //发送广播，清除其它未接来电的通知
               Intent intent = new Intent(DynamicReceiver4.CANCEL_MISSED_CALL_NOTIFICATION);
               PendingIntent contentIntent = PendingIntent.getBroadcast(ActomaController.getApp(), (int) System.currentTimeMillis(),
                       intent, PendingIntent.FLAG_CANCEL_CURRENT);
           **/
          /**20160616-mengbo-end**/

          mNotificationBuilder.setContentIntent(contentIntent);
          //切割成圆形图片
          //20151117 加个为空的判断，加载失败时，显示默认头像
          if (bitmap == null) {
               largeIcon = BitmapFactory.decodeResource(ActomaController.getApp().getResources(), R.drawable.ic_contact);
          } else {
               largeIcon = splitImage(bitmap);
          }
          mNotificationBuilder.setLargeIcon(largeIcon);
          //小图标一定要设置，否则整个Notification会显示不出来
          mNotificationBuilder.setSmallIcon(R.drawable.af_abs_ic_logo);
          //点击后消失
          mNotificationBuilder.setAutoCancel(true);
          //设置notification发生时声音与振动都有 xjq 2016-05-17 13:42
          mNotificationBuilder.setDefaults(Notification.DEFAULT_ALL);
          mNotificationBuilder.setTicker(tickerText);
          mNotificationBuilder.setWhen(when);
          mNotificationBuilder.setOnlyAlertOnce(true);
          mNotificationBuilder.setContentTitle(displayName);
          mNotificationBuilder.setContentText(missedCallCount + ActomaController.getApp().getString(R.string.MISSED_CALL));
          missedCallTag.add(account);

          //zjc 20151103 当与某人的未接来电已存在并且未被消除时，新生成的未接来电先取消，然后重新生成一条，这样可以刷新时间
          notificationManager.cancel(account, NotifiParamUtil.MISSED_CALL_NOTIF_ID);
          //wxf@xdja.com 2016-08-03 add. fix bug 1792 . review by mengbo. Start
          notificationManager.notify(account,NotifiParamUtil.MISSED_CALL_NOTIF_ID, mNotificationBuilder.build());
          //wxf@xdja.com 2016-08-03 add. fix bug 1792 . review by mengbo. End
     }

     /**
      * 未接来电通知 - 没有头像的
      *
      * @param callLog
      */
     /*public void showNotificationForMissedCall(ContentValues callLog) {
          int icon = R.drawable.af_abs_ic_logo;
          CharSequence tickerText = ActomaController.getApp().getText(R.string.missed_call);
          long when = System.currentTimeMillis();
          if (missedCallNotification == null) {
               mNotificationBuilder = new NotificationCompat.Builder(ActomaController.getApp());
               mNotificationBuilder.setLargeIcon(BitmapFactory.decodeResource(ActomaController.getApp().getResources(), R.drawable.ic_contact));
               mNotificationBuilder.setSmallIcon(icon);
               mNotificationBuilder.setTicker(tickerText);
               mNotificationBuilder.setWhen(when);
               mNotificationBuilder.setOnlyAlertOnce(false);
               mNotificationBuilder.setAutoCancel(true);
               mNotificationBuilder.setDefaults(Notification.DEFAULT_ALL);
          }
          *//**
           * 这是一个悲惨的故事
           * 20150827 现在点击通知之后会跳转到通话记录的fragment，并且，清除其它的未接来电
           * 所以，先发出一个自定义的取消通知广播，广播收到后，清空所有的未接来电。
           * 然后，跳转到通话记录的fragment
           * **//*
          Intent intent = new Intent(DynamicReceiver4.CANCEL_MISSED_CALL_NOTIFICATION);
          //获取完整的sip url
          String remoteContact = callLog.getAsString(CallLog.Calls.NUMBER);
          String actomaAccount = parseAccountFromCallLog(callLog);
          long accId = callLog.getAsLong(SipManager.CALLLOG_PROFILE_ID_FIELD);
          //number格式化
          String name = CustContacts.getFriendName(parseAccountFromCallLog(callLog));
          //mNotificationBuilder.setWhen(callLog.getAsLong(CallLog.Calls.DATE));
          String showContent = name + com.securevoip.utils.CallLogHelper.getFiendMissedCallCount(actomaAccount) + "条未接来电";
          mNotificationBuilder.setContentTitle(showContent);
          mNotificationBuilder.setContentText(ActomaController.getApp().getText(R.string.missed_call));
          PendingIntent contentIntent = PendingIntent.getBroadcast(ActomaController.getApp(), (int) System.currentTimeMillis(),
                  intent, PendingIntent.FLAG_CANCEL_CURRENT);
          mNotificationBuilder.setContentIntent(contentIntent);
          *//**zjc 20150828 使用number转化的安通账号标志不同人的notification，好一并清除**//*
          missedCallTag.add(actomaAccount);
          notificationManager.cancel(actomaAccount, MISSED_CALL_NOTIF_ID);
          notificationManager.notify(actomaAccount, MISSED_CALL_NOTIF_ID, mNotificationBuilder.build());
     }*/


     /**
      * 从格式化过的number中提取出name
      *
      * @param psci
      * @return
      */
     private String formatMissedCallTitle(ParsedSipContactInfos psci) {
          StringBuilder notifTitle = new StringBuilder();
          String uName = CustContacts.getFriendName(psci.userName);
          if (uName != null) {
               notifTitle.append(uName);
          } else if (psci.displayName != null) {
               notifTitle.append(psci.displayName);
          } else {
               notifTitle.append(psci.userName);
          }
          return notifTitle.toString();
     }

     // Cancels
     public final void cancelRegisters() {
          if (!isServiceWrapper) {
               LogUtil.getUtils(THIS_FILE).e("Trying to cancel a service notification from outside the service");
          }
     }

     public final void cancelCalls() {
          // 将头像显示标志置为false xjq 2015-10-16
          shouldShow = false;
          notificationManager.cancel(NotifiParamUtil.CALL_NOTIF_ID);
     }
     //wxf@xdja.com 2016-08-08 add. fix bug 1864、2394 . review by mengbo. Start
     public final void exitCancelCalls(){
          //退出登录，取消来电
          notificationManager.cancelAll();
     }
     //wxf@xdja.com 2016-08-08 add. fix bug 1864、2394 . review by mengbo. End

     public final void cancelMissedCalls() {
          notificationManager.cancel(NotifiParamUtil.CALLLOG_NOTIF_ID);
     }

     public final void cancelMissedCall(String actomaAccount) {
          notificationManager.cancel(actomaAccount, NotifiParamUtil.MISSED_CALL_NOTIF_ID);
     }

     public final void cancelMessages() {
          notificationManager.cancel(NotifiParamUtil.MESSAGE_NOTIF_ID);
     }

     public final void cancelVoicemails() {
          notificationManager.cancel(NotifiParamUtil.VOICEMAIL_NOTIF_ID);
     }

     public final void cancelAll() {
          if (isServiceWrapper) {
               cancelRegisters();
          }
          cancelMessages();
          cancelMissedCalls();
          cancelVoicemails();
     }

     /**
      * 清除所有的未接来电
      * 现在未接来电维护在一个集合中
      */
     public void cancelAllMissedCall() {
          for (int i = 0; i < missedCallTag.size(); i++) {
//               LogUtil.e(THIS_FILE, "清除" + missedCallTag.get(i) + "的未接来电通知");
               notificationManager.cancel(missedCallTag.get(i), NotifiParamUtil.MISSED_CALL_NOTIF_ID);
          }
          missedCallTag.clear();
     }

     /**
      * 清除与某个人的未接来电
      * @param tag
      */
     public void cancelSpecificMissedCall(String tag) {
          notificationManager.cancel(tag, NotifiParamUtil.MISSED_CALL_NOTIF_ID);
     }

     /**
      * 从通话记录中解析出安通账号
      *
      * @param callLog
      * @return
      */
     public static String parseAccountFromCallLog(ContentValues callLog) {
          String account = "";
          if (!TextUtils.isEmpty(callLog.getAsString(CallLog.Calls.NUMBER))) {
               account = callLog.getAsString(CallLog.Calls.NUMBER);
               account = SipUri.parseSipContact(account).userName;
          }

          return account;
     }

     /**
      * 头像切割成圆形
      *
      * @param source
      * @return
      */
     private Bitmap splitImage(Bitmap source) {
          if (source == null) {
               BitmapFactory.decodeResource(ActomaController.getApp().getResources(), R.drawable.ic_contact);
          }
          int size = Math.min(source.getWidth(), source.getHeight());
          int x = (source.getWidth() - size) / 2;
          int y = (source.getHeight() - size) / 2;

          Bitmap squared = null;
          Bitmap result = null;

          try {
               LogUtil.getUtils().d(THIS_FILE+ "bitmap create");
               squared = Bitmap.createBitmap(source, x, y, size, size);
               result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
          } catch (Exception e) {
               e.printStackTrace();
          }


          Canvas canvas = new Canvas(result);
          Paint paint = new Paint();
          paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
          paint.setAntiAlias(true);
          float r = size / 2f;
          canvas.drawCircle(r, r, r, paint);
          if (squared != null) {
               squared.recycle();
          }
          return result;
     }

}

