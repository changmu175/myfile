package com.xdja.presenter_mainframe.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.data.AppInfoBean;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.AppInfoServer;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.BaseResponse;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geyao on 2015/7/30.
 * 下载管理器
 */
public class DownloadManager {
    private static final String TAG = "Gy: ";
    /*======================应用下载=======================*/
    /**
     * 开始
     */
    public static final int START = 0;
    /**
     * 完成
     */
    public static final int COMPLETE = 1;
    /**
     * 暂停
     */
    public static final int PAUSE = 2;
    /**
     * 失败
     */
    public static final int FAIL = 3;
    /**
     * 进度
     */
    public static final int PROGRESS = 4;
    /**
     * 安装
     */
    public static final int INSTALL = 5;
    /**
     * 消息
     */
    private final Handler handler = new DonwloadManagerHandler(this);
    public static class DonwloadManagerHandler extends Handler {

        private WeakReference<DownloadManager> mDownloadManager;

        public DonwloadManagerHandler(DownloadManager downloadManager) {
            mDownloadManager = new WeakReference<>(downloadManager);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadManager downloadManager = mDownloadManager.get();
            if (!ObjectUtil.objectIsEmpty(downloadManager)) {
                downloadManager.postBean(msg.what, String.valueOf(msg.arg1), String.valueOf(msg.arg2), String.valueOf(msg.obj));
            }
        }
    }

    /*======================应用下载=======================*/

    /**
     * 广播接收器-用于监听应用安装完成的广播
     */
    private BroadcastReceiver receiver;
    /**
     * 文件存储总目录
     */
    public static String FILEPATH;
    /**
     * 正在下载
     */
    public static int DOWNLOAD_KNOW = 1;
    /**
     * 暂停下载
     */
    public static int DOWNLOAD_PAUSE = 2;
    /**
     * 继续下载
     */
    public static int DOWNLOAD_CONTINUE = 3;
    /**
     * 取消下载
     */
    public static int DOWNLOAD_CANCEL = 4;
    /**
     * 下载完成
     */
    public static int DOWNLOAD_COMPLETE = 5;
    /**
     * 安装完成
     */
    public static int INSTALL_COMPLETE = 6;
    /**
     * 安装失败
     */
    public static int INSTALL_FAIL = 7;
    /**
     * 下载失败
     */
    public static int DOWNLOAD_FAIL = 7;


    /**
     * 页面
     */
    private WebView webView;
    /**
     * 上下文句柄
     */
    private Context context;
    /**
     * 下载队列
     */
    public List<AppInfoBean> list = new ArrayList<>();
    /**
     * 下载线程集合
     */
    public List<Map<String, DownloadImp>> downList = new ArrayList<>();
    /**
     * 文件下载接口
     */
    private IDownload download;
    /**
     * 限制
     */
    private static int MAX = 3;
    /**
     * 当前下载数量
     */
    private int count = 0;
    /**
     * 当前下载总大小
     */
    private long allDownSize = 0;

    public DownloadManager(WebView webView, Context context) {
        this.webView = webView;
        this.context = context;
        FILEPATH = ActomaApplication.getInstance().getObbDir() + "/";
        initReceiver();
    }

