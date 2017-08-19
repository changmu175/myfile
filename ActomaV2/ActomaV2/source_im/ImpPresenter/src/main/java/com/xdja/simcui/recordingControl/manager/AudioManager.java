package com.xdja.simcui.recordingControl.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.PermissionUtil;
import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.presenter.activity.ChatDetailActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class AudioManager {
	private static final int VERSION = Build.VERSION.SDK_INT;
	private MediaRecorder mMediaRecorder;
	private String mDir;
	private String mCurrentFilePath;
	private static final int REC_LENGTH = 60000;

	private boolean isPrepare;
	private Context mContext;
	private AppOpsManager mAppOpsManager;
	private Method mCheckOpMethod;
	private Handler mHandler = new Handler();
	//add by ycm 2016/09/30
	public int EXCEPTION = 1;//录音权限被禁异常
	public int ILLEGALSTATE_EXCEPTION = 2;//录音被占用时非法异常

	//add by zya@xdja.com,use permission sdk>=23,20160802
	private ChatDetailActivity mActivity;
	@SuppressLint("InlinedApi")
	public AudioManager(Context context, String dir) {
		mDir = dir;
		mContext = context;
		if(VERSION >= Build.VERSION_CODES.KITKAT){
			mAppOpsManager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
			mCheckOpMethod = obtainOpsMethod(mAppOpsManager);
		}
	}

	/**
	 * 使用接口 用于回调
	 */
	public interface AudioStateListener {
        void wellPrepared();
        void checkCurrentFocusView();
		//start: fix bug 4364 by ycm 2016/09/30
		void prepareFaild(int exceptionType);
	}

	public AudioStateListener mAudioStateListener;
	//add by zya@xdja.com,use permission sdk>=23,20160802
	public void setActivity(ChatDetailActivity activity){
		mActivity = activity;
	}

	/**
	 * 回调方法
	 */
	public void setOnAudioStateListener(AudioStateListener listener) {
		mAudioStateListener = listener;
	}

	//add by zya@xdja.com ,获取方法checkOp
	private Method obtainOpsMethod(AppOpsManager opm){
		try{
			Class<?> appOpsClass = opm.getClass();
			return appOpsClass.getDeclaredMethod("checkOp",new Class[]{int.class,int.class,String.class});
		}catch (Exception e){
			LogUtil.getUtils().i("zhu->AudioManager.obtainOpsMethod is null :" + e.getMessage());
			return null;
		}
	}

	/**
	 * 版本大于19,进行权限的判断。
	 * add by zya@xdja.com ,执行checkOp获得返回值，并进行判断。
	 * @return
     */
	private int checkMediaRecorderPermission(){
		if(VERSION <= 19) {
			return -1;
		}

		try{
			//OP_RECORD_AUDIO->27
			Method method = mCheckOpMethod;
			int result = -1;
			if(method != null){
				//27-->OP_RECORD_AUDIO,AppOpsManager中隐藏的常量。
				result = (int)method.invoke(mAppOpsManager,
						new Object[]{27, Binder.getCallingUid(),ActomaController.getApp().getPackageName()});
				if(result == AppOpsManager.MODE_IGNORED){
					showDialog(R.string.none_audio_permission,R.string.none_audio_permission_hint);
				}
			}
			return result;
		}catch(Exception e) {
			LogUtil.getUtils().i("zhu->checkMediaRecorderPermission error:" + e.getMessage());
			return -1;
		}
	}

	//fix bug 3014 by licong, reView zya,2016/08/18
	private void checkMediaRecorderPermissionForHighSDK(){
		//add by zya@xdja.com,mate8 permission relative SDK>=23
		/*if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
			if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity,Manifest.permission.RECORD_AUDIO)){
				showDialog(R.string.none_audio_permission,R.string.none_audio_permission_hint);
			} else {
				ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.RECORD_AUDIO},2);
			}
		}
		//end*/
		mActivity.runOnUiThread(
				new Runnable() {
					@Override
					public void run() {
						PermissionUtil.requestPermissions(mActivity,2,new String[]{Manifest.permission.RECORD_AUDIO});
					}
				}
		);
	}

	//modify by zya@xdja.com,show dialog
	private void showDialog(final int title,final int message){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				//弹出提示框
				final CustomDialog customDialog = new CustomDialog(mContext);
				customDialog.setTitle(mContext.getString(title))//R.string.none_audio_permission))
						.setMessage(mContext.getString(message))//R.string.none_audio_permission_hint))
						.setNegativeButton(
								mContext.getString(R.string.confirm), new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										customDialog.dismiss();
									}
								}
						)
						.show();
			}
		});
	}

	// 去准备
	public synchronized void prepareAudio() {
		try {
			//add by zya@xdja.com,fix bug 2233,2268,SDK>=23,view by gbc,gr
			if(VERSION >= 23){
				checkMediaRecorderPermissionForHighSDK();
			} else {
				//add by zya@xdja.com, view by gbc,权限判断，为拒绝的时候，终止录音,bug NACTOMA-446
				if(AppOpsManager.MODE_IGNORED == checkMediaRecorderPermission()) {
					return;
				}
			}
			isPrepare = false;
			File dir = new File(mDir);
			if (!dir.exists() && !dir.mkdirs()) {
				LogUtil.getUtils().i("AudioManager:mkdirs error!");
				return ;
			}

			String fileName = generateFileName();
			File file = new File(dir, fileName);

			mCurrentFilePath =file.getAbsolutePath();

			release();

			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			// 设置MediaRecorder的音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			// 设置音频编码
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			//设置最大录音时长
			mMediaRecorder.setMaxDuration(REC_LENGTH);

            long beginTime = System.currentTimeMillis();


            // 准备录音
            mMediaRecorder.prepare();

            mMediaRecorder.start();

			//add by zya@xdja.com, view by gbc ,权限判断，为拒绝的时候，终止录音,bug NACTOMA-446
			int result = checkMediaRecorderPermission();
//			//4-->MODE_ASK表示总是询问。
			if(result == AppOpsManager.MODE_IGNORED  || result == 4) {
				release();
				return ;
			}//end

            long endTime = System.currentTimeMillis();

			if ((endTime - beginTime) > 500) {
				mAudioStateListener.checkCurrentFocusView();
                if (mMediaRecorder!=null) {
					mMediaRecorder.stop();
                    mMediaRecorder.reset();
                }

            }else {
                if (mAudioStateListener != null) {
                    mAudioStateListener.wellPrepared();
                }
                isPrepare = true;
            }

		} catch (IllegalStateException e) {
			e.printStackTrace();
			prepareFaild(ILLEGALSTATE_EXCEPTION);
		} catch (IOException e) {
			e.printStackTrace();
			prepareFaild(EXCEPTION);
		} catch (Exception e) {
			e.printStackTrace();
			prepareFaild(EXCEPTION);
		} finally {
		}

	}
	//fix bug 4364 by ycm 2016/09/30
	private void prepareFaild(final int exceptionType) {
		isPrepare = false;
		release();
		//add by zya@xdja.com ,在主线程中回调,BYD-ANR
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mAudioStateListener != null) {
					mAudioStateListener.prepareFaild(exceptionType);
				}
			}
		});
	}

	/**
	 * 随机生成文件的名称
	 */
	private String generateFileName() {
		return UUID.randomUUID().toString();
	}

	//获得声音振幅大小
	public synchronized double getAmplitude() {
		try {
			if (isPrepare) {
				if (mMediaRecorder != null) {
                    return mMediaRecorder.getMaxAmplitude();
                }
			}
		} catch (Exception e) {
		}
		return 1;
	}

	/**
	 * 释放资源
	 */
	public synchronized void release() {
		try {
			if (mMediaRecorder != null) {
				mMediaRecorder.stop();
				mMediaRecorder.reset();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		} catch (Exception e) {

		} finally {
			if (mMediaRecorder != null) {
				mMediaRecorder = null;
			}
		}
	}

	/**
	 * 取消录音
	 */
	public synchronized void cancel() {
		release();
		if (mCurrentFilePath != null) {
			File file = new File(mCurrentFilePath);
			if (!file.delete()) {
				LogUtil.getUtils().i("AudioManager.cancel failed:file delete error!");
			}
			mCurrentFilePath = null;
		}

	}

	public String getCurrentFilePath() {

		return mCurrentFilePath;
	}
}
