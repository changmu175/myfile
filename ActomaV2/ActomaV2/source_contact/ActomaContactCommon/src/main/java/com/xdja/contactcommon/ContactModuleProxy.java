package com.xdja.contactcommon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.contact.exception.http.DepartmentException;
import com.xdja.contact.http.DepartmentHttpServiceHelper;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.bean.ErrorPush;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.bean.dto.ContactNameDto;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.dao.AvatarDao;
import com.xdja.contact.presenter.fragment.ContactFragment;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.service.ErrorPushService;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.comm.contacttask.ITask;
import com.xdja.contact.task.account.TaskIncrementAccount;
import com.xdja.contact.task.configuration.TaskContactConfiguration;
import com.xdja.contact.task.department.TaskIncrementDepartContact;
import com.xdja.contact.task.friend.TaskIncrementFriend;
import com.xdja.contact.task.friend.TaskIncrementalRequest;
import com.xdja.contact.task.group.TaskIncrementGroup;
import com.xdja.contact.usereditor.fragment.ContactEditorFragment;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contactcommon.dto.ContactDto;
import com.xdja.contactcommon.push.PushServiceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghao on 2015/8/11.
 * 其他模块对接使用的都是当前对象
 */
public class ContactModuleProxy {

    private static final String TAG = ContactModuleProxy.class.getSimpleName();

    public interface ContactModuleResult {

        void result(boolean bool);

    }

    /***
     * 最初和产品确认过需要每小时触发一次
     * 一下定时刷新的动作不在使用
     * 原因如下: 在最初的方案的里面介于密信 自己收发消息的通道和联系人推送不是同一条通道，导致消息正常收发但是联系人推送可能不在线
     * 所以为了保证联系人的数据完整性就触发定时机制
     * 当前：新IM的机制是走推送然后拉取机制，而且目前的推送和联系人是同一个推送 如果推送异常了那么所有的安通+业务都会受到影响，换句话说
     * 只要新IM正常那么推送必然正常同时联系人的数据推送也必然正常。因此这里不再需要定时触发业务
     * @param context
     */
    /*
    public static void startAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmReceiver.ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long espasedTime = 1 * 60 * 60 * 1000;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + espasedTime, espasedTime, pi);
    }

    public static void stopAlarm(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmReceiver.ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pi);
    }*/


    /**
     * 登录或者定时刷新流程执行以下业务
     * <ol>
     * <li>账户信息增量</li>
     * <li>好友信息增量</li>
     * <li>好友请求信息增量</li>
     * <li>群组信息增量</li>
     * <li>集团信息增量</li>
     * <ol/>
     */
    public static void initContactsModule(){

        TaskContactConfiguration taskContactConfiguration = new TaskContactConfiguration();
        taskContactConfiguration.template();

        TaskIncrementAccount taskIncrementAccount = new TaskIncrementAccount();
        taskIncrementAccount.template();

        TaskIncrementFriend taskDownloadFriend = new TaskIncrementFriend();
        taskDownloadFriend.template();

        TaskIncrementalRequest taskIncrementalRequest = new TaskIncrementalRequest();
        taskIncrementalRequest.template();

        TaskIncrementGroup taskIncrementGroup = new TaskIncrementGroup();
        taskIncrementGroup.template();

        //[s]modify by xienana for bug 8526 @20170210
        if (ContactUtils.isHasCompany()) {
            if (CommonUtils.isCompanyCodeChanged()) {
                ContactModuleService.deleteDeparmentAndDeptMember();
            }
            new TaskIncrementDepartContact().template();
        } else {
            ContactModuleService.deleteDeparmentAndDeptMember();
        }
        //[e]modify by xienana for bug 8526 @20170210
    }


    /**
     * 输入的账号与当前账号是否存在好友关系
     * @param account
     * @return true : 是好友 false : 不是好友或者好友处于删除状态
     */
    public static boolean isFriendRelated(String account) {
        if (ContactModuleService.isExistFriend(ContactModuleService.getFriend(ActomaController.getApp(), account))) {
            return true;
        }
        return false;
    }

    /**
     * 输入的账号是否存在于当前集团通讯录
     * @param account
     * @return
     */
    public static boolean isExistDepartment(String account) {
        if (ContactModuleService.isExistInDepartment(ContactModuleService.getMember(ActomaController.getApp(), account))) {
            return true;
        }
        return false;
    }


