package com.xdja.frame.data.ckms;

import rx.Observable;

/**
 * Created by tangsha on 2016/6/30.
 */
public interface CkmsCallback {
    void initCallback(int errorCode, int validHour, String errorInfo);
}
