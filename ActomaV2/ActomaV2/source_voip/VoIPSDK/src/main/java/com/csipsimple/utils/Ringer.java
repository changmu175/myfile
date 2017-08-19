/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
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
/**
 * This file contains relicensed code from Apache copyright of 
 * Copyright (C) 2006 The Android Open Source Project
 */

package com.csipsimple.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;

import com.csipsimple.models.CallerInfo;

/**
 * Ringer manager for the Phone app.
 */
public class Ringer {

    private static final int VIBRATE_LENGTH = 1000; // ms
    private static final int PAUSE_LENGTH = 1000; // ms

    // Uri for the ringtone.
//    Uri customRingtoneUri;

    Vibrator vibrator;
    VibratorThread vibratorThread;
    HandlerThread ringerThread;
    Context context;

    private RingWorkerHandler ringerWorker;

    private boolean isRing=false;

    public Ringer(Context aContext) {
        context = aContext;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        ringerThread = new HandlerThread("RingerThread");
        ringerThread.start();
        ringerWorker = new RingWorkerHandler(ringerThread.getLooper());
    }

    /**
     * Starts the ringtone and/or vibrator. 
     * 
     */
    public void ring(String remoteContact, String defaultRingtone) {
        isRing=true;
        synchronized (this) {
        	AudioManager audioManager =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        	
        	//Save ringtone at the begining in case we raise vol
            Ringtone ringtone = getRingtone(remoteContact, defaultRingtone);
            ringerWorker.setRingtone(ringtone);
            
        	//No ring no vibrate
            int ringerMode = audioManager.getRingerMode();
            if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
            	return;
            }
            
            // Vibrate
            int vibrateSetting = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
            if (vibratorThread == null &&
            		(vibrateSetting == AudioManager.VIBRATE_SETTING_ON || 
            				ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
                vibratorThread = new VibratorThread();
                vibratorThread.start();
            }

            // Vibrate only
            if (ringerMode == AudioManager.RINGER_MODE_VIBRATE ||
            		audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0 ) {
            	return;
            }

            // Ringer normal, audio set for ring, do it
            if(ringtone == null) {
            	return;
            }

            ringerWorker.startRinging(audioManager);
        	
        }
    }

    /**
     * @return true if we're playing a ringtone and/or vibrating
     *     to indicate that there's an incoming call.
     *     ("Ringing" here is used in the general sense.  If you literally
     *     need to know if we're playing a ringtone or vibrating, use
     *     isRingtonePlaying() or isVibrating() instead.)
     */
    public boolean isRinging() {
    	return isRing;
    }
    
    /**
     * Stops the ringtone and/or vibrator if any of these are actually
     * ringing/vibrating.
     */
	public void stopRing() {
        isRing=false;
        stopRingerAndVibrator();
	}


    /**
     * 即停止响铃也停止震动
     */
    private void stopRingerAndVibrator(){
        synchronized (this) {

            stopVibrator();
            stopRinger();
        }
    }

    
	private void stopRinger() {
	    ringerWorker.askStop();
	}
    
	private void stopVibrator() {

		if (vibratorThread != null) {
			vibratorThread.interrupt();
			try {
				vibratorThread.join(250); // Should be plenty long (typ.)
			} catch (InterruptedException e) {
			} // Best efforts (typ.)
			vibratorThread = null;
		}
	}

	public void updateRingerMode() {

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		synchronized (this) {
			int ringerMode = audioManager.getRingerMode();
			// Silent : stop everything
			if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
				stopRingerAndVibrator();
				return;
			}

			// Vibrate
			int vibrateSetting = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
			// If not already started restart it
			if (vibratorThread == null && (vibrateSetting == AudioManager.VIBRATE_SETTING_ON || ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
				vibratorThread = new VibratorThread();
				vibratorThread.start();
			}

			// Vibrate only
			if (ringerMode == AudioManager.RINGER_MODE_VIBRATE || audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
				stopRinger();
				return;
			}

            //Ringer
			ringerWorker.startRinging(audioManager);
		}
	}

