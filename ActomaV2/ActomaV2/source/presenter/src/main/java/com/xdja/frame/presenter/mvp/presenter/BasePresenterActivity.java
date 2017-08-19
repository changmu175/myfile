package com.xdja.frame.presenter.mvp.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xdja.dependence.uitls.ApkDetector;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.AndroidApplication;
import com.xdja.frame.data.cache.SharedPreferencesUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.R;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.annotation.StackInto;
import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.frame.widget.XDialog;
import com.xdja.safekeyservice.jarv2.SecuritySDKManager;
import com.xdja.safekeyservice.jarv2.bean.IVerifyPinResult;

import java.util.ArrayList;


/**
 * <p>Summary:通用Activity相关的Presenter</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.actoma.presenter.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/8</p>
 * <p>Time:10:30</p>
 */
public abstract class BasePresenterActivity<P extends Command, V extends ActivityVu> extends AppCompatActivity {

    private static final String TAG = "BasePresenterActivity";

    public static final int REQ_WIRTE_READ_CODE = 1;

    //[S]modify by xienana for bug 4629 @2016/10/09 [reviewed by wangchao]
    public  static final int POST_GlOBAL_NOTIFI_ID = 0x00011;
    //[E]modify by xienana for bug 4629 @2016/10/09 [reviewed by wangchao]

    public static final String HAI_XIN = "Hisense";

    public static String ACTION_ACTOMA_SET_LANGUAGE = "languageToFinish";

    //[S] add by ysp @2016.11.23
    //通过变量来判断安装和卸载的状态
    public boolean isChipUninstall;
    //[E] add by ysp @2016.11.23

    protected boolean mIsOnkeyDown = false;

    private V vu;

    public V getVu() {
        return vu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //通过注解定义该Activity是否入栈
            StackInto annotation = getClass().getAnnotation(StackInto.class);
            if (annotation == null || annotation.value()) {
                ActivityStack.getInstanse().pushActivity(this);
            }

            preBindView(savedInstanceState);
			/*[S]add by tangsha@20161011 for multi language*/
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_ACTOMA_SET_LANGUAGE);
            filter.addAction(Intent.ACTION_LOCALE_CHANGED);
            registerReceiver(configChangeReceiver,filter);
			/*[E]add by tangsha@20161011 for multi language*/
            if (getVuClass() != null) {
                //初始化View
                vu = getVuClass().newInstance();
                //设置view对业务的调用句柄
                vu.setCommand(getCommand());
                //设置和View关联的Activity
                vu.setActivity(this);
                vu.init(getLayoutInflater(), null);
                setContentView(vu.getView());
                vu.onCreated();
            }

