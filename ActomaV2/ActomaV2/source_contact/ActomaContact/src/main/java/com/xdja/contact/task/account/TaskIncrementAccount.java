package com.xdja.contact.task.account;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.https.ErrorCode.StatusCode;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.R;
import com.xdja.contact.bean.enumerate.ServiceErrorCode;
import com.xdja.contact.callback.IAccountCallback;
import com.xdja.contact.http.engine.OnOperateListener;
import com.xdja.contact.http.engine.OperateCallBack;
import com.xdja.contact.http.engine.Result;
import com.xdja.contact.http.response.account.ResponseActomaAccount;
import com.xdja.contact.http.response.account.ResponseIncrementAccounts;
import com.xdja.contact.http.wrap.IHttpParams;
import com.xdja.contact.http.wrap.params.account.AccountIncrementalParam;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.task.AbstractAccountTask;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.List;


/**
 * Created by wanghao on 2015/12/24.
 * 增量账户信息
 */
public class TaskIncrementAccount extends AbstractAccountTask {

    private IAccountCallback accountCallback;

    private final String TASK_TAG_PRE = INCREMENT_ACCOUNT_TASK;

    public TaskIncrementAccount(){
        super();
        setTaskTag(TASK_TAG_PRE);
    }

    /**
     * 好友下拉刷新时调用当前构造
     * @param accountCallback
     */
    public TaskIncrementAccount(IAccountCallback accountCallback, String tagSuf) {
        this();
        this.accountCallback = accountCallback;
        setTaskTag(TASK_TAG_PRE+tagSuf);
    }

    @Override
    protected OnOperateListener buildCallBack(final List<ResponseActomaAccount> serverAccounts) {
        return new OperateCallBack(){
            @Override
            public IHttpParams isNeedNext(int position, String lastSuccessData) {
                boolean isCancel = isCancelled();
                LogUtil.getUtils().d("TaskIncrementAccount isNeedNext "+lastSuccessData+" isCancel "+isCancel
                +" size "+serverAccounts.size());
                if(ObjectUtil.stringIsEmpty(lastSuccessData) || isCancel){
                    return null;
                }
                ResponseIncrementAccounts accounts = JSON.parseObject(lastSuccessData, ResponseIncrementAccounts.class);
                if (!ObjectUtil.objectIsEmpty(accounts) && !ObjectUtil.collectionIsEmpty(accounts.getAccounts())) {
                    serverAccounts.addAll(accounts.getAccounts());
                    lastUpdateId = accounts.getLastUpdateId();
                    return new AccountIncrementalParam(lastUpdateId, batchSize);
                } else{
                    //更新完成持久化到本地
                    if (!ObjectUtil.collectionIsEmpty(serverAccounts) && isCancelled() == false) {
                        ActomAccountService accountService = new ActomAccountService();
                        boolean result = accountService.batchSaveAccountsAssociateWithAvatar(serverAccounts);
                        if(result) {
                            PreferenceUtils.saveAccountLastUpdateId(context, lastUpdateId);
                        }
                    }

                }
                return null;
            }

            @Override
            public void onTaskSuccess(Result result) {
                if(!ObjectUtil.objectIsEmpty(accountCallback) && accountCallback.isSupportLoading()){
                    accountCallback.onAccountSuccess(serverAccounts);
                }
                deleteErrorPush();
                TaskManager.getInstance().removeTask(TaskIncrementAccount.this);
                ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_ACCOUNT_DOWNLOAD_SUCCESS));
            }

            @Override
            public void onTaskFailed(Result result) {
                //start:add by wal@xdja.com for 3991
                LogUtil.getUtils().e("TaskIncrementAccount onTaskFailed accountCallback "+accountCallback);
                HttpErrorBean httpErrorBean = result.getHttpErrorBean();
                if(!ObjectUtil.objectIsEmpty(accountCallback) && accountCallback.isSupportLoading()){
                    accountCallback.stopRefreshLoading();
                    if ( httpErrorBean.getStatus() <= StatusCode.NET_ERROR ){
                        accountCallback.onShowErrorToast(null);
                    }
                }
                //end:add by wal@xdja.com for 3991
                if (ServiceErrorCode.isMatch(httpErrorBean.getErrCode()) || httpErrorBean.getStatus() <= StatusCode.NET_ERROR) {
                    setHttpErrorBean(httpErrorBean);
                    saveOrUpdateErrorPush();
                }
                TaskManager.getInstance().removeTask(TaskIncrementAccount.this);
            }
        };
    }

    @Override
    public String getTaskId() {
        return getTaskTag();
    }

    @Override
    public String getReason() {
        return String.format(ActomaController.getApp().getString(R.string.increment_error_account), httpErrorBean.getMessage(), httpErrorBean.getStatus());
    }

}
