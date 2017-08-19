package com.xdja.comm.zxing.scan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.xdja.comm.R;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.QRHandleEvent;
import com.xdja.comm.uitl.QRUtil;
import com.xdja.comm.uitl.TextUtil;
import com.xdja.comm.zxing.scan.camera.CameraManager;
import com.xdja.comm.zxing.scan.decoding.CaptureActivityHandler;
import com.xdja.comm.zxing.scan.decoding.InactivityTimer;
import com.xdja.comm.zxing.scan.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

@SuppressWarnings("deprecation")
public class CaptureActivity extends ActionBarActivity implements Callback ,CameraManager.CameraManagerListener {
    public static final String QR_RESULT = "RESULT";
    private static final int REQ_CAMERA_CODE = 4;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    // private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private CameraManager cameraManager;

    private Toolbar toolbar;
    private SurfaceHolder surfaceHolder;
    private CustomDialog mDialog;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capture_common);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);

        //设置toolbar显示
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.scan));

        mDialog = new CustomDialog(this);
        mDialog.setCancelable(false);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        toolbar.setNavigationIcon(R.drawable.af_abs_ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //initialize();
    }

    private void initialize(){
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        cameraManager = new CameraManager(getApplication());
        cameraManager.setCameraManagerListener(this);
        viewfinderView.setCameraManager(cameraManager);
        surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            //alh@xdja.com<mailto://alh@xdja.com> 2016-08-05 add. fix bug 2475 . review by wangchao1. Start
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager
                        .PERMISSION_GRANTED) {
                    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2073 . review by guobinchang. Start
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQ_CAMERA_CODE);
                    return;
                    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-05 add. fix bug 2475 . review by wangchao1. End
                }
            }
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-07 add. fix bug 2736 and 3699 . review by wangchao1. Start
        if (cameraManager == null || cameraManager.getCamera() == null) {
            if (isPermissionsResult) return;
            initialize();
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-07 add. fix bug 2736 and 3699 . review by wangchao1. End
    }

    private void showPermissionDialog() {
        if (mDialog == null) {
            mDialog = new CustomDialog(this);
        }
        if (!mDialog.isShowing()) {
            mDialog.setTitle(getString(R.string.camera_prompt)).setMessage(TextUtil.getActomaText(this, TextUtil
                    .ActomaImage.IMAGE_LIST, 0, 0, 0, getString(R.string.camera_error))).setNegativeButton(getString
                    (R.string.security_password_layout_confim), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                }
            }).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void showRuntimeExceptionDialog() {
        showPermissionDialog();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) return;
        try {
            // CameraManager.get().openDriver(surfaceHolder);
            //cameraManager.closeDriver();
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException | RuntimeException ioe) {// modified by ycm for lint 2017/02/13
            showPermissionDialog();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        if (!hasSurface) {
            hasSurface = true;
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager
                        .PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                            REQ_CAMERA_CODE);
                    return;
                }
            }
            initCamera(holder);
        }

    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 3014 . review by wangchao1. Start
    private boolean isPermissionsResult = false;
    @SuppressWarnings("ConstantConditions")
    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == REQ_CAMERA_CODE){
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                initCamera(surfaceHolder);
            }else{
                if (!isPermissionsResult) {
                    showPermissionDialog();
                    isPermissionsResult = true;
                }
            }
        }
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 3014 . review by wangchao1. End

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        showResult(obj, barcode);
    }

    private void showResult(final Result rawResult, Bitmap barcode) {

//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//		Drawable drawable = new BitmapDrawable(barcode);
//		builder.setIcon(drawable);
//
//		builder.setTitle("类型:" + rawResult.getBarcodeFormat() + "\n 结果：" + rawResult.getText());
//		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				Intent intent = new Intent();
//				intent.putExtra("result", rawResult.getText());
//				setResult(RESULT_OK, intent);
//				finish();
//			}
//		});
//		builder.setNegativeButton("重新扫描", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				cameraManager.startPreview();
//				restartPreviewAfterDelay(0L);
//			}
//		});
//		builder.setCancelable(false);
//		builder.show();
//		cameraManager.stopPreview();
//		XToast.show(this, rawResult.getText());


//		cameraManager.closeDriver();
//
//		restartPreviewAfterDelay(2000L);

//		LogUtil.getUtils().i("掃描結果====" + rawResult.getText());
//		 Intent intent = new Intent();
//		 intent.putExtra(QR_RESULT, rawResult.getText());
//		 setResult(RESULT_OK, intent);
//		 finish();


        String qrMessage = rawResult.getText();
        if (TextUtils.isEmpty(qrMessage)) return;

//        qrMessage = "{'cardNo':'78646a6178646a61365231303219447d'," +
//                "'businessIdentity’:'USBKeyUnlock'," +
//                "'cardSn':'621000000014'}";

        //测试代码
        //判断是否是扫一扫添加好友或者扫描解锁需要的正确内容
        //2016-5-30 ldy 不再在扫描界面判断二维码是不是账号
        if (qrMessage.contains("businessIdentity")) {//假设是解锁所需内容

//                QRType businessType = QRUtil.getQRBusinessIdentity(qrMessage);
//                switch (businessType) {
//                    case Error:
//                        XToast.show(this, "二维码扫描失败，请重试");
//                        break;
//                    case USBKeyUnlock:
            QRHandleEvent event = new QRHandleEvent();
            event.setMessage(qrMessage);
            event.setActivity(this);
            event.setContext(this);
            BusProvider.getMainProvider().post(event);
//                        break;
//                    default:
//                        break;
//                }
        } else if (qrMessage.startsWith(QRUtil.ACCOUNT_TITLE)//扫一扫添加好友
                ||qrMessage.startsWith(QRUtil.AUTHORIZE_ID_TITLE)) {
            showResultBack(rawResult.getText());
        } else {
            XToast.show(this, getResources().getString(R.string.invalid_QRCode));
            restartPreviewAfterDelay(3000);
        }

    }


    /**
     * 传递扫描结果回
     *
     * @param message
     */
    public void showResultBack(String message) {
        Intent intent = new Intent();
        intent.putExtra(QR_RESULT, message);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(MessageIDs.restart_preview, delayMS);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            try {
                AssetFileDescriptor fileDescriptor = getAssets().openFd("qrbeep.ogg");
                this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException e) {
                this.mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}