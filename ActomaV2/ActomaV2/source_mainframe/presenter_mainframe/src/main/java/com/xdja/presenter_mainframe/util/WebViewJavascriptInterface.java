package com.xdja.presenter_mainframe.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.data.AppInfoBean;
import com.xdja.comm.data.AppInfoDao;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.server.AccountServer;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.AppInfoServer;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.dependence.uitls.NetworkUtil;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.BaseResponse;
import com.xdja.presenter_mainframe.presenter.activity.FeedBackPresenter;
import com.xdja.presenter_mainframe.presenter.activity.WebViewPresenter;
import com.xdja.presenter_mainframe.presenter.fragement.AppStorePresenter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by geyao on 2015/7/21.
 * 向webview提供的接口
 */
@SuppressWarnings({"NumericCastThatLosesPrecision"})
public class WebViewJavascriptInterface {
    private static final String TAG = "Gy: ";
    /**
     * 常见问题-详情地址
     */
    public static String SEVER_HOST;
    /**
     * 应用详情-前缀地址
     */
    public static String APP_INFO_HOST;
    /**
     * 状态-正在下载
     */
    public static final String DOWNLOAD = "1";
    /**
     * 状态-下载完成
     */
    public static final String COMPLETE_DOWNLOAD = "2";
    /**
     * 状态-暂停下载
     */
    public static final String PAUSE_DOWNLOAD = "3";
    /**
     * 状态-等待下载
     */
    public static final String WAIT_DOWNLOAD = "4";
    /**
     * 状态-安装完成
     */
    public static final String INSTALL_COMPLETE = "5";
    /**
     * 上下文句柄
     */
    private Context context;
    /**
     * 当前显示网页的webview
     */
    private WebView webView;
    /**
     * 是否是详情页面
     */
    private boolean isDetailPage = false;

    public WebViewJavascriptInterface(Context context, WebView webView, boolean isDetailPage) {
        this.context = context;
        this.webView = webView;
        this.isDetailPage = isDetailPage;
        //if (TextUtils.isEmpty(AppStorePresenter.HOST)) {
//            AppStorePresenter.HOST = ConfigurationServer.getDefaultConfig(context)
//                    .read("appStoreUrl", "", String.class);
        //}
        APP_INFO_HOST = AppStorePresenter.HOST;
        SEVER_HOST = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("aboutAtUrl");
    }

    /**
     * 获取本地设备的安通账号
     *
     * @return 设备的安通账号
     */
    @JavascriptInterface
    public String getAccount() {
        AccountBean bean = AccountServer.getAccount();
        LogUtil.getUtils().i(TAG + "==获取本地设备的安通账号==" + bean.getAccount());
        return bean.getAccount();
    }

    /**
     * webview 获取终端已安装的应用列表
     *
     * @return 终端已安装的应用列表集合
     */
    @JavascriptInterface
    public String getInstalledApps() {
        String appInfoList = getAppInfoList();
        LogUtil.getUtils().i(TAG + "==获取终端已安装的应用列表==" + appInfoList);
        return appInfoList;
    }

    /**
     * webview 获取应用状态
     *
     * @param packageName 应用包名
     * @return 应用信息
     */
    @JavascriptInterface
    public String getAppStatus(String packageName) {
        String appInfo = getAppInfo(packageName);
        LogUtil.getUtils().i(TAG + "==获取应用状态==packageName: " + packageName + " getAppStatus: " + appInfo);
        return appInfo;
    }

