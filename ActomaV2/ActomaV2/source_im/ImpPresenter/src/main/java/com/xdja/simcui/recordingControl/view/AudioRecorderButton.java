package com.xdja.simcui.recordingControl.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.Toast;

import com.securevoipcommon.VoipFunction;
import com.xdja.comm.uitl.TelphoneState;
import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.presenter.activity.ChatDetailActivity;
import com.xdja.imp.util.AudioFunctions;
import com.xdja.imp.util.DataFileUtils;
import com.xdja.imp.util.IMMediaPlayer;
import com.xdja.simcui.recordingControl.manager.AudioManager;
import com.xdja.simcui.recordingControl.manager.DialogManager;

import java.lang.ref.WeakReference;

public class AudioRecorderButton extends Button {
    private static final int VERSION = Build.VERSION.SDK_INT;
    private static final int STATE_NORMAL = 1;// 默认的状态
    private static final int STATE_RECORDING = 2;// 正在录音
    private static final int STATE_WANT_TO_CANCEL = 3;// 希望取消
    private static final int LAST_TEN_SECOND_TO_CANCEL = 4;// 最后十秒取消
    private int mCurrentState = STATE_NORMAL; // 当前的状态

    private final DialogManager mDialogManager;//录音弹出框
    private final AudioManager mAudioManager;//录音业务操作类
    private boolean isRecording = false; // 已经开始录音
    private float mTime;

    private boolean mReady;// 是否触发longClick

    private static final int DISTANCE_Y_CANCEL = 50;//滑动Y轴最大高度
    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_DIALOG_DIMISS = 0x112;
    private boolean isLastTenSecord = false;//是否是最后十秒

    private final String dirPath;//录音保存位置

    private static String lastTenSecordstr;//最后十秒提示字符串


    private final Context mContext;

    private TimerHandler timeHandler = new TimerHandler(new WeakReference<>(this));
    private MyHandler mHandler = new MyHandler(new WeakReference<>(this));

