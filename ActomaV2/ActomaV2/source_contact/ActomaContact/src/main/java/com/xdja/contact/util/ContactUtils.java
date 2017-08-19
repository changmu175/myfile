package com.xdja.contact.util;

import android.app.Activity;
import android.content.Intent;

import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangpeng on 2015/4/14.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ContactUtils {

    private static long lastClickTime;
    public static boolean isFastDoubleClick(){
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if(0 < timeD && timeD < 800){
            return true;
        }
        lastClickTime = time;
        return  false;
    }


     /*start:add by wal@xdja.com for ckms 2016/8/3 */
    /**
     * 启动好友聊天
     * @param activity
     * @param account
     */
//    public static void startFriendTalk(Activity activity, String account){
//        if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
//            new CreateSGroupTask(CkmsGpEnDecryptManager.START_FRIEND_TALK,activity,account).execute();
//        }else {
//            startCkmsForFriendTalk(activity,account);
//        }
//
//    }
    public static void startFriendTalk(Activity activity,String account) {
        try {
            Intent intent = new Intent("com.xdja.imp.presenter.activity.ChatDetailActivity");
            intent.putExtra(RegisterActionUtil.TALKERID, account);
            intent.putExtra(RegisterActionUtil.TALKTYPE, 1);
            activity.startActivity(intent);
        } catch (Exception e){
            LogUtil.getUtils().e("ContactUtils startFriendTalk Exception:"+e.getMessage());
        }
    }

    // Task 2632 [Begin]
    public static void handOut( Activity activity, ArrayList<String> selectAccountList) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(RegisterActionUtil.SELECT_ACCOUNT, selectAccountList);
        activity.setResult(RegisterActionUtil.HAND_OUT, intent);
        activity.finish();
    }

    /**
     * 创建单人会话分享
     * @param activity
     * @param account
     */
    public static void startFriendTalkForShare(Activity activity, String account) {
        Intent intent = new Intent();
        intent.putExtra(RegisterActionUtil.ACCOUNT, account);
        activity.setResult(RegisterActionUtil.SINLE_SESSION, intent);
    }

    /**
     * 创建群聊分享
     * @param activity
     * @param groupId
     */
    public static void startGroupTalkForShare(Activity activity, String groupId) {
        Intent intent = new Intent();
        intent.putExtra(RegisterActionUtil.ACCOUNT, groupId);
        activity.setResult(RegisterActionUtil.GROUP_SESSION, intent);
    }
    // Task 2632 [End]

    /**
     * 开启群组聊天界面
     */
    public static void startGroupTalk(Activity activity,String groupId){
        try {
            Intent intent = new Intent("com.xdja.imp.presenter.activity.ChatDetailActivity");
            intent.putExtra(RegisterActionUtil.TALKERID, groupId);
            intent.putExtra(RegisterActionUtil.TALKTYPE , 2);
            activity.startActivity(intent);
        }catch (Exception e){
            LogUtil.getUtils().e("ContactUtils startGroupTalk Exception:"+e.getMessage());
        }
    }

    /**
     * 开启群组聊天界面
     */
//    public static void startGroupTalk(Activity activity,String groupId){
//        if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
//            new CreateSGroupTask(CkmsGpEnDecryptManager.START_GROUP_TALK,activity,groupId).execute();
//        }else{
//            startCkmsForGroupTalk(activity,groupId);
//        }
//    }

    /**
     * 拨打加密电话
     * @param activity
     * @param account
     */
