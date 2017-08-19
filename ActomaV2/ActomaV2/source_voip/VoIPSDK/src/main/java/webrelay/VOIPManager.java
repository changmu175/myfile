package webrelay;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipManager;
import com.csipsimple.db.DatabaseHelper;
import com.csipsimple.service.SipNotifications;
import com.csipsimple.service.SipService;
import com.csipsimple.utils.Compatibility;
import com.csipsimple.utils.PreferencesProviderWrapper;
import com.csipsimple.utils.Ringer;
import com.csipsimple.utils.ScreenUtil;
import com.securevoip.contacts.CustContacts;
import com.securevoip.pninter.PNMessageManager;
import com.securevoip.pninter.SipStateListener;
import com.securevoip.utils.CallLogHelper;
import com.securevoip.utils.ToastUtil;
import com.securevoip.voip.PhoneManager;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.HttpUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.report.bean.reportVoipAccept;
import com.xdja.report.reportClientMessage;
import com.xdja.safekeyjar.util.StringResult;
import com.xdja.voipsdk.InCallPresenter;
import com.xdja.voipsdk.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.pjsip.pjsua.pjsua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import util.DateUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.EncDecUtil;
import util.MultiVoiceAssetsUtil;
import util.PermissionHelper;
import util.SPUtil;
import webrelay.bean.CallSession;
import webrelay.bean.MsgType;
import webrelay.bean.Role;
import webrelay.bean.State;
import webrelay.bean.StatusCode;
import webrelay.bean.VOIPError;
import webrelay.inter.CallResultInter;
import webrelay.inter.PushListener;
import webrelay.inter.UpdateCurrentUI;
import webrelay.voice.PlayVoiceManager;

/**
 * Created by guoyaxin on 2015/12/31.
 * Modify  by mengbo 2016-08-30 修改挂断流程，解决网络差挂不断问题
 */
public class VOIPManager implements SipStateListener, PushListener, CallResultInter,PlayVoiceManager.Callback{

    private static VOIPManager voipManager;

    public final static int HANGUP_TYPE_NORMAL = 1;
    public final static int HANGUP_TYPE_HAIXINWANWEI = 2;

    public final int CAN_MAKE_CALL = 0;             // 可以发起新的呼叫
    public final int TARGET_IN_CALL = 1;            // 已经与被叫建立通话
    public final int LOCAL_IN_CALL = 2;             // 主叫已经在通话中
    public final int LOCAL_NAME_CONFLICT = 3;       // 主叫用户名错误
    public final int NETWORK_ERROR = 4;             // 网络不可用
    public final int PHONE_IN_CALL = 5;             // 普通电话正在通话中
    public final int ERROR_CONTEXT_NULL = 6;        // Context为null异常
    public final int NO_RECORD_PERMISSION = 7;      // 没有录音权限

    //来电时间（毫秒）
    private long voipCreateTime;

    //通话接通的时间（毫秒）
    private long voipTimeOff;

    //按下接听键的时间（毫秒）
    private long voipTimeStart;

    private static final String THIS_FILE = VOIPManager.class.getName();

    private Context context = null;
	
    private PowerManager.WakeLock wakeLockBright;

    //mengbo 挂断类型
    private int hangupType = HANGUP_TYPE_NORMAL;

    public static VOIPManager getInstance() {
        //add by gbc@xdja.com 20160819. begin
        if (null == voipManager) {
            synchronized (VOIPManager.class) {
                voipManager = new VOIPManager();
            }
        }
        //add by gbc@xdja.com 20160819. end
        return voipManager;
    }

    /**
     * 启动SipService
     * @param cxt
     * @param accName
     * @param ticket
     * @return
     */
    public VOIPManager startService(Context cxt,String  accName,String ticket){
        initAccout(cxt, accName, ticket);
//        cxt.startService(new Intent(cxt, SipService.class));
        return voipManager;
    }

    /**
     * 账号初始化
     * @param context
     * @param accName
     * @param ticket
     */
    public VOIPManager initAccout(Context context,String  accName,String ticket){
        PhoneManager.getInstance().initAccount(context, accName, ticket);
        //获取推送服务器地址
        VOIPPush.getInstance().getAs(context, accName);
        DatabaseHelper.initDatabaseHelper(context, accName);
        this.context = context;
//        context = ActomaController.getApp();
        init();
        return this;
    }

    public Context getContext() {
        return context;
    }

    private SipService sipService = null;
    private SipNotifications sipNotifications;
    private AudioManager audioManager = null;
    private TelephonyManager telephonyManager = null;

    public void setSipService(SipService service) {
        this.sipService = service;
    }

    private VOIPManager init() {

        if (ringer==null){
            ringer=new Ringer(context);
            ringer.registerRingModeChangedReceiver();
        }

        PlayVoiceManager.getInstance(context).addCallback(this);
        sipNotifications = new SipNotifications();

        VOIPPush.getInstance().addPushListener(this);
        PNMessageManager.getInstance().addSipStateListener(this);

        prefs = context.getSharedPreferences("audiostate", Context.MODE_PRIVATE);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return this;
    }
    //wxf@xdja.com 2016-08-10 add. fix bug 1447 . review by mengbo. Start
    public TelephonyManager getTelephonyManager(){
         return telephonyManager;
    }
    //wxf@xdja.com 2016-08-10 add. fix bug 1447 . review by mengbo. End

    /**
     * 解绑语音提示服务
     */
    private void unbindPlayVoiceService(){
        PlayVoiceManager.getInstance(context).unbindService();
    }

    /**
     * 要启动通话界面的Activity，可配
     */
    private Class mPlugin = InCallPresenter.class;

    /**
     * 配置通话界面的Activity
     * @param plugin 通话界面的Activity
     */
    public void registerInCallPlugin(Class plugin) {
        mPlugin = plugin;
    }

    //当前通话模式
    private int curCallMode = VOIPApi.CALL_MODE_PN;
    private int curSipCallId = SipCallSession.INVALID_CALL_ID;

