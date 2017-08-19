package com.xdja.contact.presenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.CloseMenuEvent;
import com.xdja.comm.event.TabTipsEvent;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.callback.IAccountCallback;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.presenter.activity.AnTongComeInPresenter;
import com.xdja.contact.presenter.activity.CommonDetailPresenter;
import com.xdja.contact.presenter.activity.FriendRequestHistoryPresenter;
import com.xdja.contact.presenter.adapter.FriendListAdapter;
import com.xdja.contact.presenter.command.IFriendListCommand;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.task.account.TaskIncrementAccount;
import com.xdja.contact.task.friend.TaskIncrementFriend;
import com.xdja.contact.task.friend.TaskIncrementalRequest;
import com.xdja.contact.ui.def.IFriendListVu;
import com.xdja.contact.ui.view.FriendListVu;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wanghao on 2015/7/20.
 *
 */
public class FriendListPresenter extends FragmentPresenter<IFriendListCommand,IFriendListVu> implements
        IFriendListCommand,IAccountCallback {


    private static final String TAG = FriendListPresenter.class.getSimpleName();

    private static final int TAG_RED_POINT = 111;

    private FriendListAdapter adapter;

    private EncryptRecordService encryptRecordService ;

    private Map map;

    private HashMap<String,String> hashMap = new HashMap<String,String>();

    @Override
    protected Class<? extends IFriendListVu> getVuClass() {
        return FriendListVu.class;
    }

    @Override
    protected FriendListPresenter getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
        if(!ObjectUtil.mapIsEmpty(map)){
            if(!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))){
                hashMap.put("destAccount", (String) map.get("destAccount"));
            }
            if(!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))){
                hashMap.put("appPackage", (String) map.get("appPackage"));
            }
        }
        this.encryptRecordService = new EncryptRecordService(getActivity());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.adapter = new FriendListAdapter(getActivity(),hashMap,map);
        getVu().setAdapter(adapter);
        loadFriendData(); //add  by lwl 2169
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.getUtils().e("Actoma contact FriendListP,onResume,back to list view to refresh");
       // loadFriendData(); remove by lwl 2169
        map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
        if(!ObjectUtil.mapIsEmpty(map)){
            if(!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))){
                hashMap.put("destAccount", (String) map.get("destAccount"));
            }
            if(!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))){
                hashMap.put("appPackage", (String) map.get("appPackage"));
            }
            adapter.setHashMapAndMap(hashMap, map);
            adapter.notifyDataSetChanged();
        }else {
            adapter.setMap(null);
            adapter.setHashMap(null);
            adapter.notifyDataSetChanged();
        }
    }

//modify by lwl start
    @Override
    public void loadFriendData() {
        freshData().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.getUtils().e("Actoma contact FriendListP,loadFriendData fail");
                    }

                    @Override
                    public void onNext(List<Friend> friends) {
                        getVu().setDataSource(friends, friendSize);
                    }
                });

    }
    //[S]add by tangsha@20161128 for show friend count
    private int friendSize = 0;
    //[E]add by tangsha@20161128 for show friend count
    public Observable<List<Friend>> freshData(){
        return  Observable.create(new Observable.OnSubscribe<List<Friend>>() {
            @Override
            public void call(Subscriber<? super List<Friend>> subscriber) {
                FriendService service = new FriendService();
                List<Friend> dataSource = service.queryFriends();
                dataSource.add(Friend.Builder.buildAtFriend());
                //[S]add by tangsha@20161128 for show friend count
                friendSize = dataSource.size();
                //[E]add by tangsha@20161128 for show friend count
                dataSource = ContactShowUtil.comparatorDataSource(ContactShowUtil.dataSeparate(dataSource));
                dataSource.add(0, Friend.Builder.buildNewFriend());
                subscriber.onNext(dataSource);
            }
        });

    }
