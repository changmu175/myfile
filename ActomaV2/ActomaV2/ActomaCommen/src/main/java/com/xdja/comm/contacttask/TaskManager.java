package com.xdja.comm.contacttask;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wanghao on 2015/12/22.
 */
public final class TaskManager {

    private static final String TAG = TaskManager.class.getSimpleName();

    private final Map<String,ITask> taskMap = Collections.synchronizedMap(new HashMap());

    private static TaskManager instance;

    private TaskManager(){}

    public static TaskManager getInstance(){
        if(ObjectUtil.objectIsEmpty(instance)){
            synchronized (TaskManager.class) {
                TaskManager temp = instance;
                if(ObjectUtil.objectIsEmpty(temp)) {
                    synchronized (TaskManager.class) {
                        temp = new TaskManager();
                    }
                    instance = temp;
                }
            }
        }
        return instance;
    }

    public ITask getTask(String key){
        if(ObjectUtil.stringIsEmpty(key))return null;
        return taskMap.get(key);
    }

    public synchronized void putTask(ITask task){
        String key = task.getTaskId();
        if(!ObjectUtil.objectIsEmpty(taskMap.get(key))){
            LogUtil.getUtils(TAG).i("putTask任务已经存在于当前队列当中:"+key);
            return ;
        }
        LogUtil.getUtils(TAG).i("putTask:"+key+" task "+task);
        taskMap.put(key, task);
    }

    private synchronized void stopTask(ITask task){
        if(ObjectUtil.objectIsEmpty(taskMap) || taskMap.size()<= 0){
            LogUtil.getUtils().d("20170220 TaskManager stopTask taskMap is empty!");
            return;
        }
        String key = task.getTaskId();
        LogUtil.getUtils(TAG).i("20170220 TaskManager stopTask:" + key);
        ITask value = taskMap.get(key);
        if(ObjectUtil.objectIsEmpty(value)){
            LogUtil.getUtils().d("20170220 TaskManager 要停止的任务值不能为空");
            return;
        }
        if(value instanceof ContactAsyncTask){
            ContactAsyncTask asyncTask = (ContactAsyncTask)value;
            boolean hasCancel = asyncTask.isCancelled();
            ContactAsyncTask.Status status = asyncTask.getStatus();
            LogUtil.getUtils().d("20170220 TaskManager stopTask ContactAsyncTask hasCancel "+hasCancel+" status "+status);
            if(!hasCancel && status != ContactAsyncTask.Status.FINISHED) {
                asyncTask.cancel(true);
                taskMap.remove(key);
            }
        }else if(value instanceof AsyncTask){
            AsyncTask asyncTask = (AsyncTask)value;
            boolean hasCancel = asyncTask.isCancelled();
            AsyncTask.Status status = asyncTask.getStatus();
            LogUtil.getUtils().d("20170220 TaskManager stopTask AsyncTask hasCancel "+hasCancel+" status "+status);
            if(!hasCancel && status != AsyncTask.Status.FINISHED) {
                asyncTask.cancel(true);
                taskMap.remove(key);
            }
        }
    }

    public void removeAllTask(){
        LogUtil.getUtils().d("20170220 TaskManager removeAllTask");
        if(ObjectUtil.objectIsEmpty(taskMap) || taskMap.size()<= 0){
            LogUtil.getUtils().d("20170220 TaskManager removeAllTask taskMap is empty!");
            return;
        }
        Set<Map.Entry<String, ITask>> values = taskMap.entrySet();
        List<Map.Entry<String, ITask>> taskList = new ArrayList<>(values);
        for(int length = taskList.size()-1; length>=0 ;length--){
            Map.Entry<String, ITask> entry = taskList.get(length);
            stopTask(entry.getValue());
        }
    }

    public synchronized void removeTask(ITask task){
        if(ObjectUtil.objectIsEmpty(taskMap) || taskMap.size()<= 0)return;
        /*Set<Map.Entry<String, ITask>> values = taskMap.entrySet();
        List<Map.Entry<String, ITask>> taskList = new ArrayList<Map.Entry<String, ITask>>(values);
        for(int length = taskList.size()-1; length>=0 ;length--){
            Map.Entry<String, ITask> entry = taskList.get(length);
            if(task.equals(entry.getValue())){
                taskMap.remove(entry.getKey());
            }
        }*/
        if(task != null) {
            taskMap.remove(task.getTaskId());
        }
        if(ObjectUtil.mapIsEmpty(taskMap)){
            ActomaController.getApp().sendBroadcast(new Intent().setAction(RegisterActionUtil.ACTION_TASK_ALL_REMOVE));
        }
    }

    //判断是否为空
    public boolean isEmpty(){
        return ObjectUtil.mapIsEmpty(taskMap);
    }

    //判断当前任务是否存在任务栈中
    public boolean isIncludeTaskPool(ITask task){
        if(ObjectUtil.mapIsEmpty(taskMap))return false;
        if(!ObjectUtil.objectIsEmpty(taskMap.get(task.getTaskId()))){
            Log.d("lwlactoma","taskMap.get(task.getTaskId()).getTaskId() "+taskMap.get(task.getTaskId()).getTaskId());

            return true;
        }
        return false;
    }

}
