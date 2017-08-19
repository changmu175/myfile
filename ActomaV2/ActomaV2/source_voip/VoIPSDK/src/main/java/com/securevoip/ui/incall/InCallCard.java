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

package com.securevoip.ui.incall;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.service.SipService;
import com.csipsimple.utils.PreferencesProviderWrapper;
import com.securevoip.contacts.CustContacts;
import com.securevoip.widget.StatusView;
import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import webrelay.bean.StatusCode;


public class InCallCard extends FrameLayout {

    private static final String THIS_FILE = "InCallCard";

    private SipCallSession callInfo;
    private SipCallSession pre_callInfo;
    private String cachedRemoteUri = "";

    private CircleImageView photo;
    private TextView remoteName, callStatusText;
    private StatusView extraStatusText;
    private Chronometer elapsedTime;
    private SurfaceView renderView;
    private PreferencesProviderWrapper prefs;
    //    private ViewGroup endCallBar;
//    private MenuBuilder btnMenuBuilder;
    private boolean hasVideo = false;
    private boolean canVideo = false;

    ObjectAnimator oa;

    public InCallCard(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.in_call_card, this, true);
        prefs = new PreferencesProviderWrapper(context);
        canVideo = prefs.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO);
        initControllerView();
    }

    public InCallCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.in_call_card, this, true);
        prefs = new PreferencesProviderWrapper(context);
        canVideo = prefs.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO);
        initControllerView();
    }

    public InCallCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.in_call_card, this, true);
        prefs = new PreferencesProviderWrapper(context);
        canVideo = prefs.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO);
        initControllerView();
    }
    private void initControllerView() {
        photo = (CircleImageView) findViewById(R.id.contact_photo);
        remoteName = (TextView) findViewById(R.id.contact_display_name);
        elapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
        elapsedTime.setFormat(ActomaController.getApp().getString(R.string.CONFIRMED_TIME));
        callStatusText = (TextView) findViewById(R.id.call_status_text);
        extraStatusText = (StatusView) findViewById(R.id.extra_status_text);
    }


    /**
     * 初始化当前展示的信息
     * @param name
     */
    public synchronized void showUser(String name){
           if (name==null || name.equals(""))
               return;
        userHandler.sendMessage(userHandler.obtainMessage(SHOW_USER_NAME,
                name));
    }

    public synchronized void showExtraText(String text) {
        if(TextUtils.isEmpty(text)) {
            userHandler.sendMessage(userHandler.obtainMessage(SHOW_EXTRA_TEXT, ""));
            return;
        }
        userHandler.sendMessage(userHandler.obtainMessage(SHOW_EXTRA_TEXT, text));

    }
    /**
     * 更新通话界面状态，比如正在连接、正在通话中等
     * @param state
     */
    public void updateCurrentState(String state){
         if (state==null || state.equals(""))
             return;
        userHandler.sendMessage(userHandler.obtainMessage(UPDATE_CURRENT_STATE,
                state));
    }


    /**
     * 开始计时
     */
    public void startTimer(String startTime){
        if (startTime==null|| startTime.equals(""))
            return;
        userHandler.sendMessage(userHandler.obtainMessage(SHOW_TIME, startTime));
    }

    /**
     * 停止计时
     */
    public void stopTimer(){
        if (elapsedTime==null)
            return;
        elapsedTime.stop();
    }

    /**
     * 更新界面显示文字
     */
    private static final int UPDATE_CURRENT_STATE=1;

    /**
     * 显示用户名字
     */
    private static final int SHOW_USER_NAME=2;

    /**
     * 显示通话时间
     */
    private static final int SHOW_TIME=3;

    /**
     * 显示一些特殊提示：比如网络断开，网络重连等
     */
    private static final int SHOW_EXTRA_TEXT = 4;

    private   Handler userHandler = new ContactLoadedHandler();

    @SuppressLint("AndroidLintHandlerLeak")
    private class ContactLoadedHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_CURRENT_STATE:
                    userHandler.removeMessages(UPDATE_CURRENT_STATE);
                    if (elapsedTime.isShown()){
                        elapsedTime.stop();
                        elapsedTime.setVisibility(GONE);
                    }
                    if (!callStatusText.isShown())
                        callStatusText.setVisibility(VISIBLE);
                    String state= (String) msg.obj;
                    callStatusText.setText(state);
                    break;
                case SHOW_USER_NAME:
                    userHandler.removeMessages(SHOW_USER_NAME);
                    String name= (String) msg.obj;
                    if (!remoteName.isShown())
                        remoteName.setVisibility(VISIBLE);
                    LogUtil.getUtils(THIS_FILE).e("remoteName" + " name=" + name + "  " + remoteName.isShown());
                    showUserName(name);
                    showPhoto(name);
                    break;
                case SHOW_EXTRA_TEXT:
                    userHandler.removeMessages(SHOW_EXTRA_TEXT);
                    String text= (String) msg.obj;
                    if (!extraStatusText.isShown())
                        extraStatusText.setVisibility(VISIBLE);
                    LogUtil.getUtils(THIS_FILE).e("extra text =" + text + "  " + extraStatusText.isShown());


                    if(text.equals(StatusCode.getComment(StatusCode.NETWORK_DISCONNECTED))) {
                        extraStatusText.setProgressVisibility(VISIBLE);
                    } else {
                        extraStatusText.setProgressVisibility(GONE);
                    }

                    extraStatusText.setText(text);
                    if(text.equals(StatusCode.getComment(StatusCode.NETWORK_CONNECT))) {
                        extraStatusText.dismissSelfDelay(3000);
                    }



                    break;
                case SHOW_TIME:
                    userHandler.removeMessages(SHOW_TIME);
                    if (callStatusText.isShown())
                        callStatusText.setVisibility(GONE);
                    if (!elapsedTime.isShown())
                        elapsedTime.setVisibility(VISIBLE);
                    String startTime= (String) msg.obj;
                    elapsedTime.setBase(Long.parseLong(startTime));
                    elapsedTime.start();
                    break;
            }
        }
    }

    /**
     * 显示头像
     * @param account
     */
    private void showPhoto(String account){
        String photoUrl = CustContacts.getFriendThumbNailPhoto(account);
        /*HeadImgParamsBean bean = HeadImgParamsBean.getParams(photoUrl);
        photo.loadImage(bean.getHost(), true, bean.getFileId(), bean.getSize(), R.drawable.incall_default, true);*/
        //TODO
        photo.loadImage(photoUrl, true, R.drawable.incall_default);
    }


    /**
     * 显示用户名
     * @param account
     */
    private void showUserName(String account){
        String contactname = CustContacts.getFriendName(account);
        if (null == contactname || contactname.isEmpty())
            contactname=account;
        remoteName.setText(contactname);
    }
    private IOnCallActionTrigger onTriggerListener;

    /*
     * Registers a callback to be invoked when the user triggers an event.
     * @param listener the OnTriggerListener to attach to this view
     */
    public void setOnTriggerListener(IOnCallActionTrigger listener) {
        onTriggerListener = listener;
    }


    private void dispatchTriggerEvent(int whichHandle) {
        if (onTriggerListener != null) {
            onTriggerListener.onTrigger(whichHandle, callInfo);
        }
    }


    public void terminate() {
        if (callInfo != null && renderView != null) {
            SipService.setVideoWindow(callInfo.getCallId(), null, false);
        }
    }


    private void setVisibleWithFade(View v, boolean in) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), in ? android.R.anim.fade_in : android.R.anim.fade_out);
        anim.setDuration(1000);
        v.startAnimation(anim);
        v.setVisibility(in ? View.VISIBLE : View.GONE);
    }



    public void resetCallStateText() {
        callStatusText.setText("");
        callStatusText.setVisibility(GONE);
    }

    public void setIncomingText() {
        callStatusText.setVisibility(VISIBLE);
        callStatusText.setText(ActomaController.getApp().getString(R.string.ON_CALL_COMMING_IN));
    }

    /**
     * 文字闪动
     */
    @SuppressLint("AccessStaticViaInstance")
    public void startBlink() {
        oa = new ObjectAnimator().ofFloat(callStatusText, "alpha", 0.5f, 1f);
        oa.setDuration(1000);
        oa.setRepeatMode(ValueAnimator.REVERSE);
        oa.setRepeatCount(60);
        oa.start();
    }

    /**
     * 文字停止闪动
     */
    private void stopBlink() {
        if (null != oa) {
            oa.cancel();
            callStatusText.setAlpha(1);
        }
    }


}