    /**
     * 当用户网络状态变化之后触发--->搜索异常数据表和异常推送表恢复
     *
     * @param context
     */
    public static void recoveryContacts(Context context) {
        LogUtil.getUtils(TAG).i("--------recoveryContacts---context:"+context);
        if(!ContactModuleService.isNetConnect(ActomaController.getApp())){
            LogUtil.getUtils(TAG).i("--------recoveryContacts---context:");
            return;
        }
        ErrorPushService service = new ErrorPushService(context);
        List<ErrorPush> dataSource = service.queryErrorPush();
        for (ErrorPush push : dataSource) {
            String trans = push.getTransId();
            if(ObjectUtil.stringIsEmpty(trans))continue;
            ITask strategy = PushServiceFactory.getInstance().recovery(trans);
            if(!ObjectUtil.objectIsEmpty(strategy)){
                strategy.template();
            }
        }

    }

    /**
     * 密信模块和电话模块调用当前函数
     * <b>具体的数据格式参看ContactDto对象</b>
     * <font color = 'red'>注意这里的账号只是查询联系人不是群组的名称</font>
     * @param account 安通账号
     * note :
     * 当前函数只能针对于密信好友、密话好友通话或者账号查询对应数据信息
     * 如果查询对应的群组信息时(群名称  群头像 等需要调用getGroupInfo)
     * 如果查询对应的->群成员<-信息需要调用getGroupMemberInfo
     * @return
     */
    public static ContactDto getContactInfo(String account) {
        ContactDto dto = new ContactDto();
        List<ContactNameDto> contactNameDtos = new GroupExternalService(ActomaController.getApp()).queryContactDto(null, account);
        if(ObjectUtil.collectionIsEmpty(contactNameDtos)){
            dto.setAvatarUrl("");
            dto.setAccount(account);
            dto.setName(account);
            dto.setNamePY(account);
            dto.setNamePinYin(account);
        }else{
            ContactNameDto contactNameDto = contactNameDtos.get(0);
            if(!ObjectUtil.objectIsEmpty(contactNameDto)){
                dto.setAvatarUrl(contactNameDto.getAvatarUrl());
                dto.setThumbnailUrl(contactNameDto.getThumbnailUrl());
                dto.setName(contactNameDto.getDisplayNameContact());//contacts 没有  groupMemberNickName
                dto.setAccount(account);
                //下面两个函数只有voip 用到但是在voip模块没有发现调用拼音的地方
                dto.setNamePY("");
                dto.setNamePinYin("");
            }else{
                dto.setAccount(account);
                dto.setAvatarUrl("");
                dto.setName(account);
                dto.setNamePY(account);
                dto.setNamePinYin(account);
            }
        }
        return dto;
    }

    /**
     * 密信收到好友添加或者群创建推送根据账号查询对应的联系人数据显示信息接口
     * <b>调用显示名称请使用getDisplayName</b>
     * @see ContactNameDto#getDisplayName()
     * @param account
     * @return
     */
    public static Map<String,ContactNameDto> getContactInfoMap(String... account){
        Map<String,ContactNameDto> contactNameDtoMap = new HashMap<String,ContactNameDto>();
        List<ContactNameDto> contactNameDtos = new GroupExternalService(ActomaController.getApp()).queryContactDto(null,account);
        for(ContactNameDto nameDto : contactNameDtos){
            contactNameDtoMap.put(nameDto.getAccount(),nameDto);
        }
        return contactNameDtoMap;
    }

    /**
     * 供主框架调用
     * @param account
     */
    private static void openSafeTransfer(Context context,String account){
        EncryptRecordService encryptRecordService = new EncryptRecordService(context);
        boolean result = encryptRecordService.selectionFriendUpdateState(account);
        if (result) {
//            StateParams.getStateParams().setEncryptAccount(account);
        }
    }

