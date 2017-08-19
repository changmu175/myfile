package com.xdja.presenter_mainframe.ui;


import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.WebViewCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.WebViewVu;
import com.xdja.presenter_mainframe.util.UnityWebViewSetter;

import butterknife.Bind;

/**
 * Created by geyao on 2015/7/7.
 * 通用webview
 */
@ContentView(R.layout.activity_webview)
public class ViewWebView extends ActivityView<WebViewCommand> implements WebViewVu {
    /**
     * 用于显示内容的webview
     */
    @Bind(R.id.web_view)
    WebView webView;
    /**
     * 等待框
     */
    @Bind(R.id.web_progress)
    ProgressBar webProgressBar;
    /**
     * 加载网页异常视图所在布局
     */
    @Bind(R.id.net_error_layout)
    RelativeLayout netErrorLayout;

    @Override
    public void onCreated() {
        super.onCreated();
        // 初始化webview
        UnityWebViewSetter setter = new UnityWebViewSetter(getActivity()
                , webView, webProgressBar, netErrorLayout);
        setter.initWebView();
        getCommand().setWebView(webView);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /**
     * webview加载地址
     *
     * @param url 要加载内容的url
     */
    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_activity_view_terms_policy);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
