package com.xdja.contact.http.proxy;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.https.HttpsRequstResult;
import com.xdja.comm.https.Property.HttpResultSate;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.convert.DepartmentConvert;
import com.xdja.contact.exception.http.FriendHttpException;
import com.xdja.contact.http.FriendHttpServiceHelper;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.util.EncryptManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Member;
import com.xdja.contact.callback.OnBatchTaskListener;
import com.xdja.contact.http.engine.HttpTask;
import com.xdja.contact.http.engine.OnOperateListener;
import com.xdja.contact.http.engine.OperateCallBack;
import com.xdja.contact.http.engine.Result;
import com.xdja.contact.http.response.department.CheckDepartUpdateRes;
import com.xdja.contact.http.response.department.ServerMember;
import com.xdja.contact.http.response.department.UpdateMemberResponse;
import com.xdja.contact.http.wrap.AbstractHttpParams;
import com.xdja.contact.http.wrap.IHttpParams;
import com.xdja.contact.http.wrap.params.department.DepartMemberIncrementalParam;
import com.xdja.contact.http.wrap.params.department.DetectDepartIncrementalParam;
import com.xdja.contact.service.MemberService;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghao on 2016/5/13.
 */
public class DepartMemberHttpTask extends HttpTask {

    private int total = 0;

    private int batchSize = 100;

    private ArrayList<Member> allMembers = new ArrayList<>();

    private ArrayList<Member> updateMembers = new ArrayList<>();

    private ArrayList<Member> delMembers = new ArrayList<>();

    private ArrayList<Member> unBindMembers = new ArrayList<>();//解绑用户List(1.解绑成非安通用户2.直接删除该用户)

    protected OnBatchTaskListener<List<Member>,HttpErrorBean> onBatchTaskListener;

    public DepartMemberHttpTask(OnBatchTaskListener<List<Member>, HttpErrorBean> onBatchTaskListener){
        this.onBatchTaskListener = onBatchTaskListener;
    }


