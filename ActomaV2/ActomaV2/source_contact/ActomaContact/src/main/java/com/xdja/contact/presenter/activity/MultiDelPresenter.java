package com.xdja.contact.presenter.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.executor.SearchAsyncExecutor;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.presenter.adapter.BaseSelectAdapter;
import com.xdja.contact.presenter.adapter.GroupDelMulMemberAdapter;
import com.xdja.contact.presenter.adapter.SearchGroupMemberAdapter;
import com.xdja.contact.presenter.command.IMultiDelCommand;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.ui.def.IMultiDelVu;
import com.xdja.contact.ui.view.MultiDelVu;
import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.dependence.uitls.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangshaopeng on 2016/11/1.
 */
public class MultiDelPresenter extends ActivityPresenter<MultiDelPresenter, IMultiDelVu> implements IMultiDelCommand, ISelectCallBack, IContactEvent {

    public static final int MULTI_DEL_MEMBER = 0;
    public static final int ALL_GROUP_MEMBER = 1;
    public static final String OPEN_TYPE = "open_type";
    public static final String GROUP_ID = "group_id";
    private static final int COMPLETE_INIT_DATA = 1;
    private static final int UPDATE_DB_SUCCESS = 2;
    private static final int UPDATE_DB_FAILED = 3;
    private static final int BEGIN_SEARCH = 1000;
    private static final int END_SEARCH = 2000;

    private String groupId;
    private List<UserInfo> userInfoList;
    private GroupDelMulMemberAdapter groupDelMulMemberAdapter;
    private SearchGroupMemberAdapter searchGroupMemberAdapter;
    private String searchKey;
    private List<String> selectMember = new ArrayList<>(); //删除联系人账号的集合
    private int openType;  //0代表进入批量删除页面，1代表进入全部群成员页面
    private List<GroupMember> groupMembers;
    private AsyncTask<String, Integer, List<UserInfo>> task;
    private boolean isSearching;

    @NonNull
    @Override
    protected MultiDelPresenter getCommand() {
        return this;
    }

    @NonNull
    @Override
    protected Class<? extends IMultiDelVu> getVuClass() {
        return MultiDelVu.class;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        openType = getIntent().getIntExtra(OPEN_TYPE, ALL_GROUP_MEMBER);
        groupId = getIntent().getStringExtra(GROUP_ID);
        getVu().setToolBarTitle(0);
        FireEventUtils.addGroupListener(this);
        initData();
    }

