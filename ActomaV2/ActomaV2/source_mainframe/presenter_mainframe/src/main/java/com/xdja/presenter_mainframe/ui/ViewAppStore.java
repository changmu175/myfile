package com.xdja.presenter_mainframe.ui;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AppStoreCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.AppStoreVu;
import com.xdja.presenter_mainframe.util.UnityWebViewSetter;

import butterknife.Bind;


/**
 * Created by chenbing on 2015-07-21.
 */
@ContentView(value = R.layout.fragment_webview)
public class ViewAppStore extends FragmentSuperView<AppStoreCommand> implements AppStoreVu {
    /**
     * 用于显示内容的webview
     */
    @Bind(R.id.web_view)
    WebView webView;
    /**
     * 进度条
     */
    @Bind(R.id.web_progress)
    ProgressBar webProgress;
    /**
     * 网络错误提示view
     */
    @Bind(R.id.net_error_layout)
    RelativeLayout netErrorLayout;
    //[S]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
    /**
     * 检查地址的进度条
     */
    @Bind(R.id.check_url_layout)
    RelativeLayout checkUrlLayout;
    //[E]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
    /**
     * 刷新组件
     */
    @Bind(R.id.appStore_swipeRefreshLayout)
    SwipeRefreshLayout appStoreSwipeRefreshLayout;

    @Override
    public void onCreated() {
        super.onCreated();
        // 初始化webview
        UnityWebViewSetter setter = new UnityWebViewSetter(getActivity()
                , webView, webProgress, netErrorLayout);
        setter.initWebView();
        //添加相关设置
        getCommand().setWebView(webView);
        //设置刷新控件刷新监听
        appStoreSwipeRefreshLayout.setColorSchemeColors(getColorRes(R.color.golden_title));
        appStoreSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCommand().refreshWeb();
                appStoreSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    /**
     * 刷新网页
     */
    @Override
    public void refresh() {
        //[S]modify by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
        if (TextUtils.isEmpty(webView.getUrl())) {
            getCommand().checkUrl();
        } else {
            webView.reload();
        }
        //[S]modify by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
    }

    //[S]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
    @Override
    public void showErrorView() {
        String errorHtml = "<html><body><h1></h1></body></html>";
        webView.loadData(errorHtml, "text/html", "UTF-8");
        if (netErrorLayout != null) {
            netErrorLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            netErrorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.setVisibility(View.VISIBLE);
                    netErrorLayout.setVisibility(View.GONE);
                    getCommand().checkUrl();
                }
            });
        }
    }

    @Override
    public void showCheckUrlView() {
        if (appStoreSwipeRefreshLayout != null) {
            appStoreSwipeRefreshLayout.setEnabled(false);
        }
        if (checkUrlLayout != null) {
            checkUrlLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideCheckUrlView() {
        if (appStoreSwipeRefreshLayout != null) {
            appStoreSwipeRefreshLayout.setEnabled(true);
        }
        if (checkUrlLayout != null) {
            checkUrlLayout.setVisibility(View.GONE);
        }
    }
    //[E]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.

    @Override
    public <A extends Fragment> void setFragment(A fragment) {

    }

    @Override
    public void onDestroyView() {

    }
}
