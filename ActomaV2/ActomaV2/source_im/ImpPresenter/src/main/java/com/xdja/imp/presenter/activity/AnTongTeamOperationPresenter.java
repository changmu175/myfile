package com.xdja.imp.presenter.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.ClearUnReadMsg;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.command.IAnTongTeamOperationCommand;
import com.xdja.imp.ui.ViewAnTongTeamOperation;
import com.xdja.imp.ui.vu.IAnTongTeamOperationVu;
import com.xdja.imp.util.BitmapUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;

import javax.inject.Inject;

import dagger.Lazy;


/**
 * Created by cxp on 2015/8/7.
 */
public class AnTongTeamOperationPresenter extends IMActivityPresenter<IAnTongTeamOperationCommand, IAnTongTeamOperationVu> implements IAnTongTeamOperationCommand {

    private String urlStr;
    private long timeOut = 20000;
    private static final int TIMEOUT_VALUE = 1;
    private static final int LOADING_FINISH_VALUE = 2;
    private static final int LOADING_ERROR_VALUE = 3;
    private static final int NETWORK_UNABLE_VALUE = 4;
    private Timer timer;
    //TODO: 安通+团队，是否必须写死
    private static final String anToneTeamUrl;

    @Inject
    Lazy<ClearUnReadMsg> clearUnReadMsg;

    @Inject
    BusProvider busProvider;

    private MyHandler mHandler = new MyHandler(new WeakReference<>(this));

    static {
        anToneTeamUrl = PreferencesServer.getWrapper(ActomaController.getApp()).gPrefStringValue("aboutAtUrl") + "team.html";
    }