    void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfoList = new GroupExternalService(getBaseContext()).getUserInfosByGroupId(groupId);
                owner2FirstIndex(userInfoList);
                Message msg = Message.obtain();
                msg.what = COMPLETE_INIT_DATA;
                handler.sendMessage(msg);
            }
        }).start();

    }

    //将群主放在第一位
    private List<UserInfo> owner2FirstIndex(List<UserInfo> userInfos) {
        for (int i = 0; i < userInfos.size(); i++) {
            if(GroupUtils.isGroupOwner(this, groupId, userInfos.get(i).getAccount())) {
                userInfos.add(0, userInfos.remove(i));
                break;
            }
        }
        return userInfos;
    }

    /**
     * 批量删除联系人
     */
    @Override
    public void delMemberMulti() {
        startDelMember(selectMember);
    }

    /**
     * 长按条目单个删除联系人
     *
     * @param account 要删除的账号
     */
    @Override
    public void delMemberSingle(String account) {
        startDelMember(Arrays.asList(account));
    }

    /**
     * 开始删除联系人入口(单个删除和批量删除)
     *
     * @param accounts 删除联系人账号集合
     */
    public void startDelMember(final List<String> accounts) {
        if (!ContactModuleService.checkNetWork() || accounts.size() < 1) {
            return;
        }
        List<String> delMemberList = new ArrayList<>();
        delMemberList.addAll(accounts);
        getVu().showCommonProgressDialog(getResources().getString(R.string.is_deleting));
        GroupHttpServiceHelper.delMemberFromGroup(groupId, delMemberList, new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean errorBean) {
                getVu().dismissCommonProgressDialog();
                LogUtil.getUtils().e("Actoma Contact delete onFail errorCode:" + errorBean.getErrCode() + ";errorMsg" + errorBean.getMessage());
                Toast.makeText(getBaseContext(), getResources().getString(R.string.delete_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String body) {
                groupMembers = new ArrayList<>();
                for (String account : accounts) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setAccount(account);
                    groupMember.setGroupId(groupId);
                    groupMember.setIsDeleted(GroupConvert.DELETED);
                    groupMembers.add(groupMember);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = Message.obtain();
                        boolean isDelSucc = GroupInternalService.getInstance().multiDeleteGroupMember(groupMembers);
                        if (isDelSucc) {
                            msg.what = UPDATE_DB_SUCCESS;
                        } else {
                            msg.what = UPDATE_DB_FAILED;
                        }
                        handler.sendMessage(msg);
                    }
                }).start();


            }

            @Override
            public void onErr() {
                getVu().dismissCommonProgressDialog();
                LogUtil.getUtils().e("Actoma Contact delete onError");
            }
        });
    }

    @Override
    public List<UserInfo> getCurrentAdapterSource() {
        if (groupDelMulMemberAdapter != null) {
            return groupDelMulMemberAdapter.getDataSource();
        }
        if (searchGroupMemberAdapter != null) {
            return searchGroupMemberAdapter.getDataSource();
        }
        return null;
    }

    @Override
    public void startSearch(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        groupDelMulMemberAdapter = null; //将之前的adapter置为空，目的是为了获取两个adapter资源数据的时候加个判断条件。
        searchGroupMemberAdapter = new SearchGroupMemberAdapter(this, this, null, null, openType);
        getVu().setSearchGroupMemberAdapter(searchGroupMemberAdapter);
        searchKey = key;
        handler.removeMessages(BEGIN_SEARCH);
        handler.sendEmptyMessage(BEGIN_SEARCH);
    }

    @Override
    public void endSearch() {
        searchGroupMemberAdapter = null; //将之前的adapter置为空，目的是为了获取两个adapter资源数据的时候加个判断条件。
        groupDelMulMemberAdapter = new GroupDelMulMemberAdapter(this, this, null, userInfoList, openType);
        getVu().setMultiDelAdapter(groupDelMulMemberAdapter);
        handler.removeMessages(END_SEARCH);
        handler.sendEmptyMessage(END_SEARCH);
        isSearching = false;
    }

    public String getGroupId() {
        return groupId;
    }

    /**
     * 群组成员异步搜索
     */
    private class SearchGroupMember extends AsyncTask<String, Integer, List<UserInfo>> {
        @Override
        protected List<UserInfo> doInBackground(String... params) {
            isSearching = true;
            return GroupInternalService.getInstance().searchGroupMemberByKey(groupId, params[0].trim());
        }

        @Override
        protected void onPostExecute(List<UserInfo> userInfos) {
            super.onPostExecute(userInfos);
            owner2FirstIndex(userInfos);
            if (!ObjectUtil.collectionIsEmpty(userInfos)) {
                getVu().setSearchGroupMemberAdapter(searchGroupMemberAdapter);
                //adapter有可能为空，初始化之前的数据。
                if(ObjectUtil.objectIsEmpty(searchGroupMemberAdapter)) {
                    initData();
                    return;
                }
                if(!ObjectUtil.collectionIsEmpty(userInfos) && !userInfos.isEmpty() && !TextUtils.isEmpty(searchKey))
                    searchGroupMemberAdapter.setDataSource(userInfos, searchKey);
            }
        }
    }

    @Override
    public void callBackCount(int count) {
        getVu().updateTitle(count);
    }

    @Override
    public void selectedCallback(Map<String, Object> selectedMap) {
        selectMember.clear();
        selectMember.addAll(selectedMap.keySet());
    }

    @Override
    public int getOpenType() {
        return openType;
    }

    @Override
    public boolean isGroupOwner() {
        return GroupUtils.isGroupOwner(this, groupId);
    }

    @Override
    public void startCommonDetail(View view) {
        if(openType == 0) { //删除界面不进入好友详情界面
            return;
        }
        if(!ObjectUtil.objectIsEmpty(groupDelMulMemberAdapter)) {
            GroupDelMulMemberAdapter.ViewHolder viewHolder = (GroupDelMulMemberAdapter.ViewHolder)view.getTag();
            if(!viewHolder.getAccount().equals(ContactUtils.getCurrentAccount())) {
                ContactModuleService.startContactDetailActivity(this, viewHolder.getAccount());
            }
        } else {
            SearchGroupMemberAdapter.ViewHolder viewHolder = (SearchGroupMemberAdapter.ViewHolder)view.getTag();
            if(!viewHolder.getAccount().equals(ContactUtils.getCurrentAccount())) {
                ContactModuleService.startContactDetailActivity(this, viewHolder.getAccount());
            }
        }
    }

    /**
     * 在全部群成员界面进入添加群成员界面
     */
    @Override
    public void startChooseContact() {
        int groupMemberLimit = PreferenceUtils.getGroupMemberLimitConfiguration();
        if(userInfoList.size() >= groupMemberLimit) {
            XToast.show(this, String.format(getResources().getString(R.string.group_max_member), groupMemberLimit));
            return;
        }
        Set<String> accounts = new HashSet<String>();
        for (UserInfo member : userInfoList) {
            if(!ObjectUtil.stringIsEmpty(member.getAccount())){
                accounts.add(member.getAccount());
            }
        }
        GroupUtils.launchChooseContactActivity(this, getCommand().getGroupId(), new ArrayList<String>(accounts));
    }

    @Override
    public void onEvent(int event, Object param1, Object param2, Object param3) {
        switch (event) {
            case EVENT_GROUP_QUIT:  //群主移除成员  刷新Adapter界面
            case EVENT_MEMBER_ADDED:  //添加成员
                if(param3 instanceof  Integer){
                    if(EVENT_GROUP_QUIT==event&&(IContactEvent.REMOVED==(int)param3||IContactEvent.DISMISS==(int)param3)){
                        if(GroupUtils.isGroupOwner(this, (String)param1) == false) {
                            finish();  //被移除的群成员退出群信息界面
                        }                       
                        return;
                    }
                }
                initData();  //刷新界面
                getVu().setSearchEtContent("");
                break;
        }
    }

    private static class MultiDelHandler extends Handler{
        private WeakReference<MultiDelPresenter> mActivity;
        public MultiDelHandler(MultiDelPresenter activity){
            mActivity = new WeakReference<MultiDelPresenter>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MultiDelPresenter activity = mActivity.get();
            if(activity != null){
                activity.getVu().dismissCommonProgressDialog();
                LogUtil.getUtils().e("Actoma MultiDelHandler msg "+msg.what);
                switch (msg.what) {
                    case COMPLETE_INIT_DATA:
                        activity.processInitDataMsg();
                        break;
                    case UPDATE_DB_SUCCESS:
                        activity.processUpdateDbMsg();
                        break;
                    case UPDATE_DB_FAILED:
                        break;
                    case BEGIN_SEARCH:
                        activity.executeSearch();
                        break;
                }
            }
        }
    }

    public void processInitDataMsg(){
        getVu().setToolBarTitle(userInfoList.size());
        groupDelMulMemberAdapter = new GroupDelMulMemberAdapter(MultiDelPresenter.this, MultiDelPresenter.this, null, userInfoList, openType);
        getVu().setMultiDelAdapter(groupDelMulMemberAdapter);
    }

    public void processUpdateDbMsg(){
        FireEventUtils.fireDeleteMemberEvent(groupMembers);
        LogUtil.getUtils().e("Actoma Contact delete success:" + getResources().getString(R.string.delete_success));
        if(getOpenType()  == MULTI_DEL_MEMBER) {
            finish();
        } else {  //全部群成员界面，删除成功之后刷新列表
            if(groupDelMulMemberAdapter != null){
                groupDelMulMemberAdapter.deleteData(groupMembers.get(0).getAccount());
            } else {
                searchGroupMemberAdapter.deleteData(groupMembers.get(0).getAccount());
            }
            for (UserInfo userInfo : userInfoList) {
                if(userInfo.getAccount().equals(groupMembers.get(0).getAccount())) {
                    userInfoList.remove(userInfo);
                    break;
                }
            }
            getVu().setToolBarTitle(userInfoList.size());
        }
    }

    public void executeSearch(){
        if(isSearching && task != null) {
            task.cancel(true);
        }
        task = new SearchGroupMember().executeOnExecutor(SearchAsyncExecutor.SEARCH_TASK_POOL, searchKey);
    }

   private final Handler handler = new MultiDelHandler(this);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseSelectAdapter.clear();
        handler.removeCallbacksAndMessages(null);
    }
}
