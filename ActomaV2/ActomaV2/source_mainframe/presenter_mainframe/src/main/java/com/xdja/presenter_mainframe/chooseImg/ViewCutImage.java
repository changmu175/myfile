package com.xdja.presenter_mainframe.chooseImg;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.uitl.BitmapUtil;
import com.xdja.comm.uitl.GcMemoryUtil;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.ui.ActivityView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by geyao
 * 裁剪图片
 */
@SuppressWarnings({"NumericCastThatLosesPrecision"})
@ContentView(value = R.layout.activity_view_cut_image)
public class ViewCutImage extends ActivityView<CutImageCommand> implements CutImageVu {
    /**
     * 显示图片的imageview
     */
    @Bind(R.id.cutimg_img)
    ImageView cutimgImg;
    /**
     * 使用button
     */
    @Bind(R.id.cutimage_use)
    Button cutimageUse;
    /**
     * 裁剪框
     */
    @Bind(R.id.clipview)
    ClipView clipview;

    @Bind(R.id.cur_image_root)
    View pageRoot;
//    /**
//     * 内容区域顶部的距离
//     */
//    private int topMargin = ViewStatus.getViewStatus().getContentTop();
//    /**
//     * DisplayMetrics
//     */
//    private DisplayMetrics metrics;
    /**
     * 裁剪框宽度
     */
    private int clipSize;


//    /**
//     * 最大缩放尺寸
//     */
//    private float maxScale;


    /**
     * 限制缩放的最小尺寸
     */
    private float minWidth;
    private float minHeight;

    private Bitmap srcBitmap;

    private ImageTouchController touchController;

    private static final int DP = 40;
    private static final int POST_DELAY = 300;
    private static final int BASE_DEGREE = 90;

    @Override
    public void onCreated() {
        super.onCreated();
        if (cutimageUse != null) cutimageUse.setEnabled(false);
    }

    /**
     * 设置图片
     *
     * @param url 图片地址
     */
    @Override
    public void setImage(String url) {

        minWidth = DeviceUtil.dp2pxFloat(getActivity(), DP);
        minHeight = DeviceUtil.dp2pxFloat(getActivity(), DP);
        int degree = readPictureDegree(url);
        //设置裁剪框touch监听
        touchController = new ImageTouchController();
        cutimgImg.setOnTouchListener(touchController);
        srcBitmap = BitmapUtil.getBitmapByPath(url, 2);
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-06 add. fix bug 3695 . review by wangchao1. Start
        if (degree > 0) {
            srcBitmap = rotate(srcBitmap, degree);
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-06 add. fix bug 3695 . review by wangchao1. End
        if (srcBitmap != null) {
            clipview.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cutimgImg.setImageBitmap(srcBitmap);
                    clipSize = postImage2Center(srcBitmap.getWidth(), srcBitmap.getHeight(), touchController);
                    if (cutimageUse != null) cutimageUse.setEnabled(true);
                }
            }, POST_DELAY);// 延时300毫秒设置图片，不然获取不到clipview的宽和高，无法计算transX、transY的值
        }

