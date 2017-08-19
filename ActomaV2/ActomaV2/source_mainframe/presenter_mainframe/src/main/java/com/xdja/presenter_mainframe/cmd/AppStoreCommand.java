package com.xdja.presenter_mainframe.cmd;


import android.webkit.WebView;

import com.xdja.frame.presenter.mvp.Command;


/**
 * Created by chenbing on 2015-7-21.
 */
public interface AppStoreCommand extends Command {
    /**
     * 给webview添加相关设置
     *
     * @param webView
     */
    void setWebView(WebView webView);

    /**
     * 刷新网页
     */
    void refreshWeb();

    //[S]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
    void checkUrl();
    //[E]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
}
