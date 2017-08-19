package com.xdja.presenter_mainframe.autoupdate;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.comm.uitl.DeviceUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;
import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * 升级后续业务操作类
 * 1、检查是否需要退出登录
 * 2、在V1升级V2时删除V1版遗留的冗余数据及文件
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-26 15:12
 */
public class UpdateManager {

    private String TAG = "UpdateManager";

    @Inject
    public UpdateManager() {
    }

    /**
     * 检查是否需要退出登录
     *
     * @param ctx 上下文句柄
     * @return Observable<Boolean> true:需要退出登录； false:不需要退出登录。
     */
    public Observable<Boolean> checkWhetherNeedLogout(@NonNull final Context ctx) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                String prevVersion = SharePreferceUtil.getPreferceUtil(ctx).getPreviousVersion();
                String currVersion = DeviceUtil.getClientVersion(ctx);
                LogUtil.getUtils().i(TAG + " >>> previousVersion=" + prevVersion + " - currentVersion=" + currVersion);
                if (!TextUtils.isEmpty(prevVersion) && !TextUtils.isEmpty(currVersion) && !currVersion.equals(prevVersion)) {
                    subscriber.onNext(Boolean.TRUE);
                } else {
                    subscriber.onNext(Boolean.FALSE);
                }
                subscriber.onCompleted();
            }
        });
    }

    public void saveCurrentVersion(@NonNull final Context ctx){
        String currVersion = DeviceUtil.getClientVersion(ctx);
        SharePreferceUtil.getPreferceUtil(ctx).setPreviousVersion(currVersion);
    }

    /**
     * 删除actoma+V1版遗留的冗余数据及文件
     *
     * @param ctx 上下文句柄
     */
    public void cleanActomaV1Data(@NonNull final Context ctx) {
        // 删除actoma+V1版遗留的冗余数据及文件的逻辑判断
        try {
            boolean isClean = SharePreferceUtil.getPreferceUtil(ctx).getIsCleanV1Data();
            LogUtil.getUtils().i(TAG + " >>> isCleanV1Data=" + isClean);
            if (!isClean) {
                LogUtil.getUtils().i(TAG + " >>> 开始清理数据...");
                long start = System.currentTimeMillis();
                SharePreferceUtil.getPreferceUtil(ctx).clearPreference(); //清空sharedprefence 内容. add by mengbo
                exeChmod(ctx);
                cleanSharedPreference(ctx);
                cleanInternalCache(ctx);
                cleanExternalCache(ctx);
                cleanAppWebview(ctx);
                cleanDatabases(ctx);
                cleanFiles(ctx);
                long end = System.currentTimeMillis();
                LogUtil.getUtils().i(TAG + " >>> 完成清理数据...");
                LogUtil.getUtils().i(TAG + " >>> 清理数据共耗时：" + (end - start) + " 毫秒");
                SharePreferceUtil.getPreferceUtil(ctx).setIsCleanV1Data(true);
            }
            countFiles(ctx.getFilesDir().getParent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改文件权限
     *
     * @param context 上下文句柄
     */
    @SuppressWarnings("CallToRuntimeExecWithNonConstantString")
    private void exeChmod(Context context) {
        String root = context.getFilesDir().getParent();
        try {
            Runtime.getRuntime().exec("chmod -R 755 " + root);
            LogUtil.getUtils().i(TAG + " >>> 文件权限修改成功!");
        } catch (Exception e) {
            LogUtil.getUtils().i(TAG + " >>> 文件权限修改失败!");
            e.printStackTrace();
        }
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xdja.actoma/cache)
     *
     * @param context 上下文句柄
     */
    private void cleanInternalCache(Context context) throws Exception {
        deleteFile(context.getCacheDir());
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xdja.actoma/cache)
     *
     * @param context 上下文句柄
     */
    private void cleanExternalCache(Context context) throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFile(context.getExternalCacheDir());
        }
    }

    /**
     * 清除本应用webview缓存(/data/data/com.xdja.actoma/app_webview)
     *
     * @param context 上下文句柄
     */
    private void cleanAppWebview(Context context) throws Exception {
        deleteFile(new File(context.getFilesDir().getParent() + "/app_webview"));
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xdja.actoma/databases)
     *
     * @param context 上下文句柄
     */
    private void cleanDatabases(Context context) throws Exception {
        deleteFile(new File(context.getFilesDir().getParent() + "/databases"));
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xdja.actoma/shared_prefs)
     *
     * @param context 上下文句柄
     */
    private void cleanSharedPreference(Context context) throws Exception {
        deleteFile(new File(context.getFilesDir().getParent() + "/shared_prefs"));
    }

    /**
     * 清除本应用XdjaIm(/data/data/com.xdja.actoma/XdjaIm)
     * 【该方法用于V2版imsdk.db的删除】
     *
     * @param context 上下文句柄
     */
    private void cleanXdjaIm(Context context) throws Exception {
        deleteFile(new File(context.getFilesDir().getParent() + "/XdjaIm"));
    }

    /**
     * 清除/data/data/com.xdja.actoma/files下的内容
     *
     * @param context 上下文句柄
     */
    private void cleanFiles(Context context) throws Exception {
        deleteFile(context.getFilesDir());
    }

    /**
     * 删除文件
     *
     * @param file 要删除的文件
     */
    private void deleteFile(File file) throws Exception {
        if (file.exists()) {
            if (file.isFile()) {
                LogUtil.getUtils().i(TAG + " >>> 删除文件：" + file.getAbsolutePath());
                boolean isDel = file.delete();
                LogUtil.getUtils().i(TAG + (isDel ? " >>> 文件删除成功!" : " >>> 文件删除失败!"));
            } else if (file.isDirectory()) {
                for (File item : file.listFiles()) {
                    deleteFile(item);
                }
            }
        }
    }

    private void countFiles(String path) throws Exception {
        File dir = new File(path);
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                countFiles(f.getAbsolutePath());
            } else {
                LogUtil.getUtils().i(TAG + " >>> 剩余文件：" + f.getAbsolutePath());
            }
        }
    }

}
