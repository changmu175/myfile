
package com.csipsimple.service;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.pjsip.UAStateReceiver;
import com.csipsimple.service.SipService.SameThreadException;
import com.csipsimple.service.SipService.SipRunnable;
import com.csipsimple.utils.Compatibility;
import com.csipsimple.utils.Ringer;
import com.csipsimple.utils.accessibility.AccessibilityWrapper;
import com.csipsimple.utils.audio.AudioFocusWrapper;
import com.csipsimple.utils.bluetooth.BluetoothUtils8;
import com.csipsimple.utils.bluetooth.BluetoothWrapper;
import com.csipsimple.utils.bluetooth.BluetoothWrapper.BluetoothChangeListener;
import com.csipsimple.utils.headset.HeadsetWrapper;
import com.securevoip.pninter.PNMessageManager;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import webrelay.VOIPManager;
import webrelay.voice.PlayVoiceManager;
import webrelay.voice.PlayVoiceService;
import webrelay.voice.VoiceMediaManager;

public class MediaManager implements BluetoothChangeListener, HeadsetWrapper.HeadsetConnectChangeListener {
	
	final private static String TAG = "MediaManager";

	public static final String SPEAKER = "speaker";
	public static final String MUTE = "mute";
	public static final String MEDIA_DEVICE = "media_device";

	private SipService service;
	// 原来是private改为public xjq 2015-09-10
	public AudioManager audioManager;

	// 震动管理 xjq 2015-11-04
	private Vibrator vibrator;

	private Ringer ringer;
	

	//Locks
	private WifiLock wifiLock;
	private WakeLock screenLock;
	
	// Media settings to save / resore
	private boolean isSetAudioMode = false;
	
	//By default we assume user want bluetooth.
	//If bluetooth is not available connection will never be done and then
	//UI will not show bluetooth is activated
	private boolean userWantBluetooth = false;
	private boolean userWantSpeaker = false;
	private boolean userWantMicrophoneMute = false;
	private boolean userWantHeadset = false;
	private boolean headsetFlag = false;

	private boolean restartAudioWhenRoutingChange = true;
	private Intent mediaStateChangedIntent;
	
	//Bluetooth related
	private BluetoothWrapper bluetoothWrapper;
	private HeadsetWrapper headsetWrapper;

	private AudioFocusWrapper audioFocusWrapper;
	private AccessibilityWrapper accessibilityManager;
	

	private SharedPreferences prefs;
	private boolean useSgsWrkAround = false;
	private boolean useWebRTCImpl = false;
	private boolean doFocusAudio = true;


    private boolean startBeforeInit;
	private static int modeSipInCall = AudioManager.MODE_NORMAL;

	public MediaManager(SipService aService) {
		service = aService;
		audioManager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);

		// vibrate service. xjq 2015-11-04
		vibrator = (Vibrator)service.getSystemService(Context.VIBRATOR_SERVICE);

		prefs = service.getSharedPreferences("audio", Context.MODE_PRIVATE);
		//prefs = PreferenceManager.getDefaultSharedPreferences(service);
		accessibilityManager = AccessibilityWrapper.getInstance();
		accessibilityManager.init(service);
		
//		ringer = new Ringer(service);
//		mediaStateChangedIntent = new Intent(SipManager.ACTION_SIP_MEDIA_CHANGED); // 修改此处初始化操作到后面函数中。 xjq 2015-11-16
		
