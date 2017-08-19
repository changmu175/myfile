package com.xdja.imp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;


import com.xdja.imp.ImApplication;
import com.xdja.imp.R;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitmapUtils {

    private static final int itemh = 360;
    private static final int itemw = 360;
    public static final String AN_TONG_TITLE_PLUS = "anTong_title_plus";
    public static final String AN_TONG_DETAIL_PLUS = "anTong_detail_plus";
    /**
     * 缩略图最大大小（Byte)
     */
    private static final int miniMapMaxSize = 8192;


    /**
     * 1.5倍放大bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1.5f, 1.5f); // 长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    /**
     * 缩小bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap small(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        //memory leak
        if (resizeBmp != bitmap) {
            bitmap.recycle();
        }
        return resizeBmp;
    }

    /**
     * 根据图片名称获取对应的bitmap
     *
     * @param fieldName 名称
     * @param resource  资源实例
     * @return
     * @throws NoSuchFieldException
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Bitmap getBitmapWithName(String fieldName, Resources resource)
            throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field field = R.drawable.class.getDeclaredField(fieldName);
        int resouseId = Integer.parseInt(field.get(null).toString());
        return BitmapFactory.decodeResource(resource, resouseId);
    }



    /**
     * 对安通+文本进行输出
     *
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    public static SpannableString formatAnTongSpanContent(CharSequence sourceContent, Context context, float smallScall, String plusType) {
        if (TextUtils.isEmpty(sourceContent)) {
            return null;
        }
        SpannableString spannableString =  new SpannableString(sourceContent);
        try {
            Bitmap bitmap;
            String anTongStr;
            if (AN_TONG_TITLE_PLUS.equalsIgnoreCase(plusType)){
                anTongStr = context.getResources().getString(R.string.antong_title);
            }else if (AN_TONG_DETAIL_PLUS.equalsIgnoreCase(plusType)){
                anTongStr = context.getResources().getString(R.string.antongname);
            } else {
                anTongStr = "xxxx";
            }
            Pattern pattern = Pattern.compile(context.getResources().getString(R.string.anTongStr));
            Matcher matcher = pattern.matcher(sourceContent);
            while (matcher.find()) {
                bitmap = BitmapUtils.getBitmapWithName(anTongStr,
                        context.getResources());
                bitmap = BitmapUtils.small(bitmap, smallScall);
                // 缩小表情图片
                // 用ImageSpan指定图片替代文字
                ImageSpan span = new ImageSpan(context, bitmap);
                // 其实写入EditView中的是这个字段“[fac”，表情图片会替代这个字段显示
                spannableString.setSpan(span, matcher.end(), matcher.end()+1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    /**
     * 对带表情的文本进行输出
     *
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    public static SpannableString formatSpanContentAdapter(String sourceContent,
                                                           Context context) throws
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {
        if (TextUtils.isEmpty(sourceContent)) {
            return null;
        }
        SpannableString spannableString = new SpannableString(sourceContent);
        String emoStr;
        Pattern pattern = Pattern.compile("(\\[emoji_[0-9]{3}\\])");
        Matcher matcher = pattern.matcher(sourceContent);
        while (matcher.find()) {
            emoStr = matcher.group(1);
            emoStr = emoStr.substring(1, emoStr.length() - 1);
            Bitmap bitmap = BitmapUtils.getBitmapWithName(emoStr,
                    context.getResources());
            // 缩小表情图片
            bitmap = BitmapUtils.small(bitmap, (float) 0.5);
            // 用ImageSpan指定图片替代文字
            ImageSpan span = new ImageSpan(context, bitmap);
            // 其实写入EditView中的是这个字段“[fac”，表情图片会替代这个字段显示
            spannableString.setSpan(span, matcher.start(), matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    /**
     * byte[]转Bitmap
     *
     * @param b byte数组
     * @return Bitmap
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * Bitmap转byte[]
     *
     * @param bm Bitmap
     * @return byte[]
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Bitmap转byte[]
     *
     * @param bm Bitmap
     * @return byte[]
     */
    private static byte[] Bitmap2Bytes(Bitmap bm, int level) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, level, baos);
        return baos.toByteArray();
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

            float ft =  ((float) w / (float) h);
            float fs =  ((float) itemw / (float) itemh);

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
     * * 获取圆形图片方法
     *
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 按照图片大小载入图片
     *
     * @param path  文件路径
     * @param width 最多宽度
     * @param heigh 最大高度
     * @return 图片信息
     */
    private static Bitmap getBitmap(String path, int width, int heigh) {
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            int xScale = op.outWidth / width;
            int yScale = op.outHeight / heigh;
            op.inSampleSize = xScale > yScale ? xScale : yScale;
            op.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, op);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2014-05-17 11:04:07 xrj 添加缩略图
    public static String getMiniMap(String pictureFilePath) {
        String miniMapStr_Base64;
        int curCompressLevel = 100;// 当前压缩比
        Bitmap miniMap = getBitmap(pictureFilePath, itemw, itemh);
        if (miniMap == null) {
            return "";
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
                miniMapStr_Base64 = Base64.encodeToString(lastButOneMiniMapBytes, Base64.NO_WRAP);
            }

            //如果当前一次压缩和上一次压缩都小于目标最大值，证明上一次的前一次大于目标最大值，并且当前一次大于上一次，结果去当前一次
            else if (curMiniMapBytes.length < miniMapMaxSize && lastMiniMapBytes.length < miniMapMaxSize) {
                miniMapStr_Base64 = Base64.encodeToString(curMiniMapBytes, Base64.NO_WRAP);
            } else {
                //如果当前一次和上一次分别在目标最大值的两侧，取比目标最大值小的那个
                if (curMiniMapBytes.length > miniMapMaxSize) {
                    miniMapStr_Base64 = Base64.encodeToString(lastMiniMapBytes, Base64.NO_WRAP);
                } else {
                    miniMapStr_Base64 = Base64.encodeToString(curMiniMapBytes, Base64.NO_WRAP);
                }
            }

        } else {
            miniMapStr_Base64 = Base64.encodeToString(curMiniMapBytes, Base64.DEFAULT);
        }

        return miniMapStr_Base64;
    }


    /**
     * 旋转图片
     * @param bmap 要旋转的图片
     * @return
     */
    public static Bitmap rotaingImageView(Bitmap bmap, int angle) {
        //旋转图片 动作
        Matrix matrix1 = new Matrix();
        matrix1.postRotate(angle);
        int width  = bmap.getWidth();
        int height = bmap.getHeight();
        // 创建新的图片
        return Bitmap.createBitmap(bmap, 0, 0,
                width, height, matrix1, true);
    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    //生成圆角图片
    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));


            // 拿到bitmap宽或高的小值
            int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
            final float roundPx = (float) (bSize/2);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }



    public static SpannableString formatSpanContent(CharSequence sourceContent, Context context, float smallScall) {
        if (sourceContent == null) {
            return null;
        }
        SpannableString spannableString = null;
        try {
            spannableString = new SpannableString(sourceContent);
            String emoStr;
            Pattern pattern = Pattern.compile("(\\[emoji_[0-9]{3}\\])");
            Matcher matcher = pattern.matcher(sourceContent);
            while (matcher.find()) {
                emoStr = matcher.group(1);
                emoStr = emoStr.substring(1, emoStr.length() - 1);
                ImageCache imageCache = ImageCache.getInstance();
                Bitmap bitmap = null;
                if (smallScall== ImApplication.FACE_ITEM_SMALL_VALUE){
                    if (imageCache.smallCashGet(emoStr) != null) {
                        bitmap = imageCache.smallCashGet(emoStr);
                    }else {
                        bitmap = BitmapUtils.getBitmapWithName(emoStr,
                                context.getResources());
                        bitmap = BitmapUtils.small(bitmap, smallScall);
                        imageCache.smallCashPut(emoStr, bitmap);
                    }

                } else if (smallScall== ImApplication.FACE_ITEM_NORMAL_VALUE) {
                    if (imageCache.get(emoStr) != null) {
                        bitmap = imageCache.get(emoStr);
                    } else {
                        bitmap = BitmapUtils.getBitmapWithName(emoStr,
                                context.getResources());
                        bitmap = BitmapUtils.small(bitmap, smallScall);
                        imageCache.put(emoStr, bitmap);
                    }
                }

                // 缩小表情图片
                // 用ImageSpan指定图片替代文字
                ImageSpan span = new ImageSpan(context, bitmap);
                // 其实写入EditView中的是这个字段“[fac”，表情图片会替代这个字段显示
                spannableString.setSpan(span, matcher.start(), matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }
}
