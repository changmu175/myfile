package com.xdja.voipsdk;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.utils.ScreenUtil;
import com.csipsimple.utils.keyguard.KeyguardWrapper;
import com.securevoip.ui.incall.CallProximityManager;
import com.securevoip.ui.incall.IOnCallActionTrigger;
import com.securevoip.ui.incall.InCallCard;
import com.securevoip.ui.incall.InCallControls;
import com.securevoip.ui.incall.locker.InCallAnswerControls;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.mvp.ActivityView;

import butterknife.ButterKnife;
import webrelay.VOIPManager;
import webrelay.bean.CallSession;
import webrelay.bean.Role;
import webrelay.bean.StatusCode;

import static webrelay.bean.State.CONFIRMED;

/**
 * Created by guoyaxin on 2016/1/7.
 */


public class ViewInCall extends ActivityView<InCallCommand> implements InCallVu, IOnCallActionTrigger, CallProximityManager.ProximityDirector, View.OnClickListener {

     private static final String THIS_FILE = "ViewInCall";


     InCallCard activeInCallCard;
     InCallControls inCallControls;
     Button endButton;
     Button recall;
     Button sendIM;
     LinearLayout delayFun;
     FrameLayout funGroup;
     InCallAnswerControls inCallAnswerControls;
     LinearLayout inCallContainer;

     /** 20161108-mengbo-start: 废弃ScreenLocker **/
//     ScreenLocker lockerOverlay;
     /** 20161108-mengbo-end **/

     RelativeLayout mainFrame;

     RelativeLayout answer_refuse_layout;
     ImageView reject_call, answer_call;

     // 感应器控制 xjq 2015-12-18
     private CallProximityManager proximityManager;

     private KeyguardWrapper keyguardManager;


     @Override
     public void init(LayoutInflater inflater, ViewGroup container) {
          super.init(inflater, container);
          activeInCallCard = getView(R.id.activeInCallCard);
          inCallControls = getView(R.id.inCallControls);
          endButton = getView(R.id.endButton);
          recall = getView(R.id.recall);
          sendIM = getView(R.id.sendIM);
          delayFun = getView(R.id.delay_fun);
          funGroup = getView(R.id.fun_group);
          inCallAnswerControls = getView(R.id.inCallAnswerControls);
          inCallContainer = getView(R.id.inCallContainer);

          /** 20161108-mengbo-start: 废弃ScreenLocker **/
//          lockerOverlay = getView(R.id.lockerOverlay);
          /** 20161108-mengbo-end **/

          mainFrame = getView(R.id.mainFrame);

          answer_refuse_layout = getView(R.id.answer_refuse_layout);
          reject_call = getView(R.id.reject_call);
          answer_call = getView(R.id.answer_call);

          endButton.setOnClickListener(this);
          sendIM.setOnClickListener(this);
          recall.setOnClickListener(this);

          reject_call.setOnClickListener(this);
          answer_call.setOnClickListener(this);

     }

     @Override
     public void onCreated() {
          super.onCreated();

//        registerHomeWatcher();
          // overlay

          /** 20161108-mengbo-start: 废弃ScreenLocker **/
//          lockerOverlay.setActivity(getActivity());
          proximityManager = new CallProximityManager(getActivity(), this, /*lockerOverlay*/null);
          /** 20161108-mengbo-end **/

          proximityManager.startTracking();

          keyguardManager = KeyguardWrapper.getKeyguardManager(getActivity());

          inCallControls.setOnTriggerListener(this);
          inCallAnswerControls.setOnTriggerListener(this);

     }

     @Override
     public void onTrigger(int whichAction, SipCallSession call) {
          proximityManager.restartTimer();

          switch (whichAction) {
               case TAKE_CALL:
                    getCommand().answer();
                    break;
               //被叫自己滑动挂断之后走到这里
               case DONT_TAKE_CALL:
               case REJECT_CALL:
               case TERMINATE_CALL:
                    getCommand().hangup();
                    break;
               case MUTE_ON:
               case MUTE_OFF:
                    getCommand().setMicrophoneMute(whichAction == MUTE_ON);
                    break;
               case SPEAKER_ON:
               case SPEAKER_OFF:
                    getCommand().setSpeakerphoneOn(whichAction == SPEAKER_ON);
                    break;
               case BLUETOOTH_ON:
               case BLUETOOTH_OFF:
                    getCommand().setBluetoothOn(whichAction == BLUETOOTH_ON);
                    break;
          }
     }

