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

package com.csipsimple.utils.bluetooth;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothClass.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.csipsimple.service.MediaManager;
import com.xdja.dependence.uitls.LogUtil;

import java.util.Set;

import webrelay.VOIPManager;

public class BluetoothUtils8 extends BluetoothWrapper {

	private static final String TAG = "BT8";
	private AudioManager audioManager;

	private boolean isBluetoothConnected = false;

	/**20160908-mengbo-start:主要用于解决蓝牙通话中，蓝牙接听键按下，蓝牙无声问题，改为声音从听筒出**/
	//之前蓝牙sco是否开启
	private boolean lastBluetoothScoOn = false;
	/**20160908-mengbo-end**/

	private BroadcastReceiver mediaStateReceiver = new BroadcastReceiver() {

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.getUtils(TAG).d("BluetoothUtils8 onReceive start");

			/**20160910-mengbo-start:没有打电话时，蓝牙监听失效**/
			if(VOIPManager.getInstance().getCurSession() == null){
				LogUtil.getUtils(TAG).d("BluetoothUtils8 onReceive but curSession null return");
				return;
			}
			/**20160910-mengbo-end**/

			String action = intent.getAction();
			if(action == null){
				return;
			}

			LogUtil.getUtils(TAG).d("BluetoothUtils8 onReceive action:" + action);

			//蓝牙SCO音频连接状态已改变
			if(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED.equals(action)) {
				int status = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, AudioManager.SCO_AUDIO_STATE_ERROR );
				//这个intent包含额外信息：EXTRA_SCO_AUDIO_STATE，它表明新的状态是SCO_AUDIO_STATE_DISCONNECTED或SCO_AUDIO_STATE_CONNECTED。
				LogUtil.getUtils(TAG).d("BT SCO state changed : " + status
						+ " target is " + targetBt
						+ " lastBluetoothScoOn is " + lastBluetoothScoOn);

				audioManager.setBluetoothScoOn(targetBt);

				if(status == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
					isBluetoothConnected = true;
					lastBluetoothScoOn = true;
				}else if(status == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
					/**20160908-mengbo-start:解决蓝牙通话中，蓝牙接听键按下，蓝牙无声问题，改为声音从听筒出**/
					if(targetBt && lastBluetoothScoOn){
						VOIPManager.getInstance().setBluetoothOn(false);
					}
					/**20160908-mengbo-end**/
					isBluetoothConnected = false;
					lastBluetoothScoOn = false;
				}

				if(btChangesListener != null) {
					btChangesListener.onBluetoothStateChanged(status);
				}
			}
			//[S]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
			else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)
					|| BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)
					|| BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                int headset =  adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
				LogUtil.getUtils(TAG).d("xjq blue tooth headphone headset connect state " + headset);

				if(BluetoothProfile.STATE_DISCONNECTED == headset) {

					/** 20160918-mengbo-start:  蓝牙断开时，蓝牙连接状态重置 **/
					isBluetoothConnected = false;
					/** 20160918-mengbo-end **/

					//Bluetooth headset is now disconnected
					if(btChangesListener != null) {
						btChangesListener.onBluetoothStateChanged(MediaManager.BT_HEADSET_DISCON_VAL);
					}else{
						LogUtil.getUtils(TAG).e("xjq btChangesListener is null-----");
					}

				} else if(BluetoothProfile.STATE_CONNECTED == headset) {

					isBluetoothConnected = false; // 此处需要重新设置此值为false xjq 2015-11-17

					if(btChangesListener != null) {
						btChangesListener.onBluetoothStateChanged(MediaManager.BT_HEADSET_CON_VAL);
					}else{
						LogUtil.getUtils(TAG).e("xjq btChangesListener is null-----");
					}

				}
			}
			//[E]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
		}
	};

	protected BluetoothAdapter bluetoothAdapter;

	@Override
	public void setContext(Context aContext){
		super.setContext(aContext);
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if(bluetoothAdapter == null) {
			try {
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			}catch(RuntimeException e) {
				e.printStackTrace();
				LogUtil.getUtils(TAG).w("Cant get default bluetooth adapter ");
			}
		}
	}

	public boolean canBluetooth() {
		// Detect if any bluetooth a device is available for call
		if (bluetoothAdapter == null) {
			// Device does not support Bluetooth

			return false;
		}
		boolean hasConnectedDevice = false;
		//If bluetooth is on
		if(bluetoothAdapter.isEnabled()) {

			//We get all bounded bluetooth devices
			// bounded is not enough, should search for connected devices....
			Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
			for(BluetoothDevice device : pairedDevices) {
				BluetoothClass bluetoothClass = device.getBluetoothClass();
				if (bluetoothClass != null) {
					int deviceClass = bluetoothClass.getDeviceClass();
					if(bluetoothClass.hasService(Service.RENDER) ||
							deviceClass == Device.AUDIO_VIDEO_WEARABLE_HEADSET ||
							deviceClass == Device.AUDIO_VIDEO_CAR_AUDIO ||
							deviceClass == Device.AUDIO_VIDEO_HANDSFREE ) {
						//And if any can be used as a audio handset
						hasConnectedDevice = true;
						break;
					}
				}
			}
		}
		//判断蓝牙耳机是否连接
		boolean isBluetoothHeadsetConnected= (BluetoothProfile.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET));
		boolean retVal = hasConnectedDevice && audioManager.isBluetoothScoAvailableOffCall() && isBluetoothHeadsetConnected;
		LogUtil.getUtils(TAG).d("Can I do BT ? " + retVal + " hasConnectedDevice " + hasConnectedDevice + " isBluetoothHeadsetConnected " + isBluetoothHeadsetConnected);
		return retVal;
	}

	//当前setBluetoothOn设置的蓝牙状态
	private boolean targetBt = false;
	public void setBluetoothOn(boolean on) {
		LogUtil.getUtils(TAG).d("Ask for " + on + " vs " + audioManager.isBluetoothScoOn());
		targetBt = on;
		if(on != isBluetoothConnected) {
			// BT SCO connection state is different from required activation
			if(on) {
				// First we try to connect
				LogUtil.getUtils(TAG).d("BT SCO on >>>");
				audioManager.startBluetoothSco();

			}else {
				LogUtil.getUtils(TAG).d("BT SCO off >>>");
				// We stop to use BT SCO
				audioManager.setBluetoothScoOn(false);
				// And we stop BT SCO connection
				audioManager.stopBluetoothSco();

			}
		}else if(on != audioManager.isBluetoothScoOn()) {
			// BT SCO is already in desired connection state
			// we only have to use it
			audioManager.setBluetoothScoOn(on);
		}

	}

	public boolean isBluetoothOn() {
		return isBluetoothConnected;
	}

	@SuppressWarnings("deprecation")
	public void register() {
		LogUtil.getUtils(TAG).d("Register BT media receiver");

		//监听蓝牙耳机状态。 xjq 2015-10-23
		IntentFilter bluetoothFilter = new IntentFilter();
		//[S]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
		bluetoothFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
		bluetoothFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
		bluetoothFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		context.registerReceiver(mediaStateReceiver, bluetoothFilter);
		//[E]tangsha@xdja.com 2016-09-07 add. for 3707. review by mengbo.
	}

	public void unregister() {
		try {
			LogUtil.getUtils(TAG).d("Unregister BT media receiver");
			context.unregisterReceiver(mediaStateReceiver);
		}catch(Exception e) {
			e.printStackTrace();
			LogUtil.getUtils(TAG).w("Failed to unregister media state receiver");
		}
	}

	@Override
	public boolean isBTHeadsetConnected() {
		return canBluetooth();
	}
}