    private static class MyHandler extends Handler {
        private final WeakReference<AnTongTeamOperationPresenter> mActivity;
        public  MyHandler(WeakReference<AnTongTeamOperationPresenter> mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            AnTongTeamOperationPresenter activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case TIMEOUT_VALUE: {
                /*
                * 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
                     */
                        if (activity.getVu().getWebViewProgress() < 100) {
                            activity.getVu().getCurrentWebView().setVisibility(View.GONE);
                            activity.getVu().setNetworkLoadViewVisible(View.VISIBLE);
                            activity.getVu().webViewStopLoading();
                        }
                    }
                    break;
                    case LOADING_FINISH_VALUE: {
                        activity.getVu().getCurrentWebView().setVisibility(View.VISIBLE);
                    }
                    break;

                    case LOADING_ERROR_VALUE: {
                        String errorHtml = "<html><body><h1></h1></body></html>";
                        activity.getVu().getCurrentWebView().loadData(errorHtml, "text/html", "UTF-8");
                        activity.getVu().getCurrentWebView().setVisibility(View.GONE);
                        activity.getVu().setNetworkLoadViewVisible(View.VISIBLE);
                    }
                    break;
                    case NETWORK_UNABLE_VALUE: {
                        activity.getVu().getCurrentWebView().setVisibility(View.GONE);
                        activity.getVu().setNetworkLoadViewVisible(View.VISIBLE);
                    }
                    break;
                }
                activity.getVu().setProgressViewVisible(View.GONE);
                activity.timerCancel();
            }
        }
    }

    @Override
    protected Class<? extends IAnTongTeamOperationVu> getVuClass() {
        return ViewAnTongTeamOperation.class;
    }

    @Override
    protected IAnTongTeamOperationCommand getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);

        SpannableString spannableString = BitmapUtils.formatAnTongSpanContent(getResources().getString(R.string.activity_notification_operation_presenter), this,
                (float) 1.0, BitmapUtils.AN_TONG_TITLE_PLUS);
        setTitle(spannableString);
        CharSequence urlChars = getIntent().getCharSequenceExtra(ConstDef.AN_TONG_NOTIFICATION_URL);
        if (null != urlChars) {
            urlStr = urlChars.toString();
        }
        //[s]modify by xnn for private version @20170222
        String loadUrl ="";
        loadUrl = UniversalUtil.changeLanServerWebUrl(this,anToneTeamUrl);
        if(!TextUtils.isEmpty(loadUrl)){
            urlStr = loadUrl;
            if (CustInfo.isCustom()) {
                if(CommonUtils.isZH(this)){
                    if (urlStr != null && urlStr.lastIndexOf(".") > -1) {
                        urlStr = urlStr.substring(0, urlStr.lastIndexOf(".")) + "-2.html";
                    }
                }
            }else{
                if (!UniversalUtil.isXposed() && CommonUtils.isZH(this)) {
                    if (urlStr != null && urlStr.lastIndexOf(".") > -1) {
                        urlStr = urlStr.substring(0, urlStr.lastIndexOf(".")) + "-1.html";
                    }
                }
            }
        } //[e]modify by xnn for private version @20170222
    }

    // 网络状态
    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (null == useCaseComponent) {
            LogUtil.getUtils().i("useCaseComponent is null");
            return;
        }
        //初始化注入
        useCaseComponent.inject(this);

        loadUrl(urlStr);

        clearUnReadCount();

    }

    /**
     * 删除文件
     *
     * @param file 要删除的文件
     */
    private void deleteFile(File file) throws Exception {
        if (file.exists()) {
            if (file.isFile()) {
                LogUtil.getUtils().i( " >>> 删除文件：" + file.getAbsolutePath());
                boolean isDel = file.delete();
                LogUtil.getUtils().i((isDel ? " >>> 文件删除成功!" : " >>> 文件删除失败!"));
            } else if (file.isDirectory()) {
                for (File item : file.listFiles()) {
                    deleteFile(item);
                }
            }
        }
    }

    /**
     * 加载网页
     *
     * @param rawUrl
     */
    private void loadUrl(final String rawUrl) {
        boolean isOk = getCommand().getNetWorkState();
        if (isOk) {
            getVu().setProgressViewVisible(View.VISIBLE);
            getVu().getCurrentWebView().setWebViewClient(new WebViewClient() {

                                                             @Override
                                                             public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                                                 super.onPageStarted(view, url, favicon);
                                                                 //TODO 超时时间暂时屏蔽
//                                                                 timer = new Timer();
//                                                                 TimerTask tt = new TimerTask() {
//                                                                     @Override
//                                                                     public void run() {
//
//                                                                         Message msg = new Message();
//                                                                         msg.what = TIMEOUT_VALUE;
//                                                                         mHandler.sendMessage(msg);
//                                                                     }
//                                                                 };
//                                                                 timer.schedule(tt, timeOut, 1);
                                                             }

                                                             @Override
                                                             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                                                 // TODO Auto-generated method stub
                                                                 //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器

                                                                 if (!TextUtils.isEmpty(url)) {
                                                                     view.loadUrl(url);
                                                                 }
                                                                 return true;
                                                             }

                                                             @Override
                                                             public void onPageFinished(WebView view, String url) {
                                                                 // TODO Auto-generated method stub
                                                                 // super.onPageFinished(view, url);

                                                                 Message msg = new Message();
                                                                 msg.what = LOADING_FINISH_VALUE;
                                                                 mHandler.sendMessage(msg);

                                                             }

                                                             @Override
                                                             public void onReceivedError(WebView view, int errorCode,
                                                                                         String description, String failingUrl) {
                                                                 // TODO Auto-generated method stub
                                                                 //super.onReceivedError(view, errorCode, description, failingUrl);
                                                                 Message msg = new Message();
                                                                 msg.what = LOADING_ERROR_VALUE;
                                                                 mHandler.sendMessage(msg);
                                                             }
                                                         }

            );
            /*=================modify by gy 2015-10-27 start=======================*/
            getVu().getCurrentWebView().setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        getVu().setProgressViewVisible(View.GONE);
                    } else {
                        getVu().setProgressViewVisible(View.VISIBLE);
                        getVu().setWebViewProgress(newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }

                // start: wangchao for 2882
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    //[Start]YangShaoPeng<mailto://ysp@xdja.com> 2016-09-13 add. fix bug #3948 . review by LiXiaoLong.
                    if(!TextUtils.isEmpty(title) && title.toLowerCase().contains("error")) {
                        Message msg = new Message();
                        msg.what = LOADING_ERROR_VALUE;
                        mHandler.sendMessage(msg);
                    }
                    //[End]YangShaoPeng<mailto://ysp@xdja.com> 2016-09-13 add. fix bug #3948 . review by LiXiaoLong.
                    if (!rawUrl.equals(view.getUrl())) {
                        Message msg = new Message();
                        msg.what = LOADING_ERROR_VALUE;
                        mHandler.sendMessage(msg);

                        try {
                            deleteFile(new File(getFilesDir().getParent() + "/app_webview"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    super.onReceivedTitle(view, title);
                    LogUtil.getUtils().i("onReceivedTitle title=" + title + ", url=" + view.getUrl());
                }
                // end: wangchao
            });
            /*=================modify by gy 2015-10-27 end=======================*/
        } else {
            getVu().showToastWithMessage(getResources().getString(R.string.check_network_setting));
            Message msg = new Message();
            msg.what = NETWORK_UNABLE_VALUE;
            mHandler.sendMessage(msg);
        }
        getVu().getCurrentWebView().loadUrl(rawUrl);
    }

    private void timerCancel() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //构建事件对象
        IMProxyEvent.RefreshSingleTalkEvent talkEvent
                = new IMProxyEvent.RefreshSingleTalkEvent();

        TalkListBean talkListBean = new TalkListBean();
        talkListBean.setTalkerAccount("-10000");
        talkListBean.setTalkFlag("-10000_100");
        talkListBean.setContent(anToneTeamUrl+getResources().getString(R.string.antong_text_content));
        talkListBean.setNotReadCount(0);
        talkListBean.setTalkType(ConstDef.CHAT_TYPE_ACTOMA);
        talkListBean.setLastMsg(null);
        talkListBean.setLastTime(0L);


        talkEvent.setTalkListBean(talkListBean);

        //打印事件对象
        LogUtil.getUtils().d(talkEvent.toString());
        //发送事件
        if (null != busProvider) {
            busProvider.post(talkEvent);
        }
    }

    /**
     * 清空未读消息数
     */
    private void clearUnReadCount(){
        clearUnReadMsg
                .get()
                .clear("-10000_100")
                .execute(new OkSubscriber<Integer>(this.okHandler) {
                    @Override
                    public void onNext(Integer integer) {
                        super.onNext(integer);
                    }
                });
    }


    /**
     * 关闭当前界面
     */
    @Override
    public void finishCurrentActivity() {
        finish();
    }

    /**
     * 获得网络状态
     *
     * @return
     */
    @Override
    public boolean getNetWorkState() {
        return isNetworkConnected(getApplicationContext());
    }

    @Override
    public AnTongTeamMessage initWebViewData() {


        AnTongTeamMessage notificationMessage = new AnTongTeamMessage(this);

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            notificationMessage.setUrlStr("");
            LogUtil.getUtils().e("安通+团队URL为空");
            return notificationMessage;
        }
        String urlStr;
        String path = url.getPath();
        String portStr = "";
        int port = url.getPort();

        if (port > 0) {
            portStr = ":" + port;
        }
        if (path != null && path.indexOf("/", 1) > -1) {
            path = path.substring(0, path.indexOf("/", 1));
            urlStr = url.getProtocol() + "://" + url.getHost() + portStr + path;
        } else {
            urlStr = url.getProtocol() + "://" + url.getHost() + portStr;
        }

//        //增加有没有第三方加密的判断 add 2015-10-08 17:10:50
//        if(!UniversalUtil.isXposed()){
//           if( urlStr != null && urlStr.lastIndexOf(".") > -1){
//               urlStr = urlStr.substring(0, urlStr.lastIndexOf(".") + 1) + "-1.html";
//           }
//        }

        notificationMessage.setUrlStr(urlStr);

        return notificationMessage;
    }

    /**
     * 重新加载URL
     */
    @Override
    public void refreshUrl() {
        getVu().getCurrentWebView().clearCache(true);
        getVu().getCurrentWebView().clearHistory();
        getVu().getCurrentWebView().setVisibility(View.GONE);
        loadUrl(urlStr);
    }
}
