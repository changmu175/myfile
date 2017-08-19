package com.xdja.contact.task.group;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.https.HttpsRequest;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.util.ContactUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.ErrorPush;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.IPullCallback;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.exception.ATCipherException;
import com.xdja.contact.exception.ATHttpResponseException;
import com.xdja.contact.exception.ATJsonBodyNullException;
import com.xdja.contact.exception.ATJsonParseException;
import com.xdja.contact.http.request.group.GroupMemberRequest;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.http.response.group.ResponseGroup;
import com.xdja.contact.http.response.group.ResponseGroupMember;
import com.xdja.contact.http.response.group.ResponseGroupMembers;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.IHttpParams;
import com.xdja.contact.http.wrap.params.account.BatchAccessAccountParam;
import com.xdja.contact.http.wrap.params.group.IncrementGroupMembersParam;
import com.xdja.contact.http.wrap.params.group.IncrementGroupsParams;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.ErrorPushService;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupExternalService;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.comm.contacttask.ContactAsyncTask;
import com.xdja.comm.contacttask.ITask;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wanghao on 2016/1/19.
 * 增量更新所有群组
 */
public class TaskIncrementGroup extends ContactAsyncTask<Void,Void,Map<String,HttpsRequstResult>> implements ITask{

    private IPullCallback groupUiCallback;

    private String serviceType;

    private Context context;

    private Map<String,HttpsRequstResult> resultMap = new HashMap<>();

    private static final String AT_RESPONSE_KEY = "response_key";

    private static final String CIPHER_KEY = "cipher_key";

    private static final String JSON_BODY_NULL_KEY = "json_body_null";

    private static final String JSON_PARSE_KEY = "json_parse_key";

    private static final String GROUP_KEY = "group_key";

    private static final String GROUP_MEMBER_KEY = "group_member_key";

    private static final String ACCOUNT_KEY = "account_key";

    private boolean showErrorToast = false;

    private HttpErrorBean httpErrorBean;
    private List<GroupMember> allMembers = new ArrayList<>();
    private List<GroupMember> addMemebers = new ArrayList<>();
    private List<GroupMember> deleteMembers = new ArrayList<>();
    private List<GroupMember> updateMembers = new ArrayList<>();
    private Map<String,String> groupMemberMap;
    private Map<String,Group> groupMap;
    private Map<String,Group> accountGroupMap;
    private Map<String,String> accountUpdateSerialMap;
    private Map<String,String> accountDeleteSerialMap;
    private Map<String,String> accountGroupMemberMap;
    private Set<String> accountsSet = new HashSet<>();
    private List<Group> addGroups = new ArrayList<>();
    private List<Group> deleteGroups = new ArrayList<>();
    private List<Group> updateGroups = new ArrayList<>();
    private List<Group> allGroups = new ArrayList<>();
    //[S]tangsha for 8110
    private List<Group> groupsNoMemberInfo = new ArrayList<>();
    //[E]tangsha for 8110
    private int FLAG_FIRE_GROUP_NAME_EVENT= 1;
    private int FLAG_FIRE_GROUP_EVENT = 2;
    private int FLAG_FIRE_GROUP_ACCOUNT_EVENT = 4;
    private int FLAG_FIRE_EVENT_COUNT = 3;
    private int eventFlag = 0;

    public TaskIncrementGroup(){
        this.context = ActomaController.getApp();
    }

    public TaskIncrementGroup(String serviceType){
        this();
        this.serviceType = serviceType;
    }

    public TaskIncrementGroup(IPullCallback groupUiCallback){
        this();
        this.groupUiCallback = groupUiCallback;
    }

