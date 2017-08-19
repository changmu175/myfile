package com.xdja.imsdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.xdja.pushsdk.utils.LogHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Package: com.appcom.commonview.utils
 * @Author: xdjaxa
 * @Date: 2017-03-24 10:23
 * @Version V1.0
 * @Comment:
 */
public class ThumbnailUtils {

    /**
     * 缩略图
     */
    public static final int KIND_TH = 1;

    /**
     * 高清缩略图
     */
    public static final int KIND_HD = 2;

    private static final int MAX_NUM_PIXELS_THUMBNAIL = 480 * 360;
    private static final int MAX_NUM_PIXELS_HD_THUMBNAIL = 1280 * 720;
    private static final int UNCONSTRAINED = -1;

    public static final float RATIO = 3.0F;

    public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

    public static final int TARGET_SIZE_HD_THUMBNAIL = 720;

    public static String createImageThumbnail(String filePath, int kind, File saveFile) {
        boolean wantMini = (kind == KIND_TH);
        int targetSize = wantMini
                ? TARGET_SIZE_MINI_THUMBNAIL
                : TARGET_SIZE_HD_THUMBNAIL;
        int maxPixels = wantMini
                ? MAX_NUM_PIXELS_THUMBNAIL
                : MAX_NUM_PIXELS_HD_THUMBNAIL;

        Bitmap bitmap = null;
        FileInputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            stream = new FileInputStream(filePath);
            FileDescriptor fd = stream.getFD();
            BitmapFactory.decodeFileDescriptor(fd, null, options);

            if (options.mCancel || options.outWidth <= 0
                    || options.outHeight <= 0) {
                return null;
            }

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;

            //图片太小，直接拷贝
            if (outWidth <= 360 && outHeight <= 480) {
                bitmap = createBitmap(getDegree(filePath),
                        BitmapFactory.decodeFileDescriptor(fd, null, null));
            }
            //图片狭长，宽高比大于3
            else if ((outWidth * 1.0F / outHeight >= RATIO) || (outHeight * 1.0F / outWidth >= RATIO)) {
                if (wantMini) {
                    //图片进行裁剪，取中间部分
                    if (outWidth * 1.0F / outHeight >= RATIO) {
                        outHeight = Math.min(outHeight, 1920);
                        bitmap = cropCenterBitmap(filePath, options, (int) (outHeight * RATIO), outHeight);
                    } else {
                        outWidth = Math.min(outWidth, 1080);
                        bitmap = cropCenterBitmap(filePath, options, outWidth, (int) (outWidth * RATIO));
                    }
                } else {
                    bitmap = createBitmap(getDegree(filePath),
                            readImage(filePath, options));
                }
            } else {
                options.inSampleSize = computeSampleSize(
                        options, targetSize, maxPixels);
                options.inJustDecodeBounds = false;
                options.inDither = false;
                bitmap = createBitmap(getDegree(filePath),
                        BitmapFactory.decodeFileDescriptor(fd, null, options));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) {
            }
        }
        return saveBitmap(bitmap, 80, saveFile);
    }

    public static Bitmap readImage(String originalPath, BitmapFactory.Options options) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(originalPath));
            FileDescriptor fd = fis.getFD();
            options.inJustDecodeBounds = false;
            options.inDither = false;
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
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

    private static Bitmap createBitmap(int degree, Bitmap bitmap) {
        if (bitmap == null) {
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
            if (!bitmap.equals(rotateBitmap)) {
                bitmap.recycle();
            }
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param filePath 图片绝对路径
     * @return degree 旋转的角度
     */
    public static int getDegree(String filePath) {
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
     * 裁剪获取图片中间部分
     *
     * @param filePath  图片文件路径
     * @param reqWidth  所需图片宽度
     * @param reqHeight 所需图片高度
     * @return
     */
    private static Bitmap cropCenterBitmap(String filePath,
                                           BitmapFactory.Options options,
                                           int reqWidth, int reqHeight) {

        if (options.outWidth > 1080 && options.outHeight > 1920) {
            options.inSampleSize = (int) Math.max(options.outWidth * 1.0F / reqWidth,
                    options.outHeight * 1.0F/ reqHeight);
        }
        Bitmap bitmap = readImage(filePath, options);
        if (bitmap == null) {
            LogHelper.getHelper().d("read bitmap error!!");
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
        try {
            Bitmap output = Bitmap.createBitmap(
                    bitmap,
                    xTopLeft,
                    yTopLeft,
                    reqWidth,
                    reqHeight,
                    matrix,
                    true);
            if (!bitmap.equals(output)) {
                bitmap.recycle();
            }
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static String saveBitmap(Bitmap bitmap, int quality, File saveFile) {
        if (saveFile == null || bitmap == null) {
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
                if (bos != null) {
                    bos.close();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bSaveRet ? saveFile.getAbsolutePath() : "";
    }

    /**
     * 文件拷贝(仅限缩略图文件拷贝使用)
     *
     * @param srcFilePath 源文件路径
     * @param destFile    目标文件
     * @return
     */
    public static String copyFile(String srcFilePath, File destFile) {
        //源文件不存在，返回错误
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            return null;
        }

        //目前文件已经存在，则不进行拷贝
        if (destFile.exists()) {
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
                if (bos != null) {
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