    /**
     * 下载应用
     */
    public void downLoadApp(final AppInfoBean bean) {
        //判断未超出限制且下载队列内有下载事件则进行下载
        if (count < MAX) {
            //增加下载次数
            count = count + 1;
            //计算当前全部下载应用大小
            allDownSize = allDownSize + Long.parseLong(bean.getAppSize());
            //实例化下载事件
            download = new DownloadImp(bean, bean.getFileName(), context.getApplicationContext());
            //加入到下载线程集合中
            Map<String, DownloadImp> map = new HashMap<>();
            map.put(bean.getAppId(), (DownloadImp) download);
            downList.add(map);
            //开始下载
            download.start(new IDownload.DownloadCallBack() {
                @Override
                public void onStart() {
                    LogUtil.getUtils().i(TAG + "==开始下载==" + bean.getFileName());
                    //发送本地已开始下载消息
                    Message message = handler.obtainMessage();
                    message.what = START;
                    message.arg1 = Integer.parseInt(bean.getAppId());
                    handler.sendMessage(message);
                    //移除队列内对应的应用下载信息
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getAppId().equals(bean.getAppId())) {
                            list.remove(i);
                            break;
                        }
                    }
                }

                @SuppressWarnings("NumericCastThatLosesPrecision")
                @Override
                public void onProgress(long size) {
                    //获取应用大小
                    long appSize = Long.valueOf(bean.getAppSize());
                    //计算应用下载百分比
                    int result = (int) ((double) size / (double) appSize * 100);
                    //更新数据库
                    AppInfoBean appInfoBean = new AppInfoBean(bean.getAppId(), bean.getDownloadUrl()
                            , bean.getPackageName(), bean.getVersionName(), bean.getVersionCode()
                            , String.valueOf(false), String.valueOf(false), WebViewJavascriptInterface.DOWNLOAD
                            , String.valueOf(size), bean.getFileName(), bean.getAppSize(), String.valueOf(result));
                    //入库信息(保存已下载的文件最新信息)
                    AppInfoServer.updateAppInfo(appInfoBean);
                    //发送更新进度消息
                    Message message = handler.obtainMessage();
                    message.what = PROGRESS;
                    message.arg1 = Integer.parseInt(bean.getAppId());
                    message.arg2 = result;
                    message.obj = size;
                    handler.sendMessage(message);
                }

                @SuppressWarnings("NumericCastThatLosesPrecision")
                @Override
                public void onStop() {
                    LogUtil.getUtils().i(TAG + "==暂停下载==" + bean.getFileName());
                    //获取本地已下载的文件大小
                    File file = new File(FILEPATH + bean.getFileName() + ".apk");
                    String downSize = String.valueOf(file.length());
                    //计算应用下载百分比
                    int percentage = (int) (Double.parseDouble(downSize) / Double.parseDouble(bean.getAppSize()) * 100);
                    //实例化应用信息
                    AppInfoBean appInfoBean = new AppInfoBean(bean.getAppId(), bean.getDownloadUrl()
                            , bean.getPackageName(), bean.getVersionName(), bean.getVersionCode()
                            , String.valueOf(false), String.valueOf(false), WebViewJavascriptInterface.PAUSE_DOWNLOAD
                            , downSize, bean.getFileName(), bean.getAppSize(), String.valueOf(percentage));
                    //入库信息(保存已下载的文件最新信息)
                    AppInfoServer.insertAppInfo(appInfoBean);
                    //发送本地已停止消息
                    Message message = handler.obtainMessage();
                    message.what = PAUSE;
                    message.arg1 = Integer.parseInt(bean.getAppId());
                    handler.sendMessage(message);
                    //减少当前下载数量
                    count = count - 1;
                    //移除对应的下载线程集合
                    if (!downList.isEmpty()) {
                        for (int i = 0; i < downList.size(); i++) {
                            Map<String, DownloadImp> map = downList.get(i);
                            if (map.containsKey(bean.getAppId())) {
                                downList.remove(i);
                                break;
                            }
                        }
                    }
                    //下载队列不为空 下载
                    if (!list.isEmpty()) {
                        //发送下载事件
                        BusProvider.getMainProvider().post(list.get(0));
                    }
                }

                @Override
                public void onComplete() {
                    LogUtil.getUtils().i(TAG + "==下载完成==" + bean.getFileName());
                    //发送更新最后100%进度消息
                    Message message = handler.obtainMessage();
                    message.what = PROGRESS;
                    message.arg1 = Integer.parseInt(bean.getAppId());
                    message.arg2 = 100;
                    message.obj = bean.getAppSize();
                    handler.sendMessage(message);
                    //减少当前下载数量
                    count = count - 1;
                    //实例化应用信息
                    AppInfoBean appInfoBean = new AppInfoBean(bean.getAppId(), bean.getDownloadUrl()
                            , bean.getPackageName(), bean.getVersionName(), bean.getVersionCode()
                            , String.valueOf(false), String.valueOf(true), WebViewJavascriptInterface.COMPLETE_DOWNLOAD
                            , String.valueOf(bean.getAppSize()), bean.getFileName(), bean.getAppSize(), "100");
                    //修改信息状态-下载完成
                    boolean result = AppInfoServer.updateAppInfo(appInfoBean);
                    if (result == true) {
                        //移除对应的下载线程集合
                        if (!downList.isEmpty()) {
                            for (int i = 0; i < downList.size(); i++) {
                                Map<String, DownloadImp> map = downList.get(i);
                                if (map.containsKey(bean.getAppId())) {
                                    downList.remove(i);
                                    break;
                                }
                            }
                        }
                        //下载队列不为空下载
                        if (!list.isEmpty()) {
                            //发送下载事件
                            BusProvider.getMainProvider().post(list.get(0));
                        }
                        //发送本地已下载完成消息
                        Message msg = handler.obtainMessage();
                        msg.what = COMPLETE;
                        msg.arg1 = Integer.parseInt(bean.getAppId());
                        handler.sendMessage(msg);
                        //执行安装操作
                        installApp(bean.getAppId(), bean.getFileName());
                        LogUtil.getUtils().i("==下载完成安装==" + bean.getFileName());
                    }
                }

                @SuppressWarnings("NumericCastThatLosesPrecision")
                @Override
                public void onError(Throwable throwable) {
                    LogUtil.getUtils().i(TAG + "==下载出错==" + bean.getFileName());
                    //获取本地已下载的文件大小
                    File file = new File(FILEPATH + bean.getFileName() + ".apk");
                    String downSize = String.valueOf(file.length());
                    //计算应用下载百分比
                    int percentage = (int) (Double.parseDouble(downSize) / Double.parseDouble(bean.getAppSize()) * 100);
                    //实例化应用信息
                    AppInfoBean appInfoBean = new AppInfoBean(bean.getAppId(), bean.getDownloadUrl()
                            , bean.getPackageName(), bean.getVersionName(), bean.getVersionCode()
                            , String.valueOf(false), String.valueOf(false), WebViewJavascriptInterface.PAUSE_DOWNLOAD
                            , downSize, bean.getFileName(), bean.getAppSize(), String.valueOf(percentage));
                    //入库信息(保存已下载的文件最新信息)
                    AppInfoServer.insertAppInfo(appInfoBean);
                    //减少当前下载数量
                    count = count - 1;
                    //移除对应的下载线程集合
                    if (!downList.isEmpty()) {
                        for (int i = 0; i < downList.size(); i++) {
                            Map<String, DownloadImp> map = downList.get(i);
                            if (map.containsKey(bean.getAppId())) {
                                downList.remove(i);
                                break;
                            }
                        }
                    }
                    //发送本地下载出现错误消息
                    Message message = handler.obtainMessage();
                    message.what = FAIL;
                    message.arg1 = Integer.parseInt(bean.getAppId());
                    handler.sendMessage(message);
                }
            });
        }
    }

    /**
     * 安装应用
     *
     * @param appId 服务器端的appId
     * @return 安装结果
     */
    public String installApp(String appId, String fileName) {
        AppInfoBean appInfoBean = AppInfoServer.queryAppInfo(appId);
        if (appInfoBean != null) {
            // 通过Intent安装APK文件
            Intent i = new Intent(Intent.ACTION_VIEW);
            File file = new File(FILEPATH + fileName + ".apk");
            i.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            context.startActivity(i);
            return new Gson().toJson(new BaseResponse(false, ActomaController.getApp().getString(R.string.start_install_app)));
        } else {
            return new Gson().toJson(new BaseResponse(false, ActomaController.getApp().getString(R.string.install_app_failed)));
        }
    }

    /**
     * 取消所有的下载线程
     */
    public void cancelAllTask() {
        LogUtil.getUtils().i(TAG + "==取消所有的下载线程==");
        if (downList != null) {
            for (int i = 0; i < downList.size(); i++) {
                Map<String, DownloadImp> map = downList.get(i);
                for (Map.Entry<String, DownloadImp> entry : map.entrySet()) {
                    entry.getValue().release();
                }
            }
        }
    }

    /**
     * 添加应用下载信息到下载队列
     *
     * @param bean 应用下载信息
     */
    @Subscribe
    public void addList(AppInfoBean bean) {
        if (list.contains(bean)) {
            list.remove(bean);
        }
        long freeSize = getSDFreeSize();
        if (freeSize != -1) {
            //加入下载队列集合
            list.add(bean);
            //信息入库 若数据库内无对应AppId的应用信息则插入该数据,否则更新该数据
            AppInfoServer.insertAppInfo(bean);
            //判断是否有足够的内存下载应用
            if (freeSize > allDownSize) {
                downLoadApp(bean);
            } else {
                XToast.show(context, ActomaController.getApp().getString(R.string.oom));
            }
        } else {
            XToast.show(context, ActomaController.getApp().getString(R.string.check_sd_card));
        }
    }

    /**
     * 暂停下载
     *
     * @param bean 暂停事件对象
     */
    @Subscribe
    public void pauseDown(WebViewJavascriptInterface.PauseBean bean) {
        if (!downList.isEmpty()) {
            for (int i = 0; i < downList.size(); i++) {
                Map<String, DownloadImp> map = downList.get(i);
                if (map.containsKey(bean.getAppId())) {
                    DownloadImp downloadImp = map.get(bean.getAppId());
                    downloadImp.stop();
                    break;
                }
            }
        }
    }

    /**
     * 继续下载
     *
     * @param bean 继续事件对象
     */
    @Subscribe
    public void continueDown(WebViewJavascriptInterface.ContinueBean bean) {
        AppInfoBean appInfoBean = AppInfoServer.queryAppInfo(bean.getAppInfoBean().getAppId());
        //判断当前该应用是否下载
        boolean isHave = false;
        if (!downList.isEmpty()) {
            for (int i = 0; i < downList.size(); i++) {
                Map<String, DownloadImp> map = downList.get(i);
                if (map.containsKey(appInfoBean.getAppId())) {
                    isHave = true;
                    break;
                }
            }
        }
        if (isHave == false) {
            list.add(appInfoBean);
            downLoadApp(appInfoBean);
        }
    }

    /**
     * 初始化广播接收器
     */
    private void initReceiver() {
        //实例化广播接收器
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //接收安装广播
                if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                    String packageName = intent.getDataString().replace("package:", "");
                    List<AppInfoBean> applist = AppInfoServer.queryAllAppInfo();
                    if (applist != null) {
                        for (int i = 0; i < applist.size(); i++) {
                            if (applist.get(i).getPackageName().equals(packageName)) {
                                //删除对应数据信息
                                AppInfoServer.deleteAppInfo(applist.get(i).getAppId());
                                //删除安装包
                                File file = new File(FILEPATH + applist.get(i).getFileName() + ".apk");
                                if (file.exists()) {
                                    file.delete();
                                }
                                if (webView != null) {
                                    //发送本地已安装完成消息
                                    Message message = handler.obtainMessage();
                                    message.what = INSTALL;
                                    message.arg1 = Integer.parseInt(applist.get(i).getAppId());
                                    handler.sendMessage(message);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        };
        //注册安装完成广播监听
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme("package");
        context.registerReceiver(receiver, filter);
    }

    /**
     * 发送事件处理webUI
     *
     * @param type       当前所需操作的类型
     * @param appId      当前下载的应用id
     * @param percentage 当前已下载百分比
     * @param downSize   当前已下载大小
     */
    private void postBean(int type, String appId, String percentage, String downSize) {
        //实例化事件对象
        LoadUrlBean bean = new LoadUrlBean();
        //当前下载的应用id
        bean.setAppId(appId);
        //当前所需操作的类型
        bean.setType(type);
        //当前已下载百分比
        bean.setPercentage(percentage);
        //当前已下载大小
        bean.setDownSize(downSize);
        //发送事件 去处理网页UI
        BusProvider.getMainProvider().post(bean);
    }

    /**
     * 注销接收器
     */
    public void unRegisterReceiver() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }

    @SuppressWarnings("deprecation")
    public long getSDFreeSize() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //取得SD卡文件路径
            File path = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(path.getPath());
            //获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSize();
            //空闲的数据块的数量
            long freeBlocks = sf.getAvailableBlocks();
            //返回SD卡空闲大小
            return freeBlocks * blockSize;  //单位Byte
            //return (freeBlocks * blockSize)/1024;   //单位KB
            //return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
        } else {
            return -1;
        }
    }


    /**
     * 更新页面UI对象
     */
    public class LoadUrlBean {
        /**
         * 当前下载的应用id
         */
        private String appId;
        /**
         * 当前所需操作的类型
         */
        private int type;
        /**
         * 当前已下载百分比
         */
        private String percentage;
        /**
         * 当前已下载大小
         */
        private String downSize;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPercentage() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage = percentage;
        }

        public String getDownSize() {
            return downSize;
        }

        public void setDownSize(String downSize) {
            this.downSize = downSize;
        }
    }
}
