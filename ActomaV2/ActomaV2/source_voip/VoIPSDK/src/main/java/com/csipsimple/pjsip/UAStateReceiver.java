/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * Copyright (C) 2010 Chris McCormick (aka mccormix - chris@mccormick.cx) 
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csipsimple.pjsip;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipCallSession.StatusCode;
import com.csipsimple.api.SipCallSession.StatusCommentReplace;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipManager.PresenceStatus;
import com.csipsimple.api.SipMessage;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipUri;
import com.csipsimple.api.SipUri.ParsedSipContactInfos;
import com.csipsimple.service.MediaManager;
import com.csipsimple.service.SipNotifications;
import com.csipsimple.service.SipService;
import com.csipsimple.service.SipService.SameThreadException;
import com.csipsimple.service.SipService.SipRunnable;
import com.csipsimple.service.impl.SipCallSessionImpl;
import com.csipsimple.utils.Compatibility;
import com.csipsimple.utils.Threading;
import com.csipsimple.utils.TimerWrapper;
import com.securevoip.pninter.PNMessageManager;
import com.securevoip.utils.CallLogHelper;
import com.securevoip.utils.StringUtil;
import com.securevoip.utils.ToastUtil;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.HttpUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import org.pjsip.pjsua.Callback;
import org.pjsip.pjsua.SWIGTYPE_p_int;
import org.pjsip.pjsua.SWIGTYPE_p_pjsip_rx_data;
import org.pjsip.pjsua.pj_str_t;
import org.pjsip.pjsua.pj_stun_nat_detect_result;
import org.pjsip.pjsua.pjsip_event;
import org.pjsip.pjsua.pjsip_redirect_op;
import org.pjsip.pjsua.pjsip_status_code;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsua_buddy_info;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webrelay.VOIPApi;
import webrelay.VOIPManager;
import webrelay.bean.CallSession;
import webrelay.bean.State;

/*import com.xdja.wx.frame.bean.TalkOutBean;
import com.xdja.wx.frame.datacontrol.DataInsertControl;
import com.xdja.wx.frame.datacontrol.TxlDataControl;
import com.xdja.wx.frame.utils.Function;
import com.xdja.wx.frame.utils.SharePreferenceUtils;*/

/**
 * Modify  by mengbo 2016-08-30 修改挂断流程，解决网络差挂不断问题
 */
@SuppressLint("SynchronizeOnNonFinalField")
public class UAStateReceiver extends Callback {
    private final static String TAG = "SIP_UA_Receiver";
    private final static String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
    private final static String ENCRIPT_PHONE_STATE_CHANGED = "android.intent.action.ENCRIPT_PHONE_STATE";

    private SipNotifications notificationManager;
    private PjSipService pjService;
    // private ComponentName remoteControlResponder;

    // Time in ms during which we should not relaunch call activity again
    final static long LAUNCH_TRIGGER_DELAY = 2000;
    private long lastLaunchCallHandler = 0;

    private int eventLockCount = 0;
    private boolean mIntegrateWithCallLogs;
    private boolean mPlayWaittone;
    private int mPreferedHeadsetAction;
    private boolean mAutoRecordCalls;
    private int mMicroSource;

    private void lockCpu() {
        if (eventLock != null) {
            LogUtil.getUtils(TAG).d("< LOCK CPU");
            eventLock.acquire();
            eventLockCount++;
        }
    }

    private void unlockCpu() {
        if (eventLock != null && eventLock.isHeld()) {
            eventLock.release();
            eventLockCount--;
            LogUtil.getUtils(TAG).d("> UNLOCK CPU " + eventLockCount);
        }
    }

    /*
     * private class IncomingCallInfos { public SipCallSession callInfo; public
     * Integer accId; }
     */
    @Override
    public void on_incoming_call(final int accId, final int callId, SWIGTYPE_p_pjsip_rx_data rdata) {
        lockCpu();
        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_incoming_call--callId:" + callId);

        // Check if we have not already an ongoing call
        boolean hasOngoingSipCall = false;

        // BEGIN:Set incoming flag. Add by xjq, 2015/4/15
        SipCallSessionImpl callInfo = null;
        try {
            callInfo = updateCallInfoFromStack(callId, null);
            callInfo.setIncoming(true);

            // 设置呼叫角色为被叫 xjq 2015-09-03
            callInfo.setRole(SipCallSession.PJSIP_ROLE_UAS);
            callInfo.setIsHanging(false);

        } catch(SameThreadException e) {
            e.printStackTrace();
        }
        // END:Set incoming flag.Add by xjq, 2015/4/15

        // 设置推送会话的callid xjq 2016-01-25
        String extraCallId = PjSipService.pjStrToString(pjsua.get_rx_data_header(
                pjsua.pj_str_copy(SipManager.HDR_EXTRA_CALL_ID), rdata));
        if(callInfo != null)
            callInfo.setExtraCallId(extraCallId);


        /* BEGIN:判断普通电话是否在通话中，若是则拒接新的加密电话来电。 Add by xjq, 2015/3/26 */
        if(pjService.service.getGSMCallState() != TelephonyManager.CALL_STATE_IDLE) {
            if (callInfo != null) {
                callInfo.setMissed(true);
            }

            Log.d("voip_disconnect", "UAStateReceiver on_incoming_call 判断普通电话是否在通话中，若是则拒接新的加密电话来电) -> (pjsua.call_hangup)");

            pjsua.call_hangup(callId, StatusCode.BUSY_HERE, null, null);
            unlockCpu();
            return;
        }
        /* END:判断普通电话是否在通话中，若是则拒接新的加密电话来电。 Add by xjq, 2015/3/26 */

        //不支持多个会话
        if (pjService != null && pjService.service != null) {
            SipCallSessionImpl[] calls = getCalls();
            if (calls != null) {
                for (SipCallSessionImpl existingCall : calls) {
                    //
                    if (!existingCall.isAfterEnded() && existingCall.getCallId() != callId ) {
                        if (!pjService.service.supportMultipleCalls || !existingCall.isHanging()) {  // Active call must not be hanging, add by xjq, 2015/1/29
                            LogUtil.getUtils(TAG).d("Settings to not support two call at the same time !!!");
                            // If there is an ongoing call and we do not support
                            // multiple calls
                            // Send busy here
                            if (callInfo != null) {
                                callInfo.setMissed(true);
                            }

                            Log.d("voip_disconnect", "UAStateReceiver on_incoming_call 不支持多个会话) -> (pjsua.call_hangup)");

                            pjsua.call_hangup(callId, StatusCode.BUSY_HERE, null, null);
                            unlockCpu();
                            return;
                        } else {
                            if(!existingCall.isHanging()) // 若有正在挂断中的回话，则也要响铃
                                hasOngoingSipCall = true;
                        }
                    }
                }
            }
        }

        try {
            LogUtil.getUtils(TAG).d("Incoming call << for account " + accId);

            // Extra check if set reference counted is false ???
//            if (!ongoingCallLock.isHeld()) {
//                ongoingCallLock.acquire();
//            }
            final String remContact = callInfo.getRemoteContact();
            callInfo.setIncoming(true);
            // set over flag, Add by xjq, 2015/2/26
            callInfo.setOver(false);

            //通知需要加载图片，需要Handler中进行
            Message message = new Message();
            message.obj = callInfo;
            message.what = CREATE_CALL_NOTIFICATION;
            msgHandler.sendMessage(message);

            // Auto answer feature
            SipProfile acc = pjService.getAccountForPjsipId(accId);
            Bundle extraHdr = new Bundle();
            fillRDataHeader("Call-Info", rdata, extraHdr);
//            final int shouldAutoAnswer = pjService.service.shouldAutoAnswer(remContact, acc,
//                    extraHdr);

            int shouldAutoAnswer = 200;

            LogUtil.getUtils(TAG).d("Should I anto answer ? " + shouldAutoAnswer);
            if (shouldAutoAnswer >= 200) {
                // Automatically answer incoming calls with 200 or higher final
                // code
                callInfo.setCallState(SipCallSession.InvState.INCOMING);

                LogUtil.getUtils("curSipCallId").e("UAStateReceiver--on_incoming_call--callStateChanged");


                PNMessageManager.getInstance().callStateChanged(callInfo);// 告知会话状态发生改变 xjq 2015-12-22

                pjService.callAnswer(callId, shouldAutoAnswer);
            } else {
                // Ring and inform remote about ringing with 180/RINGING
                pjService.callAnswer(callId, 180);

                if (pjService.mediaManager != null) {
                    if (pjService.service.getGSMCallState() == TelephonyManager.CALL_STATE_IDLE
                            && !hasOngoingSipCall) {
                        pjService.mediaManager.startRing(remContact);
                    } else {
                        pjService.mediaManager.playInCallTone(MediaManager.TONE_CALL_WAITING);
                    }
                }
                broadCastAndroidCallState("RINGING", remContact);
            }
            if (shouldAutoAnswer < 300) {
                // Or by api
//                launchCallHandler(callInfo, true);
                LogUtil.getUtils(TAG).d("Incoming call >>");
            }
        } catch (SameThreadException e) {
            // That's fine we are in a pjsip thread
        } finally {
            unlockCpu();
        }
    }


