package com.xdja.imp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.xdja.imp.R;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频上传下载进度条     <br>
 * 创建时间：2017/2/9       <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public class CircleProgressBar extends View {

    /**
     * circle radius
     */
    private int radius = -1;

    /**
     * max progress
     */
    private int max = -1;

    /**
     * current progress
     */
    private int progress = -1;

    private final Paint paint = new Paint();

    public CircleProgressBar(Context context) {
        super(context);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.cpb);
        radius = array.getDimensionPixelSize(R.styleable.cpb_radius, -1);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (radius != -1 && getVisibility() == VISIBLE) {
            paint.setAntiAlias(true);

            float angle = 0;
            if (max > 0 && progress >= 0) {
                angle = (float)progress / (float)max * 360f;
            }
            paint.setColor(Color.argb(0x73, 0xff, 0xff, 0xff));
            @SuppressLint("DrawAllocation") RectF oval2 = new RectF(((float) getWidth() - radius * 2f) / 2f,
                    ((float) getHeight() - radius * 2f) / 2f,
                    (float) getWidth() - ((float) getWidth() - radius * 2f) / 2f,
                    (float) getHeight() - ((float) getHeight() - radius * 2f) / 2f);
            canvas.drawArc(oval2, 270, angle, true, paint);
        }

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            invalidate();
        }
    }

    /**
     * set max progress value
     * @param max 最大进度
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * set progress value
     * @param progress 进度
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}