    /**
     * webview 下载接口
     *
     * @param appId       服务器端的appId
     * @param downloadUrl 下载地址
     * @param packageName 应用包名
     * @param versionCode 版本号
     * @param versionName 版本名称
     * @param appName     应用名称
     * @param appSize     应用大小
     * @return 返回处理结果
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    @JavascriptInterface
    public String downloadApp(long appId, String downloadUrl, String packageName,
                              String versionCode, String versionName, String appName,
                              long appSize) {

//        if (!Function.isNetConnect(context)) {
//            // 提示未连接网络
//            XToast.show(context,context.getString(R.string.feedback_network_error));
//            return new Gson().toJson(new BaseResponse(false, "网络异常"));
//        }

        return exeDownload(appId, downloadUrl, packageName, versionCode, versionName, appName, appSize);
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private String exeDownload(long appId, String downloadUrl, String packageName, String versionCode, String versionName, String appName, long appSize) {
        LogUtil.getUtils().i(TAG + "==下载接口==appId: " + appId + " downloadUrl: " + downloadUrl +
                " packageName: " + packageName + " versionCode: " + versionCode + " versionName: " +
                versionName + " appName: " + appName + " appSize: " + appSize);
        //获取数据库对应appId的应用信息
        AppInfoBean bean = AppInfoServer.queryAppInfo(String.valueOf(appId));
        //判断信息是否为空
        if (bean == null) {//若信息为空 则下载
            boolean result = NetworkUtil.isMobileConnected(context);
            if (result) {
                showStartDownMobileNetDialog(context, appId, downloadUrl, packageName, versionCode, versionName, appName, appSize);
            } else {
                startDownAppInfo(appId, downloadUrl, packageName, versionCode, versionName, appName, appSize);
            }
            //返回结果
            return new Gson().toJson(new BaseResponse(true, ActomaController.getApp().getString(R.string.exe_dowload)));
        } else {//若不为空 则证明有对应的安装包
            //判断安装包是否存在
            String filePath = DownloadManager.FILEPATH + bean.getFileName() + ".apk";
            File file = new File(filePath);
            if (file.exists()) {//存在
                //判断当前下载状态
                if (bean.getIsDownNow().equals(DOWNLOAD)) {//正在下载 执行暂停操作
                    //发送事件执行暂停下载操作
                    PauseBean pauseBean = new PauseBean();
                    pauseBean.setAppId(String.valueOf(appId));
                    BusProvider.getMainProvider().post(pauseBean);
                    return new Gson().toJson(new BaseResponse(true, ActomaController.getApp().getString(R.string.exe_pause_dowload)));
                } else if (bean.getIsDownNow().equals(COMPLETE_DOWNLOAD)) {//下载完成 执行安装操作
                    //获取安装包信息
                    PackageManager packageManager = context.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath
                            , PackageManager.GET_ACTIVITIES);
                    //判断安装包版本号跟最新版本号是否一致
                    if (String.valueOf(packageInfo.versionCode).equals(versionCode)) {//版本号一致
                        //安装APP
                        installApp(String.valueOf(appId), appName);
                        return new Gson().toJson(new BaseResponse(true, ActomaController.getApp().getString(R.string.exe_install)));
                    } else {//版本号不一致
                        //实例化应用下载信息
                        boolean result = NetworkUtil.isMobileConnected(context);
                        if (result) {
                            showStartDownMobileNetDialog(context, appId, downloadUrl, packageName, versionCode, versionName, appName, appSize);
                        } else {
                            startDownAppInfo(appId, downloadUrl, packageName, versionCode, versionName, appName, appSize);
                        }

                        //返回结果
                        return new Gson().toJson(new BaseResponse(true, ActomaController.getApp().getString(R.string.download_and_update)));
                    }
                } else if (bean.getIsDownNow().equals(PAUSE_DOWNLOAD)) {//暂停下载 执行继续下载操作

                    boolean result = NetworkUtil.isMobileConnected(context);
                    if (result) {
                        showContinueDownMobileNetDialog(context, bean, file);
                    } else {
                        continueDownloadAppInfo(bean, file);
                    }

                    return new Gson().toJson(new BaseResponse(true, ActomaController.getApp().getString(R.string.download_app_mobile_net_continue)));
                } else {
                    return new Gson().toJson(new BaseResponse(true, ActomaController.getApp().getString(R.string.unknown_oper)));
                }
            } else {//安装包不存在
                //实例化应用下载信息
                boolean result = NetworkUtil.isMobileConnected(context);
                if (result) {
                    showStartDownMobileNetDialog(context, appId, downloadUrl, packageName, versionCode, versionName, appName, appSize);
                } else {
                    startDownAppInfo(appId, downloadUrl, packageName, versionCode, versionName, appName, appSize);
                }
                //返回结果
                return new Gson().toJson(new BaseResponse(true, ActomaController.getApp().getString(R.string.unknown_download_oper)));
            }
        }
    }


    /**
     * mhandler发消息
     */
    private final Handler mHandler = new WebViewJSHandler(this);

