package com.xdja.contact.presenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.presenter.adapter.BaseSelectAdapter;
import com.xdja.contact.presenter.adapter.CustomViewPagerAdapter;
import com.xdja.contact.presenter.command.IChooseContactPresenter;
import com.xdja.contact.presenter.fragment.ChooseCompanyPresenter;
import com.xdja.contact.presenter.fragment.ChooseFriendsPresenter;
import com.xdja.contact.presenter.fragment.ChooseGroupPresenter;
import com.xdja.contact.task.ckms.AddAccountToSgroupTask;
import com.xdja.contact.task.ckms.CheckCKMSEntityTask;
import com.xdja.contact.task.ckms.CreateSGroupResultCallback;
import com.xdja.contact.task.ckms.CreateSGroupTask;
import com.xdja.contact.ui.def.IChooseContactVu;
import com.xdja.contact.ui.view.ChooseContactVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.annotation.StackInto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 选择联系人主界面
 * @author hkb.
 * @since 2015/7/22/0022.
 *
 * wanghao 2016-02-26 修改
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)BUG 6564, modify for share and forward function by ycm at 20161201.
 * 3)Task 2632, modify for share and forward file function by ycm at 20161222.
 */
@StackInto
public class ChooseContactPresenter extends ActivityPresenter<ChooseContactPresenter, IChooseContactVu>
        implements IChooseContactPresenter, ISelectCallBack {

    private final String TAG = ChooseContactPresenter.class.getSimpleName();

    private ChooseFriendsPresenter friendsPresenter;

    private ChooseGroupPresenter chooseGroupPresenter;// Task 2632

    private ChooseCompanyPresenter companyPresenter;

    private ArrayList<String> selectAccounts = new ArrayList<String>();
    private List<String> noSecEntityAccounts;
    //当前群或者好友里面已经存在的成员
    private ArrayList<String> existedAccounts;

    private boolean isCreateGroup;

    private String groupId;

    private Intent intent;// Task 2632

    private String shareActionType = null;// Task 2632
    private String messageType = null;// modified by ycm 2016/12/22:[文件转发或分享]
    @Override
    protected Class<? extends IChooseContactVu> getVuClass() {
        return ChooseContactVu.class;
    }

    @Override
    protected ChooseContactPresenter getCommand() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();// Task 2632
        groupId = intent.getStringExtra("group_id");
        existedAccounts = intent.getStringArrayListExtra("raw_members");
        /*if(!ObjectUtil.stringIsEmpty(groupId)){ //编辑群组
            Group groupInfo = GroupInternalService.getInstance(this).queryByGroupId(groupId);
            if (!ObjectUtil.objectIsEmpty(groupInfo)) {
                String groupOwner = groupInfo.getGroupOwner();
                if(existedAccounts.contains(groupOwner)){
                    existedAccounts.remove(groupOwner);
                }
            }
        }*/
        // Task 2632 [Begin]
        shareActionType = intent.getStringExtra(RegisterActionUtil.SHARE);
        messageType = intent.getType();// modified by ycm 2016/12/22:[文件转发或分享]
        buildViewPager();
        //[s]modify by xienana for select count @20161201
        getVu().updateConfirmAndTitle(0);
        //[s]modify by xienana for select count @20161201
		// Task 2632 [End]



    }


    /**
     * 构建ViewPager
     */
    private void buildViewPager(){

        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        buildFriendFragment(fragments, titles);
		// Task 2632 [Begin]
        if (RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(shareActionType)) {
		    //如果是从更多联系人进来则显示群聊
            buildGroupFragment(fragments, titles);
        }
		// Task 2632 [End]
        buildCompanyFragment(fragments, titles);
        CustomViewPagerAdapter viewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        getVu().setViewPageAdapter(viewPagerAdapter);

    }

    //构建好友列表
    private void buildFriendFragment(List<Fragment> fragments,List<String> titles){
        friendsPresenter = new ChooseFriendsPresenter();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ChooseFriendsPresenter.EXIST_MEMBER_KEY, existedAccounts);
        bundle.putString(RegisterActionUtil.SHARE_MESSAGE_TYPE, messageType);// modified by ycm 2016/12/22:[文件转发或分享]是否是文件分享或转发
		// Task 2632 [Begin]
        if (RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(shareActionType)) {
            bundle.putString(RegisterActionUtil.SHARE, shareActionType);
        }
		// Task 2632 [End]
        friendsPresenter.setArguments(bundle);
        fragments.add(friendsPresenter);
        titles.add(getString(R.string.choose_friend));
    }

    // Task 2632 [Begin]
    //构建群列表
    private void buildGroupFragment(List<Fragment> fragments, List<String> titles) {
        chooseGroupPresenter = new ChooseGroupPresenter();
         Bundle bundle = new Bundle();
        bundle.putStringArrayList(ChooseGroupPresenter.EXIST_MEMBER_KEY, existedAccounts);
        if (RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(shareActionType)) {
            bundle.putString(RegisterActionUtil.SHARE, shareActionType);
        }
        chooseGroupPresenter.setArguments(bundle);
        fragments.add(chooseGroupPresenter);
        titles.add(getString(R.string.choose_group));
    }
	// Task 2632 [End]

    //构建集团人员
    private void buildCompanyFragment(List<Fragment> fragments, List<String> titles) {
        companyPresenter = new ChooseCompanyPresenter();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ChooseCompanyPresenter.EXIST_MEMBER_KEY, existedAccounts);
        bundle.putString(RegisterActionUtil.SHARE_MESSAGE_TYPE, messageType);// modified by ycm 2016/12/22:[文件转发或分享]是否是文件分享或转发
		// Task 2632 [Begin]
        if (RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(shareActionType)) {
            bundle.putString(RegisterActionUtil.SHARE, shareActionType);
        }
		// Task 2632 [End]
        companyPresenter.setArguments(bundle);
        if (ContactUtils.isHasCompany()) {
            titles.add(getString(R.string.company_contact));
            fragments.add(companyPresenter);
        } else {
            //如果无集团通讯录，则隐藏header
            if (!RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(shareActionType)) {
                getVu().setSearchHeaderVisibility(View.GONE);
            }
        }
    }

    /**
     * 点击对勾,建立群组
     */
    @Override
    public void createGroupOrAddGroupMember() {
        getVu().setClickEnable(false);
        if (!ContactModuleService.checkNetWork()) {

            getVu().setClickEnable(true);
            return;
        }
		// Task 2632 [Begin]
        if (RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(shareActionType)) {
            if (ObjectUtil.collectionIsEmpty(selectAccounts)) {
                finish();
            } else {
                final ArrayList<String> selectAccountList = new ArrayList<String>();
                noSecEntityAccounts = new ArrayList<String>();
                selectAccountList.addAll(selectAccounts);
                handOutMessage(selectAccountList);
                return;
            }
        }
		// Task 2632 [End]
		
		//判断是哪种建群方式
        buildGroup();
    }
    /**
     * *判断哪三种情况进行建群
     * 1.已经是群组，只需要添加成员的操作
     * 2.不是群组，且只选择1人，进入2人聊天界面
     * 3.不是群组，且选择多人，需要创建群组
     */
    private void buildGroup(){
        if(ObjectUtil.collectionIsEmpty(selectAccounts)){
            finish();
        } else {
            final ArrayList<String> selectAccountList = new ArrayList<String>();
            selectAccountList.addAll(selectAccounts);
            if (CkmsGpEnDecryptManager.getCkmsIsOpen()) {
                CheckCKMSEntityTask checkSecurityEntityTask = new CheckCKMSEntityTask(selectAccountList,new CheckCkmsCallback(selectAccountList));
                checkSecurityEntityTask.execute();
            }else{
                startBuildGroup(selectAccountList);
            }
        }
    }

    private class CheckCkmsCallback implements CreateSGroupResultCallback
    {
        ArrayList<String> selectAccountList;

        CheckCkmsCallback(ArrayList<String> list){
            selectAccountList = list;
        }

        @Override
        public void onTaskPreExec() {
            getVu().showCommonProgressDialog(ObjectUtil.stringIsEmpty(groupId) ? getString(R.string.creating_group) : getString(R.string.adding_group_member));
        }

        @Override
        public void showExceptionToast(String message) {

        }

        @Override
        public void onTaskPostExec(int resultCode, Object info) {
            if (resultCode == CheckCKMSEntityTask.RESULT_FAIL || resultCode == AddAccountToSgroupTask.SGROUP_ACTION_FAIL){
                createGroupFailed(null);
            }
        }

        @Override
        public void onTaskBackgroundOk(int resultCode, Object info, Object info1, Object info2) {
            if(resultCode == CheckCKMSEntityTask.RESULT_OK){
                startBuildGroup(selectAccountList);
                noSecEntityAccounts = (List<String>) info;
            }
        }

        @Override
        public void onTaskProgress(int value) {

        }
    }


    private void showExceptionToast(final int resString){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XToast.show(ChooseContactPresenter.this,getString(resString));
                getVu().setClickEnable(true);
                getVu().dismissCommonProgressDialog();
            }
        });
    }

    // Task 2632 [Begin]
    private void handOutMessage(ArrayList<String> selectAccountList) {
        ContactUtils.handOut(this, selectAccountList);
    }
	// Task 2632 [End]

    private void startBuildGroup(ArrayList<String> selectAccountList) {
        boolean isGroup = !ObjectUtil.stringIsEmpty(groupId);
        if (isGroup) {
            //已经是群组，只需要添加成员的操作
            //所选的人员都没有安全身份，不能添加到群组
            if (selectAccountList.isEmpty()) {
                showExceptionToast(R.string.no_sec_entities_not_add_into_group);
            }else{
                createOrAddUser(selectAccountList);
            }
        }else {
            //点击是否+进入创群
            if (ObjectUtil.collectionIsEmpty(existedAccounts)) {
                if (selectAccounts.size() == 1) {
				    // Task 2632 [Begin]
                    //判断是否是分享
                    if (RegisterActionUtil.SHARE_FOR_CREATENEWSESSION.equals(
                            intent.getStringExtra(RegisterActionUtil.SHARE))) {
                        ContactUtils.startFriendTalkForShare(this, selectAccounts.get(0));
					// Task 2632 [End]
                    } else {
                        ContactUtils.startFriendTalk(this, selectAccounts.get(0));
                    }

                    finish();
                }else{
                    if (selectAccountList.isEmpty()) {
                        showExceptionToast(R.string.no_sec_entities_not_add_into_group);
                    } else if (selectAccountList.size() == 1){
                        showExceptionToast(R.string.no_sec_entities_not_into_group);
                        // Task 2632 [Begin]
                        //判断是否是分享
                        if (RegisterActionUtil.SHARE_FOR_CREATENEWSESSION.equals(
                                intent.getStringExtra(RegisterActionUtil.SHARE))) {
                            getVu().dismissCommonProgressDialog();
                            ContactUtils.startFriendTalkForShare(this, selectAccountList.get(0));
                            // Task 2632 [End]
                        } else {
                            ContactUtils.startFriendTalk(this, selectAccountList.get(0));
                        }
                        finish();//wal
                    } else{
                        createOrAddUser(selectAccountList);
                    }
                }
            }else{
                if (selectAccountList.isEmpty()) {
                    showExceptionToast(R.string.no_sec_entities_not_add_into_group);
                } else {
                    selectAccountList.addAll(existedAccounts);
                    createOrAddUser(selectAccountList);
                }
            }
        }
    }
    @Override
    public void callBackCount(int count) {
        //task 2632 [start]
        //[s]modify by xienana for select count @20161201
        getVu().updateConfirmAndTitle(count);
        //[e]modify by xienana for select count @20161201
        //task 2632 [end]
    }
	
	  private void createOrAddUser(ArrayList<String> selectAccountList){
        isCreateGroup = ObjectUtil.stringIsEmpty(groupId);
        if (isCreateGroup) {
            CreateSGroupTask createSGroupTask = new CreateSGroupTask(this,groupId,selectAccountList,new GroupActionResultCallback(CREATE_GROUP));
            createSGroupTask.execute();
        } else {
            AddAccountToSgroupTask addAccountToSgroupTask = new AddAccountToSgroupTask(this,groupId,selectAccountList,new GroupActionResultCallback(ADD_ACCOUNT_TO_GROUP));
            addAccountToSgroupTask.execute();
        }
    }

    private int CREATE_GROUP = 1;
    private int ADD_ACCOUNT_TO_GROUP = 2;
    private class GroupActionResultCallback implements CreateSGroupResultCallback{
        int actionType = CREATE_GROUP;
        GroupActionResultCallback(int type){
            actionType = type;
        }
        @Override
        public void onTaskPreExec() {

        }

        @Override
        public void showExceptionToast(String message) {

        }

        @Override
        public void onTaskPostExec(int resultCode, Object info) {
            if(resultCode == CreateSGroupTask.CREATE_SGROUP_FAIL){
                if(info != null) {
                    createGroupFailed((HttpErrorBean) info);
                }else{
                    createGroupFailed(null);
                }
            }
        }

        @Override
        public void onTaskBackgroundOk(int resultCode, Object info, Object info1, Object info2) {
            if(resultCode == CreateSGroupTask.CREATE_SGROUP_OK) {
                if(actionType == CREATE_GROUP && info2 != null && info2 instanceof String) {
                    groupId = (String) info2;
                }else if(actionType == CREATE_GROUP){
                    LogUtil.getUtils().e(TAG+" GroupActionResultCallback onTaskBackgroundOk resultCode "+resultCode+" info2 "+info2);
                }
                if(noSecEntityAccounts != null && !noSecEntityAccounts.isEmpty()) {
                    ContactUtils.sendNoSecMemberEvent(noSecEntityAccounts, groupId);//add by wal@xdja.com for 未关联安全设备的账号事件通知
                }
                createGroupSuccessed();
            }else{
                LogUtil.getUtils().e(TAG+" GroupActionResultCallback onTaskBackgroundOk resultCode "+resultCode);
            }
        }

        @Override
        public void onTaskProgress(int value) {
            getVu().showCommonProgressDialog(ObjectUtil.stringIsEmpty(groupId) ? getString(R.string.creating_group) : getString(R.string.adding_group_member));
        }
    };




    @Override
    public void selectedCallback(Map<String, Object> selectedMap) {
        //去除重复的数据
        selectAccounts.clear();
        selectAccounts.addAll(selectedMap.keySet());
    }

    /**
     * 创建群组失败
     */
    private void createGroupFailed(HttpErrorBean httpErrorBean){
        getVu().setClickEnable(true);
        getVu().dismissCommonProgressDialog();
        //modify by lwl start 2145
            //[s]modify by xienana for create group limit @20161205
        if(httpErrorBean != null ){
            if(ServiceErrorCode.GROUP_MAX_NUM_LIMIT.getCode().equals(httpErrorBean.getErrCode())) {
                XToast.show(getApplication(),R.string.group_max_num_limit);
            } else if(ServiceErrorCode.GROUP_MEMBER_MAX_NUM_LIMIT.getCode().equals(httpErrorBean.getErrCode())){
                XToast.show(getApplication(),R.string.group_members_max_num_limit);
            }
            //[e]modify by xienana for create group limit @20161205
        }else{
            XToast.show(this, getString(ObjectUtil.stringIsEmpty(groupId) ?
                    R.string.creating_group_failed : R.string.adding_group_member_failed));
        }
        //modify by lwl end 2145
        //start:add by wal@xdja.com for 2589  2016/08/09
//        if(existedAccounts != null){
//            selectAccounts.removeAll(existedAccounts);
//        }
        //end:add by wal@xdja.com for 2589  2016/08/09
    }

    /**
     * 创建群组成功
     */
    private void createGroupSuccessed(){
        getVu().dismissCommonProgressDialog();
        if (!ObjectUtil.stringIsEmpty(groupId)) {
		    // Task 2632 [Begin]
            if (RegisterActionUtil.SHARE_FOR_CREATENEWSESSION.equals(
                    intent.getStringExtra(RegisterActionUtil.SHARE))) {
                ContactUtils.startGroupTalkForShare(this, groupId);
			// Task 2632 [End]
            } else {
                ContactUtils.startGroupTalk(this, groupId);
            }

        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseSelectAdapter.clear();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
    }
}