//    public static void startVoip(Activity activity,String account){
//        if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
//            new CreateSGroupTask(CkmsGpEnDecryptManager.START_FRIEND_VOIP,activity,account).execute();
//        }else {
//            startCkmsForVoip(activity,account);
//        }
//    }
    public static void startVoip(Activity activity,String account){
        Intent intent = new Intent();
        intent.setAction("com.xdja.voip.ACTION_SIP_ACTOMA_CALLING");
        intent.putExtra("call_num", account);
        activity.sendBroadcast(intent, RegisterActionUtil.ACTION_ACTOMA_USE_SIP);
    }
 /*end:add by wal@xdja.com for ckms 2016/8/3 */


    /**
     * 获取当前账号信息
     * @return
     */
    public static AccountBean getCurrentBean(){
        AccountBean accountBean = AccountServer.getAccount();
        return accountBean;
    }


    /**
     * 获取当前账号
     * @return
     */
    public static String getCurrentAccount(){
        AccountBean accountBean = getCurrentBean();
        if(!ObjectUtil.objectIsEmpty(accountBean)){
            return accountBean.getAccount();
        }
        return "";
    }

    /**
     * 获取当前用户的别名
     * 如果没有别名则显示账号
     * @return
     */
    public static String getCurrentAccountAlias(){
        AccountBean accountBean = getCurrentBean();
        if(!ObjectUtil.objectIsEmpty(accountBean)){
            if(!ObjectUtil.stringIsEmpty(accountBean.getAlias())){
                return accountBean.getAlias();
            }
            return accountBean.getAccount();
        }
        return "";
    }

    /**
     * 获取当前用户的手机号
     * @return
     */
    public static String getCurrentAccountPhone(){
        AccountBean accountBean = getCurrentBean();
        if(!ObjectUtil.objectIsEmpty(accountBean)){
            return accountBean.getMobile();
        }
        return "";
    }

    /**
     * 获取集团号
     * @return
     */
    public static String getCompanyCode(){
        AccountBean accountBean = getCurrentBean();
        if(ObjectUtil.objectIsEmpty(accountBean)){
            return "";
        }else{
            String companyCode = accountBean.getCompanyCode();
            if(!ObjectUtil.stringIsEmpty(companyCode)){
                return companyCode;
            }
        }
        return "";
    }

    /**
     * 判断是否有集团联系人
     * @return
     */
    public static boolean isHasCompany(){
        AccountBean accountBean = getCurrentBean();
        if(ObjectUtil.objectIsEmpty(accountBean)){
            return false;
        }else{
            String companyCode = accountBean.getCompanyCode();
            if(!ObjectUtil.stringIsEmpty(companyCode)){
                return true;
            }
        }
        return false;
    }


    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    /**
     * 获取手机号
     *
     * @param phone
     * @return
     */
    public static String[] getPhones(String phone) {
        if (ObjectUtil.stringIsEmpty(phone)) {
            return null;
        } else {
            return phone.split("#");
        }
    }

     /*[S]modify by tangsha@20161103 for 5661(move from TaskPullAccountInfo)*/
    public static void updateLocalAccountInfo(String account, ResponseActomaAccount responseActomaAccount){
        Avatar avatar = responseActomaAccount.getAvatarBean();

        ActomAccountService actomAccountService = new ActomAccountService();
        actomAccountService.saveOrUpdate(responseActomaAccount);

        AvaterService avaterService = new AvaterService();
        avaterService.saveOrUpdate(avatar);
        //start:add by wal@xdja.com foe 2705
        List<String> accounts = new ArrayList<String>();
        accounts.add(account);
        FireEventUtils.pushFriendUpdateNickName(accounts);
		//end:add by wal@xdja.com foe 2705
    }
	/*[E]modify by tangsha@20161103 for 5661(move from TaskPullAccountInfo)*/

    /**
     *被拉入进群的账号可能是满群状态或者账号异常，让im提示用户
     *add by wal@xdja.com
     */
    public static void sendFailAddMemberEvent(List<String> outRangeNames,List<String> notAccountNames,String groupId){
        FireEventUtils.fireGroupMemberTips(outRangeNames, notAccountNames,groupId);//modify by wal@xdja.com for不能入群的事件通知
    }

    /**
     *被拉入进群的账号是未关联安全用户，让im提示用户
     *add by wal@xdja.com
     */
    public static void sendNoSecMemberEvent(List<String> noSecNames,String groupId){
        List<String> noSecShowNames = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("", noSecNames);
        FireEventUtils.fireNoSecGroupMemberTips(noSecShowNames,groupId);//modify by wal@xdja.com for不能入群的事件通知
    }

    /**
     *被拉入进群的账号已是群组成员，让im提示用户
     *add by wal@xdja.com for 4116
     */
    public static void sendMemberInGroupEvent(List<String> inGroupNames,String groupId){
        FireEventUtils.fireMemberInGroupTips(inGroupNames,groupId);
    }

    public static void contactLogoutAction(){
        TaskManager.getInstance().removeAllTask();
        GroupInternalService.setInstanceToEmpty();
        //PreferenceUtils.setDefaultUpdateId(context);
        //ContactDataBaseHelper.getInstance().deleteDatabase();
    }

}
