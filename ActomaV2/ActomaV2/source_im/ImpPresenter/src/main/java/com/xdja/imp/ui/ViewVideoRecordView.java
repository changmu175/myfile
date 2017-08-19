package com.xdja.imp.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileExtraInfo;
import com.xdja.imp.domain.model.VideoFileInfo;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.IVideoRecordCommand;
import com.xdja.imp.ui.vu.IVideoRecordVu;
import com.xdja.imp.util.DisplayUtils;
import com.xdja.imp.util.UnitUtil;
import com.xdja.imp.util.XToast;
import com.xdja.imp.widget.CustomView;
import com.xdja.imp.widget.SendView;
import com.xdja.simcui.recordingControl.manager.VideoManager;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频录制View     <br>
 * 创建时间：2017/1/28        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */


public class ViewVideoRecordView extends ImpActivitySuperView <IVideoRecordCommand>
        implements IVideoRecordVu ,VideoManager.IVideoRecordCallback,View.OnClickListener,
        View.OnTouchListener{

    private final static int MAX_PRO = 10;
	//jyg add 2017/3/20 start 修改短视频录制最短时间为2s
    private final static int MIN_PRO = 2;
	//jyg add 2017/3/20 end
    private VideoManager mVideoManager;
    private CustomView progressBar;
    private int mProgress;
    private TextView videoRecordTime;
    private TextView mBtnRecord;
    private SendView send;
    private RelativeLayout recordLayout;
    private RelativeLayout toolBars;
    private boolean isCompleteRecord;
    private boolean isSending;
    private Handler handler;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_video;
    }
    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if (view != null){
            initView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCompleteRecord) {
            mVideoManager.resume();
        } else {
            cancelAndRerecord();
        }
    }

    @Override
    public void onPause() {
        if (isSending) {
            return;
        }
        if (isCompleteRecord) {
            mVideoManager.pause();
        } else {
            cancelAndRerecord();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mVideoManager.stopPlay(false);
        super.onDestroy();
    }

    /**
     *
     * 初始化界面控件
     *
     */
    private void initView(View view){
        handler = new ViewRecordHandler();
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.main_surface_view);
        toolBars = (RelativeLayout) view.findViewById(R.id.layout_video_tools);
        TextView bottomBarBack = (TextView) view.findViewById(R.id.bottombar_back_recorder);

        send = (SendView) view.findViewById(R.id.view_send);
        send.backLayout.setOnClickListener(this);
        send.selectLayout.setOnClickListener(this);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottomBarBack.getLayoutParams();
        params.height = DisplayUtils.getBottomStatusHeight(getActivity());
        bottomBarBack.setLayoutParams(params);

        mBtnRecord = (TextView) view.findViewById(R.id.btn_recorder);
        mBtnRecord.setOnTouchListener(this);
        TextView btnTransCamera = (TextView) view.findViewById(R.id.btn_trans);
        btnTransCamera.setOnClickListener(this);

        videoRecordTime = (TextView) view.findViewById(R.id.video_record_time);
        TextView btnClose = (TextView) view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(this);

        recordLayout = (RelativeLayout) view.findViewById(R.id.layout_recorder);
        progressBar = (CustomView) view.findViewById(R.id.main_progress_bar);

        mVideoManager = new VideoManager(getActivity());
        mVideoManager.setVideoRecordCallback(this);
        mVideoManager.setSurfaceView(surfaceView);
    }


    private void startView() {
        if ( progressBar.getVisibility() == View.GONE) {
            videoRecordTime.setVisibility(View.VISIBLE);
            toolBars.setVisibility(View.GONE);
            videoRecordTime.setVisibility(View.VISIBLE);
            videoRecordTime.setText("");
            progressBar.startAnim();
            mProgress = 0;
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startProgressBar();
        }
    }

    private void stopView(boolean isSave) {
        if ( progressBar.getVisibility() == View.VISIBLE) {
            progressBar.stopAnim();
            handler.removeMessages(0);
            progressBar.stopProgressBar();
            progressBar.setVisibility(View.GONE);
            videoRecordTime.setVisibility(View.GONE);

            if (isSave) {
                mBtnRecord.setVisibility(View.GONE);
                send.startAnim(getContext());
            } else {
                toolBars.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void prepareRecord() {
        handler.removeMessages(0);
        handler.sendMessageDelayed(handler.obtainMessage(0), 1000);
        isCompleteRecord = false;
    }

    @Override
    public void completeRecord() {
        isCompleteRecord = true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_rerecored) {//重新录制
            cancelAndRerecord();

        } else if (id == R.id.btn_send) {
            isSending = true;
            mVideoManager.stopPlay(false);

            VideoFileInfo videoFileInfo = new VideoFileInfo();
            videoFileInfo.setAmountOfTime(mProgress);
            videoFileInfo.setFilePath(mVideoManager.getFirstFrameFile().getPath());
            videoFileInfo.setFileName(mVideoManager.getFirstFrameFile().getName());
            videoFileInfo.setSuffix(ConstDef.VIDEO_SUFFIX);
            videoFileInfo.setFileType(ConstDef.TYPE_VIDEO);
            videoFileInfo.setFileSize(mVideoManager.getFirstFrameFile().length());
            videoFileInfo.setVideoSize(mVideoManager.getVideoFile().length());
            FileExtraInfo extraInfo = new FileExtraInfo();
            extraInfo.setRawFileUrl(mVideoManager.getVideoFile().getPath());
            extraInfo.setRawFileName(mVideoManager.getVideoFile().getName());
            extraInfo.setRawFileSize(mVideoManager.getVideoFile().length());
            videoFileInfo.setExtraInfo(extraInfo);

            getCommand().sendVideoMessage(videoFileInfo);

        } else if (id == R.id.btn_close) {
            getActivity().finish();
        } else if (id == R.id.btn_trans) {
            mVideoManager.transformCamera();
        }
    }

    private void cancelAndRerecord(){
        send.stopAnim(getContext());
        stopView(false);
        isCompleteRecord = false;
        recordLayout.setVisibility(View.VISIBLE);
        toolBars.setVisibility(View.VISIBLE);
        mBtnRecord.setVisibility(View.VISIBLE);
        videoRecordTime.setVisibility(View.GONE);
        mVideoManager.deleteTargetFile();
        //停止预览并且重新录制视频
        mVideoManager.stopPlay(true);
    }

    @Override
    public int onKeyBack() {
        if (isCompleteRecord) {
            cancelAndRerecord();
            return -1;
        }
        return 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
            boolean ret = false;
            int action = event.getAction();

            if (v.getId() == R.id.btn_recorder) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mVideoManager.record();
                        startView();
                        ret = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mProgress < MIN_PRO) {
                            //时间太短不保存
                            mVideoManager.stopRecordUnSave();
                            new XToast(getContext()).display(R.string.video_record_timeshort,
                                    Gravity.BOTTOM, 0, DisplayUtils.dp2px(getContext(), 150),
                                    Toast.LENGTH_SHORT);
                            stopView(false);
                            break;
                        }
                        //停止录制
                        mVideoManager.stopRecordSave();
                        stopView(true);
                        ret = false;
                        break;
                }

            }
            return ret;
    }


    @SuppressLint("HandlerLeak")
    private class ViewRecordHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mVideoManager.isRecording()) {
                        mProgress++;
                        videoRecordTime.setText(UnitUtil.getVideoRecordDuration(mProgress, getContext()));

                        if ( mProgress > MAX_PRO) {
                            mProgress--;
                            videoRecordTime.invalidate();
                            //停止录制
                            mVideoManager.stopRecordSave();
                            stopView(true);
                        } else {
                            sendMessageDelayed(handler.obtainMessage(0), 1000);
                        }
                    }
                    break;
            }
        }
    }
}
