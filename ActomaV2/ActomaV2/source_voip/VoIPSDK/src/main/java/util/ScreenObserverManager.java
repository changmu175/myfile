package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 
 * @author mengbo
 * 监听屏幕锁屏
 */
public class ScreenObserverManager {

	private static ScreenObserverManager mScreenObserverManager;
	private OnScreenStateUpdateListener mOnScreenStateUpdateListener;
	private ScreenBroadcastReceiver mScreenBroadcastReceiver;
	private Context mContext;
	
	public static synchronized ScreenObserverManager getInstance(Context context){
		if (mScreenObserverManager == null){
			mScreenObserverManager = new ScreenObserverManager(context);
		}
		return mScreenObserverManager;
	}
	
	public  ScreenObserverManager(Context context){
		this.mContext=context;
		mScreenBroadcastReceiver=new ScreenBroadcastReceiver();
	}
	
	private class ScreenBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
				mOnScreenStateUpdateListener.onScreenOn();
			}else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
				mOnScreenStateUpdateListener.onScreenOff();
			}
		}
	}
	
	public interface OnScreenStateUpdateListener{
		void onScreenOn();
		void onScreenOff();
	}
	
	public void register(OnScreenStateUpdateListener listener){
		mOnScreenStateUpdateListener=listener;
		registerListener();
	}
	
	public void unregister(OnScreenStateUpdateListener listener){
		try{
			if(mScreenBroadcastReceiver != null){
				mContext.unregisterReceiver(mScreenBroadcastReceiver);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void registerListener() {
		IntentFilter mIntentFilter=new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mContext.registerReceiver(mScreenBroadcastReceiver, mIntentFilter);
	}
}
