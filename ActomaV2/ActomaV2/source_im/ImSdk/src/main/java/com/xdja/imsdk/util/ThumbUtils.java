package com.xdja.imsdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：                 <br>
 * 创建时间：2016/11/16 17:05  <br>
 * 修改记录：                 <br>
 *
 * @author liming@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class ThumbUtils {

    /**
     * 图片最小宽度
     */
    private static final int MIN_WIDTH = 320;
    /**
     * 图片最小高度
     */
    private static final int MIN_HEIGHT = 320;
    /**
     * 图片最大宽度
     */
    private static final int MAX_WIDTH = 360;
    /**
     * 图片最大高度
     */
    private static final int MAX_HEIGHT = 360;

    /**
     * 高清缩略图宽度
     */
    private static final int HD_THUMB_WIDTH = 720;

    /**
     *高清缩略图高度
     */
    private static final int HD_THUMB_HEIGHT = 1280;

    /**
     * 获取高清缩略图
     * @param originalPath
     * @param saveFile
     * @return
     */
    public static String getHDThumbFilePath(String originalPath, File saveFile){
        return saveBitmap(
                getHDThumbFilePath(originalPath),
                75,
                saveFile
        );
    }

    /**
     * 获取高清缩略图
     * -对于比较特殊的图片，直接使用原图即可，上层加载时进行处理
     * @param originalPath
     * @return
     */
    public static Bitmap getHDThumbFilePath(String originalPath){
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(originalPath, options);

        //fixed by leill 针对狭长图片失真处理，对图片增加压缩比例 2016/7/26
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int maxWidth = HD_THUMB_WIDTH;
        int maxHeight = HD_THUMB_HEIGHT;
        int minWidth = MAX_WIDTH;
        int minHeight = MAX_HEIGHT;

        if (outHeight > 0 && (outWidth / outHeight >= 4)){
            //maxWidth *= 4;
            //maxHeight = options.outHeight;
            return null;
        } else if (outHeight > 0 && (outWidth / outHeight > 2)){
            maxWidth *= 2;
            maxHeight = options.outHeight;
        }

        if (outWidth > 0 && (outHeight / outWidth >= 4)){
            //maxHeight *= 4;
            //maxWidth = options.outWidth;
            return null;
        } else if (outWidth > 0 && (outHeight / outWidth > 2)){
            maxHeight *= 2;
            maxWidth = options.outWidth;
        }
        //end by leill 狭长图片失真处理完成.

        //计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(
                options.outWidth, options.outHeight,
                minWidth, minHeight,
                maxWidth, maxHeight);
        //不进行图片抖动处理
        options.inDither = false;
        //系统内存不足时，可以回收Bitmap占据的内存空间
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        try {
            return createBitmap(
                    getDegree(originalPath),
                    readImage(originalPath, options));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取缩略图
     * @param originalPath
     * @param saveFile
     * @return
     */
    public static String getThumbFilePath(String originalPath, File saveFile){
        return saveBitmap(
                getThumbFilePath(originalPath),
                75,
                saveFile
        );
    }

    /**
     * 获取缩略图
     * @param originalPath
     * @return
     */
    public static Bitmap getThumbFilePath(String originalPath){
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(originalPath, options);

        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int maxWidth = MAX_WIDTH;
        int maxHeight = MAX_HEIGHT;
        int minWidth = MIN_WIDTH;
        int minHeight = MIN_HEIGHT;
        Bitmap bitmap;

        if (outWidth <= 0 || outHeight <= 0) {
            return null;
        }

        if (outWidth < MIN_WIDTH && outHeight < MIN_HEIGHT) {
            int reqWidth;
            int reqHeight;
            float ratio = options.outWidth * 1.0f / options.outHeight;
            if (ratio > 1.0f) {
                reqWidth = MIN_WIDTH;
                reqHeight = (int) (outHeight * (reqWidth * 1.0f / outWidth));
            } else {
                reqHeight = MIN_HEIGHT;
                reqWidth = (int) (MIN_HEIGHT * 1.0f / outHeight * outWidth);
            }
            bitmap = scaledBitmap(originalPath, reqWidth, reqHeight);
        } else if (outHeight > 0 && (outWidth / outHeight > 3)) {
            int reqWidth = maxWidth;
            int reqHeight = outHeight;

            if (outHeight < minHeight) {
                reqWidth = (int) (maxWidth * (outHeight * 1.0f / minHeight));
            }

            options.inSampleSize = calculateInSampleSize(outWidth, outHeight,
                    maxWidth, maxHeight,
                    maxWidth * 3, maxHeight);
            options.inJustDecodeBounds = false;
            bitmap = cropCenterBitmap(originalPath, reqWidth, reqHeight, options);
        } else if (outWidth > 0 && (outHeight / outWidth > 3)) {
            int reqWidth = outWidth;
            int reqHeight = maxHeight;

            if (outWidth < minWidth) {
                reqHeight = (int) (maxHeight * (outWidth * 1.0f / minWidth));
            }
            options.inSampleSize = calculateInSampleSize(outWidth, outHeight,
                    maxWidth, maxHeight,
                    maxWidth , maxHeight * 3);
            options.inJustDecodeBounds = false;
            bitmap = cropCenterBitmap(originalPath, reqWidth, reqHeight, options);
        } else {
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            //计算inSampleSize值
            options.inSampleSize = calculateInSampleSize(
                    options.outWidth, options.outHeight,
                    MIN_WIDTH, MIN_HEIGHT,
                    maxWidth, maxHeight);
            //不进行图片抖动处理
            options.inDither = false;
            //系统内存不足时，可以回收Bitmap占据的内存空间
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = createBitmap(getDegree(originalPath),
                    readImage(originalPath, options));
        }
        return bitmap;
    }

    /**
     * 加载本地图片至内存中，以流的形式
     * @param originalPath
     * @param options
     * @return
     */
    public static Bitmap readImage(String originalPath, BitmapFactory.Options options){

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(originalPath));
            return BitmapFactory.decodeStream(fis, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 旋转图片
     * @param degree
     * @param bitmap
     * @return
     */
    private static Bitmap createBitmap(int degree, Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        Bitmap rotateBitmap = null;
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);

            rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix, true);
            return rotateBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            //do nothing
        } finally {
            //当返回的不是同一对象时，必须清理掉
            if (!bitmap.equals(rotateBitmap)){
                bitmap.recycle();
            }
        }
        return null;
    }

    /**
     * 图片缩放
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static Bitmap scaledBitmap(String filePath,
                                        int reqWidth, int reqHeight) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Bitmap scaledBitmap = null;
        try {
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
            return scaledBitmap;
        } catch (OutOfMemoryError e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!bitmap.equals(scaledBitmap)){
                bitmap.recycle();
            }
        }
        return  null;
    }

    /**
     * 裁剪获取图片中间部分
     * @param filePath 图片文件路径
     * @param reqWidth 所需图片宽度
     * @param reqHeight 所需图片高度
     * @return
     */
    private static Bitmap cropCenterBitmap(String filePath,
                                           int reqWidth, int reqHeight,
                                           BitmapFactory.Options options){
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap == null){
            return null;
        }
        int xTopLeft = 0;
        int yTopLeft = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > reqWidth) {
            xTopLeft = (width - reqWidth) / 2;
        } else {
            reqWidth = width;
        }
        if (height > reqHeight) {
            yTopLeft = (height - reqHeight) / 2;
        } else {
            reqHeight = height;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(getDegree(filePath));
        Bitmap cropBitmap = null;
        try {
            cropBitmap = Bitmap.createBitmap(bitmap,
                    xTopLeft,
                    yTopLeft,
                    reqWidth,
                    reqHeight,
                    matrix,
                    true);
            return cropBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
        }  finally {
            if (!bitmap.equals(cropBitmap)){
                bitmap.recycle();
            }
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param filePath 图片绝对路径
     * @return degree 旋转的角度
     */
    public static int getDegree(String filePath){
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 计算inSampleSize值大小
     * @param outWidth
     * @param outHeight
     * @param minWidth
     * @param minHeight
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private static int calculateInSampleSize(int outWidth, int outHeight,
                                             int minWidth, int minHeight,
                                             int maxWidth, int maxHeight){
        int inSampleSize = 1;
        int width ;
        int height;
        if (outWidth < maxWidth && outHeight < maxHeight){
            return inSampleSize;
        }
        if (outWidth / maxWidth > outHeight / maxHeight) {
            if (outWidth >= maxWidth) {
                width = maxWidth;
                height = outHeight * maxWidth / outWidth;
            } else {
                width = outWidth;
                height = outHeight;
            }
            if (outHeight < minHeight) {
                height = minHeight;
                width = outWidth * minHeight / outHeight;
                if (width > maxWidth) {
                    width = maxWidth;
                }
            }
        } else {
            if (outHeight >= maxHeight) {
                height = maxHeight;
                width = outWidth * maxHeight / outHeight;
            } else {
                width = outWidth;
                height = outHeight;
            }
            if (outWidth < minWidth) {
                width = minWidth;
                height = outHeight * minWidth / outWidth;
                if (height > maxHeight) {
                    height = maxHeight;
                }
            }
        }
        int widthRatio = Math.round((float) outWidth / (float) width );
        int heightRatio = Math.round((float) outHeight / (float) height);
        inSampleSize = Math.max(widthRatio, heightRatio);
        if (inSampleSize <= 1){
            inSampleSize = 1;
        }
        return inSampleSize;
    }

    /**
     * 保存bitmap到指定路径
     * @param bitmap
     * @param saveFile 保存目标文件
     * @return 目标文件绝对路径
     */
    public static String saveBitmap(Bitmap bitmap, int quality, File saveFile){
        if (saveFile == null || bitmap == null){
            return null;
        }
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        //
        BufferedOutputStream bos = null;
        boolean bSaveRet = false;
        try {
            //创建文件
            saveFile.createNewFile();
            //写入文件
            bos = new BufferedOutputStream(new FileOutputStream(saveFile));
            bSaveRet = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        return bSaveRet ? saveFile.getAbsolutePath() : null;
    }

    /**
     * 文件拷贝(仅限缩略图文件拷贝使用)
     * @param srcFilePath 源文件路径
     * @param destFile 目标文件
     * @return
     */
    public static String copyFile(String srcFilePath, File destFile){
        //源文件不存在，返回错误
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()){
            return null;
        }

        //目前文件已经存在，则不进行拷贝
        if (destFile.exists()){
            return srcFilePath;
        }

        //文件拷贝
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(srcFilePath));
            bos = new BufferedOutputStream(new FileOutputStream(destFile));
            //创建文件
            destFile.createNewFile();
            byte[] buffer = new byte[1024];
            int readLen = 0;
            while ((readLen = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, readLen);
            }
            bos.flush();
            return destFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null){
                    bos.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return srcFilePath;
    }
}