     @Override
     public void onDisplayVideo(boolean show) {

     }


     @Override
     public boolean shouldActivateProximity() {
          CallSession session = VOIPManager.getInstance().getCurSession();
          if (session != null && session.getState() == CONFIRMED)
               return true;

          return false;
     }

     @Override
     public void onProximityTrackingChanged(boolean acquired) {

     }

     @Override
     protected int getLayoutRes() {
          return R.layout.in_call_main;
     }

     @Override
     public void onStart() {
          super.onStart();
          keyguardManager.unlock();
     }

     @Override
     public void onResume() {
          super.onResume();
          getCommand().setCurrent();
          getCommand().clearNotify();
     }


     @Override
     public void onStop() {
          super.onStop();
          keyguardManager.lock();
          getCommand().showNotify();
     }

     @Override
     public void onDestroy() {
          super.onDestroy();
          proximityManager.releaseWakeLock();
          proximityManager.stopTracking();
          proximityManager.release(0);

//        unregisterHomeWatcher();
     }

     @Override
     public void setNameAndAvatar(CallSession callSession) {
          activeInCallCard.showUser(callSession.getUser());
     }

     //mengbo 2016-08-30 start 修改此方法，Disconnecting处理异常CallSession Code
     /**
      * 主叫界面setVisibility
      */
     @Override
     public void showCallerViews(CallSession callSession) {
//          inCallControls.setAllButtonEnable(true);
          inCallControls.initAllButtonEnable();
          inCallControls.setVisibility(View.VISIBLE);
          inCallAnswerControls.setVisibility(View.GONE);
          answer_refuse_layout.setVisibility(View.GONE);
          delayFun.setVisibility(View.GONE);
          endButton.setEnabled(true);
          endButton.setVisibility(View.VISIBLE);
          activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.CALLING));
          activeInCallCard.showExtraText(StatusCode.getComment(callSession.getLastErrCode()));
     }
     //mengbo 2016-08-30 end

     /**
      * 被叫界面
      */
     @Override
     public void showCalleeViews(CallSession callSession) {
          inCallControls.setVisibility(View.GONE);
          delayFun.setVisibility(View.GONE);
          endButton.setVisibility(View.GONE);
//          activeInCallCard.showUser(callSession.getUser());
          activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.INCOMING));
          //wxf@xdja.com 2016-09-30 add. fix bug 3935 . review by mengbo. Start