    @Override
    protected OnOperateListener getOperateListener() {
        return new OperateCallBack(){
            @Override
            public IHttpParams isNeedNext(int position, String lastSuccessData) {
                if (ObjectUtil.stringIsEmpty(lastSuccessData)) {
                    getAccountInfoNotInLocal();
                    return null;
                }
                int personSubUpdateID = -1;
                if (position == 0) {
                    CheckDepartUpdateRes response = JSON.parseObject(lastSuccessData, CheckDepartUpdateRes.class);
                    total = Integer.valueOf(response.getTotalSize());
                    if (total > 0) {
                        personSubUpdateID = Integer.valueOf(response.getPersonSubUpdateId());
                        return new DepartMemberIncrementalParam(PreferenceUtils.getPersonLastUpdateId(ActomaController.getApp()), personSubUpdateID, batchSize);
                    }
                } else if (position > 0) {
                    //获取人员更新参数
                    UpdateMemberResponse memberResponse = JSON.parseObject(lastSuccessData, UpdateMemberResponse.class);
                    MemberService memberService = new MemberService();
                    for (ServerMember serverMember : memberResponse.getPersons()) {
                        if (Member.ADD.equals(serverMember.getType()) || Member.MODIFY.equals(serverMember.getType())) {
                            if(Member.MODIFY.equals(serverMember.getType())){
                                if(TextUtils.isEmpty(serverMember.getAccount())){
                                    unBindMembers.add(serverMember.convert2Member());
                                }
                            }
                            updateMembers.add(serverMember.convert2Member());
                            LogUtil.getUtils().i("更新人员:" + serverMember.getName());
                        } else {
                            unBindMembers.add(serverMember.convert2Member());
                            delMembers.add(serverMember.convert2Member());
                        }
                    }

                    //判断是否还有数据
                    if (memberResponse.getHasMore()) {
                        return new DepartMemberIncrementalParam(Integer.parseInt(memberResponse.getPersonLastUpdateId()), personSubUpdateID, batchSize);
                    } else {
                        //Note: 过滤数据如果有删除的集团人员且正好是加密选中的人员则关闭第三方加密模块
                        //更新人员完成,插入本地数据库
                        if (!ObjectUtil.collectionIsEmpty(updateMembers)) {
                            if (!ObjectUtil.objectIsEmpty(unBindMembers)) {
                                List<Member> resultMembers = memberService.findMembersByIds(DepartmentConvert.convertDepartMemberWorkId(unBindMembers));
                                EncryptManager.closeDepartmentEncryptionChannel(DepartmentConvert.convertDepartMemberAccount(resultMembers));
                            }
                            memberService.insert(updateMembers);
                            allMembers.addAll(updateMembers);
                        }
                        if (!ObjectUtil.collectionIsEmpty(delMembers)) {
                            if (!ObjectUtil.objectIsEmpty(unBindMembers)) {
                                List<Member> resultMembers = memberService.findMembersByIds(DepartmentConvert.convertDepartMemberWorkId(unBindMembers));
                                EncryptManager.closeDepartmentEncryptionChannel(DepartmentConvert.convertDepartMemberAccount(resultMembers));
                            }
                            memberService.delete(delMembers);
                            allMembers.addAll(delMembers);
                        }
                        //保存更新标识
                        PreferenceUtils.savePersonLastUpdateId(ActomaController.getApp(), memberResponse.getPersonLastUpdateId());
                    }
                }
                getAccountInfoNotInLocal();
                return null;
            }

            private final int ONCE_GET_ACCOUNT_MAX_SIZE = 100;
            private void getAccountInfoNotInLocal(){
                MemberService memberService = new MemberService();
                List<String> noAccountInfoList = memberService.getMembersNoAccountInfo();
                if(noAccountInfoList != null && noAccountInfoList.size() > 0) {
                    LogUtil.getUtils().d("Actoma contact DepartMemberHttpTask getAccountInfoNotInLocal noAccountInfoList size "+noAccountInfoList.size());
                    int getInfoStartIndex = 0;
                    int getInfoEndIndex;
                    int totalSize = noAccountInfoList.size();
                    ActomAccountService accountService = new ActomAccountService();
                    while(getInfoStartIndex < totalSize) {
                        getInfoEndIndex = getInfoStartIndex+ONCE_GET_ACCOUNT_MAX_SIZE;
                        getInfoEndIndex = getInfoEndIndex <= totalSize ? getInfoEndIndex : totalSize;
                        List<String> subToGetAccounts = noAccountInfoList.subList(getInfoStartIndex,getInfoEndIndex);
                        LogUtil.getUtils().d("Actoma contact DepartMemberHttpTask getAccountInfoNotInLocal subToGetAccounts size "+subToGetAccounts.size());
                        try {
                            HttpsRequstResult requstResult = FriendHttpServiceHelper.bulkDownloadAccounts(subToGetAccounts);
                            if (!ObjectUtil.objectIsEmpty(requstResult) && requstResult.result == HttpResultSate.SUCCESS) {
                                String body = requstResult.body;
                                if (!ObjectUtil.stringIsEmpty(body)) {
                                    try {
                                        List<ResponseActomaAccount> responseAccountInfos = JSON.parseArray(body, ResponseActomaAccount.class);
                                        boolean bool = accountService.batchSaveAccountsAssociateWithAvatar(responseAccountInfos);
                                    } catch (Exception e) {
                                        LogUtil.getUtils().e("Actoma contact DepartMemberHttpTask getAccountInfoNotInLocal Exception " + e.getMessage());
                                    }
                                }
                            }
                        } catch (FriendHttpException e) {
                            e.printStackTrace();
                            LogUtil.getUtils().e("Actoma contact DepartMemberHttpTask getAccountInfoNotInLocal FriendHttpException " + e.getMessage());
                        }
                        getInfoStartIndex = getInfoEndIndex;
                    }
                }else{
                    LogUtil.getUtils().d("Actoma contact DepartMemberHttpTask getAccountInfoNotInLocal noAccountInfoList is null");
                }
            }

            @Override
            public void onTaskSuccess(Result result) {
                if (!ObjectUtil.objectIsEmpty(onBatchTaskListener)) {
                    onBatchTaskListener.onBatchTaskSuccess(allMembers);
                    //onBatchTaskListener.onBatchTaskStart();
                }
            }

            @Override
            public void onTaskFailed(Result result) {
                if (!ObjectUtil.objectIsEmpty(onBatchTaskListener)) {
                    onBatchTaskListener.onBatchTaskFailed(result.getHttpErrorBean());
                }
            }
        };
    }

    @Override
    public String getTaskId() {
        return INCREMENT_DEPART_MEMBER_TASK;//modify by lwl
    }

    @Override
    public String getReason() {
        return "";
    }
//modify by lwl start for refresh
    @Override
    public void template() {
        if(TaskManager.getInstance().isIncludeTaskPool(this))
            return;
        TaskManager.getInstance().putTask(this);
        execute(getHttpParams());
    }
    public void template(int refresh) {
        execute(getHttpParams());
    }
    //modify by lwl end for refresh
    private IHttpParams getHttpParams(){
        AbstractHttpParams abstractHttpParams = new DetectDepartIncrementalParam(PreferenceUtils.getDeptLastUpdateId(ActomaController.getApp()), PreferenceUtils.getPersonLastUpdateId(ActomaController.getApp()));
        return abstractHttpParams;
    }


}