		//Try to reset if there were a crash in a call could restore previous settings
		restoreAudioState();
	}

	public void startService() {
		LogUtil.getUtils(TAG).d("MediaManager startService");

		modeSipInCall = service.getPrefs().getInCallMode();
		useSgsWrkAround = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.USE_SGS_CALL_HACK);
		useWebRTCImpl = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.USE_WEBRTC_HACK);
		doFocusAudio = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.DO_FOCUS_AUDIO);
		userWantBluetooth = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.AUTO_CONNECT_BLUETOOTH);
		userWantSpeaker = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.AUTO_CONNECT_SPEAKER);
		restartAudioWhenRoutingChange = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.RESTART_AUDIO_ON_ROUTING_CHANGES);
		startBeforeInit = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.SETUP_AUDIO_BEFORE_INIT);

		if(bluetoothWrapper == null) {
			bluetoothWrapper = BluetoothWrapper.getInstance(service);
			//[S]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
			bluetoothWrapper.setBluetoothChangeListener(this);
			//[E]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
			bluetoothWrapper.register();

			LogUtil.getUtils(TAG).d("MediaManager bluetoothWrapper register");
		}

		if(audioFocusWrapper == null) {
			audioFocusWrapper = AudioFocusWrapper.getInstance();
			audioFocusWrapper.init(service, audioManager);

			// 20160910-mengbo-start: 注册耳机按键监听
			audioFocusWrapper.focus(userWantBluetooth);
			// 20160910-mengbo-end
		}

		// 20160905-mengbo-start: 注册耳机是否连接状态广播
		if(headsetWrapper == null){
			headsetWrapper = HeadsetWrapper.getInstance(service);
			headsetWrapper.setHeadsetConnectChangeListener(this);
			headsetWrapper.register();
		}
		// 20160905-mengbo-end

		if (ringer== null){
			ringer=new Ringer(service);
			ringer.registerRingModeChangedReceiver();
		}
	}
	
	public void stopService() {
		LogUtil.getUtils(TAG).d("MediaManager stopService");

		/** 20160908-mengbo-start: 打开注销蓝牙耳机监听广播 **/
//		if(bluetoothWrapper != null) {
//			bluetoothWrapper.unregister();
//			bluetoothWrapper.setBluetoothChangeListener(null);
//			bluetoothWrapper = null;
//
//			Log.e("mb", "#-#-MediaManager bluetoothWrapper unregister");
//		}
		/** 20160908-mengbo-end **/

		if(headsetWrapper != null){
			headsetWrapper.unregister();
			headsetWrapper.setHeadsetConnectChangeListener(null);
			headsetWrapper = null;
		}

		// 20160905-mengbo-start: 注销耳机按键监听
		if(audioFocusWrapper != null){
			audioFocusWrapper.unFocus();
			audioFocusWrapper = null;
		}
		// 20160905-mengbo-end

		if (ringer!=null){
			ringer.unRegisterRingModeChangedReceiver();
		}
	}
	
	private int getAudioTargetMode() {
		int targetMode = modeSipInCall;

		if(service.getPrefs().useModeApi()) {
			LogUtil.getUtils(TAG).d("User want speaker now..." + userWantSpeaker);
			if(!service.getPrefs().generateForSetCall()) {
				return userWantSpeaker ? AudioManager.MODE_NORMAL : AudioManager.MODE_IN_CALL;
			}else {
				return userWantSpeaker ? AudioManager.MODE_IN_CALL: AudioManager.MODE_NORMAL ;
			}
		}
		if(userWantBluetooth && bluetoothWrapper.canBluetooth()) { // 必须加上蓝牙耳机是否可用的判断 xjq 2015-11-23
		    targetMode = AudioManager.MODE_NORMAL;
		}

		LogUtil.getUtils(TAG).d("Target mode... : " + targetMode);
		return targetMode;
	}
	
	public int validateAudioClockRate(int clockRate) {
	    if(bluetoothWrapper != null && clockRate != 8000) {
            if(userWantBluetooth && bluetoothWrapper.canBluetooth()) {
                return -1;
            }
        }
	    return 0;
	}
	
	public void setAudioInCall(boolean beforeInit) {
	    if(!beforeInit || (beforeInit && startBeforeInit) ) {
	        actualSetAudioInCall();
	    }
	}
	
	public void unsetAudioInCall() {
	    actualUnsetAudioInCall();
	}
	
	
	/**
	 * Set the audio mode as in call
	 */
	@SuppressWarnings("deprecation")
    private synchronized void actualSetAudioInCall() {
		//Ensure not already set
		if(isSetAudioMode) {
			return;
		}
		stopRing();
		saveAudioState();
		
		// Set the rest of the phone in a better state to not interferate with current call
		// Do that only if we were not already in silent mode
		/*
		 * Not needed anymore with on flight gsm call capture
		if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
    		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
    		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
    		audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
		*/
		
		//LOCKS
		
		//Wifi management if necessary
		ContentResolver ctntResolver = service.getContentResolver();
		Settings.System.putInt(ctntResolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);
		
		
		//Acquire wifi lock
		WifiManager wman = (WifiManager) service.getSystemService(Context.WIFI_SERVICE);
		if(wifiLock == null) {
			wifiLock = wman.createWifiLock( 
							(Compatibility.isCompatible(9)) ? WifiManager.WIFI_MODE_FULL_HIGH_PERF : WifiManager.WIFI_MODE_FULL, 
							"com.csipsimple.InCallLock");
			wifiLock.setReferenceCounted(false);
		}
		WifiInfo winfo = wman.getConnectionInfo();
		if(winfo != null) {
			DetailedState dstate = WifiInfo.getDetailedStateOf(winfo.getSupplicantState());
			//We assume that if obtaining ip addr, we are almost connected so can keep wifi lock
			if(dstate == DetailedState.OBTAINING_IPADDR || dstate == DetailedState.CONNECTED) {
				if(!wifiLock.isHeld()) {
					wifiLock.acquire();
				}
			}
			
			//This wake lock purpose is to prevent PSP wifi mode 
			if(service.getPrefs().getPreferenceBooleanValue(SipConfigManager.KEEP_AWAKE_IN_CALL)) {
				if(screenLock == null) {
					PowerManager pm = (PowerManager) service.getSystemService(Context.POWER_SERVICE);
		            screenLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "com.csipsimple.onIncomingCall.SCREEN");
		            screenLock.setReferenceCounted(false);
				}
				//Ensure single lock
				if(!screenLock.isHeld()) {
					screenLock.acquire();
					
				}
				
			}
		}

		if(!useWebRTCImpl) {
			//Audio routing
			int targetMode = getAudioTargetMode();
			LogUtil.getUtils(TAG).d("Set mode audio in call to " + targetMode);
			
			if(service.getPrefs().generateForSetCall()) {
			    boolean needOutOfSilent = (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT);
			    if(needOutOfSilent) {
			        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			    }
				ToneGenerator toneGenerator = new ToneGenerator( AudioManager.STREAM_VOICE_CALL, 1);
				toneGenerator.startTone(41 /*ToneGenerator.TONE_CDMA_CONFIRM*/);
				toneGenerator.stopTone();
				toneGenerator.release();
				// Restore silent mode
				if(needOutOfSilent) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
			}
			
			//Set mode
			if(targetMode != AudioManager.MODE_IN_CALL && useSgsWrkAround) {
				//For galaxy S we need to set in call mode before to reset stack
				audioManager.setMode(AudioManager.MODE_IN_CALL);
			}
			
			
			audioManager.setMode(targetMode);
			
			//Routing
			if(service.getPrefs().useRoutingApi()) {
				audioManager.setRouting(targetMode, userWantSpeaker?AudioManager.ROUTE_SPEAKER:AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
			}else {
				//audioManager.setSpeakerphoneOn(userWantSpeaker ? true : false);
				/**2017-2-28 -wangzhen modify.Change the if way Simplifly**/
				audioManager.setSpeakerphoneOn(userWantSpeaker);
			}
			
			audioManager.setMicrophoneMute(false);
			if(bluetoothWrapper != null && userWantBluetooth && bluetoothWrapper.canBluetooth()) {
				LogUtil.getUtils(TAG).d("Try to enable bluetooth");
				bluetoothWrapper.setBluetoothOn(true);
			}
		
		}else {
			//WebRTC implementation for routing
			int apiLevel = Compatibility.getApiLevel();
			
			//SetAudioMode
			// ***IMPORTANT*** When the API level for honeycomb (H) has been
	        // decided,
	        // the condition should be changed to include API level 8 to H-1.
	        if ( android.os.Build.BRAND.equalsIgnoreCase("Samsung") && (8 == apiLevel)) {
	            // Set Samsung specific VoIP mode for 2.2 devices
	            int mode = 4;
	            audioManager.setMode(mode);
	            if (audioManager.getMode() != mode) {
					LogUtil.getUtils(TAG).d("Could not set audio mode for Samsung device");
	            }
	        }

			
			
			//SetPlayoutSpeaker
	        if ((3 == apiLevel) || (4 == apiLevel)) {
	            // 1.5 and 1.6 devices
	            if (userWantSpeaker) {
	                // route audio to back speaker
	            	audioManager.setMode(AudioManager.MODE_NORMAL);
	            } else {
	                // route audio to earpiece
	            	audioManager.setMode(AudioManager.MODE_IN_CALL);
	            }
	        } else {
	            // 2.x devices
	            if ((android.os.Build.BRAND.equalsIgnoreCase("samsung")) &&
	                            ((5 == apiLevel) || (6 == apiLevel) ||
	                            (7 == apiLevel))) {
	                // Samsung 2.0, 2.0.1 and 2.1 devices
	                if (userWantSpeaker) {
	                    // route audio to back speaker
	                	audioManager.setMode(AudioManager.MODE_IN_CALL);
	                	audioManager.setSpeakerphoneOn(userWantSpeaker);
	                } else {
	                    // route audio to earpiece
	                	audioManager.setSpeakerphoneOn(userWantSpeaker);
	                	audioManager.setMode(AudioManager.MODE_NORMAL);
	                }
	            } else {
	                // Non-Samsung and Samsung 2.2 and up devices
	            	audioManager.setSpeakerphoneOn(userWantSpeaker);
	            }
	        }

			
		}
		
		//Set stream solo/volume/focus

		int inCallStream = Compatibility.getInCallStream(userWantBluetooth);

		LogUtil.getUtils(TAG).d("doFocusAudio:"+doFocusAudio + "--inCallStream:" +inCallStream);

		/** 20160918-mengbo-start: audioManager.setStreamSolo方法无效 **/
//		if(doFocusAudio) {
//			if(!accessibilityManager.isEnabled()) {
//
//				LogUtil.getUtils(TAG).d("audioManager.setStreamSolo");
//
//				audioManager.setStreamSolo(inCallStream, true);
//			}
//			// 20160905-mengbo-start: 屏蔽此处，在启动service时调用
////			audioFocusWrapper.focus(userWantBluetooth);
//			// 20160905-mengbo-end
//		}
		/** 20160918-mengbo-end **/

		LogUtil.getUtils(TAG).d("Initial volume level : " + service.getPrefs().getInitialVolumeLevel());
		setStreamVolume(inCallStream,
				(int) (audioManager.getStreamMaxVolume(inCallStream) * service.getPrefs().getInitialVolumeLevel()),
				0);
		
		
		isSetAudioMode = true;
	//	System.gc();
	}
	

	private boolean needSaveAudioState = true;
	/**
	 * Save current audio mode in order to be able to restore it once done
	 */
    @SuppressWarnings("deprecation")
	private synchronized void saveAudioState() {
		if(!needSaveAudioState) {
			return;
		}
		if( prefs.getBoolean("isSavedAudioState", false) ) {
			//If we have already set, do not set it again !!! 
			return;
		}
		ContentResolver ctntResolver = service.getContentResolver();
		
		Editor ed = prefs.edit();
//		ed.putInt("savedVibrateRing", audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER));
//		ed.putInt("savedVibradeNotif", audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION));
//		ed.putInt("savedRingerMode", audioManager.getRingerMode());
		ed.putInt("savedWifiPolicy" , android.provider.Settings.System.getInt(ctntResolver, android.provider.Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT));
		
		int inCallStream = Compatibility.getInCallStream(userWantBluetooth);
		ed.putInt("savedVolume", audioManager.getStreamVolume(inCallStream));
		
		int targetMode = getAudioTargetMode();
		if(service.getPrefs().useRoutingApi()) {
			ed.putInt("savedRoute", audioManager.getRouting(targetMode));
		}
//		else {
//			ed.putBoolean("savedSpeakerPhone", audioManager.isSpeakerphoneOn());
//		}

//		Log.d("audiomode", "sip saved audio mode " + audioManager.getMode());
//		ed.putInt("savedMode", audioManager.getMode());
		
		ed.putBoolean("isSavedAudioState", true);
		/**2017-2-28 -wangzhen -modify.change commit by apply for lint warning**/
		//ed.commit();
		ed.apply();
	}
    /**
     * Restore the state of the audio
     */
    @SuppressWarnings("deprecation")
	@SuppressLint("FinalPrivateMethod")
	private final synchronized void restoreAudioState() {
		if(!needSaveAudioState) {
			return;
		}

		if( !prefs.getBoolean("isSavedAudioState", false) ) {
			//If we have NEVER set, do not try to reset !
			return;
		}
		
		ContentResolver ctntResolver = service.getContentResolver();

		Settings.System.putInt(ctntResolver, Settings.System.WIFI_SLEEP_POLICY, prefs.getInt("savedWifiPolicy", Settings.System.WIFI_SLEEP_POLICY_DEFAULT));
//		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, prefs.getInt("savedVibrateRing", AudioManager.VIBRATE_SETTING_ONLY_SILENT));
//		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, prefs.getInt("savedVibradeNotif", AudioManager.VIBRATE_SETTING_OFF));
//		audioManager.setRingerMode(prefs.getInt("savedRingerMode", AudioManager.RINGER_MODE_NORMAL));
		
		int inCallStream = Compatibility.getInCallStream(userWantBluetooth);
		setStreamVolume(inCallStream, prefs.getInt("savedVolume", (int)(audioManager.getStreamMaxVolume(inCallStream)*0.8) ), 0);
		
		int targetMode = getAudioTargetMode();
		if(service.getPrefs().useRoutingApi()) {
			audioManager.setRouting(targetMode, prefs.getInt("savedRoute", AudioManager.ROUTE_SPEAKER), AudioManager.ROUTE_ALL);
		}
//		else {
//			audioManager.setSpeakerphoneOn(prefs.getBoolean("savedSpeakerPhone", false));
//		}

//		Log.d("audiomode", "sip restore audio mode " + prefs.getInt("savedMode", AudioManager.MODE_NORMAL));
//		audioManager.setMode(prefs.getInt("savedMode", AudioManager.MODE_NORMAL));
		

		Editor ed = prefs.edit();
		ed.putBoolean("isSavedAudioState", false);
		ed.apply();
	}
	
	/**
	 * Reset the audio mode
	 */
	private synchronized void actualUnsetAudioInCall() {

		LogUtil.getUtils(TAG).d("MediaManager---actualUnsetAudioInCall---11111"
				+ "--prefs.getBoolean(isSavedAudioStat, false):" + prefs.getBoolean("isSavedAudioState", false)
				+ "--isSetAudioMode:" + isSetAudioMode);

		if(!prefs.getBoolean("isSavedAudioState", false) || !isSetAudioMode) {
			return;
		}

		LogUtil.getUtils(TAG).d("Unset Audio In call");

		int inCallStream = Compatibility.getInCallStream(userWantBluetooth);

		LogUtil.getUtils(TAG).d("MediaManager---actualUnsetAudioInCall---22222" + "--inCallStream:" + inCallStream);

		if(bluetoothWrapper != null) {
			//This fixes the BT activation but... but... seems to introduce a lot of other issues
			//bluetoothWrapper.setBluetoothOn(true);
			LogUtil.getUtils(TAG).d("Unset bt");
			bluetoothWrapper.setBluetoothOn(false);
		}
		audioManager.setMicrophoneMute(false);
		if(doFocusAudio) {
			audioManager.setStreamVolume(inCallStream,0,0);

			// 20160905-mengbo-start: 屏蔽此处，在关闭service时调用
//			audioFocusWrapper.unFocus();
			// 20160905-mengbo-end
		}

		LogUtil.getUtils(TAG).d("MediaManager---actualUnsetAudioInCall---33333" + "--doFocusAudio:" + doFocusAudio);
		restoreAudioState();

		LogUtil.getUtils(TAG).d("MediaManager---actualUnsetAudioInCall---44444");
		
		if(wifiLock != null && wifiLock.isHeld()) {
			wifiLock.release();
		}
		if(screenLock != null && screenLock.isHeld()) {
			LogUtil.getUtils(TAG).d("Release screen lock");
			screenLock.release();
		}
		
		
		isSetAudioMode = false;
	}
	
	/**
	 * Start ringing announce for a given contact.
	 * It will also focus audio for us.
	 * @param remoteContact the contact to ring for. May resolve the contact ringtone if any.
	 */
	synchronized public void startRing(String remoteContact) {
		saveAudioState();
		
		if(!ringer.isRinging()) {
			ringer.ring(remoteContact, service.getPrefs().getRingtone());
		}else {
			LogUtil.getUtils(TAG).d("Already ringing ....");
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
	 * Stop call announcement.
	 */
	public void stopRingAndUnfocus() {
		stopRing();
		if(audioFocusWrapper != null){
			audioFocusWrapper.unFocus();
		}
	}
	
	public void resetSettings() {
		userWantBluetooth = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.AUTO_CONNECT_BLUETOOTH);
		userWantSpeaker = service.getPrefs().getPreferenceBooleanValue(SipConfigManager.AUTO_CONNECT_SPEAKER);
        userWantHeadset = false;
		userWantMicrophoneMute = false;
	}


	public void toggleMute() throws SameThreadException {
		setMicrophoneMute(!userWantMicrophoneMute);
	}

	public void setMediaState(MediaState mediaState) {
		//[S]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
		//bluetoothWrapper.setBluetoothChangeListener(this);
		//[E]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
		userWantBluetooth = mediaState.canBluetoothSco && mediaState.isBluetoothScoOn;
		userWantMicrophoneMute = mediaState.canMicrophoneMute&&mediaState.isMicrophoneMute;
		userWantSpeaker = mediaState.canSpeakerphoneOn && mediaState.isSpeakerphoneOn;
        userWantHeadset = mediaState.isHeadset;
	}


	public void setMicrophoneMute(boolean on) {
		if(on != userWantMicrophoneMute ) {
			userWantMicrophoneMute = on;
			setSoftwareVolume();
//			broadcastMediaChanged(MUTE);
			updateMediaState();
		}
	}
	
	public void setSpeakerphoneOn(boolean on) throws SameThreadException {

		if(on) {
			userWantBluetooth = false;
		}

		if(service != null && restartAudioWhenRoutingChange && !ringer.isRinging()) {
			service.setNoSnd();
			userWantSpeaker = on;
			service.setSnd();
		}else {
			userWantSpeaker = on;
			audioManager.setSpeakerphoneOn(on);
		}
//		broadcastMediaChanged(SPEAKER);
        updateMediaState();
	}

	/** 20160928-mengbo-start: 修复通话中插入耳机，耳机麦克风无声 **/
    public void setHeadsetOn(boolean on) throws SameThreadException {
		userWantHeadset = on;
		if (on && userWantSpeaker) {
			//关闭扬声器
			isSetSpeakerphoneOn(false);
		}

		/** 20170324-mengbo-start: 解决有些手机插拔耳机后通话无声问题 **/
		if(Compatibility.shouldResetSnd()){
			service.setNoSnd();
			service.setSnd();
		}
		/** 20170324-mengbo-end **/

		updateMediaState();
    }
	/** 20160928-mengbo-end **/

	/**
	 * wxf@xdja.com
	 * 打开扬声器时候插入线控耳机
	 * @param on
     */
	public void isSetSpeakerphoneOn(boolean on){
		    userWantSpeaker = on;
			audioManager.setSpeakerphoneOn(on);
	}
	
	public void setBluetoothOn(boolean on) throws SameThreadException {
		LogUtil.getUtils(TAG).d("Set BT " + on);
		userWantBluetooth = on;
		/** 20161010-mengbo-start: 蓝牙连接扬声器不可用 **/
		if(on && userWantSpeaker) {
			isSetSpeakerphoneOn(false);
		}
		/** 20161010-mengbo-end **/
		if(service != null && restartAudioWhenRoutingChange && !ringer.isRinging()) {
		    service.setNoSnd();
			service.setSnd();
		}else {
		    bluetoothWrapper.setBluetoothOn(on);
		}
		//wxf@xdja.com 2016-09-26 add. fix bug 4471 . review by mengbo. Start
		updateMediaState();
		//wxf@xdja.com 2016-09-26 add. fix bug 4471 . review by mengbo. End
	}
	

	

	public MediaState getMediaState() {
		MediaState mediaState = new MediaState();
		
		// Micro 
		mediaState.isMicrophoneMute = userWantMicrophoneMute;
		mediaState.canMicrophoneMute = true; /*&& !mediaState.isBluetoothScoOn*/ //Compatibility.isCompatible(5);
		
		// Speaker
		mediaState.isSpeakerphoneOn = userWantSpeaker;
		mediaState.canSpeakerphoneOn =true; //Compatibility.isCompatible(5);
		
		//Bluetooth
		
		if(bluetoothWrapper != null) {
			mediaState.isBluetoothScoOn = userWantBluetooth  &&  bluetoothWrapper.isBluetoothOn();
			mediaState.canBluetoothSco = bluetoothWrapper.canBluetooth();
		}else {
			mediaState.isBluetoothScoOn = false;
			mediaState.canBluetoothSco = false;
		}

		// Headset
		mediaState.isHeadset = userWantHeadset;

		return mediaState;
	}

	/**
	 * Change the audio volume amplification according to the fact we are using bluetooth
	 */
	public void setSoftwareVolume(){

		LogUtil.getUtils(TAG).d("#-#-#- setSoftwareVolume");

		if(service != null) {
			final boolean useBT = (bluetoothWrapper != null && bluetoothWrapper.isBluetoothOn());

			String speaker_key = useBT ? SipConfigManager.SND_BT_SPEAKER_LEVEL : SipConfigManager.SND_SPEAKER_LEVEL;
			String mic_key = useBT ? SipConfigManager.SND_BT_MIC_LEVEL : SipConfigManager.SND_MIC_LEVEL;

			final float speakVolume = service.getPrefs().getPreferenceFloatValue(speaker_key);
			final float micVolume = userWantMicrophoneMute? 0 : service.getPrefs().getPreferenceFloatValue(mic_key);

			service.getExecutor().execute(new SipRunnable() {

				@Override
				protected void doRun() throws SameThreadException {
					service.confAdjustTxLevel(speakVolume);
					service.confAdjustRxLevel(micVolume);

					/** 20160918-mengbo-start: 优化连接蓝牙调手机音量键不起作用，及断开蓝牙调音量图标仍显示蓝牙问题**/
					// Force the BT mode to normal
					LogUtil.getUtils(TAG).d("#-#-#- setSoftwareVolume useBT:"+useBT);
					if(useBT) {
						audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
					}else{
						if(bluetoothWrapper != null) {
							LogUtil.getUtils(TAG).d("Unset bt");
							bluetoothWrapper.setBluetoothOn(false);
						}
					}
					/** 20160918-mengbo-end **/
				}
			});
		}
	}

	// 蓝牙耳机连接或者断开的相关事件处理 xjq 2015-11-16
	public static final String BT_HEADSET_KEY = "bluetooth_headset_connected";
	public static final int BT_HEADSET_CON_VAL = 1000;
	public static final int BT_HEADSET_DISCON_VAL = 1001;
	public static final long BT_HEADSET_CON_DELAY = 1500;

	public void broadcastMediaChanged() {
		mediaStateChangedIntent = new Intent(SipManager.ACTION_SIP_MEDIA_CHANGED);
		service.sendBroadcast(mediaStateChangedIntent, SipManager.PERMISSION_USE_SIP);
	}

	public void broadcastMediaChanged(String mediaDevice) {
		mediaStateChangedIntent = new Intent(SipManager.ACTION_SIP_MEDIA_CHANGED);
		mediaStateChangedIntent.putExtra(MEDIA_DEVICE, mediaDevice);
		service.sendBroadcast(mediaStateChangedIntent, SipManager.PERMISSION_USE_SIP);
	}

	/**
	 * 广播一些Intent中需要有附加参数的方法形式 xjq 2015-11-16
	 * @param status
	 */
	public void broadcastMediaChanged(int status){
		mediaStateChangedIntent = new Intent(SipManager.ACTION_SIP_MEDIA_CHANGED);
		mediaStateChangedIntent.putExtra(BT_HEADSET_KEY, status);
		service.sendBroadcast(mediaStateChangedIntent, SipManager.PERMISSION_USE_SIP);
	}

	/**
	 * 更新当前设备状态  包括 蓝牙耳机、外放、 静音等；
	 */
	private void  updateMediaState(){
		try {
			MediaState mediaState =service.getBinder().getCurrentMediaState();
			PNMessageManager.getInstance().mediaStateChanged(mediaState);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private static final String ACTION_AUDIO_VOLUME_UPDATE = "org.openintents.audio.action_volume_update";
	private static final String EXTRA_STREAM_TYPE = "org.openintents.audio.extra_stream_type";
	private static final String EXTRA_VOLUME_INDEX = "org.openintents.audio.extra_volume_index";
	private static final String EXTRA_RINGER_MODE = "org.openintents.audio.extra_ringer_mode";
	private static final int EXTRA_VALUE_UNKNOWN = -9999;

	private void broadcastVolumeWillBeUpdated(int streamType, int index) {
		Intent notificationIntent = new Intent(ACTION_AUDIO_VOLUME_UPDATE);
		notificationIntent.putExtra(EXTRA_STREAM_TYPE, streamType);
		notificationIntent.putExtra(EXTRA_VOLUME_INDEX, index);
		notificationIntent.putExtra(EXTRA_RINGER_MODE, EXTRA_VALUE_UNKNOWN);

		service.sendBroadcast(notificationIntent, null);
	}

	public void setStreamVolume(int streamType, int index, int flags) {
		broadcastVolumeWillBeUpdated(streamType, index);
		audioManager.setStreamVolume(streamType, index, flags);
	}

	public void adjustStreamVolume(int streamType, int direction, int flags) {
		broadcastVolumeWillBeUpdated(streamType, EXTRA_VALUE_UNKNOWN);
		audioManager.adjustStreamVolume(streamType, direction, flags);
		if(streamType == AudioManager.STREAM_RING) {
			// Update ringer
			ringer.updateRingerMode();
		}

		int inCallStream = Compatibility.getInCallStream(userWantBluetooth);
		if(streamType == inCallStream) {
			int maxLevel = audioManager.getStreamMaxVolume(inCallStream);
			float modifiedLevel = (audioManager.getStreamVolume(inCallStream)/(float) maxLevel)*10.0f;
			// Update default stream level
			service.getPrefs().setPreferenceFloatValue(SipConfigManager.SND_STREAM_LEVEL, modifiedLevel);

		}
	}

	// Public accessor
	public boolean doesUserWantMicrophoneMute() {
		return userWantMicrophoneMute;
	}

	public boolean doesUserWantBluetooth() {
		return userWantBluetooth;
	}

	// The possible tones we can play.
	public static final int TONE_NONE = 0;
	public static final int TONE_CALL_WAITING = 1;
	public static final int TONE_BUSY = 2;
	public static final int TONE_CONGESTION = 3;
	public static final int TONE_BATTERY_LOW = 4;
	public static final int TONE_CALL_ENDED = 5;

	/**
	 * Play a tone in band
	 * @param toneId the id of the tone to play.
	 */
	public void playInCallTone(int toneId) {
		(new InCallTonePlayer(toneId)).start();
	}


	/*
		震动
	 */
	public void vibrate(long time) {
		vibrator.vibrate(time);
	}
	/**
	 * Helper class to play tones through the earpiece (or speaker / BT)
	 * during a call, using the ToneGenerator.
	 *
	 * To use, just instantiate a new InCallTonePlayer
	 * (passing in the TONE_* constant for the tone you want)
	 * and start() it.
	 *
	 * When we're done playing the tone, if the phone is idle at that
	 * point, we'll reset the audio routing and speaker state.
	 * (That means that for tones that get played *after* a call
	 * disconnects, like "busy" or "congestion" or "call ended", you
	 * should NOT call resetAudioStateAfterDisconnect() yourself.
	 * Instead, just start the InCallTonePlayer, which will automatically
	 * defer the resetAudioStateAfterDisconnect() call until the tone
	 * finishes playing.)
	 */
	private class InCallTonePlayer extends Thread {
		private int mToneId;


		// The tone volume relative to other sounds in the stream
		private static final int TONE_RELATIVE_VOLUME_HIPRI = 80;
		private static final int TONE_RELATIVE_VOLUME_LOPRI = 50;

		InCallTonePlayer(int toneId) {
			super();
			mToneId = toneId;
		}

		@Override
		public void run() {
			LogUtil.getUtils(TAG).d("InCallTonePlayer.run(toneId = " + mToneId + ")...");

			int toneType; // passed to ToneGenerator.startTone()
			int toneVolume; // passed to the ToneGenerator constructor
			int toneLengthMillis;
			switch (mToneId) {
				case TONE_CALL_WAITING:
					toneType = ToneGenerator.TONE_SUP_CALL_WAITING;
					toneVolume = TONE_RELATIVE_VOLUME_HIPRI;
					toneLengthMillis = 5000;
					break;
				case TONE_BUSY:
					toneType = ToneGenerator.TONE_SUP_BUSY;
					toneVolume = TONE_RELATIVE_VOLUME_HIPRI;
					toneLengthMillis = 4000;
					break;
				case TONE_CONGESTION:
					toneType = ToneGenerator.TONE_SUP_CONGESTION;
					toneVolume = TONE_RELATIVE_VOLUME_HIPRI;
					toneLengthMillis = 4000;
					break;
				case TONE_BATTERY_LOW:
					// For now, use ToneGenerator.TONE_PROP_ACK (two quick
					// beeps). TODO: is there some other ToneGenerator
					// tone that would be more appropriate here? Or
					// should we consider adding a new custom tone?
					toneType = ToneGenerator.TONE_PROP_ACK;
					toneVolume = TONE_RELATIVE_VOLUME_HIPRI;
					toneLengthMillis = 1000;
					break;
				case TONE_CALL_ENDED:
					toneType = ToneGenerator.TONE_PROP_PROMPT;
					toneVolume = TONE_RELATIVE_VOLUME_LOPRI;
					toneLengthMillis = 2000;
					break;
				default:
					throw new IllegalArgumentException("Bad toneId: " + mToneId);
			}

			// If the mToneGenerator creation fails, just continue without it. It is
			// a local audio signal, and is not as important.
			ToneGenerator toneGenerator;
			try {
				toneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, toneVolume);
				// if (DBG) log("- created toneGenerator: " + toneGenerator);
			} catch (RuntimeException e) {
				LogUtil.getUtils(TAG).w("InCallTonePlayer: Exception caught while creating ToneGenerator: " + e);
				toneGenerator = null;
			}

			// Using the ToneGenerator (with the CALL_WAITING / BUSY /
			// CONGESTION tones at least), the ToneGenerator itself knows
			// the right pattern of tones to play; we do NOT need to
			// manually start/stop each individual tone, or manually
			// insert the correct delay between tones. (We just start it
			// and let it run for however long we want the tone pattern to
			// continue.)
			//
			// TODO: When we stop the ToneGenerator in the middle of a
			// "tone pattern", it sounds bad if we cut if off while the
			// tone is actually playing. Consider adding API to the
			// ToneGenerator to say "stop at the next silent part of the
			// pattern", or simply "play the pattern N times and then
			// stop."

			if (toneGenerator != null) {
				toneGenerator.startTone(toneType);
				SystemClock.sleep(toneLengthMillis);
				toneGenerator.stopTone();

				LogUtil.getUtils(TAG).d("- InCallTonePlayer: done playing.");
				toneGenerator.release();
			}
		}
	}

	@Override
	public void onBluetoothStateChanged(int status) {
	    //[S]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
		LogUtil.getUtils(TAG).d("onBluetoothStateChanged status " + status);
		UAStateReceiver uaStateReceiver = service.getUAStateReceiver();

		/** 20161025-mengbo-start: 优化蓝牙、扬声器在通话和非通话中的状态切换 **/
		if(uaStateReceiver != null && uaStateReceiver.getCallConfirmed() == null) {
			//非通话中
			boolean isUserWantSpeaker = false;
			PlayVoiceService playVoiceService = PlayVoiceManager.getInstance(service).getVoiceService();
			if(playVoiceService != null){
				VoiceMediaManager voiceMediaManager = playVoiceService.getVoiceMediaManager();
				if(voiceMediaManager != null){
					isUserWantSpeaker = voiceMediaManager.isUserWantSpeaker();
				}
			}
			if(isUserWantSpeaker){
				if(service != null) {
					service.getExecutor().execute(new SipRunnable() {
						@Override
						protected void doRun() throws SameThreadException {
							setSpeakerphoneOn(true);
						}
					});
				}
			}else{
				if(status == BT_HEADSET_CON_VAL) {
					mediaHandler.sendEmptyMessageDelayed(BT_HEADSET_CON_VAL, BT_HEADSET_CON_DELAY);
				} else if(status == BT_HEADSET_DISCON_VAL) {
					updateMediaState();
				} else {
					updateMediaState();
				}
			}
		} else {
			//通话中
			if(userWantSpeaker){
				if(service != null) {
					service.getExecutor().execute(new SipRunnable() {
						@Override
						protected void doRun() throws SameThreadException {
							setSpeakerphoneOn(true);
						}
					});
				}
			}else{
				/** 20161015-mengbo-start: 呼叫、通话中连接蓝牙，需求：蓝牙无效，界面状态不改变 **/
				//[E]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
				// 如果是蓝牙耳机事件，则调用此接口告知界面 xjq 2015-11-16
				if(status == BT_HEADSET_CON_VAL) {
					mediaHandler.sendEmptyMessageDelayed(BT_HEADSET_CON_VAL, BT_HEADSET_CON_DELAY);
				} else if(status == BT_HEADSET_DISCON_VAL) {
					updateMediaState();
				} else {
					updateMediaState();
				}
				/** 20161015-mengbo-end **/
			}

			setSoftwareVolume();
		}
		/** 20161025-mengbo-end **/
	}
	@SuppressLint("AndroidLintHandlerLeak")
	private Handler mediaHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case BT_HEADSET_CON_VAL:
					updateMediaState();
					break;
				default:
					break;
			}
		}
	};
	public static String getCurrentAudioMode(int mode) {
		String currentMode = "";
		switch (mode) {
			case AudioManager.MODE_NORMAL :
				currentMode =  "MODE_NORMAL " + mode + ActomaController.getApp().getString(R.string.MODE_NORMAL);
				break;

			case AudioManager.MODE_RINGTONE :
				currentMode =  "MODE_RINGTONE " + mode + ActomaController.getApp().getString(R.string.MODE_RINGTONE);
				break;

			case AudioManager.MODE_IN_CALL :
				currentMode = "MODE_IN_CALL " + mode + ActomaController.getApp().getString(R.string.MODE_IN_CALL);
				break;

			case AudioManager.MODE_IN_COMMUNICATION :
				currentMode = "MODE_IN_COMMUNICATION " + mode + ActomaController.getApp().getString(R.string.MODE_IN_COMMUNICATION);
				break;

			default:
				currentMode = ActomaController.getApp().getString(R.string.MODE_OTHER) + mode;
				break;
			/*case AudioManager.ROUTE_SPEAKER :
				currentMode = "ROUTE_SPEAKER " + mode + "扬声器模式";
				break;*/

		}
		return currentMode;
	}

	public static String getCurrentSpeakerMode(boolean isSpeakerOn) {
		if (isSpeakerOn) {
			return ActomaController.getApp().getString(R.string.ISSPEAKERON);
		} else {
			return ActomaController.getApp().getString(R.string.ISSPEAKEROFF);
		}
	}

	// 20160905-mengbo-start: 加入耳机是否连接状态回调
	@Override
	public void onHeadsetConnected() {
		LogUtil.getUtils(TAG).d("MediaManager onHeadsetConnected");
		headsetFlag = true;
		userWantHeadset = true;
        VOIPManager.getInstance().setHeadsetOn(true);
		//wxf@xdja.com 2016-10-18 add. fix bug 4905 . review by mengbo. Start
		if(service != null){
			HeadsetButtonReceiver.setService(service.getUAStateReceiver());
		}
		//wxf@xdja.com 2016-10-18 add. fix bug 4905 . review by mengbo. End
	}

	@Override
	public void onHeadsetDisconnected() {
		LogUtil.getUtils(TAG).d("MediaManager onHeadsetDisconnected");
		//弹出吐司提示用户
		if(headsetFlag){
			if (BluetoothUtils8.getInstance(ActomaController.getApp()).isBluetoothOn()) {
				Toast.makeText(ActomaController.getApp(), ActomaController.getApp().getString(R.string.BLUETOOTH_ON), Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(ActomaController.getApp(), ActomaController.getApp().getString(R.string.SPEAKER_ON), Toast.LENGTH_LONG).show();
			}
		}
		userWantHeadset = false;
        VOIPManager.getInstance().setHeadsetOn(false);
		//wxf@xdja.com 2016-10-18 add. fix bug 4905 . review by mengbo. Start
		HeadsetButtonReceiver.setService(null);
		//wxf@xdja.com 2016-10-18 add. fix bug 4905 . review by mengbo. End
	}
	// 20160905-mengbo-end


	/** 20160910-mengbo-start: 注销广播 **/
	public void unregisterReceiver(){
		LogUtil.getUtils(TAG).d("MediaManager unregisterReceiver");

		if(bluetoothWrapper != null) {
			bluetoothWrapper.unregister();
			bluetoothWrapper.setBluetoothChangeListener(null);
			bluetoothWrapper = null;
		}

		if(headsetWrapper != null){
			headsetWrapper.unregister();
			headsetWrapper.setHeadsetConnectChangeListener(null);
			headsetWrapper = null;
		}

		if(audioFocusWrapper != null){
			audioFocusWrapper.unFocus();
			audioFocusWrapper = null;
		}
	}
	/** 20160910-mengbo-end **/
}
