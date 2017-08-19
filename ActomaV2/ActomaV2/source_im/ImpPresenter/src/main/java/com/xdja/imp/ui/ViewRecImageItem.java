package com.xdja.imp.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.xdja.comm.uitl.CommonUtils;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.ImageFileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.simcui.view.ChatImageView;

import java.io.File;
import java.lang.reflect.Field;

/**
 * 图片接收显示控件
 * Created by leill on 2016/6/22.
 */
public class ViewRecImageItem extends ViewChatDetailRecItem {

    /**
     * 图片显示控件
     */
    private ChatImageView mContentImg;

    /**
     * 闪信图片标识
     */
    private ImageView mShanFlagImg;

    /**
     * 播放销毁动画的容器
     */
    private ImageView bombAnimImageView;

    /**
     * 图片加载进度条
     */
    private ProgressBar mLoadingPBar;

    /**
     * 图片重下载
     */
    private ImageView reDownloadImageBtn;

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_recphoto;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null){
            mContentImg = (ChatImageView) view.findViewById(R.id.img_rec_photo);
            mShanFlagImg = (ImageView) view.findViewById(R.id.img_shan_flag);
            bombAnimImageView = (ImageView) view.findViewById(R.id.bomb_anim);
            mLoadingPBar = (ProgressBar) view.findViewById(R.id.loadProgress);
            reDownloadImageBtn = (ImageView) view.findViewById(R.id.reDownload);
        }

        if (contentLayout != null) {
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().clickImageMessage(dataSource);
                }
            });
        }
    }

    @Override
    public void onViewReused() {
        super.onViewReused();
        mContentImg.setImageBitmap(null);
        contentLayout.setBackgroundResource(0);
        mLoadingPBar.setVisibility(View.VISIBLE);
        mContentImg.setMaxWidth(getImageViewFieldValue(mContentImg, "mMaxWidth"));
        mContentImg.setMaxHeight(getImageViewFieldValue(mContentImg, "mMaxHeight"));
        mContentImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView();
    }

    private void initView() {
	    //fix bug 3007 by juyingang  20161012 begain 
        contentLayout.setVisibility(View.VISIBLE);
        reDownloadImageBtn.setVisibility(View.GONE);
        //fix bug by licong
        //如果状态为销毁中，无法执行销毁动画，更改消息状态
        if (dataSource.getMessageState() == ConstDef.STATE_DESTROY /*||
                dataSource.getMessageState() == ConstDef.STATE_DESTROYING*/) {
            //加载闪信显示图片
            loadImageResource(R.drawable.bg_shanxin_image);
            //闪信标识可见
            mShanFlagImg.setVisibility(View.VISIBLE);
        } else {
            mShanFlagImg.setVisibility(View.GONE);

            ImageFileInfo imageFileInfo = (ImageFileInfo) dataSource.getFileInfo();
            if (imageFileInfo == null ||
                    TextUtils.isEmpty(imageFileInfo.getFilePath())){
                loadImageResource(R.drawable.chatdetail_pic_failed);
                return ;
            }

            //文件未下载，或者下载失败，或者数据请被清除
            if (TextUtils.isEmpty(imageFileInfo.getFilePath())) {
                return;
            }

            File file = new File(imageFileInfo.getFilePath());
            if(!file.exists() || (file.length() != imageFileInfo.getFileSize())){
                if (imageFileInfo.getFileState() == ConstDef.FAIL) {
                    if (reDownloadImageBtn!=null){
                        reDownloadImageBtn.setVisibility(View.VISIBLE);
                        reDownloadImageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageFileInfo imageInfo = (ImageFileInfo) dataSource.getFileInfo();

                                if (imageInfo == null) {
                                    return;
                                }
                                if (!CommonUtils.isFastDoubleClick() &&
                                        imageInfo.getFileState() == ConstDef.FAIL) {
                                    mLoadingPBar.setVisibility(View.VISIBLE);
                                    reDownloadImageBtn.setVisibility(View.GONE);
                                    contentLayout.setVisibility(View.INVISIBLE);
                                    getCommand().loadImage(dataSource);
                                }
                            }
                        });
                    }
                    //fix bug 3007 by juyingang  20161012 end
                    //文件下载失败，显示失败图标
                    loadImageResource(R.drawable.chatdetail_pic_failed);

                } else if (isFileExist(imageFileInfo.getFilePath())){
                    //加载本地图片
                    if (contentLayout.getVisibility() == View.VISIBLE){
                        loadImage(imageFileInfo.getFilePath());
                    }
                } else {
                    //网路请求,并且判断当前item是否可见
                    if (contentLayout.getVisibility() == View.VISIBLE){
                        getCommand().loadImage(dataSource);
                    }
                }
            }
            //已经已经下载成功（存在被清除的可能性）
            else {
                if (isFileExist(imageFileInfo.getFilePath())){
                    //加载本地图片
                    if (contentLayout.getVisibility() == View.VISIBLE){
                        loadImage(imageFileInfo.getFilePath());
                    }
                } else {
                    //显示失败图片
                    loadImageResource(R.drawable.chatdetail_pic_failed);
                }
            }
        }
    }

    /**
     * 根据URL加载图片
     * @param url
     */
    private void loadImage(String url){
        mContentImg.loadImage(url);
        mLoadingPBar.setVisibility(View.GONE);
        setMessageDestroy(false);

        //需求：图片下载成功后，才发送已阅读状态
        if (dataSource.getMessageState() < ConstDef.STATE_READED) {
            //如果当前界面正在显示，并且消息状态是初始状态，发送已阅读回执
            if (getCommand().getActivityIsShowing()) {
                //发送阅读回执
                getCommand().sendReadReceipt(dataSource);
            }
        }

        //如果状态为闪信销毁，则启动销毁动画
        if (dataSource.getMessageState() == ConstDef.STATE_DESTROYING) {
            startBombAnim();

            final TalkMessageBean cloneSource = obtainCloneObj(dataSource);
            getCommand().postMsgDestory(cloneSource);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    getCommand().postDestroyAnimate(cloneSource);
                    loadImageResource(R.drawable.bg_shanxin_image);
                }
            }, 700);
        }
    }

    /**
     * 根据资源文件，加载图片
     */
    private void loadImageResource(int srcId){
        mContentImg.setImageResource(srcId);
        mLoadingPBar.setVisibility(View.GONE);
        setMessageDestroy(dataSource.isBomb());
    }


    /**
     * 开始播放消息销毁动画
     */
    private void startBombAnim() {
        if (bombAnimImageView != null) {
            AnimationDrawable voiceAnim = (AnimationDrawable) bombAnimImageView.getBackground();
            if (voiceAnim != null) {
                voiceAnim.start();
            }
        }
    }

    /**
     *
     * @param dataSource
     * @return
     */
    private TalkMessageBean obtainCloneObj(TalkMessageBean dataSource){
        /*TalkMessageBean resultSource = new TalkMessageBean();
        resultSource.setSortTime(dataSource.getSortTime());
        resultSource.setMine(dataSource.isMine());
        resultSource.setShowTime(dataSource.getShowTime());
        resultSource.setContent(dataSource.getContent());
        resultSource.setTo(dataSource.getTo());
        resultSource.setFailCode(dataSource.getFailCode());
        resultSource.set_id(dataSource.get_id());
        resultSource.setFileInfo(dataSource.getFileInfo());
        resultSource.setFrom(dataSource.getFrom());
        resultSource.setIsBomb(dataSource.isBomb());
        resultSource.setGroupMsg(dataSource.isGroupMsg());
        resultSource.setSenderCardId(dataSource.getSenderCardId());
        resultSource.setLimitTime(dataSource.getLimitTime());
        resultSource.setMessageType(dataSource.getMessageType());*/
        return new TalkMessageBean(dataSource);
    }
}
