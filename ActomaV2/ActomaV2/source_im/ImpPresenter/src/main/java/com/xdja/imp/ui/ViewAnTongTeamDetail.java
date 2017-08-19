package com.xdja.imp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.imp.R;
import com.xdja.imp.presenter.command.IAnTongTeamDetailCommand;
import com.xdja.imp.ui.vu.IAnTongTeamDetailVu;

import butterknife.ButterKnife;

/**
 * Created by cxp on 2015/8/10.
 */
public class ViewAnTongTeamDetail extends BaseActivityVu<IAnTongTeamDetailCommand> implements IAnTongTeamDetailVu, View.OnClickListener {

    private WebView myWebView;
    private ProgressBar myProgressBar;
    private LinearLayout networkLoadRefreshRL;



    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);

        initViews();

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.error_detail_loading_ly) {
            networkLoadRefreshRL.setVisibility(View.GONE);
            getCommand().refreshUrl();
        }
    }

    /**
     * 初始化视图控件
     */
    private void initViews() {
        myWebView = ButterKnife.findById(getView(), R.id.notification_detail_webView);
        myProgressBar = ButterKnife.findById(getView(), R.id.notification_detail_progressBar);
        networkLoadRefreshRL = ButterKnife.findById(getView(), R.id.error_detail_loading_ly);
        networkLoadRefreshRL.setOnClickListener(this);
    }




    @Override
    public void onNavigateBackPressed() {
        if (isWebViewCanGoBAck()) {
            webViewGoBack();
        } else {
            getCommand().finishCurrentActivity();
        }
    }

    /**
     * webView是否有上一级
     *
     * @return
     */
    @Override
    public boolean isWebViewCanGoBAck() {

        return myWebView.canGoBack();
    }

    /**
     * 后退到上一级
     */
    @Override
    public void webViewGoBack() {
        myWebView.goBack();
    }

    /**
     * 控制进度条的显示
     *
     * @param isVisible
     */
    @Override
    public void setProgressViewVisible(int isVisible) {
        myProgressBar.setVisibility(isVisible);
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
        if (myWebView!=null){
            return myWebView.getProgress();
        }
        return 0;
    }

    /**
     * WebView停止加载
     */
    @Override
    public void webViewStopLoading() {
        if (myWebView!=null){
            myWebView.stopLoading();
        }
    }

    /**
     * 获得当前加载的WebView
     *
     * @return
     */
    @Override
    public WebView getCurrentWebView() {
        if (myWebView!=null){
            return myWebView;
        }
        myWebView = new WebView(getContext());
        return myWebView;
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


    /**
     * 内容视图
     *
     * @return
     */
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_antong_team_detail_notification_operation;
    }

    /**
     * 导航栏ID
     *
     * @return
     */
    @Override
    protected int getToolBarId() {
        return R.id.notification_detail_tool_bar;
    }

    /**
     * 导航条类型
     *
     * @return
     */
    @Override
    protected int getToolbarType() {
        //fix bug 9582 by zya ,20170307
        //return ToolbarDef.NAVIGATE_BACK;
        return ToolbarDef.NAVIGATE_CUSTOM_BACK;
        //end by zya
    }


}
