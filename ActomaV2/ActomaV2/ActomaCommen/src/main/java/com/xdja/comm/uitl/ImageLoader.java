package com.xdja.comm.uitl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xdja.comm.server.ActomaController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ycm on 2017/6/7.
 * Description: 静态图片加载器
 * Modified by:
 */
public class ImageLoader {

    private static final String TAG = "ImageLoader";

    /**
     * 缓存文件路径
     */
    private static final String IMAGE_CACHE_DIR = "image_manager_disk_cache";

    /**
     * 线程个数
     */
    private static final int THREAD_COUNT = 2;
    /**
     * 图片缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 轮询的线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHandler未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;

    private static ImageLoader mInstance;

    private File mRootDirectory;

    private ImageLoader(int threadCount) {
        init(threadCount);
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static ImageLoader getInstance() {

        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(THREAD_COUNT);
                }
            }
        }
        return mInstance;
    }

    private void init(int threadCount) {
        // loop thread
        mPoolThread = new Thread() {
            @SuppressLint("HandlerLeak")
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Runnable task = getTask();
                        if (task != null) {
                            mThreadPool.execute(task);
                        }
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
HandlerThread
        mRootDirectory = new File(ActomaController.getApp().getCacheDir(), IMAGE_CACHE_DIR);
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<>();
    }

    /**
     * 清除缓存,并释放单例对象
     */
    public void clearCache() {
        clearLruCache();
        mInstance = null;

    }

    public Builder crateBuilder() {
        return new Builder();
    }

    public class Builder {

        private String filePath;

        private int maxWidth;

        private int maxHeight;

        private @DrawableRes int errorResId, defaultResId;

        private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;

        private ImageView target;

        public Builder load(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder preLoad(int maxWidth, int maxHeight) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder error(@DrawableRes int errorResId) {
            this.errorResId = errorResId;
            return this;
        }

        public Builder placeholder(@DrawableRes int defaultResId) {
            this.defaultResId = defaultResId;
            return this;
        }

        public Builder fitCenter() {
            this.scaleType = ImageView.ScaleType.FIT_CENTER;
            return this;
        }

        public Builder centerCrop() {
            this.scaleType = ImageView.ScaleType.CENTER_CROP;
            return this;
        }

        public Builder centerInside() {
            this.scaleType = ImageView.ScaleType.CENTER_INSIDE;
            return this;
        }

        public Builder into(ImageView target) {
            this.target = target;
            return this;
        }

        public void build() {
            loadImage(filePath, target,
                    maxWidth, maxHeight,
                    errorResId, defaultResId,
                    scaleType);
        }

        public void build(int color) {
            loadImage(filePath, target, maxWidth, maxHeight, errorResId, defaultResId, scaleType, color);
        }
    }

    private void loadImage(String path, ImageView imageView,
                           int maxWidth, int maxHeight,
                           int errorResId, int defaultResId,
                           ImageView.ScaleType scaleType,
                           int color) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(errorResId);
            return;
        }
        // set tag
        imageView.setTag(path);

