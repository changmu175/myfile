package com.xdja.presenter_mainframe.presenter.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.xdja.comm.event.BusProvider;
import com.xdja.presenter_mainframe.cmd.WebViewCommand;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewWebView;
import com.xdja.presenter_mainframe.ui.uiInterface.WebViewVu;
import com.xdja.presenter_mainframe.util.WebViewJavascriptInterface;

/**
 * Created by geyao on 2015/7/7.
 * 通用webview
 */
public class WebViewPresenter extends PresenterActivity<WebViewCommand, WebViewVu> implements WebViewCommand {
    /**
     * 不同页面不同的url
     */
    public static final String WEBURL = "weburl";
    /**
     * 不同页面不同的标题
     */
    public static final String TITLE = "title";
    /**
     * Html5调用本地方法的Javascript接口
     */
    private WebViewJavascriptInterface webViewJavascriptInterface;

    @Override
    protected Class<? extends WebViewVu> getVuClass() {
        return ViewWebView.class;
    }

    @Override
    protected WebViewCommand getCommand() {
        return this;
    }


    /**
     * View初始化之后
     *
     * @param savedInstanceState
     */
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        //设置标题
        String title = getIntent().getStringExtra(TITLE);
        getSupportActionBar().setTitle(title);
        //加载内容
        String url = getIntent().getStringExtra(WEBURL);
        if (!TextUtils.isEmpty(url)) {
            getVu().loadUrl(url);
        }
    }

    /**
     * View初始化之前
     *
     * @param savedInstanceState
     */
    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    /**
     * 给webview添加相关设置
     *
     * @param webView
     */
    @Override
    public void setWebView(WebView webView) {
        //添加JavaScript支持
        webView.getSettings().setJavaScriptEnabled(true);
        //添加Javascript接口
        webViewJavascriptInterface = new WebViewJavascriptInterface(WebViewPresenter.this, webView, true);
        //注册
        BusProvider.getMainProvider().register(webViewJavascriptInterface);
        //添加Javascript接口
        webView.addJavascriptInterface(webViewJavascriptInterface, "android");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webViewJavascriptInterface != null) {
            BusProvider.getMainProvider().unregister(webViewJavascriptInterface);
        }
    }
}
