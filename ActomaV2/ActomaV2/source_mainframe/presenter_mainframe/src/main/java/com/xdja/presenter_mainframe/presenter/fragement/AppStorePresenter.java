package com.xdja.presenter_mainframe.presenter.fragement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.squareup.otto.Subscribe;
import com.xdja.comm.data.AppInfoBean;
import com.xdja.comm.data.AppInfoDao;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.NotifyAppStoreEvent;
import com.xdja.comm.http.OkHttpsClient;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.AppInfoServer;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterFragment;
import com.xdja.presenter_mainframe.cmd.AppStoreCommand;
import com.xdja.presenter_mainframe.ui.ViewAppStore;
import com.xdja.presenter_mainframe.ui.uiInterface.AppStoreVu;
import com.xdja.presenter_mainframe.util.DownloadManager;
import com.xdja.presenter_mainframe.util.WebViewJavascriptInterface;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by chenbing on 2015-7-21.
 */
public class AppStorePresenter extends BasePresenterFragment<AppStoreCommand, AppStoreVu> implements AppStoreCommand {
    private static final String TAG = "Gy: ";
    /**
     * 下载事件管理器
     */
    public DownloadManager manager;
    /**
     * Html5调用本地方法的Javascript接口
     */
    private WebViewJavascriptInterface webViewJavascriptInterface;
    /**
     * 应用市场-前缀地址
     */
    public static String HOST = "https://11.12.110.133:8443/app-store";
    /**
     * 应用市场列表地址
     */
    private String APP_STORE_LIST;

    public static final int HTTP200 = 200;

    @Override
    protected Class<? extends AppStoreVu> getVuClass() {
        return ViewAppStore.class;
    }

    @Override
    protected AppStoreCommand getCommand() {
        return this;
    }

    //[S]modify by lixiaolong on 20160905. fix bug 3674. review by myself.
    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        String url = PreferencesServer.getWrapper(ActomaController.getApp())
                .gPrefStringValue("appStoreUrl");
        if (!TextUtils.isEmpty(url)) {
            HOST = url;
        }
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        BusProvider.getMainProvider().register(this);
//        String url = PreferencesServer.getWrapper(ActomaController.getApp())
//                .gPrefStringValue("appStoreUrl");
//        if (!TextUtils.isEmpty(url)) {
//            HOST = url;
//        }
        initWebView();
    }
    //[E]modify by lixiaolong on 20160905. fix bug 3674. review by myself.

    /**
     * 初始化
     */
    private void initWebView() {
        //修改应用下载状态
        updateAppInfo();
        //获取本地配置文件内应用市场主址
        //HOST = "https://11.12.110.133:8443/app-store";//ConfigurationServer.getDefaultConfig
        // (getActivity()).read("appStoreUrl", "", String.class);
        //拼接应用市场列表地址
        APP_STORE_LIST = HOST + "/appList.htm";
        //加载地址
        //[S]modify by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
        //getVu().loadUrl(APP_STORE_LIST);
        checkUrl();
        //[E]modify by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
    }

    //[S]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.
    @Override
    public void checkUrl(){
        getVu().showCheckUrlView();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    final Request request = new Request.Builder()
                            .url(APP_STORE_LIST)
                            .get()
                            .build();
                    /*OkHttpsClient client = OkHttpsClient.getInstance(getActivity()).getOkHttpClient().build();
                    client.setConnectTimeout(10, TimeUnit.SECONDS);
                    client.setReadTimeout(10, TimeUnit.SECONDS);*/
                    OkHttpClient client = OkHttpsClient.getInstance(getActivity()).getOkHttpClientBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .build();
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response.code());
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                getVu().hideCheckUrlView();
                getVu().showErrorView();
            }

            @Override
            public void onNext(Integer integer) {
                getVu().hideCheckUrlView();
                if (integer == HTTP200) {
                    getVu().loadUrl(APP_STORE_LIST);
                } else {
                    getVu().showErrorView();
                }
            }
        });
    }
    //[E]add by lixiaolong on 20160912. fix bug 3789. review by wangchao1.

    @SuppressLint("SetJavaScriptEnabled")
    /**
     * webview添加相关设置
     *
     * @param webView
     */
    @Override
    public void setWebView(WebView webView) {
        //支持JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        //实例化下载管理器
        manager = new DownloadManager(webView, getActivity());
        //注册
        BusProvider.getMainProvider().register(manager);
        //添加Javascript接口
        webViewJavascriptInterface = new WebViewJavascriptInterface(getActivity(), webView, false);
        //注册
        BusProvider.getMainProvider().register(webViewJavascriptInterface);
        webView.addJavascriptInterface(webViewJavascriptInterface, "android");
    }

    /**
     * 刷新网页
     */
    @Override
    public void refreshWeb() {
        LogUtil.getUtils().i(TAG + "==刷新网页==");
        getVu().refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.getUtils().i(TAG + "==onDestroy==");
        if (manager != null) {
            try {
                manager.cancelAllTask();
                BusProvider.getMainProvider().unregister(manager);
                manager.unRegisterReceiver();
            } catch (Exception e) {
                LogUtil.getUtils().e(e.getMessage());
            }
        }
        if (webViewJavascriptInterface != null) {
            BusProvider.getMainProvider().unregister(webViewJavascriptInterface);
        }
        BusProvider.getMainProvider().unregister(this);
    }

    /**
     * 修改应用下载状态
     */
    private void updateAppInfo() {
        LogUtil.getUtils().i(TAG + "==修改应用下载状态==");
        //若因部分原因程序意外停止,更新数据库内本地正在下载的应用数据信息(正在下载改为暂停下载)
        List<AppInfoBean> list = AppInfoServer.queryAllAppInfo();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getIsDownNow().equals(WebViewJavascriptInterface.DOWNLOAD)) {
                    AppInfoServer.updateAppInfoField(list.get(i).getAppId()
                            , AppInfoDao.FIELD_ISDOWNNOW
                            , WebViewJavascriptInterface.PAUSE_DOWNLOAD);
                }
            }
        }
    }

    /**
     * 接收事件刷新列表数据
     *
     * @param bean
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void notifyAppStore(NotifyAppStoreEvent bean) {
        getVu().refresh();
    }

    public class NullHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