//        //显示图片
//        Glide.with(getActivity()).load(url).listener(new RequestListener<String, GlideDrawable>() {
//            @Override
//            public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
//                return false;
//            }
//
//            //图片加载完成
//            @Override
//            public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target,
// boolean b, boolean b1) {
//                int height = glideDrawable.getIntrinsicHeight();
//                int width = glideDrawable.getIntrinsicWidth();
//                clipViewWidth = postImage2Center(width, height, touchController);
//
////                srcBitmap = Bitmap.createBitmap(width, height, glideDrawable.getOpacity() != PixelFormat.OPAQUE ?
// Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
////                Canvas canvas = new Canvas(srcBitmap);
////                glideDrawable.setBounds(0, 0, width, height);
////                glideDrawable.draw(canvas);
////                //设置居中显示
////                clipViewWidth = postImage2Center(srcBitmap, touchController);
//                //设置使用按钮可点击
//                cutimageUse.setEnabled(true);
//                return false;
//            }
//        }).into(cutimgImg);
    }

    private int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface
                    .ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = BASE_DEGREE;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = BASE_DEGREE*2;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = BASE_DEGREE*3;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public Bitmap rotate(Bitmap b, int degrees) {
        if (degrees == 0) {
            return b;
        }
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth(), (float) b.getHeight());
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return b;
    }

    /**
     * 裁剪图片
     */
    @OnClick(R.id.cutimage_use)
    public void can() {
//        //实例化DisplayMetrics
//        DisplayMetrics metrics = new DisplayMetrics();
//        //获取屏幕宽高
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        //获取屏幕的高度
//        int widthPixels = metrics.widthPixels;
//        //获取toolbar的高度
//        int toolBarHeight = ((AppCompatActivity) getActivity()).getSupportActionBar()
//                .getHeight();
//        //获取状态栏高度
//        Rect frame = new Rect();
//        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        int res = frame.bottom - frame.top;
        //裁剪图片
        //[S]modify by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
        cutImage();
//        getCommand().cutImage(cutimgImg,
//                clipview.getClipViewX(), clipview.getClipViewY(),
//                clipViewWidth,
//                clipViewWidth);
        //[E]modify by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    //[S]add by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
    private void cutImage() {
        //[S]add by lixiaolong on 20160908. fix bug 3724. review by myself.
        if (srcBitmap == null || cutimgImg == null || cutimgImg.getDrawable() == null || clipview == null || touchController == null) {
            XToast.show(getActivity(), getStringRes(R.string.reselect_image));
            return;
        }
        //[E]add by lixiaolong on 20160908. fix bug 3724. review by myself.
        try {
            Bitmap cutBmp;
            int cltX = clipview.getClipViewX();
            int cltY = clipview.getClipViewY();
            int crbX = cltX + clipSize;
            int crbY = cltY + clipSize;

            Rect rect = cutimgImg.getDrawable().getBounds();
            Matrix matrix = touchController.getMatrix();
            float[] val = new float[9];
            matrix.getValues(val);// matrix 回填的矩阵数据
            int ltX = (int) val[2];// postTranslate后的x轴
            int ltY = (int) val[5];// postTranslate后的y轴
            float realW = rect.width() * val[0];// val[0]是scale的比率
            float realH = rect.height() * val[4];// val[0]是scale的比率
            float rbX = ltX + realW;
            float rbY = ltY + realH;

            if (rbX < cltX || ltX > crbX || rbY < cltY || ltY > crbY) {
                XToast.show(getActivity(), getStringRes(R.string.send_the_image_to_cutbox));
                return;
            }
            //[S]modify by lixiaolong on 20160912. fix bug 3727. review by gbc.
//            int x = (cltX - ltX) > 0 ? (cltX - ltX) : 0;
//            int y = (cltY - ltY) > 0 ? (cltY - ltY) : 0;
//            int tmpS = Math.abs(crbX - ltX) < Math.abs(crbY - ltY) ? Math.abs(crbX - ltX) : Math.abs(crbY - ltY);
//            int s = tmpS < clipSize ? tmpS : clipSize;
//            Bitmap bm = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);

            int x = cltX > ltX ? cltX : ltX;
            int y = cltY > ltY ? cltY : ltY;
            int rbx = rbX < crbX ? (int) rbX : crbX;
            int rby = rbY < crbY ? (int) rbY : crbY;
            int tmpS = Math.abs(rbx - x) < Math.abs(rby - y) ? Math.abs(rbx - x) : Math.abs(rby - y);
            int s = tmpS < clipSize ? tmpS : clipSize;
            cutimgImg.setDrawingCacheEnabled(true);
            Bitmap bm = cutimgImg.getDrawingCache();
            //[E]modify by lixiaolong on 20160912. fix bug 3727. review by gbc.
            int bw = bm.getWidth();
            int bh = bm.getHeight();
            int min = bw < bh ? bw : bh;
            if (min < s) {
                s = min;
            }
            if (x + s <= bw && y + s <= bh) {
                cutBmp = Bitmap.createBitmap(bm, x, y, s, s);
            } else {
                int wh = bw - x < bh - y ? bw - x : bh - y;
                cutBmp = Bitmap.createBitmap(bm, x, y, wh, wh);
            }
            cutimgImg.destroyDrawingCache();
            cutimgImg.setDrawingCacheEnabled(false);
            if (!bm.isRecycled()) {
                bm.recycle();
            }
            getCommand().cutImage(cutBmp);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            XToast.show(getActivity(), getStringRes(R.string.image_cut_failed));
        }
    }
    //[E]add by lixiaolong on 20160901. fix bug 1534. review by wangchao1.

    /**
     * 将原始图片居中
     *
     * @return 矩形区域的大小
     */
    private int postImage2Center(int width, int height, ImageTouchController touchController) {
//        int width = loadedImage.getWidth();
//        int height = loadedImage.getHeight();
        //矩形区域的宽度
        int clipViewWidth = clipview.getW();
        //计算缩放倍数
        float scale = width <= height ? (float) clipViewWidth / (float) width : (float) clipViewWidth / (float) height;

        float maxScale;
        if (width <= height) {//宽短高长
            height = (int) ((float) height * scale);
            width = clipViewWidth;

            maxScale = minWidth / width;

        } else {//宽长高短
            width = (int) ((float) width * scale);
            height = clipViewWidth;

            maxScale = minHeight / width;
        }

        int transX;
        int transY;

//        //获取toolbar的高度
//        int toolBarHeight = ((AppCompatActivity) getActivity()).getSupportActionBar()
//                .getHeight();
//        //获取状态栏高度
//        Rect frame = new Rect();
//        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        //实例化DisplayMetrics
//        DisplayMetrics metrics = new DisplayMetrics();
//        //获取屏幕宽高
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (width <= clipViewWidth) {
            transX = clipview.getClipViewX();
        } else {
            transX = clipview.getClipViewX() - (width - clipViewWidth) / 2;
        }
        if (height <= clipViewWidth) {
            transY = clipview.getClipViewY();
        } else {
            transY = clipview.getClipViewY() - (height - clipViewWidth) / 2;
        }

        Matrix m = new Matrix();
        m.postScale(scale, scale);
        m.postTranslate(transX, transY);
        cutimgImg.setImageMatrix(m);
        touchController.setMatrix(m);
        touchController.setTargetScale(maxScale);
        return clipViewWidth;
    }

    //[Start]YangShaoPeng<mailto://ysp@xdja.com> 2016-08-23 add. fix bug #3185 . review by wangchao1.
    @Override
    public void onDestroy() {
        GcMemoryUtil.clearMemory(pageRoot);
        if (cutimgImg != null) {
            cutimgImg.setImageResource(0);
            cutimgImg.setImageBitmap(null);
            cutimgImg = null;
        }
        if (srcBitmap != null) {
            srcBitmap.recycle();
            srcBitmap = null;
        }
    }
    //[End]YangShaoPeng<mailto://ysp@xdja.com> 2016-08-23 add. fix bug #3185 . review by wangchao1.

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_activity_view_cut_image);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