    @Override
    protected Map<String,HttpsRequstResult> doInBackground(Void... params) {
        //首先增量所有的群信息
        //根据增量的群信息过滤对应的群id，调用服务器增量所对应的群成员信息
        //根据成员信息账号增量所对应的账户信息
        HttpsRequstResult httpsRequstResult = null;
        try {
            groupsNoMemberInfo = GroupInternalService.getInstance().queryGroupNoMemberInfo();
            LogUtil.getUtils().d("TaskIncrementGroup groupsNoMemberInfo size "+groupsNoMemberInfo.size());
            if(isCancelled() == false) {
                httpsRequstResult = filterGroupsResult();
                if (httpsRequstResult.result == HttpResultSate.SUCCESS && isCancelled() == false) {
                    List<ResponseGroup> responseGroups = parseResponseGroups(httpsRequstResult);
                    if (ObjectUtil.collectionIsEmpty(responseGroups)) {
                        if (groupsNoMemberInfo.isEmpty()) {
                            resultMap.put(GROUP_KEY, httpsRequstResult);
                        } else {
                            httpsRequstResult = getGroupMembers();
                        }
                    } else {
                        httpsRequstResult = filterGroupsResult(responseGroups);
                    }
                    if (resultMap.get(GROUP_KEY) == null) {
                        if (httpsRequstResult.result == HttpResultSate.SUCCESS) {
                            parseResponseGroupMembers(httpsRequstResult);
                            if (ObjectUtil.collectionIsEmpty(allMembers)) {
                                resultMap.put(GROUP_MEMBER_KEY, httpsRequstResult);
                            } else {
                                httpsRequstResult = filterGroupMembersResult();
                                resultMap.put(ACCOUNT_KEY, httpsRequstResult);
                            }
                        } else {
                            resultMap.put(GROUP_MEMBER_KEY, httpsRequstResult);
                        }
                    }
                } else {
                    resultMap.put(GROUP_KEY, httpsRequstResult);
                }
            }
        } catch (ATHttpResponseException e) {
            resultMap.put(AT_RESPONSE_KEY,httpsRequstResult);
        } catch (ATCipherException e){
            resultMap.put(CIPHER_KEY,httpsRequstResult);
        } catch (ATJsonBodyNullException e){
            resultMap.put(JSON_BODY_NULL_KEY,httpsRequstResult);
        }catch (ATJsonParseException e){
            resultMap.put(JSON_PARSE_KEY,httpsRequstResult);
        }finally {
		    //[S]modify by tangsha@20161101 for 5748
            processResult(resultMap);
			//[E]modify by tangsha@20161101 for 5748
            TaskManager.getInstance().removeTask(this);
            return resultMap;
        }
    }

    @Override
    protected void onPostExecute(Map<String,HttpsRequstResult> resultMap) {
        super.onPostExecute(resultMap);
        //通知界面ui 群组loading 停止刷新
        if(!ObjectUtil.objectIsEmpty(groupUiCallback) && groupUiCallback.isSupportLoading()){
            groupUiCallback.stopRefreshLoading();
        }
		//[S]modify by tangsha@20161101 for 5748
       /* if((eventFlag & FLAG_FIRE_GROUP_NAME_EVENT) == FLAG_FIRE_GROUP_NAME_EVENT){
            processFlagFireGroupName();
        }*/
        if((eventFlag & FLAG_FIRE_GROUP_EVENT) == FLAG_FIRE_GROUP_EVENT){
            processFlagFireGroup();
        }
        if((eventFlag & FLAG_FIRE_GROUP_ACCOUNT_EVENT) == FLAG_FIRE_GROUP_ACCOUNT_EVENT){
           processFlagAccountMember();
        }
        if(showErrorToast){
            //Start:add by wal@xdja.com for 3991
            if(!ObjectUtil.objectIsEmpty(groupUiCallback) && groupUiCallback.isSupportLoading()){
                groupUiCallback.onShowErrorToast(null);
            }
            //End:add by wal@xdja.com for 3991
        }
		//[E]modify by tangsha@20161101 for 5748
        TaskManager.getInstance().removeTask(this);//add by lwl 去重 3257
    }

