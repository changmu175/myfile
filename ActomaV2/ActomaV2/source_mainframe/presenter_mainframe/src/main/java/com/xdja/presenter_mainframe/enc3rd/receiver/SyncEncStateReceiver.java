package com.xdja.presenter_mainframe.enc3rd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * <p>Summary:用于接收第三方加密状态</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.bean</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/8/30</p>
 * <p>Time:14:01</p>
 */
public abstract class SyncEncStateReceiver extends BroadcastReceiver {
    public static final String ACTION_ENC_STATE = "com.xdja.actoma.action.syncenc.encstate";

    public static final String ACTION_PWD_STATE = "com.xdja.actoma.action.syncenc.needpwd";

    public static final String ACTION_APP_STATE = "com.xdja.actoma.action.syncenc.changeapp";

    public static final String ARG_ENC_STATE = "encstate";

    public static final String ARG_PWD_STATE = "needpwd";

    public static final String ARG_APP_STATE = "appState";

    public static final String ARG_APP_ID = "appId";

    public SyncEncStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case ACTION_ENC_STATE:
                        boolean encState = intent.getBooleanExtra(ARG_ENC_STATE, false);
                        onSyncEncState(encState);
                        break;
                    case ACTION_PWD_STATE:
                        boolean isPwd = intent.getBooleanExtra(ARG_PWD_STATE, true);
                        onSyncPwdState(isPwd);
                        break;
                    case ACTION_APP_STATE:
                        boolean isAppEnc = intent.getBooleanExtra(ARG_APP_STATE, true);
                        String appid = intent.getStringExtra(ARG_APP_ID);
                        onSysncAppState(appid, isAppEnc);
                        break;
                }
            }
        }

    }

    /**
     * 接收加解密开关状态
     * @param isOpen 加解密开关开启状态
     */
    public abstract void onSyncEncState(boolean isOpen);
    /**
     * 接收密码输入验证开关状态
     * @param isOpen 密码输入验证开关开启状态
     */
    public abstract void onSyncPwdState(boolean isOpen);

    /**
     * 接收应用加密开关状态
     * @param appId 应用Id
     * @param isOpen    应用加密开启状态
     */
    public abstract void onSysncAppState(String appId,boolean isOpen);
}
