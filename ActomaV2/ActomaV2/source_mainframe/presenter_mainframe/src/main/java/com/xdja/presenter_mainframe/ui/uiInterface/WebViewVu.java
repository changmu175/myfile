package com.xdja.presenter_mainframe.ui.uiInterface;


import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.WebViewCommand;

/**
 * Created by geyao on 2015/7/14.
 */
public interface WebViewVu extends ActivityVu<WebViewCommand> {
    /**
     * 设置webview
     *
     * @param url 要加载内容的url
     */
    void loadUrl(String url);


}