    @Override
    @SuppressLint("Wakelock")
    public void on_call_state(final int callId, pjsip_event e) {
        pjsua.css_on_call_state(callId, e);
        lockCpu();

        LogUtil.getUtils(TAG).d("Call state <<");
        try {
            // Get current infos now on same thread cause fix has been done on
            // pj
        	/* BEGIN: We need a changable callinfo instance. add by xjq, 2015/1/29 */
            // final SipCallSession callInfo = updateCallInfoFromStack(callId, e);
            SipCallSessionImpl callInfo = updateCallInfoFromStack(callId, e);

            LogUtil.getUtils(TAG).d("on_call_state call id = " + callInfo.getCallId() + " state = " + callInfo.getCallState());

            int callState = callInfo.getCallState();

            LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_call_state--callState:" + callState);

            if(callState == SipCallSession.InvState.CONFIRMED){
                VOIPManager.getInstance().setCurCallMode(VOIPApi.CALL_MODE_SIP);
            }

            if(callInfo != null){
                String sipSessionCallid = callInfo.getExtraCallId();
                if(VOIPManager.getInstance().getCurSession() != null){
                    String curSessionCallid = VOIPManager.getInstance().getCurSession().getCallid();

                    LogUtil.getUtils(TAG).d("UAStateReceiver--on_call_state" +
                            "--curSessionCallid:" + curSessionCallid +
                            "--sipSessionCallid:" + sipSessionCallid);

                    if(curSessionCallid != null
                            && sipSessionCallid != null
                            && !curSessionCallid.equals(sipSessionCallid)
                            && !curSessionCallid.equals("")){
                        return;
                    }

                    State curSessionCallState = VOIPManager.getInstance().getCurSession().getState();

                    LogUtil.getUtils(TAG).d("UAStateReceiver--on_call_state--curSessionCallState:" + curSessionCallState);
                    if(callState == SipCallSession.InvState.CONFIRMED
                            && (curSessionCallState == State.DISCONNECTING
                            || curSessionCallState == State.DISCONNECTED)){

                        Log.d("voip_disconnect", "UAStateReceiver on_call_state " +
                                "(callState == SipCallSession.InvState.CONFIRMED " +
                                "&& (curSessionCallState == State.DISCONNECTING " +
                                "|| curSessionCallState == State.DISCONNECTED) " +
                                "-> (pjsua.call_hangup)");

                        pjService.callHangup(callId, 0);
                        pjService.service.getBinder().setCallHangingState(callId, true);
                    }

                    LogUtil.getUtils(TAG).d("UAStateReceiver--on_call_state--0000");

                }
            }


            // If disconnected immediate stop required stuffs
            if (callState == SipCallSession.InvState.DISCONNECTED) {

                /* BEGIN: Reset hanging flag. add by xjq, 2015/1/29 */
                callInfo.setIsHanging(false);
            	/* END: Reset hanging flag. add by xjq, 2015/1/29 */

                if (pjService.mediaManager != null) {
                    if(getRingingCall() == null) {
                        pjService.mediaManager.stopRingAndUnfocus();
                        pjService.mediaManager.resetSettings();
                    }
                }
//                if (ongoingCallLock != null && ongoingCallLock.isHeld()) {
//                    ongoingCallLock.release();
//                }
                // Call is now ended
                pjService.stopDialtoneGenerator(callId);

                /** 20161108-mengbo-start: 关闭自动录音调试 **/
//                pjService.stopRecording(callId);
                /** 20161108-mengbo-end **/

                pjService.stopPlaying(callId);
                pjService.stopWaittoneGenerator(callId);
            } else {
                //这个地方一定要先置isHang的状态，再setOver()
                // 设置呼叫角色：主叫 or 被叫？xjq 2015-09-03
                if(callState == SipCallSession.InvState.CALLING) {
                    callInfo.setRole(SipCallSession.PJSIP_ROLE_UAC);
                    // 关闭扬声器 xjq 2015-11-19
//                    pjService.setSpeakerphoneOn(false);
                    callInfo.setIsHanging(false);
                }

                // set over flag false
                if(callInfo.isActive()) {
                    callInfo.setReconnect(false);
                    callInfo.setOver(false);

                }

//                if (ongoingCallLock != null && !ongoingCallLock.isHeld()) {
//                    ongoingCallLock.acquire();
//                }
            }

            if(callInfo.getLastStatusCode() == StatusCode.NOT_FOUND) {
                msgHandler.sendMessageDelayed(msgHandler.obtainMessage(ON_CALL_STATE, callInfo.getCopy()), 1000);
            } else {
                msgHandler.sendMessage(msgHandler.obtainMessage(ON_CALL_STATE, callInfo.getCopy()));
            }
            LogUtil.getUtils(TAG).d("Call state >>");
        } catch (Exception ex) {
            // We don't care about that we are at least in a pjsua thread
        } finally {
            // Unlock CPU anyway
            unlockCpu();
        }

    }

    @Override
    public void on_call_tsx_state(int call_id, org.pjsip.pjsua.SWIGTYPE_p_pjsip_transaction tsx, pjsip_event e) {
        lockCpu();

        LogUtil.getUtils(TAG).d("Call TSX state <<");
        try {
            SipCallSessionImpl callInfo = updateCallInfoFromStack(call_id, e);

            LogUtil.getUtils(TAG).d("UAStateReceiver--on_call_tsx_state" +
                    "--callInfo.getExtraCallId:" + callInfo.getExtraCallId() +
                    "--callInfo.getCallState:" + callInfo.getCallState());
            LogUtil.getUtils(TAG).d("Call TSX state >>");
        } catch (SameThreadException ex) {
            // We don't care about that we are at least in a pjsua thread
        } finally {
            // Unlock CPU anyway
            unlockCpu();
        }
    }

    @Override
    public void on_buddy_state(int buddyId) {
        lockCpu();

        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_buddy_state--buddyId:" + buddyId);

        pjsua_buddy_info binfo = new pjsua_buddy_info();
        pjsua.buddy_get_info(buddyId, binfo);

        LogUtil.getUtils(TAG).d("On buddy " + buddyId + " state " + binfo.getMonitor_pres() + " state "
                + PjSipService.pjStrToString(binfo.getStatus_text()));
        PresenceStatus presStatus = PresenceStatus.UNKNOWN;
        // First get info from basic status
        String presStatusTxt = PjSipService.pjStrToString(binfo.getStatus_text());
        boolean isDefaultTxt = presStatusTxt.equalsIgnoreCase("Online")
                || presStatusTxt.equalsIgnoreCase("Offline");
        switch (binfo.getStatus()) {
            case PJSUA_BUDDY_STATUS_ONLINE:
                presStatus = PresenceStatus.ONLINE;
                break;
            case PJSUA_BUDDY_STATUS_OFFLINE:
                presStatus = PresenceStatus.OFFLINE;
                break;
            case PJSUA_BUDDY_STATUS_UNKNOWN:
            default:
                presStatus = PresenceStatus.UNKNOWN;
                break;
        }
        // Now get infos from RPID
        switch (binfo.getRpid().getActivity()) {
            case PJRPID_ACTIVITY_AWAY:
                presStatus = PresenceStatus.AWAY;
                if (isDefaultTxt) {
                    presStatusTxt = "";
                }
                break;
            case PJRPID_ACTIVITY_BUSY:
                presStatus = PresenceStatus.BUSY;
                if (isDefaultTxt) {
                    presStatusTxt = "";
                }
                break;
            default:
                break;
        }

//        pjService.service.presenceMgr.changeBuddyState(PjSipService.pjStrToString(binfo.getUri()),
//                binfo.getMonitor_pres(), presStatus, presStatusTxt);
        unlockCpu();
    }

    @Override
    public void on_pager(int callId, pj_str_t from, pj_str_t to, pj_str_t contact,
                         pj_str_t mime_type, pj_str_t body) {
        lockCpu();

        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_pager");

        long date = System.currentTimeMillis();
        String fromStr = PjSipService.pjStrToString(from);
        String canonicFromStr = SipUri.getCanonicalSipContact(fromStr);
        String contactStr = PjSipService.pjStrToString(contact);
        String toStr = PjSipService.pjStrToString(to);
        String bodyStr = PjSipService.pjStrToString(body);
        String mimeStr = PjSipService.pjStrToString(mime_type);

        // Sanitize from sip uri
        int slashIndex = fromStr.indexOf("/");
        if (slashIndex != -1){
            fromStr = fromStr.substring(0, slashIndex);
        }

        SipMessage msg = new SipMessage(canonicFromStr, toStr, contactStr, bodyStr, mimeStr,
                date, SipMessage.MESSAGE_TYPE_INBOX, fromStr);

        // Insert the message to the DB
        ContentResolver cr = pjService.service.getContentResolver();

        //20170224-mengbo : 动态获取URI
        //cr.insert(SipMessage.MESSAGE_URI, msg.getContentValues());
        cr.insert(SipMessage.getMessageUri(ActomaController.getApp()), msg.getContentValues());

        // Broadcast the message
        Intent intent = new Intent(SipManager.ACTION_SIP_MESSAGE_RECEIVED);
        // TODO : could be parcelable !
        intent.putExtra(SipMessage.FIELD_FROM, msg.getFrom());
        intent.putExtra(SipMessage.FIELD_BODY, msg.getBody());
        pjService.service.sendBroadcast(intent, SipManager.PERMISSION_USE_SIP);

        unlockCpu();
    }

