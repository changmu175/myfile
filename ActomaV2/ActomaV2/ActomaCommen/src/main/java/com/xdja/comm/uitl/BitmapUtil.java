package com.xdja.comm.uitl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Toast;

import com.xdja.comm.R;
import com.xdja.safekeyjar.Base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    private static final int maxInSampleSize = 4;

    private static final int itemh = 150;
    private static final int itemw = 150;

    public static Bitmap getBitmapByPath(String path, int inSampleSize){
        Bitmap bmp;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            options.inPreferredConfig = Config.RGB_565;
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeFile(path, options);
        }catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (inSampleSize < maxInSampleSize) {
                bmp = getBitmapByPath(path, inSampleSize + 1);
            } else {
                return null;
            }
        }
        return bmp;
    }

    /*
     * 获取压缩后的图片
     */
    public static Bitmap getZoomedDrawable(String filePath, int zoom) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int mWidth = options.outWidth;
        int mHeight = options.outHeight;
        int s = 1;
        while ((mWidth / s > itemw * 2 * zoom)
                || (mHeight / s > itemh * 2 * zoom)) {
            s *= 2;
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = s;
        options.inPreferredConfig = Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);

        if (bm != null) {
            int h = bm.getHeight();
            int w = bm.getWidth();

            float ft = (float)  w / h;// modified by ycm for lint 2017/02/13
            float fs = (float)  itemw /  itemh;// modified by ycm for lint 2017/02/13

            int neww = ft >= fs ? itemw * zoom : (int) (itemh * zoom * ft);
            int newh = ft >= fs ? (int) (itemw * zoom / ft) : itemh * zoom;

            float scaleWidth = ((float) neww) / w;
            float scaleHeight = ((float) newh) / h;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            bm = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
            // System.gc();
            return bm;
        }
        return null;
    }

    /**
     * 将textView转换为bitMap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();// modified by ycm for lint 2017/02/13
    }

    /**
     * 获取缩略图
     *
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getExtractBitmap(String imagePath, int width, int height) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inJustDecodeBounds = false; // 设为 false
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param filepath
     * @param destBitmap
     * @throws IOException
     */
    public static void saveBitmapToLocal(String filepath, Bitmap destBitmap, String fileName)
            throws IOException {
        File f = new File(filepath);
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(filepath + "/" + fileName);
        file.createNewFile();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            destBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        }
    }

    /**
     * 保存byte[]到文件
     *
     * @param filepath
     * @throws IOException
     */
    public static void saveByteToLocal(String filepath, String fileName,byte[] imageByte)
            throws IOException {
        File f = new File(filepath);
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(filepath + "/" + fileName);
        file.createNewFile();

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);

            //读取字节数字节数
            byte[] buffer = new byte[1024];
            int lastCount = imageByte.length;

            //读写数据
            while (lastCount >= 0) {
                if (lastCount >= buffer.length) {
                    System.arraycopy(imageByte, imageByte.length - lastCount, buffer, 0, buffer.length);
                    fos.write(buffer, 0, buffer.length);
                }else {
                    System.arraycopy(imageByte, imageByte.length - lastCount, buffer, 0, lastCount);
                    fos.write(buffer, 0, lastCount);
                }
                lastCount = lastCount - buffer.length;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        }
    }

    /**
     * bitmap 转 base64 不压缩
     */
    public static String bitmapBase64(Bitmap bitmap) {
        String result;
        ByteArrayOutputStream baoss = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoss);
        byte[] bitmapBytes = baoss.toByteArray();
        result = Base64.encodeBytes(bitmapBytes, Base64.DONT_BREAK_LINES);
        return result;
    }

    /**
     * 压缩图片至指定大小
     *
     * @param bitmap  要压缩的图片
     * @param maxSize 最大尺寸-KB
     */
    public Bitmap imageZoom(Bitmap bitmap, double maxSize) {
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }


    /**
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
    }

    /**
     * 根据base64码转换成bitmap
     *
     * @param s
     * @return
     */
    public static Bitmap getBitmapFromString(String s) {
        byte[] temp = Base64.decode(s, Base64.DONT_BREAK_LINES);
        return getBitmapFromByte(temp);
    }

    /**
     * 根据二进制转换为bitmap
     *
     * @param temp
     * @return
     */
    public static Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            return BitmapFactory.decodeByteArray(temp, 0, temp.length);// modified by ycm for lint 2017/02/13
        } else {
            return null;
        }
    }

    /**
     * 从本地SD卡中获取图片
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapFromLocalFile(String filePath) {
        FileInputStream fis = null;
        Bitmap bmp = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                fis = new FileInputStream(file);
            }
            bmp = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bmp;
    }

    /**
     * Bitmap转为Byte[]
     *
     * @param bm 位图
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm, int level) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, level, baos);
        baos.size();
        return baos.toByteArray();
    }

    /**
     * @param bitmap     图片
     * @param maxHeight  最大高度(像素)
     * @param maxWidth   最大宽度(像素)
     * @param MapMaxSize 最大尺寸(字节)
     * @return
     */
    public byte[] getZoomBitmap(Bitmap bitmap, int maxHeight, int maxWidth, int MapMaxSize) {

        byte[] resultBitMap;

        int curCompressLevel = 100;// 当前压缩比

        Bitmap miniMap = zoomImage(bitmap, maxWidth, maxHeight);

        byte[] curMiniMapBytes = bitmap2Bytes(miniMap, curCompressLevel);// 当前压缩之后的大小
        if (curMiniMapBytes.length > MapMaxSize) {
            byte[] lastMiniMapBytes = curMiniMapBytes;// 上一次压缩之后的大小
            byte[] lastButOneMiniMapBytes = null;

            int maxCompressLevel = 99;// 最大压缩比
            int minCompressLevel = 1;// 最小压缩比
            int lastCompressLevel = curCompressLevel;// 上一次的压缩比
            int tempCompressLevel;
            curCompressLevel = 100 - MapMaxSize * 100 / curMiniMapBytes.length;
            curCompressLevel = curCompressLevel == lastCompressLevel ? curCompressLevel - 1 : curCompressLevel;
            curMiniMapBytes = bitmap2Bytes(miniMap, curCompressLevel);// 当前压缩之后的大小
            // 如果当前压缩比例与上次压缩比例不相等并且不相临
            while (Math.abs(curCompressLevel - lastCompressLevel) > 1 && curMiniMapBytes.length != MapMaxSize) {
                // 首先将原来缩略图赋值给临时存放变量，将原来的缩略图赋值为当前的缩略图
                lastButOneMiniMapBytes = lastMiniMapBytes;
                lastMiniMapBytes = curMiniMapBytes;

                tempCompressLevel = curCompressLevel;
                if (curMiniMapBytes.length > MapMaxSize) {
                    maxCompressLevel = curCompressLevel;
                    if (curCompressLevel > lastCompressLevel) {
                        curCompressLevel = curCompressLevel - (curCompressLevel - lastCompressLevel) / 2;
                    } else {
                        curCompressLevel = minCompressLevel + (curCompressLevel - minCompressLevel) / 2;
                    }
                } else {
                    minCompressLevel = curCompressLevel;
                    if (curCompressLevel > lastCompressLevel) {
                        curCompressLevel = maxCompressLevel - (maxCompressLevel - curCompressLevel) / 2;
                    } else {
                        curCompressLevel = curCompressLevel + (lastCompressLevel - curCompressLevel) / 2;
                    }
                }
                lastCompressLevel = tempCompressLevel;

                if (curCompressLevel != lastCompressLevel) {
                    curMiniMapBytes = bitmap2Bytes(miniMap, curCompressLevel);
                }
            }

            // 如果当前一次压缩和上一次压缩都大于目标最大值，结果是上一次的前一次压缩
            if (curMiniMapBytes.length > MapMaxSize && lastMiniMapBytes.length > MapMaxSize) {
                resultBitMap = lastButOneMiniMapBytes;
            }

            //如果当前一次压缩和上一次压缩都小于目标最大值，证明上一次的前一次大于目标最大值，并且当前一次大于上一次，结果去当前一次
            else if (curMiniMapBytes.length < MapMaxSize && lastMiniMapBytes.length < MapMaxSize) {
                resultBitMap = curMiniMapBytes;
            } else {
                //如果当前一次和上一次分别在目标最大值的两侧，取比目标最大值小的那个
                if (curMiniMapBytes.length > MapMaxSize) {
                    resultBitMap = lastMiniMapBytes;
                } else {
                    resultBitMap = curMiniMapBytes;
                }
            }
        } else {
            return bitmap2Bytes(miniMap, 100);
        }
        return resultBitMap;
    }

    /**
     * @param bitmap     图片
     * @param MapMaxSize 最大尺寸(字节)
     * @return
     */
    public byte[] getBigmapZoomBitmap(Bitmap bitmap, int MapMaxSize) {

        byte[] resultBitMap;

        int curCompressLevel = 100;// 当前压缩比


        byte[] curMiniMapBytes;// 当前压缩之后的大小// modified by ycm for lint 2017/02/13
        curMiniMapBytes = bitmap2Bytes(bitmap, curCompressLevel);

        if (curMiniMapBytes.length > MapMaxSize) {
            byte[] lastMiniMapBytes = curMiniMapBytes;// 上一次压缩之后的大小
            byte[] lastButOneMiniMapBytes = null;

            int maxCompressLevel = 99;// 最大压缩比
            int minCompressLevel = 1;// 最小压缩比
            int lastCompressLevel = curCompressLevel;// 上一次的压缩比
            int tempCompressLevel;
            curCompressLevel = 100 - MapMaxSize * 100 / curMiniMapBytes.length;
            curCompressLevel = curCompressLevel == lastCompressLevel ? curCompressLevel - 1 : curCompressLevel;
            curMiniMapBytes = bitmap2Bytes(bitmap, curCompressLevel);// 当前压缩之后的大小
            // 如果当前压缩比例与上次压缩比例不相等并且不相临
            while (Math.abs(curCompressLevel - lastCompressLevel) > 1 && curMiniMapBytes.length != MapMaxSize) {
                // 首先将原来缩略图赋值给临时存放变量，将原来的缩略图赋值为当前的缩略图
                lastButOneMiniMapBytes = lastMiniMapBytes;
                lastMiniMapBytes = curMiniMapBytes;

                tempCompressLevel = curCompressLevel;
                if (curMiniMapBytes.length > MapMaxSize) {
                    maxCompressLevel = curCompressLevel;
                    if (curCompressLevel > lastCompressLevel) {
                        curCompressLevel = curCompressLevel - (curCompressLevel - lastCompressLevel) / 2;
                    } else {
                        curCompressLevel = minCompressLevel + (curCompressLevel - minCompressLevel) / 2;
                    }
                } else {
                    minCompressLevel = curCompressLevel;
                    if (curCompressLevel > lastCompressLevel) {
                        curCompressLevel = maxCompressLevel - (maxCompressLevel - curCompressLevel) / 2;
                    } else {
                        curCompressLevel = curCompressLevel + (lastCompressLevel - curCompressLevel) / 2;
                    }
                }
                lastCompressLevel = tempCompressLevel;

                if (curCompressLevel != lastCompressLevel) {
                    curMiniMapBytes = bitmap2Bytes(bitmap, curCompressLevel);
                }
            }

            // 如果当前一次压缩和上一次压缩都大于目标最大值，结果是上一次的前一次压缩
            if (curMiniMapBytes.length > MapMaxSize && lastMiniMapBytes.length > MapMaxSize) {
                resultBitMap = lastButOneMiniMapBytes;
            }

            //如果当前一次压缩和上一次压缩都小于目标最大值，证明上一次的前一次大于目标最大值，并且当前一次大于上一次，结果去当前一次
            else if (curMiniMapBytes.length < MapMaxSize && lastMiniMapBytes.length < MapMaxSize) {
                resultBitMap = curMiniMapBytes;
            } else {
                //如果当前一次和上一次分别在目标最大值的两侧，取比目标最大值小的那个
                if (curMiniMapBytes.length > MapMaxSize) {
                    resultBitMap = lastMiniMapBytes;
                } else {
                    resultBitMap = curMiniMapBytes;
                }
            }
        } else {
            return bitmap2Bytes(bitmap, 100);
        }
//        Log.d("xrjTest", "resultBitMap.length 1    " + resultBitMap.length);
        return resultBitMap;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param destBitmap
     */
    public static void saveBitmapToLocalCamera(Context context, Bitmap destBitmap) {
        // 调用系统提供的插入图库的方法
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                destBitmap, "", "");
        // 更新系统图库
        context.sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://"
                        + Environment.getExternalStorageDirectory())));
        Toast.makeText(context, context.getResources().getString(R.string.save_picture_success),
                Toast.LENGTH_SHORT).show();
    }


    /**
     * 按照图片大小载入图片
     *
     * @param path  文件路径
     * @param width 最多宽度
     * @param heigh 最大高度
     * @return 图片信息
     */
    public static Bitmap getBitmap(String path, int width, int heigh) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            Bitmap bt;// modified by ycm for lint 2017/02/13
            int xScale = op.outWidth / width;
            int yScale = op.outHeight / heigh;
            op.inSampleSize = xScale > yScale ? xScale : yScale;
            op.inJustDecodeBounds = false;
            bt = BitmapFactory.decodeFile(path, op);

            return bt;
        } catch (Exception e) {// delete by ycm for lint 2017/02/13
                e.printStackTrace();

            return null;
        }
    }

    /**
     * Bitmap转byte[]
     *
     * @param bm Bitmap
     * @return byte[]
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, int level) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, level, baos);
        return baos.toByteArray();
    }

    /**
     * 添加缩略图
      */
    public static byte[] getMiniMap(String pictureFilePath,int width,int height,int miniMapMaxSize) {
        byte[] miniMapStr;
        int curCompressLevel = 100;// 当前压缩比
        Bitmap miniMap = getBitmap(pictureFilePath, width, height);
        if (miniMap == null) {
            return null;
        }
        byte[] curMiniMapBytes = Bitmap2Bytes(miniMap, curCompressLevel);// 当前压缩之后的大小
        if (curMiniMapBytes.length > miniMapMaxSize) {
            byte[] lastMiniMapBytes = curMiniMapBytes;// 上一次压缩之后的大小
            byte[] lastButOneMiniMapBytes = null;

            int maxCompressLevel = 99;// 最大压缩比
            int minCompressLevel = 1;// 最小压缩比
            int lastCompressLevel = curCompressLevel;// 上一次的压缩比
            int tempCompressLevel;
            curCompressLevel = 100 - miniMapMaxSize * 100 / curMiniMapBytes.length;
            curCompressLevel = curCompressLevel == lastCompressLevel ? curCompressLevel - 1 : curCompressLevel;
            curMiniMapBytes = Bitmap2Bytes(miniMap, curCompressLevel);// 当前压缩之后的大小
            // 如果当前压缩比例与上次压缩比例不相等并且不相临
            while (Math.abs(curCompressLevel - lastCompressLevel) > 1 && curMiniMapBytes.length != miniMapMaxSize) {
                // 首先将原来缩略图赋值给临时存放变量，将原来的缩略图赋值为当前的缩略图
                lastButOneMiniMapBytes = lastMiniMapBytes;
                lastMiniMapBytes = curMiniMapBytes;

                tempCompressLevel = curCompressLevel;
                if (curMiniMapBytes.length > miniMapMaxSize) {
                    maxCompressLevel = curCompressLevel;
                    if (curCompressLevel > lastCompressLevel) {
                        curCompressLevel = curCompressLevel - (curCompressLevel - lastCompressLevel) / 2;
                    } else {
                        curCompressLevel = minCompressLevel + (curCompressLevel - minCompressLevel) / 2;
                    }
                } else {
                    minCompressLevel = curCompressLevel;
                    if (curCompressLevel > lastCompressLevel) {
                        curCompressLevel = maxCompressLevel - (maxCompressLevel - curCompressLevel) / 2;
                    } else {
                        curCompressLevel = curCompressLevel + (lastCompressLevel - curCompressLevel) / 2;
                    }
                }
                lastCompressLevel = tempCompressLevel;

                if (curCompressLevel != lastCompressLevel) {
                    curMiniMapBytes = Bitmap2Bytes(miniMap, curCompressLevel);
                }
            }

            // 如果当前一次压缩和上一次压缩都大于目标最大值，结果是上一次的前一次压缩
            if (curMiniMapBytes.length > miniMapMaxSize && lastMiniMapBytes.length > miniMapMaxSize) {
                miniMapStr = lastButOneMiniMapBytes;
            }

            //如果当前一次压缩和上一次压缩都小于目标最大值，证明上一次的前一次大于目标最大值，并且当前一次大于上一次，结果去当前一次
            else if (curMiniMapBytes.length < miniMapMaxSize && lastMiniMapBytes.length < miniMapMaxSize) {
                miniMapStr = curMiniMapBytes;
            } else {
                //如果当前一次和上一次分别在目标最大值的两侧，取比目标最大值小的那个
                if (curMiniMapBytes.length > miniMapMaxSize) {
                    miniMapStr = lastMiniMapBytes;
                } else {
                    miniMapStr = curMiniMapBytes;
                }
            }

            lastMiniMapBytes = null;
            lastButOneMiniMapBytes = null;
        } else {
            miniMapStr = curMiniMapBytes;
        }

        curMiniMapBytes = null;

        return miniMapStr;
    }

}