//modify by lwl end
    /**
     * 刷新列表
     */
    public void updateFriendList(){
        new TaskIncrementAccount(this, String.valueOf(SystemClock.elapsedRealtimeNanos())).template(0);
    }

    @Override
    public void updateFriendRequestList() {
        TaskIncrementalRequest incrementalRequest = new TaskIncrementalRequest(String.valueOf(SystemClock.elapsedRealtimeNanos()));
        incrementalRequest.template(0);
    }

    private void pullFriends(){
        TaskIncrementFriend taskIncrementFriend = new TaskIncrementFriend(this, String.valueOf(SystemClock.elapsedRealtimeNanos()));
        taskIncrementFriend.template(0);
    }

    @Override
    public void startDetailFriend(Friend friend) {
        //安通+团队
        if(Friend.AT_OBJECT.equals(friend.getType())){
            //跳转到单一的安通+团队页面
            Intent intent = new Intent(getActivity(), AnTongComeInPresenter.class);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_ANTONG_FRIEND_DATA,friend);
            startActivity(intent);
        }else{
            if(Friend.NEW_FRIEND == friend.getViewType()){
                /**使用startActivityForResult方法解决主框架底部小红点消失问题,不再在onResume中missRedTips()
                 这使已出现的小红点再次消失掉 add by yangpeng 2015-09-09**/
                Intent intent = new Intent(getActivity(), FriendRequestHistoryPresenter.class);
                startActivityForResult(intent,TAG_RED_POINT);

            }else if(Friend.CONTACT_ITEM == friend.getViewType()) {
                ActomaAccount actomaAccount = friend.getActomaAccount();
                Intent intent = new Intent(getActivity(), CommonDetailPresenter.class);
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,friend.getAccount());
                startActivity(intent);
            }else{
                Log.e(TAG, "点击的是字母索引");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAG_RED_POINT){
            if(resultCode == FriendRequestHistoryPresenter.RESULT_OK)
            missRedTips();
        }

    }

    /**
     * 点击查看好友请求历史,红点消失
     */
    private void missRedTips(){
        TabTipsEvent event = new TabTipsEvent();
        event.setContent("");
        event.setIndex(TabTipsEvent.INDEX_CONTACT);
        event.setIsShowPoint(false);
        BusProvider.getMainProvider().post(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegisterActionUtil.ACTION_REFRESH_LIST);
        filter.addAction(RegisterActionUtil.ACTION_CLOSE_MENU);
        filter.addAction(RegisterActionUtil.ACTION_FRIEND_REQUEST);
        filter.addAction(RegisterActionUtil.ACTION_DELETE_FRIEND_CLOSE_TRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_OPEN_TRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_DEPARTMENT_DOWNLOAD_SUCCESS);
        filter.addAction(RegisterActionUtil.ACTION_ACCOUNT_DOWNLOAD_SUCCESS);
        filter.addAction(RegisterActionUtil.ACTION_REQUEST_DOWNLOAD_SUCCESS);
        filter.addAction(RegisterActionUtil.ACTION_CLOSE_TANSFER);
        filter.addAction(RegisterActionUtil.ACTION_CONTACT_GIVE_APP_NAME);
        filter.addAction(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS);//add by lwl 3453
        getActivity().registerReceiver(broadcastReceiver,filter);
    }



    /**
     * 安全通信开启  通知联系人刷新
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(adapter == null || adapter.getCount() != getVu().friendListCount()) {
                LogUtil.getUtils().e("Actoma contact FriendListP, broadcastReceiver adapter size wrong "+adapter);
                return;
            }
            LogUtil.getUtils().e("Actoma contact FriendListP,broadcast action: "+intent.getAction());
            //Start:add by wal@xdja.com for 4698
            if(RegisterActionUtil.ACTION_ACCOUNT_DOWNLOAD_SUCCESS.equals(intent.getAction())){
                loadFriendData();
            }
            //End:add by wal@xdja.com for 4698
            if(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS.equals(intent.getAction())){//add by lwl 3453
                loadFriendData();
            }
            if(RegisterActionUtil.ACTION_OPEN_TRANSFER.equals(intent.getAction())){
                adapter.notifyDataSetChanged();
            }
            if(RegisterActionUtil.ACTION_DELETE_FRIEND_CLOSE_TRANSFER.equals(intent.getAction())){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BusProvider.getMainProvider().post(new CloseMenuEvent());
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            //统计未读消息  显示红点
            if(RegisterActionUtil.ACTION_REFRESH_LIST.equals(intent.getAction())){
                BusProvider.getMainProvider().post(new CloseMenuEvent());
                loadFriendData();

            }
            if(RegisterActionUtil.ACTION_FRIEND_REQUEST.equals(intent.getAction())){
                BusProvider.getMainProvider().post(new CloseMenuEvent());
                loadFriendData();
            }
            if(RegisterActionUtil.ACTION_REQUEST_DOWNLOAD_SUCCESS.equals(intent.getAction())){
                loadFriendData();
            }
            if(RegisterActionUtil.ACTION_DEPARTMENT_DOWNLOAD_SUCCESS.equals(intent.getAction())){
                loadFriendData();
            }
            if (RegisterActionUtil.ACTION_CLOSE_MENU.equals(intent.getAction())){
                encryptRecordService.closeSafeTransfer();
                BroadcastManager.sendBroadcast();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BusProvider.getMainProvider().post(new CloseMenuEvent());
                        adapter.notifyDataSetChanged();
                    }
                });

            }

            if(intent.getAction().equals(RegisterActionUtil.ACTION_CONTACT_GIVE_APP_NAME)){
                String appName = intent.getStringExtra("appShowName");
                String account = intent.getStringExtra("account");
                hashMap.put("destAccount", account);
                hashMap.put("appPackage", appName);
                adapter.setHashMap(hashMap);
                adapter.notifyDataSetChanged();
            }

            if(intent.getAction().equals(RegisterActionUtil.ACTION_CLOSE_TANSFER)){
                map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
                if(!ObjectUtil.mapIsEmpty(map)){
                    if(!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))){
                        hashMap.put("destAccount", (String) map.get("destAccount"));
                    }
                    if(!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))){
                        hashMap.put("appPackage", (String) map.get("appPackage"));
                    }
                    adapter.setHashMapAndMap(hashMap,map);
                    adapter.notifyDataSetChanged();
                }else {
                    adapter.setMap(null);
                    adapter.setHashMap(null);
                    adapter.notifyDataSetChanged();
                }

            }
        }
    };

    @Override
    public void onAccountSuccess(List<ResponseActomaAccount> accountList) {
        if (!ObjectUtil.collectionIsEmpty(accountList)) {
            ArrayList<String> accounts = new ArrayList<String>();
            for (ActomaAccount account : accountList) {
                accounts.add(account.getAccount());
            }
            FireEventUtils.pushFriendUpdateNickName(accounts);
            BroadcastManager.refreshFriendList();
            BroadcastManager.sendBroadcastRefreshTabName();
        }
        pullFriends();
    }

    //start:add by wal@xdja.com for 3991
    @Override
    public void onShowErrorToast(HttpErrorBean httpErrorBean) {
        if (!ObjectUtil.objectIsEmpty(httpErrorBean)){
            XToast.show(getActivity(), httpErrorBean.getMessage());
        }else{
            XToast.show(getActivity(),R.string.contact_net_error);
        }
    }
    //end:add by wal@xdja.com for 3991
    @Override
    public boolean isSupportLoading() {
        return true;
    }

    @Override
    public void stopRefreshLoading() {
        getVu().stopRefush();
    }
}
