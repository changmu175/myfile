package com.xdja.contact.task.ckms;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.exception.ATJsonParseException;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.http.request.group.CreateGroupBody;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wal@xdja.com on 2016/8/8.
 */
public class CreateSGroupTask extends ContactAsyncTask<ArrayList<String>,Integer,Integer> {
    private String TAG = "CreateSGroupTask";
    private Activity activity;
    private String currentAccount;
    private ArrayList<String> accountsList;
    private String groupId;
    private CreateSGroupResultCallback createSGroupResultCallback;
    private String createGroupName;
    public static final int CREATE_SGROUP_OK = 0;
    public static final int CREATE_SGROUP_FAIL = -1;

    public CreateSGroupTask(Activity activity,String groupId,
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
        doCreateGroup(accountsList);
        return CREATE_SGROUP_OK;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        createSGroupResultCallback.onTaskPostExec(CREATE_SGROUP_OK, null);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        createSGroupResultCallback.onTaskProgress(0);
    }

    ResponseCreateGroup respGroup;
    List<String> outRangeNames = new ArrayList<String>();
    List<String> notAccountNames = new ArrayList<String>();

    private void doCreateGroup(final ArrayList<String> createSelections) {
        CreateGroupBody reqestCreateBean = new CreateGroupBody();
        reqestCreateBean.setGroupName(constructGroupName(createSelections));
        reqestCreateBean.setMembers(createSelections);
        GroupHttpServiceHelper.createGroup(reqestCreateBean, new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean errorBean) {
                LogUtil.getUtils().e(TAG+" create group failed ...");
                createSGroupResultCallback.onTaskPostExec(CREATE_SGROUP_FAIL,errorBean);
            }

            @Override
            public void onSuccess(String body) {
                LogUtil.getUtils().d(TAG+" create group success ...");
                try {
                    respGroup = JSON.parseObject(body, ResponseCreateGroup.class);
                    Map<String, List<String>> blockAccounts = respGroup.getBlockAccounts();
                    if (!ObjectUtil.mapIsEmpty(blockAccounts)) {
                        List<String> rangeAccounts = blockAccounts.get(ResponseCreateGroup.OUT_OF_GROUP_RANGE);
                        List<String> notAccounts = blockAccounts.get(ResponseCreateGroup.ACCOUNT_NOT_EXIST);
                        //List<String> memberInGroups = blockAccounts.get(ResponseCreateGroup.MEMBER_EXIST_IN_GROUP);
                        if (!ObjectUtil.collectionIsEmpty(rangeAccounts)) {
                            //start:add by wal@xdja.com for 2575
                            outRangeNames = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("", rangeAccounts);
                            createSelections.removeAll(rangeAccounts);
                            //end:add by wal@xdja.com for 2575
                        }
                        if (!ObjectUtil.collectionIsEmpty(notAccounts)) {
                            notAccountNames = new GroupExternalService(ActomaController.getApp()).queryDisplayNameByAccounts("", notAccounts); //add by wal@xdja.com for 2575
                        }
                    }
                    //start:add by wal@xdja.com for ckms
                    if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
                        new ContactAsyncTask<Void,Void,Void>(){

                            @Override
                            protected Void doInBackground(Void... params) {
                                List<String> accountList= new ArrayList<String>();
                                accountList.addAll(createSelections);
                                accountList.add(currentAccount);
                                String opSign =  GroupHttpServiceHelper.syncGetCkmsGroupOpSign(respGroup.getGroupId()+"",accountList,CkmsGpEnDecryptManager.CREATE_GROUP);
                                doCkmsGroupHttpCallback(respGroup.getGroupId()+"",accountList,opSign);
                                return null;
                            }
                        }.execute();
                    }
                    //end:add by wal@xdja.com for ckms
                    //保存群成员和群组
                    insertNewGroupMember(createSelections);//add by wal@xdja.com for 2575
                    ContactUtils.sendFailAddMemberEvent(outRangeNames,notAccountNames,groupId);//modify by wal@xdja.com for不能入群的事件通知
                    createSGroupResultCallback.onTaskBackgroundOk(CREATE_SGROUP_OK,outRangeNames,notAccountNames, groupId);
                } catch (Exception e) {
                    LogUtil.getUtils().e(TAG+" doCreateGroup onSuccess "+e.toString());
                    createSGroupResultCallback.onTaskPostExec(CREATE_SGROUP_FAIL,null);
                    new ATJsonParseException(e);
                }
            }

