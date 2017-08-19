package com.securevoipcommon;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.csipsimple.utils.PreferencesProviderWrapper;
import com.csipsimple.utils.PreferencesWrapper;
import com.securevoip.presenter.fragment.CallLogFragmentPresenter;
import com.securevoip.utils.CallLogHelper;
import com.securevoip.voip.PhoneManager;
import com.xdja.comm.server.AccountServer;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.voipsdk.InCallPresenter;

import webrelay.VOIPManager;
import webrelay.VOIPPush;

/**
 * Created by gbc on 2015/7/25.
 */
public class VoipFunction {
    private static VoipFunction mInstance;
    private PhoneManager phoneManager;
    //SharedPreference里ticket的标识，调用主框架提供的接口时传入
    public static final String ticketTag = "ticket";
//    private Context context;

    private static final String THIS_FILE = "VoipFunction";

    private VoipFunction(){
        phoneManager = PhoneManager.getInstance();
    }

    public static VoipFunction getInstance() {
        if (null == mInstance) {
            synchronized (VoipFunction.class) {
                mInstance = new VoipFunction();
            }
        }
        return mInstance;
    }

    /*
    * entry
    * */
    public void initAccount(final Context context, String accName, String ticket) {
        VOIPManager.getInstance().initAccout(context,accName,ticket).registerInCallPlugin(InCallPresenter.class);
    }


    /**
     * 注册推送状态监听
     * @param cxt
     */
    public void initPush(Context cxt){
        VOIPPush.getInstance().init(cxt, TFCardManager.getTfCardId());
        VOIPPush.getInstance().registerPushStateReceiver(cxt);
    }

    public void unregisterPushStateReceiver(Context cxt){
        VOIPPush.getInstance().unregisterPushStateReceiver(cxt);
    }


    /**
     * 拨打加密电话 -- 即时通信模块
     * @param context
     * @param account
     */
    public void makeCall(Context context, String account) {
        String user= AccountServer.getAccount().getAccount();
        if (user==null || user.equals("") || account==null || account.equals(""))
            return;
        VOIPManager.getInstance().makeCall(account, user);

    }





    /**
     * 账户上线
     * @param context
     * @param account
     * @param ticket
     */
    public void online(Context context, String account, String ticket) {
        // 重置退出标识 xjq 2015-09-16
        PreferencesProviderWrapper prefProviderWrapper = new PreferencesProviderWrapper(context);
        prefProviderWrapper.setPreferenceBooleanValue(PreferencesWrapper.HAS_BEEN_QUIT, false);

        if ((!TextUtils.isEmpty(account) && !TextUtils.isEmpty(ticket))) {
            phoneManager.addAccount(context, account, account, ticket);
        }
    }


    /**
     * 账户下线
     * @param context
     */
    public void offline(Context context) {
        phoneManager.offLine(context);
    }



    /*----------------------------------------------------------------------------*/
    public Fragment getVoipCallLogFragment() {
        return new CallLogFragmentPresenter();
    }

    /**
     * 判断SipService是否存活，不存活，将其启动
     * @param context
     * @return
     */
    public boolean isSipServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.csipsimple.services.SipService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    /**
     * 清空通话记录
     */
    public boolean clearAllCallLog() {
        return CallLogHelper.removeAllCallLogs();
    }

    /**
     * 删除与该账户的通话记录
     * @param account
     */
    public void removeCallLogs(String account) {
        CallLogHelper.removeCallLogs(account);
    }


    /**
     * VoIP是否正在通话中
     * @return true 正在通话中， false 没有在通话中
     */
    public boolean hasActiveCall() {
        return VOIPManager.getInstance().hasActiveCall();
    }

    public boolean isMediaPlaying() {
        return VOIPManager.getInstance().isMediaPlaying();
    }




}
