package com.xdja.presenter_mainframe.util;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.xdja.comm.data.AppLogLevel;
import com.xdja.comm.data.AppModule;
import com.xdja.comm.data.LogInfoBean;
import com.xdja.comm.uitl.NetUtil;
import com.xdja.comm.uitl.XdjaLogUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.WakeLockManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    private boolean isEnable = true;

    //用于格式化日期,作为日志文件名的一部分
    @SuppressWarnings("SimpleDateFormatWithoutLocale")
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        if (isEnable) {
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            //设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    private static final int THREAD_SLEEP = 3000;
    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(THREAD_SLEEP);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }

            //退出程序
            //todo gbc 走查退出流程
            android.os.Process.killProcess(android.os.Process.myPid());
           // Function.simpleLogout(mContext);
//            ActivityContoller.getInstanse().exit();
            ActivityStack.getInstanse().exitApp();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                //Toast.makeText(mContext, ActomaController.getApp().getString(R.string.app_error_and_will_exit), Toast.LENGTH_LONG).show();
                LogUtil.getUtils().e("Actoma Application is crash, log save in atfram->app_log");
                WakeLockManager.getInstance().forceRelease();
                Looper.loop();
            }
        }.start();

        //保存错误日志
        try {
//            ex.printStackTrace();
            saveLogToDb(mContext, ex);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void saveLogToDb(Context context, Throwable e) {
        if(e != null) {
            LogInfoBean logInfoBean = new LogInfoBean();
            logInfoBean.setAppModule(AppModule.UN_KNOWN);
            logInfoBean.setCrashTime(System.currentTimeMillis());
            logInfoBean.setLogCode(AppLogCode.CRASH);
            logInfoBean.setLevel(AppLogLevel.CRASH.getKey());
            logInfoBean.setNetType(NetUtil.getNetType(context));

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            String error = writer.toString();
            logInfoBean.setContent(error);

            //modify by thz 增加对异常问题的日志打印，并增加相关标签 2016-3-8
            Log.e("AtError", error);
            //end
            try {
                writer.close();
            } catch (Exception ex) {

            }
            printWriter.close();
            XdjaLogUtils.saveLog(context, logInfoBean, XdjaLogUtils.SYNC_UP_LOAD);
        }
    }
}