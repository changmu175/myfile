package com.xdja.dependence.uitls;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Summary:APK检测工具</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.dependence.uitls</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/13</p>
 * <p>Time:14:08</p>
 */
public class ApkDetector {

    /**
     * 检测当前设备安装对应包名的程序的版本
     *
     * @param context 上下文句柄
     * @param pkgName 包名
     * @return 程序版本（-1:表示未安装该程序）
     */
    public static int getInstalledApkVersion(Context context, String pkgName) {
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
        return packageInfo.versionCode;
    }

    /**
     * 安装私有目录下的某个APK安装包
     *
     * @param context  上下文
     * @param fileName APK名称
     */
        /*[S]modify by xienana @20160721 for security chip driver detection to install apk (rummager : tangsha)*/
    public static boolean installApk(Context context, String fileName) {
        try {
            //chomd 644  context.getFilesDir()
            String path = context.getExternalFilesDir(null) + File.separator + fileName;
            Runtime.getRuntime().exec("chmod 644" + path);
            File f = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(path)),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
            return Boolean.TRUE;
        } catch (IOException e) {
            LogUtil.getUtils().e(e.getMessage());
            return Boolean.FALSE;
        }
    }/*[E]modify by xienana @20160721 for security chip driver detection  to install apk (rummager : tangsha)*/

    /**
     * 将Assets下的APK包复制到程序私有目录
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    public static void copyArchive(Context context, String fileName) {
        InputStream in = null;
        FileOutputStream out = null;
        try {/*[S]modify by xienana @20160721 for security chip driver detection to copy apk (rummager : tangsha) */
            File f = new File(context.getExternalFilesDir(null) + File.separator + fileName);
            if (f.exists()) {
                f.delete();
            }
            in = context.getResources().getAssets().open(fileName);
            out = new FileOutputStream(f);
            byte[] buffer = new byte[1024*8];
            int count = 0;
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }/*[E]modify by xienana @20160721 for security chip driver detection to copy apk (rummager : tangsha) */
        } catch (Exception ioe) {
            LogUtil.getUtils().e(ioe.getMessage());
            return;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtil.getUtils().e(e.getMessage());
            }
        }
    }

    /**
     * 判断是否安装了芯片管家
     *
     * @param context 上下文，不能为空
     * @return true:表示已安装，false：表示未安装
     */
    public static boolean judgeIsInstall(Context context) {
        String pkgName = "com.xdja.safekeyservice";
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 启动芯片管家的升级页面
     *
     * @param context 上下文，不能为空
     * @return 0：正常启动；其他：不能正常启动
     */
    public static int startUpdateActivity(Context context) {
        if (judgeIsInstall(context)) {
            Intent intent = new Intent();
            String pkgName = "com.xdja.safekeyservice";
            String className = "com.xdja.scservice.presenter.activity.UpdatePresener";
            intent.setComponent(new ComponentName(pkgName,className));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return 0;
        }
        return -1;
    }
}
