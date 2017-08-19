package com.xdja.contact.callback;

import com.xdja.comm.https.HttpErrorBean;

/**
 * Created by wanghao on 2016/2/26.
 *
 */
public interface IPullCallback {

    boolean isSupportLoading();

    void stopRefreshLoading();

    void onShowErrorToast(HttpErrorBean httpErrorBean);
}
