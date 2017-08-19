package com.xdja.contact.presenter.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.TextUtil;
import com.xdja.contact.util.EncryptManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.PermissionUtil;
import com.xdja.comm.uitl.StateParams;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.EncryptRecord;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.CommonDetailDto;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.http.response.group.ResponseCkmsOpSign;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.params.account.PullAccountInfoParam;
import com.xdja.contact.http.wrap.params.account.QueryAccountInfoParam;
import com.xdja.contact.presenter.command.ICommonDetailCommand;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.service.DepartService;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.MemberService;
import com.xdja.contact.task.account.TaskPullAccountInfo;
import com.xdja.contact.ui.def.ICommonDetailVu;
import com.xdja.contact.ui.view.CommonDetailVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.frame.presenter.mvp.annotation.StackInto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2015/10/23.
 */
@StackInto
public class CommonDetailPresenter extends ActivityPresenter<ICommonDetailCommand, ICommonDetailVu> implements
        ICommonDetailCommand, IContactEvent {
    private static final String TAG = "anTongCkms  CommonDetailPresenter";

    /*start:add by wal@xdja.com for ckms 2016/8/3 */
    private String currentAccount;
    public static final int DELETE_FRIEND_GROUP = 2;
   /*end:add by wal@xdja.com for ckms 2016/8/3 */

    private CommonDetailDto detailDto;

    private ActomaAccount actomaAccount;

    //是否是首次进入  用于判断在删除好友 或者修改备注后更新界面
    private boolean isFirst = false;


    String phone;

    TaskPullAccountInfo taskPullAccountInfo;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        currentAccount=ContactUtils.getCurrentAccount();//add by wal@xdja.com for ckms 2016/8/3
        this.isFirst = true;
        FireEventUtils.addGroupListener(this);
        registerBroadReceiver();
        String data_type = getIntent().getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE);
        LogUtil.getUtils().d(TAG+" onBindView data_type "+data_type);
        if (RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT.equals(data_type)) {
            //传递账号过来
            String data = getIntent().getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY);
            LogUtil.getUtils().i("---------->帐号:" + data);
            getAccountInfo(data, "");
            initLocalData(data, "");
        } else if (RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID.equals(data_type)) {
            //传递集团工号
            String data = getIntent().getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY);
            LogUtil.getUtils().i("---------->工号:" + data);
            getAccountInfo("", data);
            initLocalData("", data);
        } else if (RegisterActionUtil.EXTRA_KEY_DATA_TYPE_SERVER_SEARCH.equals(data_type)) {
            //添加好友搜索
            detailDto = getIntent().getParcelableExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY);
            getVu().setDetailData(detailDto);
        } else if (RegisterActionUtil.EXTRA_KEY_DATA_TYPE_SCAN_SEARH.equals(data_type)) {
            // 扫描二维码
            String keyword = getIntent().getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY);
            searchFromWeb(keyword);
        } else if (RegisterActionUtil.EXTRA_KEY_DATA_TYPE_GROUP_MEMBER.equals(data_type)) {
            //群组普通成员 既不是好友也不是集团人员
            //2015-10-28 刘晓瑞说群组进来的时候出现点击添加好友不执行动作
            detailDto = getIntent().getParcelableExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY);
            actomaAccount = detailDto.getActomaAccount();
            detailDto.setAccount(actomaAccount.getAccount());
            responseActomaAccount(actomaAccount.getAccount());
        }
    }

    private void getAccountInfo(String account, String workId){
        //Start:add by wal@xdja.com for 3376
        if(TextUtils.isEmpty(account) == false) {
            ActomaAccount actomaAccount = new ActomAccountService().queryByAccount(account);
            if (actomaAccount != null) {
                taskPullAccountInfo = new TaskPullAccountInfo(actomaAccount.getAccount(), actomaAccount.getIdentify());
                taskPullAccountInfo.template();
            } else {
                taskPullAccountInfo = new TaskPullAccountInfo(account, "1");
                taskPullAccountInfo.template();
            }
        }else if(TextUtils.isEmpty(workId) == false) {
            Member member = new MemberService().getMemberById(workId);
            if (ObjectUtil.objectIsEmpty(member) == false) {
                ActomaAccount actomaAccount1 = member.getActomaAccount();
                //[s]modify by xienana for bug 5239 @20161025 [review by wangalei]
                if (actomaAccount1 != null) {
                    taskPullAccountInfo = new TaskPullAccountInfo(workId, actomaAccount1.getAccount(), actomaAccount1.getIdentify());
                    taskPullAccountInfo.template();
                } else if (ObjectUtil.stringIsEmpty(member.getAccount()) == false) {
                    taskPullAccountInfo = new TaskPullAccountInfo(workId, member.getAccount(), "1");
                    taskPullAccountInfo.template();
                }//[e]modify by xienana for bug 5239 @20161025 [review by wangalei]
                else{
                    LogUtil.getUtils().e(TAG+"getAccountInfo member workId "+workId+" account is null!!!");
                }
            }else{
                LogUtil.getUtils().e(TAG+"getAccountInfo member is empty!!!");
            }
        }
        //End:add by wal@xdja.com for 3376
    }

    private void responseActomaAccount(String accountParam) {
        if(!ContactModuleService.checkNetWork()){
            finish();
        }else {
            getVu().loadingDialogController(true, getResources().getString(R.string.loading));
            //add by wal@xdja.com for 1480
//            new HttpRequestWrap().request(new QueryAccountInfoParam(new IModuleHttpCallBack() {
            new HttpRequestWrap().request(new PullAccountInfoParam(new IModuleHttpCallBack() {
                @Override
                public void onFail(HttpErrorBean httpErrorBean) {
                    searchAccountError(httpErrorBean);
                }

                @Override
                public void onSuccess(String body) {
                    getVu().loadingDialogController(false, "");
                    if (ObjectUtil.stringIsEmpty(body)) {
                        return;
                    }
                    try {
                        ResponseActomaAccount responseActomaAccount = JSON.parseObject(body, ResponseActomaAccount.class);
                        if (!ObjectUtil.objectIsEmpty(responseActomaAccount)) {
                            //start:add by wal@xdja.com for 2705
                            Avatar avatar = responseActomaAccount.getAvatarBean();
                            ActomAccountService actomAccountService = new ActomAccountService();
                            actomAccountService.saveOrUpdate(responseActomaAccount);
                            AvaterService avaterService = new AvaterService();
                            avaterService.saveOrUpdate(avatar);
                            //end:add by wal@xdja.com for 2705
                            detailDto.setServerActomaAccount(responseActomaAccount);
                            //start:add by wal@xdja.com for 3907
                            detailDto.setIsExist(false);
                            List<String> accounts = new ArrayList<String>();
                            accounts.add(responseActomaAccount.getAccount());
                            FireEventUtils.pushFriendUpdateNickName(accounts);
                            //end:add by wal@xdja.com for 3907
                            getVu().setDetailData(detailDto);
                        } else {
                            XToast.show(CommonDetailPresenter.this, R.string.server_is_busy);
                        }
                    }catch (Exception e){
                        XToast.show(ActomaController.getApp(), R.string.server_is_busy);
                        LogUtil.getUtils().e("Actoma contact CommonDetailPresenter responseActomaAccount:群组成员跳转过来加载账户信息，解析json出错");
                    }
                }

                @Override
                public void onErr() {
                    getVu().loadingDialogController(false, "");
                    finish();//add by lwl 3282
                }
            },accountParam , "1"));   //add by wal@xdja.com for 1480
        }
    }

    private void searchAccountError(HttpErrorBean httpErrorBean){
        getVu().loadingDialogController(false, "");
        String errorCode = httpErrorBean.getErrCode();
        //alh@xdja.com<mailto://alh@xdja.com> 2016-11-16 add. fix bug 6043 . review by wangchao1. Start
        if(ServiceErrorCode.EXCEPTION_NO_USER_FOUND.getCode().equals(errorCode)){ // 没有搜索到账户信息 modify by lwl
            XToast.show(CommonDetailPresenter.this, R.string.get_contact_info_error);
        }else if(ServiceErrorCode.REQUEST_PARAMS_NOT_VALID.getCode().equals(errorCode)
                || ServiceErrorCode.REQUEST_PARAMS_ERROR.getCode().equals(errorCode)){
            XToast.show(getApplication(), R.string.version_too_low);
        } else if(ServiceErrorCode.INTERNAL_SERVER_ERROR.getCode().equals(errorCode)
                || ServiceErrorCode.EXCEPTION_HANDLE_ERROR.getCode().equals(errorCode)){
            XToast.show(getApplication(), R.string.server_is_busy);
        }else{
            XToast.show(CommonDetailPresenter.this, R.string.get_contact_info_error);
            LogUtil.getUtils().e("Actoma contact CommonDetailPresenter searchAccountError:非集团好友人员加载用户信息出错，errorCode:"+errorCode+"");
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-11-16 add. fix bug 6043 . review by wangchao1. End
        finish();
    }

    @Override
    public boolean isFirstComeIn() {
        return isFirst;
    }

    @Override
    public String getAccount() {
        return ObjectUtil.objectIsEmpty(detailDto) ? "" : detailDto.getAccount();
    }

    public synchronized void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }



    private void initLocalData(String accountParam, String workId) {
        detailDto = new CommonDetailDto();
        if (!ObjectUtil.stringIsEmpty(accountParam)) {
            Friend friend = new FriendService().queryFriendByAccountNonDeleted(accountParam);
            Avatar avatar = new AvaterService().queryByAccount(accountParam);
            //start:add for 1212 by wal@xdja.com
            ActomaAccount actomaAccount = new ActomAccountService().queryByAccount(accountParam);
            if (ObjectUtil.objectIsEmpty(actomaAccount)) {
                actomaAccount=new ActomaAccount();
                actomaAccount.setAccount(accountParam);
            }
            //end:add for 1212 by wal@xdja.com
            Member member = new MemberService().getMemberByAccount(accountParam);
            if (!ObjectUtil.objectIsEmpty(friend) && friend.isShow()) {
                detailDto.setIsFriend(true);
                detailDto.setFriend(friend);
            } else {
                detailDto.setIsFriend(false);
            }
            if (!ObjectUtil.objectIsEmpty(member)) {
                Department department = new DepartService(this).getDepartmentById(member.getDepartId());
                detailDto.setDepartment(department);
                detailDto.setMember(member);
                detailDto.setIsMember(true);
            } else {
                detailDto.setIsMember(false);
            }
            detailDto.setAlias(actomaAccount.getAlias());
            detailDto.setAccount(accountParam);
            detailDto.setIsExist(true);
            detailDto.setAvatar(avatar);
            detailDto.setActomaAccount(actomaAccount);
        }
        if (!ObjectUtil.stringIsEmpty(workId)) {
            Member member = new MemberService().getMemberById(workId);
            if(ObjectUtil.objectIsEmpty(member)){
                LogUtil.getUtils().e(TAG+" initLocalData member is null-----");
                finish();
            }else if(ObjectUtil.stringIsEmpty(member.getAccount()) || ObjectUtil.stringIsEmpty(member.getDepartId())){
                LogUtil.getUtils().i("----根据集团工作id 为查询到对应的集团人员信息---------");
                Department department = new DepartService(this).getDepartmentById(member.getDepartId());
                detailDto.setIsExist(true);
                detailDto.setIsFriend(false);
                detailDto.setIsMember(true);
                detailDto.setMember(member);
                detailDto.setDepartment(department);
            }else{
                String memberAccount = member.getAccount();
                Department department = new DepartService(this).getDepartmentById(member.getDepartId());
                Friend friend = new FriendService().queryFriendByAccountNonDeleted(memberAccount);
                Avatar avatar = new AvaterService().queryByAccount(memberAccount);
                //start:add for 1212 by wal@xdja.com
                ActomaAccount actomaAccount = new ActomAccountService().queryByAccount(memberAccount);
                if (ObjectUtil.objectIsEmpty(actomaAccount)) {
                    actomaAccount=new ActomaAccount();
                    actomaAccount.setAccount(memberAccount);
                }
                //end:add for 1212 by wal@xdja.com
                if (!ObjectUtil.objectIsEmpty(friend)) {
                    detailDto.setFriend(friend);
                    detailDto.setIsFriend(true);
                } else {
                    detailDto.setIsFriend(false);
                }
                detailDto.setAlias(actomaAccount.getAlias());
                detailDto.setAccount(memberAccount);
                detailDto.setIsExist(true);
                detailDto.setIsMember(true);
                detailDto.setActomaAccount(actomaAccount);
                detailDto.setAvatar(avatar);
                detailDto.setMember(member);
                detailDto.setDepartment(department);
            }
        }
        getVu().setDetailData(detailDto);
    }

    @Override
    protected Class<? extends ICommonDetailVu> getVuClass() {
        return CommonDetailVu.class;
    }

    @Override
    protected ICommonDetailCommand getCommand() {
        return this;
    }


    //陌生人发出好友请求
    @Override
    public void startRequestInfo() {
        if (!ObjectUtil.objectIsEmpty(detailDto)) {
            ResponseActomaAccount actomaAccount = detailDto.getServerActomaAccount();
            if(actomaAccount!=null && "0".equals(actomaAccount.getStatus())){//add by lwl 3621
                XToast.show(ActomaController.getApp(), R.string.version_low);
                return;
            }
            Intent intent = new Intent(this, FriendRequestInfoPresenter.class);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_TAG_ACCOUNT_DATA, actomaAccount);
            startActivity(intent);
        } else {
            LogUtil.getUtils().i("二维码搜索添加好友请求出现异常");
        }
    }

    @Override
    public void sendEncryptionMessage() {
        String  friendAccount = detailDto.getAccount();
        if (ObjectUtil.stringIsEmpty(friendAccount) || ObjectUtil.stringIsEmpty(currentAccount)) return;
        LogUtil.getUtils().i("调用密信---帐号:" + detailDto.getAccount());
        /*start:add by wal@xdja.com for ckms 2016/8/3 */
        ContactUtils.startFriendTalk(CommonDetailPresenter.this, friendAccount);
        CommonDetailPresenter.this.finish();
        /*end:add by wal@xdja.com for ckms 2016/8/3 */
    }
    /*start:add by wal@xdja.com for ckms 2016/8/3 */
    private final class CkmsGroupHttpCallback implements IModuleHttpCallBack {

        private int taskType;
        private String groupId;

        private CkmsGroupHttpCallback(int type,String groupId) {
            this.taskType = type;
            this.groupId = groupId;
        }

        @Override
        public void onFail(HttpErrorBean httpErrorBean) {
            if (!ObjectUtil.objectIsEmpty(httpErrorBean)) {
                LogUtil.getUtils().e(TAG+ "Ckms create group httpErrorBean.getMessage:" + httpErrorBean.getMessage());
            } else {
                LogUtil.getUtils().e(TAG+ "Ckms create group exception");
            }
        }

        @Override
        public void onSuccess(String s) {
            ResponseCkmsOpSign response = JSON.parseObject(s, ResponseCkmsOpSign.class);
            String opSign = response.getSignedOpCode();
            LogUtil.getUtils().d(TAG+"CkmsGroupHttpCallback onSuccess opSign " + opSign);
            if (DELETE_FRIEND_GROUP==taskType){
                CkmsGpEnDecryptManager.destroySGroup(currentAccount, groupId, opSign);
            }

        }

        @Override
        public void onErr() {

        }
    }
    /*end:add by wal@xdja.com for ckms 2016/8/3 */

    @Override
    public void callWithEncryption() {
        if (ObjectUtil.stringIsEmpty(detailDto.getAccount())) return;
        LogUtil.getUtils().i("调用密话---帐号:"+detailDto.getAccount());
        ContactUtils.startVoip(this, detailDto.getAccount());
    }

    //出现添加好友的集团界面
    //Note: 这里需要和后台协调一下集团人员添加好友的时候 没有kuep需要这里获取一下
    @Override
    public void toolBarAddFriend() {
        if (!ContactModuleService.checkNetWork()) return;
        if (ObjectUtil.objectIsEmpty(detailDto)) {
            XToast.showErrorTop(this, this.getString(R.string.contact_no_account));//modify by wal@xdja.com for string 没有对应的帐号
            return;
        }
        ActomaAccount actomaAccount = detailDto.getActomaAccount();
        if (ObjectUtil.objectIsEmpty(actomaAccount)) {
            XToast.showErrorTop(this, this.getString(R.string.contact_account_excepte));//modify by wal@xdja.com for string 帐户信息异常
            return;
        }
        String account = actomaAccount.getAccount();
        getVu().loadingDialogController(true, this.getString(R.string.contact_attaching_account_info));//modify by wal@xdja.com for string 正在获取联系人信息
        //网络搜索好友
        //add by lwl start
        String identify="1";
        new HttpRequestWrap().request(new PullAccountInfoParam(new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean httpErrorBean) {
                searchAccountError(httpErrorBean);
            }

            @Override
            public void onSuccess(String s) {
                getVu().loadingDialogController(false, "");
                if (ObjectUtil.stringIsEmpty(s)) {
                    return;
                } else {
                    try {
                        ResponseActomaAccount responseActomaAccount = JSON.parseObject(s, ResponseActomaAccount.class);
                        if (responseActomaAccount != null) {
                            Intent intent = new Intent(ActomaController.getApp(), FriendRequestInfoPresenter.class);
                            intent.putExtra(RegisterActionUtil.EXTRA_KEY_TAG_ACCOUNT_DATA, responseActomaAccount);
                            startActivity(intent);
                        } else {
                            XToast.show(ActomaController.getApp(), R.string.server_is_busy);
                        }
                    } catch (Exception e) {
                        XToast.show(ActomaController.getApp(), R.string.server_is_busy);
                        LogUtil.getUtils().e("Actoma contact CommonDetailPresenter toolBarAddFriend:集团人员执行添加好友操作，解析json出错");
                    }
                }
            }

            @Override
            public void onErr() {
                getVu().loadingDialogController(false, "");
            }
        }, account,identify));
    }

    //扫一扫
    @Override
    public void searchFromWeb(final String keyword) {
        if (!ContactModuleService.checkNetWork()) {
            finish();
        }else{
            if (ObjectUtil.stringIsEmpty(keyword)) return;
            getVu().loadingDialogController(true, getResources().getString(R.string.loading));
            new HttpRequestWrap().request(new QueryAccountInfoParam(new IModuleHttpCallBack() {
                @Override
                public void onFail(HttpErrorBean httpErrorBean) {
                    searchAccountError(httpErrorBean);
                }

                @Override
                public void onSuccess(String body) {
                    getVu().loadingDialogController(false, null);
                    if (ObjectUtil.stringIsEmpty(body)) return;
                    try {
                        ResponseActomaAccount responseActomaAccount = JSON.parseObject(body, ResponseActomaAccount.class);
                        detailDto = new CommonDetailDto();
                        detailDto.setIsExist(false);
                        detailDto.setServerActomaAccount(responseActomaAccount);


                        //modify by lwl start
                        FriendService service = new FriendService();
                        Friend friend = service.queryFriendByAccountNonDeleted(responseActomaAccount.getAccount());
                        if(ObjectUtil.objectIsEmpty(friend)){
                            getVu().setDetailData(detailDto);
                        }else {
                            initLocalData(responseActomaAccount.getAccount(),"");
                        }
                        //modify by lwl end

                    } catch (Exception e) {
                        XToast.show(ActomaController.getApp(), R.string.server_is_busy);
                        LogUtil.getUtils().e("Actoma contact CommonDetailPresenter searchFromWeb:扫一扫添加好友解析json出错");
                    }
                }

                @Override
                public void onErr() {
                    getVu().loadingDialogController(false, "");
                    finish();//add by lwl 3282
                }
            }, keyword));
        }
    }


    @Override
    public void editRemark() {
        Friend friend = detailDto.getFriend();
        Intent intent = new Intent(this, FriendRemarkPresenter.class);
//        intent.putExtra(FriendRemarkPresenter.KEY_INTENT_FRIEND, friend);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_INTENT_FRIEND, friend);
        startActivity(intent);
    }

    @Override
    public void deleteFriend() {
        final Friend friend = detailDto.getFriend();
        if (!ContactModuleService.checkNetWork()) return;
        getVu().loadingDialogController(true, getString(R.string.delete_friend_tips));
        try {
            FriendHttpServiceHelper.deleteFriend(new IModuleHttpCallBack() {
                @Override
                public void onFail(HttpErrorBean httpErrorBean) {
                    getVu().loadingDialogController(false, "");
                    String errorCode = httpErrorBean.getErrCode();
                    if (!ObjectUtil.stringIsEmpty(errorCode)) {
                        if (errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_NOT_VALID.getCode())
                                || errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_ERROR.getCode())) {
                            XToast.show(getApplication(), R.string.version_too_low);
                            return;
                        } else if (errorCode.equals(ServiceErrorCode.INTERNAL_SERVER_ERROR.getCode())
                                || errorCode.equals(ServiceErrorCode.EXCEPTION_HANDLE_ERROR.getCode())) {
                            XToast.show(getApplication(), R.string.server_is_busy);
                            return;
                        } else {
                            XToast.show(getApplication(), getString(R.string.request_info_error));
                            LogUtil.getUtils().e("Actoma contact CommonDetailPresenter deleteFriend:删除好友出错，errorCode:" + errorCode + "");
                            return;
                        }
                    } else {
                        XToast.show(getApplication(), getString(R.string.delete_friend_error));
                        LogUtil.getUtils().e("Actoma contact CommonDetailPresenter deleteFriend:删除好友出错，errorCode is null");
                        return;
                    }
                }

                @Override
                public void onSuccess(String body) {
                    getVu().loadingDialogController(false, "");
                    boolean result = new FriendService().delete(friend);
                    if (result) {
                        closeTransferService(friend);
                        FireEventUtils.pushFriendClickedDeleteButton(friend.getAccount());
                        //杨鹏修改第三方加密
                        //[s]modify by xnn for bug 9932
                        List<String> deleteFriendList = new ArrayList<String>();
                        deleteFriendList.add(friend.getAccount());
                        EncryptManager.closeEncryptionChannel(deleteFriendList);
                        //[e]modify by xnn for bug 9932
                        /*start:add by wal@xdja.com for ckms add group child 2016/08/02*/
                        //开会确认删除好友不要销毁CMKS群组
//                        if( CkmsGpEnDecryptManager.getCkmsIsOpen()){
//                            ArrayList accountList = new ArrayList<>();
//                            accountList.add(currentAccount);
//                            accountList.add(friend.getAccount());
//                            String ckmsGroupId = CkmsGpEnDecryptManager.getGroupIdWithFriend(currentAccount, friend.getAccount());
//                            GroupHttpServiceHelper.getCkmsGroupOpSign(new CkmsGroupHttpCallback(DELETE_FRIEND_GROUP,ckmsGroupId),ckmsGroupId,accountList, CkmsGpEnDecryptManager.DESTROY_GROUP);
//                        }
                        /*end:add by wal@xdja.com for ckms add group child 2016/08/02*/
                        finish();
                    } else {
                        XToast.show(ActomaController.getApp(), getString(R.string.delete_friend_error));
                    }
                }

                @Override
                public void onErr() {
                    getVu().loadingDialogController(false, "");
                }
            }, friend.getAccount());
        } catch (FriendHttpException e) {
            getVu().loadingDialogController(false, "");
            LogUtil.getUtils().e("CommonDetailPresenter deleteFriend exception:"+e.getMessage());
        }
    }

    /**
     * 关闭加密通道
     *
     * @param friend
     */
    private void closeTransferService(Friend friend) {
        if (StateParams.getStateParams().isSeverOpen()) {
            EncryptRecordService recordService = new EncryptRecordService(this);
            EncryptRecord selectedRecord = recordService.lastSelectedRecord();
            if (!ObjectUtil.objectIsEmpty(selectedRecord)) {
                String account = friend.getAccount();
                String recordAccount = selectedRecord.getAccount();
                if (account.equals(recordAccount)) {
                    MemberService memberService = new MemberService();
                    Member member = memberService.getMemberByAccount(account);
                    if (ObjectUtil.objectIsEmpty(member)) {
                        boolean bool = recordService.closeSafeTransfer();
                        if (bool) {
                            Intent intent = new Intent();
                            intent.setAction(RegisterActionUtil.ACTION_DELETE_FRIEND_CLOSE_TRANSFER);
                            sendBroadcast(intent);
                        }
                    }
                }
            }
        }
    }



    @Override
    public void callPhone(String phone) {
        this.phone=phone;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        int i;
        if (Build.VERSION.SDK_INT < 23) {
            i = PermissionUtil.ALL_PERMISSION_OBTAINED;
        } else {
            i = PermissionUtil.requestPermissions(this, PermissionUtil.DAIL_PERMISSION_REQUEST_CODE, Manifest.permission.CALL_PHONE);
        }
        LogUtil.getUtils().d(TAG+" callPhone i "+i);
        switch (i){
               case PermissionUtil.ALL_PERMISSION_OBTAINED:
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length <= 0){
            return;
        }
        if(requestCode == PermissionUtil.DAIL_PERMISSION_REQUEST_CODE){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(intent);
            }else{
                final CustomDialog customDialog = new CustomDialog(this);
                customDialog.setTitle(getString(R.string.none_phone_permission))
                        .setMessage(TextUtil.getActomaText(this, TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                                0, 0, 0, getString(R.string.none_phone_permission_hint)))
                        .setNegativeButton(getString(R.string.content_yes)
                                , new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customDialog.dismiss();
                                    }
                                }).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getVu().loadingDialogController(false, "");
        FireEventUtils.removeGroupListener(this);
        unregisterReceiver(receiver);
        if(taskPullAccountInfo!=null)
            taskPullAccountInfo.cancel(true);//add by lwl  3410
        getVu().onDestroy();
    }


    @Override
    public void onEvent(int event, Object param1, Object param2, Object param3) {
        if(event == IContactEvent.EVENT_FRIEND_UPDATE_REMARK){
            String friendAccount = (String)param1;
            setIsFirst(false);
            initLocalData(friendAccount, "");
        }
    }

    private void registerBroadReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS);
        registerReceiver(receiver, intentFilter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS)){
                setIsFirst(false);
                //start:modify for update actoma acount by wal@xdja.com
                String data_type=intent.getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE);
                String data=intent.getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY);//fix 1212 by wal@xdja.com
                if (RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT.equals(data_type)) {
                    initLocalData(data, "");
                } else if (RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID.equals(data_type)) {
                    initLocalData("", data);
                }
                //end:modify for update actoma acount by wal@xdja.com
            }
        }
    };


}
