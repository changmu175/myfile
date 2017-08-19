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
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 */
/**
 * This file contains relicensed code from Apache copyright of 
 * Copyright (C) 2008 The Android Open Source Project
 */

package com.securevoip.ui.incall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipConfigManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import webrelay.VOIPManager;

/**
 * Manages in call controls not relative to a particular call such as media
 * route
 */
public class InCallControls extends FrameLayout implements View.OnClickListener {

    private static final String THIS_FILE = "InCallControls";
    IOnCallActionTrigger onTriggerListener;

    private MediaState lastMediaState;
    private SipCallSession currentCall;

    private boolean supportMultipleCalls = false;
//    private LinearLayout speakerButton;
//    private LinearLayout muteButton;
    private ImageView speakerButton;
    private ImageView muteButton;

    private TextView speakerView;
    private TextView muteView;

    private ImageView speakerImage;
    private ImageView muteImage;

    public InCallControls(Context context) {
        this(context, null, 0);
    }

    public InCallControls(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("AndroidLintCutPasteId")
    public InCallControls(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);

        if (!isInEditMode()) {
            supportMultipleCalls = SipConfigManager.getPreferenceBooleanValue(
                    getContext(), SipConfigManager.SUPPORT_MULTIPLE_CALLS,
                    false);
        }
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        View menuView = LayoutInflater.from(context).inflate(
                R.layout.in_call_controls_menu, null);
//        speakerButton = (LinearLayout) menuView.findViewById(R.id.speakerButton);
        speakerButton = (ImageView) menuView.findViewById(R.id.speakerImage);
        speakerImage = (ImageView) menuView.findViewById(R.id.speakerImage);
//        muteButton = (LinearLayout) menuView.findViewById(R.id.muteButton);
        muteButton = (ImageView) menuView.findViewById(R.id.meteImage);
        muteImage = (ImageView) menuView.findViewById(R.id.meteImage);

        speakerView = (TextView) menuView.findViewById(R.id.speakerView);
        muteView = (TextView) menuView.findViewById(R.id.muteView);

        speakerButton.setOnClickListener(this);
        muteButton.setOnClickListener(this);
        this.addView(menuView, layoutParams);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setEnabledMediaButtons(false);
    }

    /**
     * 当前是否在通话中
     */
    private boolean callOngoing = false;

    public void setEnabledMediaButtons(boolean isInCall) {
        callOngoing = isInCall;
        setMediaState(lastMediaState);
    }



    /**
     * Registers a callback to be invoked when the user triggers an event.
     *
     * @param listener
     *            the OnTriggerListener to attach to this view
     */
    public void setOnTriggerListener(IOnCallActionTrigger listener) {
        onTriggerListener = listener;
    }

    private void dispatchTriggerEvent(int whichHandle) {
        if (onTriggerListener != null) {
            onTriggerListener.onTrigger(whichHandle, currentCall);
        }
    }

