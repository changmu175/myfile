package com.securevoip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import webrelay.VOIPManager;

/**
 * Created by admin on 16/3/17.
 */
public class HaiXinDeviceReceiver extends BroadcastReceiver  {

    private static final String XDJA_KEY_DOWN_CALL = "com.xdja.actoma.key.down";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {
            case XDJA_KEY_DOWN_CALL:
                if(VOIPManager.getInstance().hasActiveCall()){
                    /** 20161019-mengbo-start: 通话结束界面弹出0.5秒，并挂断 **/
                    new AsyncTask<Object, Object, Object>() {

                        protected void onPreExecute() {
                            VOIPManager.getInstance().startPlugin();
                        }

                        protected Object doInBackground(Object... params) {
                            try{
                                Thread.sleep(100);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            return null;
                        }

                        protected void onPostExecute(Object result) {
                            VOIPManager.getInstance().HXhangup();
                        }

                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    /** 20161019-mengbo-end **/
                }
                break;
            default :
                break;
        }
    }
}