    /**
     * 以下2个方法是构造方法
     */
    public AudioRecorderButton(final Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        mDialogManager = new DialogManager(context);

        //录音文件存储位置  初始化录音操作类使用
        dirPath = DataFileUtils.getVoiceSavePath();

        mAudioManager = new AudioManager(context,dirPath);
        //录音准备状态回调
        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {

            public void wellPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }

            @Override
            public void checkCurrentFocusView() {
                    changeState(STATE_NORMAL);
                    mDialogManager.tooShortToShow(getContext());
            }
            //start: fix bug 4364 by ycm 2016/09/30
            @Override
            public void prepareFaild(int exceptionType) {
                changeState(STATE_NORMAL);
                if (VERSION < 23) {
                    if (exceptionType == mAudioManager.EXCEPTION) {
                        mDialogManager.getAudioPermissionErrToShow(getContext());
                    } else if (exceptionType == mAudioManager.ILLEGALSTATE_EXCEPTION) {
                        mDialogManager.getAudioDeviceErrToShow(getContext());
                    }
                }
                //end: fix bug 4364 by ycm 2016/09/30
            }

        });

        // 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View v) {
                if(!TelphoneState.getPhotoStateIsIdle(mContext) || VoipFunction.getInstance().hasActiveCall()){
                    Toast.makeText(mContext, mContext.getString(R.string.Phone_is_inCall), Toast.LENGTH_SHORT).show();
                    return true;
                }
                new AudioAsyncTask().execute();
                return true;
            }
        });

        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.recording_control_str_recorder_voice_too_short), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //无参构造函数
    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    //add by zya@xdja.com,mate permission relative
    public void setChatDetailActivity(ChatDetailActivity activity){
        mAudioManager.setActivity(activity);
    }

    private static class TimerHandler extends Handler {
        private final WeakReference<AudioRecorderButton> mActivity;
        public  TimerHandler(WeakReference<AudioRecorderButton> mActivity) {
            this.mActivity = mActivity;
        }
        @Override
        public void handleMessage(Message msg) {
           if (mActivity.get() != null) {
               lastTenSecordstr = mActivity.get().getContext().getResources().getString(R.string.also_have) + msg.what + mActivity.get().getContext().getResources().getString(R.string.second);
               switch (msg.what) {
                   case 0:
                       MotionEvent motionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                               SystemClock.uptimeMillis(),
                               MotionEvent.ACTION_UP,
                               10,
                               10, 0);
                       mActivity.get().dispatchTouchEvent(motionEvent);
                       break;
                   default:
                       mActivity.get().mDialogManager.mLable.setText(lastTenSecordstr);
                       break;
               }
           }
        }
    }

    /*
     * 获取音量大小的线程
     */
    private final Runnable mGetVoiceLevelRunnable = new Runnable() {

        public void run() {
            long beginTime = System.currentTimeMillis();
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime = (System.currentTimeMillis() - beginTime) / 1000.0f;
//                    Log.i("time", "准备获取声音振幅");
                    // 修改录音音量图片
                    setImage(mAudioManager.getAmplitude());
                    if (isRecording) {
                        if (mTime >= 50) {
                            isLastTenSecord = true;
                            timeHandler.sendEmptyMessage(60 - (int) mTime);
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isLastTenSecord = false;
                    // 重置数据
                    reset();
                }
            }
        }
    };

    private static class MyHandler extends Handler {
        private final WeakReference<AudioRecorderButton> mActivity;
        public  MyHandler(WeakReference<AudioRecorderButton> mActivity) {
            this.mActivity = mActivity;
        }
        @Override
        public void handleMessage(Message msg) {
           if (mActivity.get() != null) {
               switch (msg.what) {
                   case MSG_AUDIO_PREPARED:
                       //获取音频焦点
                       AudioFunctions.muteAudioFocus(true);
                       mActivity.get().startToRecording();
                       break;

                   case MSG_DIALOG_DIMISS:
                       mActivity.get().mDialogManager.dimissDialog();
                       break;
                   //录音振幅设置图片资源
                   default:
                       mActivity.get().mDialogManager.updateVoiceLevel(msg.what);
                       break;

               }
               super.handleMessage(msg);
           }
        }
    }


    /**
     * 开始录音
     *
     * @作者 cxp
     * @since 2015年7月22日 下午9:00:42
     */
    private void startToRecording() {
        // 显示對話框在开始录音以后
        isRecording = true;


        mDialogManager.showRecordingDialog();
        mDialogManager.mVoiceChronometer.setBase(SystemClock
                .elapsedRealtime());
        mDialogManager.mVoiceChronometer.start();
        mDialogManager.mVoiceChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                // 时间改变后就会触发  1s回调一次
                // 时间达到60s就自动完成录音并断掉录音  button长按事件取消  在这里可以记录时间的变化值
            }
        });

        // 开启一个线程
        Thread voiceThread = new Thread(mGetVoiceLevelRunnable);
        voiceThread.start();

    }

    /**
     * 屏幕的触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();// 获得x轴坐标
        int y = (int) event.getY();// 获得y轴坐标

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 如果想要取消，根据x,y的坐标看是否需要取消
                    if (wantToCancle(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }

                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //释放音频焦点
                AudioFunctions.muteAudioFocus(false);


                if (!mReady) {
                    reset();
                    //fix bug 8256 by zya ,20170204
                    if(mDialogManager.isShowing()){
                        mDialogManager.dimissDialog();
                        mAudioManager.cancel();
                    }//end by zya
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.6f) {
                    // Toast 提示录音时间过短
                    mDialogManager.tooShort(getContext());
                    mAudioManager.cancel();
                    // 延迟显示对话框
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 10);
                } else if (mCurrentState == STATE_RECORDING || mTime >= 60) { //fix bug 3388 by licong, reView by zya, 2016/8/30
                    // 正在录音的时候，结束
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if (audioFinishRecorderListener != null) {
                        audioFinishRecorderListener.onFinish(mTime,
                                mAudioManager.getCurrentFilePath());
                    }

                } else if (mCurrentState == STATE_WANT_TO_CANCEL) { // 想要取消
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                //重置配置数据
                reset();
                break;
            default:
                LogUtil.getUtils().i("AudioRecorderButton:default");
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
        isLastTenSecord = false;

    }

    //根据x、y坐标计算想要取消的状态值
    private boolean wantToCancle(int x, int y) {
        if (x < 0 || x > getWidth()) { // 超过按钮的宽度
            return true;
        }
        // 超过按钮的高度
        return y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL;
    }

    /**
     * 改变
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setText(R.string.recording_control_str_recorder_normal);
                    break;

                case STATE_RECORDING:
                    setText(R.string.recording_control_str_recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;

                case STATE_WANT_TO_CANCEL:
                    setText(R.string.recording_control_str_recorder_want_cancel);

                    mDialogManager.wantToCancel();
                    break;
                case LAST_TEN_SECOND_TO_CANCEL:

                    mDialogManager.lastTenSeconds(lastTenSecordstr);
                    break;
                default:
                    break;
            }
        }
    }


    private void setImage(double voiceValue) {
        int voiceImageID;
        if (voiceValue < 600.0) {
            voiceImageID = 1;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
            voiceImageID = 1;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
            voiceImageID = 1;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            voiceImageID = 2;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            voiceImageID = 2;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            voiceImageID = 2;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            voiceImageID = 3;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            voiceImageID = 3;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            voiceImageID = 4;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
            voiceImageID = 5;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            voiceImageID = 6;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            voiceImageID = 6;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            voiceImageID = 6;
            mHandler.sendEmptyMessage(voiceImageID);
        } else if (voiceValue > 12000.0) {
            voiceImageID = 7;
            mHandler.sendEmptyMessage(voiceImageID);
        }
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener audioFinishRecorderListener;

    public void setAudioFinishRecorderListener(
            AudioFinishRecorderListener listener) {
        audioFinishRecorderListener = listener;
    }

    /**
     *OnLongClick对应的异步操作
     */
   private class AudioAsyncTask extends AsyncTask<Void,Void,Boolean>{


        @Override
        protected Boolean doInBackground(Void... params) {
            //停止语音消息播放
            IMMediaPlayer.stopPlay();

            //获取音频焦点
            AudioFunctions.muteAudioFocus(true);


            mReady = true;
            //开始录音前的准备操作

            mAudioManager.prepareAudio();
            return true;
        }
    }

}
