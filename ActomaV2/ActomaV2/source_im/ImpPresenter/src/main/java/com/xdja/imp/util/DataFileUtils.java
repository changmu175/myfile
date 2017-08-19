package com.xdja.imp.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.xdja.imp.R;
import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.domain.model.ConstDef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据文件操作类
 * Created by leill on 2016/6/22.
 */
public class DataFileUtils {

    /**
     * 获取图片文件保存路径
     * @return /sdcard/Actoma+/XdjaIm/ActomaImage/
     */
    public static String getImageSavePath(){
        String path = SimcUiConfig.cacheFileDir + SimcUiConfig.savePicFileDir;
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        }
        return path;
    }

    /**
     * 获取语音文件保存路径
     * @return /sdcard/Actoma+/XdjaIm/.MD5(account)/Voice/random1/random2/
     */
    public static String getVoiceSavePath() {
        return ToolUtil.getVoicePath();
    }

    /**
     * 获取短视频文件保存路径
     * @return /sdcard/Actoma+/XdjaIm/.MD5(account)/Video/random1/random2/
     */
    public static String getVideoSavePath() {
        return ToolUtil.getVideoPath();
    }

    /**
     * 获取短视频文件保存路径
     * @return /sdcard/Actoma+/XdjaIm/ActomaVideo/
     */
    public static String getVideoSaveToPhonePath() {
        String path = SimcUiConfig.cacheFileDir + SimcUiConfig.saveVideoFileDir;
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return path;
            }
        }
        return path;
    }


    /**
     * 另存为文件到本地
     * @param type 文件类型，觉得Toast展示话语
     * @param oldPath 源文件地址
     * @param newPath 要另存为的地址
     * @param activity 当前界面的activity
     * @return -1 失败，0 成功
     */
    public static int saveFileToSDCard(final int type, final String oldPath,
                                       final String newPath, final Activity activity) {

        File oldFile = new File(oldPath);

        if (oldFile.exists() || "".equals(oldPath)){
            if (oldFile.exists() && oldFile.length() > getSdcardSpace() && getSdcardSpace() > 0) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new XToast(activity).display(R.string.momery_not_enough);
                    }
                });
                return -1;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //保存文件
                        File file = new File(newPath);
                        boolean bSaveSuccess = false;
                        if (!file.exists() || "".equals(oldPath)) {
                            InputStream inputStream = null;
                            FileOutputStream fos = null;
                            try {
                                File oldFile = new File(oldPath);
                                if (!oldFile.exists()) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (type == ConstDef.TYPE_VIDEO) {
                                                new XToast(activity).display(R.string.save_video_failed);
                                            } else {

                                            }
                                        }
                                    });

                                }
                                if(!"".equals(oldPath)){
                                    inputStream = new FileInputStream(oldFile);
                                }else{
                                    Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources() , R.drawable.bg_shanxin_image);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                    inputStream = new ByteArrayInputStream(baos.toByteArray());
                                }
                                fos = new FileOutputStream(file);
                                byte[] buffer = new byte[8192];
                                int length;
                                while ((length = inputStream.read(buffer)) != -1) {
                                    fos.write(buffer, 0, length);
                                }
                                fos.flush();
                                bSaveSuccess = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                                bSaveSuccess = false;
                            } finally {
                                try {
                                    if (fos != null) {
                                        fos.close();
                                    }
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            bSaveSuccess = true;
                        }
                        if (bSaveSuccess) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri uri = Uri.fromFile(new File(newPath));
                                    intent.setData(uri);
                                    activity.sendBroadcast(intent);
                                    if (type == ConstDef.TYPE_VIDEO) {
                                        new XToast(activity).display(String.format(activity.getResources()
                                                        .getString(R.string.save_video_to_path),
                                                DataFileUtils.getVideoSaveToPhonePath()));
                                    } else if (type == ConstDef.TYPE_PHOTO) {
                                        new XToast(activity).display(String.format(activity.getResources()
                                                        .getString(R.string.save_picture_to_path),
                                                DataFileUtils.getImageSavePath()));
                                    }

                                }
                            });
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (type == ConstDef.TYPE_VIDEO) {
                                        new XToast(activity).display(R.string.save_video_failed);
                                    } else if (type == ConstDef.TYPE_PHOTO) {
                                        new XToast(activity).display(R.string.save_picture_failed);
                                    }
                                }
                            });
                        }
                    }
                }).start();
                return 0;
            }
        }
        return -1;
    }
    @SuppressWarnings("deprecation")
    private static long getSdcardSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        return statFs.getBlockSize() * statFs.getAvailableBlocks();
    }


}
