package com.xdja.imp.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.imp.R;
import com.xdja.imp.presenter.command.IAnTongTeamOperationCommand;
import com.xdja.imp.ui.vu.IAnTongTeamOperationVu;

import butterknife.ButterKnife;

/**
 * Created by cxp on 2015/8/7.
 */
public class ViewAnTongTeamOperation extends BaseActivityVu<IAnTongTeamOperationCommand> implements IAnTongTeamOperationVu, View.OnClickListener {


    private WebView webView;

    private ProgressBar progressBar;

    private RelativeLayout networkLoadRefreshRL;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);

        webView = ButterKnife.findById(getView(), R.id.notification_operation_webView);
        initWebViewSettings();
        progressBar = ButterKnife.findById(getView(), R.id.notification_progressBar_loading);
        networkLoadRefreshRL = ButterKnife.findById(getView(), R.id.error_loading_ly);
        networkLoadRefreshRL.setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.error_loading_ly) {
            networkLoadRefreshRL.setVisibility(View.GONE);
            getCommand().refreshUrl();
        }
    }

    /**
     * 初始化webView设置信息
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        //设置支持JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.addJavascriptInterface(getCommand().initWebViewData(),
                "android");
    }

    /**
     * 控制进度条的显示
     *
     * @param isVisible
     */
    @Override
    public void setProgressViewVisible(int isVisible) {
        progressBar.setVisibility(isVisible);
    }

    /**
     * 控制加载试图的显示
     *
     * @param isVisible
     */
    @Override
    public void setNetworkLoadViewVisible(int isVisible) {

        networkLoadRefreshRL.setVisibility(isVisible);
    }

    /**
     * 获取WebView加载进度
     */
    @Override
    public int getWebViewProgress() {
        if (webView != null) {
            return webView.getProgress();
        }
        return 0;
    }

    /**
     * WebView停止加载
     */
    @Override
    public void webViewStopLoading() {
        if (webView != null) {
            webView.stopLoading();
        }
    }

    /**
     * 获得当前加载的WebView
     *
     * @return
     */
    @Override
    public WebView getCurrentWebView() {
        if (webView != null) {
            return webView;
        }
        webView = new WebView(getContext());
        return webView;
    }

    /**
     * 显示Toast提示信息
     *
     * @param msg
     */
    @Override
    public void showToastWithMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT)
                .show();
    }
    /*=================modify by gy 2015-10-27 start=======================*/

    /**
     * 设置进度条进度
     *
     * @param progress 进度
     */
    @Override
    public void setWebViewProgress(int progress) {
        progressBar.setProgress(progress);
    }
    /*=================modify by gy 2015-10-27 end=======================*/

    @Override
    public void onNavigateBackPressed() {
        getCommand().finishCurrentActivity();
    }

    /**
     * 内容视图
     *
     * @return
     */
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_antong_team_notification_operation;
    }

    /**
     * 导航栏ID
     *
     * @return
     */
    @Override
    protected int getToolBarId() {
        return R.id.notification_operation_tool_bar;
    }

    /**
     * 导航条类型
     *
     * @return
     */
    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_CUSTOM_BACK;
    }


    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.activity_notification_operation_presenter);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