    /**
     * 供主框架使用
     * @param context
     */
    private static  void closeSafeTransfer(Context context){
        EncryptRecordService encryptRecordService = new EncryptRecordService(context);
        encryptRecordService.closeSafeTransfer();
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_CLOSE_TANSFER);
        context.sendBroadcast(intent);

    }

    /**
     * 调用后去后台同步群组信息
     * 首先判断群组是否被解散，如果没有去查询本地数据库，本地没有则去服务器同步
     *
     * @param groupId 要查询群信息的群id
     */
    public static void getGroupInfoFromServer(String groupId) {
        GroupExternalService externalService = new GroupExternalService(ActomaController.getApp());
        List<GroupMember> groupMembers = externalService.queryGroupMembers(groupId);
        if(!ObjectUtil.objectIsEmpty(externalService.queryDeletedGroupMap().get(groupId))) {
            FireEventUtils.fireGroupInfoGet(groupId, 0);
        } else if (!ObjectUtil.collectionIsEmpty(groupMembers)) {
            FireEventUtils.fireGroupInfoGet(groupId, groupMembers.size());
        } else {
            externalService.getGroupInfoWithServerById(ActomaController.getApp(), groupId);
        }
    }







    /**
     * 开启加密通道
     * @param context
     * @param account
     */
    public static  void safeTransferOpened(Context context,String account,String appShowName){
        openSafeTransfer(context, account);
        setOpenedAccountAppSafeTransterAppPackage(context, appShowName, account);
    }

    /**
     * 关闭加密通道
     * @param context
     */
    public static  void safeTransferClosed(Context context){
        closeSafeTransfer(context);
    }



    /**
     * 判断制定的账号是否在给定的群组中
     *
     * @param account 帐号
     * @param groupId 群组ID
     * @return 账号是否在群组中
     * **
     */
    public static boolean isAccountInGroup(String account, String groupId) {
        GroupExternalService externalService = new GroupExternalService(ActomaController.getApp());
        Group group = externalService.queryByGroupId(groupId);
        if(ObjectUtil.objectIsEmpty(group)){
            /*[S]modify by tangsha for group info update*/
            TaskIncrementGroup task = new TaskIncrementGroup();
            task.template();
            return false;
            /*[E]modify by tangsha for group info update*/
        }else if(GroupConvert.DELETED.equals(group.getIsDeleted())){
            return false;
        }else{
            List<GroupMember> members = externalService.queryGroupMembers(groupId);
            if(!ObjectUtil.collectionIsEmpty(members)){
                for (GroupMember member : members) {
                    if (account.equals(member.getAccount())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 生成联系人Fragment实例
     *
     * @return
     */
    public static Fragment createContactFragment() {
        return new ContactFragment();
    }

    /**
     * <p>
     * 启动选择联系人界面
     * @param context  启动打开界面的上下文
     * @param groupId  群组ID（如果是好友，置为null）
     * @param accounts 当前已存在的联系人账号。如果是二人聊天这里传递的是对方的账号
     * </p>
     */
    public static void startChooseActivity(Context context, String groupId, ArrayList<String> accounts) {
        GroupUtils.launchChooseContactActivity(context, groupId, accounts);
    }

    /**
     * 点击应用主框架传过来appname
     * @param context
     * @param appShowName
     */
    public static void setOpenedAccountAppSafeTransterAppPackage(Context context,String appShowName,String account){
        Intent intent = new Intent();
        intent.setAction("com.contactcommon.give.appName");
        intent.putExtra("appShowName",appShowName);
        intent.putExtra("account", account);
        context.sendBroadcast(intent);
        Intent intent2 = new Intent();//add by lwl 集团联系人  三方加密open
        intent2.setAction(RegisterActionUtil.ACTION_OPEN_TRANSFER);
        context.sendBroadcast(intent2);
    }

    /**
     * <p>
     * 生成群组成员编辑Fragment
     * @param groupId 群组ID <b><font color=red >函数里面不在校验groupId,调用方需验证</font></b>
     * @return fragment ：联系人编辑fragment
     * </p>
     */
    public static Fragment groupInfoDetailManager(String groupId) {
        ContactEditorFragment fragment = new ContactEditorFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ContactEditorFragment.KEY_GROUP_ID, groupId);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 根据账号 开启详情界面
     *
     * @param context
     * @param account
     */
    public static void startContactDetailActivity(Context context, String account) {
        new ContactModuleService().startContactDetailActivity(context, account);
    }

    /**
     * 退出或者解散群组
     * @param context
     * @param groupId
     * @param account
     * @param callback
     */
    public static void quitOrDismissGroup(final Context context, final String groupId, final String account,
                                          final ContactModuleProxy.ContactModuleResult callback){
        GroupProxy.exitFromGroup(context, groupId, account, callback);
    }


    /**
     * 判断当前用户是否是某群组的群主
     * @param context 上下文
     * @param groupId 要判断的群组ID
     * @return true or false
     */
    public static boolean isGroupOwner(Context context, String groupId) {
        String loginUserAccount = ContactUtils.getCurrentAccount();
        Group localGroup = GroupInternalService.getInstance().queryByGroupId(groupId);
        if(!ObjectUtil.objectIsEmpty(localGroup)){
            String ownerAccount = localGroup.getGroupOwner();
            if(!ObjectUtil.stringIsEmpty(ownerAccount) && loginUserAccount.equals(ownerAccount)){
                return true;
            }
        }
        return false;
    }

    /**
     * 查询群组成员信息
     * @param groupId 群组ID
     * @param account 查询的群组成员账号
     * @return 返回ContactDto对象,返回数据不为空
     */
    public static ContactDto getGroupMemberInfo(String groupId, String account){
        ContactDto contactDto = new ContactDto();
        List<ContactNameDto> contactNameDtos = new GroupExternalService(ActomaController.getApp()).queryContactDto(groupId, account);
        if(!ObjectUtil.collectionIsEmpty(contactNameDtos)){
            ContactNameDto contactNameDto = contactNameDtos.get(0);
            contactDto.setName(contactNameDto.getDisplayName());
            contactDto.setAvatarUrl(contactNameDto.getAvatarUrl());
            contactDto.setThumbnailUrl(contactNameDto.getThumbnailUrl());
            contactDto.setAccount(contactNameDto.getAccount());
        }else{
            contactDto.setAccount(account);
            contactDto.setName(account);
            contactDto.setAvatarUrl("");
            contactDto.setThumbnailUrl("");
            //Start:add by wal@xdja.com for update group data
            if (!ObjectUtil.stringIsEmpty(groupId)){
                new TaskIncrementGroup().template();
            }
            //End:add by wal@xdja.com for update group data
        }
        return contactDto;
    }

    /**
     * 查询群组信息
     * @param groupId 群组ID
     * @return 如没有查到，返回null；查询到返回ContactDto对象
     */
    public static ContactDto getGroupInfo(String groupId) {
        ContactDto contactDto = new ContactDto();
        Group groupInfo = GroupInternalService.getInstance().queryByGroupId(groupId);
        if(!ObjectUtil.objectIsEmpty(groupInfo)){
            String groupName = groupInfo.getGroupName();
            if(!ObjectUtil.stringIsEmpty(groupName)){
                contactDto.setName(groupName);
            }
            contactDto.setAvatarUrl(groupInfo.getThumbnail());
        }
        return contactDto;
    }

    public static List<String> getAllAvatarUrls(Context context) {
        AvatarDao dao = new AvatarDao();
        return dao.queryUrls();
    }

    public static String COMPANY_RESULT_CODE_TAG = "contactCompanyResultCode";
    public static String COMPANY_RESULT_INFO_TAG = "contactCompanyResultInfo";
    public static int COMPANY_CODE_FAIL = -1;
    public static int COMPANY_CODE_SUCCESS = 0;
    public static HashMap<String,Object> getCompanyCodeFromServer(String digitalAccount){
        HashMap<String,Object> resultMap = new HashMap<>();
        int companyResultCode = COMPANY_CODE_FAIL;
        HttpsRequstResult result = null;
        String companyCode = "";
        try {
            result = DepartmentHttpServiceHelper.getComapnyCode(digitalAccount);
        } catch (DepartmentException e) {
            e.printStackTrace();
            result = null;
        }finally {
            if(!ObjectUtil.objectIsEmpty(result) && result.result == HttpResultSate.SUCCESS){
                String resultBody = result.body;
                JSONObject s = JSON.parseObject(resultBody);
                companyCode = s.getString("companyCode");
                resultMap.put(COMPANY_RESULT_INFO_TAG,companyCode);
                companyResultCode = COMPANY_CODE_SUCCESS;
                LogUtil.getUtils().d("0328 getCompanyCodeFromServer companyCode "+companyCode);
            }else {
                LogUtil.getUtils().d("0328 getCompanyCodeFromServer fail-----");
                companyResultCode = COMPANY_CODE_FAIL;
            }
            resultMap.put(COMPANY_RESULT_CODE_TAG,companyResultCode);
        }
        return resultMap;
    }

    public static boolean compareCompanyCodeServerToLocal(String digitalAccount){
        boolean isSame = false;
        HashMap<String,Object> companyInfo = ContactModuleProxy.getCompanyCodeFromServer(digitalAccount);
        if(companyInfo != null && companyInfo.get(ContactModuleProxy.COMPANY_RESULT_CODE_TAG) != null){
            int companyResult = (int)companyInfo.get(ContactModuleProxy.COMPANY_RESULT_CODE_TAG);
            if(companyResult == ContactModuleProxy.COMPANY_CODE_SUCCESS){
                String companyCode = (String)companyInfo.get(ContactModuleProxy.COMPANY_RESULT_INFO_TAG);
                String preCompanyCode = CommonUtils.getCommpanyCode(digitalAccount);
                LogUtil.getUtils().d("0328 compareCompanyCodeServerToLocal companyCode "+companyCode+" preCompanyCode "+preCompanyCode
                +" thread "+Thread.currentThread().getName());
                boolean noCompany = TextUtils.isEmpty(companyCode) && TextUtils.isEmpty(preCompanyCode);
                boolean sameCompany = (companyCode != null && preCompanyCode != null && companyCode.compareTo(preCompanyCode) == 0);
                if(noCompany || sameCompany){
                    isSame = true;
                }
            }
        }
        LogUtil.getUtils().e("0328 compareCompanyCodeServerToLocal isSame "+isSame);
        return isSame;
    }
}
