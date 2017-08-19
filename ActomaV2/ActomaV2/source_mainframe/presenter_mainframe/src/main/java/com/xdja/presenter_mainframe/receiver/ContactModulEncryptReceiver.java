package com.xdja.presenter_mainframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.StateParams;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.contactcommon.ContactModuleProxy;
import com.xdja.contactcommon.dto.ContactDto;
import com.xdja.presenter_mainframe.enc3rd.service.EncryptManager;

import java.util.Map;

/**
 * Created by geyao on 2016/2/25.
 * 用于处理联系人加解密相关操作的receiver
 */
public class ContactModulEncryptReceiver extends BroadcastReceiver {
    /**
     * 联系人模块将关闭加密服务
     */
    private static final String CLOSE_ENCRYPT_SEVER = "com.xdja.contact.close_frame_transfer";
    /**
     * 联系人模块将开启加密服务
     */
    private static final String OPEN_ENCRYPT_SEVER = "com.xdja.contact.open_frame_transfer";
    /**
     * 联系人模块修改昵称
     */
    private static final String UPDATE_NICK_NAME = "com.xdja.contact.change_nick_name";
    /**
     * 联系人模块删除好友
     */
    private static final String DELETE_FRIEND = "com.xdja.contact.delete_friend_or_departmember";//modify by xnn for bug 9932
    /**
     * 昵称可显示的最大字符数
     */
    public static final int MAX_NICK_NAME_LENGTH = 6;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (!UniversalUtil.isXposed()) {
            return;
        }
        final EncryptManager manager = new EncryptManager();
        if (intent.getAction().equals(CLOSE_ENCRYPT_SEVER)) {//关闭加密服务
            //若广播发送的时间小于记录的点击扇形菜单开启加密服务的时间 则证明广播延迟接收 不处理
            long time = intent.getLongExtra("time", -1);
            long clickArcTime = StateParams.getStateParams().getClickArcTime();
            if (time < clickArcTime) {
                return;
            }
            clearActomaEncrypt(context);
        } else if (intent.getAction().equals(DELETE_FRIEND)) {//删除联系人好友或集团通讯录好友
            Map map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
            if (map == null) {
                return;
            }
            String account_receiver = intent.getStringExtra("account");
            String account_local = StateParams.getStateParams().getEncryptAccount();
            if (TextUtils.isEmpty(account_receiver) || TextUtils.isEmpty(account_local)) {
                return;
            }
            if (!account_local.equals(account_receiver)) {
                return;
            }
            //[S]modify by lixiaolong on 20161010. fix bug 4858. review by gbc.
                clearActomaEncrypt(context);//modify by xnn for bug 9932
            //[E]modify by lixiaolong on 20161010. fix bug 4858. review by gbc.
        } else if (intent.getAction().equals(OPEN_ENCRYPT_SEVER)) {//打开加密服务
//            String nickName = intent.getStringExtra("nick_name");
            String account = intent.getStringExtra("account");
            StateParams.getStateParams().setEncryptAccount(account);
        } else if (intent.getAction().equals(UPDATE_NICK_NAME)) {//修改昵称
            Map map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
            if (map == null) {
                return;
            }
            String account_receiver = intent.getStringExtra("account");
            String account_local = StateParams.getStateParams().getEncryptAccount();
            String pkgName = StateParams.getStateParams().getPkgName();
            // add by thz 增加对接受到的账户和本地设定的账户进行判空处理  2016-2-28
            if (TextUtils.isEmpty(account_receiver) ||
                    TextUtils.isEmpty(account_local) ||
                    TextUtils.isEmpty(pkgName)) {
                return;
            }
            //end
            if (account_local.equals(account_receiver)) {
                //String appName = IEncryptUtils.getAppName(pkgName);
                ContactDto contactDto = ContactModuleProxy.getContactInfo(account_receiver);
                //王浩 修正
                //String[] showName = ContactModuleProxy.getShowName(
                //        ActomaApp.getActomaApp().getApplicationContext(), account_receiver);
                String showName = contactDto.getName();
                showName = getCanShowNickName(showName);
                //manager.changeNotificationContent(context, showName + appName);

                //wxf@xdja.com 2016-11-11 add. fix bug 5346 . review by mengbo. Start
                // 修复打开加密通道时候通知栏刷新应用名称有误的问题
                manager.changeNotificationContent(context, showName, "");
                //wxf@xdja.com 2016-11-11 add. fix bug 5346 . review by mengbo. End
            }
        } else {//未知action类型的广播 不处理
            return;
        }

    }

    /**
     * 获取可显示的昵称
     *
     * @param nickName 原昵称
     * @return 可显示的昵称
     */
    public static String getCanShowNickName(String nickName) {
        //若拿到的昵称长度超过6个字符 则截取前面6个字符
        if (nickName.length() > MAX_NICK_NAME_LENGTH) {
            nickName = nickName.substring(0, MAX_NICK_NAME_LENGTH) + "...";
        }
        return nickName;
    }

    private void clearActomaEncrypt(final Context context) {
        boolean b = IEncryptUtils.closeAccountAppEncryptSwitch();
        if (b) {
            //告知联系人关闭加密服务
            ContactModuleProxy.safeTransferClosed(context);
            //修改加密通道安通账号静态变量值
            StateParams.getStateParams().setEncryptAccount(null);
            //关闭通知栏第三方加密通知
            new EncryptManager().clearNotificaiton(context);
        } else {
            //[S]modify by lixiaolong on 20161008. fixed bug 4702. review by myself.
            // XToast.show(ActomaApplication.getInstance().getApplicationContext(), "清除加密数据失败");
            LogUtil.getUtils().e("清除加密数据失败");
            //[E]modify by lixiaolong on 20161008. fixed bug 4702. review by myself.
        }
    }
}