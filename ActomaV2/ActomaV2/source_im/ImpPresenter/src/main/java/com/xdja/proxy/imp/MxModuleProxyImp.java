package com.xdja.proxy.imp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.xdja.frame.data.cache.SharedPreferencesUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.presenter.fragment.ChatListFragmentPresenter;
import com.xdja.imp.service.SimcUiService;
import com.xdja.imp.util.NotificationUtil;
import com.xdja.proxy.IMxModuleProxy;

/**
 * Created by XDJA_XA on 2016/3/22.
 */
public class MxModuleProxyImp implements IMxModuleProxy {

    private static IMxModuleProxy instance = null;

    public static IMxModuleProxy getInstance() {
        if (instance == null) {
            synchronized (MxModuleProxyImp.class)  {
                if (instance == null) {
                    instance = new MxModuleProxyImp();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean startMXService(Context context, String account, String cardId, String ticket) {
        Log.v("SimcUiService" , "H>>> Start SimcUiService");
        SharedPreferencesUtil.setNormalStopService(context , false);
        Intent intent = new Intent();
        intent.setClass(context, SimcUiService.class);

        Log.d("SimcUiService", "startMXService, account="+account+", cardId="+cardId+", ticket="+ticket );
        if (account != null && !account.isEmpty()) {
            intent.putExtra(ConstDef.TAG_CARDID, cardId);
            intent.putExtra(ConstDef.TAG_ACCOUNT, account);
            intent.putExtra(ConstDef.TAG_TICKET, ticket);
        }
        context.startService(intent);
        return false;
    }

    @Override
    public void stopService(Context context) {
        Log.v("SimcUiService" , "H>>> Stop SimcUiService");
        SharedPreferencesUtil.setNormalStopService(context , true);
        Intent intent = new Intent();
        intent.setClass(context, SimcUiService.class);
        context.stopService(intent);
    }

    @Override
    public boolean dropAllMessages(Context context) {
        Intent intent = new Intent("com.xdja.imp.service.ClearAllMessages");
        intent.setClass(context, SimcUiService.class);
        context.startService(intent);

        return true;
    }

    @Override
    public boolean clearNotifications(Context context) {
        NotificationUtil.getInstance(context).clearPNNotification();
        return true;
    }

    @Override
    public Fragment createChatListFragment() {
        return new ChatListFragmentPresenter();
    }

    @Override
    public void startChatActivity(Context context, String account, int chatType) {
        try {
            Intent intent = new Intent("com.xdja.imp.presenter.activity.ChatDetailActivity");
            intent.putExtra(ConstDef.TAG_TALKERID, account);
            intent.putExtra(ConstDef.TAG_TALKTYPE, chatType);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