//          activeInCallCard.showExtraText("");
          //wxf@xdja.com 2016-09-30 add. fix bug 3935 . review by mengbo. End

          boolean isScreenOn = ScreenUtil.getScreenState(getActivity());
          LogUtil.getUtils(THIS_FILE).e("isScreenOn" + isScreenOn + "");
          if (ScreenUtil.getScreenState(getActivity())) {
               inCallAnswerControls.setVisibility(View.VISIBLE);
               answer_refuse_layout.setVisibility(View.GONE);
          } else {
               answer_refuse_layout.setVisibility(View.VISIBLE);
               inCallAnswerControls.setVisibility(View.GONE);
          }
          activeInCallCard.showExtraText(StatusCode.getComment(callSession.getLastErrCode()));
     }

     /**
      * 通话结束展示界面
      */
     @Override
     public void showViewsAfterDisconnected(CallSession callSession) {
          inCallControls.setVisibility(View.GONE);
          inCallAnswerControls.setVisibility(View.GONE);
          answer_refuse_layout.setVisibility(View.GONE);
          delayFun.setVisibility(View.VISIBLE);
          endButton.setVisibility(View.GONE);
          activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.DISCONNECTED));

          /** 20161230-mengbo-start: 通话结束界面，不显示“网络繁忙，请等待约10秒” **/
          if(callSession.getLastErrCode() == StatusCode.WAIT_DISCONNECTING_CALLBACK){
               callSession.setLastErrCode(StatusCode.SUCCESS);
               activeInCallCard.showExtraText(StatusCode.getComment(callSession.getLastErrCode()));
          }
          /** 20161230-mengbo-end **/
     }

     //mengbo 2016-10-08 start 优化此方法
     //mengbo 2016-08-30 start 修改此方法，Disconnecting处理异常CallSession Code
     /**
      * 正在挂断界面显示
      */
     @Override
     public void showViewsOnDisconnecting(CallSession callSession) {
          Integer code = Integer.parseInt(callSession.getCode());
          Role role = callSession.getRole();
          if(!code.equals(StatusCode.SUCCESS) && role == Role.CALLER){
               switch(code){
                    case StatusCode.CALLER_TIMEOUT:
                    case StatusCode.CALLEE_REJECT:
                    case StatusCode.CALLEE_BUSY:
                         inCallControls.setVisibility(View.VISIBLE);
                         inCallAnswerControls.setVisibility(View.GONE);
                         answer_refuse_layout.setVisibility(View.GONE);
                         delayFun.setVisibility(View.GONE);
                         endButton.setEnabled(true);
                         endButton.setVisibility(View.VISIBLE);
                         activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.MISSED));
                         activeInCallCard.showExtraText(StatusCode.getComment(callSession.getLastErrCode()));
                         break;
                    default:
                         activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.DISCONNECTING));
                         inCallAnswerControls.setVisibility(View.GONE);
                         answer_refuse_layout.setVisibility(View.GONE);
                         endButton.setVisibility(View.GONE);
                         inCallControls.setVisibility(View.GONE);
               }
          }else{
               activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.DISCONNECTING));
               inCallAnswerControls.setVisibility(View.GONE);
               answer_refuse_layout.setVisibility(View.GONE);
               endButton.setVisibility(View.GONE);
               inCallControls.setVisibility(View.GONE);
          }
     }
     //mengbo 2016-08-30 end
     //mengbo 2016-10-08 end

     /**
      * 电话接通后显示的界面
      */
     @Override
     public void showViewsAfterConnect(CallSession callSession) {
          Role role = callSession.getRole();
          if (role.equals(Role.CALLEE)) {
               inCallControls.setEnabled(true);
               endButton.setEnabled(true);
          }
          inCallControls.setVisibility(View.VISIBLE);
          inCallAnswerControls.setVisibility(View.GONE);
          answer_refuse_layout.setVisibility(View.GONE);
          delayFun.setVisibility(View.GONE);
          endButton.setVisibility(View.VISIBLE);
          String startTime = String.valueOf(callSession.getStartTime());
          activeInCallCard.startTimer(startTime);
//          activeInCallCard.showUser(callSession.getUser());
          activeInCallCard.showExtraText(StatusCode.getComment(callSession.getLastErrCode()));
     }


     //mengbo 2016-08-30 start 修改此方法，Disconnecting处理异常CallSession Code
     @Override
     public void showViewsAfterCallSuccess(CallSession callSession) {
          inCallControls.setVisibility(View.VISIBLE);
          inCallAnswerControls.setVisibility(View.GONE);
          answer_refuse_layout.setVisibility(View.GONE);
          delayFun.setVisibility(View.GONE);
          endButton.setVisibility(View.VISIBLE);
//          activeInCallCard.showUser(callSession.getUser());
          if (callSession.getRole() == Role.CALLER) {
               activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.WAIT_CONFIRMED));
          } else {
               activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.CONECTTING));
          }
          activeInCallCard.showExtraText(StatusCode.getComment(callSession.getLastErrCode()));
     }
     //mengbo 2016-08-30 end


     /**
      * 更新静音、蓝牙、扩音器等状态
      *
      * @param mediaState
      */
     @Override
     public void updateMedia(MediaState mediaState) {
          inCallControls.setMediaState(mediaState);
          proximityManager.updateProximitySensorMode();
     }

     @Override
     public void showError(CallSession callSession) {
          int code = callSession.getLastErrCode();
          if (code == StatusCode.CALLER_TIMEOUT) {
               activeInCallCard.updateCurrentState(ActomaController.getApp().getString(R.string.NOBODY));
          } else {
               activeInCallCard.showExtraText(StatusCode.getComment(callSession.getLastErrCode()));
          }
     }

     @SuppressLint("UNCHECKED_WARNING")
     public <T extends View> T getView(int resId) {
          View root = getView();
          View v = ButterKnife.findById(root, resId);
          return (T) v;
     }

     @Override
     public void onClick(View v) {
          if (v == endButton || v == reject_call) {
               getCommand().hangup();
          }

          if (v == sendIM) {
               getCommand().sendIM();
          }

          if (v == recall) {
               getCommand().recall();
          }

          if (v == answer_call) {
               getCommand().answer();
          }
     }


}
