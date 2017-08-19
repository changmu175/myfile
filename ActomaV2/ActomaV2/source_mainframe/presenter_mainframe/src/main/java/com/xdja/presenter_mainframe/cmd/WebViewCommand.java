package com.xdja.presenter_mainframe.cmd;


import android.webkit.WebView;

import com.xdja.frame.presenter.mvp.Command;


/**
 * Created by geyao on 2015/7/7.
 */
public interface WebViewCommand extends Command {
    /**
     * 给Webview添加相关设置
     * @param webView
     */
    void setWebView(WebView webView);
}
