package com.xdja.contact.http;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.data.CommonHeadBitmap;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.contact.bean.Group;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.exception.ATUpdateGroupNameException;
import com.xdja.contact.exception.ATUploadGroupAvatarException;
import com.xdja.contact.http.request.account.GetCkmsOpSignBody;
import com.xdja.contact.http.request.group.CreateGroupBody;
import com.xdja.contact.http.request.group.RemoveGroupMemberBody;
import com.xdja.contact.http.request.group.UpdateGroupNameBody;
import com.xdja.contact.http.request.group.UpdateNicknameBody;
import com.xdja.contact.http.request.group.UploadGroupAvatarBody;
import com.xdja.contact.http.response.group.ResponseCkmsOpSign;
import com.xdja.contact.http.wrap.HttpRequestWrap;
import com.xdja.contact.http.wrap.params.account.GetCkmsOperSignParam;
import com.xdja.contact.http.wrap.params.group.AddGroupMembersParams;
import com.xdja.contact.http.wrap.params.group.CreateGroupParams;
import com.xdja.contact.http.wrap.params.group.DismissGroupParams;
import com.xdja.contact.http.wrap.params.group.ExitFromGroupParams;
import com.xdja.contact.http.wrap.params.group.GetGroupInfoParams;
import com.xdja.contact.http.wrap.params.group.RemoveGroupMemberParam;
import com.xdja.contact.http.wrap.params.group.UpdateGroupAvatarParam;
import com.xdja.contact.http.wrap.params.group.UpdateGroupNameParams;
import com.xdja.contact.http.wrap.params.group.UpdateNicknameParam;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Created by wanghao on 2016/1/31.
 * 群组模块与服务端对接帮助对象
 * 集成当前群组功能内所有与服务对接的接口
 *</pre>
 */
public class GroupHttpServiceHelper {

    /**
     * 删除群成员
     * @param groupId
     * @param cb
     * @return
     */
    public static void delMemberFromGroup(String groupId, List<String> accounts, IModuleHttpCallBack cb) {

        RemoveGroupMemberBody request = new RemoveGroupMemberBody();

        for (String account : accounts) {
            RemoveGroupMemberBody.RemoveMembersInfo memberInfo =  request.new RemoveMembersInfo(account);
            request.addRequestParams(memberInfo);
        }

        new HttpRequestWrap().request(new RemoveGroupMemberParam(accounts, cb, groupId));


        //首先保证当前用户是群主
        //构建请求实体
       /* RemoveGroupMemberBody request = new RemoveGroupMemberBody();
        RemoveGroupMemberBody.RemoveMemberInfo memberInfo =  request.new RemoveMemberInfo(account);
        request.addRequestParams(memberInfo);
        //add by lwl 移除成员 start
        ArrayList accountList= new ArrayList<String>();
        accountList.add(account);
        //add by lwl 移除成员 end
        new HttpRequestWrap().request(new RemoveGroupMemberParam(accountList, cb, groupId));*/
    }

    /**
     * 上传群组头像
     * @param groupId  当前群id
     * @param bitmap 回调的头像数据
     * @param callBack 通知群组详情刷新
     * @throws ATUploadGroupAvatarException
     */
    public static void uploadGroupAvatar(String groupId, CommonHeadBitmap bitmap, IModuleHttpCallBack callBack) throws ATUploadGroupAvatarException {
        if(ObjectUtil.stringIsEmpty(groupId))return;
        UploadGroupAvatarBody request = new UploadGroupAvatarBody(bitmap);
        try {
            new HttpRequestWrap().request(new UpdateGroupAvatarParam(request, callBack, groupId));
        }catch (Exception e){
            throw new ATUploadGroupAvatarException();
        }
    }
    /**
     * 更新群名称
     * @param groupId
     * @param groupName
     * @param callBack
     * @throws ATUpdateGroupNameException
     */
    public static void updateGroupName(String groupId, String groupName, IModuleHttpCallBack callBack) throws ATUpdateGroupNameException {
        if(ObjectUtil.stringIsEmpty(groupId) || ObjectUtil.stringIsEmpty(groupName)){
            throw new ATUpdateGroupNameException();
        }
        UpdateGroupNameBody request = new UpdateGroupNameBody();
        request.setGroupName(groupName);
        try {
            new HttpRequestWrap().request(new UpdateGroupNameParams(request, callBack, groupId));
        }catch (Exception e){
            throw new ATUpdateGroupNameException();
        }
    }

    /**
     * 修改自己在群组的昵称
     * @param groupId  群组ID
     * @param newName  新的昵称
     * @param callBack http响应回调
     * @throws ATUpdateGroupNameException
     */
    public static void updateNickName(String groupId, String newName, IModuleHttpCallBack callBack) throws ATUpdateGroupNameException {
        if(ObjectUtil.stringIsEmpty(groupId)){
            throw new ATUpdateGroupNameException();
        }
        UpdateNicknameBody request = new UpdateNicknameBody();
        request.setNickname(newName);
        new HttpRequestWrap().request(new UpdateNicknameParam(request, callBack, groupId));
    }


