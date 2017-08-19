package com.xdja.imp.ui.vu;

import android.webkit.WebView;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.imp.presenter.command.IAnTongTeamOperationCommand;

/**
 * Created by cxp on 2015/8/7.
 */
public interface IAnTongTeamOperationVu extends ActivityVu<IAnTongTeamOperationCommand> {

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
    /*=================modify by gy 2015-10-27 start=======================*/

    /**
     * 更新进度条进度
     *
     * @param progress 进度
     */
    void setWebViewProgress(int progress);
    /*=================modify by gy 2015-10-27 end=======================*/
}
