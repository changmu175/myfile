package com.xdja.imp.presenter.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.DownloadFile;
import com.xdja.imp.domain.interactor.def.PauseReceiveFile;
import com.xdja.imp.domain.interactor.def.ResumeReceiveFile;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.command.IChatDetailFileCheckCommand;
import com.xdja.imp.receiver.NetworkStateBroadcastReceiver;
import com.xdja.imp.receiver.NetworkStateEvent;
import com.xdja.imp.ui.ViewFileCheck;
import com.xdja.imp.ui.vu.IFileCheckVu;
import com.xdja.imp.util.HistoryFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by guorong on 2016/11/30.
 */
public class ChatDetailFileCheckPresenter extends
        IMActivityPresenter<IChatDetailFileCheckCommand , IFileCheckVu>
                                        implements IChatDetailFileCheckCommand{
    private static final String MSG_BEAN = "msg_bean";
    private static final String TITLE = "title";
    private static int WIFI = 1;
    private static int MOBILE_NETWORK = 2;
    private static int UNUSABLE = -1;
    private String title;
    @Inject
    Lazy<PauseReceiveFile> pauseReceiveFile;

    @Inject
    Lazy<ResumeReceiveFile> resumeReceiveFile;

    @Inject
    Lazy<DownloadFile> downloadFile;

    @Inject
    BusProvider busProvider;

    private TalkMessageBean talkMessageBean;

    private FileInfo fileInfo;

    private long msgId;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (null == useCaseComponent) {
            LogUtil.getUtils().i("useCaseComponent is null");
            return;
        }
        //初始化注入
        useCaseComponent.inject(this);
        busProvider.register(this);
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            title = intent.getStringExtra(TITLE);
            if(bundle != null){
                talkMessageBean = bundle.getParcelable(MSG_BEAN);
                if (talkMessageBean != null) {
                    msgId = talkMessageBean.get_id();
                    fileInfo = talkMessageBean.getFileInfo();
                }
            }
        }
        setTitle(title);
        if(fileInfo != null){
            if(fileInfo.getFileState() != ConstDef.PAUSE &&
                    fileInfo.getFileState() != ConstDef.DONE){
                if(NetworkStateBroadcastReceiver.getState() != 0){
                    fileInfo.setFileState(ConstDef.PAUSE);
                    Toast.makeText(this, getString(R.string.network_disabled)
                            , Toast.LENGTH_SHORT).show();
                }else{
                    downLoadFile(true);
                }
            }
            getVu().setData();
        }
    }
    @NonNull
    @Override
    protected Class<? extends IFileCheckVu> getVuClass() {
        return ViewFileCheck.class;
    }

    @NonNull
    @Override
    protected IChatDetailFileCheckCommand getCommand() {
        return this;
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.unregister(this);
    }

    @Override
    public void downLoadFile(final boolean flag){
        if(fileInfo.getFileState() == ConstDef.LOADING){
            return;
        }
        int networkType = getNetworkState(this);
        if (networkType == WIFI) {
            if(!flag){
                getVu().setCtrlBtnText(R.string.pause_download);
            }
            //开始下载文件
            if(fileInfo.getFileState() == ConstDef.INACTIVE){
                startDownloadFile();
            }else{
                resumeDownloadFile();
            }
        } else if(networkType == MOBILE_NETWORK){
            //检测是否为2G/3G/4G网络
            final CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle(R.string.download_file_title)
                    .setMessage(R.string.download_file_net_type)
                    // 设置内容
                    .setPositiveButton(getString(R.string.download_file_btn_continue),// 设置确定按钮
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //开始下载文件
                                    if(fileInfo.getFileState() == ConstDef.INACTIVE){
                                        startDownloadFile();
                                    }else{
                                        resumeDownloadFile();
                                    }
                                    if(!flag){
                                        getVu().setCtrlBtnText(R.string.pause_download);
                                    }
                                    dialog.dismiss();// 关闭进度对话框
                                }
                            })
                    .setNegativeButton(getString(R.string.download_file_btn_cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();// 关闭进度对话框
                                    if(flag){
                                        finish();
                                    }
                                }
                            })
                    .setCancelable(false)
                    .show();
        }

    }
    @Override
    public void startDownloadFile() {
        if(talkMessageBean == null){
            return;
        }
        List<FileInfo> fileInfos = new ArrayList<>();
        fileInfos.add(fileInfo);
        fileInfo.setFileState(ConstDef.LOADING);
        downloadFile.get().downLoad(fileInfos).execute(new OkSubscriber<Integer>(null));
    }

    @Override
    public void pauseDownloadFile() {
        if(fileInfo == null){
            return;
        }
        fileInfo.setFileState(ConstDef.PAUSE);
        pauseReceiveFile.get()
                .pause(fileInfo)
                .execute(new OkSubscriber<Integer>(null));
    }

    @Override
    public void resumeDownloadFile() {
        if(fileInfo == null){
            return;
        }
        fileInfo.setFileState(ConstDef.LOADING);
        resumeReceiveFile.get()
                .resume(fileInfo)
                .execute(new OkSubscriber<Integer>(null));
    }

    @Override
    public FileInfo getFileInfo() {
        return talkMessageBean.getFileInfo();
    }

    /**
     * 文件下载完成
     * */
    @Subscribe
    public void RecieveFileFinished(final IMProxyEvent.ReceiveFileFinishedEvent event) {
        if(event != null && talkMessageBean != null && msgId == event.getAttachedMsgId()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fileInfo.setFileState(ConstDef.DONE);
                    getVu().downloadFileFinish();
                }
            });
        }
    }

    /**
     * 文件下载过程中进度更新回调
     */
    @Subscribe
    public void RecieveFileUpdated(final IMProxyEvent.ReceiveFileProgressUpdateEvent event) {
        if(event != null && talkMessageBean != null && msgId == event.getAttachedMsgId()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fileInfo.setPercent(event.getPercent());
                    fileInfo.setFileState(ConstDef.LOADING);
                    getVu().updateDownloadRate(event.getPercent());
                }
            });
        }
    }

    /**
       * 文件下载过程中暂停回调
       * */
    @Subscribe
    public void RecieveFilePaused(final IMProxyEvent.ReceiveFilePaused event) {
        if(event != null && msgId == event.getAttachedMsgId() && fileInfo != null) {
            fileInfo.setFileState(ConstDef.PAUSE);
        }
    }

    /**
     * 下载文件失败
     */
    @Subscribe
    public void RecieveFileFailed(IMProxyEvent.ReceiveFileFailedEvent event) {
        if(event != null && msgId == event.getAttachedMsgId() && fileInfo != null) {
            fileInfo.setFileState(ConstDef.FAIL);
        }
    }

    @Subscribe
    public void NetworkStateChange(NetworkStateEvent event){
        getVu().changeViewSate(event.getState());
    }

    @Override
    public void openFile() {
        if(talkMessageBean == null){
            return;
        }
        FileInfo fileInfo = talkMessageBean.getFileInfo();
        File file = new File(fileInfo.getFilePath());
        if(!file.exists()){
            Toast.makeText(this,getString(R.string.history_send_file_not_exist),Toast.LENGTH_SHORT).show();
        }else{
            if(fileInfo.getFilePath() != null){
            	HistoryFileUtils.intentBuilder(this,fileInfo.getFilePath() , fileInfo.getSuffix());
        	}
        }
    }

    @Override
    public int getIcon() {
        if(talkMessageBean == null){
            return R.drawable.ic_others;
        }
        return HistoryFileUtils.getIconWithSuffix(talkMessageBean);
    }

    @Override
    public String getToolbarTitle() {
        return title == null ? "" : title;
    }

    @Override
    public void forward() {
        //转发
        final FileInfo fileInfo = talkMessageBean.getFileInfo();// 文件信息
        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        fileInfos.add(fileInfo);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConstDef.TAG_SELECTFILE, fileInfos);
        Intent forwardIntent = new Intent();
        forwardIntent.setAction(ConstDef.FORWARD);
        forwardIntent.putExtras(bundle);
        forwardIntent.setType(ConstDef.FILE_SHARE_TYPE);
        forwardIntent.putExtra(ConstDef.TAG_TALKERID, talkMessageBean.getTo());
        forwardIntent.putExtra(ConstDef.TAG_TALKTYPE, ConstDef.CHAT_TYPE_P2P);//消息类型
        forwardIntent.setClass(this, ChooseIMSessionActivity.class);
        startActivityForResult(forwardIntent, ConstDef.REQUEST_CODE_FORWARD);
    }

    @Override
    public void back() {
        //在下载界面返回时，需要将当前文件信息返回给会话详情界面
        Intent intent = new Intent();
        intent.putExtra("msg_id" , talkMessageBean.get_id());
        intent.putExtra("percent" , fileInfo.getPercent());
        intent.putExtra("state" , fileInfo.getFileState());
        setResult(ConstDef.REQUEST_CODE_FILE_CHECK , intent);
        finish();
    }

    public int getNetworkState(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return UNUSABLE;
        } else {
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            //如果是wifi连接成功，就表示有网络可用
            if (wifiNetInfo.isConnected()) {
                return WIFI;
            } else if(mobNetInfo.isConnected()){
                //如果是仅移动网络可用，就要判断是否开启仅wifi可用
                return MOBILE_NETWORK;
            }
        }
        return UNUSABLE;
    }
}
