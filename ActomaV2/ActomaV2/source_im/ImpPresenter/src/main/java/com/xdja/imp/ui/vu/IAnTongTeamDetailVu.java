package com.xdja.imp.ui.vu;

import android.webkit.WebView;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IAnTongTeamDetailCommand;

/**
 * Created by cxp on 2015/8/10.
 */
public interface IAnTongTeamDetailVu extends ActivityVu<IAnTongTeamDetailCommand> {

    /**
     * webView是否有上一级
     *
     * @return
     */
    boolean isWebViewCanGoBAck();

    /**
     * 后退到上一级
     */
    void webViewGoBack();

    /**
     * 控制进度条的显示
     *
     * @param isVisible
     */
    void setProgressViewVisible(int isVisible);

    /**
     * 控制加载试图的显示
     *
     * @param isVisible
     */
    void setNetworkLoadViewVisible(int isVisible);

    /**
     * 获取WebView加载进度
     */
    int getWebViewProgress();

    /**
     * WebView停止加载
     */
    void webViewStopLoading();

    /**
     * 获得当前加载的WebView
     *
     * @return
     */
    WebView getCurrentWebView();

    /**
     * 显示Toast提示信息
     *
     * @param msg
     */
    void showToastWithMessage(String msg);

}
