package com.xdja.contact.presenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.zxing.creat.PopupWindowZxing;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.bean.enumerate.FriendHistoryState;
import com.xdja.contact.presenter.adapter.FriendRequestHistoryAdapter;
import com.xdja.contact.presenter.command.IFriendRequestCommand;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.ui.def.IFriendRequestHistoryVu;
import com.xdja.contact.ui.view.FriendRequestHistoryVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.util.List;

/**
 * Created by wanghao on 2015/7/21.
 *
 */
public class FriendRequestHistoryPresenter extends ActivityPresenter<IFriendRequestCommand,IFriendRequestHistoryVu> implements IFriendRequestCommand {

    public static final int RESULT_OK = 1;

    private FriendRequestHistoryAdapter adapter;

    private FriendRequestService service;

    private List<FriendRequestHistory> dataSource;
    private PopupWindowZxing popupWindowZxing;
    @Override
    protected Class<? extends IFriendRequestHistoryVu> getVuClass() {
        return FriendRequestHistoryVu.class;
    }


    @Override
    protected IFriendRequestCommand getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.adapter = new FriendRequestHistoryAdapter(this);
        this.service = new FriendRequestService();
        getVu().setAdapter(adapter);
        //start:add by wal@xdja.com for 2317 review by lwl 2016/08/05
        this.dataSource = service.queryFriendRequestHistories();
        getVu().setDataSource(dataSource);
        //end:add by wal@xdja.com for 2317 review by lwl 2016/08/05
    }
    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        processingClick = false;
        //start:modify by wal@xdja.com for 2317 review by lwl 2016/08/05
//        this.dataSource = service.queryFriendRequestHistories();
        service.updateIsRead();
//        getVu().setDataSource(dataSource);
        //end:modify by wal@xdja.com for 2317 review by lwl 2016/08/05
        sendBroadcastRead();
        //add by lwl start
        if(popupWindowZxing!=null){
            popupWindowZxing.hidePopupWindow();
        }
        //add by lwl end
    }

    //[S]tangsha modify@20170117 for 8130
    private boolean isPaused = false;
    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }
    //[E]tangsha modify@20170117 for 8130

    //通知主框架更新联系人下标红点
    private void sendBroadcastRead(){
        ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_REQUEST_DOWNLOAD_SUCCESS));
    }

    /**
     * 查看好友详情
     */
    private boolean processingClick = false;
    @Override
    public void showFriendDetail(int position) {
        //[S]modify by tangsha@20161202 for 6477
        if(ObjectUtil.collectionIsEmpty(dataSource) || processingClick){
            LogUtil.getUtils().e("FriendRequestHistoryPresenter showFriendDetail return directly, processingClick "+processingClick);
            return;
        }
        //[E]modify by tangsha@20161202 for 6477
        FriendRequestHistory history = dataSource.get(position);
        String account = history.getShowAccount();
        Friend friend = new FriendService().queryFriendByAccount(account);
        //start:add by wal@xdja.com for 682
        ActomaAccount actomaAccount = history.getActomaAccount();
        //end:add by wal@xdja.com for 682
        //如果已经删除  我们该怎么跳转 (因为当前的状态 可能是已添加 但是删除的时候我们没有执行删除历史请求记录的动作)
        if(history.getRequestState() == FriendHistoryState.ALREADY_FRIEND){
            if(!ObjectUtil.objectIsEmpty(friend) && friend.isShow()){
                processingClick = true;
                startFriendDetail(friend);
            }else{
                XToast.show(ActomaController.getApp(),getString(R.string.not_related));
            }
        }
        if(history.getRequestState() == FriendHistoryState.WAIT_ACCEPT){
            if(ContactModuleService.checkNetWork()){
                //start:add by wal@xdja.com for 682
                processingClick = true;
                if(!ObjectUtil.objectIsEmpty(actomaAccount) && !ObjectUtil.objectIsEmpty(actomaAccount.getAlias())){
                    startAccountDetail(actomaAccount.getAlias());
                }else{
                    startAccountDetail(account);
                }
//                startAccountDetail(account);
                //end:add by wal@xdja.com for 682
            }
        }
        if(history.getRequestState() == FriendHistoryState.ACCEPT){
            //接受好友请求页面
            if(ObjectUtil.stringIsEmpty(account) == false) {
                processingClick = true;
                startAcceptFriendDetail(history);//modify by lwl
            }
        }
    }

    //好友详情
    private void startFriendDetail(Friend friend){
        Intent intent = new Intent(this, CommonDetailPresenter.class);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,friend.getAccount());
        startActivity(intent);
    }
    //账户详情
    private void startAccountDetail(String account){
        Intent intent = new Intent(this, CommonDetailPresenter.class);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_SCAN_SEARH);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,account);
        startActivity(intent);
    }

    //接受好友请求详情
    private void startAcceptFriendDetail(FriendRequestHistory history){
        Intent intent = new Intent(this,AcceptFriendApplyPresenter.class);
        intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_ACCOUNT_ACCEPT,history);
        startActivity(intent);
    }

    @Override
    public void startSearch() {
        Intent intent = new Intent(this,FriendSearchPresenter.class);
        startActivity(intent);
    }

    @Override
    public void showErWeiMa() {
        AccountBean account=ContactUtils.getCurrentBean();//add by lwl
        String currentAccount = account.getAlias()==null?account.getAccount():account.getAlias();
        if(ObjectUtil.stringIsEmpty(currentAccount))return;
        popupWindowZxing = new PopupWindowZxing(this,"1#"+currentAccount);
        popupWindowZxing.showPopupWindow();
    }

    @Override
    public void backWithResult() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegisterActionUtil.ACTION_FRIEND_REQUEST);
        filter.addAction(RegisterActionUtil.ACTION_REFRESH_LIST);//add by wal@xdja.com for 691
        filter.addAction(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS);//add for 4449 by wal@xdja.com 2016/09/23
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(adapter == null && adapter.getCount() != getVu().historyListCount()) {
                return;
            }
            if(RegisterActionUtil.ACTION_FRIEND_REQUEST.equals(intent.getAction())||RegisterActionUtil.ACTION_REFRESH_LIST.equals(intent.getAction())
                    ||RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS.equals(intent.getAction())){ //add by wal@xdja.com for 691,4449
                dataSource = service.queryFriendRequestHistories();
                //[S]tangsha modify@20170117 for 8130
                if(isPaused == false) {
                    service.updateIsRead();
                }
                //[E]tangsha modify@20170117 for 8130
                getVu().setDataSource(dataSource);
            }
        }
    };
}
