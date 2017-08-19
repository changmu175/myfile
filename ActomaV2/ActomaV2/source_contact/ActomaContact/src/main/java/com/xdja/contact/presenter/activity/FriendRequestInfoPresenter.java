package com.xdja.contact.presenter.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.task.friend.TaskAcceptFriendReq;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.presenter.command.IFriendRequestInfoCommand;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.FriendRequestInfoService;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.ui.def.IFriendRequestInfoVu;
import com.xdja.contact.ui.view.FriendRequestInfoVu;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.NotificationUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.mvp.annotation.StackInto;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送好友申请
 */
@StackInto
public class FriendRequestInfoPresenter extends ActivityPresenter<IFriendRequestInfoCommand, IFriendRequestInfoVu> implements IFriendRequestInfoCommand {

    private ResponseActomaAccount actomaAccount;
    private final String TAG = FriendRequestInfoPresenter.class.getSimpleName();
    private String ckmsGroupId = "";
    private String currentAccount = ContactUtils.getCurrentAccount();
    private String accountRequested = null; //被请求好友的账号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onBindView(Bundle savedInstanceState)  {
        super.onBindView(savedInstanceState);
//        ActivityContoller.getInstanse().addActivity(true, this);
    }
    @Override
    protected Class<? extends IFriendRequestInfoVu> getVuClass() {
        return FriendRequestInfoVu.class;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        actomaAccount = getIntent().getParcelableExtra(RegisterActionUtil.EXTRA_KEY_TAG_ACCOUNT_DATA);
        if (actomaAccount != null) {
            accountRequested = actomaAccount.getAccount();//add by lwl
        }
    }

    @Override
    protected IFriendRequestInfoCommand getCommand() {
        return this;
    }

    @Override
    public void executeSendRequestInfo() {
        try {
            sendAddFriendRequest();
        } catch (FriendHttpException e) {
           LogUtil.getUtils().e("FriendRequestInfoPresenter executeSendRequestInfo exception:"+e.getMessage());
        }
    }

    /**
     * 发送添加好友请求
     */
    private void sendAddFriendRequest() throws FriendHttpException {
        final FriendRequestHistory history = queryHistoryRequest();
        if (!ObjectUtil.objectIsEmpty(history)) {
            //start:add by wal@xdja.com for ckms
            TaskAcceptFriendReq.IAcceptFriendCallback acceptFriendCallback = new TaskAcceptFriendReq.IAcceptFriendCallback() {
                @Override
                public void onPreExecute() {
                }

                @Override
                public void onPostExecute() {
                    getVu().dismissLoading();
                }
            };
            IModuleHttpCallBack moduleHttpCallBack = new IModuleHttpCallBack() {
                @Override
                public void onFail(HttpErrorBean httpErrorBean) {
                    getVu().dismissLoading();
                    if (!ObjectUtil.objectIsEmpty(httpErrorBean)) {
                        LogUtil.getUtils().e("接受好友出现异常httpErrorBean.getMessage:" + httpErrorBean.getMessage());
                        if (ServiceErrorCode.EXCEPTION_ALREADY_FRIEND.getCode().equals(httpErrorBean.getErrCode())) {//modify by lwl
                            XToast.show(FriendRequestInfoPresenter.this, getString(R.string.already_is_friend));
                            FriendRequestInfoPresenter.this.finish();
                            return;
                        } else if (ServiceErrorCode.FRIENDS_LIMIT_ERROR.getCode().equals(httpErrorBean.getErrCode())) {
                            XToast.show(FriendRequestInfoPresenter.this, ActomaController.getApp().getString(R.string.friend_friends_max_limit));//modify by xienana for multi language change @20161205
                            FriendRequestInfoPresenter.this.finish();
                            return;
                        }
                        XToast.show(FriendRequestInfoPresenter.this, getString(R.string.request_info_error));
                    } else {
                        LogUtil.getUtils().e("接受好友出现异常");
                        XToast.show(FriendRequestInfoPresenter.this, getString(R.string.request_info_error));
                    }
                }

                @Override
                public void onSuccess(String body) {
                    FriendRequestService friendRequestService = new FriendRequestService();
                    history.setRequestState(FriendHistoryState.ALREADY_FRIEND);
                    history.setIsRead(FriendRequestHistory.READED);
                    boolean updateHistory = friendRequestService.update(history);
                    if (updateHistory) {
                        FriendService friendService = new FriendService();
                        Friend friend = new Friend.Builder().buildNormalFriend(accountRequested);
                        int count = friendService.countFriends();
                        if (count > 0) {
                            friend.setUpdateSerial("1");
                        }
                        boolean result = friendService.saveOrUpdate(friend);
                        if (result) {
                            //start:add by wal@xdja.com for 好友事件通知
                            List<String> friendAcountList = new ArrayList<String>();
                            friendAcountList.add(accountRequested);
                            List<String> showNameList = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("", friendAcountList);
                            if (showNameList.size() > 0) {
                                FireEventUtils.pushFriendClickedAcceptButton(accountRequested, showNameList.get(0));
                            } else {
                                FireEventUtils.pushFriendClickedAcceptButton(accountRequested, accountRequested);
                            }
                            //end:add by wal@xdja.com for 好友事件通知
                            BroadcastManager.refreshFriendRequestList();
                            BroadcastManager.refreshFriendList();
                            NotificationUtil.cancelNotification();
//                            ActivityContoller.getInstanse().finishSearchFlowActivity();
//                            finish();
                            ActivityStack.getInstanse().popActivityByClass(CommonDetailPresenter.class, true);
                            ActivityStack.getInstanse().popActivityByClass(ChooseContactPresenter.class, true);
                            ActivityStack.getInstanse().popActivityByClass(FriendSearchPresenter.class, true);
                            ActivityStack.getInstanse().popActivityByClass(FriendRequestInfoPresenter.class, true);
                            Intent intent = new Intent(FriendRequestInfoPresenter.this, FriendRequestHistoryPresenter.class);
                            startActivity(intent);

                        }
                    } else {
                        LogUtil.getUtils().i("接受好友更新历史数据失败");
                        XToast.show(FriendRequestInfoPresenter.this, getString(R.string.request_info_error));
                    }
                    getVu().dismissLoading();
                }

                @Override
                public void onErr() {
                    getVu().dismissLoading();
                }
            };

            TaskAcceptFriendReq acceptFriendReqTask = new TaskAcceptFriendReq(this, acceptFriendCallback, moduleHttpCallBack);
            acceptFriendReqTask.execute(accountRequested);
            return;
        }//add by lwl

        final String verifyInfo = getVu().getVerificationInfo().trim();
        FriendHttpServiceHelper.addFriend(new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean httpErrorBean) {
                getVu().dismissLoading();
                String errorCode = httpErrorBean.getErrCode();
                if (!ObjectUtil.stringIsEmpty(errorCode)) {
                    //if (errorCode.equals("already_friend")) {modify by lwl
                    if (ServiceErrorCode.EXCEPTION_ALREADY_FRIEND.getCode().equals(errorCode)) {
                        XToast.show(getApplication(), R.string.already_is_friend);
                        return;
                        // } else if(errorCode.equals("friend_account_not_exists")){modify by lwl
                    } else if (ServiceErrorCode.EXCEPTION_FRIEND_NOT_EXIST.getCode().equals(errorCode)) {
                        XToast.show(getApplication(), R.string.invalid_account);
                        return;
                    } else if (errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_NOT_VALID.getCode())
                            || errorCode.equals(ServiceErrorCode.REQUEST_PARAMS_ERROR.getCode())) {
                        XToast.show(getApplication(), R.string.version_too_low);
                        return;
                    } else if (errorCode.equals(ServiceErrorCode.INTERNAL_SERVER_ERROR.getCode())
                            || errorCode.equals(ServiceErrorCode.EXCEPTION_HANDLE_ERROR.getCode())) {
                        XToast.show(getApplication(), R.string.server_is_busy);
                        return;
                        //[s]modify by xienana for request friend while yourself friends number limit @20161205
                    } else if (errorCode.equals(ServiceErrorCode.FRIEND_REQUEST_FRIEND_LIMIT.getCode())) {
                        XToast.show(getApplication(), ActomaController.getApp().getString(R.string.friend_friends_max_limit));
                        return;
                        //[e]modify by xienana for request friend while yourself friends number limit @20161205
                    } else {
                        XToast.show(getApplication(), getString(R.string.request_info_error));
                        LogUtil.getUtils().e("Actoma contact FriendRequestInfoPresenter sendAddFriendRequest:发送好友请求出错，errorCode:" + errorCode + "");
                        return;
                    }
                } else {
                    XToast.show(getApplication(), getString(R.string.request_info_error));
                    LogUtil.getUtils().e("Actoma contact FriendRequestInfoPresenter sendAddFriendRequest:发送好友请求出错，errorCode is null");
                    return;
                }
            }

            @Override
            public void onSuccess(String s) {
                getVu().dismissLoading();

                FriendRequestInfoService requestInfoService = new FriendRequestInfoService();
                AuthInfo authInfo = new AuthInfo();
                authInfo.setAccount(actomaAccount.getAccount());
                authInfo.setCreateTime(String.valueOf(System.currentTimeMillis()));
                authInfo.setValidateInfo(verifyInfo);
                boolean saveAuth = requestInfoService.insert(authInfo);

                FriendRequestService friendRequestService = new FriendRequestService();
                FriendRequestHistory history = new FriendRequestHistory();
                history.setReqAccount(ContactUtils.getCurrentAccount());
                history.setRecAccount(actomaAccount.getAccount());
                history.setIsRead(history.READED);
                history.setRequestState(FriendHistoryState.WAIT_ACCEPT);
                history.setUpdateSerial("0");
                history.setTime(System.currentTimeMillis() + "");
                history.setAuthInfo(verifyInfo);
                boolean saveOrUpdateHistory = friendRequestService.saveOrUpdate(history);
                if (saveOrUpdateHistory)
                    BroadcastManager.refreshFriendRequestList();//add by lwl

                ActomAccountService actomAccountService = new ActomAccountService();
                boolean accountSave = actomAccountService.saveOrUpdate(actomaAccount);

                AvaterService avaterService = new AvaterService();
                Avatar avatar = actomaAccount.getAvatarBean();
                boolean avaterInsert = avaterService.saveOrUpdate(avatar);

                if (saveAuth && saveOrUpdateHistory && accountSave && avaterInsert) {
                    BroadcastManager.refreshFriendRequestList();
//                   finish();
//                   ActivityContoller.getInstanse().finishSearchFlowActivity();
                    ActivityStack.getInstanse().popActivityByClass(CommonDetailPresenter.class, true);
                    ActivityStack.getInstanse().popActivityByClass(ChooseContactPresenter.class, true);
                    ActivityStack.getInstanse().popActivityByClass(FriendSearchPresenter.class, true);
                    ActivityStack.getInstanse().popActivityByClass(FriendRequestInfoPresenter.class, true);
                } else {
                    XToast.show(getApplication(), getString(R.string.request_info_error));
                    LogUtil.getUtils().e("Actoma contact FriendRequestInfoPresenter sendAddFriendRequest:发送好友请求，服务器返回数据保存出错");
                }
            }

            @Override
            public void onErr() {
                getVu().dismissLoading();
            }
        }, verifyInfo, actomaAccount.getAccount());
    }
    //add by lwl start 2776
    public FriendRequestHistory queryHistoryRequest(){
        FriendRequestService friendRequestService = new FriendRequestService();
        return friendRequestService.query(accountRequested,currentAccount);
    }
    //add by lwl end
}