    @Override
    public void on_pager_status(int callId, pj_str_t to, pj_str_t body, pjsip_status_code status,
                                pj_str_t reason) {
        lockCpu();

        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_pager_status");

        // TODO : treat error / acknowledge of messages
        int messageType = (status.equals(pjsip_status_code.PJSIP_SC_OK)
                || status.equals(pjsip_status_code.PJSIP_SC_ACCEPTED)) ? SipMessage.MESSAGE_TYPE_SENT
                : SipMessage.MESSAGE_TYPE_FAILED;
        String toStr = SipUri.getCanonicalSipContact(PjSipService.pjStrToString(to));
        String reasonStr = PjSipService.pjStrToString(reason);
        String bodyStr = PjSipService.pjStrToString(body);
        int statusInt = status.swigValue();

        LogUtil.getUtils(TAG).d("SipMessage in on pager status " + status.toString() + " / " + reasonStr);

        // Update the db
        ContentResolver cr = pjService.service.getContentResolver();
        ContentValues args = new ContentValues();
        args.put(SipMessage.FIELD_TYPE, messageType);
        args.put(SipMessage.FIELD_STATUS, statusInt);
        if (statusInt != StatusCode.OK
                && statusInt != StatusCode.ACCEPTED) {
            args.put(SipMessage.FIELD_BODY, bodyStr + " // " + reasonStr);
        }

        //20170224-mengbo : 动态获取URI
        //cr.update(SipMessage.MESSAGE_URI, args, SipMessage.FIELD_TO + "=? AND " + SipMessage.FIELD_BODY + "=? AND "
        //              + SipMessage.FIELD_TYPE + "=" + SipMessage.MESSAGE_TYPE_QUEUED, new String[] {toStr, bodyStr });
        cr.update(SipMessage.getMessageUri(ActomaController.getApp()), args, SipMessage.FIELD_TO + "=? AND " + SipMessage.FIELD_BODY
                        + "=? AND " + SipMessage.FIELD_TYPE + "=" + SipMessage.MESSAGE_TYPE_QUEUED, new String[] {toStr, bodyStr});

        // Broadcast the information
        Intent intent = new Intent(SipManager.ACTION_SIP_MESSAGE_RECEIVED);
        intent.putExtra(SipMessage.FIELD_FROM, toStr);
        pjService.service.sendBroadcast(intent, SipManager.PERMISSION_USE_SIP);
        unlockCpu();
    }

    /**
     * 上线回调，发送Register报文之后
     * @param accountId
     */
    @Override
    public void on_reg_state(final int accountId) {
        lockCpu();

        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_reg_state--accountId:" + accountId);

        pjService.service.getExecutor().execute(new SipRunnable() {
            @Override
            public void doRun() throws SameThreadException {
                // Update java infos
                pjService.updateProfileStateFromService(accountId);
            }
        });
        unlockCpu();
    }

    @Override
    public void on_call_media_state(final int callId) {

        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_call_media_state--callId:" + callId);

        // 暂时由callid来做业务代码 xjq 2015-09-10
        if(callId == StatusCommentReplace.NETWORK_DELAY) {
            lockCpu();
//            pjService.forceResetMediaMode();
            SipCallSession call = getActiveCallInProgress();
            if (call != null && call.getCallId() != SipCallSession.INVALID_CALL_ID) {
                Intent intent = new Intent(SipManager.ACTION_XDJA_SIP_CHANGED);
                intent.putExtra("CALL_ID", call.getCallId());
                intent.putExtra("STATE_CODE", StatusCommentReplace.NETWORK_DELAY);
                pjService.service.sendBroadcast(intent, SipManager.PERMISSION_USE_SIP);
            }

            unlockCpu();
            return;
        }else if(callId == StatusCommentReplace.CALL_DURATION_EXCEED){
            /** 20160909-mengbo-start: 通话已经到最大时长自动挂断 **/
            lockCpu();
            CallSession callSession = VOIPManager.getInstance().getCurSession();
            if (callSession != null && callSession.getState() != null
                    && callSession.getState() == State.CONFIRMED) {
                VOIPManager.getInstance().hangup();
            }
            unlockCpu();
            /** 20160909-mengbo-end **/
            return;
        }else if(callId == 1010) {
            LogUtil.getUtils(TAG).d("获取录音设备权限失败");
        }

        try{

            SipCallSessionImpl callInfo = updateCallInfoFromStack(callId, null);

            LogUtil.getUtils(TAG).d("on_call_state call id = " + callInfo.getCallId() + " state = " + callInfo.getCallState());

            int callState = callInfo.getCallState();

            LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_call_media_state--callState:" + callState);

            if(callInfo != null){
                String sipSessionCallid = callInfo.getExtraCallId();
                if(VOIPManager.getInstance().getCurSession() != null){
                    String curSessionCallid = VOIPManager.getInstance().getCurSession().getCallid();

                    LogUtil.getUtils(TAG).d("UAStateReceiver--on_call_state" +
                            "--curSessionCallid:" + curSessionCallid +
                            "--sipSessionCallid:" + sipSessionCallid);

                    if(curSessionCallid != null
                            && sipSessionCallid != null
                            && !curSessionCallid.equals(sipSessionCallid)
                            && !curSessionCallid.equals("")){
                        return;
                    }

                    State curSessionCallState = VOIPManager.getInstance().getCurSession().getState();

                    LogUtil.getUtils(TAG).d("UAStateReceiver--on_call_media_state--curSessionCallState:" + curSessionCallState);

                    if(callState != SipCallSession.InvState.DISCONNECTED){
                        if(curSessionCallState == State.DISCONNECTING
                                || curSessionCallState == State.DISCONNECTED){
                            return;
                        }
                    }

                    LogUtil.getUtils(TAG).d("UAStateReceiver--on_call_media_state--0000");

                }
            }
        } catch (SameThreadException e) {
            // Nothing to do we are in a pj thread here
        }


        pjsua.css_on_call_media_state(callId);

        lockCpu();
        if (pjService.mediaManager != null) {
            // Do not unfocus here since we are probably in call.
            // Unfocus will be done anyway on call disconnect
            pjService.mediaManager.stopRing();
        }

        try {
            final SipCallSession callInfo = updateCallInfoFromStack(callId, null);

            /*
             * Connect ports appropriately when media status is ACTIVE or REMOTE
             * HOLD, otherwise we should NOT connect the ports.
             */
            boolean connectToOtherCalls = false;
            int callConfSlot = callInfo.getConfPort();
            int mediaStatus = callInfo.getMediaStatus();
            if (mediaStatus == SipCallSession.MediaState.ACTIVE ||
                    mediaStatus == SipCallSession.MediaState.REMOTE_HOLD) {

                connectToOtherCalls = true;
                pjsua.conf_connect(callConfSlot, 0);
                pjsua.conf_connect(0, callConfSlot);

                // Adjust software volume
                if (pjService.mediaManager != null) {
                    pjService.mediaManager.setSoftwareVolume();
                }

                /** 20161108-mengbo-start: 关闭自动录音调试 **/
                // 调试使用开启自动录音 xjq
                // Auto record
//                if (pjService.canRecord(callId)
//                        && !pjService.isRecording(callId)) {
//                    pjService
//                            .startRecording(callId, SipManager.BITMASK_IN | SipManager.BITMASK_OUT);
//                }
                /** 20161108-mengbo-end **/
            }

            // Connects/disconnnect to other active calls (for conferencing).
            boolean hasOtherCall = false;
            synchronized (callsList) {
                if (callsList != null) {
                    for (int i = 0; i < callsList.size(); i++) {
                        SipCallSessionImpl otherCallInfo = getCallInfo(i);
                        if (otherCallInfo != null && otherCallInfo != callInfo) {
                            int otherMediaStatus = otherCallInfo.getMediaStatus();
                            if(otherCallInfo.isActive() && otherMediaStatus !=  SipCallSession.MediaState.NONE) {
                                hasOtherCall = true;
                                boolean connect = connectToOtherCalls && (otherMediaStatus == SipCallSession.MediaState.ACTIVE ||
                                        otherMediaStatus == SipCallSession.MediaState.REMOTE_HOLD);
                                int otherCallConfSlot = otherCallInfo.getConfPort();
                                if(connect) {
                                    pjsua.conf_connect(callConfSlot, otherCallConfSlot);
                                    pjsua.conf_connect(otherCallConfSlot, callConfSlot);
                                }else {
                                    pjsua.conf_disconnect(callConfSlot, otherCallConfSlot);
                                    pjsua.conf_disconnect(otherCallConfSlot, callConfSlot);
                                }
                            }
                        }
                    }
                }
            }

            // Play wait tone
            if(mPlayWaittone) {
                if(mediaStatus == SipCallSession.MediaState.REMOTE_HOLD && !hasOtherCall) {
                    pjService.startWaittoneGenerator(callId);
                }else {
                    pjService.stopWaittoneGenerator(callId);
                }
            }

            msgHandler.sendMessage(msgHandler.obtainMessage(ON_MEDIA_STATE, callInfo));
        } catch (SameThreadException e) {
            // Nothing to do we are in a pj thread here
        }

        unlockCpu();
    }

    @Override
    @SuppressLint("UnnecessaryContinue")
    public void on_mwi_info(int acc_id, pj_str_t mime_type, pj_str_t body) {
        lockCpu();
        // Treat incoming voice mail notification.

        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_mwi_info");

        String msg = PjSipService.pjStrToString(body);
        // Log.d(THIS_FILE, "We have a message :: " + acc_id + " | " +
        // mime_type.getPtr() + " | " + body.getPtr());

        boolean hasMessage = false;
        int numberOfMessages = 0;
        // String accountNbr = "";

        String lines[] = msg.split("\\r?\\n");
        // Decapsulate the application/simple-message-summary
        // TODO : should we check mime-type?
        // rfc3842
        Pattern messWaitingPattern = Pattern.compile(".*Messages-Waiting[ \t]?:[ \t]?(yes|no).*",
                Pattern.CASE_INSENSITIVE);
        // Pattern messAccountPattern =
        // Pattern.compile(".*Message-Account[ \t]?:[ \t]?(.*)",
        // Pattern.CASE_INSENSITIVE);
        Pattern messVoiceNbrPattern = Pattern.compile(
                ".*Voice-Message[ \t]?:[ \t]?([0-9]*)/[0-9]*.*", Pattern.CASE_INSENSITIVE);

        for (String line : lines) {
            Matcher m;
            m = messWaitingPattern.matcher(line);
            if (m.matches()) {
                LogUtil.getUtils(TAG).w("Matches : " + m.group(1));
                if ("yes".equalsIgnoreCase(m.group(1))) {
                    LogUtil.getUtils(TAG).d("Hey there is messages !!! ");
                    hasMessage = true;

                }
                continue;
            }
            /*
             * m = messAccountPattern.matcher(line); if(m.matches()) {
             * accountNbr = m.group(1); Log.d(THIS_FILE, "VM acc : " +
             * accountNbr); continue; }
             */
            m = messVoiceNbrPattern.matcher(line);
            if (m.matches()) {
                try {
                    numberOfMessages = Integer.parseInt(m.group(1));
                } catch (NumberFormatException e) {
                    LogUtil.getUtils(TAG).w("Not well formated number " + m.group(1));
                }
                LogUtil.getUtils(TAG).d("Nbr : " + numberOfMessages);
                continue;
            }
        }

        if (hasMessage && numberOfMessages > 0) {
            SipProfile acc = pjService.getAccountForPjsipId(acc_id);
            if (acc != null) {
                LogUtil.getUtils(TAG).d(acc_id + " -> Has found account " + acc.getDefaultDomain() + " "
                        + acc.id + " >> " + acc.getProfileName());
            }
            LogUtil.getUtils(TAG).d("We can show the voice messages notification");
//            notificationManager.showNotificationForVoiceMail(acc, numberOfMessages);
        }
        unlockCpu();
    }

