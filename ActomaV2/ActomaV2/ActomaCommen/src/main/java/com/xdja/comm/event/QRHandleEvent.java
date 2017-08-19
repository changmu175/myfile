package com.xdja.comm.event;

import android.app.Activity;
import android.content.Context;

/**
 * Created by chenbing on 2015/11/26.
 * 二维码扫描结果处理通知事件
 */
public class QRHandleEvent {
    private Context context;
    private Activity activity;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
