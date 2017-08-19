package com.xdja.contact.http.proxy;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.contact.bean.Department;
import com.xdja.contact.callback.DatabaseListener;
import com.xdja.contact.callback.OnBatchTaskListener;
import com.xdja.contact.http.engine.HttpTask;
import com.xdja.contact.http.engine.OnOperateListener;
import com.xdja.contact.http.engine.OperateCallBack;
import com.xdja.contact.http.engine.Result;
import com.xdja.contact.http.response.department.CheckDepartUpdateRes;
import com.xdja.contact.http.response.department.ServerDepart;
import com.xdja.contact.http.response.department.UpdateDepartResponse;
import com.xdja.contact.http.wrap.AbstractHttpParams;
import com.xdja.contact.http.wrap.IHttpParams;
import com.xdja.contact.http.wrap.params.department.DepartmentIncrementalParam;
import com.xdja.contact.http.wrap.params.department.DetectDepartIncrementalParam;
import com.xdja.contact.service.DepartService;
import com.xdja.comm.contacttask.TaskManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wanghao on 2015/12/10.\
 * 增量更新集团通讯录
 */
public class DepartmentHttpTask extends HttpTask {

    private int total;

    private int batchSize = 50;

    private List<Department> addDepartments = new ArrayList<>();

    private List<Department> delDepartments = new ArrayList<>();

    protected OnBatchTaskListener<List<Department>,HttpErrorBean> onBatchTaskListener;

    public DepartmentHttpTask(OnBatchTaskListener<List<Department>, HttpErrorBean> onBatchTaskListener){
        this.onBatchTaskListener = onBatchTaskListener;
    }

    @Override
    public String getTaskId() {
        return INCREMENT_DEPART_TASK;
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

    @Override
    protected OnOperateListener getOperateListener() {
        return new OperateCallBack(){
            @Override
            public IHttpParams isNeedNext(int position, String lastSuccessData) {
                int deptSubUpdateId = 0;
                if (position == 0) {
                    CheckDepartUpdateRes checkDepartUpdateRes = JSON.parseObject(lastSuccessData, CheckDepartUpdateRes.class);
                    total = Integer.parseInt(checkDepartUpdateRes.getTotalSize());
                    if (total > 0) {
                        if (onBatchTaskListener != null) {
                            onBatchTaskListener.onNext(total, 0);
                        }
                        deptSubUpdateId = Integer.parseInt(checkDepartUpdateRes.getDeptSubUpdateId());
                        return new DepartmentIncrementalParam(PreferenceUtils.getDeptLastUpdateId(ActomaController.getApp()), deptSubUpdateId, batchSize);
                    } else {
                        return null;
                    }

                } else if (position > 0) {
                    //获取部门更新参数
                    UpdateDepartResponse updateDepartResponse = JSON.parseObject(lastSuccessData, UpdateDepartResponse.class);

                    DepartService service = new DepartService(ActomaController.getApp());
                    for (ServerDepart serverDepart : updateDepartResponse.getDepts()) {
                        if(Department.ADD.equals(serverDepart.getType()) || Department.MODIFY.equals(serverDepart.getType())){
                            addDepartments.add(serverDepart.convert2DepartMent());
                        }else {
                            delDepartments.add(serverDepart.convert2DepartMent());
                        }
                    }

                    //判断是否还有数据
                    if (updateDepartResponse.getHasMore()) {
                        return new DepartmentIncrementalParam(Integer.valueOf(updateDepartResponse.getDeptLastUpdateId()), deptSubUpdateId, batchSize);
                    } else if(isCancelled() == false){
                        service.insert(addDepartments, new DatabaseListener() {
                            @Override
                            public void onInsert(int count, int progress) {
                                if (onBatchTaskListener != null) {
                                    onBatchTaskListener.onNext(total, progress);
                                }
                            }
                        });
                        service.delete(delDepartments);
                        PreferenceUtils.savetDeptLastUpdateId(ActomaController.getApp(), updateDepartResponse.getDeptLastUpdateId());
                        return null;
                    }
                }
                return null;
            }

            @Override
            public void onTaskSuccess(Result result) {
                if (!ObjectUtil.objectIsEmpty(onBatchTaskListener)) {
                    onBatchTaskListener.onBatchTaskSuccess(addDepartments);
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

    private IHttpParams getHttpParams(){
        AbstractHttpParams abstractHttpParams = new DetectDepartIncrementalParam(PreferenceUtils.getDeptLastUpdateId(ActomaController.getApp()),PreferenceUtils.getPersonLastUpdateId(ActomaController.getApp()));
        return abstractHttpParams;
    }
}