    /**
     * 退出或者解散群组
     * @param group 群组
     * @param callBack http响应的回调
     */
    public static void quitOrDismissGroup(Group group, IModuleHttpCallBack callBack) {
        String groupId = group.getGroupId();
        String currentAccount = ContactUtils.getCurrentAccount();
        String groupOwner = group.getGroupOwner();
        if(currentAccount.equals(groupOwner)){
            DismissGroupParams params = new DismissGroupParams(callBack, groupId);
            new HttpRequestWrap().request(params);
        }else{
            ExitFromGroupParams params = new ExitFromGroupParams(callBack, groupId);
            new HttpRequestWrap().request(params);
        }
    }

    /**
     * 创建群组
     * @param reqBean
     * @param callBack
     */
    public static void createGroup(CreateGroupBody reqBean, IModuleHttpCallBack callBack) {
        new HttpRequestWrap().request(new CreateGroupParams(reqBean, callBack));
    }

    /**
     * 添加成员
     * @param groupId 群组ID
     * @param body 请求实体
     * @param cb  http响应回调
     */
    public static void addUsersToGroup(String groupId,Object body,IModuleHttpCallBack cb ) {
        //首先保证当前用户是群组成员
        new HttpRequestWrap().request(new AddGroupMembersParams(body, cb, groupId));
    }


    public static void getGroupInfoById(String groupId , IModuleHttpCallBack cb){
        new HttpRequestWrap().request(new GetGroupInfoParams(cb, groupId), false);
    }


    /*[S]add by tangsha for get ckms create group operation sign*/
    public static void getCkmsGroupOpSign(IModuleHttpCallBack cb, String groupId, List<String> groupAccounts, String OpSignType){
        GetCkmsOpSignBody body = new GetCkmsOpSignBody();
        String currentAccount = ContactUtils.getCurrentAccount();
        body.setOpCode(CkmsGpEnDecryptManager.getGroupOpStr(currentAccount,groupId,groupAccounts,OpSignType));
        new HttpRequestWrap().requestWithDevice(new GetCkmsOperSignParam(body,cb));
    }


    public static String syncGetCkmsGroupOpSign(String groupId, List<String> groupAccounts, String OpSignType){
        final Map<String, String> response = new HashMap<>();
        GetCkmsOpSignBody body = new GetCkmsOpSignBody();
        String currentAccount = ContactUtils.getCurrentAccount();
        body.setOpCode(CkmsGpEnDecryptManager.getGroupOpStr(currentAccount,groupId,groupAccounts,OpSignType));
        new HttpRequestWrap().requestWithDevice(new GetCkmsOperSignParam(body, new IModuleHttpCallBack() {
            @Override
            public void onFail(HttpErrorBean errorBean) {
                response.put("result", "fail");
            }

            @Override
            public void onSuccess(String body) {
                ResponseCkmsOpSign responseBean = JSON.parseObject(body, ResponseCkmsOpSign.class);
                response.put("opCode", responseBean.getSignedOpCode());
                response.put("result", "success");
            }

            @Override
            public void onErr() {
                response.put("result", "error");
            }
        }));

        while (!response.containsKey("result")) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                LogUtil.getUtils().e(" GroupHttpServiceHelper syncGetCkmsGroupOpSign exception:"+e.getMessage());
            }
        }

        return response.containsKey("opCode") ? response.get("opCode"): "";
    }
    /*[E]add by tangsha for get ckms create group operation sign*/


    /*[S]add by xienana for get ckms quit or destroy  operation sign @2016/08/01(rummager:tangsha)*/
    public static void getQuitOrDestroyGroupOpSign(IModuleHttpCallBack cb, String groupId,String OpSignType){
        GetCkmsOpSignBody body = new GetCkmsOpSignBody();
        String currentAccount = ContactUtils.getCurrentAccount();
        body.setOpCode(CkmsGpEnDecryptManager.getQuitOrDestroyGroupOpStr(currentAccount,groupId,OpSignType));
        new HttpRequestWrap().requestWithDevice(new GetCkmsOperSignParam(body,cb));
    }

    public static void getQuitOrDestroyEntityOpSign(IModuleHttpCallBack cb,String OpSignType){
        GetCkmsOpSignBody body = new GetCkmsOpSignBody();
        String currentAccount = ContactUtils.getCurrentAccount();
        body.setOpCode(CkmsGpEnDecryptManager.getQuiteOrDestroyEntityOpStr(currentAccount,OpSignType));
        new HttpRequestWrap().requestWithDevice(new GetCkmsOperSignParam(body,cb));
    }
    /*[E]add by xienana for get ckms quit or destroy  operation sign @2016/08/01(rummager:tangsha)*/

}
