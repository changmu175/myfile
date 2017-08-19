package com.xdja.contact.task.ckms;

/**
 * Created by tangsha on 2016/11/10.
 */
public interface CreateSGroupResultCallback {
    void onTaskPreExec();
    void showExceptionToast(String message);
    void onTaskPostExec(int resultCode,Object info);
    void onTaskBackgroundOk(int resultCode,Object info, Object info1,Object info2);
    void onTaskProgress(int value);
}
