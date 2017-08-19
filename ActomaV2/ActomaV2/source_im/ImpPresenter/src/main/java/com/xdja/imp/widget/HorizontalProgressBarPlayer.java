package com.xdja.imp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.xdja.imp.R;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频播放进度条     <br>
 * 创建时间：2017/2/10        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class HorizontalProgressBarPlayer extends ProgressBar {

    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_RADIUS = 6;

    private final Paint mPaint = new Paint();

    private final int mCircleRadius = (int)dp2px(DEFAULT_RADIUS);
    // 覆盖进度高度
    private int mReachedProgressBarHeight = (int)dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);
    // 未覆盖进度高度
    private int mUnReachedProgressBarHeight = (int)dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    // 未覆盖进度颜色
    private int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;

    private int mRealWidth;

    public HorizontalProgressBarPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        obtainStyledAttributes(attrs);
        mPaint.setAntiAlias(true);
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        // 获取自定义属性
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBarWithNumber);
        mUnReachedBarColor = attributes.getColor(R.styleable.HorizontalProgressBarWithNumber_progress_unreached_color, DEFAULT_COLOR_UNREACHED_COLOR);
        mReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithNumber_progress_reached_bar_height, mReachedProgressBarHeight);
        mUnReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithNumber_progress_unreached_bar_height, mUnReachedProgressBarHeight);
        attributes.recycle();
        setPadding(0, 18, 0, 18);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            float textHeight = (mPaint.descent() - mPaint.ascent());
            result = (int) (getPaddingTop() + getPaddingBottom() + Math.max(
                    Math.max(mReachedProgressBarHeight, mUnReachedProgressBarHeight), Math.abs(textHeight)));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        float radio = getProgress() * 1.0f / getMax();
        float progressPosX = (int) (mRealWidth * radio);

        if (progressPosX > -1) {
            mPaint.setColor(Color.argb(0xff, 0xff, 0xff, 0xff));
            //noinspection SuspiciousNameCombination
            mPaint.setStrokeWidth(mReachedProgressBarHeight);
            canvas.drawLine(0, 0, progressPosX, 0, mPaint);
        }

        // 未覆盖的进度
        mPaint.setColor(mUnReachedBarColor);
        //noinspection SuspiciousNameCombination
        mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
        canvas.drawLine(progressPosX, 0, mRealWidth + getPaddingRight(), 0, mPaint);

        // 圆
        mPaint.setColor(Color.argb(0xff, 0xff, 0xff, 0xff));
        if ( progressPosX+mCircleRadius >= mRealWidth-mCircleRadius ){
            canvas.drawCircle(mRealWidth - mCircleRadius, 0, mCircleRadius, mPaint);
        } else {
            canvas.drawCircle(progressPosX+mCircleRadius, 0, mCircleRadius, mPaint);
        }
        canvas.restore();
    }

    /**
     * dp 2 px
     */
    private float dp2px(float dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

}
