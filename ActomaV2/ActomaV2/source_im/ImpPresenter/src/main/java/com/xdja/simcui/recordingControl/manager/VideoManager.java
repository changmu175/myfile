package com.xdja.simcui.recordingControl.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.util.AudioFunctions;
import com.xdja.imp.util.CameraHelper;
import com.xdja.imp.util.DataFileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频录制管理类     <br>
 * 创建时间：2017/1/28        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

@SuppressWarnings("deprecation")
public class VideoManager implements SurfaceHolder.Callback {
    private final Activity activity;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private int previewWidth, previewHeight;
    private boolean isRecording;
    private GestureDetector mDetector;
    private boolean isZoomIn = false;
    private MediaPlayer mediaPlayer;
    private IVideoRecordCallback videoRecordCallback;
    private boolean isFacingBack = true;
    private boolean isConfirm;
    private File videoFile;
    private File firstFrameFile;

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public File getFirstFrameFile() {
        return firstFrameFile;
    }

    public void setVideoRecordCallback(IVideoRecordCallback videoRecordCallback) {
        this.videoRecordCallback = videoRecordCallback;
    }


    public VideoManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置录制展示SurfaceView
     *
     * @param view SurfaceView
     */
    public void setSurfaceView(SurfaceView view) {
        this.mSurfaceView = view;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mDetector = new GestureDetector(activity, new ZoomGestureListener());
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return false;
            }
        });
    }


    /**
     * 录制视频
     */
    public void record() {
        AudioFunctions.muteAudioFocus(true);//暂停系统音乐
        if (isRecording) {
            try {
                mMediaRecorder.stop();  // stop the recording
            } catch (RuntimeException e) {
                LogUtil.getUtils().d("RuntimeException: stop() is called immediately after start()");
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder
            isRecording = false;
        }
        startRecord();
    }

    /**
     * 录制视频前设置摄像机参数以及视频输出参数
     *
     * @return true表示成功，false表示失败
     */
    private boolean prepareRecord() {
        try {

            mMediaRecorder = new MediaRecorder();
            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);

            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//音频源
            if (isFacingBack) {

                mMediaRecorder.setOrientationHint(90);
            } else {
                mMediaRecorder.setOrientationHint(270);
            }

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//视频输出格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//音频格式
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//视频录制格式
            mMediaRecorder.setVideoSize(previewWidth, previewHeight);
            mMediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);

            File videoDri = new File(DataFileUtils.getVideoSavePath());
            videoFile = new File(videoDri, generateFileName());
            mMediaRecorder.setOutputFile(videoFile.getPath());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MediaRecorder", "Exception prepareRecord: ");
            releaseMediaRecorder();
            return false;
        }

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("MediaRecorder", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("MediaRecorder", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    /**
     * 停止录制视频并保存视频
     */
    public void stopRecordSave() {
        Log.d("Recorder", "stopRecordSave");
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
                //保存短视频第一帧
                saveFirstFrame(videoFile.getPath());

            } catch (RuntimeException r) {
                Log.d("Recorder", "RuntimeException: stop() is called immediately after start()");
            } finally {
                releaseMediaRecorder();
                releaseCamera();
            }
            //保存完毕后，直接循环播放
            confirmVideoPlay(videoFile.getAbsolutePath());
        }
    }

    /**
     * 停止录制视频不保存视频
     */
    public void stopRecordUnSave() {
        Log.d("Recorder", "stopRecordUnSave");
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
                if (videoFile.exists()) {
                    //不保存直接删掉
                    //noinspection ResultOfMethodCallIgnored
                    videoFile.delete();
                }
            } catch (RuntimeException r) {
                Log.d("Recorder", "RuntimeException: stop() is called immediately after start()");
            } finally {
                releaseMediaRecorder();
            }
        }
    }

    /**
     * 保存短视频第一帧缩略图
     *
     * @param path 短视频地址
     */
    private void saveFirstFrame(String path) {
        MediaMetadataRetriever media = null;

        try {
            media = new MediaMetadataRetriever();
            media.setDataSource(path);
        } catch (IllegalArgumentException e) {
            LogUtil.getUtils().e("Save first frame failed, when path of path is error");
        }
        if (media == null) {
            return;
        }
        Bitmap bitmap = media.getFrameAtTime(0);
        String fileName = generateFileName();
        BufferedOutputStream bos = null;
        firstFrameFile = new File(DataFileUtils.getVideoSavePath(), fileName);

        try {
            bos = new BufferedOutputStream(new FileOutputStream(firstFrameFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 删除短视频文件
     *
     * @return true删除成功 false删除失败
     */
    public boolean deleteTargetFile() {
        return videoFile != null && videoFile.delete();
    }

    /**
     * 启动摄像头展示预览画面
     *
     * @param holder       SurfaceHolder
     * @param isFacingBack true表示使用后置摄像头，false表示前置摄像头
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void startPreView(SurfaceHolder holder, boolean isFacingBack) {
        //记录当前摄像头方向
        this.isFacingBack = isFacingBack;
        int cameraId = 0;
        if (mCamera == null) {
            if (isFacingBack) {
                cameraId = CameraHelper.getDefaultBackFacingCameraInstance();
            } else {
                cameraId = CameraHelper.getDefaultFrontFacingCameraInstance();
            }
            mCamera = Camera.open(cameraId);
        }

        if (mCamera != null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = rotation * 90;
            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
            mCamera.setDisplayOrientation(result);

            try {
                mCamera.setPreviewDisplay(holder);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
                Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                        mSupportedPreviewSizes, mSurfaceView.getWidth(), mSurfaceView.getHeight());
                previewWidth = optimalSize.width;
                previewHeight = optimalSize.height;
                if (result == 90 || result == 270) {
                    //noinspection SuspiciousNameCombination,SuspiciousNameCombination
                    mSurfaceHolder.setFixedSize(previewHeight, previewWidth);

                } else {
                    mSurfaceHolder.setFixedSize(previewWidth, previewHeight);
                }
                CameraHelper.setCameraFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
                        mCamera);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 释放录像机
     */
    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            Log.d("Recorder", "release Recorder");
        }
    }

    /**
     * 释放摄像头
     */
    private void releaseCamera() {
        if (mCamera != null) {
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
            Log.d("Recorder", "release Camera");
        }
    }

    /**
     * 短视频确认界面播放
     *
     * @param path 待确认发送短视频路径
     */
    private void confirmVideoPlay(String path) {
        AudioFunctions.muteAudioFocus(true);//暂停系统音乐
        isConfirm = true;
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        if (videoRecordCallback != null) {
            videoRecordCallback.completeRecord();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            mediaPlayer.setDataSource(file.getAbsolutePath());
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(mSurfaceHolder);
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 恢复播放短视频
     */
    public void resume() {
        confirmVideoPlay(videoFile.getPath());
    }

    /**
     * 暂停播放短视频
     */
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }


    /**
     * 停止播放
     *
     * @param isRerecording true 正在录制 false 则否
     */
    public void stopPlay(boolean isRerecording) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        //是否重新录制
        if (isRerecording) {
            startPreView(mSurfaceHolder, isFacingBack);
        }
    }

    /**
     * 转化摄像头方向
     */
    public void transformCamera() {
        releaseCamera();
        startPreView(mSurfaceHolder, !isFacingBack);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.getUtils().d("surfaceCreated: ");
        mSurfaceHolder = holder;

        if (isConfirm) {
            confirmVideoPlay(videoFile.getAbsolutePath());
        } else {
            startPreView(holder, isFacingBack);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRecording = false;
        if (mCamera != null) {
            releaseCamera();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (mMediaRecorder != null) {
            releaseMediaRecorder();
        }
    }

    /**
     * 启动录像，进行短视频录制
     */
    private void startRecord() {
        if (prepareRecord()) {
            try {
                videoRecordCallback.prepareRecord();
                mMediaRecorder.start();
                isRecording = true;
                Log.d("Recorder", "Start Record");
            } catch (RuntimeException r) {
                releaseMediaRecorder();
                Log.e("Recorder", "RuntimeException: start() is called immediately after stop()");
            }
        }
    }


    /**
     * 随机生成文件的名称
     */
    private String generateFileName() {
        return UUID.randomUUID().toString();
    }


    /**
     * 短视频放大缩小
     *
     * @param zoomValue 缩放参数
     */
    private void setZoom(int zoomValue) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {
                int maxZoom = parameters.getMaxZoom();
                if (maxZoom == 0) {
                    return;
                }
                if (zoomValue > maxZoom) {
                    zoomValue = maxZoom;
                }
                parameters.setZoom(zoomValue);
                mCamera.setParameters(parameters);
            }
        }
    }


    /**
     * 项目名称：短视频             <br>
     * 类描述  ：短视频录制界面聚焦放大管理类   <br>
     * 创建时间：2017/1/28        <br>
     * 修改记录：                 <br>
     *
     * @author jyg@xdja.com   <br>
     */
    private class ZoomGestureListener extends GestureDetector.SimpleOnGestureListener {
        //双击手势事件
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            super.onDoubleTap(e);
            if (!isZoomIn) {
                setZoom(20);
                isZoomIn = true;
            } else {
                setZoom(0);
                isZoomIn = false;
            }
            return true;
        }
    }

    /**
     * 项目名称：短视频             <br>
     * 接口描述  ：短视频录制回调接口     <br>
     * 创建时间：2017/1/28        <br>
     * 修改记录：                 <br>
     *
     * @author jyg@xdja.com   <br>
     */
    public interface IVideoRecordCallback {
        /**
         * 视频准备录制回调
         */
        void prepareRecord();
        /**
         * 视频完成录制回调
         */
        void completeRecord();
    }
}
