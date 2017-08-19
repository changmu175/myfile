package com.xdja.contact.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterActivity;

/**
 * Created by wanghao on 2016/6/2.
 */
public class BroadcastLoginOut extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //if(intent.getAction().equals(action)){
        if(BasePresenterActivity.ACTION_APPLICATION_EXIT.equals(intent.getAction())){
            LogUtil.getUtils().e("Actoma contact BroadcastLoginOut,action:"+intent.getAction());
            //ContactUtils.contactLogoutAction();//has done in ContactAccountLifeCycle logout
        }
    }
}