    @SuppressLint("InfiniteLoopStatement")
    private class VibratorThread extends Thread {
        public void run() {
        	try {
	            while (true) {
	                vibrator.vibrate(VIBRATE_LENGTH);
	                Thread.sleep(VIBRATE_LENGTH + PAUSE_LENGTH);
	            }
        	} catch (InterruptedException ex) {
        	} finally {
        		vibrator.cancel();
        	}
        }
    }

    /**
     * Thread worker class that handles the task playing ringtone
     */
    private class RingWorkerHandler extends Handler {
        public static final int PROGRESS_RING = 0;
        private Boolean askedStopped = false;
        private Ringtone ringtone = null;

        public RingWorkerHandler(Looper looper) {
            super(looper);
        }
        
        /**
         * @param audioManager 
         * 
         */
        public void startRinging(AudioManager audioManager) {
            if(ringtone != null) {
                askedStopped=false;
                Message msg = ringerWorker.obtainMessage(RingWorkerHandler.PROGRESS_RING);
                msg.arg1 = RingWorkerHandler.PROGRESS_RING;
                audioManager.setMode(AudioManager.MODE_RINGTONE);
                ringerWorker.sendMessage(msg);
            }
        }

        /**
         * @param ringtone
         */
        public synchronized void setRingtone(Ringtone ringtone) {
            if(this.ringtone != null) {
                this.ringtone.stop();
            }
            this.ringtone = ringtone;
            askedStopped = false;
        }

        public synchronized void askStop() {
            askedStopped = true;
        }
        
        public synchronized boolean isStopped() {
            return askedStopped || (ringtone == null);
        }

        @SuppressLint("SynchronizeOnNonFinalField")
        public void handleMessage(Message msg) {
            if(ringtone == null) {
                return;
            }
            if (msg.arg1 == PROGRESS_RING) {
                synchronized (askedStopped) {
                    if(askedStopped) {
                        ringtone.stop();
                        if (!isRinging()){
                            ringtone = null;
                        }
                        return;
                    }
                    if(!ringtone.isPlaying()) {
                        /** 20170104-mengbo-start: 解决 IIIegalStateException crash **/
                        try{
                            ringtone.play();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        /** 20170104-mengbo-end **/
                    }
                }


                Message msgBis = ringerWorker.obtainMessage(RingWorkerHandler.PROGRESS_RING);
                msg.arg1 = RingWorkerHandler.PROGRESS_RING;
                ringerWorker.sendMessageDelayed(msgBis, 100);
            }
        }
    }

    private Ringtone getRingtone(String remoteContact, String defaultRingtone) {
    	Uri ringtoneUri = Uri.parse(defaultRingtone);
		
		// TODO - Should this be in a separate thread? We would still have to wait for
		// it to complete, so at present, no.
		CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(remoteContact);
		
		if(callerInfo != null && callerInfo.contactExists && callerInfo.contactRingtoneUri != null) {
			ringtoneUri = callerInfo.contactRingtoneUri;
		}
		
		return RingtoneManager.getRingtone(context, ringtoneUri);
    }



    /**
     * 情景模式切换广播接收器,方便情景模式变化时,实现相应的场景
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
//                if (isRinging())
                if (isRing)
                   updateRingerMode();
//                AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
//                final int ringerMode = am.getRingerMode();
//                switch (ringerMode) {
//                    case AudioManager.RINGER_MODE_NORMAL:
//                        Log.d(THIS_FILE, "RINGER_MODE_NORMAL");
//                        //normal
//                        break;
//                    case AudioManager.RINGER_MODE_VIBRATE:
//                        Log.d(THIS_FILE, "RINGER_MODE_VIBRATE");
//                        //vibrate
//                        break;
//                    case AudioManager.RINGER_MODE_SILENT:
//                        Log.d(THIS_FILE, "RINGER_MODE_SILENT");
//                        //silent
//                        break;
//                }
            }
        }
    } ;


    /**
     * 注册情景模式切换监听
     */
    public void  registerRingModeChangedReceiver(){

        IntentFilter intentFilter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        context.registerReceiver(mReceiver,intentFilter);

    }


    /**
     * 注销情景模式切换监听
     */
    public void unRegisterRingModeChangedReceiver(){
        context.unregisterReceiver(mReceiver);
    }

}
