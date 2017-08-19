package com.xdja.voipsdk;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.csipsimple.api.MediaState;
import com.csipsimple.utils.Compatibility;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.voipsdk.mvp.ActivityPresenter;

import java.lang.ref.WeakReference;

import util.CustomDialog;
import util.PermissionHelper;
import util.ScreenObserverManager;
import webrelay.VOIPManager;
import webrelay.bean.CallSession;
import webrelay.bean.State;
import webrelay.inter.UpdateCurrentUI;

public class InCallPresenter extends ActivityPresenter<InCallCommand, InCallVu> implements InCallCommand, UpdateCurrentUI,ScreenObserverManager.OnScreenStateUpdateListener {

     private boolean needUpdateInfo = false;
     private static final String FLAG_NEED_UPDATE = "needUpdateUI";

     private boolean needRecordAudioPermission = true;

     private QuitHandler quitHandler = new QuitHandler(this);

     //麦克风权限请求码
     private static final int RECORDAUDIO_PERMISSION_REQUEST_CODE = 1;
     private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

     private CustomDialog mPermissionDialog;

     protected int activityOpenEnterAnimation;
     protected int activityOpenExitAnimation;
     protected int activityCloseEnterAnimation;
     protected int activityCloseExitAnimation;

     //保持CPU运行锁
     private PowerManager.WakeLock wakeLockWakeUp;

     @NonNull
     @Override
     protected Class<? extends InCallVu> getVuClass() {
          return ViewInCall.class;
     }

     @NonNull
     @Override
     protected InCallCommand getCommand() {
          return this;
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          needUpdateInfo = true;
          //注册锁屏监听 mengbo
          ScreenObserverManager.getInstance(this).register(this);
          //初始化进入、退出Activity动画参数
          initAcitvityAnimationParams();
          //设置进入Activity动画
          overridePendingTransition(activityOpenEnterAnimation, activityOpenExitAnimation);
          //唤醒保持CPU运行锁
          wakeLockWakeUpAcquire();
     }

     @Override
     public void finish() {
          super.finish();
          //设置退出Activity动画
          overridePendingTransition(activityCloseEnterAnimation,activityCloseExitAnimation);
     }

