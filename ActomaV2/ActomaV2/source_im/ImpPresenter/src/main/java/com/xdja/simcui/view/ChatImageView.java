package com.xdja.simcui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xdja.imp.R;
import com.xdja.imp.util.ImageCache;

/**
 * 聊天界面图片控件
 * Created by xdjaxa on 2016/8/23.
 */
public class ChatImageView extends ImageView {

    private static final int IMAGE_MIN_WIDTH = 320;
    private static final int IMAGE_MIN_HEIGHT = 320;
    private static final int IMAGE_MAX_WIDTH = 480;
    private static final int IMAGE_MAX_HEIGHT = 480;

    public ChatImageView(Context context) {
        this(context, null);
    }

    public ChatImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 加载图片
     *
     * @param url 本地图片绝对路径
     */
    public void loadImage(String url) {
        if (url == null || "".equals(url)) {
            setImageResource(R.drawable.pic_failed);
            return;
        }

        Bitmap bitmap = ImageCache.getInstance().get(url);
        if (bitmap == null) {
            //读取bitmap
            bitmap = createScaledBitmap(url);
            if (bitmap != null) {
                ImageCache.getInstance().put(url, bitmap);
            }
        }
        if (bitmap != null) {
            setImageBitmap(bitmap);
        } else {
            setImageResource(R.drawable.pic_failed);
        }
    }

    /**
     * 图片缩放，主要针对小图片
     *
     * @param url 本地图片绝对路径
     * @return 压缩后显示的图片
     */
    private Bitmap createScaledBitmap(String url) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);

        try {
            if (options.outWidth < IMAGE_MIN_WIDTH && options.outHeight < IMAGE_MIN_HEIGHT) {
                //小图片放大
                return scaleBitmap(url, options);

            } else if (options.outWidth > IMAGE_MAX_WIDTH || options.outHeight > IMAGE_MAX_HEIGHT) {
                //大图，采样压缩处理
                float widthRatio = Math.round((float) options.outWidth / (float) IMAGE_MAX_WIDTH );
                float heightRatio = Math.round((float) options.outHeight / (float) IMAGE_MAX_HEIGHT);
                options.inSampleSize = (int) Math.max(widthRatio, heightRatio);
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(url, options);
            } else {
                return BitmapFactory.decodeFile(url);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用于对小图片进行放大
     * @param url
     * @param options
     * @return
     */
    private Bitmap scaleBitmap(String url, BitmapFactory.Options options) {
        //小图片，放大至最小宽高
        Bitmap bitmap = null;
        Bitmap scaledBitmap = null;
        try {
            int reqWidth = IMAGE_MIN_WIDTH;
            int reqHeight = IMAGE_MIN_HEIGHT;
            int outWidth = options.outWidth;
            int outHeight = options.outHeight;
            if (outWidth <= 0 || outHeight <= 0) {
                return null;
            }

            bitmap = BitmapFactory.decodeFile(url);
            float ratio = (float)outWidth / (float)outHeight;
            if (ratio > 1.0f) {
                reqHeight = (int) ((float)reqWidth / ratio);
            } else {
                reqWidth = (int) ((float)reqHeight * ratio);
            }
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            //no nothing
        } finally {
            if (scaledBitmap != null && !scaledBitmap.equals(bitmap)) {
                bitmap.recycle();
            }
        }
        return scaledBitmap;
    }

    /**
     * 获取.9背景图
     *
     * @param width 背景图片宽度
     * @param height 高度
     * @return .9根据压缩后生成的图片
     */
    private Bitmap getNineBitmap(int width, int height) {
        Drawable drawable = getBackground();
        if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(
                    width, height,
                    drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    @Override
    public void setImageResource(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        setImageBitmap(bitmap);
    }

    @Override
    public void setImageBitmap(Bitmap srcBitmap) {
        if (srcBitmap == null) {
            super.setImageBitmap(null);
            return;
        }

        //.9图背景添加
        int width = srcBitmap.getWidth() > IMAGE_MAX_WIDTH ? IMAGE_MAX_WIDTH : srcBitmap.getWidth();
        int height = srcBitmap.getHeight() > IMAGE_MAX_HEIGHT ? IMAGE_MAX_HEIGHT : srcBitmap.getHeight();
        Bitmap destBitmap = getNineBitmap(width, height);

        if (destBitmap != null && width > 0 && height > 0) {

            try {
                Bitmap output = Bitmap.createBitmap(width, height, srcBitmap.getConfig());

                Canvas canvas = new Canvas(output);
                Paint paint = new Paint();
                paint.setAntiAlias(true);

                canvas.drawBitmap(srcBitmap, 0, 0, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(destBitmap, 0, 0, paint);

                super.setImageBitmap(output);
            } catch (OutOfMemoryError e) {
                //do nothing
            }
        } else {
            super.setImageBitmap(srcBitmap);
        }
    }
}
