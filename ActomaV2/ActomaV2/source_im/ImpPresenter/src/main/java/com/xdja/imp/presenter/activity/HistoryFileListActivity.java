package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.widget.XDialog;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.DeleteMsg;
import com.xdja.imp.domain.interactor.def.DownloadFile;
import com.xdja.imp.domain.interactor.def.GetHistoryFileList;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.HistoryFileCategory;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.HistoryFileAdapterPresenter;
import com.xdja.imp.presenter.command.IHistoryFileListCommand;
import com.xdja.imp.receiver.IStateChangeCallback;
import com.xdja.imp.receiver.NetworkStateBroadcastReceiver;
import com.xdja.imp.receiver.NetworkStateEvent;
import com.xdja.imp.ui.ViewHistoryFileList;
import com.xdja.imp.ui.vu.IHistoryFileListVu;
import com.xdja.imp.util.DateUtils;
import com.xdja.imp.util.HistoryFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * 项目名称：Blade
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/8 17:05
 * 修改人：xdjaxa
 * 修改时间：2016/12/8 17:05
 * 修改备注：
 */
public class HistoryFileListActivity extends IMActivityPresenter<IHistoryFileListCommand, IHistoryFileListVu>
        implements IHistoryFileListCommand,HistoryFileAdapterPresenter.ItemLongClickListener {

    private String flag;

    private String talkId;

    private int talkType;

    @Inject
    Lazy<GetHistoryFileList> getHistoryFileListUseCase;

    //事件总线
    @Inject
    BusProvider busProvider;

    @Inject
    Lazy<DownloadFile> downloadFile;

    @Inject
    Lazy<DeleteMsg> deleteMsg;

    private HistoryFileAdapterPresenter mAdapter;

    private final Map<HistoryFileCategory,List<TalkMessageBean>> mDatas = new LinkedHashMap<>();

    //add by zya 20170103从setting进入后的标志
    private boolean isTransmit;
    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        //初始化注入
        useCaseComponent.inject(this);

        //初始化事件总线
        busProvider.register(this);

        Intent intent = getIntent();
        if(intent != null) {
            flag = intent.getStringExtra(ConstDef.TAG_TALKFLAG);
            talkId = intent.getStringExtra(ConstDef.TAG_TALKERID);
            talkType = intent.getIntExtra(ConstDef.CHAT_TYPE,1);
            if(TextUtils.isEmpty(talkId)) {
                finish();
                return ;
            }
        }

        reSetTitle();

        mAdapter = new HistoryFileAdapterPresenter(this,busProvider,mDatas,userCache);
        mAdapter.setItemLongClickListener(this);

        useCaseComponent.inject(mAdapter);
        mAdapter.setActivity(this);
        mAdapter.setListView(getVu().getListView());
        getVu().getListView().setAdapter(mAdapter);
        obtainDatas();

        isTransmit = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //add by zya 20170103
        //从后台到前台转换界面的刷新
        if(isTransmit) {
            refreshUI(getVu().refreshUI());
            isTransmit = false;
        }//end by zya
    }

    private void reSetTitle(){
        setTitle(R.string.chat_file_history_list);
    }

    private void obtainDatas(){
        getHistoryFileListUseCase
                .get()
                .deliverParams(flag)
                .execute(new OkSubscriber<Map<HistoryFileCategory,List<TalkMessageBean>>>(null){
                    @Override
                    public void onNext(Map<HistoryFileCategory,List<TalkMessageBean>> dataMaps) {
                        super.onNext(dataMaps);
                        mDatas.putAll(dataMaps);
                        getVu().showEmpty(mDatas.size() == 0);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**联网状态出现问题后，界面更新
     * @param flag
     */
    private void changeNetworkViewState(int flag){
        if(flag != NetworkStateBroadcastReceiver.NORMAL){
            //网络不可用
            if(mDatas.size() > 0){
                for(Map.Entry<HistoryFileCategory,List<TalkMessageBean>> entry : mDatas.entrySet()){
                    for(TalkMessageBean bean : entry.getValue()){
                        FileInfo fInfo = bean.getFileInfo();
                        if(fInfo.getFileState() == ConstDef.LOADING){
                            fInfo.setFileState(ConstDef.PAUSE);
                            mAdapter.clickToDownloadOfOpen(bean);
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void changeNeworkState(NetworkStateEvent event){
        changeNetworkViewState(event.getState());
    }

    @NonNull
    @Override
    protected Class<? extends IHistoryFileListVu> getVuClass() {
        return ViewHistoryFileList.class;
    }

    @NonNull
    @Override
    protected IHistoryFileListCommand getCommand() {
        return this;
    }


    private List<TalkMessageBean> getCheckBean(){
        List<TalkMessageBean> beans = new ArrayList<>();
        for(Map.Entry<HistoryFileCategory,List<TalkMessageBean>> entry : mDatas.entrySet()){
            for(TalkMessageBean bean : entry.getValue()){
                if(bean.isCheck()){
                    beans.add(bean);
                }
            }
        }
        return beans;
    }

    private List<TalkMessageBean> updateDatas(){
        List<TalkMessageBean> beans = new ArrayList<>();
        for(Map.Entry<HistoryFileCategory,List<TalkMessageBean>> entry : mDatas.entrySet()){
            Iterator<TalkMessageBean> iterator = entry.getValue().iterator();
            while(iterator.hasNext()){
                TalkMessageBean bean = iterator.next();
                if(bean.isCheck()){
                    beans.add(bean);
                    iterator.remove();
                }
            }
            //add by zya 20161220 ,delete group
            if(entry.getValue().size() == 0){
                mDatas.remove(entry.getKey());
            }//end by zya
        }
        return beans;
    }

    @Override
    public void downloadFiles() {
        List<TalkMessageBean> downloadBeans = getCheckBean();
        //add by zya,20161222
        if(downloadBeans.size() == 0){
            //提示选择的文件不可为空
            Toast.makeText(this,R.string.history_select_file_none,Toast.LENGTH_SHORT).show();
            return ;
        }//end by zya

        final List<FileInfo> downloadFiles = new ArrayList<>();
        //fix bug 8344 by zya ,20170209
        final List<FileInfo> downloadedFiles = new ArrayList<>();
        for(TalkMessageBean bean : downloadBeans){
            if(!DateUtils.isOverdue(bean.getShowTime())){
                downloadFiles.add(bean.getFileInfo());
                if(HistoryFileUtils.isFileExist(bean.getFileInfo().getFilePath())){
                   downloadedFiles.add(bean.getFileInfo());
                }//end by zya
            }
        }

        //add by zya,20161222
        if(downloadFiles.size() == 0 && downloadBeans.size() > 0){
            //提示所有文件都过期无需下载
            Toast.makeText(this,getString(R.string.history_select_all_overdue),Toast.LENGTH_SHORT).show();
            return ;
        } else if(downloadFiles.size() < downloadBeans.size() && downloadFiles.size() > 0){
            //提示有过期文件，需要下载未过期的文件
            Toast.makeText(this,getString(R.string.history_select_all_overdue),Toast.LENGTH_SHORT).show();
        }//end by zya

        //fix bug 8344 by zya ,20170209
        if(downloadedFiles.size() > 0){
            downloadFiles.removeAll(downloadedFiles);
            Toast.makeText(this,getString(R.string.history_file_downloaded),Toast.LENGTH_SHORT).show();
            if(downloadFiles.size() == 0){
                return ;
            }
        }//end by zya

        downloadFile.get().downLoad(downloadFiles).execute(new OkSubscriber<Integer>(null) {
            @Override
            public void onNext(Integer integer) {
                super.onNext(integer);
                if (integer == 0) {
                    LogUtil.getUtils().d("zhu->开始下载文件");

                    addDownloadPercentCache(downloadFiles);

                    //add by zya ,notify download state ,20170103
                    mAdapter.notifyDataSetChanged();
                    //end by zya
                }
            }
        });
    }

    private void addDownloadPercentCache(List<FileInfo> downloadFiles){
        for(FileInfo fInfo : downloadFiles){
            userCache.putProgress(fInfo.getTalkMessageId(),0);
            //add by zya 20170105
            fInfo.setFileState(ConstDef.LOADING);
            //end by zya
        }
    }

    @Override
    public void transmitFiles() {
        isTransmit = true;
        Intent intent = new Intent();

        //获取已经选择的文件
        List<TalkMessageBean> transmitBeans = getCheckBean();

        //fix bug 7740 by zya 20170102
        if (transmitBeans.size() == 0){
            Toast.makeText(this,R.string.history_select_file_none,Toast.LENGTH_SHORT).show();
            return ;
        }//end by zya

        ArrayList<FileInfo> transmitFiles = new ArrayList<>();
        for(TalkMessageBean bean : transmitBeans){
            if(HistoryFileUtils.isFileExist(bean.getFileInfo().getFilePath())){
                transmitFiles.add(bean.getFileInfo());
            }
        }
        //文件判断
        if(transmitFiles.size() == 0){
            //文件都不存在不可转发
            Toast.makeText(this,getString(R.string.history_transmit_all_noexist),Toast.LENGTH_SHORT).show();
            return ;
        } else if(transmitFiles.size() > 0 && transmitFiles.size() < transmitBeans.size()){
            //存在未下载的文件，只可以部分转发
            Toast.makeText(this,getString(R.string.history_transmit_all_noexist),Toast.LENGTH_SHORT).show();
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConstDef.TAG_SELECTFILE, transmitFiles);
        intent.putExtras(bundle);
        intent.setAction(ConstDef.FORWARD);
        intent.setType(ConstDef.FILE_SHARE_TYPE);
        intent.putExtra(ConstDef.TAG_TALKERID, talkId);
        intent.putExtra(ConstDef.TAG_TALKTYPE, talkType);//消息类型
        intent.setClass(this, ChooseIMSessionActivity.class);
        startActivityForResult(intent, ConstDef.REQUEST_CODE_FORWARD);
    }

    @Override
    public void removeFiles() {
        //getVu().showLoading();
        List<TalkMessageBean> downloadBeans = getCheckBean();

        //add by zya 20161219
        if(downloadBeans.size() == 0){
            //fix bug 7740 by zya 20170102
            Toast.makeText(this,R.string.history_select_file_none,Toast.LENGTH_SHORT).show();
            //end by zya
            return ;
        }

        List<Long> deleteIds = new ArrayList<>();
        for(TalkMessageBean bean : downloadBeans){
            deleteIds.add(bean.get_id());
        }
        showRemoveDialog(deleteIds,null);
    }

    /**
     * 删除确认框
     * @param ids
     */
    private void showRemoveDialog(final List<Long> ids, final Map<Integer, TalkMessageBean> deleteBean){
        final XDialog xDialog = new XDialog(this);
        xDialog.setTitle(getString(R.string.history_file_delete_hint))
                .setMessage(getString(R.string.history_file_delete_message))
                .setNegativeButton(getString(R.string.cancel),null)
                .setPositiveButton(getString(R.string.history_file_delete_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        xDialog.dismiss();
                        getVu().showCommonProgressDialog(R.string.hisroty_file_delete);
                        deleteMsg
                                .get()
                                .delete(ids)
                                .execute(new OkSubscriber<Integer>(null) {
                                    @Override
                                    public void onNext(Integer integer) {
                                        super.onNext(integer);
                                        getVu().dismissCommonProgressDialog();
                                        if (integer == 0) {
                                            LogUtil.getUtils().d("消息删除成功");
                                            updateUIAfterDeleteFiles(deleteBean);
                                            if(deleteBean == null){
                                                refreshUI(getVu().refreshUI());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                        getVu().dismissCommonProgressDialog();
                                        if(deleteBean == null){
                                            refreshUI(getVu().refreshUI());
                                        }
                                        LogUtil.getUtils().d("消息删除失败");
                                    }
                                });
                    }
                })
                .show();
    }

    /**
     * 多选删除和长按删除区别更新
     * @param deleteBean
     */
    private void updateUIAfterDeleteFiles(Map<Integer,TalkMessageBean> deleteBean){
        List<TalkMessageBean> deleteLists = new ArrayList<>();
        if(deleteBean != null){
            //获取删除的信息
            List<Integer> keys = new ArrayList<>(deleteBean.keySet());
            int groupPosition = keys.get(0);
            TalkMessageBean bean = deleteBean.get(groupPosition);

            //获取列表信息中对应Group的TalkMessageBean删除
            List<HistoryFileCategory> categories = new ArrayList<>(mDatas.keySet());
            Collections.sort(categories);
            HistoryFileCategory cate = categories.get(groupPosition);

            List<TalkMessageBean> beans = mDatas.get(cate);
            if(beans != null && beans.size() > 0){
                beans.remove(bean);
                if(beans.size() == 0){
                    mDatas.remove(cate);
                }
            }
            deleteLists.clear();
            deleteLists.add(bean);
        } else {
            deleteLists = updateDatas();
        }
        //更新页面
        getVu().showEmpty(mDatas.size() == 0);
        mAdapter.notifyDataSetChanged();
        //发送event到ChatDetailActivity
        onDeleteMessage(deleteLists);
    }

    private int onDeleteMessage(List<TalkMessageBean> talkMessageBeans) {
        try {
            //构建事件对象
            IMProxyEvent.DeleteMessageEvent messageEvent = new IMProxyEvent.DeleteMessageEvent();
            messageEvent.setMsgAccount(talkMessageBeans.get(0).getFrom());
            messageEvent.setTalkMessageBeansList(talkMessageBeans);

            //打印事件对象
            //LogUtil.getUtils().d(messageEvent.toString());
            //发送事件
            this.busProvider.post(messageEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    //fix bug 10092 by zya ,20170317
    @Subscribe
    public void receiveFilePaused(final IMProxyEvent.ReceiveFilePaused event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TalkMessageBean bean = getTalkMessageBean(event.getAttachedMsgId());
                FileInfo fileInfo = event.getFileInfo();
                LogUtil.getUtils().d("HistoryFile paused:" + fileInfo.getPercent());
                if(bean != null){
                    bean.getFileInfo().setFileState(ConstDef.PAUSE);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Subscribe
    public void receiveFileFailedEvent(final IMProxyEvent.ReceiveFileFailedEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TalkMessageBean bean = getTalkMessageBean(event.getAttachedMsgId());
                FileInfo fileInfo = event.getFileInfo();
                if(bean != null){
                    bean.getFileInfo().setFileState(ConstDef.FAIL);
                    userCache.removeProgress(bean.get_id());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Subscribe
    public void receiveFileFinishedEvent(final IMProxyEvent.ReceiveFileFinishedEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TalkMessageBean bean = getTalkMessageBean(event.getAttachedMsgId());
                if(bean != null){
                    bean.getFileInfo().setFileState(ConstDef.DONE);
                    userCache.removeProgress(bean.get_id());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Subscribe
    public void receiveFileProgressUpdateEvent(final IMProxyEvent.ReceiveFileProgressUpdateEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TalkMessageBean bean = getTalkMessageBean(event.getAttachedMsgId());

                if(bean != null){
                    LogUtil.getUtils().d("HistoryFile update:" + event.getPercent());
                    bean.getFileInfo().setFileState(ConstDef.LOADING);
                    bean.getFileInfo().setPercent(event.getPercent());
                    userCache.putProgress(bean.get_id(),event.getPercent());
                    mAdapter.refreshItem(event.getAttachedMsgId());
                }
            }
        });
    }//end by zya

    private TalkMessageBean getTalkMessageBean(long msgId){
        TalkMessageBean msgBean = null;
        for(Map.Entry<HistoryFileCategory,List<TalkMessageBean>> entry : mDatas.entrySet()){
            for(TalkMessageBean bean : entry.getValue()){
                if(msgId == bean.get_id()){
                    msgBean = bean;
                    break;
                }
            }
        }
        return msgBean;
    }

    @Subscribe
    public void historyRefreshSelectHintEvent(IMProxyEvent.HistoryRefreshSelectHintEvent event){
        getVu().refreshSelectHint(getCheckBean().size());
    }

    @Override
    public void refreshUI(boolean show) {
        mAdapter.updateUI(show);
    }

    @Override
    public int getDataSize() {
        return mDatas.size();
    }

    @Override
    public void itemLongClick(int groupPosition, TalkMessageBean bean) {
        Map<Integer,TalkMessageBean> deleteMap = new HashMap<>();
        deleteMap.put(groupPosition,bean);

        List<Long> deleteIds = new ArrayList<>();
        if(bean != null) {
            deleteIds.add(bean.get_id());
            showRemoveDialog(deleteIds, deleteMap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != busProvider) {
            //注销事件总线回调
            busProvider.unregister(this);
        }
        if (null != mAdapter) {
            mAdapter.onDestroy();
        }
    }
}
