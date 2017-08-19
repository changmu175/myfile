package com.xdja.presenter_mainframe.presenter.base;

import android.support.annotation.NonNull;

import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.exeptions.OkException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.main.ExceptionHandler;
import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.R;

/**
 * Created by ldy on 16/5/31.
 */
public class LoadingDialogSubscriber<T> extends PerSubscriber<T> {
    private final PresenterActivity activity;
    private ActivityVu activityVu;
    private boolean mIsDismissDialogWhenCompleleted;
    private int mMode = 0;
    public LoadingDialogSubscriber(ExceptionHandler handler, PresenterActivity activity) {
        super(handler);
        this.activity = activity;
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-05 add. fix bug 2440 . review by wangchao1. Start
    public LoadingDialogSubscriber(ExceptionHandler handler, PresenterActivity activity, boolean isDismissDialogWhenCompleleted) {
        super(handler);
        this.activity = activity;
        mIsDismissDialogWhenCompleleted = isDismissDialogWhenCompleleted;
    }

    public LoadingDialogSubscriber(ExceptionHandler handler, PresenterActivity activity , int mode) {
        super(handler);
        this.activity = activity;
        mMode = mode;
    }

    public LoadingDialogSubscriber(ExceptionHandler handler, PresenterActivity activity, boolean isDismissDialogWhenCompleleted , int mode) {
        super(handler);
        this.activity = activity;
        mIsDismissDialogWhenCompleleted = isDismissDialogWhenCompleleted;
        mMode = mode;
    }

    public void setDismissDialogWhenCompleleted(boolean dismiss){
        mIsDismissDialogWhenCompleleted = !dismiss;
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-05 add. fix bug 2440 . review by wangchao1. End

    @Override
    public <T1 extends OkException> LoadingDialogSubscriber<T> registUserMsg(@NonNull Class<T1> cls, @NonNull String errorCode, @NonNull String userMsg) {
        super.registUserMsg(cls, errorCode, userMsg);
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        PresenterActivity activity = getActivity();
        if (activity == null) {
            LogUtil.getUtils().w("activity为空");
            return;
        }
        activityVu = activity.getVu();
        if (mLoadingMsg.equals(DEFAULT_LOADING_MSG)) {
            mLoadingMsg = PRE_LOADING_MSG + mLoadingMsg + AFTER_LOADING_MSG;
        }
        showLoadingDialog();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        dismissDialog();
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        if (!mIsDismissDialogWhenCompleleted) {
            dismissDialog();
        }
    }

    //Start fix bug 5561 by licong, 2016/12/6
    public  final String DEFAULT_LOADING_MSG = ActomaController.getApp().getString(R.string.load);//"加载";
    private  final String PRE_LOADING_MSG = ActomaController.getApp().getString(R.string.being);//"正在";
    private  final String AFTER_LOADING_MSG = ActomaController.getApp().getString(R.string.apostrophe);
    //End fix bug 5561 by licong, 2016/12/6
    private String mLoadingMsg = DEFAULT_LOADING_MSG;
    private boolean isShowLoading = true;

    /**
     * see {@link #registerLoadingMsg(String, boolean)}
     */
    public LoadingDialogSubscriber<T> registerLoadingMsg(
            @NonNull String loadingMsg) {
        return registerLoadingMsg(loadingMsg, true);
    }

    /**
     * 注册loading显示的信息，loading从{@link #onStart()}开始，到{@link #onCompleted()}
     * 或{@link #onError(Throwable)}结束。
     *
     * @param loadingMsg loading的信息，默认为{@link #DEFAULT_LOADING_MSG}
     * @param isAutoAdd  是否自动填充loading的前后字符串，默认为{@link #PRE_LOADING_MSG},{@link #AFTER_LOADING_MSG}
     * @return
     */
    public LoadingDialogSubscriber<T> registerLoadingMsg(
            @NonNull String loadingMsg, boolean isAutoAdd) {
        if (isAutoAdd) {
            mLoadingMsg = PRE_LOADING_MSG + loadingMsg + AFTER_LOADING_MSG;
        } else {
            mLoadingMsg = loadingMsg;
        }
        return this;
    }

    public void showLoadingDialog() {
        if (!isShowLoading) {
            return;
        }
        if (activityVu != null) {
            if (mMode == 0) activityVu.showCommonProgressDialog(mLoadingMsg);
            else activityVu.showProgressDialog();
        }
    }

    private PresenterActivity getActivity() {
        if (activity != null) {
            return activity;
        }
//        Activity topActivity = ActivityStack.getInstanse().getTopActivity();
//        if (topActivity == null) {
//            return null;
//        }
//        if (topActivity instanceof PresenterActivity) {
//            return (PresenterActivity) topActivity;
//        }
        return null;
    }

    public boolean isShowLoading() {
        return isShowLoading;
    }

    /**
     * 设置是否显示loading，默认显示
     */
    public LoadingDialogSubscriber<T> setShowLoading(boolean showLoading) {
        isShowLoading = showLoading;
        return this;
    }

    public void dismissDialog() {
        if (!isShowLoading) {
            return;
        }
        if (activityVu != null)
            activityVu.dismissCommonProgressDialog();
    }
}