        //先从缓存中拿取
        Bitmap bm = getBitmapFromLruCache(getCacheKey(path, maxWidth, maxHeight));
        if (bm != null) {
            setImageBitmap(imageView, path, bm, scaleType);
        } else {
            //从缓存中拿取
            Bitmap cachedBitmap = getBitmapFromDiskCache(path, maxWidth, maxHeight);
            if (cachedBitmap != null) {
                //加入内存
                addBitmapToLruCache(getCacheKey(path, maxWidth, maxHeight), cachedBitmap);
                //设置图像
                setImageBitmap(imageView, path, cachedBitmap, scaleType);
                return;
            }
            //set default
            if (defaultResId > 0) {
                imageView.setImageResource(defaultResId);
            } else {
                imageView.setImageBitmap(null);
                if (color != -1) {
                    imageView.setBackgroundColor(color);
                }
            }
            //加入任务，生成缩略图
            makeImageThumbnail(path, imageView, maxWidth, maxHeight, scaleType);
        }
    }

    private void loadImage(String path, ImageView imageView,
                           int maxWidth, int maxHeight,
                           int errorResId, int defaultResId,
                           ImageView.ScaleType scaleType) {
        loadImage(path , imageView , maxWidth , maxHeight , errorResId , defaultResId , scaleType , -1);
    }

    /**
     * 生成缩略图
     *
     * @param path
     * @param imageView
     */
    private void makeImageThumbnail(final String path, final ImageView imageView,
                                    final int maxWidth, final int maxHeight,
                                    final ImageView.ScaleType scaleType) {
        TaskRunnable runnable = new TaskRunnable(path, imageView, maxWidth, maxHeight);
        runnable.setScaleType(scaleType);
        addTask(runnable);
    }

    /**
     * 设置图像
     *
     * @param imageView
     * @param path
     * @param bitmap
     */
    private void setImageBitmap(final ImageView imageView,
                                final String path,
                                final Bitmap bitmap,
                                final ImageView.ScaleType scaleType) {
        Observable.just(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (imageView.getTag().toString().equals(path)) {
                            imageView.setScaleType(scaleType);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHandler为null
            if (mPoolThreadHandler == null) {
                mSemaphore.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mTasks.contains(runnable)) {
            mTasks.remove(runnable);
        }
        mTasks.addFirst(runnable);
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mTasks.size() > 0) {
            return mTasks.removeFirst();
        }
        return null;
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Bitmap getBitmapFromLruCache(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return mLruCache.get(key);
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
    }

    /**
     * 清除LruCache缓存
     */
    private void clearLruCache() {
        if (mLruCache != null) {
            mLruCache.evictAll();
        }
        if (mTasks != null) {
            mTasks.clear();
        }
    }

    /**
     * 从硬盘中获取一张图片
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromDiskCache(String key, int maxWidth, int maxHeight) {
        try {
            File file = getFileForKey(key, maxWidth, maxHeight);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (OutOfMemoryError e) {
            //do nothing
        }
        return null;
    }

    /**
     * 图片保存至硬盘
     *
     * @param key
     * @param bitmap
     */
    private void putBitmapToDiskCache(String key, final Bitmap bitmap, final int maxWidth, final int maxHeight) {

        Observable.just(key)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String path) {
                        File saveFile = getFileForKey(path, maxWidth, maxHeight);
                        return Observable.just(saveBitmap(saveFile, bitmap));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {

                    }
                });
    }

    /**
     * 保存图片
     *
     * @param saveFile
     * @param bitmap
     * @return
     */
    private boolean saveBitmap(File saveFile, Bitmap bitmap) {
        if (saveFile == null || bitmap == null) {
            return false;
        }
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        BufferedOutputStream bos = null;
        boolean bSaveRet = false;
        try {
            //创建文件
            saveFile.createNewFile();
            //写入文件
            bos = new BufferedOutputStream(new FileOutputStream(saveFile));
            bSaveRet = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bSaveRet;
    }

    /**
     * 根据图片预览模式进行图片旋转
     *
     * @param degree 预览模式角度
     * @param bitmap 源图片
     * @return
     */
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
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
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
    private static int getDegree(String filePath) {
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
     * Returns a file object for the given cache key.
     */
    private File getFileForKey(String key, int maxWidth, int maxHeight) {
        return new File(mRootDirectory, getCacheKey(key, maxWidth, maxHeight));
    }

    /**
     * 文件缓存大小
     *
     * @param path      文件路径
     * @param maxWidth  文件宽度
     * @param maxHeight 文件高度
     * @return
     */
    private String getCacheKey(String path, int maxWidth, int maxHeight) {
        File file = new File(path);
        String cacheKey = new StringBuffer().append(path).append("#")
                .append(maxWidth).append("#").append(maxHeight).append("#")
                .append("#").append(file.lastModified()).toString();
        return Base64.encodeToString(cacheKey.getBytes(), Base64.DEFAULT);
    }

    /**
     * 根据ImageView获得适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    public ImageSize getImageViewWidth(ImageView imageView){
        ImageSize imageSize = new ImageSize();
        final DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width = params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getWidth(); // Get actual image width
        if (width <= 0){
            width = params.width; // Get layout width parameter
        }
        if (width <= 0){
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
        }
        if (width <= 0){
            width = displayMetrics.widthPixels;
        }

        int height = params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0){
            height = params.height; // Get layout height parameter
        }
        if (height <= 0){
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        }
        // parameter
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        imageSize.setWidth(width);
        imageSize.setHeight(height);
        return imageSize;
    }


    /**
     * 计算inSampleSize，用于压缩图片
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        try {
            return createBitmap(getDegree(pathName),
                    BitmapFactory.decodeFile(pathName, options));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName){
        int value = 0;
        try{
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE){
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public class ImageSize{
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public class TaskRunnable implements Runnable {

        private String filePath;
        private ImageView targetImg;
        private int maxWidth;
        private int maxHeight;
        private ImageView.ScaleType scaleType;

        public TaskRunnable(String filePath, ImageView targetImg) {
            this.filePath = filePath;
            this.targetImg = targetImg;
        }

        public TaskRunnable(String filePath, ImageView targetImg, int maxWidth, int maxHeight) {
            this.filePath = filePath;
            this.targetImg = targetImg;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
        }

        @Override
        public void run() {
            int reqWidth = maxWidth;
            int reqHeight = maxHeight;
            if (reqWidth == 0 || reqHeight == 0) {
                ImageSize imageSize = getImageViewWidth(targetImg);
                reqWidth = imageSize.getWidth();
                reqHeight = imageSize.getHeight();
            }
            Bitmap bm = decodeSampledBitmapFromResource(filePath, reqWidth,
                    reqHeight);
            //加入内存
            addBitmapToLruCache(getCacheKey(filePath, maxWidth, maxHeight), bm);
            //存入缓存
            putBitmapToDiskCache(filePath, bm, reqWidth, reqHeight);
            //设置图像
            setImageBitmap(targetImg, filePath, bm, scaleType);
            mPoolSemaphore.release();
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public ImageView getTargetImg() {
            return targetImg;
        }

        public void setTargetImg(ImageView targetImg) {
            this.targetImg = targetImg;
        }

        public int getMaxWidth() {
            return maxWidth;
        }

        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }

        public int getMaxHeight() {
            return maxHeight;
        }

        public void setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
        }

        public ImageView.ScaleType getScaleType() {
            return scaleType;
        }

        public void setScaleType(ImageView.ScaleType scaleType) {
            this.scaleType = scaleType;
        }

        @Override
        public String toString() {
            return "TaskRunnable{" +
                    "filePath='" + filePath + '\'' +
                    ", maxWidth=" + maxWidth +
                    ", maxHeight=" + maxHeight +
                    ", scaleType=" + scaleType +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TaskRunnable that = (TaskRunnable) o;

            if (maxWidth != that.maxWidth) return false;
            if (maxHeight != that.maxHeight) return false;
            if (!filePath.equals(that.filePath)) return false;
            return scaleType == that.scaleType;

        }

        @Override
        public int hashCode() {
            int result = filePath.hashCode();
            result = 31 * result + maxWidth;
            result = 31 * result + maxHeight;
            result = 31 * result + (scaleType != null ? scaleType.hashCode() : 0);
            return result;
        }
    }
}