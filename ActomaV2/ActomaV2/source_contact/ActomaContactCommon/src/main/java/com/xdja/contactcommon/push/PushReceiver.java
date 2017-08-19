package com.xdja.contactcommon.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.xdja.comm.blade.SendCommBroadcast;
import com.xdja.comm.data.AccountBean;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.PushMessage;
import com.xdja.comm.contacttask.ITask;
import com.xdja.contact.task.push.PushDepartmentTaskContact;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

/**
 * Created by wanghao on 2015/8/7.
 */
public class PushReceiver extends BroadcastReceiver {

    /**
     * 消息动作
     */
    public static final String PUSH_ACTION = "com.xdja.push.MESSAGE";

    /**
     * 消息内容，可以是任意格式
     */
    public static final String PUSH_SERVICE_TYPE = "NOTIFICATION_MESSAGE";

    private static final String TAG = "ActomaContact PushReceiver ";

    public static boolean validateConfiguration(){
        AccountBean accountBean = ContactUtils.getCurrentBean();
        if(ObjectUtil.objectIsEmpty(accountBean)){
            LogUtil.getUtils().e(TAG+"用户未登录或者清楚了本地数据");
            return false;
        }
        if(ObjectUtil.stringIsEmpty(accountBean.getAccount())){
            LogUtil.getUtils().e(TAG+"用户未登录或者清楚了本地数据");
            return false;
        }
        return true;
    }

    /**
     * 推送: 1 请求添加好友推送 2 对方接受好友请求推送 3 账户激活安通账户推送 4
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //收到推送先检查 ticket 或者 url配置文件
        if(!validateConfiguration())return;
        String action = intent.getAction();
        LogUtil.getUtils().e(TAG+"Androidpn_contact received " + action);
        if(PUSH_ACTION.equals(action)){
            String serviceType = intent.getStringExtra(PUSH_SERVICE_TYPE);
            LogUtil.getUtils().e(TAG+"Androidpn_contact received serviceType " + serviceType);
            if(ObjectUtil.stringIsEmpty(serviceType)){
                return;
            }
            PushMessage pushMessage = new PushMessage(context,serviceType);
            if(ITask.PUSH_CONTACT_UNBIND.equals(pushMessage.getPushServiceType())){
                companyCodeChage();
            }else if(ITask.PUSH_CONTACT_UPDATE.equals(pushMessage.getPushServiceType())){
                if(ContactUtils.isHasCompany()){
                    //增量更新集团联系人
                    new PushDepartmentTaskContact().template();
                }else{
                    companyCodeChage();
                }
            }else{
                ITask strategy = PushServiceFactory.getInstance().getStrategy(pushMessage);
                if(!ObjectUtil.objectIsEmpty(strategy)){
                    strategy.template();
                }
            }
        }
    }

    private void companyCodeChage(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String currentAccount = ContactUtils.getCurrentAccount();
                if(ContactModuleProxy.compareCompanyCodeServerToLocal(currentAccount) == false){
                   SendCommBroadcast.sendApplicationExitBroadcast();
                }
                return null;
            }
        }.execute();
    }
}