    //zjc 20151208 现在弃用关于扬声器和静音刷新的部分
    //原先的机制是，点击后底层打开设备（扬声器、静音、蓝牙），设备实际被打开后刷新界面，这个过程会有一秒左右延迟
    //修改后的机制是，点击后立即刷新界面，然后通知底层打开设备。在实际设备被打开之前，如果再点击，则有Toast提示
    public void setMediaState(MediaState mediaState) {
        lastMediaState = mediaState;
        boolean enabled, checked ,isHeadset,isBluetooth;
        // Mic
        if (lastMediaState == null) {
            enabled = callOngoing;
            checked = false;
        } else {
            enabled = lastMediaState.canMicrophoneMute;
            checked = lastMediaState.isMicrophoneMute;
        }
//        if (enabled) {
//            muteButton.setVisibility(View.VISIBLE);
//        } else {
//            muteButton.setVisibility(View.GONE);
//        }
        if (checked) {
            muteView.setVisibility(View.VISIBLE);
            muteImage.setBackgroundResource(R.drawable.btn_call_mute_press);
        } else {
            muteView.setVisibility(View.INVISIBLE);
            muteImage.setBackgroundResource(R.drawable.btn_call_mute_normal);
        }
        muteButton.setEnabled(true);
        // Speaker
        LogUtil.getUtils(THIS_FILE).d(">> Speaker " + lastMediaState);
        if (lastMediaState == null) {
            enabled = callOngoing;
            checked = false;
            isHeadset = false;
            isBluetooth = false;
        } else {
            LogUtil.getUtils(THIS_FILE).d(">> Speaker " + lastMediaState.isSpeakerphoneOn);
            enabled =  lastMediaState.canSpeakerphoneOn;
            checked = lastMediaState.isSpeakerphoneOn;
            isHeadset = lastMediaState.isHeadset;
            isBluetooth = lastMediaState.isBluetoothScoOn;
        }
//        if (enabled) {
//            speakerButton.setVisibility(View.VISIBLE);
//        } else {
//            speakerButton.setVisibility(View.VISIBLE);
//        }
   		/*     
   		if (checked) {
            speakerView.setVisibility(View.VISIBLE);
            speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark);
        } else {
            speakerView.setVisibility(View.INVISIBLE);
            speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark_off);
        }
        speakerButton.setEnabled(true);
        */
        if (!isHeadset && !isBluetooth) {
            speakerButton.setEnabled(true);
            speakerButton.setClickable(true);

            if (checked) {
                speakerView.setVisibility(View.VISIBLE);
                speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark);
            } else {
                speakerView.setVisibility(View.INVISIBLE);
                speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark_off);
            }

        } else {
            speakerImage.setBackgroundResource(R.drawable.ic_speaker_disable);
            speakerButton.setEnabled(false);
            speakerButton.setClickable(false);
        }

        //Bluetooth
        if (lastMediaState == null) {
            enabled = callOngoing;
            checked = false;
        } else {
            enabled =  lastMediaState.canBluetoothSco;
            checked = lastMediaState.isBluetoothScoOn;
        }
    }

    public void setMuteState(boolean isMuteOn) {
        if (isMuteOn) {
            muteImage.setBackgroundResource(R.drawable.btn_call_mute_press);
        } else {
            muteImage.setBackgroundResource(R.drawable.btn_call_mute_normal);
        }
    }

    /**
     * 手动刷新扬声器界面状态
     * @param isSpeakerOn 是否点亮扬声器
     */
    public void setSpeakerState(boolean isSpeakerOn) {
        if (isSpeakerOn) {
            speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark);
            speakerImage.setTag(1);
        } else {
            speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark_off);
            speakerImage.setTag(0);
        }
    }

    /**
     * 是否手动变暗扬声器界面状态
     */
    public void setSpeakerViewOff() {
        speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark_off);
        speakerImage.setTag(0);
    }

    /**
     * 禁止点击扬声器和静音
     */
    public void enableMediaButton() {
        speakerButton.setClickable(true);
        muteButton.setClickable(true);
    }

    /**
     * 禁止点击扬声器和静音
     */
    public void disableButton() {
        speakerButton.setClickable(false);
        muteButton.setClickable(false);
    }

    public boolean isSpeakerOn() {
        if ((Integer) speakerImage.getTag() == 1) { // xjq
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        if (id == R.id.speakerButton) {
        if (id == R.id.speakerImage) {

            /**20160618-mengbo-start:调整状态改变时机,因扬声器不能及时起效,切换扬声器延迟结束,状态改变**/
            if (speakerView.getVisibility() == View.VISIBLE) {

                dispatchTriggerEvent(IOnCallActionTrigger.SPEAKER_OFF);
            } else {
                dispatchTriggerEvent(IOnCallActionTrigger.SPEAKER_ON);
            }
            /**original code:
                if (speakerView.getVisibility() == View.VISIBLE) {
                    speakerView.setVisibility(View.INVISIBLE);
                } else {
                    speakerView.setVisibility(View.VISIBLE);
                }
                if (speakerView.getVisibility() == View.VISIBLE) {
                    dispatchTriggerEvent(IOnCallActionTrigger.SPEAKER_ON);
                } else {
                    dispatchTriggerEvent(IOnCallActionTrigger.SPEAKER_OFF);
                }
             **/
            /**20160618-mengbo-end**/
			speakerButton.setEnabled(false);
//        } else if (id == R.id.muteButton) {
        } else if (id == R.id.meteImage) {
            if (muteView.getVisibility() == View.VISIBLE) {
                muteView.setVisibility(View.INVISIBLE);
            } else {
                muteView.setVisibility(View.VISIBLE);
            }
            if (muteView.getVisibility() == View.VISIBLE) {
                dispatchTriggerEvent(IOnCallActionTrigger.MUTE_ON);
            } else {
                dispatchTriggerEvent(IOnCallActionTrigger.MUTE_OFF);
            }
            muteButton.setEnabled(false);
        }
    }

    /**
     * 设置三个控制按钮可以不可以点击
     */
    public void setAllButtonEnable(boolean isEnable){
        muteButton.setEnabled(isEnable);
        speakerButton.setEnabled(isEnable);
        if (isEnable){
         muteImage.setBackgroundResource(R.drawable.btn_call_mute_normal);
         speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark_off);
        }else {
        muteImage.setBackgroundResource(R.drawable.ic_mute_disable);
        speakerImage.setBackgroundResource(R.drawable.ic_speaker_disable);
        }
    }

    public void initAllButtonEnable(){
        muteButton.setEnabled(true);
        muteImage.setBackgroundResource(R.drawable.btn_call_mute_normal);

        if(!VOIPManager.getInstance().isHeadsetEnable()){
            speakerButton.setEnabled(true);
            speakerImage.setBackgroundResource(R.drawable.ic_sound_speakerphone_holo_dark_off);
        } else{
            speakerButton.setEnabled(false);
            speakerImage.setBackgroundResource(R.drawable.ic_speaker_disable);
        }
    }

}
