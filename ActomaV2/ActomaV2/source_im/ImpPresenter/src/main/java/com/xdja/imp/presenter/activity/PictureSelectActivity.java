package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;
import com.xdja.comm.uitl.ImageLoader;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.GetImageFileList;
import com.xdja.imp.domain.interactor.def.QueryLocalPictures;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.PictureSelectAdapterPresenter;
import com.xdja.imp.presenter.command.IPictureSelectCommand;
import com.xdja.imp.ui.ViewPictureSelect;
import com.xdja.imp.ui.vu.IPictureSelectVu;
import com.xdja.imp.util.PicInfoCollection;
import com.xdja.imp.util.XToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * 聊天图片选择列表界面
 * Created by leill on 2016/6/16.
 */
public class PictureSelectActivity extends IMActivityPresenter<IPictureSelectCommand, IPictureSelectVu>
        implements IPictureSelectCommand{

    @Inject
    BusProvider busProvider;

    @Inject
    Lazy<QueryLocalPictures> queryLocalPictures;

    @Inject
    Lazy<GetImageFileList> getImageFileList;

    private boolean isFromChatdetail = false;

    private Map<String , LocalPictureInfo> picDataSource;

    private List<LocalPictureInfo> pictureInfos = new ArrayList<>();

    private List<TalkMessageBean> infos = new ArrayList<>();

    /** 本地图片列表适配器*/
    private PictureSelectAdapterPresenter mAdapterPresenter;

    /** 标志位，如果正在发送图片中，则为true*/
    private boolean bSendingImage = false;

    @NonNull
    @Override
    protected Class<? extends IPictureSelectVu> getVuClass() {
        return ViewPictureSelect.class;
    }

    @NonNull
    @Override
    protected IPictureSelectCommand getCommand() {
        return this;
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

        //初始化事件总线
        busProvider.register(this);

        picDataSource = PicInfoCollection.getLocalPicInfo();
        picDataSource.clear();
        Intent intent = getIntent();
        if(intent != null){
            isFromChatdetail = intent.getBooleanExtra(ConstDef.FROM_CHAT_DETAIL , false);
            //初始化适配器
            mAdapterPresenter = new PictureSelectAdapterPresenter(this, pictureInfos, busProvider , isFromChatdetail);

            useCaseComponent.inject(mAdapterPresenter);

            //本地图片显示列表初始化
            getVu().initListView(mAdapterPresenter);
            if(isFromChatdetail){
                //隐藏预览和发送按钮
                getVu().hidePreAndSendBtn();
                getSupportActionBar().setTitle(R.string.all_pictures);
                infos = intent.getParcelableArrayListExtra(ConstDef.SEESION_FILE_INFOS);
                LocalPictureInfo localPictureInfo;
                if(infos != null){
                    for(TalkMessageBean talkMessageBean : infos){
                        localPictureInfo = new LocalPictureInfo();
                        FileInfo fileInfo = talkMessageBean.getFileInfo();
                        if(fileInfo instanceof  ChatDetailPicInfo){
                            localPictureInfo.setType(ConstDef.TYPE_PIC);
                            ChatDetailPicInfo tempPicInfo = (ChatDetailPicInfo) fileInfo;
                            localPictureInfo.setPicName(tempPicInfo.getThumName());
                            localPictureInfo.setLocalPath(tempPicInfo.getThumPath());
                            if(tempPicInfo.isBoom() && !tempPicInfo.isMine()){
                                localPictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_DESTROY);
                            }else{
                                localPictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_UNCHECKED);
                            }
                            localPictureInfo.setOriginalPic(false);
                        }else if(fileInfo instanceof VideoFileInfo){
                            localPictureInfo.setType(ConstDef.TYPE_TINY_VIDEO);
                            VideoFileInfo tempVideoInfo = (VideoFileInfo) fileInfo;
                            localPictureInfo.setPicName(tempVideoInfo.getFileName());
                            localPictureInfo.setLocalPath(tempVideoInfo.getFilePath());
                            localPictureInfo.setOriginalPic(false);
                        }
                        pictureInfos.add(localPictureInfo);
                    }
                    mAdapterPresenter.notifyDataSetChanged();
                }
            } else {
                //查询本地图片列表
                queryLocalPictures
                        .get()
                        .execute(
                                new OkSubscriber<List<LocalPictureInfo>>(this.okHandler){
                                    @Override
                                    public void onNext(List<LocalPictureInfo> localPictureInfo) {
                                        super.onNext(localPictureInfo);
                                        //当前是否有图片被选择
                                        boolean isNoSelected = true;
                                        if(localPictureInfo != null && localPictureInfo.size() > 0){
                                            //TODO 是否考虑优化双循环
                                            for(LocalPictureInfo info : localPictureInfo){
                                                if(LocalPictureInfo.Statue.STATUE_SELECTED == info.getStatue()){
                                                    isNoSelected = false;
                                                }
                                                picDataSource.put(info.getLocalPath() , info);
                                            }
                                            for(String  path : picDataSource.keySet()){
                                                pictureInfos.add(picDataSource.get(path));
                                            }
                                            mAdapterPresenter.notifyDataSetChanged();

                                            getVu().localPicLoadFinish(isNoSelected);
                                        }else{
                                            getVu().showEmptyImage();
                                        }
                                    }
                                }
                        );
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bSendingImage = false;
        //guorong 2016-9-12 18:28:52 fixed bug 3947
        if(picDataSource  != null){
            picDataSource.clear();
        }
        //注销事件总线回调
        if (null != busProvider) {
            busProvider.unregister(this);
        }

        //Glide清除缓存
        Glide.get(this).clearMemory();
        ImageLoader.getInstance().clearCache();
    }

    @Override
    public void sendPictureMessage() {

        bSendingImage = true;

        //发送图片之前，释放掉Glide消耗的内存
        Glide.get(this).clearMemory();
        ImageLoader.getInstance().clearCache();

        //设置加载状态位，主要在发送过程中，加载图片时，列表栏不可操作
        mAdapterPresenter.setLoadingProgress(true);

        if (picDataSource.isEmpty()){
            LogUtil.getUtils().d("no selected picture!");
            bSendingImage = false;
            return ;
        }

        List<LocalPictureInfo> pictureList = new ArrayList<>();
        for(String path : picDataSource.keySet()){
            //过滤出所有图片中状态为已经被选择的
            if(LocalPictureInfo.Statue.STATUE_SELECTED == picDataSource.get(path).getStatue()){
                pictureList.add(picDataSource.get(path));
            }
        }

        getImageFileList
                .get()
                .getImageFileList(pictureList)
                .execute(
                        new OkSubscriber<List<FileInfo>>(this.okHandler){

                            @Override
                            public void onNext(List<FileInfo> imageFileInfoList) {
                                super.onNext(imageFileInfoList);
                                if (imageFileInfoList != null && imageFileInfoList.size() > 0) {
                                    Intent intent = new Intent();
                                    //数据绑定
                                    Bundle bundle = new Bundle();
                                    ArrayList<FileInfo> bundleList = new ArrayList<>();
                                    bundleList.addAll(imageFileInfoList);
                                    //发送图片数据
                                    bundle.putParcelableArrayList(ConstDef.TAG_SELECTPIC, bundleList);
                                    //添加数据到Intent
                                    intent.putExtras(bundle);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    LogUtil.getUtils().w("create thumbnail failed.");
                                    //[S]modify by lll@xdja.com for add for 5675 2016/11/8
                                    mAdapterPresenter.setLoadingProgress(false);
                                    //[E]modify by lll@xdja.com for add for 5675 2016/11/8
                                    //重置发送状态信息
                                    getVu().resetSendStatus();
                                    //提示信息
                                    new XToast(PictureSelectActivity.this).display(R.string.send_image_failed);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                LogUtil.getUtils().w("create thumbnail failed.");

                                bSendingImage = false;

                                //[S]modify by lll@xdja.com for add for 5675 2016/11/8
                                mAdapterPresenter.setLoadingProgress(false);
                                //[E]modify by lll@xdja.com for add for 5675 2016/11/8
                                //重置发送状态信息
                                getVu().resetSendStatus();
                                //提示信息
                                new XToast(PictureSelectActivity.this).display(R.string.send_image_failed);
                            }
                        }
                );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (bSendingImage) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 启动图片预览界面（从预览按钮进入）
     */
    @Override
    public void startToPreviewPictures() {
        //点击预览按钮进入图片预览界面
        //如果无图片被选择，则点击预览按钮无效
        boolean flag = false;
        for(String path : picDataSource.keySet()){
            //过滤出所有图片中状态为已经被选择的
            if(LocalPictureInfo.Statue.STATUE_SELECTED == picDataSource.get(path).getStatue()){
                flag = true;
            }
        }
        if(!flag){
            return;
        }

        Glide.with(this).pauseRequests();

        Intent intent = new Intent(this, PicturePreviewActivity.class);
        Bundle bundle = new Bundle();
        //当前开始图片索引
        bundle.putInt(ConstDef.TAG_SELECTPIC_INDEX, 0);
        bundle.putBoolean(ConstDef.FROM_PREVIEW_BTN , true);
        //添加数据到Intent
        intent.putExtras(bundle);
        //startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    public int getSelectedCount() {
        int i = 0;
        for(String path : picDataSource.keySet()){
            //过滤出所有图片中状态为已经被选择的
            if(LocalPictureInfo.Statue.STATUE_SELECTED == picDataSource.get(path).getStatue()){
                i++;
            }
        }
        return i;
    }

    /*--------------Activity返回结果处理----------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ConstDef.FOR_RESULT_SEND){
            setResult(RESULT_OK, data);
            finish();
        }else{
            /*//界面刷新
            mAdapterPresenter.setData(picDataSource);*/
            //刷新指示器
            getVu().refreshSelectPictureIndicator(getSelectedCount());
            mAdapterPresenter.setData(PicInfoCollection.getLocalPicInfo());
        }
    }


    /*---------------以下处理来自消息总线-----------------*/
    @Subscribe
    public void onPictureSelectChanged(IMProxyEvent.PicSelectEvent event){
        getVu().refreshSelectPictureIndicator(getSelectedCount());
    }

    /**
     * 启动图片预览界面事件（从点击图片进入）
     * @param event
     */
    @Subscribe
    public void startToPreviewPic(IMProxyEvent.PicturePreviewEvent event){
        Intent intent = new Intent(this, PicturePreviewActivity.class);
        Bundle bundle = new Bundle();
        //当前开始图片索引
        bundle.putInt(ConstDef.TAG_SELECTPIC_INDEX, event.getCurrentIndex());
        //添加数据到Intent
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }

    @Subscribe
    public void startToChatDetailPreview(IMProxyEvent.PictureToChatdetailEvent event){
        //数据返回
        Intent intent = new Intent();
        String path = event.getFilePath();
        if(path == null){
            return;
        }
        long msgid = -1;
        for(TalkMessageBean info : infos){
            FileInfo fileInfo = info.getFileInfo();
            if(fileInfo instanceof ChatDetailPicInfo){
                ChatDetailPicInfo picInfo = (ChatDetailPicInfo) fileInfo;
                if(path.equals(picInfo.getThumPath())){
                    msgid = info.get_id();
                }
            }else if(fileInfo instanceof VideoFileInfo){
                VideoFileInfo videoInfo = (VideoFileInfo) fileInfo;
                if(path.equals(videoInfo.getFilePath())){
                    msgid = info.get_id();
                }
            }
        }
        intent.putExtra(ConstDef.MSG_ID ,  msgid);
        setResult(RESULT_OK, intent);
        finish();
    }
	
    //modify by guorong@xdja.com,fix bug 2638.201608012
    @Subscribe
    public void picDestoried(IMProxyEvent.DestroyedEvent event){
        String path = null;
        TalkMessageBean talkBean = event.getTalkMessageBean();
        FileInfo fileInfo = talkBean.getFileInfo();
        if(fileInfo != null){
            path =  fileInfo.getFilePath();
        }
        if(event != null && !TextUtils.isEmpty(path)){
            for(LocalPictureInfo picInfo : pictureInfos){
                if(picInfo.getLocalPath() != null && picInfo.getLocalPath().equals(path)){
                    picInfo.setStatue(LocalPictureInfo.Statue.STATUE_DESTROY);
                    mAdapterPresenter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

}