    /* (non-Javadoc)
     * @see org.pjsip.pjsua.Callback#on_call_transfer_status(int, int, org.pjsip.pjsua.pj_str_t, int, org.pjsip.pjsua.SWIGTYPE_p_int)
     */
    @Override
    public void on_call_transfer_status(int callId, int st_code, pj_str_t st_text, int final_,
                                        SWIGTYPE_p_int p_cont) {
        lockCpu();

        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_call_transfer_status");
        if((st_code / 100) == 2) {

            Log.d("voip_disconnect", "UAStateReceiver on_call_transfer_status (st_code / 100) == 2) -> (pjsua.call_hangup)");

            pjsua.call_hangup(callId, 0, null, null);
        }
        unlockCpu();
    }


    public int on_validate_audio_clock_rate(int clockRate) {
        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_validate_audio_clock_rate");
        if (pjService != null) {
            return pjService.validateAudioClockRate(clockRate);
        }
        return -1;
    }

    @Override
    public void on_setup_audio(int beforeInit) {
        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_setup_audio");
        if (pjService != null) {
            pjService.setAudioInCall(beforeInit);
            LogUtil.getUtils(TAG).d("on_setup_audio");
        }
    }

    @Override
    public void on_teardown_audio() {
        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_teardown_audio");
        if (pjService != null) {
            pjService.unsetAudioInCall();
            LogUtil.getUtils(TAG).d("on_teardown_audio");
            VOIPManager.getInstance().restoreAudioState();
        }
    }

    @Override
    public pjsip_redirect_op on_call_redirected(int call_id, pj_str_t target) {
        LogUtil.getUtils(TAG).w("Ask for redirection, not yet implemented, for now allow all "
                + PjSipService.pjStrToString(target));
        return pjsip_redirect_op.PJSIP_REDIRECT_ACCEPT;
    }

    @Override
    public void on_nat_detect(pj_stun_nat_detect_result res) {
        LogUtil.getUtils(TAG).d("NAT TYPE DETECTED !!!" + res.getNat_type_name());
        if (pjService != null) {
            pjService.setDetectedNatType(res.getNat_type_name(), res.getStatus());
        }
    }

    @Override
    public int on_set_micro_source() {
        LogUtil.getUtils(TAG).d("******----UAStateReceiver--on_set_micro_source");
        return mMicroSource;
    }

    @Override
    public int timer_schedule(int entry, int entryId, int time) {
        return TimerWrapper.schedule(entry, entryId, time);
    }

    @Override
    public int timer_cancel(int entry, int entryId) {
        return TimerWrapper.cancel(entry, entryId);
    }

    /**
     * Map callId to known {@link com.csipsimple.api.SipCallSession}. This is cache of known
     * session maintained by the UA state receiver. The UA state receiver is in
     * charge to maintain calls list integrity for {@link com.csipsimple.pjsip.PjSipService}. All
     * information it gets comes from the stack. Except recording status that
     * comes from service.
     */
    private SparseArray<SipCallSessionImpl> callsList = new SparseArray<>();

    /**
     * Update the call information from pjsip stack by calling pjsip primitives.
     *
     * @param callId The id to the call to update
     * @param e the pjsip_even that raised the update request
     * @return The built sip call session. It's also stored in cache.
     * @throws com.csipsimple.service.SipService.SameThreadException if we are calling that from outside the pjsip
     *             thread. It's a virtual exception to make sure not called from
     *             bad place.
     */
    private SipCallSessionImpl updateCallInfoFromStack(Integer callId, pjsip_event e)
            throws SameThreadException {
        SipCallSessionImpl callInfo;
        LogUtil.getUtils(TAG).d("Updating call infos from the stack");
        synchronized (callsList) {
            callInfo = callsList.get(callId);
            if (callInfo == null) {
                callInfo = new SipCallSessionImpl();
                callInfo.setCallId(callId);
            }
        }
        // We update session infos. callInfo is both in/out and will be updated
        PjSipCalls.updateSessionFromPj(callInfo, e, pjService.service);
        // We update from our current recording state
        callInfo.setIsRecording(pjService.isRecording(callId));
        callInfo.setCanRecord(pjService.canRecord(callId));
        synchronized (callsList) {
            // Re-add to list mainly for case newly added session
            callsList.put(callId, callInfo);
        }
        return callInfo;
    }

    /**
     * Get call info for a given call id.
     *
     * @param callId the id of the call we want infos for
     * @return the call session infos.
     */
    public SipCallSessionImpl getCallInfo(Integer callId) {
        SipCallSessionImpl callInfo;
        synchronized (callsList) {
            callInfo = callsList.get(callId, null);
        }
        return callInfo;
    }

    /**
     * Get list of calls session available.
     *
     * @return List of calls.
     */
    public SipCallSessionImpl[] getCalls() {
        if (callsList != null) {
            List<SipCallSessionImpl> calls = new ArrayList<>();

            synchronized (callsList) {
                for (int i = 0; i < callsList.size(); i++) {
                    SipCallSessionImpl callInfo = getCallInfo(i);
                    if (callInfo != null) {
                        calls.add(callInfo);
                    }
                }
            }
            return calls.toArray(new SipCallSessionImpl[calls.size()]);
        }
        return new SipCallSessionImpl[0];
    }

    private WorkerHandler msgHandler;
    private HandlerThread handlerThread;
    private WakeLock ongoingCallLock;
    private WakeLock eventLock;

    // private static final int ON_INCOMING_CALL = 1;
    private static final int ON_CALL_STATE = 2;
    private static final int ON_MEDIA_STATE = 3;
    private static final int CREATE_CALL_NOTIFICATION = 4;
    private static final int DUMP_CALL_INFO = 5;
    private static final int SHOW_NOTIFY = 6;
    private static final int ON_MISSED_CALL = 7;

    private static final int DATA_STATISTIC_PERIOD = 5000;
    private static final int RECONNECT_TIMEOUT = 50000;
    private static final int TOTAL_DUMP_SIZE = 17;
    private static final int RX_INFO_IDX = 5;
    private static final int RX_PLR_IDX = 6;

    private static final int TX_INFO_IDX = 11;
    private static final int TX_PLR_IDX = 12;
    private static final float PLR_BOUND = 8.0f;

    private static final int RECORD_DEVICE_INUSE = 1000;



    private static class WorkerHandler extends Handler {
        WeakReference<UAStateReceiver> sr;
//        TxlDataControl txlDataControl;

        public WorkerHandler(Looper looper, UAStateReceiver stateReceiver) {
            super(looper);
            LogUtil.getUtils(TAG).d("Create async worker !!!");
            sr = new WeakReference<>(stateReceiver);
//            txlDataControl=new TxlDataControl(stateReceiver.pjService.service);
        }

