package com.xdja.imp.presenter.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.presenter.command.IAnTongTeamDetailCommand;
import com.xdja.imp.ui.ViewAnTongTeamDetail;
import com.xdja.imp.ui.vu.IAnTongTeamDetailVu;
import com.xdja.imp.util.BitmapUtils;

import java.lang.ref.WeakReference;
import java.util.Timer;

/**
 * Created by cxp on 2015/8/10.
 */
public class AnTongTeamDetailPresenter extends IMActivityPresenter<IAnTongTeamDetailCommand, IAnTongTeamDetailVu> implements IAnTongTeamDetailCommand {

    private String myUrl;
    private long timeOut = 20000;
    private static final int TIMEOUT_VALUE = 1;
    private static final int LOADING_FINISH_VALUE = 2;
    private static final int LOADING_ERROR_VALUE = 3;
    private static final int NETWORK_UNABLE_VALUE = 4;
    private Timer timer;
    private MyHandler mHandler = new MyHandler(new WeakReference<>(this));

    private static class MyHandler extends Handler {
        private final WeakReference<AnTongTeamDetailPresenter> mActivity;
        public  MyHandler(WeakReference<AnTongTeamDetailPresenter> mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            AnTongTeamDetailPresenter activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case TIMEOUT_VALUE: {
                    /*
                    * 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
                         */
                        if (activity.getVu().getWebViewProgress() < 100) {
                            activity.getVu().getCurrentWebView().setVisibility(View.GONE);
                            activity.getVu().setNetworkLoadViewVisible(View.VISIBLE);
                            activity.getVu().webViewStopLoading();
                        }
                    }
                    break;
                    case LOADING_FINISH_VALUE: {
                        activity.getVu().getCurrentWebView().setVisibility(View.VISIBLE);
                    }
                    break;
                    case LOADING_ERROR_VALUE: {
                        activity.getVu().getCurrentWebView().setVisibility(View.GONE);
                        activity.getVu().setNetworkLoadViewVisible(View.VISIBLE);
                    }
                    break;
                    case NETWORK_UNABLE_VALUE: {
                        activity.getVu().getCurrentWebView().setVisibility(View.GONE);
                        activity.getVu().setNetworkLoadViewVisible(View.VISIBLE);
                    }
                    break;
                }
                activity.getVu().setProgressViewVisible(View.GONE);
                activity.timerCancel();
            }
        }

    }

    @NonNull
    @Override
    protected Class<? extends IAnTongTeamDetailVu> getVuClass() {
        return ViewAnTongTeamDetail.class;
    }

    @Override
    protected IAnTongTeamDetailCommand getCommand() {
        return this;
    }


    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);

        myUrl = getIntent().getStringExtra(ConstDef.AN_TONG_NOTIFICATION_URL);
        String activityTitle = getIntent().getStringExtra(ConstDef.ACTIVITY_TITLE);
        setTitle(activityTitle);

        SpannableString spannableString = BitmapUtils.formatAnTongSpanContent(getTitle(), this, (float) 1.0, BitmapUtils.AN_TONG_TITLE_PLUS);
        setTitle(spannableString);

    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        loadDetailUrl(myUrl);

    }


    // 网络状态
    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 加载网页
     *
     * @param url
     */
    private void loadDetailUrl(String url) {
        boolean isOk = getCommand().getNetWorkState();
        if (isOk) {
            getVu().setProgressViewVisible(View.VISIBLE);
            getVu().getCurrentWebView().setWebViewClient(new WebViewClient() {
                                                             @Override
                                                             public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                                                 super.onPageStarted(view, url, favicon);
                                                                 ////TODO 超时时间暂时屏蔽
//                                                                 timer = new Timer();
//                                                                 TimerTask tt = new TimerTask() {
//                                                                     @Override
//                                                                     public void run() {
//
//                                                                         Message msg = new Message();
//                                                                         msg.what = TIMEOUT_VALUE;
//                                                                         mHandler.sendMessage(msg);
//                                                                     }
//                                                                 };
//                                                                 timer.schedule(tt, timeOut, 1);
                                                             }

                                                             @Override
                                                             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                                                 // TODO Auto-generated method stub
                                                                 //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                                                                 return true;
                                                             }

                                                             @Override
                                                             public void onPageFinished(WebView view, String url) {
                                                                 // TODO Auto-generated method stub
                                                                 // super.onPageFinished(view, url);

                                                                 Message msg = new Message();
                                                                 msg.what = LOADING_FINISH_VALUE;
                                                                 mHandler.sendMessage(msg);

                                                             }

                                                             @Override
                                                             public void onReceivedError(WebView view, int errorCode,
                                                                                         String description, String failingUrl) {
                                                                 // TODO Auto-generated method stub
                                                                 super.onReceivedError(view, errorCode, description, failingUrl);
                                                                 Message msg = new Message();
                                                                 msg.what = LOADING_ERROR_VALUE;
                                                                 mHandler.sendMessage(msg);
                                                             }
                                                         }

            );
        } else {
            getVu().showToastWithMessage(getResources().getString(R.string.check_network_setting));
            Message msg = new Message();
            msg.what = NETWORK_UNABLE_VALUE;
            mHandler.sendMessage(msg);
        }
        getVu().getCurrentWebView().loadUrl(url);
    }

    private void timerCancel() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }


    /**
     * 获得网络状态
     *
     * @return
     */
    @Override
    public boolean getNetWorkState() {
        return isNetworkConnected(getApplicationContext());
    }

    /**
     * 关闭当前界面
     */
    @Override
    public void finishCurrentActivity() {
        finish();
    }

    /**
     * 重新加载URL
     */
    @Override
    public void refreshUrl() {
        getVu().getCurrentWebView().clearCache(true);
        getVu().getCurrentWebView().clearHistory();
        getVu().getCurrentWebView().setVisibility(View.GONE);
        loadDetailUrl(myUrl);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getVu().isWebViewCanGoBAck()) {
            getVu().webViewGoBack();
        }
        return super.onKeyDown(keyCode, event);
    }
}
