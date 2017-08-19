package com.xdja.imp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.util.AudioFunctions;

import java.io.File;
import java.io.IOException;


/**
 * 项目名称：            <br>
 * 类描述  ：           <br>
 * 创建时间：2017/3/2     <br>
 * 修改记录：             <br>
 *
 * @author jyg@xdja.com   <br>
 */


public class VideoPlayView extends SurfaceView implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener{

    private final SurfaceHolder.Callback mSHCallback;

    private VideoManagerHandler handler;

    private MediaPlayer mMediaPlayer;

    private SurfaceHolder mSurfaceHolder;

    private String mVideoPath;

    private VideoPlayView.IVideoPlayCallback mPlayCallback;

    private int mCurrentTime;

    public void setVideoPlayCallback(VideoPlayView.IVideoPlayCallback playCallback) {
        this.mPlayCallback = playCallback;
    }
    private String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String mVideoPath) {
        this.mVideoPath = mVideoPath;
    }

    public VideoPlayView(Context context) {
        this(context, null);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSHCallback = this;
        initMiniVideo();
    }


    /**
     * 初始化播放所需SurfaceView
     */
    private void initMiniVideo () {
        handler = new VideoManagerHandler();
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(mSHCallback);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    /**
     * 播放视频
     */
    public void play(){
        if (initMediaPlayer() != 0){
            return;
        }
        try {
            mMediaPlayer.prepare();
            if (mPlayCallback != null) {
                mPlayCallback.prepareListener();
            }
            mMediaPlayer.seekTo(0);
            mMediaPlayer.start();
            handler.sendEmptyMessage(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放短视频
     */
    public void pause() {
        if (mMediaPlayer != null) {
            if (mPlayCallback != null) {
                try {
                    mCurrentTime = mMediaPlayer.getCurrentPosition();
                    mPlayCallback.pauseListener(mCurrentTime / 1000);
                } catch (Exception e) {
                    LogUtil.getUtils().i("MediaPlayer is release");
                }
            }
            mMediaPlayer.pause();
        }
    }

    /**
     * 恢复播放短视频
     */
    public void resume() {
        if (mMediaPlayer != null) {
            if (mPlayCallback != null) {
                try {
                    mCurrentTime = mMediaPlayer.getCurrentPosition();
                    mPlayCallback.resumeListener(mCurrentTime / 1000);
                    mMediaPlayer.start();
                } catch (Exception e) {
                    LogUtil.getUtils().i("MediaPlayer is release");
                }
            }
        }
    }


    /**
     * 停止播放
     */
    public void stopPlay() {
        if (mMediaPlayer != null) {
            mCurrentTime = 0;
            mPlayCallback.completeListener();
            mMediaPlayer.release();
        }
    }

    /**
     * 恢复到暂停的一帧
     * @param position 当前帧的位置
     */
    private void resumeCurPosition(final int position){

        if (initMediaPlayer() != 0) {
            return;
        }
        try {
            handler.sendEmptyMessage(0);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.seekTo(position);
            pause();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化MediaPlayer
     * @return true 初始化成功  false初始化失败
     */
    private int initMediaPlayer () {
        AudioFunctions.muteAudioFocus(true);//暂停系统音乐
        File file = new File(getVideoPath());
        if (!file.exists()) {
            return 1;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放的视频源
        try {
            mMediaPlayer.setDataSource(getVideoPath());
        } catch (IOException e) {
            LogUtil.getUtils().e("InitMediaPlayer filed but path of video is not exist");
        }
        // 设置显示视频的SurfaceHolder
        mMediaPlayer.setDisplay(mSurfaceHolder);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        return 0;
    }

    /**
     * SurfaceView创建，存在俩种情况
     * 1. 当在播放过程中，SurfaceView被销毁后，再次恢复后，恢复到销毁时的画面
     * 2. 创建成功之后，回调给上层，方便上层实现自身业务
     * @param holder SurfaceHolder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCurrentTime != 0) {
            resumeCurPosition(mCurrentTime);
        } else {
            mPlayCallback.surfacePrepareCallback();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * 如果播放过程中SurfaceView被销毁，保存当前帧画面，等待下次恢复时显示
     * @param holder SurfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //在surfaceDestroyed时保存当前这一帧
        Bitmap curPosBitmap = saveCurrentFrame();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mPlayCallback != null && curPosBitmap != null) {
            mPlayCallback.surfaceDestroyListener(curPosBitmap);
        }
        //jyg add 2017/3/16 start
        //在surfaceView被销毁时，清除回调进度使用的handler
        handler.removeMessages(0);
        //jyg add 2017/3/16 end
    }

    /**
     * 保存短视频当前帧缩略图
     */
    private Bitmap saveCurrentFrame() {
        if (mCurrentTime == 0) {
            return null;
        }
        MediaMetadataRetriever media = null;
        Bitmap currentBitmap = null;
        try {
            media = new MediaMetadataRetriever();
            media.setDataSource(getVideoPath());
            currentBitmap = media.getFrameAtTime(mCurrentTime*1000);
        } catch (IllegalArgumentException e) {
            LogUtil.getUtils().e("When the path of video is not exist, " +
                    "it saves current frame failing");
        }
        if (media == null) {
            return null;
        }
        return currentBitmap;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mCurrentTime = 0;
        // 在播放完毕被回调
        mMediaPlayer.release();

        if (mPlayCallback != null) {
            mPlayCallback.completeListener();
        }
        //jyg add 2017/3/13 start
        //播放完成之后，清除回调进度使用的handler
        handler.removeMessages(0);
        //jyg add 2017/3/13 end
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.getUtils().e("Video play failed");
        return false;
    }

    @SuppressLint("HandlerLeak")
    private class VideoManagerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //在视频确认界面循环播放不需要回调进度
                    if (mMediaPlayer != null && mPlayCallback != null) {
                        try {
                            mCurrentTime = mMediaPlayer.getCurrentPosition();
                            mPlayCallback.progressListener(mCurrentTime);
                        } catch (Exception e) {
                            LogUtil.getUtils().d("MediaPlayer is release");
                            //jyg add 2017/3/13 start
                            return;
                            //jyg add 2017/3/13 end
                        }
                    }
                    sendEmptyMessageDelayed(0, 100);
                    break;
            }
        }
    }
    /**
     * 项目名称：短视频             <br>
     * 接口描述  ：短视频播发回调接口     <br>
     * 创建时间：2017/1/28        <br>
     * 修改记录：                 <br>
     *
     * @author jyg@xdja.com   <br>
     */
    public interface IVideoPlayCallback {

        /**
         * 视频播放完成回调
         */
        void prepareListener();

        /**
         * 播放视频进度回调
         *
         * @param pro 当前进度
         */
        void progressListener(int pro);

        /**
         * 视频播放完成回调
         */
        void completeListener();

        /**
         * 视频播放暂停回调
         */
        void pauseListener(int currentPos);

        /**
         * 视频播放恢复回调
         */
        void resumeListener(int currentPos);

        /**
         * surfaceView准备完毕
         */
        void surfacePrepareCallback();

        /**
         * 视频播放恢复回调
         */
        void surfaceDestroyListener(Bitmap currentPosBitmap);

    }
}