            onBindView(savedInstanceState);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
	    /*[S]add by tangsha@20161011 for multi language*/
        unregisterReceiver(configChangeReceiver);
		/*[E]add by tangsha@20161011 for multi language*/
        ActivityStack.getInstanse().popActivity(this, false);
        if (vu != null) vu.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vu != null) vu.onPause();
    }

    protected boolean isMoveTaskToBack(){
        return false;
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-12-21 add. fix bug 5645 . review by wangchao1. Start
    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        Log.v(TAG, "BasePresenterActivity -> onKeyShortcut keyCode : " + keyCode + " , mIsOnkeyDown : " +
                mIsOnkeyDown + " , isDestroyed = " + isDestroyed() + " , isFinishing : " + isFinishing() + " , " +
                getClass().getName());
        if (!mIsOnkeyDown && keyCode == KeyEvent.KEYCODE_BACK && !isDestroyed() && !isFinishing()) {
            if (isMoveTaskToBack()) {
                moveTaskToBack(true);
            } else {
                finish();
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG , "BasePresenterActivity -> onKeyDown keyCode : " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mIsOnkeyDown = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected  boolean isNeedCheckPermission() {
        return true;
    }

    /**
     * 判断是否是6.0系统
     * @return
     */
    public static boolean isMNC(){
        return Build.VERSION.SDK_INT >= 23;
    }

    //[s]modify by xienana for bug 6039 @20161118 review by tangsha
    public void initSafePin(){
        String pinCode = TFCardManager.getPin();
        boolean pinEmpty = TextUtils.isEmpty(pinCode);
        if(pinEmpty){
            SecuritySDKManager.getInstance().startVerifyPinActivity(this, new IVerifyPinResult() {
                @Override
                public void onResult(int i, String s) {
                    if(i != 0){
                        LogUtil.getUtils().e(TAG+"initSafePin exitApp---------");
                        ActivityStack.getInstanse().exitApp();
                    }
                }
            });
        }
    }
    //[e]modify by xienana for bug 6039 @20161118 review by tangsha

    @Override
    protected void onResume() {
        super.onResume();
        mIsOnkeyDown = false;
        //[S]modify by xienana for clear force logout notification @2016/10/09 [reviewed by anlihuang]
        ((NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(POST_GlOBAL_NOTIFI_ID);
        //[E]modify by xienana for clear force logout notification @2016/10/09 [reviewed by anlihuang]
        if (vu != null) vu.onResume();
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2073 . review by guobinchang. Start
        if (isMNC() && isNeedCheckPermission()) {
            ArrayList<String> permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
                    .permission.READ_EXTERNAL_STORAGE);
            if (permission != null && permission.size() > 0) {
                ActivityCompat.requestPermissions(this, permission.toArray(new String[]{}), REQ_WIRTE_READ_CODE);
                return;
            }
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2073 . review by guobinchang. Start
    }

    public ArrayList<String> checkSelfPermission(String... permiss) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permiss.length; i++) {
            if (!TextUtils.isEmpty(permiss[i]) && ContextCompat.checkSelfPermission(this, permiss[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permiss[i]);
            }
        }
        return permissionList;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (isMNC()) {
            if (requestCode == REQ_WIRTE_READ_CODE) {
                if (grantResults == null || grantResults.length == 0 || grantResults[0] != PackageManager
                        .PERMISSION_GRANTED) {
                    ActivityStack.getInstanse().exitApp();
                }
            }
        }
    }

    //wangchao for 3723
    @Override
    public void onBackPressed() {
        Log.v(TAG , "BasePresenterActivity -> onBackPressed");
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //end

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
		//[S]add by tangsha@20170112 for watch TF state
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addDataScheme("file");
		//[E]add by tangsha@20170112 for watch TF state
        registerReceiver(chipReceiver,intentFilter);
        if (vu != null) vu.onStart();
        boolean isChipInstall = ApkDetector.judgeIsInstall(this);
        isChipUninstall = !isChipInstall;
        detectUninstallSafekey();
        detectSafeKey();
        //[s]modify by xienana for bug 6039 @20161125 review by tangsha
        if(isChipInstall && detectTFdialog.isShowing() == false){
            initSafePin();
        }
        //[e]modify by xienana for bug 6039 @20161125 review by tangsha
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (vu != null) vu.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vu != null) vu.onStop();
        unregisterReceiver(chipReceiver);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (vu != null) vu.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (vu != null) vu.onDetachedFromWindow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (vu != null) vu.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (vu != null) vu.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (vu != null) vu.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @NonNull
    protected abstract Class<? extends V> getVuClass();

    @NonNull
    protected abstract P getCommand();

    protected void preBindView(Bundle savedInstanceState) {

    }

    protected void onBindView(Bundle savedInstanceState) {

    }
    //Start:add by wal2xdja.com for 3920
    @Override
    public Resources getResources() {
        Resources res=super.getResources();
        Configuration config = res.getConfiguration();//modify by xnn @20170303 review by tangsha
        config.fontScale=1.0f;
        res.updateConfiguration(config,res.getDisplayMetrics());
        return res;
    }
    //end:add by wal2xdja.com for 3920
    /*[S]add by tangsha@20161011 for multi language*/
    BroadcastReceiver configChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
              LogUtil.getUtils().d("BasePresenterActivity onReceive configChangeReceiver");
              finish();
        }
    };
	/*[E]add by tangsha@20161011 for multi language*/

    //[S]add by ysp @2016.11.23 for uninstall chip


    final XDialog dialog = new XDialog(this);
    private void showHandleLoginDialog() {
        dialog.setTitle(getString(R.string.update_dialog_title_tip));
        dialog.setMessage(getResources().getString(R.string.download_chip_tip_login_state));
        dialog.setPositiveButton(getString(R.string.download_chip_now), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadSafekey();
                ActivityStack.getInstanse().exitApp();
            }
        });
        dialog.setNegativeButton(getString(R.string.download_chip_later), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出应用
                ActivityStack.getInstanse().exitApp();
            }
        });
        dialog.setCancelable(false);
        if(!dialog.isShowing()) {
            dialog.show();
        }
    }

    //跳转到下载安全芯片页面
    public void downloadSafekey() {
        String url = ((AndroidApplication) getApplicationContext())
                .getApplicationComponent()
                .defaultConfigCache()
                .get()
                .get("downloadCkmsUrl");
        if(!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.url_invalid), Toast.LENGTH_SHORT).show();
        }
    }

    //检测是否有安装安全芯片
    public void detectUninstallSafekey() {
        if(isChipUninstall) {
            showHandleLoginDialog();
        } else {
            if(dialog!= null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private BroadcastReceiver chipReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chipName = "com.xdja.safekeyservice";
            boolean isRemove = intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED);
            if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || isRemove){
                String packageName = intent.getData().getSchemeSpecificPart();
                if(packageName.equals(chipName)){
                    isChipUninstall = isRemove;
                    detectUninstallSafekey();
                }
            }else if(intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){
                detectSafeKey();
            }
        }
    };
    //[E]add by ysp @2016.11.23 for uninstall chip
	
    //[S]add by tangsha@20170112 for watch TF state
	public static final String ACTION_APPLICATION_EXIT = "com.xdja.application.exit";
	public static final String EXIT_TF_OUT_KEY = "exitForSafeTFOut";
    final XDialog detectTFdialog = new XDialog(this);
    private void showTFDialog() {
        if(!detectTFdialog.isShowing()) {
            detectTFdialog.setTitle(getString(R.string.update_dialog_title_tip));
            detectTFdialog.setMessage(getResources().getString(R.string.no_chip));
            detectTFdialog.setNegativeButton(getString(R.string.i_know), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.getUtils().d(TAG+" showTFDialog  NegativeButton OnClickListener----");
                    SharedPreferencesUtil.setTicket(BasePresenterActivity.this, "");
                    Intent exitIntent = new Intent(ACTION_APPLICATION_EXIT);
                    exitIntent.putExtra(EXIT_TF_OUT_KEY, true);
                    sendBroadcast(exitIntent);
                    detectTFdialog.dismiss();
                }
            });
            detectTFdialog.setCancelable(false);
            detectTFdialog.show();
        }
    }


    public void detectSafeKey() {
        boolean hasCard = TFCardManager.detectSafeCard();
        //String cardId = TFCardManager.getDeviceId();
        LogUtil.getUtils().d(TAG+"detectSafeKey hasCard "+hasCard);
        if(hasCard == false) {
            showTFDialog();
        }
        /* //if TFCard removed, only can exit application
        else (cardId == preCardId){
            if(detectTFdialog!= null && detectTFdialog.isShowing()) {
                detectTFdialog.dismiss();
            }
        }*/
    }
	//[E]add by tangsha@20170112 for watch TF state
}
