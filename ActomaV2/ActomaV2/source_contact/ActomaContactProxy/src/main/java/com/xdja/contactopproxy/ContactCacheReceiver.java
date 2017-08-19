package com.xdja.contactopproxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.UpdateContactShowNameEvent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;

/**
 * 用户接收需要清理联系人缓存的广播
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-09-12 14:27
 */
public class ContactCacheReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String accountChangeAction = BasePresenterActivity.ACTION_APPLICATION_EXIT;

        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            String actionStr = intent.getAction();
            switch (actionStr) {
                case RegisterActionUtil.ACTION_ACCOUNT_DOWNLOAD_SUCCESS:
                case RegisterActionUtil.ACTION_DEPARTMENT_DOWNLOAD_SUCCESS:
                case RegisterActionUtil.ACTION_GROUP_DOWNLOAD_SUCCESS:
                case RegisterActionUtil.ACTION_REQUEST_DOWNLOAD_SUCCESS:
                case RegisterActionUtil.ACTION_REFRESH_LIST://add by wal@xdja.com for 4160
                case accountChangeAction://add by lixiaolong on 20160918. 切换账号时的广播
                    LogUtil.getUtils().e("ActomaContactCacheReceiver "+actionStr+" to clearCache ");
                    ContactCache.getInstance().clearCache();
                    BusProvider.getMainProvider().post(new UpdateContactShowNameEvent());//add by wal@xdja.com for 4160 更新im，voip账号展示名称
                    break;
                default:
                    break;
            }
        }
    }
}
