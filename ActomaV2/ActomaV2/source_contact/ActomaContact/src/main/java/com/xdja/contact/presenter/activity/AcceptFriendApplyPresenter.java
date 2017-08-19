package com.xdja.contact.presenter.activity;

import android.os.Bundle;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.task.friend.TaskAcceptFriendReq;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.presenter.command.IContactFriendApplyCommand;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.FriendRequestInfoService;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.ui.def.IAcceptFriendApplyVu;
import com.xdja.contact.ui.view.AcceptFriendApplyVu;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.NotificationUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * wanghao
 */
public class AcceptFriendApplyPresenter extends ActivityPresenter<IContactFriendApplyCommand, IAcceptFriendApplyVu> implements
        IContactFriendApplyCommand,IModuleHttpCallBack {

    private String account;

    private List<AuthInfo> authInfos;

    private FriendRequestHistory history;

    @Override
    protected Class<? extends IAcceptFriendApplyVu> getVuClass() {
        return AcceptFriendApplyVu.class;
    }

    @Override
    protected IContactFriendApplyCommand getCommand() {
        return this;
    }

    private void loadingVerifications(){
        getVu().showDialog();
        FriendRequestInfoService friendRequestInfoService = new FriendRequestInfoService();
        authInfos = friendRequestInfoService.queryVerificationInfo(account);
        getVu().dismissDialog();
        getVu().setFriendData(account, authInfos);
    }



    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
//        account = getIntent().getStringExtra(DATA_ACCOUNT_ACCEPT);
        history= getIntent().getParcelableExtra(RegisterActionUtil.EXTRA_KEY_DATA_ACCOUNT_ACCEPT);
        account=history.getShowAccount();//modify by lwl
        if(ObjectUtil.objectIsEmpty(account))finish();
        loadingVerifications();
    }


    /**
     * 处理好友申请
     */
    @Override
    public void agreeFriendApply() {
        /*start:add by wal@xdja.com for ckms */
        TaskAcceptFriendReq acceptFriendReqTask = new TaskAcceptFriendReq(this, new TaskAcceptFriendReq.IAcceptFriendCallback() {
            @Override
            public void onPreExecute() {
                getVu().showDialog();
            }

            @Override
            public void onPostExecute() {
                getVu().dismissDialog();
            }
        }, this);
        acceptFriendReqTask.execute(account);
        /*end:add by wal@xdja.com for ckms */
    }

    @Override
    public void onFail(HttpErrorBean httpErrorBean) {
        getVu().dismissDialog();
        //modify by lwl start already_is_friend toast
        if(!ObjectUtil.objectIsEmpty(httpErrorBean)) {
            LogUtil.getUtils().e("Actoma contact AcceptFriendApply Fail httpErrorBean getMessage:"+httpErrorBean.getMessage());
            if(ServiceErrorCode.EXCEPTION_ALREADY_FRIEND.getCode().equals(httpErrorBean.getErrCode())){//modify by lwl
                XToast.show(this, getString(R.string.already_is_friend));
                this.finish();
                return;
            }else if(ServiceErrorCode.FRIENDS_LIMIT_ERROR.getCode().equals(httpErrorBean.getErrCode())){//modify by lwl start 2319 好友数量达到上限
                XToast.show(this,ActomaController.getApp().getString(R.string.friend_friends_max_limit));//modify by xienana for multi language change @20161205
                this.finish();
                return;
            }
            XToast.show(this, getString(R.string.accept_friend_error));
        }else{
            LogUtil.getUtils().e("Actoma contact AcceptFriendApply Fail httpErrorBean getMessage is null");
            XToast.show(this, getString(R.string.accept_friend_error));
        }
        //modify by lwl end already_is_friend toast
    }
    /*start:add by wal@xdja.com for ckms */
    private final String TAG = AcceptFriendApplyPresenter.class.getSimpleName();
    private String currentAccount = ContactUtils.getCurrentAccount();
    /*end:add by wal@xdja.com for ckms */
    @Override
    public void onSuccess(String body) {
        FriendRequestService friendRequestService = new FriendRequestService();
//        FriendRequestHistory.Builder builder = new FriendRequestHistory.Builder(account);
//        FriendRequestHistory history = builder.acceptFriendRequestHistory((authInfos.size()>0)?authInfos.get(0).getValidateInfo():"");//modify by lwl 2650
        history.setRequestState(FriendHistoryState.ALREADY_FRIEND);
        history.setIsRead(FriendRequestHistory.READED);//modify by lwl
        boolean updateHistory = friendRequestService.update(history);
        if (updateHistory) {
            //保存好友数据
            FriendService friendService = new FriendService();
            Friend friend = new Friend.Builder().buildNormalFriend(account);
            int count = friendService.countFriends();
            if(count > 0){
                friend.setUpdateSerial("1");
            }
            boolean result = friendService.saveOrUpdate(friend);
            if (result) {
                //start:add by wal@xdja.com for 好友事件通知
                List<String> friendAcountList=new ArrayList<String>();
                friendAcountList.add(account);
                List<String> showNameList = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("",friendAcountList );
                if (!showNameList.isEmpty()){
                    FireEventUtils.pushFriendClickedAcceptButton(account,showNameList.get(0));
                }else{
                    FireEventUtils.pushFriendClickedAcceptButton(account,account);
                }
                //end:add by wal@xdja.com for 好友事件通知
                BroadcastManager.refreshFriendRequestList();
                BroadcastManager.refreshFriendList();
                NotificationUtil.cancelNotification();
                finish();
            }
        }else{
            LogUtil.getUtils().e("Actoma contact AcceptFriendApply update accept friend history data fail");
            XToast.show(this, getString(R.string.accept_friend_error));
        }
        getVu().dismissDialog();
    }

    @Override
    public void onErr() {
        getVu().dismissDialog();
    }
}