    @Override
    public void onCallStateChanged(SipCallSession session) {
        LogUtil.getUtils(THIS_FILE).d("onCallStateChanged callState:" + session.getCallState());
        switch (session.getCallState()) {
            case SipCallSession.InvState.CALLING:
                if (curSession == null)
                    return;
                curSipCallId = session.getCallId();
                break;
            case SipCallSession.InvState.INCOMING:
                setSipMediaState();
                break;
            case SipCallSession.InvState.CONFIRMED:
                /** 20161011-mengbo-start: modify. 加入State.DISCONNECTING判断 **/
                if (curSession == null
                        || curSession.getState() == State.DISCONNECTING
                        || curSession.getState() == State.DISCONNECTED) {
                    try {

                        Log.d("voip_disconnect", "onCallStateChanged SipCallSession.InvState.CONFIRMED -> (pjsua.call_hangup)");

                        sipService.getBinder().hangup(session.getCallId(), 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                /** 20161011-mengbo-end **/


                //记录被叫建立通话时间点
                if(curSession.getRole() == Role.CALLEE){
                    //通话接通的时间（毫秒）
                    voipTimeOff = System.currentTimeMillis();
                    //发送VOIP通话时间
                    sendVoipTime();
                }

                quitTimer();
                PlayVoiceManager.getInstance(context).stopPlay();
                curSession.setStartTime(SystemClock.elapsedRealtime());
                setCallSessionState(State.CONFIRMED);
                curSipCallId = session.getCallId();

                //初始化语音流加密密钥 mengbo@xdja.com 2016-08-08
                initPjsuaSecretkey();
                break;
            case SipCallSession.InvState.DISCONNECTED:

                Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED start");

                if (curSession == null) {
                    LogUtil.getUtils(THIS_FILE).e("(curSession == null) to return");
                    return;
                }

                quitDisconnectCallbackTimer();
                quitToDisconnectedCallbackTimer();

                Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED header curSession.getState():"+curSession.getState());

                if(curSession.getState() == State.DISCONNECTING
                        || curSession.getState() == State.DISCONNECTED){
                    LogUtil.getUtils(THIS_FILE).e("current state disconnected so just stop sipservice!");

                    Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED sipcall主动挂断 disconnected , callback Time:"
                            + DateUtil.timeStamp2Time(System.currentTimeMillis()));

                    try{
                        if(sipServiceStarted && sipService != null){
                            Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED SipServiceStarted:" + sipServiceStarted);
                            if (sipService.stopSipStack()) {
                                Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED SipService stop !!! ");
                                sipService.stopSelf();
                                //setCallSessionState(webrelay.bean.State.DISCONNECTED);
                                Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED sipservice stop Success !!!!!");
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    setCallSessionState(webrelay.bean.State.DISCONNECTED);
                    Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED end-1");
                    return;
                }

                Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED sipcall被动挂断 disconnected , callback Time:"
                        + DateUtil.timeStamp2Time(System.currentTimeMillis()) +" to disconnect()");

                if(session.getLastStatusCode() == SipCallSession.StatusCode.FORBIDDEN) {
                    LogUtil.getUtils(THIS_FILE).e(" - Ticket 认证失败！！！");
                }

                // 通话断开，底层回调，重新走disconnect，断开服务。
                directDisconnect(null);

                Log.d("voip_disconnect", "onCallStateChanged DISCONNECTED end-2");
                break;
        }
    }

    @Override
    public void onRegStateChanged(boolean success, int code) {
        LogUtil.getUtils(THIS_FILE).d("onRegStateChanged=" + success);
        if (curSession==null)
            return;

        if(needRecallAfterSipOnline() && success) {
            curSession.setSipOnline(true);
            LogUtil.getUtils(THIS_FILE).d("xjq SIP上线成功，发起呼叫");
            pnCall();
        }
    }

    private MediaState curMediaState=null;

    @Override
    public void onMediaStateChanged(MediaState mediaState) {
        curMediaState=mediaState;
        updateMedia(mediaState);
    }

    @Override
    public void onVoiceBluetoothScoEnable() {
        //如果正在呼叫或正在连接，蓝牙可用时，则直接打开蓝牙耳机
        if (curSession==null)
            return;
        State state=curSession.getState();
        if (( state.equals(State.CALLING ) || state.equals(State.CONECTTING )))
            setBluetoothOn(true);
    }

    private boolean isNetConnected=true;
    @Override
    public void onNetworkStateChanged(boolean connected, String networkType) {
        isNetConnected=connected;
        if(!connected) {
            setCallSessionErrCode(StatusCode.NETWORK_DISCONNECTED);
        }else {
            if(needRecallAfterNetworkReconnected()) {
                pnCall();
            }
            setCallSessionErrCode(StatusCode.NETWORK_CONNECT);
        }
    }

    @Override
    public void onCallParamChanged(int code) {
        switch (code) {
            case StatusCode.CHANGE_LOW_BAND:
                setCallSessionErrCode(code);
                break;
        }
    }

    private boolean sipServiceStarted = false;
    @Override
    public void onServiceStateChanged(boolean on) {
        Log.d("voip_disconnect", "onServiceStateChanged SipService is Started? " + on);

        sipServiceStarted = on;
        if(on) {

            Log.d("voip_disconnect", " *** SipService started *** hasActiveSipCallSession:" + sipService.hasActiveSipCallSession());
            Log.d("voip_disconnect", " *** SipService started *** hasHangingCallInProgress:" + sipService.hasHangingCallInProgress());

            LogUtil.getUtils(THIS_FILE).d("xjq sip service started!");
            if(needOnlineAfterSipStarted()) { //主叫
                sipOnline();
            } else if(needSipcallAfterSipStarted()) {//被叫
                sipCall();
            }
        } else {
            LogUtil.getUtils(THIS_FILE).d("xjq sip service stopped!");
        }
    }
    /**
     * 设置sipCallState回调
     * @param callSipState
     * @return
     */
    private CallSession curSession;

    @Override
    public void onCallComing(final CallSession callSession) {
        Log.d("voip_disconnect", "onCallComing");


//        /**方式一:当前状态是通话状态时为小视频发送广播判暂停视频播放用**/
//        BroadcastReceiver audioStateReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {}};
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction("com.csipsimple.action.APPLY_NIGHTLY");
//            context.registerReceiver(audioStateReceiver,intentFilter);
//
//
//        /**当前状态是通话状态时为小视频发送广播判暂停视频播放用**/
        voipCreateTime = System.currentTimeMillis();
        voipTimeStart = 0; //reset this var
        voipTimeOff = 0; //reset this var
		
        //唤醒屏幕点亮锁
        wakeLockBrightAcquire();

        callSession.setRole(Role.CALLEE);
        final String user = callSession.getUser();
        final String src = callSession.getSrc();

        callSession.setUser(src);
        callSession.setSrc(user);
        String ticket = getTicket();

        if(telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
            LogUtil.getUtils(THIS_FILE).e("onCallComing but phone calling");
            String caller = CustContacts.getFriendName(src);
            ToastUtil.showToast(context, caller + ActomaController.getApp().getString(R.string.PLEASE_CALLBACK));
            callSession.setCode(String.valueOf(StatusCode.CALLEE_BUSY));// 被叫自己正在通话中
            new VOIPApi(context, this).hangup(callSession, MsgType.MSG_CALLED_SUBSCRIBER_REJECT.mType, ticket);
            // 生成未接来电提醒 xjq
            addCallLog(callSession);
            showMissedCallNotify(callSession.getUser());
            //释放屏幕点亮锁
            wakeLockBrightRelease();
            return;
        }

        /** 20160909-mengbo-start: 当用户正在挂断的时候，可能正在播放提示音，也处于通话状态，拒接来电**/
        if (hasActiveCall()
                || (curSession != null && curSession.getState() == State.DISCONNECTING)) {
        //if (hasActiveCall()) {
        /** 20160909-mengbo-end **/

            LogUtil.getUtils(THIS_FILE).d("onCallComing but hasActiveCall");
            if (!curSession.getCallid().equals(callSession.getCallid())){
                // 当前已经有活动会话，需要挂断这个来电 xjq
                callSession.setCode(String.valueOf(StatusCode.CALLEE_BUSY));// 被叫自己正在通话中
                new VOIPApi(context, this).hangup(callSession, MsgType.MSG_CALLED_SUBSCRIBER_REJECT.mType,ticket);
                // 生成未接来电提醒 xjq
                addCallLog(callSession);
                showMissedCallNotify(callSession.getUser());

                /** 20170227-mengbo-start: 修改正在挂断中,来电提示语 **/
                if(curSession.getState() == State.DISCONNECTING){
                    /** 20170316-mengbo-start: 当语音提示时，来电，显示正在通话的文字提示 **/
                    if(isMediaPlaying()){
                        ToastUtil.showToast(context,ActomaController.getApp().getString(R.string.IN_CALL_REFUSE_CALL));
                    }else{
                        ToastUtil.showToast(context,ActomaController.getApp().getString(R.string.DISCONNECTING_REFUSE_CALL));
                    }
                    /** 20170316-mengbo-end **/
                }else{
                    //wxf@xdja.com 2016-08-08 add. fix bug 1649 . review by mengbo. Start
                    ToastUtil.showToast(context,ActomaController.getApp().getString(R.string.IN_CALL_REFUSE_CALL));
                    //wxf@xdja.com 2016-08-08 add. fix bug 1649 . review by mengbo. End
                }
                /** 20170227-mengbo-end **/
            }
            //释放屏幕点亮锁
            wakeLockBrightRelease();
            return;
        }

        //提取通话密钥  mengbo@xdja.com 2016-08-08
        boolean isExtracted = EncDecUtil.extractCKMSSecretKeyString(callSession.getSecretkey(), callSession.getSrc(), callSession.getUser());
        if(!isExtracted){
            LogUtil.getUtils(THIS_FILE).e("onCallComing but extractCKMSSecretKey failed");
            /**2016-11-14-wangzhen-start：修改字符创为资源引用*/
            ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.DECRYPT_FAILED));
            /**2016-11-14-wangzhen-end*/
            //释放屏幕点亮锁
            wakeLockBrightRelease();
            return;
        }

        /** 20161014-mengbo-start: 从Thread()中提取出,确保来电界面更新及时 **/
        curSession = callSession;
        curSession.setSrc(user);
        curSession.setUser(src);

        //[S]modify by lixiaolong on 20160906. fix bug 1245. review by myself.
        startPlugin();

        Log.d("voip_disconnect", "onCallComing-new CallSession(user, src)-callid:" + curSession.getCallid());

        // 20160907-mengbo-start: 防止频繁呼叫、挂断界面不正常更新。
        setCallSessionState(webrelay.bean.State.INCOMING);
        //curSession.setState(webrelay.bean.State.INCOMING);
        // 20160907-mengbo-end
        /** 20161014-mengbo-end **/

        new Thread(){
            @Override
            public void run() {
                if(!sipServiceStarted){
                    context.startService(new Intent(context, SipService.class));
                }

                //有来电时，如果蓝牙耳机可用，则直接使用蓝牙耳机播放
                boolean isBluetoothEnable=isBluetoothEnable();
                if (isBluetoothEnable){
                    setBluetoothOn(true);
                }

                if (isHeadsetEnable()){
                    setSpeakerphoneOn(false);
                }

                // 存储来电中的voip服务器地址
                SPUtil.saveVoIPIncomingAddr(context, curSession.getAs());
                // 有新的来电，重置呼叫模式为pn xjq
                curCallMode = VOIPApi.CALL_MODE_PN;
                // 获取音频焦点 xjq
                requestAudioFocus();
                // 保存音频模式
                saveAudioState();
                //获取来电时是否锁屏并存储,之后据此显示不同接听方式
                ScreenUtil.putScreenState(context);
                // 开始响铃 xjq
                startRing(user);
                execTimerTask( new ConnectTask());
            }
        }.start();
        //[E]modify by lixiaolong on 20160906. fix bug 1245. review by myself.
    }

    @Override
    public void callerReject(CallSession callSession) {
        Log.d("voip_disconnect", "onCallerReject start");

        if (!hasActiveCall())
            return;

        // 设置状态码 xjq 2015-01-26
        if (!isCallIdEquals(callSession)) {
            return;
        }

        curSession.setCode(callSession.getCode());

        Log.d("voip_disconnect", "onCallerReject directDisconnect Time:"
                + DateUtil.timeStamp2Time(System.currentTimeMillis()));

        //通话未建立，主叫直接挂断
        OnDisconnectCallback onDisconnectCallback = new OnDisconnectCallback() {
            @Override
            public void onFinish() {
                //未接来电显示通知
                showMissedCallNotify(curSession.getUser());
            }
        };
        directDisconnect(onDisconnectCallback);
        Log.d("voip_disconnect", "onCallerReject end");
    }

    //被叫挂断，网络不好的情况下，主叫会存在两种状态，1、主叫响铃状态；2、主叫通话状态。
    @Override
    public void calleeReject(CallSession callSession) {
        Log.d("voip_disconnect", "onCalleeReject start");

        if (!hasActiveCall()) {
            LogUtil.getUtils(THIS_FILE).e("onCalleeReject !hasActiveCall() return");
            return;
        }

        // 设置状态码 xjq 2015-01-26
        if (!isCallIdEquals(callSession)) {
            LogUtil.getUtils(THIS_FILE).e("onCalleeReject !isCallIdEquals(callSession) return");
            return;
        }

        // 设置状态码 xjq
        curSession.setCode(callSession.getCode());

        if(sipServiceStarted && sipService != null && sipService.hasActiveSipCallSession()){
            Log.d("voip_disconnect", "onCalleeReject CALL_MODE_SIP  " + "callSession.getLastErrCode():" + callSession.getLastErrCode()
                    + " callSession.getCode():" + callSession.getCode());

            /** 20170102-mengbo-start: 存在会话时，收到被叫挂断推送，也会收到sip挂断，不需要调用挂断 **/
            //收到对方拒接推送，但已建立通话，挂断时，避免语音提示界面显示对方未接听
            //curSession.setCode(String.valueOf(StatusCode.SUCCESS));
            //Log.d("voip_disconnect", "onCalleeReject sipCallDisconnect");
            //Log.d("voip_disconnect", "sipCallDisconnect sipService is Started to hangupSip Time:"
            //        + DateUtil.timeStamp2Time(System.currentTimeMillis()));
            //hangupSip();
            //sipCallDisconnect();
            /** 20170102-mengbo-end **/
        }else{
            Log.d("voip_disconnect", "onCalleeReject playSound to disconnect");

            /** 20161205-mengbo-start: 获取不同语言语音提示文件 **/
            playSound(MultiVoiceAssetsUtil.getMultiVoiceFileString(context, MultiVoiceAssetsUtil.FILE_BUSY_HERE), 2);
            //playSound(PlayVoiceManager.FILE_BUSY_HERE, 2);
            /** 20161205-mengbo-end **/

            setCallSessionState(State.DISCONNECTING);
        }
		
		Log.d("voip_disconnect", "onCalleeReject end");
    }


    /**
     * 当前是否有活动的session xjq
     * @return
     */
    public final boolean hasActiveCall() {
        if(curSession != null && curSession.getState() != State.DISCONNECTING && curSession.getState() != State.DISCONNECTED)
            return true;
        else
            return false;
    }

    /**
     * 对于被叫sip service启动后是否需要发起sip呼叫
     * @return
     */
    private boolean needSipcallAfterSipStarted() {
        return curSession != null &&
                curSession.getRole() == Role.CALLEE &&
                curSession.getState() == State.CONECTTING &&
                !curSession.isSipOnline();
    }
    /**
     * 对于主叫sip service启动后需要判断是否需要上线
     * @return
     */
    private boolean needOnlineAfterSipStarted() {
        return curSession != null &&
                curSession.getRole() == Role.CALLER &&
                curSession.getState() == State.CALLING &&
                !curSession.isSipOnline();
    }

    /**
     * sip账号上线成功之后，是否需要发起pn呼叫
     * @return
     */
    private boolean needRecallAfterSipOnline() {
        return curSession != null &&
                curSession.getRole() == Role.CALLER &&
                curSession.getState() == State.CALLING &&
                !curSession.isSipOnline() &&
                !(curSession.getCode().equalsIgnoreCase(String.valueOf(StatusCode.CALLER_TIMEOUT)));
    }

    /**
     * 网络切换恢复后，是否需要发起pn呼叫
     * @return
     */
    private boolean needRecallAfterNetworkReconnected() {
        return curSession != null &&
                curSession.getRole() == Role.CALLER &&
                curSession.getState() == State.CALLING &&
                curSession.isSipOnline() &&
                !(curSession.getCode().equalsIgnoreCase(String.valueOf(StatusCode.CALLER_TIMEOUT)));
    }

    /**
     * 判断推送过来的Callid 与 curSession的Callid
     * @return
     */
    @SuppressLint("BooleanMethodIsAlwaysInverted")
    private boolean isCallIdEquals(CallSession callSession){
        if (callSession==null || curSession==null) {
            return false;
        }

        if (callSession.getCallid().equals(curSession.getCallid())){
            return true;
        }

        return false;
    }

    /**
     * 是否允许发出新的呼叫
     *
     * @param user
     * @param src
     * @return
     */
    private int canMakeCall(String user, String src) {
        if(context == null) {
            return ERROR_CONTEXT_NULL;
        }
        PreferencesProviderWrapper prefsWrapper = new PreferencesProviderWrapper(context);
        boolean valid = prefsWrapper.isValidConnectionForIncoming();

        if(!valid) {
            return NETWORK_ERROR;
        }

        if ((!CustContacts.isFriend(user)) && (!CustContacts.isDepartment(user))){
            return NOT_FRIENDS;
        }

        if(telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
            return PHONE_IN_CALL;
        }

        if (curSession == null || curSession.getState() == State.DISCONNECTED) {
            /** 20170318-mengbo-start 可以呼叫前，判断是否开启录音权限 **/
            if(Compatibility.shouldRecordAudioPermissionOn()){
                if(!PermissionHelper.hasAudioRecordPermission(context)){
                    return NO_RECORD_PERMISSION;
                }
            }
            /** 20170318-mengbo-end **/
            return CAN_MAKE_CALL;
        }

        if (!src.equalsIgnoreCase(curSession.getSrc())) {
            return LOCAL_NAME_CONFLICT; // 不应该走到这里
        }
        return user.equalsIgnoreCase(curSession.getUser()) ? TARGET_IN_CALL : LOCAL_IN_CALL;
    }

    /**
     * 呼叫
     *
     * @param user 接受方
     * @param src  自己
     */
    public void makeCall(final String user, final String src) {
        Log.d("voip_disconnect", "makeCall");

        int canMakeCall = canMakeCall(user, src);
        //无法进行呼叫
        if (canMakeCall != CAN_MAKE_CALL) {
            switch (canMakeCall) {
                case LOCAL_NAME_CONFLICT://推送账户与呼叫账户不一致
                    ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.LOCAL_NAME_CONFLICT));
                    return;
                case TARGET_IN_CALL://已经与被叫建立通话，直接启动通话界面
                    startPlugin();
                    break;
                case LOCAL_IN_CALL://已有通话进行
                    ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.LOCAL_IN_CALL));
                    break;
                case NETWORK_ERROR:
//                    ToastUtil.showToast(sipService, "网络连接不可用，请检查网络设置");
                    HttpUtils.showError(context, ActomaController.getApp().getString(R.string.NETWORK_ERROR));
                    break;
                case PHONE_IN_CALL:
                    ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.PHONE_IN_CALL));
                    break;
                case NOT_FRIENDS:
                    ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.NOT_FRIENDS));
                    break;
                case ERROR_CONTEXT_NULL:
                    //跑Monkey出现的问题，后续处理。
                    break;
                case NO_RECORD_PERMISSION://没有录音权限
                    ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.GET_PERMISSION));
                    break;
            }

            return;
        }

        /** 20161011-mengbo-start: 添加生成通话秘钥失败的异常处理 **/
        //生成通话密钥
        boolean isGenerated = EncDecUtil.generateCKMSKey(src, user);
        if (!isGenerated){
            LogUtil.getUtils(THIS_FILE).e("makeCall but generateCKMSKey failed");
            /** 20161012-mengbo-start: 修改提示语  2016-11-14 -wangzhen-start：修改为资源引用**/
            ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.CALL_FAILED));
            /** 20161012-mengbo-end 2016-11-14-wangzhen-end**/
            return;
        }
        /** 20161011-mengbo-end **/

        //唤醒屏幕点亮锁
        wakeLockBrightAcquire();

        // 开始发起新的呼叫
        curSession = new CallSession(user, src);

        //[S]modify by lixiaolong on 20160906. fix bug 1245. review by myself.
        startPlugin();

        Log.d("voip_disconnect", "makeCall-new CallSession(user, src)-callid:" + curSession.getCallid());

        //更新界面，没有单独更新蓝牙图标，放线程里，执行慢了的话，本来蓝牙状态未激活，会变为已激活
        setCallSessionState(webrelay.bean.State.CALLING);

        new Thread(){
            @Override
            public void run() {
                // 保存音频模式
                saveAudioState();
                curCallMode = VOIPApi.CALL_MODE_PN; // 重置呼叫模式为PN xjq
                requestAudioFocus();// 获取音频焦点 xjq
                execTimerTask(new ConnectTask());
                PlayVoiceManager.getInstance(context).playVoice(context, PlayVoiceManager.FILE_RING, -1, true);
                //拨打电话时，如果蓝牙耳机可用，则直接使用蓝牙耳机播放
                boolean isBluetoothEnable=isBluetoothEnable();
                if (isBluetoothEnable){
                    setBluetoothOn(true);
                }

                if (isHeadsetEnable()){
                    setSpeakerphoneOn(false);
                }

                // 发起呼叫不需要要求当时推送必须在线！ xjq
                initCall();
            }
        }.start();
        //[E]modify by lixiaolong on 20160906. fix bug 1245. review by myself.
    }

    /**
     * 判断蓝牙耳机是否可用
     * @return
     */
    @SuppressLint("SimplifiableIfStatement")
    public boolean isBluetoothEnable(){
         MediaState mediaState=PlayVoiceManager.getInstance(context).getMediaState();
        if (mediaState==null)
            return false;
        return mediaState.canBluetoothSco && !mediaState.isBluetoothScoOn;
    }

    /**
     * 判断耳机是否可用
     * @return
     */
    @SuppressLint("SimplifiableIfStatement")
    public boolean isHeadsetEnable(){
        MediaState mediaState=PlayVoiceManager.getInstance(context).getMediaState();
        if (mediaState==null)
            return false;
        return mediaState.isHeadset;
    }

    private SharedPreferences prefs;
    private void saveAudioState() {
        if(prefs.getBoolean("savedAudioState", false))
            return;
        SharedPreferences.Editor ed = prefs.edit();
        int mode = audioManager.getMode();

        if(mode == AudioManager.MODE_IN_CALL || mode == AudioManager.MODE_IN_COMMUNICATION) {
            mode = AudioManager.MODE_NORMAL;
        }
        ed.putInt("savedMode", mode);
        ed.putBoolean("savedAudioState", true);
        ed.apply();

        LogUtil.getUtils(THIS_FILE).d("save AudioState mode:" + mode);
    }

    public void restoreAudioState() {
//        if(!prefs.getBoolean("savedAudioState", false))
//            return;

        int mode = prefs.getInt("savedMode", AudioManager.MODE_NORMAL);
        audioManager.setMode(mode);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("savedAudioState", false);
        ed.apply();

        LogUtil.getUtils(THIS_FILE).d("restore AudioState mode:" + mode);
    }

    /**
     * 呼叫初始化
     */
    public void initCall(){
        LogUtil.getUtils(THIS_FILE).d("initCall");

        if (curSession==null)
            return;
        if (curSession.getState().equals(State.CALLING)){
            String voipIP= SPUtil.getVoIPLocalAddr(context);
            LogUtil.getUtils(THIS_FILE).d("getVoIPLocalAddr:" + voipIP);

            if (voipIP==null){
                String ticket=getTicket();
                new VOIPApi(context, this).initCall(curSession,ticket);
            }else {
                curSession.setAs(voipIP);

                if(sipServiceStarted) {
                    sipOnline();
                } else {
                    // 需要先启动sip service，启动成功后账号上线
                    context.startService(new Intent(context, SipService.class));
                    curSession.setSipOnline(false);
                }
            }
        }
    }

    /**
     * 接听
     *
     * @throws RemoteException
     */
    public void answer() {
        //按下接听键的时间（毫秒）
        voipTimeStart = System.currentTimeMillis();
        LogUtil.getUtils(THIS_FILE).d("answer");

        Log.d("voip_disconnect", "answer");

        if (!isNetConnected){
            Log.d("voip_disconnect", "answer !isNetConnected");
            directDisconnect(null);
            return;
        }

        // 停止响铃，开始反向呼叫 xjq
        stopRing();
        setCallSessionState(State.CONECTTING);

        if(!sipServiceStarted) {
            Log.d("voip_disconnect", "answer !sipServiceStarted");
            return;
        }

        sipCall();
    }

    public void setSipMediaState() {
        // 反向呼叫前，重置状态 xjq
        MediaState mediaState = PlayVoiceManager.getInstance(context).getMediaState();
        try {
            sipService.getBinder().setMediaState(mediaState);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sipCall() {
        LogUtil.getUtils(THIS_FILE).d("sipCall");
        if (curSession==null)
            return;
        try {
            setSipMediaState();

            Bundle b = new Bundle();
            Bundle extraHeaders = new Bundle();
            extraHeaders.putString(SipManager.HDR_EXTRA_CALL_ID, curSession.getCallid()); // 被叫使用extra-callid来标识
            b.putBundle(SipCallSession.OPT_CALL_EXTRA_HEADERS, extraHeaders);
            sipService.getBinder().addCustAccount(SPUtil.getVoIPIncomingAddr(context));
            sipService.getBinder().makeCallWithOptions(curSession.getUser(), 1, b);
            sipService.getBinder().custAccountOnline();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 推送拨打电话
     */
    private void pnCall(){
        LogUtil.getUtils(THIS_FILE).d("pnCall");
        if (curSession==null)
            return;
        String ticket=getTicket();
        new VOIPApi(context, VOIPManager.this).pnCall(curSession, ticket);
    }

    /**
     * 挂断电话
     */
    public void hangup() {
        hangupType = HANGUP_TYPE_NORMAL;

        Log.d("voip_disconnect", "hangup start");

        /** 20160909-mengbo-start: 解决ANR,正在断开连接的时候,可能正在响铃**/
        if(!hasActiveCall() && !PlayVoiceManager.getInstance(context).isPlaying()){
            return;
        }

        if(sipServiceStarted && sipService != null && sipService.hasActiveSipCallSession()){
            Log.d("voip_disconnect", "hangup sipCallDisconnect");
            Log.d("voip_disconnect", "sipCallDisconnect sipService is Started to hangupSip Time:"
                    + DateUtil.timeStamp2Time(System.currentTimeMillis()));

            /** 20170103-mengbo-start: 放入sipCallDisconnect()方法中最后执行 **/
            //hangupSip();
            /** 20170103-mengbo-end **/
            sipCallDisconnect();
        }else{
            Log.d("voip_disconnect", "hangup directDisconnect");

            hangupPn();
            PlayVoiceManager.getInstance(context).stopPlay();
            directDisconnect(null);
        }
		
		Log.d("voip_disconnect", "hangup end");
        /** 20160909-mengbo-end **/
    }

    /**
     * 推送挂断
     */
    private void hangupPn() {
        if (curSession == null)
            return;
        String ticket=getTicket();
        if(curSession.getRole() == Role.CALLER) {
            LogUtil.getUtils(THIS_FILE).d("caller hangupPn VRCL");
            curSession.setCode(String.valueOf(StatusCode.CALLER_CANCEL)); //主叫取消呼叫 xjq 2016-01-26
            new VOIPApi(context, this).hangup(curSession, MsgType.MSG_CALLER_REJECT.mType, ticket);
        } else if(curSession.getRole() == Role.CALLEE) {
            LogUtil.getUtils(THIS_FILE).d("callee hangupPn VRCK");
            stopRing();
            curSession.setCode(String.valueOf(StatusCode.CALLEE_REJECT)); // 被叫拒绝接听 xjq 2016-01-26
            new VOIPApi(context, this).hangup(curSession, MsgType.MSG_CALLED_SUBSCRIBER_REJECT.mType, ticket);
        }
    }

    /**
     * Sip挂断
     */
    private void hangupSip() {
        if (curSipCallId == SipCallSession.INVALID_CALL_ID) {
            LogUtil.getUtils(THIS_FILE).d("hangupSip invalid call id return");
            return;
        }

        if (this.sipService == null) {
            LogUtil.getUtils(THIS_FILE).d("hangupSip sipService null return");
            return;
        }

        try {
            LogUtil.getUtils(THIS_FILE).d("do hangupSip curSipCallId:" + curSipCallId);
            sipService.getBinder().hangup(curSipCallId, 200);
            sipService.getBinder().setCallHangingState(curSipCallId, true);
            sipService.getBinder().custAccountOffline();
            sipService.getBinder().delCustAccount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterInit(CallSession callSession) {
        sipOnline();
    }

    /**
     * sip service账号上线
     */
    private void sipOnline(){
        if (sipService == null) {
            LogUtil.getUtils(THIS_FILE).d("sipOnline null return");
            return;
        }
        try {
            LogUtil.getUtils(THIS_FILE).d("do sipOnline");
            sipService.getBinder().addCustAccount(SPUtil.getVoIPLocalAddr(context));
            sipService.getBinder().custAccountOnline();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playFinish() {
        playSoundFinishDisconnect();
    }

    private class ConnectTask extends TimerTask {
        @Override
        public void run() {
            connectTimer=null;
            if(curSession == null) {
                return;
            }
            if (curSession.getRole().equals(Role.CALLER)) {
                // 主叫 对方无人接听
                State state=curSession.getState();
                if (state.equals(State.CALLING)){ //连接了一个无效wifi
                    /** 20161205-mengbo-start: 获取不同语言语音提示文件 **/
                    playSound(MultiVoiceAssetsUtil.getMultiVoiceFileString(context, MultiVoiceAssetsUtil.FILE_CONNECT_ERR), 2);
                    //playSound(PlayVoiceManager.FILE_CONNECT_ERR, 2);
                    /** 20161205-mengbo-end **/
                } else if (state.equals(State.CONECTTING)){//网络正常时的呼叫超时
                    /** 20161205-mengbo-start: 获取不同语言语音提示文件 **/
                    playSound(MultiVoiceAssetsUtil.getMultiVoiceFileString(context, MultiVoiceAssetsUtil.FILE_NO_ANSWER), 2);
                    //playSound(PlayVoiceManager.FILE_NO_ANSWER, 2);
                    /** 20161205-mengbo-end **/
                }
                curSession.setCode(String.valueOf(StatusCode.CALLER_TIMEOUT));// 主叫呼叫超时 xjq 2016-01-26
                setCallSessionState(State.DISCONNECTING);
            } else {
                //被叫 生成未接来电
                curSession.setCode(String.valueOf(StatusCode.CALLEE_TIMEOUT));

                OnDisconnectCallback onDisconnectCallback = new OnDisconnectCallback() {
                    @Override
                    public void onFinish() {
                        Message msg=Message.obtain();
                        msg.obj=curSession;
                        msg.what=SHOW_MISSED_CALL_NOTIFY;
                        myHandler.sendMessage(msg);
                    }
                };
                directDisconnect(onDisconnectCallback);
            }
         }
    }

    @Override
    public void callSeccess(CallSession callSession) {
        /** 20161011-mengbo-start: 防止正在断开或已断开状态时，推送回调，导致界面更新问题 **/
        if (curSession != null && (curSession.getState() == State.DISCONNECTING
                || curSession.getState() == State.DISCONNECTED)) {
            return;
        }
        /** 20161011-mengbo-end **/
        setCallSessionState(State.CONECTTING);
    }

    @Override
    public void callFinished(CallSession callSession, int reason) {
        Message msg = Message.obtain();
        switch (reason) {
            case StatusCode.CALLEE_OFFLINE:
                setCallSessionErrCode(StatusCode.CALLEE_OFFLINE);

                if (curSession != null) {
                    /** 20161205-mengbo-start: 获取不同语言语音提示文件 **/
                    PlayVoiceManager.getInstance(context).playVoice(context, MultiVoiceAssetsUtil.getMultiVoiceFileString(context, MultiVoiceAssetsUtil.FILE_CONNECT_ERR), 2, false);
                    //PlayVoiceManager.getInstance(context).playVoice(context, PlayVoiceManager.FILE_CONNECT_ERR, 2, false);
                    /** 20161205-mengbo-end **/
                    curSession.setCode(String.valueOf(StatusCode.CALLEE_OFFLINE));// 被叫不在线 xjq 2016-01-26
                }
                break;
            case StatusCode.NOT_FRIENDS:
                msg.what = NOT_FRIENDS;
                myHandler.sendMessage(msg);
                break;
            //wxf@xdja.com 2016-08-11 add. fix bug 2121、2123 . review by mengbo. Start
            case StatusCode.MX_SEVER_ERROR:
                msg.what = MX_SEVER_ERROR;
                myHandler.sendMessage(msg);
                break;
            //wxf@xdja.com 2016-08-11 add. fix bug 2121、2123. review by mengbo. End
            case StatusCode.TICKET_INVID:
                hangup();
                break;
            //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. Start
            case StatusCode.VOIP_SEVER_ERROR:
                setCallSessionErrCode(StatusCode.VOIP_SEVER_ERROR);
                if (curSession != null) {
                    /** 20161205-mengbo-start: 获取不同语言语音提示文件 **/
                    PlayVoiceManager.getInstance(context).playVoice(context, MultiVoiceAssetsUtil.getMultiVoiceFileString(context, MultiVoiceAssetsUtil.FILE_CONNECT_ERR), 2, false);
                    //PlayVoiceManager.getInstance(context).playVoice(context, PlayVoiceManager.FILE_CONNECT_ERR, 2, false);
                    /** 20161205-mengbo-end **/
                    curSession.setCode(String.valueOf(StatusCode.VOIP_SEVER_ERROR));// 服务器异常 wxf 2016-08-11
                }
                break;
            //wxf@xdja.com 2016-08-11 add. fix bug 2122 . review by mengbo. End
        }
    }

    @Override
    public void callException(VOIPError voipError) {
        VOIPError.ErrorType errorType = voipError.getErrorType();
        switch (errorType) {
            case ERROR_INITINAL:
//                if(curSession != null) {
//                    setCallSessionErrCode(StatusCode.CALLER_ERROR_INITIAL);
//                    curSession.setCode(String.valueOf(StatusCode.CALLER_ERROR_INITIAL));
//                }
                break;
            case ERROR_CALLING:
//                if(curSession != null) {
//                    setCallSessionErrCode(StatusCode.CALLER_ERROR_CALLING);
//                    curSession.setCode(String.valueOf(StatusCode.CALLER_ERROR_CALLING));
//                }
                break;
            case ERROR_REJECT:
//                if(curSession != null) {
//                    setCallSessionErrCode(StatusCode.ERROR_REJECT);
//                }
                break;
        }
    }

    /**
     * 设置curSession状态
     *
     * @param state
     */
    public void setCallSessionState(State state) {
        if (curSession == null)
            return;
        curSession.setState(state);
        updateCallState(curSession);
    }

    /**
     * 直接断开通话
     */
    public synchronized void directDisconnect(final OnDisconnectCallback onDisconnectCallback){
        Log.d("voip_disconnect", "directDisconnect start");

        if (curSession == null) {
            return;
        }else if (curSession != null && curSession.getState() == State.DISCONNECTED) {
            return;
        }

        new AsyncTask<Object, Object, Object>() {

            protected void onPreExecute() {
                setCallSessionState(webrelay.bean.State.DISCONNECTING);
            }

            protected Object doInBackground(Object... params) {
                clearNotificationForInCall();
                addCallLog(curSession);

                restoreAudioState();
                abandonAudioFocus(); // 释放音频焦点 xjq

                if (curSession.getRole().equals(Role.CALLER)){
                    //避免被叫调用后无法收到来电提醒
                    PlayVoiceManager.getInstance(context).stopPlay();
                }
                quitTimer();
                stopRing();
                PlayVoiceManager.getInstance(context).reset();

                return null;
            }

            protected void onPostExecute(Object result) {

                if(onDisconnectCallback != null){
                    onDisconnectCallback.onFinish();
                }

                try{
                    Log.d("voip_disconnect", "directDisconnect sipServiceStarted:" + sipServiceStarted);
                    if(sipServiceStarted){
                        if (sipService.stopSipStack()) {
                            Log.d("voip_disconnect", "directDisconnect stopSipStack finish and to sipService.stopSelf ");
                            sipService.stopSelf();
                            setCallSessionState(webrelay.bean.State.DISCONNECTED);
                        }else{
                            Log.d("voip_disconnect", "directDisconnect stopSipStack false : 等待onCallStateChanged回调");
                            execDisconnectCallbackTimerTask(new DisconnectCallbackTimerTask());

                            /** 20170102-mengbo-start: 停止服务的过程中，进程中如果存在通话，直接挂断 **/
                            if(MsgType.MSG_CALLED_SUBSCRIBER_REJECT.mType.equals(curSession.getType())
                                    || MsgType.MSG_CALLER_REJECT.mType.equals(curSession.getType())){
                                //收到主叫或被叫的挂断推送，也会收到sip挂断，无需进行挂断
                                Log.d("voip_disconnect", "directDisconnect curSession Type : MSG_CALLED_SUBSCRIBER_REJECT || MSG_CALLER_REJECT");
                            }else{
                                if (sipService.getActiveCallInProgress() != null) {
                                    Log.d("voip_disconnect", "directDisconnect callHangup(getActiveCallInProgress().getCallId(), 0)");
                                    sipService.getBinder().hangup(sipService.getActiveCallInProgress().getCallId(), 0);
                                    sipService.getBinder().setCallHangingState(sipService.getActiveCallInProgress().getCallId(), true);
                                }
                            }
                            /** 20170102-mengbo-end **/
                        }
                    }else{
                        Log.d("voip_disconnect", "directDisconnect 服务没启动，直接setCallSessionState(webrelay.bean.State.DISCONNECTED)");
                        setCallSessionState(webrelay.bean.State.DISCONNECTED);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Log.d("voip_disconnect", "directDisconnect 抛出Exception，直接setCallSessionState(webrelay.bean.State.DISCONNECTED)");
                    setCallSessionState(webrelay.bean.State.DISCONNECTED);
                }

                Log.d("voip_disconnect", "directDisconnect end");

                //释放屏幕点亮锁
                wakeLockBrightRelease();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /** 20161011-mengbo-start: 呼叫语音提示，会在正在断开状态中的判断更新界面，包含根据curSession Code判断
     * ，真正的正在断开时，需重新设置code，避免与语音提示界面显示重复 **/
    /**
     * 播放完提示音时使用的挂断
     */
    public synchronized void playSoundFinishDisconnect(){
        Log.d("voip_disconnect", "playSoundFinishDisconnect start");

        if (curSession == null) {
            return;
        }else if (curSession != null && curSession.getState() == State.DISCONNECTED) {
            return;
        }

        new AsyncTask<Object, Object, Object>() {

            protected void onPreExecute() {
                setCallSessionState(webrelay.bean.State.DISCONNECTING);
            }

            protected Object doInBackground(Object... params) {
                clearNotificationForInCall();
                addCallLog(curSession);

                //在生成通话记录后，需重新设置code，避免与语音提示界面显示重复
                curSession.setCode(String.valueOf(StatusCode.SUCCESS));

                restoreAudioState();
                abandonAudioFocus(); // 释放音频焦点 xjq

                if (curSession.getRole().equals(Role.CALLER)){
                    //避免被叫调用后无法收到来电提醒
                    PlayVoiceManager.getInstance(context).stopPlay();
                }
                quitTimer();
                stopRing();
                PlayVoiceManager.getInstance(context).reset();

                return null;
            }

            protected void onPostExecute(Object result) {
                try{
                    Log.d("voip_disconnect", "playSoundFinishDisconnect sipServiceStarted:" + sipServiceStarted);
                    if(sipServiceStarted){
                        if (sipService.stopSipStack()) {
                            Log.d("voip_disconnect", "playSoundFinishDisconnect stopSipStack finish and to sipService.stopSelf ");
                            sipService.stopSelf();
                            setCallSessionState(webrelay.bean.State.DISCONNECTED);
                        }else{
                            Log.d("voip_disconnect", "playSoundFinishDisconnect stopSipStack false : 等待onCallStateChanged回调");
                            execDisconnectCallbackTimerTask(new DisconnectCallbackTimerTask());

                            /** 20170102-mengbo-start: 停止服务的过程中，进程中如果存在通话，直接挂断 **/
                            if (sipService.getActiveCallInProgress() != null) {
                                Log.d("voip_disconnect", "playSoundFinishDisconnect hangup(sipService.getActiveCallInProgress().getCallId(), 0)");
                                sipService.getBinder().hangup(sipService.getActiveCallInProgress().getCallId(), 0);
                                sipService.getBinder().setCallHangingState(sipService.getActiveCallInProgress().getCallId(), true);

                            }
                            /** 20170102-mengbo-end **/
                        }
                    }else{
                        Log.d("voip_disconnect", "playSoundFinishDisconnect 服务没启动，直接setCallSessionState(webrelay.bean.State.DISCONNECTED)");
                        setCallSessionState(webrelay.bean.State.DISCONNECTED);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Log.d("voip_disconnect", "playSoundFinishDisconnect 抛出Exception，直接setCallSessionState(webrelay.bean.State.DISCONNECTED)");
                    setCallSessionState(webrelay.bean.State.DISCONNECTED);
                }

                Log.d("voip_disconnect", "playSoundFinishDisconnect end");

                //释放屏幕点亮锁
                wakeLockBrightRelease();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    /** 20161011-mengbo-end **/


    /**
     * 快速断开，先界面、音频、通话记录及时响应，SIPSERVICE不关闭
     */
    public synchronized void sipCallDisconnect(){
        Log.d("voip_disconnect", "sipCallDisconnect start");
        if (curSession==null) {
            return;
        }else if (curSession != null && curSession.getState() == State.DISCONNECTED) {
            return;
        }

        new AsyncTask<Object, Object, Object>() {

            protected void onPreExecute() {
                setCallSessionState(webrelay.bean.State.DISCONNECTING);
            }

            protected Object doInBackground(Object... params) {
                clearNotificationForInCall();
                addCallLog(curSession);

                restoreAudioState();
                abandonAudioFocus(); // 释放音频焦点 xjq

                if (curSession.getRole().equals(Role.CALLER)){
                    //避免被叫调用后无法收到来电提醒
                    PlayVoiceManager.getInstance(context).stopPlay();
                }
                quitTimer();
                stopRing();
                PlayVoiceManager.getInstance(context).reset();

                return null;
            }

            protected void onPostExecute(Object result) {
                Log.d("voip_disconnect", "sipCallDisconnect sipServiceStarted:"+sipServiceStarted);
                if(sipServiceStarted){
                    Log.d("voip_disconnect", "sipCallDisconnect execDisconnectCallbackTimerTask");
                    execDisconnectCallbackTimerTask(new DisconnectCallbackTimerTask());
                }else{
                    Log.d("voip_disconnect", "sipCallDisconnect sipService is not Started 直接setCallSessionState(webrelay.bean.State.DISCONNECTED)");
                    setCallSessionState(webrelay.bean.State.DISCONNECTED);
                }
                Log.d("voip_disconnect", "sipCallDisconnect end");

                /** 20170103-mengbo-start: 尽量避免异步，防止crash **/
                hangupSip();
                /** 20170103-mengbo-end **/

                //释放屏幕点亮锁
                wakeLockBrightRelease();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 生成通话记录
     */
    private void  addCallLog(CallSession callSession){
        if (callSession==null)
            return;
        ContentValues cv = CallLogHelper.logValuesForCall(callSession, callSession.getStartTime());
        CallLogHelper.addCallLog(cv);
    }

    /**
     * 显示未接来电通知
     */
    private void showMissedCallNotify(String user){
        if (user==null)
            return;
        sipNotifications.showNotificationForMissedCall(user);
    }

    /**
     * 更新一些异常状态：比如网络中断，呼叫超时
     *
     * @param code
     */
    public void setCallSessionErrCode(int code) {
        if (curSession == null)
            return;
        curSession.setLastErrCode(code);
        updateStatusText(curSession);
    }

    private ArrayList<UpdateCurrentUI> updateCurrentUIs = new ArrayList<>();

    public void addUpdateUIListener(UpdateCurrentUI updateCurrentUI) {
        if (updateCurrentUIs.contains(updateCurrentUI))
            return;
        updateCurrentUIs.add(updateCurrentUI);
    }

    public void remove(UpdateCurrentUI updateCurrentUI) {
        int size = updateCurrentUIs.size();
        if (size == 0)
            return;
        if (updateCurrentUIs.contains(updateCurrentUI))
            updateCurrentUIs.remove(updateCurrentUI);
    }

    /**
     * 通话界面重新启动时，更新外放、静音、蓝牙耳机等控件显示状态
     */
    public void updateMediaState(){
        MediaState mediaState = PlayVoiceManager.getInstance(context).getMediaState();
        if (curMediaState!=null){
            mediaState=curMediaState;
        }
        updateMedia(mediaState);
    }

    private void updateMedia(MediaState mediaState) {
        int size = updateCurrentUIs.size();
        if (size == 0)
            return;
        for (UpdateCurrentUI updateCurrentUI : updateCurrentUIs)
            updateCurrentUI.updateMediaState(mediaState);
    }

    private void updateCallState(CallSession callSession) {
        int size = updateCurrentUIs.size();
        if (size == 0)
            return;
        for (UpdateCurrentUI updateCurrentUI : updateCurrentUIs)
            updateCurrentUI.updateCallState(callSession);
    }

    private void updateStatusText(CallSession callSession) {
        int size = updateCurrentUIs.size();
        if(size == 0)
            return;
        for(UpdateCurrentUI updateCurrentUI : updateCurrentUIs)
            updateCurrentUI.updateStatusText(callSession);
    }

    /**
     * 返回当前CallSession
     *
     * @return
     */
    public CallSession getCurSession() {
        return curSession;
    }

    // 启动注册的通话界面
    public void startPlugin() {
        Intent intent = new Intent(context, mPlugin);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 显示通知
     */
    public void showNotify() {
        if (sipNotifications == null || curSession == null)
            return;
        /** 20170316-mengbo-start: 播放“暂时正在通话”语音提示时，返回需在通知栏显示 **/
        //State state = curSession.getState();
        //if (state.equals(State.DISCONNECTED) || state.equals(State.DISCONNECTING))
        //    return;
        //sipNotifications.showNotificationForCall2(mPlugin, curSession.getUser());

        if(hasActiveCall() || isMediaPlaying()){
            sipNotifications.showNotificationForCall2(mPlugin, curSession.getUser());
        }
        /** 20170316-mengbo-end **/

    }

    /**
     * 清除正在通话的通知
     */
    public void clearNotificationForInCall() {
        if (sipNotifications == null)
            return;
        sipNotifications.cancelNotificationForCall();
    }

    /**
     * 打开扩音器
     *
     * @param isOpen
     */
    public void setSpeakerphoneOn(boolean isOpen) {
        if (curSession==null)
            return;

        if (!curSession.getState().equals(State.CONFIRMED)){
            /**20160618-mengbo-start:如果想要扬声器改变的状态和当前状态一致,不设置扬声器**/
            MediaState mediaState = PlayVoiceManager.getInstance(context).getMediaState();
            if(mediaState.isSpeakerphoneOn == isOpen){
                return;
            }
            /**20160618-mengbo-end**/
            PlayVoiceManager.getInstance(context).setSpeakerphoneOn(isOpen);
        }else {
            if(sipServiceStarted) {
                try {
                    MediaState mediaState = sipService.getBinder().getCurrentMediaState();
                    if(mediaState.isSpeakerphoneOn == isOpen){
                        return;
                    }
                    sipService.getBinder().setSpeakerphoneOn(isOpen);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 蓝牙
     *
     * @param isOpen
     */
    public void setBluetoothOn(boolean isOpen) {
        if (curSession==null)
            return;
        if (!curSession.getState().equals(State.CONFIRMED)){
            PlayVoiceManager.getInstance(context).setBluetoothOn(isOpen);
        }else {
            if(sipServiceStarted) {
                try {
                    sipService.getBinder().setBluetoothOn(isOpen);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 静音
     *
     * @param isOpen
     */
    public void setMicrophoneMute(boolean isOpen) {
        if (curSession==null)
            return;
        if (!curSession.getState().equals(State.CONFIRMED)){
            PlayVoiceManager.getInstance(context).setMicrophoneMute(isOpen);
        }else {
            if(sipServiceStarted) {
                try {
                    sipService.getBinder().setMicrophoneMute(isOpen);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 线控耳机是否插入
     * @param isOpen
     */
    public void setHeadsetOn(boolean isOpen) {
        if (curSession==null)
            return;
        if (!curSession.getState().equals(State.CONFIRMED)){
            PlayVoiceManager.getInstance(context).setHeadsetOn(isOpen);
        }else {
            if(sipServiceStarted) {
                try {
                    sipService.getBinder().setHeadsetOn(isOpen);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getCurCallMode() {
        return curCallMode;
    }

    public void setCurCallMode(int curCallMode) {
        this.curCallMode = curCallMode;
    }

    private Ringer ringer;
    /**
     * Start ringing announce for a given contact.
     * It will also focus audio for us.
     * @param remoteContact the contact to ring for. May resolve the contact ringtone if any.
     */
    synchronized public void startRing(String remoteContact) {

        if(!ringer.isRinging()) {
            ringer.ring(remoteContact, Settings.System.DEFAULT_RINGTONE_URI.toString());
        }else {
            LogUtil.getUtils().d(THIS_FILE + "Already ringing ....");
        }
    }

    /**
     * Stop all ringing. <br/>
     * Warning, this will not unfocus audio.
     */
    synchronized public void stopRing() {
        if(ringer.isRinging()) {
            ringer.stopRing();
        }
    }

    /**
     * 重新呼叫
     */
    public void recall() {
        if (curSession == null)
            return;
        makeCall(curSession.getUser(), curSession.getSrc());
    }

    public void sendIM() {
        if (curSession==null)
            return;
            Intent intent = new Intent("com.xdja.imp.presenter.activity.ChatDetailActivity");
            intent.putExtra("talkerId", curSession.getUser());
            intent.putExtra("talkType", 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
    }

    private Timer connectTimer = new Timer();
    private static final int CONNECT_TIMEOUT = 50;// 无人接听最长等待时间为50s xjq

    private Timer disconnectCallbackTimer = new Timer();
    private static final int DISCONNECT_CALLBACK_TIMEOUT = 6;// 挂断回调超时时间为：6s

    private Timer toDisconnectedTimer = new Timer();
    private static final int TO_DISCONNECTED_TIMEOUT = 15;// 等待15s跳转到通话结束界面：15s


    /**
     * 计时操作
     */
    private void  execTimerTask(TimerTask timerTask){
        quitTimer();
        connectTimer = new Timer();
        connectTimer.schedule(timerTask, CONNECT_TIMEOUT * 1000);
    }

    /**
     * 取消定时操作
     */
    private void quitTimer(){
        if (connectTimer != null) {
            connectTimer.purge();
            connectTimer.cancel();
            connectTimer=null;
        }
    }

    /**
     * 启动挂断回调超时计时器
     */
    private void  execDisconnectCallbackTimerTask(TimerTask timerTask){
        quitDisconnectCallbackTimer();
        Log.d("voip_disconnect", "Exec DisconnectCallbackTimerTask Timer:" + DateUtil.timeStamp2Time(System.currentTimeMillis()));
        disconnectCallbackTimer = new Timer();
        disconnectCallbackTimer.schedule(timerTask, DISCONNECT_CALLBACK_TIMEOUT * 1000);
    }

    /**
     * 停止挂断回调超时计时器
     */
    private void quitDisconnectCallbackTimer(){
        Log.d("voip_disconnect", "Quit DisconnectCallbackTimer Timer:" + DateUtil.timeStamp2Time(System.currentTimeMillis()));
        if (disconnectCallbackTimer != null) {
            disconnectCallbackTimer.purge();
            disconnectCallbackTimer.cancel();
            disconnectCallbackTimer=null;
        }
    }

    private class DisconnectCallbackTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.d("voip_disconnect", "DisconnectCallbackTimerTask run start");
            if(curSession == null) {
                return;
            }

            State state = curSession.getState();
            Log.d("voip_disconnect", "DisconnectCallbackTimerTask run state:"+state);

            if(state != null && state == State.DISCONNECTING){
                Log.d("voip_disconnect", "DisconnectCallbackTimerTask run WAIT_DISCONNECTING_CALLBACK");
                Message msg=Message.obtain();
                msg.what=TestDisconnectCallback;
                myHandler.sendMessage(msg);
            }
            Log.d("voip_disconnect", "DisconnectCallbackTimerTask run end");
        }
    }

    /**
     * 启动挂断回调超时计时器
     */
    private void  execToDisconnectedTimerTask(TimerTask timerTask){
        quitToDisconnectedCallbackTimer();
        Log.d("voip_disconnect", "Exec ToDisconnectedTimerTask Timer:" + DateUtil.timeStamp2Time(System.currentTimeMillis()));
        toDisconnectedTimer = new Timer();
        toDisconnectedTimer.schedule(timerTask, TO_DISCONNECTED_TIMEOUT * 1000);
    }

    /**
     * 停止挂断回调超时计时器
     */
    private void quitToDisconnectedCallbackTimer(){
        Log.d("voip_disconnect", "Quit ToDisconnectedTimer Timer:" + DateUtil.timeStamp2Time(System.currentTimeMillis()));
        if (toDisconnectedTimer != null) {
            toDisconnectedTimer.purge();
            toDisconnectedTimer.cancel();
            toDisconnectedTimer=null;
        }
    }

    private class ToDisconnectedTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.d("voip_disconnect", "ToDisconnectedTimerTask run start");
            if(curSession == null) {
                return;
            }

            State state = curSession.getState();
            Log.d("voip_disconnect", "ToDisconnectedTimerTask run state:"+state);

            if(state != null && state == State.DISCONNECTING) {
                Log.d("voip_disconnect", "ToDisconnectedTimerTask run setCallSessionState(State.DISCONNECTED)");
                Message msg=Message.obtain();
                msg.what=TestToDisconnected;
                myHandler.sendMessage(msg);
            }
            Log.d("voip_disconnect", "ToDisconnectedTimerTask run end");
        }
    }

    private void playSound(String fileName,int time){
        quitTimer();
        PlayVoiceManager.getInstance(context).playVoice(context,fileName,time,false);
    }

    static class MyHandler extends WeakReferenceHandler<VOIPManager>{

        public MyHandler(VOIPManager reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(VOIPManager reference, Message msg) {
             reference.handleMsg(msg);
        }
    }

    private MyHandler myHandler=new MyHandler(this);

    private  static final int SHOW_MISSED_CALL_NOTIFY=100;
    private  static final int NOT_FRIENDS=SHOW_MISSED_CALL_NOTIFY+1;

    //先显示正在挂断，1秒后显示通话结束
    private static final int SHOW_DISCONNECTED=NOT_FRIENDS+1;

    private static final int MX_SEVER_ERROR = SHOW_DISCONNECTED + 1;


    private static final int TestDisconnectCallback = MX_SEVER_ERROR + 1;
    private static final int TestToDisconnected = TestDisconnectCallback + 1;

    //显示未接来电通知
    private void handleMsg(Message msg){
        switch (msg.what){
            case SHOW_MISSED_CALL_NOTIFY:
                CallSession callSession= (CallSession) msg.obj;
                if (callSession==null)
                    return;
                showMissedCallNotify(callSession.getUser());
                break;
            case NOT_FRIENDS:
                ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.NOT_FRIENDS_CANNOT_CALL));
                directDisconnect(null);
                break;
            case SHOW_DISCONNECTED:
                PlayVoiceManager.getInstance(context).reset();
                setCallSessionState(State.DISCONNECTED);
                break;
            case MX_SEVER_ERROR:
                ToastUtil.showToast(context, ActomaController.getApp().getString(R.string.MX_SEVER_ERROR));
                directDisconnect(null);
                break;

            /** 20170118-mengbo-start: 加入超时，UI线程操作 **/
            case TestDisconnectCallback:
                Log.d("voip_disconnect", "handleMsg -- TestDisconnectCallback");

                //boolean hasActive = sipService.hasActiveSipCallSession();
                //boolean hasHanging = sipService.hasHangingCallInProgress();
                //Toast.makeText(context, "DisconnectCallbackTimerTask--hasActive :"+hasActive + "-hasHanging :"+hasHanging, Toast.LENGTH_LONG).show();

                /** 20161230-mengbo-start: 超时6s后，显示等待10s提示语。 **/
                setCallSessionErrCode(StatusCode.WAIT_DISCONNECTING_CALLBACK);
                /** 20161230-mengbo-end **/

                execToDisconnectedTimerTask(new ToDisconnectedTimerTask());
                break;
            case TestToDisconnected:
                Log.d("voip_disconnect", "handleMsg -- TestToDisconnected");

                //hasActive = sipService.hasActiveSipCallSession();
                //hasHanging = sipService.hasHangingCallInProgress();
                //Toast.makeText(context, "ToDisconnectedTimerTask--hasActive :"+hasActive + "-hasHanging :"+hasHanging, Toast.LENGTH_LONG).show();

                setCallSessionState(State.DISCONNECTED);
                break;
            /** 20170118-mengbo-end **/
        }
    }

    public boolean isMediaPlaying(){
        return PlayVoiceManager.getInstance(context).isPlaying();
    }

    /**
     * 获取音频焦点
     *
     * @return .
     */
    private boolean requestAudioFocus() {

        muteAudioFocus(true);

        int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            /*if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
            } else */
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // mAm.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                // Stop playback
                audioManager.abandonAudioFocus(afChangeListener);
            }
        }
    };

    /**
     * 释放音频焦点
     */
    private void abandonAudioFocus() {

        //程序退出，释放音频焦点
        audioManager.abandonAudioFocus(afChangeListener);

        muteAudioFocus(false);
    }

    /**
     * @param bMute true时关闭音乐
     * @return
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public boolean muteAudioFocus(boolean bMute) {

        boolean bool = false;
        if(bMute){
            int result = audioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }else{
            int result = audioManager.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        return bool;
    }

    public String getTicket(){
        return PhoneManager.ticket;
    }

    /**
     * 初始化语音流加密密钥
     */
    private void initPjsuaSecretkey(){
        byte[] bytes = EncDecUtil.getSecretkey();
        if(bytes == null || bytes.length != 16){
            LogUtil.getUtils(THIS_FILE).e("VOIPManager--initPjsuaSecretkey--EncDecUtil.SECRET_KEY_EMPTY");
            bytes = EncDecUtil.SECRET_KEY_EMPTY;
        }
        short[] shorts = new short[bytes.length];
        for(int i=0;i<bytes.length;i++) {

            shorts[i] = (short)(bytes[i]&0xff);
        }

        //ckms开关
        if(CkmsGpEnDecryptManager.getCkmsIsOpen()){
            pjsua.AES128_init_secretkey(shorts,1);
        }else{
            pjsua.AES128_init_secretkey(shorts,0);
        }
    }

    /** 20161014-mengbo-start: 每次来电、去电屏幕点亮 **/
    // 唤醒屏幕点亮锁
    private void wakeLockBrightAcquire(){
        wakeLockBrightRelease();

        PowerManager powerMgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLockBright = powerMgr.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                getClass().getCanonicalName());
        wakeLockBright.setReferenceCounted(false);
        wakeLockBright.acquire();
    }

    // 释放屏幕点亮锁
    private void wakeLockBrightRelease(){
        if (wakeLockBright != null && wakeLockBright.isHeld()) {
            wakeLockBright.release();
            wakeLockBright = null;
        }
    }

    //判断是否是正常挂断
    public boolean isHanupTypeNormal() {
        return (hangupType == HANGUP_TYPE_NORMAL);
    }

    /** 20161014-mengbo-end **/

    //=======================海信万维需求======================
    /**
     * 海信万维侧键挂断电话
     */
    public void HXhangup() {
        hangupType = HANGUP_TYPE_HAIXINWANWEI;

        Log.d("voip_disconnect", "HXhangup start");

        if(!hasActiveCall() && !PlayVoiceManager.getInstance(context).isPlaying()){
            return;
        }

        if(sipServiceStarted && sipService != null && sipService.hasActiveSipCallSession()){
            Log.d("voip_disconnect", "HXhangup HXsipCallDisconnect sipService is Started to hangupSip Time:"
                    + DateUtil.timeStamp2Time(System.currentTimeMillis()));

            hangupSip();
            HXsipCallDisconnect();
        }else{
            Log.d("voip_disconnect", "HXhangup HXdirectDisconnect");

            hangupPn();
            PlayVoiceManager.getInstance(context).stopPlay();
            HXdirectDisconnect();
        }
		
		Log.d("voip_disconnect", "HXhangup end");
    }

    /** 20161019-mengbo-start: 将停止服务代码从doInBackground移入onPostExecute，提升通话结束界面响应速度 **/
    /**
     * 断开通话
     */
    public void HXdirectDisconnect(){
        LogUtil.getUtils(THIS_FILE).d("****** HXdirectDisconnect ******");

        if (curSession == null) {
            return;
        }

        if (curSession != null && curSession.getState() == State.DISCONNECTED) {
            return;
        }

        clearNotificationForInCall();
        addCallLog(curSession);

        new AsyncTask<Object, Object, Object>() {

            protected void onPreExecute() {
            }

            protected Object doInBackground(Object... params) {
                curSession.setState(webrelay.bean.State.DISCONNECTING);
//                setCallSessionState(webrelay.bean.State.DISCONNECTING);

                restoreAudioState();
                abandonAudioFocus(); // 释放音频焦点 xjq

                if (curSession.getRole().equals(Role.CALLER)){
                    //避免被叫调用后无法收到来电提醒
                    PlayVoiceManager.getInstance(context).stopPlay();
                }
                quitTimer();
                stopRing();
                PlayVoiceManager.getInstance(context).reset();

                setCallSessionState(webrelay.bean.State.DISCONNECTED);
                return null;
            }

            protected void onPostExecute(Object result) {
                //释放屏幕点亮锁
                wakeLockBrightRelease();

                // 停止voip 服务 xjq
                Intent intent = new Intent(SipManager.ACTION_STOP_SIPSERVICE);
                context.sendBroadcast(intent);
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    /** 20161019-mengbo-end **/
    public void HXsipCallDisconnect(){
        LogUtil.getUtils(THIS_FILE).d("****** HXsipCallDisconnect ******");

        if (curSession==null) {
            return;
        }

        if (curSession != null && curSession.getState() == State.DISCONNECTED) {
            return;
        }

        clearNotificationForInCall();
        addCallLog(curSession);

        new AsyncTask<Object, Object, Object>() {

            protected void onPreExecute() {
            }

            protected Object doInBackground(Object... params) {
                curSession.setState(webrelay.bean.State.DISCONNECTING);

                restoreAudioState();
                abandonAudioFocus(); // 释放音频焦点 xjq

                if (curSession.getRole().equals(Role.CALLER)){
                    //避免被叫调用后无法收到来电提醒
                    PlayVoiceManager.getInstance(context).stopPlay();
                }
                quitTimer();
                stopRing();
                PlayVoiceManager.getInstance(context).reset();

                setCallSessionState(webrelay.bean.State.DISCONNECTED);
                return null;
            }

            protected void onPostExecute(Object result) {
                //释放屏幕点亮锁
                wakeLockBrightRelease();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    //=======================海信万维需求======================
	
	interface OnDisconnectCallback{
        void onFinish();
    }
	
    /**
     * 数据上报,上报voip建立通话时长
     *
     */
    public void sendVoipTime(){
        //设置发送的数据
        StringResult tfCardIdResult = TFCardManager.getCardId();
        String cardId = "";
        if (tfCardIdResult != null && tfCardIdResult.getErrorCode() == 0) {
            cardId = tfCardIdResult.getResult();//cardId
        }
        reportVoipAccept rVoipAccept = new reportVoipAccept();
        rVoipAccept.setAccount(PhoneManager.name);
        rVoipAccept.setDeviceid(cardId);
        rVoipAccept.setCaller(curSession.getUser());
        rVoipAccept.setCallee(curSession.getSrc());
        rVoipAccept.setCreatetime(voipCreateTime);
        rVoipAccept.setPresstime(voipTimeStart);
        rVoipAccept.setAnswertime(voipTimeOff);

        reportClientMessage.reportClientMessage_voipAcceptPeriod(getTicket(),
                PhoneManager.name,
                rVoipAccept,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.getUtils().e(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String body = response.body().string();
                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(body);
                                if (null != jsonObject &&jsonObject.has("error")) {
                                    LogUtil.getUtils().e("report voip accept time has error!");
                                    return;
                                }
                                LogUtil.getUtils().e("report voip accept succes!");
                            } catch (JSONException e) {
                                //e.printStackTrace();
                                LogUtil.getUtils().e("report voip accept JSON parse have error!", e);
                            }
                        }
                    }
                });
    }
}