            @Override
            public void onErr() {
                createSGroupResultCallback.onTaskPostExec(CREATE_SGROUP_FAIL,null);
            }
        });
    }

    /*start:add by wal@xdja.com for ckms create group 2016/08/02*/
    private int doCkmsGroupHttpCallback (String groupID,List<String> accountList,String opSign){
        final String currentAccount = ContactUtils.getCurrentAccount();
        LogUtil.getUtils().d("CreateSGroupTask doCkmsGroupHttpCallback onSuccess opSign "+opSign);
        int flagCode = 0;
        CkmsGpEnDecryptManager.createSgroup(currentAccount,groupID,accountList,opSign);
        return flagCode;
    }
    /*end:add by wal@xdja.com for ckms create group 2016/08/02*/
    private final int GROUP_NAME_MAX_CONTACT_NUM = 3;  //add ysp@xdja.com
    private String constructGroupName(List<String> selectAccountList){
        ActomAccountService service = new ActomAccountService();
        //modify by lwl start groupname  order
        List<ActomaAccount> actomaTemp = service.findAccountsByIds(selectAccountList);
        Map<String,ActomaAccount> actomaTempMap = new HashMap<>();
        List<ActomaAccount> actomaAccounts =new  ArrayList();
        int selectSize = selectAccountList.size();
        int actomaTempSize = actomaTemp.size();
        if(selectSize != actomaTempSize){
            LogUtil.getUtils().e(TAG+" constructGroupName selectSize "+selectSize+" actomaTempSize "+actomaTempSize);
        }
        for (int i = 0; i < selectSize && i < actomaTempSize; i++) {
            actomaTempMap.put(actomaTemp.get(i).getAccount(),actomaTemp.get(i));
        }
        for (int j = 0; j <selectSize && j < actomaTempMap.size() ; j++) {
            if(!ObjectUtil.objectIsEmpty(actomaTempMap.get(selectAccountList.get(j))))
                actomaAccounts.add(actomaTempMap.get(selectAccountList.get(j)));
        }
        //modify by lwl end
        int countAccount = actomaAccounts.size();
        StringBuffer groupNameBuffer = new StringBuffer();
        AccountBean bean = ContactUtils.getCurrentBean();
        if(!ObjectUtil.objectIsEmpty(bean) && !ObjectUtil.stringIsEmpty(bean.getNickname())){
            groupNameBuffer.append(bean.getNickname());
            groupNameBuffer.append("、");
        }else if(!ObjectUtil.objectIsEmpty(bean) && !ObjectUtil.stringIsEmpty(bean.getAlias())){//2361 add by lwl
            groupNameBuffer.append(bean.getAlias());
            groupNameBuffer.append("、");
        } else {
            groupNameBuffer.append(ContactUtils.getCurrentAccount());
            groupNameBuffer.append("、");
        }

        //[S] modify by ysp@xdja.com fix bug 5156 review wal 2016.10.24
        if(countAccount > GROUP_NAME_MAX_CONTACT_NUM - 1){
            List<ActomaAccount> subList = actomaAccounts.subList(0, GROUP_NAME_MAX_CONTACT_NUM - 1);
            buildGroupName(subList, groupNameBuffer);
        }else{
            buildGroupName(actomaAccounts, groupNameBuffer);
        }
        //[E] modify by ysp@xdja.com fix bug 5156 review wal 2016.10.24
        this.createGroupName = groupNameBuffer.toString();
        if(createGroupName.length() > 50){
            createGroupName = createGroupName.substring(0,50);
        }
        return createGroupName;
    }


    /**
     * 通过传递账户数据构建群名称
     * @param actomaAccounts
     * @param groupNameBuffer
     */
    private void buildGroupName(List<ActomaAccount> actomaAccounts, StringBuffer groupNameBuffer) {
        int length = actomaAccounts.size();
        for(int i = 0 ; i < length ; i ++){
            ActomaAccount actomaAccount = actomaAccounts.get(i);
            String nickname = actomaAccount.getNickname();
            if(!ObjectUtil.stringIsEmpty(nickname)){
                groupNameBuffer.append(nickname);

            }else if(!ObjectUtil.stringIsEmpty(actomaAccount.getAlias())){
                groupNameBuffer.append(actomaAccount.getAlias());
            }else if(!ObjectUtil.stringIsEmpty(actomaAccount.getAccount())) {
                groupNameBuffer.append(actomaAccount.getAccount());
            }
            if (i != (length - 1)) {
                groupNameBuffer.append("、");
            }
        }
    }


    /**
     * 保存新建的群组信息
     */
    private String membersSeq;
    private Group group;
    private void insertNewGroupMember(List<String> addMemberInfos) {
        membersSeq = respGroup.getMemberSeq() + "";
        group = GroupUtils.convertCreateGroupBean(respGroup);
        this.groupId = group.getGroupId();
        group.setGroupOwner(ContactUtils.getCurrentAccount());
        group.setUpdateSerial("0");//server没有返回更新序列，所以保持0，下次同步时再更新
        group.setGroupName(createGroupName);
        List<GroupMember> members = new ArrayList<GroupMember>();
        buildGroupMemberList(addMemberInfos,members);
        //不要忘记自己
        members.add(GroupUtils.genFromCurrentUser(activity, group.getGroupId(), System.currentTimeMillis() + ""));
        GroupInternalService.getInstance().batchSaveOrUpdateGroupMembers(members);
        //保存群组
        GroupInternalService.getInstance().insert(group);
        //tempId = group.getGroupId();
        //通知UI更新列表
        List<Group> addedGroup = new ArrayList<Group>();
        addedGroup.add(group);
        FireEventUtils.fireAddGroupEvent(addedGroup);
        //GroupInternalService.getInstance(this).fireGroupEvent(GroupUtils.getCurrentAccount(this),addedGroup, null, null);
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
            member.setGroupId(group.getGroupId());
            /*[S]modify by tangsha@20161220 for 6843,
               group member create time is current time when create group,
               should update as server time, or there is error when update with seq*/
            //member.setUpdateSerial(membersSeq);
            member.setUpdateSerial("0");
            member.setInviteAccount(currentAccount);
            /*[E]modify by tangsha@20161220 for 6843,
               group member create time is current time when create group,
               should update as server time, or there is error when update with seq*/
            members.add(member);
        }
    }

}
