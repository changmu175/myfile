package com.xdja.imp.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.securevoipcommon.VoipFunction;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileExtraInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.presenter.command.IChatDetailMediaCommand;
import com.xdja.imp.ui.vu.FilePreviewView;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.imp.util.DisplayUtils;
import com.xdja.imp.util.UnitUtil;
import com.xdja.imp.util.XToast;
import com.xdja.imp.widget.HorizontalProgressBarPlayer;
import com.xdja.imp.widget.VideoPlayView;

import java.io.File;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频播放View     <br>
 * 创建时间：2017/2/28        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public class ViewVideoPreview extends FilePreviewView<IChatDetailMediaCommand, TalkMessageBean>
        implements View.OnClickListener, View.OnTouchListener {

    private final static int UPDATE_PROGRESS_CODE = 1;
    private final VideoPlayCallback mVideoPlayCallback = new VideoPlayCallback();
    private VideoPlayView mVideoView;
    private ImageView mFirstFrame;
    private TextView mBtnPlay;
    private TextView mVideoPlayerTime;
    private CheckBox mVideoStartCbx;
    private HorizontalProgressBarPlayer mPgbPlayer;
    private RelativeLayout mLayoutPlayerHeader;
    private RelativeLayout mLayoutPlayerRoot;
    private TextView mVideoDuration;
    private ImageView mImgFfState;
    private int progress = 0;
    private boolean isPlaying;
    private boolean isPause;
    private VideoFileInfo mVideoFileInfo;
    private Handler mHandler;

    @Override
    public int getLayoutRes() {
        return R.layout.layout_video_preview;
    }

    @Override
    public void injectView() {
        initView();
    }

    @Override
    public void onViewCreated() {
    }

    @Override
    public void onViewReused() {
        //jyg add 2017/3/13 start 解决复用产生的图片与短视频第一帧混乱
        mFirstFrame.setImageBitmap(null);
        //jyg add 2017/3/13 end
        //jyg add 2017/3/20 start 解决复用初始值未重新初始化
        isPlaying = false;
        isPause = false;
        progress = 0;
        mLayoutPlayerHeader.setVisibility(View.GONE);
        mLayoutPlayerRoot.setVisibility(View.GONE);
        mImgFfState.setVisibility(View.GONE);
        mBtnPlay.setVisibility(View.VISIBLE);
        //jyg add 2017/3/20 end
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        mVideoFileInfo = (VideoFileInfo) dataSource.getFileInfo();
        mVideoDuration.setText(UnitUtil.getVideoDuration(mVideoFileInfo.getAmountOfTime()));
        mPgbPlayer.setMax(mVideoFileInfo.getAmountOfTime());
        mVideoView.setVideoPath(mVideoFileInfo.getExtraInfo().getRawFileUrl());

        int firstItemPos = getCommand().getFirstItem();
        if (firstItemPos == position) {
            mFirstFrame.setVisibility(View.GONE);
            mBtnPlay.setVisibility(View.GONE);
        }
        if (isFirstFrameDownload()) {
            Glide.with(getActivity())
                    .load(mVideoFileInfo.getFilePath())
                    .into(mFirstFrame);
        } else {
            mBtnPlay.setVisibility(View.GONE);
            mImgFfState.setVisibility(View.VISIBLE);
            if (mVideoFileInfo.getFileState() == ConstDef.FAIL) {
                mImgFfState.setBackgroundResource(R.drawable.video_pre_fail);
            } else {
                mImgFfState.setBackgroundResource(R.drawable.video_pre_loading);
            }
        }
    }

    @Override
    public void onPause() {
        if (isPlaying && mVideoStartCbx.isChecked()) {
            mVideoStartCbx.setChecked(false);
        }
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPageSelected(int lastPos, int curPos) {
        mFirstFrame.setVisibility(View.VISIBLE);
        mLayoutPlayerHeader.setVisibility(View.GONE);
        mLayoutPlayerRoot.setVisibility(View.GONE);
        Glide.with(getActivity())
                .load(mVideoFileInfo.getFilePath())
                .into(mFirstFrame);
        if (isPlaying) {
            mVideoView.stopPlay();
        }
    }

    private void initView() {
        View view = getView();
        mHandler = new ViewPlayHandler();
        mVideoView = (VideoPlayView) view.findViewById(R.id.video_play_view);
        mVideoView.setOnClickListener(this);
        mVideoView.setVideoPlayCallback(mVideoPlayCallback);

        mFirstFrame = (ImageView) view.findViewById(R.id.video_first_frame);
        mLayoutPlayerHeader = (RelativeLayout) view.findViewById(R.id.layout_player_header);
        mLayoutPlayerRoot = (RelativeLayout) view.findViewById(R.id.layout_player_root);

        TextView bottomBar_back = (TextView) view.findViewById(R.id.bottombar_back_player);
        TextView mBtnClose = (TextView) view.findViewById(R.id.btn_close_player);
        mBtnClose.setOnTouchListener(this);

        mImgFfState = (ImageView) view.findViewById(R.id.img_ff_state);

        mVideoPlayerTime = (TextView) view.findViewById(R.id.video_play_time);
        mVideoDuration = (TextView) view.findViewById(R.id.video_duration);
        mVideoStartCbx = (CheckBox) view.findViewById(R.id.btn_start_video);
        mVideoStartCbx.setOnTouchListener(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottomBar_back.getLayoutParams();
        params.height = DisplayUtils.getBottomStatusHeight(getActivity());
        bottomBar_back.setLayoutParams(params);
        mBtnPlay = (TextView) view.findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);
        mPgbPlayer = (HorizontalProgressBarPlayer) view.findViewById(R.id.pgb_play);
        View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getCommand().longClick(getDataSource());
                return true;
            }
        };
        mVideoView.setOnLongClickListener(mLongClickListener);
        mFirstFrame.setOnLongClickListener(mLongClickListener);
        mVideoStartCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (isPause) {
                        mVideoView.resume();
                    } else if (!isPlaying) {
                        mVideoView.play();
                    }

                } else {
                    if (isPlaying) {
                        mVideoView.pause();
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_play) {
            switchPlayButton();
        } else if (v.getId() == R.id.video_play_view) {
            switchBar();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.btn_close_player) {
                getActivity().finish();
            } else if (v.getId() == R.id.btn_start_video) {
                switchPlayButton();
            }
        }
        return true;
    }

    /**
     * 切换播放按钮
     */
    private void switchPlayButton() {

        if (mVideoStartCbx.isChecked()) {
            //暂停播放
            mVideoStartCbx.setChecked(false);
            mBtnPlay.setVisibility(View.VISIBLE);
        } else {
            //开始播放，在此处对文件是否下载进行判断
            if (!isVideoDownload()) {
                LogUtil.getUtils().d("jsm-------------短视频文件未下载");
                getCommand().downLoadVideo(mVideoFileInfo);
                LogUtil.getUtils().d("jsm-------------短视频文件下载中");
                return;
            }

            //判断是否voip正在通话中
            if (!isVoipWorking()) {
                mBtnPlay.setVisibility(View.GONE);
                mFirstFrame.setVisibility(View.GONE);
                mVideoStartCbx.setChecked(true);
            }
        }
    }

    /**
     *
     */
    private void switchBar() {

        if (mLayoutPlayerHeader.getVisibility() == View.GONE) {
            mLayoutPlayerHeader.setVisibility(View.VISIBLE);
            mLayoutPlayerRoot.setVisibility(View.VISIBLE);
        } else {
            if (mLayoutPlayerHeader.getVisibility() == View.VISIBLE) {
                mLayoutPlayerHeader.setVisibility(View.GONE);
                mLayoutPlayerRoot.setVisibility(View.GONE);
            }
        }
    }

    private boolean isVideoDownload (){
        if (mVideoFileInfo != null && mVideoFileInfo.getFileSize() != 0) {
            FileExtraInfo extraInfo = mVideoFileInfo.getExtraInfo();
            if (extraInfo == null) {
                return false;
            }

            String rawPath = extraInfo.getRawFileUrl();
            File file = new File(rawPath);
            return file.exists() && file.length() == extraInfo.getRawFileSize();
        }
        return false;
    }

    private boolean isFirstFrameDownload (){
        if (mVideoFileInfo != null && mVideoFileInfo.getFileSize() != 0) {

            String firstFramePath = mVideoFileInfo.getFilePath();
            File file = new File(firstFramePath);
            return file.exists() && file.length() == mVideoFileInfo.getFileSize();
        }
        return false;
    }


    @SuppressLint("HandlerLeak")
    private class ViewPlayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRESS_CODE:
                    if (mPgbPlayer.getVisibility() == View.VISIBLE) {
                        mPgbPlayer.setProgress(progress);
                        mVideoPlayerTime.setText(UnitUtil.getVideoDuration(progress));
                    }
                    break;
            }
        }
    }

    private class VideoPlayCallback implements VideoPlayView.IVideoPlayCallback {

        @Override
        public void prepareListener() {
            isPause = false;
            isPlaying = true;
            mFirstFrame.setVisibility(View.GONE);
            mBtnPlay.setVisibility(View.GONE);
            if (!mVideoStartCbx.isChecked()) {
                mVideoStartCbx.setChecked(true);
            }
        }

        @Override
        public void progressListener(int pro) {
            if (pro / 1000 != progress) {
                progress = pro / 1000;
                mHandler.sendEmptyMessage(UPDATE_PROGRESS_CODE);
            }
        }

        @Override
        public void completeListener() {
            isPause = false;
            isPlaying = false;
            progress = 0;
            mBtnPlay.setVisibility(View.VISIBLE);
            mLayoutPlayerHeader.setVisibility(View.VISIBLE);
            mLayoutPlayerRoot.setVisibility(View.VISIBLE);
            mVideoStartCbx.setChecked(false);
            mHandler.sendEmptyMessage(UPDATE_PROGRESS_CODE);
        }

        @Override
        public void pauseListener(int currentPos) {

            isPause = true;
            isPlaying = false;
            if (mBtnPlay.getVisibility() == View.GONE) {
                mBtnPlay.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void resumeListener(int currentPos) {
            isPause = false;
            isPlaying = true;
            if (mBtnPlay.getVisibility() == View.VISIBLE) {
                mBtnPlay.setVisibility(View.GONE);
            }
        }

        @Override
        public void surfacePrepareCallback() {

            int firstItemPos = getCommand().getFirstItem();
            if (firstItemPos == getPosition() && !isVoipWorking()) {
                if (!isVideoDownload()) {
                    getCommand().downLoadVideo(mVideoFileInfo);
                } else {
                    mFirstFrame.setVisibility(View.GONE);
                    mBtnPlay.setVisibility(View.GONE);
                    mVideoView.play();
                }
                getCommand().setFirstItem(-1);

            } else {
                //如果第一帧下载完成，显示第一帧和播放按钮，否则显示第一帧下载失败状态
                if (isFirstFrameDownload()) {
                    mFirstFrame.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void surfaceDestroyListener(Bitmap currentPosBitmap) {
            Glide.with(getActivity())
                    .load(BitmapUtils.Bitmap2Bytes(currentPosBitmap))
                    .into(mFirstFrame);
            mFirstFrame.setVisibility(View.VISIBLE);
        }
    }
    //jyg add 2017/3/15 start 解决bug10026
    private boolean isVoipWorking () {
        if (VoipFunction.getInstance().hasActiveCall()
                || VoipFunction.getInstance().isMediaPlaying()) {
            XToast toast = new XToast(getActivity());
            toast.display(R.string.Phone_is_inCall);
            return true;
        }
        return false;
    }
    //jyg add 2017/3/15 end 解决bug10026
}