    public void handMsg(Message msg) {
        if (msg != null && msg.obj != null) {
            String appid = (String)msg.obj;
            if (!TextUtils.isEmpty(appid)) {
                webView.loadUrl("javascript:changeDownloadStatus(" + appid
                        + "," + DownloadManager.DOWNLOAD_PAUSE + ")");
            }
        }
    }

    private static class WebViewJSHandler extends Handler{

        WeakReference<WebViewJavascriptInterface> mWebViewJavascriptInterface;

        public WebViewJSHandler(WebViewJavascriptInterface webViewJavascriptInterface) {
            mWebViewJavascriptInterface = new WeakReference<>(webViewJavascriptInterface);
        }

        @Override
        public void handleMessage(Message msg) {
            WebViewJavascriptInterface webViewJavascriptInterface = mWebViewJavascriptInterface.get();
            if (!ObjectUtil.objectIsEmpty(webViewJavascriptInterface)) {
                webViewJavascriptInterface.handMsg(msg);
            }
        }
    }
    /**
     * 判断包名对应的应用是否存在
     *
     * @param context     上下文句柄
     * @param packageName 包名
     * @return 是否存在
     */
    public static boolean checkPackage(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName
                    , PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * webview 打开应用
     *
     * @param packageName 应用包名
     * @return 返回处理结果
     */
    @JavascriptInterface
    public String openApp(String packageName) {
        BaseResponse baseResponse = new BaseResponse();
        if (checkPackage(context, packageName) == true) {
            baseResponse.setMsg(ActomaController.getApp().getString(R.string.app_exist));
            baseResponse.setResult(true);
            startAppforPackageName(packageName);
        } else {
            baseResponse.setMsg(ActomaController.getApp().getString(R.string.app_no_exist));
            baseResponse.setResult(false);
        }
        String result = new Gson().toJson(baseResponse);
        LogUtil.getUtils().i(TAG + "==打开应用==packageName:　" + packageName + " " + result);
        return result;
    }

    /**
     * 打开Webview
     *
     * @param AppId 页面标示，在整个app中应该唯一，目前暂无具体作用传入随机值 暂无用途
     * @param url   打开页面的url
     * @param title 标题
     * @return 返回处理结果
     */
    @JavascriptInterface
    public String openWindow(String AppId, String url, String title) {
        Intent intent = new Intent(context, WebViewPresenter.class);
        intent.putExtra(WebViewPresenter.TITLE, title);
        intent.putExtra(WebViewPresenter.WEBURL, APP_INFO_HOST + url);
        context.startActivity(intent);
        BaseResponse response = new BaseResponse(true, ActomaController.getApp().getString(R.string.open_web));
        String result = new Gson().toJson(response);
        LogUtil.getUtils().i(TAG + "==打开Webview==AppId:　" + AppId + " url: " + url + " title: " +
                title + " " + result);
        return result;
    }

    /**
     * 打开常见问题详情Webview
     *
     * @param AppId 页面标示，在整个app中应该唯一，目前暂无具体作用传入随机值  暂无用途
     * @param url   打开页面的url
     * @param title 标题
     * @return 返回处理结果
     */
    @SuppressWarnings("UnusedParameters")
    @JavascriptInterface
    public String openQuestionInfoWindow(String AppId, String url, String title) {
        if (TextUtils.isEmpty(SEVER_HOST)) {
            SEVER_HOST = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("aboutAtUrl");
        }
        Intent intent = new Intent(context, WebViewPresenter.class);
        intent.putExtra(WebViewPresenter.TITLE, title);
        intent.putExtra(WebViewPresenter.WEBURL, SEVER_HOST + url);
        context.startActivity(intent);
        BaseResponse response = new BaseResponse(true, ActomaController.getApp().getString(R.string.open_web));
        String result = new Gson().toJson(response);
        return result;
    }

    /**
     * 打开广告位webview
     *
     * @param AppId 页面标示，在整个app中应该唯一，目前暂无具体作用传入随机值
     * @param url   打开页面的url
     * @return 返回处理结果
     */
    @SuppressWarnings("UnusedParameters")
    @JavascriptInterface
    public String openADWindow(String AppId, String url) {
        Intent i = new Intent();
        i.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        i.setData(uri);
        context.startActivity(i);
        BaseResponse response = new BaseResponse(true, ActomaController.getApp().getString(R.string.open_web));
        String result = new Gson().toJson(response);
        return result;
    }

    /**
     * 安装应用
     *
     * @param appId    服务器端的appId
     * @param fileName apk名称
     * @return 安装结果
     */
    public String installApp(String appId, String fileName) {
        AppInfoBean appInfoBean = AppInfoServer.queryAppInfo(appId);
        if (appInfoBean != null) {
            // 通过Intent安装APK文件
            Intent i = new Intent(Intent.ACTION_VIEW);
            File file = new File(DownloadManager.FILEPATH + fileName + ".apk");
            i.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            context.startActivity(i);
            return new Gson().toJson(new BaseResponse(false, ActomaController.getApp().getString(R.string.start_install_app)));
        } else {
            return new Gson().toJson(new BaseResponse(false, ActomaController.getApp().getString(R.string.install_app_failed)));
        }
    }

    /**
     * 获取手机内已安装应用信息集合
     */
    public String getAppInfoList() {
        List<AppInfoBean> list = getLocalAppInfoList();
        return new Gson().toJson(list);
    }

    /**
     * 获取包名对应应用详情
     *
     * @param packageName 包名
     * @return 包名对应应用详情json
     */
    private String getAppInfo(String packageName) {
        List<AppInfoBean> result = getLocalAppInfoList();
        AppInfoBean app = null;
        for (int i = 0; i < result.size(); i++) {
            AppInfoBean appInfoBean = result.get(i);
            if (appInfoBean.getPackageName().equals(packageName)) {
                int percentage = 0;
                if (appInfoBean.getIsDownNow().equals(DOWNLOAD)
                        || appInfoBean.getIsDownNow().equals(PAUSE_DOWNLOAD)) {
                    //计算应用下载百分比
                    percentage = (int) (Double.parseDouble(appInfoBean.getDownSize())
                            / Double.parseDouble(appInfoBean.getAppSize()) * 100);
                }
                //实例化应用信息
                app = new AppInfoBean(appInfoBean.getAppId(), appInfoBean.getDownloadUrl()
                        , appInfoBean.getPackageName()
                        , appInfoBean.getVersionName(), appInfoBean.getVersionCode()
                        , appInfoBean.getState()
                        , appInfoBean.getIsHaveApk(), appInfoBean.getIsDownNow()
                        , appInfoBean.getDownSize()
                        , appInfoBean.getFileName(), appInfoBean.getAppSize()
                        , String.valueOf(percentage));
//                break;
            }
        }
        return new Gson().toJson(app);
    }


    /**
     * 获取本地已安装应用信息集合
     */
    private List<AppInfoBean> getLocalAppInfoList() {
        List<AppInfoBean> result = new ArrayList<>();
        AppInfoBean bean;
        //获取本地已安装应用信息数据集合
        List<PackageInfo> list1 = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < list1.size(); i++) {
            PackageInfo packageInfo = list1.get(i);
            //读取不为系统应用的应用信息
//            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            bean = new AppInfoBean("", "", packageInfo.packageName
                    , String.valueOf(packageInfo.versionName)
                    , String.valueOf(packageInfo.versionCode)
                    , String.valueOf(true), String.valueOf(false)
                    , INSTALL_COMPLETE, "", "", "", "");
            result.add(bean);
//            }
        }
        //获取数据库内的应用信息集合
        List<AppInfoBean> list2 = AppInfoServer.queryAllAppInfo();
        if (list2 != null) {
            for (int i = 0; i < list2.size(); i++) {
                AppInfoBean appInfoBean = list2.get(i);
                //计算应用下载百分比
                int percentage = (int) (Double.parseDouble(appInfoBean.getDownSize())
                        / Double.parseDouble(appInfoBean.getAppSize()) * 100);
                bean = new AppInfoBean(appInfoBean.getAppId(), appInfoBean.getDownloadUrl()
                        , appInfoBean.getPackageName(), appInfoBean.getVersionName()
                        , appInfoBean.getVersionCode(), appInfoBean.getState()
                        , appInfoBean.getIsHaveApk(), appInfoBean.getIsDownNow()
                        , appInfoBean.getDownSize(), appInfoBean.getFileName()
                        , appInfoBean.getAppSize(), String.valueOf(percentage));
                result.add(bean);
            }
        }
        return result;
    }

    /**
     * 通过包名打开对应应用
     *
     * @param packagename 要打开的应用包名
     */
    private void startAppforPackageName(String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * 暂停事件参数
     */
    public static class PauseBean {
        private String appId;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }
    }

    /**
     * 继续事件参数
     */
    public static class ContinueBean {
        private AppInfoBean appInfoBean;

        public AppInfoBean getAppInfoBean() {
            return appInfoBean;
        }

        public void setAppInfoBean(AppInfoBean appInfoBean) {
            this.appInfoBean = appInfoBean;
        }
    }


    @Subscribe
    public void doChangeWebUI(DownloadManager.LoadUrlBean bean) {
        switch (bean.getType()) {
            case DownloadManager.START://开始下载
                LogUtil.getUtils().i(TAG + "==开始下载==");
                webView.loadUrl("javascript:changeDownloadStatus(" + bean.getAppId()
                        + "," + DownloadManager.DOWNLOAD_KNOW + ")");
                break;
            case DownloadManager.COMPLETE://下载完成
                LogUtil.getUtils().i(TAG + "==下载完成==");
                webView.loadUrl("javascript:changeDownloadStatus(" + bean.getAppId()
                        + "," + DownloadManager.DOWNLOAD_COMPLETE + ")");
                break;
            case DownloadManager.PAUSE://暂停下载
                LogUtil.getUtils().i(TAG + "==暂停下载==");
                webView.loadUrl("javascript:changeDownloadStatus(" + bean.getAppId()
                        + "," + DownloadManager.DOWNLOAD_PAUSE + ")");
                break;
            case DownloadManager.FAIL://下载失败
                LogUtil.getUtils().i(TAG + "==下载失败==");
                //修改因某些原因造成的下载失败 状态改为暂停
                AppInfoServer.updateAppInfoField(bean.getAppId(),
                        AppInfoDao.FIELD_ISDOWNNOW,
                        PAUSE_DOWNLOAD);
                XToast.show(context, ActomaController.getApp().getString(R.string.download_stoped));
                webView.loadUrl("javascript:changeDownloadStatus(" + bean.getAppId()
                        + "," + DownloadManager.DOWNLOAD_FAIL + ")");
                break;
            case DownloadManager.PROGRESS://更新进度
                LogUtil.getUtils().i("更新进度");
                if (isDetailPage) {
                    LogUtil.getUtils().i("详情页更新进度" + bean.getPercentage());
                    webView.loadUrl("javascript:updateDownloadProgress(" + bean.getAppId()
                            + "," + bean.getPercentage() + "," + bean.getDownSize() + ")");
                }
                break;
            case DownloadManager.INSTALL://安装完成
                LogUtil.getUtils().i(TAG + "==安装完成==");
                webView.loadUrl("javascript:changeDownloadStatus(" + bean.getAppId()
                        + "," + DownloadManager.INSTALL_COMPLETE + ")");
                break;
        }
    }

    /*=================================以上为应用市场所用Javascript代码=======================================*/

    /**
     * 打开问题反馈页面
     */
    @JavascriptInterface
    public void openFeedBack() {
        context.startActivity(new Intent().setClass(context, FeedBackPresenter.class));
    }

    /**
     * 判断是基础版还是增强版
     *
     * @return true-基础版 false-增强版
     */
    @JavascriptInterface
    public boolean isBaseVersion() {
        return !UniversalUtil.isXposed();
    }

    /**
     * 是否能滚动
     *
     * @param isCanScroll
     */
    @JavascriptInterface
    @SuppressLint("ClickableViewAccessibility")
    public void isCanScroll(final boolean isCanScroll) {
        LogUtil.getUtils().i("====>isCanScroll");
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (!isCanScroll) {
                            webView.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        webView.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 数据网络下载的提示框
     * @param context
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    private void showStartDownMobileNetDialog(Context context, final long appId, final String downloadUrl, final String packageName, final String versionCode, final String versionName, final String appName, final long appSize) {
            //检测是否为2G/3G/4G网络
            final CustomDialog customDialog = new CustomDialog(context);
            customDialog.setTitle(R.string.download_app_mobile_net_tip)
                    .setMessage(R.string.download_app_mobile_net_message)
                            // 设置内容
                    .setPositiveButton(context.getResources().getString(R.string.download_app_mobile_net_continue),// 设置确定按钮
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startDownAppInfo(appId, downloadUrl,packageName, versionCode, versionName, appName, appSize);
                                    customDialog.dismiss();
                                }
                            })
                    .setNegativeButton(context.getResources().getString(R.string.download_app_mobile_net_cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    customDialog.dismiss();// 关闭进度对话框
                                    sendMsg(appId + "");
                                }
                            }).setCancelable(true).show();
    }

    /**
     * 数据网络下载的提示框
     * @param context
     */
    private void showContinueDownMobileNetDialog(Context context, final AppInfoBean appInfoBean, final File file){
            //检测是否为2G/3G/4G网络
            final CustomDialog customDialog = new CustomDialog(context);
            customDialog.setTitle(R.string.download_app_mobile_net_tip)
                    .setMessage(R.string.download_app_mobile_net_message)
                            // 设置内容
                    .setPositiveButton(context.getResources().getString(R.string.download_app_mobile_net_continue),// 设置确定按钮
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    continueDownloadAppInfo(appInfoBean, file);
                                    customDialog.dismiss();
                                }
                            })
                    .setNegativeButton(context.getResources().getString(R.string.download_app_mobile_net_cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    customDialog.dismiss();// 关闭进度对话框
                                    sendMsg(appInfoBean.getAppId());
                                }
                            }).setCancelable(true).show();
    }

    /**
     * 发送消息
     * @param appId
     */
    private void sendMsg(String appId) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = appId;
        mHandler.sendMessage(msg);
    }

    /**
     * 开始下载
     * @param appId
     * @param downloadUrl
     * @param packageName
     * @param versionCode
     * @param versionName
     * @param appName
     * @param appSize
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    private void startDownAppInfo(long appId, String downloadUrl, String packageName, String versionCode, String versionName, String appName, long appSize) {
        //实例化应用下载信息
        AppInfoBean appInfoBean = new AppInfoBean(String.valueOf(appId), downloadUrl
                , packageName, versionName, versionCode, String.valueOf(false)
                , String.valueOf(false), WAIT_DOWNLOAD, "0", appName
                , String.valueOf(appSize), "0");
        //发送事件执行下载操作
        BusProvider.getMainProvider().post(appInfoBean);
    }

    /**
     * 继续下载文件信息
     * @param bean
     * @param  file FIle
     */
    private void continueDownloadAppInfo(AppInfoBean bean, File file) {
        //获取本地已下载的文件大小
        String downSize = String.valueOf(file.length());
        //计算应用下载百分比
        int percentage = (int) (Double.parseDouble(downSize)
                / Double.parseDouble(bean.getAppSize()) * 100);
        //开始下载  实例化应用下载信息
        AppInfoBean appInfoBean = new AppInfoBean(bean.getAppId(), bean.getDownloadUrl()
                , bean.getPackageName(), bean.getVersionName(), bean.getVersionCode()
                , bean.getState(), bean.getIsHaveApk(), DOWNLOAD
                , String.valueOf(file.length())
                , bean.getFileName(), bean.getAppSize(), String.valueOf(percentage));
        //发送事件执行下载操作
        ContinueBean continueBean = new ContinueBean();
        continueBean.setAppInfoBean(appInfoBean);
        BusProvider.getMainProvider().post(continueBean);
    }

}