    private void processResult(Map<String,HttpsRequstResult> resultMap){
        if(isCancelled()){
            return;
        }
        if(!ObjectUtil.objectIsEmpty(resultMap.get(AT_RESPONSE_KEY))){
            saveOrUpdateErrorPush();
        }else if(!ObjectUtil.objectIsEmpty(resultMap.get(CIPHER_KEY))){
            saveOrUpdateErrorPush();
        }else if(!ObjectUtil.objectIsEmpty(resultMap.get(JSON_BODY_NULL_KEY))){
            saveOrUpdateErrorPush();
        }else if(!ObjectUtil.objectIsEmpty(resultMap.get(JSON_PARSE_KEY))){
            saveOrUpdateErrorPush();
        }else if(!ObjectUtil.objectIsEmpty(resultMap.get(GROUP_KEY))){
            HttpsRequstResult httpsRequstResult = resultMap.get(GROUP_KEY);
            if(httpsRequstResult.result == HttpResultSate.FAIL){
                httpErrorBean = httpsRequstResult.httpErrorBean;
                LogUtil.getUtils().e("Actoma contact TaskIncrementGroup onPostExecute(): Group增量群出错");
                saveOrUpdateErrorPush();
            }else{
                LogUtil.getUtils().e("Actoma contact TaskIncrementGroup onPostExecute(): Group暂时未增量到群组数据");
            }
        }else if(!ObjectUtil.objectIsEmpty(resultMap.get(GROUP_MEMBER_KEY))){
            HttpsRequstResult httpsRequstResult = resultMap.get(GROUP_MEMBER_KEY);
            if(httpsRequstResult.result == HttpResultSate.FAIL || isCancelled()){
                httpErrorBean = httpsRequstResult.httpErrorBean;
                LogUtil.getUtils().e("Actoma contact TaskIncrementGroup onPostExecute(): GroupMember根据群成员增量群成员出错");
                saveOrUpdateErrorPush();
            }else{
                //看看这里的数据
                if(ObjectUtil.collectionIsEmpty(allMembers)){
                    //增量的只是群组数据
                    //群名称或者头像变更 注意添加区别
                    GroupExternalService externalService = new GroupExternalService(ActomaController.getApp());
                    groupMemberMap = externalService.queryDeleteGroupMembers();
                    groupMap = externalService.getGroupSerialIsZeroMap();
                    //eventFlag = eventFlag | FLAG_FIRE_GROUP_NAME_EVENT;
                    processFlagFireGroupName();//this must before update local data
                    boolean bool = GroupInternalService.getInstance().batchSaveOrUpdateGroups(allGroups);
                    if(bool && !ObjectUtil.stringIsEmpty(serviceType)){
                        eventFlag = eventFlag | FLAG_FIRE_GROUP_EVENT;
                        deleteErrorPush();
                    }else{
                        if(bool){
                            ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_GROUP_DOWNLOAD_SUCCESS));
                        }
                    }
                }
            }
        }else if(!ObjectUtil.objectIsEmpty(resultMap.get(ACCOUNT_KEY))){
            HttpsRequstResult httpsRequstResult = resultMap.get(ACCOUNT_KEY);
            if(httpsRequstResult.result == HttpResultSate.FAIL || isCancelled()){
                httpErrorBean = httpsRequstResult.httpErrorBean;
                LogUtil.getUtils().e("Actoma contact TaskIncrementGroup onPostExecute(): GroupMember----->accounts账户出错");
                saveOrUpdateErrorPush();
            }else{
                List<ResponseActomaAccount> responseAccountInfos = new ArrayList<>();
                String body = httpsRequstResult.body;
                if(ObjectUtil.stringIsEmpty(body)){
                    LogUtil.getUtils().e("Actoma contact TaskIncrementGroup onPostExecute(): 增量群组,根据帐号下载账户数据json body is null");
                    saveOrUpdateErrorPush();
                }else{
                    try {
                        responseAccountInfos = JSON.parseArray(body, ResponseActomaAccount.class);
                        saveOrUpdateLocalData(responseAccountInfos);
                        deleteErrorPush();
                    }catch (Exception e){
                        LogUtil.getUtils().e("Actoma contact TaskIncrementGroup onPostExecute(): 增量群组,根据帐号下载账户数据json解析服务器返回群组增量出错");
                        saveOrUpdateErrorPush();
                    }
                }
            }
        }
    }

    private void processFlagFireGroupName(){
        //add by lwl start 2087
        for (int i = 0; i <allGroups.size() ; i++) {
            Group group=allGroups.get(i);
			//[S]modify by tangsha@20161228 for 7490
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(group.getGroupId());
            stringBuffer.append("#");
            String deleteKey = stringBuffer.toString()+ContactUtils.getCurrentAccount();
            if(groupMemberMap.get(deleteKey) == null) {
                FireEventUtils.fireGroupNameUpdateEvent(group.getGroupId(), group.getGroupName());
            }else{
                LogUtil.getUtils().e("Actoma contact TaskIncrementGroup processFlagFireGroupName has delete "+deleteKey);
            }
			//[E]modify by tangsha@20161228 for 7490
        }
        //add by lwl end 2087
    }

    private void processFlagFireGroup(){
        FireEventUtils.pushFireAddGroupEvent(groupMap, addGroups);
        FireEventUtils.pushFireDismissGroupEvent(groupMemberMap, deleteGroups);
        FireEventUtils.pushFireUpdateGroupList(updateGroups);
    }

    private void processFlagAccountMember(){
        GroupExternalService externalService = new GroupExternalService(ActomaController.getApp());
        Map<String, String> finalDeleteGroupMember = externalService.queryDeleteGroupMembers();
        FireEventUtils.pushFireAddGroupEvent(accountGroupMap, addGroups);
        FireEventUtils.pushFireDismissGroupEvent(accountGroupMemberMap, deleteGroups);
        //群名称或者头像变更 注意添加区别
        //FireEventUtils.fireUpdateGroupEvent(GroupConvert.updateGroups, null);
        FireEventUtils.pushFireUpdateGroupList(updateGroups);
		//[S]modify by tangsha@20161228 for 7490
        FireEventUtils.pushFireAddMemberEvent(accountUpdateSerialMap, addMemebers, finalDeleteGroupMember);
		//[E]modify by tangsha@20161228 for 7490
        FireEventUtils.pushFireDeleteMemberEvent(accountDeleteSerialMap, deleteMembers);
        FireEventUtils.fireUpdateMemberEvent(updateMembers, null);
    }

    private void saveOrUpdateLocalData(List<ResponseActomaAccount> responseAccountInfos){
        ActomAccountService accountService = new ActomAccountService();
        boolean bool = accountService.batchSaveAccountsAssociateWithAvatar(responseAccountInfos);
        if(bool){
            ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_ACCOUNT_DOWNLOAD_SUCCESS));//add by wal@xdja.com for 4698
            accountUpdateSerialMap = GroupInternalService.getInstance().getSerialIsZeroMap();
            accountDeleteSerialMap = GroupInternalService.getInstance().getDeletedGroupMemberMap();
            GroupExternalService externalService = new GroupExternalService(ActomaController.getApp());
            accountGroupMemberMap = externalService.queryDeleteGroupMembers();
            boolean resultMembers = GroupInternalService.getInstance().batchSaveOrUpdateGroupMembers(allMembers);
            if(resultMembers){
                accountGroupMap = externalService.getGroupSerialIsZeroMap();
                boolean resultGroups = GroupInternalService.getInstance().batchSaveOrUpdateGroups(allGroups);
                if(resultGroups){
                    //通知小青.
                    if(!ObjectUtil.stringIsEmpty(serviceType)) {
                       eventFlag = eventFlag | FLAG_FIRE_GROUP_ACCOUNT_EVENT;
                    }else{
                        LogUtil.getUtils().i("登录时群组执行增量");
                        ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_GROUP_DOWNLOAD_SUCCESS));
                        LogUtil.getUtils().i("增量群完成通知界面刷新");
                    }
                }else {
                    saveOrUpdateErrorPush();
                }
            }else{
                saveOrUpdateErrorPush();
            }
        }else{
            saveOrUpdateErrorPush();
        }

    }



    /**
     *
     * @return
     * @throws ATHttpResponseException
     */
    private HttpsRequstResult filterGroupsResult() throws ATHttpResponseException {
        GroupInternalService internalService = GroupInternalService.getInstance();
        String updateSerial = internalService.queryMaxUpdateSerial();
        IncrementGroupsParams groupListParams = new IncrementGroupsParams(updateSerial);
        HttpsRequstResult httpsRequstResult = new HttpRequestWrap().synchronizedRequest(groupListParams);
        HttpsRequest.checkTicketError(httpsRequstResult, !isCancelled(),getTaskId());
        if(ObjectUtil.objectIsEmpty(httpsRequstResult)){
            LogUtil.getUtils().e("Actoma contact TaskIncrementGroup filterGroupsResult(): 增量群组response is null");
            throw new ATHttpResponseException();
        }else{
            return httpsRequstResult;
        }
    }

    /**
     * 解析返回的群组数据
     * @param httpsRequstResult
     * @return
     */
    private List<ResponseGroup> parseResponseGroups(HttpsRequstResult  httpsRequstResult) throws ATJsonBodyNullException, ATJsonParseException {
        List<ResponseGroup> responseGroups = new ArrayList<>();
        if(httpsRequstResult.result == HttpResultSate.SUCCESS){
            String body = httpsRequstResult.body;
            if(ObjectUtil.stringIsEmpty(body)){
                throw new ATJsonBodyNullException();
            }else{
                try {
                    responseGroups = JSON.parseArray(body, ResponseGroup.class);
                }catch (Exception e){
                    LogUtil.getUtils().e("Actoma contact TaskIncrementGroup parseResponseGroups:json解析服务器返回群组增量出错");
                    throw new ATJsonParseException();
                }
            }
        }
        return responseGroups;
    }

    /**
     * 增量群成员信息
     * @param responseGroups
     * @return
     * @throws ATJsonParseException
     * @throws ATCipherException
     * @throws ATJsonBodyNullException
     */
    private HttpsRequstResult filterGroupsResult(List<ResponseGroup> responseGroups) throws ATJsonParseException, ATCipherException, ATJsonBodyNullException {
        filterResponseGroup(responseGroups);
        return getGroupMembers();
    }

    private HttpsRequstResult getGroupMembers(){
        //根据增量的群id执行群成员下载
        if(isCancelled() == false) {
            IHttpParams httpParams = buildIncrementGroupMembersParam();
            HttpsRequstResult httpsRequstResult =  new HttpRequestWrap().synchronizedRequest(httpParams);
            HttpsRequest.checkTicketError(httpsRequstResult, !isCancelled(), getTaskId());
            return httpsRequstResult;
        }else{
            return null;
        }
    }
    /**
     * 解析群成员
     * @param httpsRequstResult
     */
    private List<ResponseGroupMembers> parseResponseGroupMembers(HttpsRequstResult  httpsRequstResult) throws ATJsonBodyNullException, ATJsonParseException {
        List<ResponseGroupMembers> responseGroupMembers = new ArrayList<>();
        if(httpsRequstResult.result == HttpResultSate.SUCCESS){
            String body = httpsRequstResult.body;
            if(ObjectUtil.stringIsEmpty(body)){
                LogUtil.getUtils().e("Actoma contact TaskIncrementGroup parseResponseGroupMembers:服务器返回群成员body体出错");
                throw new ATJsonBodyNullException();
            }else{
                try {
                    responseGroupMembers = JSON.parseArray(body, ResponseGroupMembers.class);
                    filterResponseGroupMember(responseGroupMembers);
                }catch (Exception e){
                    LogUtil.getUtils().e("Actoma contact TaskIncrementGroup parseResponseGroupMembers:json解析服务器返回群成员增量出错");
                    throw new ATJsonParseException();
                }
            }
        }
        return responseGroupMembers;
    }


    /**
     *
     * @return
     * @throws ATJsonParseException
     * @throws ATCipherException
     * @throws ATJsonBodyNullException
     */
    private HttpsRequstResult filterGroupMembersResult() throws ATJsonParseException, ATCipherException, ATJsonBodyNullException, ATHttpResponseException {
        HttpsRequstResult httpsRequstResult = new HttpRequestWrap().synchronizedRequest(buildBatchAccountInfosParam());
        HttpsRequest.checkTicketError(httpsRequstResult, !isCancelled(), getTaskId());
        if(ObjectUtil.objectIsEmpty(httpsRequstResult)){
            LogUtil.getUtils().e("Actoma contact TaskIncrementGroup filterGroupMembersResult:增量群组,根据帐号下载账户数据response is null");
            throw new ATHttpResponseException();
        }else {
            return httpsRequstResult;
        }
    }



    protected boolean saveOrUpdateErrorPush(){
        showErrorToast = true;
        ErrorPushService errorPushService = new ErrorPushService(context);
        ErrorPush errorPush = new ErrorPush();
        errorPush.setTransId(getTaskId());
        errorPush.setCreateTime(String.valueOf(System.currentTimeMillis()));
        errorPush.setReason(getReason());
        errorPush.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        return errorPushService.saveOrUpdate(errorPush);
    }

    @Override
    public String getTaskId() {
        if(ObjectUtil.stringIsEmpty(serviceType)){
            return INCREMENT_GROUP_TASK;
        }
        //Start:add by wal@xdja.com for 3859
//        return this.serviceType;
        return PUSH_INCREMENT_GROUP_TASK;
        //End:add by wal@xdja.com for 3859
    }

    @Override
    public String getReason() {
        if(!ObjectUtil.objectIsEmpty(httpErrorBean)){
            return String.format(context.getString(R.string.push_error_group), httpErrorBean.getMessage(), httpErrorBean.getStatus());
        }
        return "增量群组业务,解析数据出错了";
    }

    protected void deleteErrorPush(){
        ErrorPushService service = new ErrorPushService(context);
        ErrorPush errorPush = new ErrorPush();
        errorPush.setTransId(getTaskId());
        service.delete(errorPush);
    }

    @Override
    public void template() {
        if(TaskManager.getInstance().isIncludeTaskPool(this)){//add by lwl 去重 3257
            if(groupUiCallback!=null)
                groupUiCallback.stopRefreshLoading();
            return;
        }

        TaskManager.getInstance().putTask(this);
        execute();
    }

    public void filterResponseGroup(List<ResponseGroup> responseGroups){
        for (ResponseGroup responseGroup : responseGroups) {
            if(responseGroup.getStatus().equals(Group.ADD)){
                addGroups.add(GroupConvert.responseGroup2Group(responseGroup));
            }else if(responseGroup.getStatus().equals(Group.MODIFY)){
                updateGroups.add(GroupConvert.responseGroup2Group(responseGroup));
            }else{
                deleteGroups.add(GroupConvert.responseGroup2Group(responseGroup));
            }
        }
        allGroups.addAll(addGroups);
        allGroups.addAll(deleteGroups);
        allGroups.addAll(updateGroups);
    }

    /**
     * 根据群id查询群成员内对应的最大的成员序列
     * @return
     */
    public IHttpParams buildIncrementGroupMembersParam(){
        List<String> groupIds = new ArrayList<>();
        for (Group group : allGroups) {
            groupIds.add(group.getGroupId());
        }
        //[S]tangsha add@20170117 for 8110
        for(Group groupInfo : groupsNoMemberInfo){
            String groupId = groupInfo.getGroupId();
            boolean hasAdd = groupIds.contains(groupId);
            LogUtil.getUtils().d("TaskIncrementGroup groupsNoMemberInfo groupId "+groupId+" hasAdd "+hasAdd);
            if(hasAdd == false) {
                groupIds.add(groupId);
            }
        }
        //[E]tangsha add@20170117 for 8110
        Map<String,Long> serialMap = GroupInternalService.getInstance().getGroupMemberMaxSerialByIds(groupIds);
        List<GroupMemberRequest> groupMemberRequest = new ArrayList<GroupMemberRequest>();
        if(ObjectUtil.mapIsEmpty(serialMap) && !ObjectUtil.collectionIsEmpty(groupIds)){
            for (String groupId : groupIds) {
                GroupMemberRequest request = new GroupMemberRequest();
                request.setGroupId(groupId);
                request.setSeq("0");
                groupMemberRequest.add(request);
            }
        }else{
            for (String groupId : serialMap.keySet()) {

                GroupMemberRequest request = new GroupMemberRequest();
                request.setGroupId(groupId);
                request.setSeq(String.valueOf(serialMap.get(groupId)));
                groupMemberRequest.add(request);
            }
        }
        return new IncrementGroupMembersParam(groupMemberRequest, null);
    }

    /**
     * 过滤服务器返回的群成员数据
     * @param responseGroupMembers
     */
    public void filterResponseGroupMember(List<ResponseGroupMembers> responseGroupMembers){
        //这里返回数据结构体就这样暂时这样循环
        for (ResponseGroupMembers syncData : responseGroupMembers) {
            String groupId = syncData.getGroupId();
            for (ResponseGroupMember member : syncData.getMembers()) {
                GroupMember tempMember = GroupConvert.convertSyncDataToMember(member, groupId);
                accountsSet.add(tempMember.getAccount());
                if(tempMember.getStatus().equals(GroupMember.ADD)){
                    addMemebers.add(tempMember);
                }else if(tempMember.getStatus().equals(GroupMember.MODIFY)){
                    updateMembers.add(tempMember);
                }else{
                    deleteMembers.add(tempMember);
                }
            }
        }
        allMembers.addAll(addMemebers);
        allMembers.addAll(deleteMembers);
        allMembers.addAll(updateMembers);
    }

    /**
     * 组装生成批量请求账户信息的请求体
     * @return
     */
    public IHttpParams buildBatchAccountInfosParam(){
        /*BatchAccessAccountBody request = new BatchAccessAccountBody();
        request.setAccounts(new ArrayList<String>(accountsSet));*/
        return new BatchAccessAccountParam(accountsSet);
    }
}
