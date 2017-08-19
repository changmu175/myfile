package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ImageFileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.simcui.view.ChatImageView;

/**
 * 图片发送显示控件
 * Created by leill on 2016/6/22.
 */
public class ViewSendImageItem extends ViewChatDetailSendItem{

    /**
     * 图片内容控件
     */
    private ChatImageView mContentImg;

    private ImageView mShanFlagImg;

    /**
     * 图片加载进度条
     */
    //private ProgressBar mLoadingPBar;

    public ViewSendImageItem() {
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_sendphoto;
    }

    @Override
    public void bindDataSource(int position, @NonNull final TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        View view = getView();
        if (view != null){
            mContentImg = (ChatImageView) view.findViewById(R.id.sendPhoto);
            mShanFlagImg = (ImageView) view.findViewById(R.id.img_shan_flag);
            //mLoadingPBar = (ProgressBar) view.findViewById(R.id.loadProgress);
        }
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //启动图片预览界面
                getCommand().clickImageMessage(dataSource);
            }
        });
        initView();
    }

    @Override
    public void onViewReused() {
        mContentImg.setImageBitmap(null);
        contentLayout.setBackgroundResource(0);
        mContentImg.setMaxWidth(getImageViewFieldValue(mContentImg, "mMaxWidth"));
        mContentImg.setMaxHeight(getImageViewFieldValue(mContentImg, "mMaxHeight"));
        mContentImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private void initView(){
        ImageFileInfo imageFileInfo = (ImageFileInfo) dataSource.getFileInfo();

        if (imageFileInfo == null){
            loadImageResource(R.drawable.pic_failed);
            return ;
        }
        /**
         * 异常情况：
         * 1）数据库被清除：先判断图片是否已经在本地存在（一般情况下，存在），不存在则网络请求
         * 2）本地图片缓存文件被清理：直接显示失败图片
         */

        //本地图片存在
        if (isFileExist(imageFileInfo.getFilePath())){
            //加载本地已经下载好的图片
            if (contentLayout.getVisibility() == View.VISIBLE){
                loadImage(imageFileInfo.getFilePath());
            }
        }
        //如果本地不存在，则表示图片缓存被清理，直接显示失败图片
        else {
            //显示失败图片
            loadImageResource(R.drawable.pic_failed);
        }

        if (dataSource.isBomb()){
            mShanFlagImg.setVisibility(View.VISIBLE);
        } else {
            mShanFlagImg.setVisibility(View.GONE);
        }
    }

    /**
     * 加载URL指定图片
     * @param url
     */
    private void loadImage(String url){

        mContentImg.loadImage(url);
    }


    /**
     * 加载本地资源图片
     * @param srcId
     */
    private void loadImageResource(int srcId){
        mContentImg.setImageResource(srcId);
    }
}
