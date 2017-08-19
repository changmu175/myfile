package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.util.UnitUtil;
import com.xdja.simcui.view.ChatImageView;

import java.io.File;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频接收显示控件     <br>
 * 创建时间：2017/2/7       <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */


public class ViewRecVideoItem extends ViewChatDetailRecItem {

    private final static int MAX = 100;

    private ChatImageView mVideoImg;
    private TextView mVideoDuration;
    private TextView mVideoSize;
    private com.xdja.imp.widget.CircleProgressBar mCircleProgressBar;
    private ImageView mVideoPlayer;
    /**
     * 短视频重下载
     */
    private ImageView reDownloadBtn;

    /**
     * 短视频缩略图加载进度条
     */
    private ProgressBar mLoadingPBar;


    public ViewRecVideoItem() {
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.chatdetail_item_recvideo;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();

        if (view != null) {
            mVideoPlayer = (ImageView) view.findViewById(R.id.video_rec_player);
            mLoadingPBar = (ProgressBar) view.findViewById(R.id.loadProgress);
            mVideoImg = (ChatImageView) view.findViewById(R.id.recVideo_photo);
            mVideoDuration = (TextView) view.findViewById(R.id.rec_video_time);
            mVideoSize = (TextView) view.findViewById(R.id.rec_video_size);
			//jyg add 2017/3/15 start 解决bug9261
            reDownloadBtn = (ImageView) view.findViewById(R.id.reDownload);
            reDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reDownloadBtn.setVisibility(View.GONE);
                    mLoadingPBar.setVisibility(View.VISIBLE);
                    dataSource.getFileInfo().setFileState(ConstDef.LOADING);
                    loadImageResource(R.drawable.video_ff_loading);
                    //网路请求,并且判断当前item是否可见
                    getCommand().loadImage(dataSource);
                }
            });
			//jyg add 2017/3/15 end
            mCircleProgressBar = (com.xdja.imp.widget.CircleProgressBar) view.findViewById(R.id.video_down_cpb);
            mCircleProgressBar.setMax(MAX);

        }

        if (contentLayout != null) {
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //确保当前缩略图已经下载完成
                    if (reDownloadBtn.getVisibility() != View.VISIBLE
                            && mLoadingPBar.getVisibility() == View.GONE) {
                        getCommand().clickVideoMessage(dataSource);
                    }
                }
            });
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull final TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView();
    }

    @Override
    public void onViewReused() {
        super.onViewReused();
        contentLayout.setBackgroundResource(0);
        mVideoImg.setImageBitmap(null);
        mLoadingPBar.setVisibility(View.GONE);
        mVideoSize.setVisibility(View.GONE);
        mVideoDuration.setVisibility(View.GONE);
        mCircleProgressBar.setVisibility(View.GONE);
        mVideoPlayer.setVisibility(View.GONE);
        reDownloadBtn.setVisibility(View.INVISIBLE);
        mVideoImg.setMaxWidth(getImageViewFieldValue(mVideoImg, "mMaxWidth"));
        mVideoImg.setMaxHeight(getImageViewFieldValue(mVideoImg, "mMaxHeight"));
        mVideoImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private void initView() {

        VideoFileInfo videoFileInfo = (VideoFileInfo) dataSource.getFileInfo();
        if (videoFileInfo == null) {
            return;
        }
        //第一帧地址为空时， 加载默认失败图标
        if (TextUtils.isEmpty(videoFileInfo.getFilePath())) {
            loadImageResource(R.drawable.video_ff_fail);
            return;
        }
        //jyg add 2017/3/16 start 解决
        if (videoFileInfo.getType() == ConstDef.FILE_IS_THUMB
                && videoFileInfo.getFileState() == ConstDef.LOADING) {
            if (mLoadingPBar.getVisibility() == View.GONE) {
                mLoadingPBar.setVisibility(View.VISIBLE);
            }
            return;
        }
        //jyg add 2017/3/16 end
		//jyg add 2017/3/15 start 解决bug9261
        if (videoFileInfo.getType() != ConstDef.FILE_IS_RAW
                && videoFileInfo.getFileState() == ConstDef.FAIL) {
            mLoadingPBar.setVisibility(View.GONE);
            reDownloadBtn.setVisibility(View.VISIBLE);
            loadImageResource(R.drawable.video_ff_fail);
            return;
        }
		//jyg add 2017/3/15 end

        //文件未下载，或者下载失败，或者数据请被清除，下载第一帧
        File file = new File(videoFileInfo.getFilePath());
        if (!file.exists() || (file.length() != videoFileInfo.getFileSize())) {
            dataSource.getFileInfo().setFileState(ConstDef.LOADING);
            mLoadingPBar.setVisibility(View.VISIBLE);
            loadImageResource(R.drawable.video_ff_loading);
            getCommand().loadImage(dataSource);
            return;
        } else {
            loadImage(videoFileInfo.getFilePath());
            mLoadingPBar.setVisibility(View.GONE);
        }

        //短视频下载过程中，进度更新
        if (videoFileInfo.getFileState() == ConstDef.LOADING
                && videoFileInfo.getType() == ConstDef.FILE_IS_RAW) {

            if (mCircleProgressBar.getVisibility() == View.GONE) {
                mCircleProgressBar.setVisibility(View.VISIBLE);
                mVideoSize.setVisibility(View.GONE);
                mVideoPlayer.setVisibility(View.GONE);
                mVideoDuration.setVisibility(View.GONE);
            }
            mCircleProgressBar.setProgress(videoFileInfo.getPercent());

        } else {
            //非下载过程中不显示进度条
                mCircleProgressBar.setVisibility(View.GONE);
                mVideoPlayer.setVisibility(View.VISIBLE);
                mVideoDuration.setVisibility(View.VISIBLE);
                mVideoSize.setVisibility(View.VISIBLE);
                mVideoDuration.setText(UnitUtil.getVideoDuration(videoFileInfo.getAmountOfTime()));
                mVideoSize.setText(UnitUtil.getVideoFileSize(videoFileInfo.getVideoSize()));
        }
    }

    /**
     * 加载URL指定图片
     *
     * @param url 加载图片地址
     */
    private void loadImage(String url) {
        setMessageDestroy(false);

        if (mVideoImg.getVisibility() == View.GONE) {
            mVideoImg.setVisibility(View.VISIBLE);
        }
        mVideoImg.loadImage(url);
    }

    /**
     * 根据资源文件，加载图片
     */
    private void loadImageResource(int srcId) {
        setMessageDestroy(false);

        if (mVideoImg.getVisibility() == View.GONE) {
            mVideoImg.setVisibility(View.VISIBLE);
        }
        mVideoImg.setImageResource(srcId);
    }
}
