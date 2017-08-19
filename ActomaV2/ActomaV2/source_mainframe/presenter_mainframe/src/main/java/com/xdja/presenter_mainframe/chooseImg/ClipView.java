package com.xdja.presenter_mainframe.chooseImg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.presenter_mainframe.R;


/**
 * <p>summary:自定义用于裁剪的矩形区域</p>
 * <p>description:</p>
 * <p>author:fanjiandong</p>
 * <p>time:2015/4/29 13:30</p>
 */
public class ClipView extends View {

//    private int margin = 80;
    /**
     * clipView边长
     */
    private int w = 200;
    /**
     * clipView x坐标
     */
    private static int clipViewX = 0;
    /**
     * clipView y坐标
     */
    private static int clipViewY = 0;

    /**
     *
     */
    private PointF pointF = new PointF();


    //阴影的画笔
    Paint paintShadow = new Paint();
    //矩形的边框画笔
    Paint paintLine = new Paint();

    //    public int getMargin() {
//        return margin;
//    }

    /**
     * 获取clipView边长
     *
     * @return
     */
    public int getW() {
        return w;
    }

    /**
     * 获取clipView x坐标
     *
     * @return
     */
    public int getClipViewX() {
        return clipViewX;
    }

    /**
     * 获取clipView y坐标
     *
     * @return
     */
    public int getClipViewY() {
        return clipViewY;
    }

    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrValue(attrs);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrValue(attrs);
    }

    private void getAttrValue(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClipView);
        try {
//            margin = a.getDimensionPixelSize(R.styleable.ClipView_margin, margin);
            w = a.getDimensionPixelSize(R.styleable.ClipView_width, w);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(!ObjectUtil.objectIsEmpty(a))
                a.recycle();
        }
    }

    /**
     * 获取切图
     *
     * @return 切图bitmap
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public Bitmap getCutBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap source = this.getDrawingCache();
        return Bitmap.createBitmap(source, (int) pointF.x, (int) pointF.y, w, w);
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //屏幕宽度
        int width = this.getWidth();
        //屏幕高度
        int height = this.getHeight();

        //阴影的颜色
        paintShadow.setColor(getResources().getColor(R.color.blade_black_50));

        //矩形的边框画笔颜色
        paintLine.setColor(getResources().getColor(R.color.base_title_gold));
//
//        int widthRec = width - margin * 2;
//        int marginH = (height - widthRec) / 2;
//        int marginW = margin;

//        if (height >= width) {
//            widthRec = width - margin * 2;
//            marginH = (height - widthRec) / 2;
//            marginW = margin;
//        }
//        else {
//            widthRec = height - margin * 2;
//            marginH = margin;
//            marginW = (width - widthRec) / 2;
//
//        }

//        //裁剪框外上部阴影蒙板
//        canvas.drawRect(0, 0, width, marginH, paintShadow);
//        //裁剪框外下部阴影蒙板
//        canvas.drawRect(0, marginH + widthRec, width, height, paintShadow);
//        //裁剪框外左部阴影蒙板
//        canvas.drawRect(0, marginH, marginW, marginH + widthRec, paintShadow);
//        //裁剪框外右部阴影蒙板
//        canvas.drawRect(marginW + widthRec, marginH, width, marginH + widthRec, paintShadow);
//
//        //裁剪框上边线
//        canvas.drawLine(marginW, marginH, marginW + widthRec, marginH, paintLine);
//        //裁剪框左边线
//        canvas.drawLine(marginW, marginH, marginW, marginH + widthRec, paintLine);
//        //裁剪框右边线
//        canvas.drawLine(marginW + widthRec, marginH, marginW + widthRec, marginH + widthRec, paintLine);
//        //裁剪框下边线
//        canvas.drawLine(marginW, marginH + widthRec, marginW + widthRec, marginH + widthRec, paintLine);

//        pointF.set(marginW, marginH);
//        actWidth = widthRec;

        int widthRec = w;
        int marginH = (height - w) / 2;
        int marginW = (width - w) / 2;

        clipViewX = marginW;
        clipViewY = marginH;

        //裁剪框外上部阴影蒙板
        canvas.drawRect(0, 0, width, marginH, paintShadow);
        //裁剪框外下部阴影蒙板
        canvas.drawRect(0, marginH + widthRec, width, height, paintShadow);
        //裁剪框外左部阴影蒙板
        canvas.drawRect(0, marginH, marginW, marginH + widthRec, paintShadow);
        //裁剪框外右部阴影蒙板
        canvas.drawRect(marginW + widthRec, marginH, width, marginH + widthRec, paintShadow);

        //裁剪框上边线
        canvas.drawLine(marginW, marginH, marginW + widthRec, marginH, paintLine);
        //裁剪框左边线
        canvas.drawLine(marginW, marginH, marginW, marginH + widthRec, paintLine);
        //裁剪框右边线
        canvas.drawLine(marginW + widthRec, marginH, marginW + widthRec, marginH + widthRec, paintLine);
        //裁剪框下边线
        canvas.drawLine(marginW, marginH + widthRec, marginW + widthRec, marginH + widthRec, paintLine);
    }
}
