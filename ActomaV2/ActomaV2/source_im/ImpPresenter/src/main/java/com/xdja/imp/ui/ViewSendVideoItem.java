package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.util.UnitUtil;
import com.xdja.simcui.view.ChatImageView;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：图片发送显示控件     <br>
 * 创建时间：2017/2/7       <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public class ViewSendVideoItem extends ViewChatDetailSendItem{

    private static final int MAX_PROGRESS = 100;

    private ChatImageView mVideoImg;

    private TextView mVideoTime;

    private ImageView mVideoPlayer;

    private com.xdja.imp.widget.CircleProgressBar mCircleProgressBar;

    public ViewSendVideoItem() {
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_sendvideo;
    }

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null){
            mVideoImg = (ChatImageView) view.findViewById(R.id.sendVideo_photo);
            mVideoTime = (TextView) view.findViewById(R.id.video_time);
            mCircleProgressBar = (com.xdja.imp.widget.CircleProgressBar) view.findViewById(R.id.video_upload_cpb);
            mVideoPlayer = (ImageView) view.findViewById(R.id.video_upload_player);
            mCircleProgressBar.setMax(MAX_PROGRESS);
        }

        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getCommand().clickVideoMessage(dataSource);
                VideoFileInfo videoFileInfo = (VideoFileInfo) dataSource.getFileInfo();
                if (videoFileInfo == null ||
                        TextUtils.isEmpty(videoFileInfo.getFilePath())){
                    loadImageResource(R.drawable.pic_failed);
                }
            }
        });
    }

    @Override
    public void bindDataSource(int position, @NonNull final TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView();
    }

    private void initView(){
        VideoFileInfo videoFileInfo = (VideoFileInfo) dataSource.getFileInfo();

        if (videoFileInfo == null){
            return;
        }

        if( dataSource.getMessageState() == ConstDef.STATE_SENDING) {

            if (mCircleProgressBar.getVisibility() == View.GONE) {
                mCircleProgressBar.setVisibility(View.VISIBLE);
            }
            if (mCircleProgressBar.getVisibility() == View.VISIBLE) {
                mVideoPlayer.setVisibility(View.GONE);
            }

            if (mVideoTime.getVisibility() == View.VISIBLE) {
                mVideoTime.setVisibility(View.GONE);
            }

            mCircleProgressBar.setProgress(videoFileInfo.getPercent());

        } else {

            if ( mCircleProgressBar.getVisibility() == View.VISIBLE) {
                mCircleProgressBar.setVisibility(View.GONE);

            }

            if (mCircleProgressBar.getVisibility() == View.GONE) {
                mVideoPlayer.setVisibility(View.VISIBLE);
            }

            if (mVideoTime.getVisibility() == View.GONE) {
                mVideoTime.setVisibility(View.VISIBLE);
            }
            mVideoTime.setText(UnitUtil.getVideoDuration(videoFileInfo.getAmountOfTime()));
        }

        mVideoImg.setVisibility(View.VISIBLE);
        //本地图片存在
        if (isFileExist(videoFileInfo.getFilePath())){
            if (contentLayout.getVisibility() == View.VISIBLE){
                loadImage(videoFileInfo.getFilePath());
            }
        } else {//如果本地不存在，则表示图片缓存被清理，直接显示失败图片
            //显示失败图片
            loadImageResource(R.drawable.pic_failed);
        }
    }

    @Override
    public void onViewReused() {

        mVideoImg.setImageBitmap(null);
        contentLayout.setBackgroundResource(0);
        mVideoImg.setMaxWidth(getImageViewFieldValue(mVideoImg, "mMaxWidth"));
        mVideoImg.setMaxHeight(getImageViewFieldValue(mVideoImg, "mMaxHeight"));
        mVideoImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }


    /**
     * 根据资源文件，加载图片
     */
    private void loadImageResource(int srcId){
        mVideoImg.setImageResource(srcId);
    }

    /**
     * 加载URL指定图片
     * @param url 图片路径
     */
    private void loadImage(String url){

        mVideoImg.loadImage(url);
    }

}
