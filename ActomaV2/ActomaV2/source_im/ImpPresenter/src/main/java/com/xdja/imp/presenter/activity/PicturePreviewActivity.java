package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.domain.interactor.def.GetImageFileList;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.PicturePreviewAdapterPresenter;
import com.xdja.imp.presenter.command.IPicturePreviewCommand;
import com.xdja.imp.ui.ViewPicturePreview;
import com.xdja.imp.ui.vu.IPicturePreviewVu;
import com.xdja.imp.util.PicInfoCollection;
import com.xdja.imp.util.XToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * 图片预览界面
 * Created by leill on 2016/6/20.
 */
public class PicturePreviewActivity extends IMActivityPresenter<IPicturePreviewCommand, IPicturePreviewVu>
        implements IPicturePreviewCommand{

    @Inject
    Lazy<GetImageFileList> getImageFileList;

    /** 当前选中图片的索引值 */
    private int mCurrentSelectIndex = 0;

    /** 图片列表*/
    private List<LocalPictureInfo> pictureInfos = new ArrayList<>();

    private boolean isSendPic = false;

    /**是否是从上个界面的预览按钮跳转*/
    private boolean isFromPreviewBtn = false;

    /**是否是从拍照界面跳转*/
    private boolean isFromTakephoto = false;

    @Override
    protected Class<? extends IPicturePreviewVu> getVuClass() {
        return ViewPicturePreview.class;
    }

    @Override
    protected IPicturePreviewCommand getCommand() {
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

        //获取预览图片
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentSelectIndex = intent.getIntExtra(ConstDef.TAG_SELECTPIC_INDEX, 0);
            isFromPreviewBtn = intent.getBooleanExtra(ConstDef.FROM_PREVIEW_BTN , false);
            isFromTakephoto = intent.getBooleanExtra(ConstDef.FROM_TAKE_PHOTO , false);
        }

        if(isFromTakephoto){
            pictureInfos = intent.getParcelableArrayListExtra(ConstDef.TAG_SELECTPIC);
            if(pictureInfos != null && pictureInfos.size() != 0){
                LocalPictureInfo localPictureInfo = pictureInfos.get(0);
                localPictureInfo.setStatue(LocalPictureInfo.Statue.STATUE_SELECTED);
                pictureInfos.clear();
                pictureInfos.add(localPictureInfo);
                PicInfoCollection.getLocalPicInfo().put(localPictureInfo.getLocalPath() , localPictureInfo);
            }

        }else{
            /*本地所有图片*/
            Map<String, LocalPictureInfo> dataSource = PicInfoCollection.getLocalPicInfo();

            for(String path : dataSource.keySet()){
                if(isFromPreviewBtn){
                    if(LocalPictureInfo.Statue.STATUE_SELECTED != dataSource.get(path).getStatue()){
                        continue;
                    }
                }
                pictureInfos.add(dataSource.get(path));
            }
        }

        //获取ViewPager
        /* 适配器*/
        PicturePreviewAdapterPresenter mPicturePreviewAdapter = new PicturePreviewAdapterPresenter(this, pictureInfos);

        //设置适配器
        getVu().initViewPager(mPicturePreviewAdapter);

        //添加数据
        getVu().setDataSource(pictureInfos);
        //设置当前默认选中项
        getVu().setCurrentItem(mCurrentSelectIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Glide清除缓存
        Glide.get(this).clearMemory();
    }

    @Override
    public void sendPictureMessage() {

        isSendPic = true;

        if (isFromTakephoto){
            List<LocalPictureInfo> picList = new ArrayList();
            if(pictureInfos != null && pictureInfos.size() != 0){
                picList.addAll(pictureInfos);
            }
            sendImage(picList);
        } else {
            List<LocalPictureInfo> picList = new ArrayList();
            for (LocalPictureInfo info : pictureInfos) {
                if (info.getStatue() == LocalPictureInfo.Statue.STATUE_SELECTED){

                    //juyingang fix bug 4236 begin 20160928
                    try {
                        File file = new File(info.getLocalPath());
                        if (!file.exists() || file.length() == 0){
                            LogUtil.getUtils().d("picture don't exist");
                            //提示信息
                            new XToast(PicturePreviewActivity.this).display(R.string.pic_inexist_text);
                            continue;
                        }
                    } catch (Exception e) {
                        LogUtil.getUtils().e(e.getMessage());
                    }
                    //juyingang fix bug 4236 begin 20160928

                    picList.add(info);
                }
            }
            sendImage(picList);
        }
    }

    @Override
    public void onBackPressed() {
        if (isSendPic) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean isFromTakePhoto() {
        return isFromTakephoto;
    }

    /**
     * 发送图片图片
     * @param pictureList
     */
    private void sendImage(List<LocalPictureInfo> pictureList){

        //发送图片之前，清理一下Glide消耗的内存
        Glide.get(this).clearMemory();

        getImageFileList
                .get()
                .getImageFileList(pictureList)
                .execute(
                        new OkSubscriber<List<FileInfo>>(this.okHandler){

                            @Override
                            public void onNext(List<FileInfo> imageFileInfoList) {
                                super.onNext(imageFileInfoList);
                                //fix bug 3073 by licong,reView by zya,2016/8/29
                                if (imageFileInfoList != null) {
                                    Intent intent = new Intent();
                                    //数据绑定
                                    Bundle bundle = new Bundle();
                                    ArrayList<FileInfo> bundleList = new ArrayList<>();
                                    bundleList.addAll(imageFileInfoList);
                                    //发送图片数据
                                    bundle.putParcelableArrayList(ConstDef.TAG_SELECTPIC, bundleList);
                                    //添加数据到Intent
                                    intent.putExtras(bundle);
                                    if(isSendPic && !isFromTakephoto){
                                        setResult(ConstDef.FOR_RESULT_SEND, intent);
                                    }else{
                                        setResult(RESULT_OK, intent);
                                    }
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                LogUtil.getUtils().w("create thumbnail failed.");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //重置发送状态信息
                                        getVu().resetSendStatus();
                                        //提示信息
                                        new XToast(PicturePreviewActivity.this).display(R.string.send_image_failed);
                                    }
                                });
                            }

                        }
                );
    }

}
