package com.xdja.presenter_mainframe.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.xdja.dependence.uitls.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class UnityWebViewSetter {

    private Context cxt;

    private WebView webView;

    private ProgressBar proBar;

    private View netErrorView;
    /**
     * ******** 常量相关 begin ***************
     */
    private final int PROGRESS_MAX = 100;

    /*********** 常量相关 end ****************/

    /**
     * 对webview进行统一的设置
     *
     * @param cxt     上下文句柄
     * @param webView 浏览器控件
     */
    public UnityWebViewSetter(Context cxt, WebView webView) {
        this.cxt = cxt;
        this.webView = webView;
    }

    /**
     * 对webview进行统一的设置
     *
     * @param cxt     上下文句柄
     * @param webView 浏览器控件
     */
    public UnityWebViewSetter(Context cxt, WebView webView, ProgressBar progressBar) {
        this.cxt = cxt;
        this.webView = webView;
        this.proBar = progressBar;
    }

    public UnityWebViewSetter(Context cxt, WebView webView, ProgressBar proBar, View netErrorView) {
        this.cxt = cxt;
        this.webView = webView;
        this.proBar = proBar;
        this.netErrorView = netErrorView;
    }

    public void setProgress(ProgressBar proBar) {
        this.proBar = proBar;
    }
//	public ProgressBar getPrgressBar() {
//		return this.proBar;
//	}

    @SuppressWarnings({"deprecation", "SetJavaScriptEnabled"})
    public void initWebView() {
        this.webView.requestFocus(View.FOCUS_DOWN);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    view.loadUrl(URLDecoder.decode(url, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setWebProgressEnable(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setWebProgressEnable(false);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书
//				super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
                //显示加载错误视图
                showNetErrorView(view, failingUrl, netErrorView);
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    setWebProgressEnable(false);
                } else {
                    if (!getWebProgressEnable())
                        setWebProgressEnable(true);
                    setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//				AlertDialog.Builder b2 = new AlertDialog.Builder(cxt).setTitle(R.string.warnning_text).setMessage(message)
//						.setPositiveButton(R.string.positive_text, new AlertDialog.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								result.confirm();
//							}
//						});
//				b2.setCancelable(false);
//				b2.create();
//				b2.show();
                return true;
            }

            //start: wangchao for 3359
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title) && title.toLowerCase().contains("error")) {
                    LogUtil.getUtils().i("onReceivedTitle title="+title+", url="+view.getUrl());
                    showNetErrorView(view, view.getUrl(), netErrorView);
                }
            }
            //end: wangchao for 3359
        });
        this.webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // 实现下载的代码，这里跳转到其他浏览器下载
                Uri uri = Uri.parse(url);
                // Uri uri = Uri.parse("http://www.abc.com/a.apk");如果只下载单个文件
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                cxt.startActivity(intent);
            }
        });

        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(PluginState.ON);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setPluginState(PluginState.ON);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(true);
        // 支持缩放
        settings.setSupportZoom(false);
        // 设置不显示缩放按钮
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // 支持缓存
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 　优先使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存

        // SMALLEST(50%),
        // SMALLER(75%),
        // NORMAL(100%),
        // LARGER(150%),
        // LARGEST(200%)
        // .setTextSize(TextSize.LARGER);
        // settings.setTextZoom(120);

    }

    /**
     * 设置进度条是否可见
     *
     * @param isEnable 是否可见
     */
    private void setWebProgressEnable(boolean isEnable) {
        if (this.proBar != null) {
            this.proBar.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 获取进度条可见状态
     *
     * @return 当前是否可见
     */
    private boolean getWebProgressEnable() {
        return this.proBar != null && this.proBar.getVisibility() == View.VISIBLE;
    }

    /**
     * 设置进度条进度
     *
     * @param progress 要设置的进度（max = 100）
     */
    private void setProgress(int progress) {
        if (this.proBar != null) {
            this.proBar.setProgress(progress);
        }
    }

    /**
     * 显示网络错误视图
     *
     * @param webView   当前显示网页的webview
     * @param url       当前加载出错的地址
     * @param errorView 网络错误视图
     */
    private void showNetErrorView(final WebView webView, final String url, final View errorView) {
        String errorHtml = "<html><body><h1></h1></body></html>";
        webView.loadData(errorHtml, "text/html", "UTF-8");
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            errorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                    errorView.setVisibility(View.GONE);
                }
            });
        }
    }
}