        public void handleMessage(Message msg) {
            LogUtil.getUtils(TAG).d("WorkerHandler handleMessage msg.what=" + Integer.toString(msg.what));
            final UAStateReceiver stateReceiver = sr.get();
            if (stateReceiver == null) {
                return;
            }

            stateReceiver.lockCpu();
            switch (msg.what) {

                case SHOW_NOTIFY:
                    int code = (int) msg.obj;
                    switch (code) {
                        case RECORD_DEVICE_INUSE:
//                            ToastUtil.showNoRepeatToast(stateReceiver.pjService.service, "录音设备被占用，请关闭其他程序！");
                            HttpUtils.showError(ActomaController.getApp(),ActomaController.getApp().getString(R.string.RECORD_DEVICE_INUSE));
                            break;
                    }
                    break;
                case CREATE_CALL_NOTIFICATION :
//                    SipCallSession callInfo1 = (SipCallSession) msg.obj;
                    // 修改通知栏头像显示调用接口 xjq 2015-10-16
//                    stateReceiver.notificationManager.showNotificationForCall2(stateReceiver.pjService.service, callInfo1);
                    break;
                case DUMP_CALL_INFO:
                    int callId = (int) msg.obj;
                    try {

                        /** 20170103-mengbo-start: 服务没有创建不继续执行 **/
                        if(stateReceiver.pjService != null && !stateReceiver.pjService.isCreated()){
                            Log.d("voip_disconnect", " *** WorkerHandler handleMessage DUMP_CALL_INFO *** !pjService.isCreated to return");
                            break;
                        }
                        /** 20170103-mengbo-end **/

                        String infos = PjSipCalls.dumpCallInfo(callId);
                        SipCallSession session = stateReceiver.pjService.getCallInfo(callId);


                        if(session != null){
                            String curSessionCallid = "";
                            String sipSessionCallid = session.getExtraCallId();
                            if(VOIPManager.getInstance().getCurSession() != null){
                                curSessionCallid = VOIPManager.getInstance().getCurSession().getCallid();
                            }

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case DUMP_CALL_INFO:" +
                                    "curSessionCallid--11111--curSessionCallid:" + curSessionCallid + "" +
                                    "--sipSessionCallid:" + sipSessionCallid);

                            if((curSessionCallid != null
                                    && sipSessionCallid != null
                                    && !curSessionCallid.equals(sipSessionCallid)
                                    && !curSessionCallid.equals(""))){
                                break;
                            }

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case DUMP_CALL_INFO:curSessionCallid--22222");
                        }



                        String rxInfo = null;
                        String txInfo = null;

                        String rxPLR = null;
                        String txPLR = null;

                        ArrayList<String> infoArray = new ArrayList<>();
                        StringUtil.stringToLines(infos, infoArray);
                        LogUtil.getUtils(TAG).d("xjq dump call info array size " + infoArray.size());

                        if(infoArray.size() == TOTAL_DUMP_SIZE) {
                            rxInfo = infoArray.get(RX_INFO_IDX);
                            txInfo = infoArray.get(TX_INFO_IDX);

                            rxPLR = infoArray.get(RX_PLR_IDX);
                            txPLR = infoArray.get(TX_PLR_IDX);

                            Pattern patternByte = Pattern.compile("pkt ([\\s\\S]+?) B");
                            Pattern patternKB = Pattern.compile("pkt ([\\s\\S]+?)KB");
                            Pattern patternMB = Pattern.compile("pkt ([\\s\\S]+?)MB");

                            Pattern patternRxPLR = Pattern.compile("\\(([\\s\\S]+?)%");
                            Pattern patternTxPLR = Pattern.compile("\\(([\\s\\S]+?)%");

                            Matcher matcherRxPLR = patternRxPLR.matcher(rxPLR);
                            Matcher matcherTxPLR = patternTxPLR.matcher(txPLR);

                            float rxPLRValue = 0.0f;
                            float txPLRValue = 0.0f;

                            if(matcherRxPLR.find()) {
                                rxPLRValue = Float.parseFloat(matcherRxPLR.group(1));
                                LogUtil.getUtils(TAG).d("xjq 接收 丢包率 " + rxPLRValue + "%");
                            }
                            if(matcherTxPLR.find()) {
                                txPLRValue = Float.parseFloat(matcherTxPLR.group(1));
                                LogUtil.getUtils(TAG).d("xjq 发送 丢包率 " + txPLRValue + "%");
                            }

                            if(rxPLRValue >= PLR_BOUND || txPLRValue >= PLR_BOUND) {
                                if(rxPLRValue >= PLR_BOUND) {
                                    LogUtil.getUtils(TAG).d("xjq 对方网络状况不佳");
                                } else if(txPLRValue >= PLR_BOUND) {
                                    LogUtil.getUtils(TAG).d("xjq 网络状况不佳");
                                }

                                // 判断当前网络带宽
//                                ConnectivityManager cm =
//                                        (ConnectivityManager) stateReceiver.pjService.service.getSystemService(Context.CONNECTIVITY_SERVICE);
//                                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//
//                                if(networkInfo == null || !networkInfo.isConnected()) {
//                                    Log.d(THIS_FILE, "xjq 当前无可用网路连接，不需要重新协商参数");
//
//                                } else {
//                                    String currentBandType = stateReceiver.pjService.getBandType(
//                                            networkInfo);
//
//                                    if(currentBandType.equalsIgnoreCase(SipConfigManager.CODEC_WB)) {
//                                        // 需要重新协商为窄带参数
//                                        if(session.isLowBand()) {
//                                            Log.d(THIS_FILE, "xjq 需要重新协商媒体参数为窄带参数！");
//                                            VOIPManager.getInstance().onCallParamChanged(webrelay.bean.StatusCode.CHANGE_LOW_BAND);
//                                            stateReceiver.pjService.setCodecsPrioritiesManual(0);
//                                            stateReceiver.pjService.callReconnect(callId);
//                                            session.setLowBand(true);
//                                        }
//                                    }
//
//                                }

                            }


                            // 接收字节
                            StringBuilder sb = new StringBuilder();
                            float rxValue = -1.0f;
                            float txValue = -1.0f;
                            SipCallSession.TrafficCompany rxCompany = SipCallSession.TrafficCompany.Byte;
                            SipCallSession.TrafficCompany txCompany = SipCallSession.TrafficCompany.Byte;


                            Matcher matcherRxByte = patternByte.matcher(rxInfo);
                            if(matcherRxByte.find()) {
                                rxValue = Float.parseFloat(matcherRxByte.group(1));
                                sb.append(ActomaController.getApp().getString(R.string.RECEIVE) + " " + rxValue + "B");
                                rxCompany = SipCallSession.TrafficCompany.Byte;
                            } else {
                                Matcher matcherRxKB = patternKB.matcher(rxInfo);
                                if (matcherRxKB.find()) {
//                                Log.d(THIS_FILE, "xjq rx data size " + matcherRxKB.group(1) + "KB");
                                    rxValue = Float.parseFloat(matcherRxKB.group(1));
                                    sb.append(ActomaController.getApp().getString(R.string.RECEIVE) + " " + rxValue + "KB");
                                    rxCompany = SipCallSession.TrafficCompany.KiloByte;
                                } else {
                                    Matcher matcherRxMB = patternMB.matcher(rxInfo);
                                    if (matcherRxMB.find()) {
                                        rxValue = Float.parseFloat(matcherRxMB.group(1));
                                        sb.append(ActomaController.getApp().getString(R.string.RECEIVE) + " " + rxValue + "MB");
                                        rxCompany = SipCallSession.TrafficCompany.MByte;
                                    }
                                }
                            }

                            // 发送字节
                            Matcher matcherTxByte = patternByte.matcher(txInfo);
                            if(matcherTxByte.find()) {
                                txValue = Float.parseFloat(matcherTxByte.group(1));
                                sb.append(ActomaController.getApp().getString(R.string.SEND) + " " + txValue + "B");
                                txCompany = SipCallSession.TrafficCompany.Byte;
                            } else {
                                Matcher matcherTxKB = patternKB.matcher(txInfo);
                                if (matcherTxKB.find()) {
//                                Log.d(THIS_FILE, "xjq tx data size " + matcherTxKB.group(1) + "KB");
                                    txValue = Float.parseFloat(matcherTxKB.group(1));
                                    sb.append(ActomaController.getApp().getString(R.string.SEND) + " " + txValue + "KB");
                                    txCompany = SipCallSession.TrafficCompany.KiloByte;
                                } else {
                                    Matcher matcherTxMB = patternMB.matcher(txInfo);
//                                Log.d(THIS_FILE, "xjq tx data size " + matcherTxMB.group(1) + "MB");
                                    if (matcherTxMB.find()) {
                                        txValue = Float.parseFloat(matcherTxMB.group(1));
                                        sb.append(ActomaController.getApp().getString(R.string.SEND) + " " + txValue + "MB");
                                        txCompany = SipCallSession.TrafficCompany.MByte;
                                    }
                                }
                            }

                            // 比较新的接收到的数据包数目
                            float oldRxValue = session.getRxStatistics().getValue();
                            SipCallSession.TrafficCompany oldRxCompany = session.getRxStatistics().getCompany();

                            float oldTxValue = session.getTxStatistics().getValue();
                            SipCallSession.TrafficCompany oldTxCompany = session.getTxStatistics().getCompany();

                            // 首次初始化，赋值即可
                            if(oldRxValue < 0 || oldTxValue < 0) {
                                session.getRxStatistics().setValue(rxValue);
                                session.getRxStatistics().setCompany(rxCompany);

                                session.getTxStatistics().setValue(txValue);
                                session.getTxStatistics().setCompany(txCompany);
                            } else {
                                // 比较接收数目
                                if(rxCompany == oldRxCompany && rxValue == oldRxValue) {
//                                    VOIPManager.getInstance().setCallSessionErrCode(webrelay.bean.StatusCode.LOCAL_NETWORK_DELAY);
                                    int count = session.getNoPktRxCnt();
                                    session.setNoPktRxCnt(count + 1);

                                    int boundary = RECONNECT_TIMEOUT / DATA_STATISTIC_PERIOD;
                                    if(session.getNoPktRxCnt() >= boundary) {
                                        // 长时间没有接收到数据，挂断电话
//                                        Intent intent = new Intent(SipManager.ACTION_XDJA_SIP_CHANGED);
//                                        intent.putExtra("CALL_ID", callId);
//                                        intent.putExtra("STATE_CODE", StatusCommentReplace.NETWORK_DELAY);
//                                        stateReceiver.pjService.service.sendBroadcast(intent, SipManager.PERMISSION_USE_SIP);

                                        VOIPManager.getInstance().setCallSessionErrCode(webrelay.bean.StatusCode.NETWORK_DELAY_HANGUP);

                                        Log.d("voip_disconnect", "UAStateReceiver handleMessage 长时间没有接收到数据，挂断电话" +
                                                "-> (pjsua.call_hangup)");

                                        stateReceiver.pjService.callHangup(callId, 0);
                                    }
                                } else {
                                    // 记录当前统计结果
                                    session.getRxStatistics().setValue(rxValue);
                                    session.getRxStatistics().setCompany(rxCompany);
                                    session.getTxStatistics().setValue(txValue);
                                    session.getTxStatistics().setCompany(txCompany);

//                                    if(session.getNoPktRxCnt() > 0 ) {
//                                        VOIPManager.getInstance().setCallSessionErrCode(webrelay.bean.StatusCode.LOCAL_NETWORK_NORMAL);
//                                    }

                                    session.setNoPktRxCnt(0);
                                }
                            }

                            LogUtil.getUtils(TAG).d("xjq " + sb.toString() + " no pkg rx count " + session.getNoPktRxCnt());
                        }

                    } catch (SameThreadException e) {
                        e.printStackTrace();
                    }
                    Message m = new Message();
                    m.what = DUMP_CALL_INFO;
                    m.obj = callId;
                    sendMessageDelayed(m, DATA_STATISTIC_PERIOD);
                    break;

                case ON_CALL_STATE: {
                    SipCallSessionImpl callInfo = (SipCallSessionImpl) msg.obj;

                    if(callInfo != null){
                        String curSessionCallid = "";
                        String sipSessionCallid = callInfo.getExtraCallId();
                        if(VOIPManager.getInstance().getCurSession() != null){
                            curSessionCallid = VOIPManager.getInstance().getCurSession().getCallid();
                        }


                        LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:" +
                                "curSessionCallid--11111--curSessionCallid:" + curSessionCallid + "" +
                                "--sipSessionCallid:" + sipSessionCallid);

                        if((curSessionCallid != null
                                && sipSessionCallid != null
                                && !curSessionCallid.equals(sipSessionCallid)
                                && !curSessionCallid.equals(""))){
                            break;
                        }

                        LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:curSessionCallid--22222");
                    }

                    LogUtil.getUtils(TAG).d("UAStateReceiver的ON_CALL_STATE的callInfo的remoteContact" + callInfo.getRemoteContact());
                    SipCallSessionImpl oriCallInfoImpl = (SipCallSessionImpl)(stateReceiver.pjService.getCallInfo(callInfo.getCallId()));


                    //解决crash
                    if(oriCallInfoImpl == null){
                        return;
                    }

                    final int callState = callInfo.getCallState();
                    LogUtil.getUtils(TAG).d("WorkerHandler handleMessage callId = " + callInfo.getCallId() + "callState= " + Integer.toString(callState));

                    /** Begin:If this call is hanging, don't show in UI except disconnected.add by xjq, 2015/1/23 */
                    if(callInfo.isHanging()) {
                        switch (callState) {
                            case SipCallSession.InvState.INCOMING:
                            case SipCallSession.InvState.EARLY:
                            case SipCallSession.InvState.CONNECTING:
                            case SipCallSession.InvState.CONFIRMED:

                                if(callInfo.getRole() == SipCallSession.PJSIP_ROLE_UAS && callState == SipCallSession.InvState.EARLY)
                                    break;

                                if (stateReceiver.pjService != null
                                        && stateReceiver.pjService.service != null)
                                    try {

                                        Log.d("voip_disconnect", "UAStateReceiver handleMessage callInfo.isHanging() " +
                                                "-> (pjsua.call_hangup)");

                                        stateReceiver.pjService.callHangup(callInfo.getCallId(), 0);
                                    } catch (SameThreadException e) {
                                        e.printStackTrace();
                                    }
                                break;
                        }
                        break;
                    }
                    /** End:If this call is hanging, don't show in UI except disconnected.add by xjq, 2015/1/23 */
                    switch (callState) {
                        case SipCallSession.InvState.INCOMING:
                        case SipCallSession.InvState.CALLING:
                            //发广播给系统，通知当前有电话。避免和其他APP的Sip电话冲突
                            stateReceiver.broadCastAndroidCallState("RINGING", callInfo.getRemoteContact());
                            break;
                        case SipCallSession.InvState.EARLY:
                        case SipCallSession.InvState.CONNECTING:
                        case SipCallSession.InvState.CONFIRMED:
                            //发广播给系统，通知当前有电话。避免和其他APP的Sip电话冲突
                            stateReceiver.broadCastAndroidCallState("OFFHOOK", callInfo.getRemoteContact());

                            if (stateReceiver.pjService.mediaManager != null) {
                                if (callState == SipCallSession.InvState.CONFIRMED) {
                                    // Don't unfocus here
                                    stateReceiver.pjService.mediaManager.stopRing();
                                }
                            }

                            // 接通之后短震动 xjq 2015-11-04
                            if(callState == SipCallSession.InvState.CONFIRMED) {
                                stateReceiver.pjService.mediaManager.vibrate(200);

                                if(!oriCallInfoImpl.isStatisticsStarted()) {
                                    oriCallInfoImpl.setStatisticsStarted(true);
                                    Message message = new Message();
                                    message.what = DUMP_CALL_INFO;
                                    message.obj = callInfo.getCallId();
                                    sendMessageDelayed(message, DATA_STATISTIC_PERIOD);
                                }
                            }


                            // Auto send pending dtmf
                            if (callState == SipCallSession.InvState.CONFIRMED) {
                                stateReceiver.sendPendingDtmf(callInfo.getCallId());
                            }
                            // If state is confirmed and not already intialized
                            if (callState == SipCallSession.InvState.CONFIRMED
                                    && callInfo.getCallStart() == 0) {
//                                long curTime = System.currentTimeMillis();
                                long curTime = SystemClock.elapsedRealtime();
                                callInfo.setCallStart(curTime);
                                /* BEGIN:同时修改原始callinfo。 Add by xjq, 2015/3/27 */
                                oriCallInfoImpl.setCallStart(curTime);
                                /* END:同时修改原始callinfo。 Add by xjq, 2015/3/27 */
                            }

                            break;
                        case SipCallSession.InvState.DISCONNECTED:

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED");

                            // 停止状态统计 xjq
                            if(oriCallInfoImpl.isStatisticsStarted()) {
                                removeMessages(DUMP_CALL_INFO);
                                oriCallInfoImpl.setStatisticsStarted(false);
                            }

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--11111");

                            // 重新宽带参数 xjq
                            if(oriCallInfoImpl.isLowBand()) {
                                try {
                                    stateReceiver.pjService.setCodecsPrioritiesManual(1);
                                } catch (SameThreadException e) {
                                    e.printStackTrace();
                                }
                            }

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--22222");

                            if (stateReceiver.pjService.mediaManager != null && stateReceiver.getRingingCall() == null) {
                                stateReceiver.pjService.mediaManager.stopRing();
                            }

                            stateReceiver.broadCastAndroidCallState("IDLE",
                                    callInfo.getRemoteContact());

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--33333");

                            // If no remaining calls, cancel the notification
                            if (stateReceiver.getActiveCallInProgress() == null) {
                                //zjc 20151105;一般情况下，收到DISCONNECT状态，直接清除正在通话的通知。如果是播放语音的状态，在IncallActivity结束时再清除
                                if (!(callInfo.getLastStatusCode() == SipCallSession.StatusCode.BUSY_HERE || callInfo.getLastStatusCode() == SipCallSession.StatusCode.NOT_FOUND || callInfo.getLastStatusCode() == StatusCommentReplace.REQUEST_TIMEOUT_CODE)) {
                                    stateReceiver.notificationManager.cancelCalls();
                                }
                                // We should now ask parent to stop if needed
                                if (stateReceiver.pjService != null
                                        && stateReceiver.pjService.service != null) {
                                    stateReceiver.pjService.service
                                            .treatDeferUnregistersForOutgoing();
                                }
                            }

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--44444");
/*------------------------------------------------------------------start----------------------------------------------------------*/
                            // CallLogf
                            final ContentValues cv = CallLogHelper.logValuesForCall(callInfo, callInfo.getCallStart());

                            // 通话记录生成之后，重置incoming状态
                            oriCallInfoImpl.setIncoming(false);

                            // 通话记录生成之后，重置missed状态。Add by xjq, 2015/4/15
                            oriCallInfoImpl.setMissed(false);

                            /**Begin:sunyunlei 添加异常捕获，因为莫名出现diskIOException 2014-4-16**/
                            // Fill our own database
                            /*try{
                            	Uri uri = stateReceiver.pjService.service.getContentResolver().insert(
                            			SipManager.CALLLOG_URI, cv);
                            }catch(Exception e){
                            	e.printStackTrace();
                            }*/
//                            try{
//                                Uri uri = CallLogHelper.addCallLog(stateReceiver.pjService.service, cv);
//                                //zjc 20150908 取消被叫挂断后跳转到通话记录
//                                //BusProvider.getMainProvider().post(MissedCallOttoPost.missedCallEvent(stateReceiver.pjService.service));
//                            }catch(Exception e){
//                                e.printStackTrace();
//                            }
                            /**End:sunyunlei 添加异常捕获，因为莫名出现diskIOException 2014-4-16**/
/*-----------------------------------------------------------------end-----------------------------------------------------------*/
//                            Integer isNew = cv.getAsInteger(Calls.NEW);
//                            if (isNew != null && isNew == 1) {
//                                if (!TextUtils.isEmpty(hasImage(stateReceiver.pjService.service, cv))) {
//                                    final Context context = stateReceiver.pjService.service.getBaseContext();
//                                    stateReceiver.notificationManager.showNotificationForMissedCall(context, cv);
//                                } else {
//                                    stateReceiver.notificationManager.showNotificationForMissedCall(cv);
//                                }
//                            }

                            // If the call goes out in error...
                            /* 增加通话中，网络变化后通话自动挂断功能-Add-lixin-0514-S */

                            /**Begin:sunyunlei 添加密信未开通或者账户不存在时的Toast 提示 20150302**/
                            if (callInfo.getLastStatusCode() != 200 && callInfo.getLastReasonCode() != 200) {
                                if (callInfo.getLastStatusCode() >= 400 && callInfo.getLastStatusCode() < 800) {
                                    switch (callInfo.getLastStatusCode()) {
                                        case StatusCommentReplace.ACCOUNT_NOT_FOUND_CODE: //密信号未开通或者不存在
                                            // case StatusCommentReplace.SERVICE_UNAVAILABLE_CODE://服务器繁忙
                                            if(stateReceiver.pjService != null &&
                                                    stateReceiver.pjService.service != null)
                                                stateReceiver.pjService.service.notifyUserOfMessage(callInfo.getLastStatusComment(), 3000);

                                            break;
                                    }
                                }
                                /**End:sunyunlei 添加密信未开通或者账户不存在时的Toast 提示 20150302**/

                            }
                            /* 增加通话中，网络变化后通话自动挂断功能-Add-lixin-0514-E */

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--55555");

                            // If needed fill native database
                            if (stateReceiver.mIntegrateWithCallLogs) {
                                // Don't add with new flag
                                cv.put(Calls.NEW, false);
                                // Remove csipsimple custom entries
                                cv.remove(SipManager.CALLLOG_PROFILE_ID_FIELD);
                                cv.remove(SipManager.CALLLOG_STATUS_CODE_FIELD);
                                cv.remove(SipManager.CALLLOG_STATUS_TEXT_FIELD);

                                // Reformat number for callogs
                                ParsedSipContactInfos callerInfos = SipUri.parseSipContact(cv
                                        .getAsString(Calls.NUMBER));
                                if (callerInfos != null) {
                                    String phoneNumber = SipUri.getPhoneNumber(callerInfos);

                                    // Only log numbers that can be called by
                                    // GSM too.
                                    // TODO : if android 2.3 add sip uri also
                                    if (!TextUtils.isEmpty(phoneNumber)) {
                                        cv.put(Calls.NUMBER, phoneNumber);
                                        // For log in call logs => don't add as
                                        // new calls... we manage it ourselves.
                                        cv.put(Calls.NEW, false);
                                        ContentValues extraCv = new ContentValues();

                                        if (callInfo.getAccId() != SipProfile.INVALID_ID) {
                                            SipProfile acc = stateReceiver.pjService.service
                                                    .getAccount(callInfo.getAccId());
                                            if (acc != null && acc.display_name != null) {
                                                extraCv.put(CallLogHelper.EXTRA_SIP_PROVIDER,
                                                        acc.display_name);
                                            }
                                        }
                                        CallLogHelper.addCallLog(cv, extraCv);
                                    }
                                }
                            }

                            LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--66666");

                            callInfo.applyDisconnect();
                            // BEGIN: 重置callinfo状态,修改通话记录来电去电状态异常的BUG。Add by xjq, 2015/4/3
                            oriCallInfoImpl.applyDisconnect();
                            // END: 重置callinfo状态,修改通话记录来电去电状态异常的BUG。Add by xjq, 2015/4/3

                            break;
                        default:
                            break;
                    }

                    LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--77777");

                    stateReceiver.onBroadcastCallState(callInfo);

                    LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_CALL_STATE:SipCallSession.InvState.DISCONNECTED--88888");

                    PNMessageManager.getInstance().callStateChanged(callInfo);// 告知会话状态发生改变 xjq 2015-12-22
                    break;
                }
                case ON_MEDIA_STATE: {
                    SipCallSession mediaCallInfo = (SipCallSession) msg.obj;
                    SipCallSessionImpl callInfo = stateReceiver.callsList.get(mediaCallInfo.getCallId());

                    if(callInfo != null){
                        String curSessionCallid = "";
                        String sipSessionCallid = callInfo.getExtraCallId();
                        if(VOIPManager.getInstance().getCurSession() != null){
                            curSessionCallid = VOIPManager.getInstance().getCurSession().getCallid();
                        }

                        LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_MEDIA_STATE:" +
                                "curSessionCallid--11111--curSessionCallid:" + curSessionCallid + "" +
                                "--sipSessionCallid:" + sipSessionCallid);

                        if((curSessionCallid != null
                                        && sipSessionCallid != null
                                        && !curSessionCallid.equals(sipSessionCallid)
                                        && !curSessionCallid.equals(""))){
                            break;
                        }

                        LogUtil.getUtils(TAG).d("UAStateReceiver--case ON_MEDIA_STATE:curSessionCallid--22222");
                    }