     public void checkRecordAudioPermission(){
          if (Build.VERSION.SDK_INT >= 23) {
               /** 20170310-mengbo-start: 加入权限必须开启判断，解决诺亚信R6权限没开启，通话崩溃问题 **/
               if(Compatibility.shouldRecordAudioPermissionOn()){
                    if(!PermissionHelper.hasAudioRecordPermission(this)){
                         onRequestPermissionsResult(RECORDAUDIO_PERMISSION_REQUEST_CODE
                                 , new String[]{PERMISSION_RECORD_AUDIO}
                                 , new int[]{PackageManager.PERMISSION_DENIED});
                    }
               }else{
                    if (ContextCompat.checkSelfPermission(this, PERMISSION_RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                         ActivityCompat.requestPermissions(this, new String[]{PERMISSION_RECORD_AUDIO}, RECORDAUDIO_PERMISSION_REQUEST_CODE);
                    }
               }
               /** 20170310-mengbo-end **/
          }
     }

     @Override
     protected void onResume() {
          VOIPManager.getInstance().addUpdateUIListener(this);
          super.onResume();

          if(needRecordAudioPermission){
               checkRecordAudioPermission();
          }

          /** 20170322-mengbo-start: curSession为null说明进程被杀，这里处理避免通话界面显示花屏 **/
          if(VOIPManager.getInstance().getCurSession() == null){
               Log.e("mb", "InCallPresenter onResume curSession is null to finish !");
               finish();
          }
          /** 20170322-mengbo-end **/
     }

     @Override
     protected void onPause() {
          super.onPause();
          VOIPManager.getInstance().remove(this);
          needUpdateInfo = true;
     }

     @Override
     protected void onStop() {
          super.onStop();
          needUpdateInfo = true;
     }

     @Override
     protected void onDestroy() {
          //注销锁屏监听 mengbo
          ScreenObserverManager.getInstance(this).unregister(this);
          super.onDestroy();
          needUpdateInfo = true;
          if (quitHandler != null) {
               quitHandler.removeCallbacksAndMessages(null);
          }

          //wxf@xdja.com 2016-09-20 add. fix bug 3932 . review by mengbo. Start
          //取消麦克风权限弹框
          dissmissPermissionDialog();
          //wxf@xdja.com 2016-09-20 add. fix bug 3932 . review by mengbo. End
		  //释放保持CPU运行锁
          wakeLockWakeUpRelease();
     }

     //wxf@xdja.com 2016-08-08 add. fix bug 2585 . review by mengbo gbc. Start
     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
          needRecordAudioPermission = false;
          if(grantResults.length <= 0){
               return;
          }
          if(requestCode == RECORDAUDIO_PERMISSION_REQUEST_CODE){
               if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LogUtil.getUtils().d("InCallPresenter - 权限申请成功");
               }else{
                    //弹出提示框
                    showPermissionDialog();
               }
          }
     }
     //wxf@xdja.com 2016-08-08 add. fix bug 2585 . review by mengbo gbc. End

     @Override
     protected void onSaveInstanceState(Bundle outState) {
          super.onSaveInstanceState(outState);
          outState.putBoolean(FLAG_NEED_UPDATE, needUpdateInfo);
          LogUtil.getUtils().d("InCallPresenter - " + needUpdateInfo);
     }

     @Override
     protected void onRestoreInstanceState(Bundle savedInstanceState) {
          super.onRestoreInstanceState(savedInstanceState);
          LogUtil.getUtils().d("InCallPresenter - " + savedInstanceState.getBoolean(FLAG_NEED_UPDATE));
     }

     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
         if(keyCode == KeyEvent.KEYCODE_VOLUME_UP
                 || keyCode ==KeyEvent.KEYCODE_VOLUME_DOWN){
             CallSession session = VOIPManager.getInstance().getCurSession();
             if (session != null && session.getState() == State.INCOMING) {
                  VOIPManager.getInstance().stopRing();
                 return true;
             }
         }
         return super.onKeyDown(keyCode, event);
     }

	  //modified by wxf
     @Override
     public void onBackPressed() {
          super.onBackPressed();
          this.finish();
     }

     @Override
     public void onScreenOn() {
          //用户解锁 mengbo
     }

     @Override
     public void onScreenOff() {
          //用户锁屏铃声停止 mengbo
          CallSession session = VOIPManager.getInstance().getCurSession();
          if (session != null && session.getState() == State.INCOMING) {
               VOIPManager.getInstance().stopRing();
          }
     }

     @Override
     public void hangup() {
          VOIPManager.getInstance().hangup();
     }

     @Override
     public void recall() {
          stopQuit();
          needRecordAudioPermission = true;
          VOIPManager.getInstance().recall();
     }

     @Override
     public void sendIM() {
          VOIPManager.getInstance().sendIM();
     }

     @Override
     public void answer() {
          VOIPManager.getInstance().answer();
     }

     @Override
     public void setMicrophoneMute(boolean isOpen) {
          VOIPManager.getInstance().setMicrophoneMute(isOpen);
     }

     @Override
     public void setSpeakerphoneOn(boolean isOpen) {
          VOIPManager.getInstance().setSpeakerphoneOn(isOpen);
     }

     @Override
     public void setBluetoothOn(boolean isOpen) {
          VOIPManager.getInstance().setBluetoothOn(isOpen);
     }

     @Override
     public void setCurrent() {
          CallSession callSession = VOIPManager.getInstance().getCurSession();
          if (callSession == null)
               return;
          VOIPManager.getInstance().setCallSessionState(callSession.getState());
          VOIPManager.getInstance().setCallSessionErrCode(callSession.getLastErrCode());
          VOIPManager.getInstance().updateMediaState();
     }


     @Override
     public void showNotify() {
          VOIPManager.getInstance().showNotify();
          VOIPManager.getInstance().remove(this);
     }

     @Override
     public void clearNotify() {
          VOIPManager.getInstance().clearNotificationForInCall();
     }

     private final Object callMutex = new Object();

     @Override
     public void updateMediaState(MediaState mediaState) {
          synchronized (callMutex) {
               runOnUiThread(new UpdateUIFromMediaRunnable(mediaState));
          }
     }

     @Override
     public void updateCallState(CallSession callSession) {
          runOnUiThread(new UpdateUIFromCallRunnable(callSession));
     }

     @Override
     public void updateStatusText(CallSession callSession) {
          runOnUiThread(new UpdateUIFromStatus(callSession));
     }

     /**
      * 根据异常状态刷新界面
      */
     private class UpdateUIFromStatus implements Runnable {
          private CallSession callSession;

          public UpdateUIFromStatus(CallSession callSession) {
               this.callSession = callSession;
          }

          @Override
          public void run() {
               getVu().showError(callSession);
          }
     }

     /**
      * 根据媒体状态刷新界面
      */
     private class UpdateUIFromMediaRunnable implements Runnable {

          private MediaState mediaState;

          public UpdateUIFromMediaRunnable(MediaState mediaState) {
               this.mediaState = mediaState;
          }

          @Override
          public void run() {
               getVu().updateMedia(mediaState);
          }
     }

     /**
      * 通过呼叫状态刷新界面`
      */
     private class UpdateUIFromCallRunnable implements Runnable {

          private CallSession callSession;

          public UpdateUIFromCallRunnable(CallSession callSession) {
               this.callSession = callSession;
          }

          @Override
          public void run() {
               if (callSession == null)
                    return;
               State state = callSession.getState();
               switch (state) {
                    case CALLING:
                         getVu().setNameAndAvatar(callSession);
                         getVu().showCallerViews(callSession);
                         needUpdateInfo = false;
                         break;
                    case INCOMING:
                         getVu().setNameAndAvatar(callSession);
                         getVu().showCalleeViews(callSession);
                         needUpdateInfo = false;
                         break;
                    case CONECTTING:
                         if (needUpdateInfo) {
                              getVu().setNameAndAvatar(callSession);
                              needUpdateInfo = false;
                         }
                         getVu().showViewsAfterCallSuccess(callSession);
                         break;
                    case CONFIRMED:
                         if (needUpdateInfo) {
                              getVu().setNameAndAvatar(callSession);
                              needUpdateInfo = false;
                         }
                         getVu().showViewsAfterConnect(callSession);
                         break;
                    case DISCONNECTING:
                         /** 20160921-mengbo-start: 解决提示"您拨打的用户正在通话中"时，返回，呼叫，界面不刷新问题 **/
                         if (needUpdateInfo) {
                              getVu().setNameAndAvatar(callSession);
                              needUpdateInfo = false;
                         }
                         /** 20160921-mengbo-end **/
                         getVu().showViewsOnDisconnecting(callSession);
                         break;
                    case DISCONNECTED:
                         /** 20161019-mengbo-start: 解决万维退出通话界面，切后台，侧键挂断，进入通话结束界面，界面头像不显示问题 **/
                         if (needUpdateInfo) {
                              getVu().setNameAndAvatar(callSession);
                              needUpdateInfo = false;
                         }
                         /** 20161019-mengbo-end **/
                         getVu().showViewsAfterDisconnected(callSession);
                         break;
               }
               quit(state);
          }
     }

     public static final int START = 824;
     public static final int STOP = 323;

     /** 20161105-mengbo-start: 加入海信万维侧键退出 **/
     private static class QuitHandler extends Handler {
          private final WeakReference<InCallPresenter> mActivity;

          private QuitRunnable runnable;
          private WanWeiQuitRunnable wanWeirunnable;

          public QuitHandler(InCallPresenter activity) {
               mActivity = new WeakReference<>(activity);
               if(VOIPManager.getInstance().isHanupTypeNormal()){
                    runnable = new QuitRunnable(mActivity);
               }else{
                    wanWeirunnable = new WanWeiQuitRunnable(mActivity);
               }
          }

          @Override
          public void handleMessage(Message msg) {

               if(VOIPManager.getInstance().isHanupTypeNormal()){
                    if (msg.what == START) {
                         InCallPresenter activity = mActivity.get();
                         if (activity != null) {
                              if(runnable == null){
                                   runnable = new QuitRunnable(mActivity);
                              }
                              postDelayed(runnable, when);
                              LogUtil.getUtils().d("InCallPresenter - " + new StringBuilder().append("开始延时退出"));
                         }
                    } else if (msg.what == STOP) {
                         removeCallbacks(runnable);
                         LogUtil.getUtils().d("InCallPresenter - " + new StringBuilder().append("延时退出取消"));
                    }
               }else{
                    if (msg.what == START) {
                         InCallPresenter activity = mActivity.get();
                         if (activity != null) {
                              if(wanWeirunnable == null){
                                   wanWeirunnable = new WanWeiQuitRunnable(mActivity);
                              }
                              postDelayed(wanWeirunnable, when_wanwei);
                              LogUtil.getUtils().d("InCallPresenter - " + new StringBuilder().append("开始延时退出"));
                         }
                    } else if (msg.what == STOP) {
                         removeCallbacks(wanWeirunnable);
                         LogUtil.getUtils().d("InCallPresenter - " + new StringBuilder().append("延时退出取消"));
                    }
               }
          }
     }
     /** 20161105-mengbo-end **/

     public static class QuitRunnable implements Runnable {

          private final WeakReference<InCallPresenter> activity;

          public QuitRunnable(WeakReference<InCallPresenter> activity) {
               this.activity = activity;
          }

          @Override
          public void run() {
               //mengbo 2016-08-30 start 如果存在通话，不关闭界面
               if (VOIPManager.getInstance().hasActiveCall()) {
                    return;
               }
               //mengbo 2016-08-30 end
               activity.get().finish();
          }

     }

     /** 20161105-mengbo-start: 海信万维侧键退出 **/
     public static class WanWeiQuitRunnable implements Runnable {

          private final WeakReference<InCallPresenter> activity;

          public WanWeiQuitRunnable(WeakReference<InCallPresenter> activity) {
               this.activity = activity;
          }

          @Override
          public void run() {
               //mengbo 2016-08-30 start 如果存在通话，不关闭界面
               if (VOIPManager.getInstance().hasActiveCall()) {
                    return;
               }
               //mengbo 2016-08-30 end

               ActivityStack.getInstanse().moveToBackAllActivities();

               try{
                    Thread.sleep(500);
               }catch(Exception e){
                    e.printStackTrace();
               }

               activity.get().finish();
          }

     }
     /** 20161105-mengbo-end **/

     public static long when = 2500;
     public static long when_wanwei = 500;

     /**
      * 开始定时退出
      */
     private void startQuit() {
          stopQuit();
          quitHandler.sendEmptyMessage(START);
     }

     /**
      * 停止定时推出
      */
     private void stopQuit() {
          quitHandler.sendEmptyMessage(STOP);
     }


     private void quit(State state) {
          if (state.equals(State.CALLING) || state.equals(State.INCOMING))
               stopQuit();
          if (state.equals(State.DISCONNECTED))
               startQuit();
     }

     private void showPermissionDialog() {
          if (mPermissionDialog == null) {
               mPermissionDialog = new CustomDialog(this);
               mPermissionDialog.setCancelable(false);
          }
          if (!mPermissionDialog.isShowing()) {
               mPermissionDialog.setTitle(ActomaController.getApp().getString(R.string.STOP_PERMISSION))
                       .setMessage(ActomaController.getApp().getString(R.string.GET_PERMISSION))
                       .setPositiveButton(ActomaController.getApp().getString(R.string.HANGUP_PERMISSION), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                 needRecordAudioPermission = false;
                                 VOIPManager.getInstance().hangup();
                                 mPermissionDialog.dismiss();
                            }
                       })
                       .setNegativeButton(ActomaController.getApp().getString(R.string.SET_PERMISSION), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                 needRecordAudioPermission = true;
                                 startSettings(InCallPresenter.this);
                                 mPermissionDialog.dismiss();
                            }
                       })
                       .show();
          }
     }

     private void dissmissPermissionDialog(){
          if(mPermissionDialog != null && mPermissionDialog.isShowing()){
               mPermissionDialog.dismiss();
               mPermissionDialog = null;
          }
     }

     private void startSettings(final Activity activity){
          Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
          activity.startActivity(intent);
     }

     /**
      * 初始化进入、退出Activity动画参数
      */
     @SuppressLint("ResourceType")
     private void initAcitvityAnimationParams(){
          TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
          int windowAnimationStyleResId = activityStyle.getResourceId(0,0);
          activityStyle.recycle();
          activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId,
                  new int[]{android.R.attr.activityOpenEnterAnimation
                          ,android.R.attr.activityOpenExitAnimation
                          ,android.R.attr.activityCloseEnterAnimation
                          ,android.R.attr.activityCloseExitAnimation});
          activityOpenEnterAnimation = activityStyle.getResourceId(0, 0);
          activityOpenExitAnimation = activityStyle.getResourceId(1, 0);
          activityCloseEnterAnimation = activityStyle.getResourceId(2, 0);
          activityCloseExitAnimation = activityStyle.getResourceId(3, 0);
          activityStyle.recycle();
     }

     /** 20161014-mengbo-start: PowerManager.PARTIAL_WAKE_LOCK 按Power键，不会进入Sleep，其它几个会进入Sleep **/
     //唤醒保持CPU运行锁
     private void wakeLockWakeUpAcquire(){
          PowerManager powerMgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
          wakeLockWakeUp = powerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
          wakeLockWakeUp.setReferenceCounted(false);
          wakeLockWakeUp.acquire();
     }

     // 释放保持CPU运行锁
     private void wakeLockWakeUpRelease(){
          if (wakeLockWakeUp != null && wakeLockWakeUp.isHeld()) {
               wakeLockWakeUp.release();
               wakeLockWakeUp = null;
          }
     }
     /** 20161014-mengbo-end **/
}
