package com.xdja.contact.task.ckms;


import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.exception.ATJsonParseException;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.http.response.group.ResponseAddGroupMember;
import com.xdja.contact.http.response.group.ResponseCreateGroup;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.comm.contacttask.ContactAsyncTask;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tangsha on 2016/11/15.
 */
public class AddAccountToSgroupTask extends ContactAsyncTask<ArrayList<String>,Integer,Integer>{
    private String TAG = "ActomaContact AddAccountToSgroupTask";
    private List<String> noCkmsAccounts;
    public static final int SGROUP_ACTION_OK = 0;
    public static final int SGROUP_ACTION_FAIL = -1;
    private Activity activity;
    private String currentAccount;
    private ArrayList<String> accountsList;
    private String groupId;
    private CreateSGroupResultCallback createSGroupResultCallback;
    private int flag;

    public AddAccountToSgroupTask(Activity activity, String groupId,
                                  ArrayList<String> accounts, CreateSGroupResultCallback callback) {
        this.activity = activity;
        currentAccount=ContactUtils.getCurrentAccount();
        this.groupId = groupId;
        this.accountsList = accounts;
        this.createSGroupResultCallback = callback;
    }

    @Override
    protected Integer doInBackground(ArrayList<String>... params) {
        publishProgress();
        //判断是否存在Sgroup，如果存在,则过滤掉那些已经存在SGroup中的账号，进行添加成员操作，否则直接添加。
        //start:add by wal@xdja.com for ckms add group child 2016/08/02
        if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
            int  inSGroupCode= CkmsGpEnDecryptManager.isEntityInSGroup(ContactUtils.getCurrentAccount(), groupId);
            if(inSGroupCode==CkmsGpEnDecryptManager.ENTITY_IN_GROUP){
                noCkmsAccounts = CkmsGpEnDecryptManager.filterExistedEntities(accountsList,groupId);
                if (ListUtils.isEmpty(noCkmsAccounts)) {
                    doAddUserToGroup(accountsList);
                } else {
                    String opSign = GroupHttpServiceHelper.syncGetCkmsGroupOpSign( groupId, noCkmsAccounts, CkmsGpEnDecryptManager.ADD_ENTITY);
                    flag = doCkmsGroupHttpCallback(groupId, noCkmsAccounts, accountsList,opSign);
                }
            } //start:add by wal@xdja.com for 4625
            else if (inSGroupCode==CkmsGpEnDecryptManager.GROUP_NOT_EXIST){
                doAddUserToGroup(accountsList);
            }else{
                flag = SGROUP_ACTION_FAIL;
            }
            //end:add by wal@xdja.com for 4625
        }else{
            doAddUserToGroup(accountsList);
        }
        //end:add by wal@xdja.com for ckms add group child 2016/08/02
        return flag;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        createSGroupResultCallback.onTaskProgress(0);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        createSGroupResultCallback.onTaskPostExec(flag,null);
    }

    /*start:add by wal@xdja.com for ckms create group 2016/08/02*/
    private int doCkmsGroupHttpCallback (String groupID,List<String> accountList,ArrayList<String> accountAddOrRemoveList,String opSign){
        final String currentAccount = ContactUtils.getCurrentAccount();
        LogUtil.getUtils().d(" doCkmsGroupHttpCallback onSuccess opSign "+opSign);
        int flagCode = 0;
        int addRes = CkmsGpEnDecryptManager.addEntitiesInSGroup(currentAccount,groupID,accountList,opSign);
        if (addRes == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
            doAddUserToGroup(accountAddOrRemoveList);
        }else{
            flagCode = -1;
        }
        return flagCode;
    }
    /*end:add by wal@xdja.com for ckms create group 2016/08/02*/

    List<String> outRangeNames = new ArrayList<String>();
    List<String> notAccountNames = new ArrayList<String>();
    List<String> inGroupNames = new ArrayList<String>();//add by wal@xdja.com for 4116


    private void doAddUserToGroup(final ArrayList<String> createSelections) {
        GroupHttpServiceHelper.addUsersToGroup(groupId, createSelections, new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean errorBean) {
                LogUtil.getUtils().e(TAG+" add user to group failed ...");
                createSGroupResultCallback.onTaskPostExec(SGROUP_ACTION_FAIL,errorBean);
            }

            @Override
            public void onSuccess(String body) {
                LogUtil.getUtils().e(TAG+" add user to group success ...");
                if (!ObjectUtil.stringIsEmpty(body)) {
                    try {
                        ResponseAddGroupMember response = JSON.parseObject(body, ResponseAddGroupMember.class);
                        if (!ObjectUtil.objectIsEmpty(response)) {
                            Map<String, List<String>> blockAccounts = response.getBlockAccounts();
                            if (!ObjectUtil.mapIsEmpty(blockAccounts)) {
                                final List<String> rangeAccounts = blockAccounts.get(ResponseCreateGroup.OUT_OF_GROUP_RANGE);
                                List<String> notAccounts = blockAccounts.get(ResponseCreateGroup.ACCOUNT_NOT_EXIST);
                                //Start:add by wal@xdja.com for 4116
                                List<String> inGroupAccount = blockAccounts.get(ResponseCreateGroup.MEMBER_EXIST_IN_GROUP);
                                if (!ObjectUtil.collectionIsEmpty(inGroupAccount)){
                                    inGroupNames = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("", inGroupAccount);
                                    createSelections.removeAll(inGroupAccount);
                                }
                                //End:add by wal@xdja.com for 4116
                                if (!ObjectUtil.collectionIsEmpty(rangeAccounts)) {
                                    //start:add by wal@xdja.com for 2575
                                    outRangeNames = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("", rangeAccounts);
                                    createSelections.removeAll(rangeAccounts);
                                    //end:add by wal@xdja.com for 2575
                                }
                                if (!ObjectUtil.collectionIsEmpty(notAccounts)) {
                                    notAccountNames = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("", notAccounts);//add by wal@xdja.com for 2575
                                }
                                /*start:add by wal@xdja.com for ckms add group child 2016/08/02*/
                                //[S]remove by tangsha@1104 for quite not do ckms action
                                /*if(!ObjectUtil.collectionIsEmpty(rangeAccounts)&&rangeAccounts.size()>0 && CkmsGpEnDecryptManager.getCkmsIsOpen()){
                                    new ContactAsyncTask<Void,Void,Void>(){

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            int  inSGroupCode= CkmsGpEnDecryptManager.isEntityInSGroup(currentAccount, groupId);
                                            if(inSGroupCode==CkmsGpEnDecryptManager.ENTITY_IN_GROUP){
                                                List<String> accountList= new ArrayList<String>();
                                                accountList.addAll(rangeAccounts);
                                                String opSign= GroupHttpServiceHelper.syncGetCkmsGroupOpSign(groupId,accountList, CkmsGpEnDecryptManager.REMOVE_ENTITY);
                                                Log.d(TAG," syncGetCkmsGroupOpSign onSuccess opSign "+opSign);
                                                doCkmsGroupHttpCallback(groupId,accountList,(ArrayList<String>) accountList,CKMS_GROUP_ENTITY_REMOVE,opSign);
                                            }
                                            return null;
                                        }
                                    }.execute();
                                }*/
                                //[E]remove by tangsha@1104 for quite not do ckms action
                                /*end:add by wal@xdja.com for ckms add group child 2016/08/02*/
                            }
                        }
                    } catch (Exception e) {
                        new ATJsonParseException(e);
                    }
                }
                //保存新添加的群组新成员信息
                insertGroupMember(createSelections);
                ContactUtils.sendFailAddMemberEvent(outRangeNames,notAccountNames,groupId);//modify by wal@xdja.com for不能入群的事件通知
                ContactUtils.sendMemberInGroupEvent(inGroupNames,groupId);//add by wal@xdja.com for 已在群组中的账号事件通知
                createSGroupResultCallback.onTaskBackgroundOk(SGROUP_ACTION_OK,null,null,null);
            }

            @Override
            public void onErr() {
                LogUtil.getUtils().e(TAG+" add user to group onErr ...");
                createSGroupResultCallback.onTaskPostExec(SGROUP_ACTION_FAIL,null);
            }
        });
    }

    /**
     * 保存新添加的群成员信息
     */
    private void insertGroupMember(List<String> accounts){
        List<GroupMember> members = new ArrayList<>();
        buildGroupMemberList(accounts, members);
        //members
        GroupInternalService.getInstance().batchSaveOrUpdateGroupMembers(members);
        //如果群组未命名，UI收到事件后需要更新群组名称（由成员昵称组成）
        if (members != null && !members.isEmpty()) {
            FireEventUtils.fireAddMemberEvent(members);
        }
    }

    private void buildGroupMemberList(List<String> accounts,List<GroupMember> members){
        ActomAccountService actomService = new ActomAccountService();
        long createTime = System.currentTimeMillis();
        AvaterService service = new AvaterService();
        //保存新添加的成员
        for (String account : accounts) {
            ActomaAccount actomaAccount = actomService.queryByAccount(account);
            //start:add by by wal@xdja.com for 3586
            if(ObjectUtil.objectIsEmpty(actomaAccount)){
                actomaAccount = new ActomaAccount();
                actomaAccount.setAccount(account);
            }
            //end:add by by wal@xdja.com for 3586
            GroupMember member = new GroupMember(true);
            member.setAccount(account);
            member.setCreateTime(String.valueOf(createTime));
            member.setAvatar(service.queryByAccount(actomaAccount.getAccount()));
            member.setGroupId(groupId);
            member.setUpdateSerial("0"); //server没有返回更新序列，所以保持0，下次同步时再更新
            member.setInviteAccount(GroupUtils.getCurrentAccount(activity));
            members.add(member);
        }
    }

}