                    callInfo.setMediaStatus(mediaCallInfo.getMediaStatus());
                    stateReceiver.callsList.put(mediaCallInfo.getCallId(), callInfo);
                    stateReceiver.onBroadcastCallState(callInfo);
                    break;
                }
                /**Begin:sunyunlei 添加未接来电消息处理 20141225**/
                case ON_MISSED_CALL:
//                    MissedCall mc=(MissedCall)msg.obj;
//                    final ContentValues cv=CallLogHelper.logValuesForCall(stateReceiver.pjService.service, mc);
//                    try{
//                        Uri uri = stateReceiver.pjService.service.getContentResolver().insert(
//                                SipManager.CALLLOG_URI, cv);
//                        if (!TextUtils.isEmpty(hasImage(stateReceiver.pjService.service, cv))) {
//                            Context context = stateReceiver.pjService.service.getBaseContext();
//                            stateReceiver.notificationManager.showNotificationForMissedCall(context, cv);
//                        } else {
//                            stateReceiver.notificationManager.showNotificationForMissedCall(cv);
//                        }
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
                    break;
                /**End:sunyunlei 添加未接来电消息处理 20141225**/
            }
            stateReceiver.unlockCpu();
        }
    }

    public void pauseCallStatics(int callId) {
        SipCallSession session = getCallInfo(callId);
        if(session.isStatisticsStarted()) {
            msgHandler.removeMessages(DUMP_CALL_INFO);
            session.setNoPktRxCnt(0);
            session.setStatisticsStarted(false);
        }
    }


    public void resumeCallStatics(int callId) {
        SipCallSession session = getCallInfo(callId);

        if(!session.isStatisticsStarted()) {
            session.setStatisticsStarted(true);
            Message message = new Message();
            message.what = DUMP_CALL_INFO;
            message.obj = callId;
            msgHandler.sendMessageDelayed(message, DATA_STATISTIC_PERIOD);
        }
    }

    // -------
    // Public configuration for receiver
    // -------

    public void initService(PjSipService srv) {
        pjService = srv;
        notificationManager = pjService.service.notificationManager;

        if (handlerThread == null) {
            handlerThread = new HandlerThread("UAStateAsyncWorker");
            handlerThread.start();
        }
        if (msgHandler == null) {
            msgHandler = new WorkerHandler(pjService.service.getMainLooper(), this);

        }

        if (eventLock == null) {
            PowerManager pman = (PowerManager) pjService.service
                    .getSystemService(Context.POWER_SERVICE);
            eventLock = pman.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "com.csipsimple.inEventLock");
            eventLock.setReferenceCounted(true);

        }
        if (ongoingCallLock == null) {
            PowerManager pman = (PowerManager) pjService.service
                    .getSystemService(Context.POWER_SERVICE);
            ongoingCallLock = pman.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "com.csipsimple.ongoingCallLock");
            ongoingCallLock.setReferenceCounted(false);
        }
    }

    public void stopService() {

        Threading.stopHandlerThread(handlerThread, true);
        handlerThread = null;
        msgHandler = null;

        // Ensure lock is released since this lock is a ref counted one.
        if (eventLock != null) {
            while (eventLock.isHeld()) {
                eventLock.release();
            }
            eventLock = null;
        }
//        if (ongoingCallLock != null) {
//            if (ongoingCallLock.isHeld()) {
//                ongoingCallLock.release();
//            }
//            ongoingCallLock = null;
//        }
    }

    public void reconfigure(Context ctxt) {
        mIntegrateWithCallLogs = SipConfigManager.getPreferenceBooleanValue(ctxt,
                SipConfigManager.INTEGRATE_WITH_CALLLOGS);
        mPreferedHeadsetAction = SipConfigManager.getPreferenceIntegerValue(ctxt,
                SipConfigManager.HEADSET_ACTION, SipConfigManager.HEADSET_ACTION_CLEAR_CALL);
        mAutoRecordCalls = SipConfigManager.getPreferenceBooleanValue(ctxt,
                SipConfigManager.AUTO_RECORD_CALLS);
        mMicroSource = SipConfigManager.getPreferenceIntegerValue(ctxt,
                SipConfigManager.MICRO_SOURCE);
        mPlayWaittone = SipConfigManager.getPreferenceBooleanValue(ctxt,
                SipConfigManager.PLAY_WAITTONE_ON_HOLD, false);
    }

    // --------
    // Private methods
    // --------

    /**
     * Broadcast csipsimple intent about the fact we are currently have a sip
     * call state change.<br/>
     * This may be used by third party applications that wants to track
     * csipsimple call state
     *
     * @param callInfo the new call state infos
     */
    private void onBroadcastCallState(final SipCallSession callInfo) {
        SipCallSession publicCallInfo = new SipCallSession(callInfo);
        Intent callStateChangedIntent = new Intent(SipManager.ACTION_SIP_CALL_CHANGED);
        callStateChangedIntent.putExtra(SipManager.EXTRA_CALL_INFO, publicCallInfo);
        pjService.service.sendBroadcast(callStateChangedIntent, SipManager.PERMISSION_USE_SIP);

    }

    /**
     * Broadcast to android system that we currently have a phone call. This may
     * be managed by other sip apps that want to keep track of incoming calls
     * for example.
     *
     * @param state The state of the call
     * @param number The corresponding remote number
     */
    public void broadCastAndroidCallState(String state, String number) {
        // Android normalized event
        if(!Compatibility.isCompatible(19)) {
            // Not allowed to do that from kitkat
            Intent intent = new Intent(ACTION_PHONE_STATE_CHANGED);
            intent.putExtra(TelephonyManager.EXTRA_STATE, state);
            if (number != null) {
                intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
            }
            intent.putExtra(pjService.service.getString(R.string.app_name), true);
            pjService.service.sendBroadcast(intent, android.Manifest.permission.READ_PHONE_STATE);
            //pjService.service.sendBroadcast(intent);
        } else {
            /**
             * zjc 20150901 这又是一个悲惨的故事
             * Compatibility.isCompatible(19)的意思是，手机的版本是否大于19，即安卓4.4，而不是手机版本仅为安卓4.4
             * 4.4版本之后，没有权限使用系统广播，因此我们需要自定义
             */
            Intent intent = new Intent(ENCRIPT_PHONE_STATE_CHANGED);
            intent.putExtra(TelephonyManager.EXTRA_STATE, state);
            if (number != null) {
                intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
            }
            intent.putExtra(pjService.service.getString(R.string.app_name), true);
            pjService.service.sendBroadcast(intent);
        }
    }

    /**
     * Start the call activity for a given Sip Call Session. <br/>
     * The call activity should take care to get any ongoing calls when started
     * so the currentCallInfo2 parameter is indication only. <br/>
     * This method ensure that the start of the activity is not fired too much
     * in short delay and may just ignore requests if last time someone ask for
     * a launch is too recent
     *
     * @param currentCallInfo2 the call info that raise this request to open the
     *            call handler activity
     */
    private synchronized void launchCallHandler(SipCallSession currentCallInfo2, boolean renew) { // add by xjq
        long currentElapsedTime = SystemClock.elapsedRealtime();
        // Synchronized ensure we do not get this launched several time
        // We also ensure that a minimum delay has been consumed so that we do
        // not fire this too much times
        // Specially for EARLY - CONNECTING states
        if (lastLaunchCallHandler + LAUNCH_TRIGGER_DELAY < currentElapsedTime || renew) {
            Context ctxt = pjService.service;

            // Launch activity to choose what to do with this call
            Intent callHandlerIntent = SipService.buildCallUiIntent(ctxt, currentCallInfo2);
            LogUtil.getUtils(TAG).d("Anounce call activity");
            ctxt.startActivity(callHandlerIntent);
            lastLaunchCallHandler = currentElapsedTime;
        } else {
            LogUtil.getUtils(TAG).d("Ignore extra launch handler");
        }
    }
    /**
     * Check if any of call infos indicate there is an active call in progress.
     *
     * @see com.csipsimple.api.SipCallSession#isActive()
     */
    public SipCallSession getActiveCallInProgress() {
        // Go through the whole list of calls and find the first active state.
        synchronized (callsList) {
            for (int i = 0; i < callsList.size(); i++) {
                SipCallSession callInfo = getCallInfo(i);
                if (callInfo != null && callInfo.isActive()) {
                    return callInfo;
                }
            }
        }
        return null;
    }


    /**
     * Get call need to reconnect. Add by xjq, 2015/4/15
     * @return call session need to reconnect after network recovers.
     */
    public SipCallSession getCallNeedReconnect() {
        synchronized (callsList) {
            for (int i = 0; i < callsList.size(); i++) {
                SipCallSession callInfo = getCallInfo(i);
                if (callInfo != null && callInfo.getCallState() == SipCallSession.InvState.CONFIRMED && callInfo.needReconnect()) {
                    return callInfo;
                }
            }
        }
        return null;
    }

    /**
     * 获取当前正在通话中的回话。xjq 2015-09-03
     * @return
     */
    public SipCallSession getCallConfirmed() {
        synchronized (callsList) {
            for (int i = 0; i < callsList.size(); i++) {
                SipCallSession callInfo = getCallInfo(i);
                if (callInfo != null && callInfo.getCallState() == SipCallSession.InvState.CONFIRMED ) {
                    return callInfo;
                }
            }
        }
        return null;
    }

    public SipCallSession getHangingCallInProgress() {
        synchronized (callsList) {
            for (int i = 0; i < callsList.size(); i++) {
                SipCallSession callInfo = getCallInfo(i);
                if (callInfo != null && callInfo.isHanging()) {
                    return callInfo;
                }
            }
        }
        return null;
    }

    /**
     * Check if any of call infos indicate there is an active call in progress.
     *
     * @see com.csipsimple.api.SipCallSession#isActive()
     */
    public SipCallSession getActiveCallOngoing() {
        // Go through the whole list of calls and find the first active state.
        synchronized (callsList) {
            for (int i = 0; i < callsList.size(); i++) {
                SipCallSession callInfo = getCallInfo(i);
                if (callInfo != null && callInfo.isActive() && callInfo.isOngoing()) {
                    return callInfo;
                }
            }
        }
        return null;
    }

    public SipCallSession getRingingCall() {
        // Go through the whole list of calls and find the first ringing state.
        synchronized (callsList) {
            for (int i = 0; i < callsList.size(); i++) {
                SipCallSession callInfo = getCallInfo(i);
                if (callInfo != null && callInfo.isActive() && callInfo.isBeforeConfirmed() && callInfo.isIncoming()) {
                    return callInfo;
                }
            }
        }
        return null;

    }

    // 20160905-mengbo-start: 修改耳机接听按钮功能
    /**
     * Broadcast the Headset button press event internally if there is any call
     * in progress. TODO : register and unregister only while in call
     */
    public boolean handleHeadsetButton() {
        CallSession callSession = VOIPManager.getInstance().getCurSession();
        // 20160908-mengbo-start: 优化代码，修复空指针crash
        if(callSession != null){
            State callSessionState = callSession.getState();
            if(callSessionState != null){
                if(callSessionState == State.INCOMING){
                    VOIPManager.getInstance().answer();
                    return true;
                }

                if(callSessionState == State.CALLING || callSessionState == State.CONECTTING){
                    VOIPManager.getInstance().hangup();
                    return true;
                }
            }
        }
        // 20160908-mengbo-end

        final SipCallSession callInfo = getActiveCallInProgress();
        if (callInfo != null) {
            // Headset button has been pressed by user. If there is an
            // incoming call ringing the button will be used to answer the
            // call. If there is an ongoing call in progress the button will
            // be used to hangup the call or mute the microphone.
            int state = callInfo.getCallState();
//            if (callInfo.isIncoming() &&
//                    (state == SipCallSession.InvState.INCOMING ||
//                            state == SipCallSession.InvState.EARLY)) {
//                if (pjService != null && pjService.service != null) {
//                    pjService.service.getExecutor().execute(new SipRunnable() {
//                        @Override
//                        protected void doRun() throws SameThreadException {
//
//                            pjService.callAnswer(callInfo.getCallId(),
//                                    pjsip_status_code.PJSIP_SC_OK.swigValue());
//                        }
//                    });
//                }
//                return true;
//            } else
            if (state == SipCallSession.InvState.INCOMING ||
                    state == SipCallSession.InvState.EARLY ||
                    state == SipCallSession.InvState.CALLING ||
                    state == SipCallSession.InvState.CONFIRMED ||
                    state == SipCallSession.InvState.CONNECTING) {
                //
                // In the Android phone app using the media button during
                // a call mutes the microphone instead of terminating the call.
                // We check here if this should be the behavior here or if
                // the call should be cleared.
                //
                if (pjService != null && pjService.service != null) {
                    pjService.service.getExecutor().execute(new SipRunnable() {

                        @Override
                        protected void doRun() throws SameThreadException {
                            if (mPreferedHeadsetAction == SipConfigManager.HEADSET_ACTION_CLEAR_CALL) {

                                Log.d("voip_disconnect", "UAStateReceiver handleHeadsetButton " +
                                        "-> (pjsua.call_hangup)");

                                pjService.callHangup(callInfo.getCallId(), 0);
                            } else if (mPreferedHeadsetAction == SipConfigManager.HEADSET_ACTION_HOLD) {
                                pjService.callHold(callInfo.getCallId());
                            } else if (mPreferedHeadsetAction == SipConfigManager.HEADSET_ACTION_MUTE) {
                                pjService.mediaManager.toggleMute();
                            }
                        }
                    });
                }
                return true;
            }
        }
        return false;
    }
    // 20160905-mengbo-end

    /**
     * Update status of call recording info in call session info
     *
     * @param callId The call id to modify
     * @param canRecord if we can now record the call
     * @param isRecording if we are currently recording the call
     */
    public void updateRecordingStatus(int callId, boolean canRecord, boolean isRecording) {
        SipCallSessionImpl callInfo = getCallInfo(callId);
        callInfo.setCanRecord(canRecord);
        callInfo.setIsRecording(isRecording);
        synchronized (callsList) {
            // Re-add it just to be sure
            callsList.put(callId, callInfo);
        }
        onBroadcastCallState(callInfo);
    }

    private void sendPendingDtmf(final int callId) {
        pjService.service.getExecutor().execute(new SipRunnable() {
            @Override
            protected void doRun() throws SameThreadException {
                pjService.sendPendingDtmf(callId);
            }
        });
    }

    private void fillRDataHeader(String hdrName, SWIGTYPE_p_pjsip_rx_data rdata, Bundle out)
            throws SameThreadException {
        String valueHdr = PjSipService.pjStrToString(pjsua.get_rx_data_header(
                pjsua.pj_str_copy(hdrName), rdata));
        if (!TextUtils.isEmpty(valueHdr)) {
            out.putString(hdrName, valueHdr);
        }
    }

    public void updateCallMediaState(int callId) throws SameThreadException {
        SipCallSession callInfo = updateCallInfoFromStack(callId, null);
        msgHandler.sendMessage(msgHandler.obtainMessage(ON_MEDIA_STATE, callInfo));
    }

    /**
     * 从通话记录中解析出安通账号
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

    public class HeadPhoneReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                ToastUtil.showNoRepeatToast(context, ActomaController.getApp().getString(R.string.HEADSET_BROADCAST));
                if (intent.getIntExtra("state", 0) == 0) {
                    //如果此时扬声器开启，则关闭
                    ToastUtil.showNoRepeatToast(context, ActomaController.getApp().getString(R.string.HEADSET_BROADCAST_OUT));
                } else if (intent.getIntExtra("state", 0) == 1) {
                    ToastUtil.showNoRepeatToast(context,ActomaController.getApp().getString(R.string.HEADSET_BROADCAST_IN));
                    //如果此时扬声器开启，则关闭
                }
            }
        }
    }


    @Override
    public void on_record_device_state(int state) {
        if(state == 1000) {
            SipCallSession session = getActiveCallInProgress();
            if(session != null) {
                if(session.isNotifyRecDevice()) {
                    session.setNotifyRecDevice(false);
                    LogUtil.getUtils(TAG).e("xjq 音频设备被占用！");
                    Message m = msgHandler.obtainMessage();
                    m.what = SHOW_NOTIFY;
                    m.obj = RECORD_DEVICE_INUSE;
                    msgHandler.sendMessage(m);
                }
            }
        }
    }
}
