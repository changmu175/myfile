package com.xdja.imp.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Property;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频录制进度动画     <br>
 * 创建时间：2017/2/9       <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class CircularProgressDrawable extends Drawable
        implements Animatable {

    private static final Interpolator SWEEP_INTERPOLATOR = new LinearInterpolator();
    private static final int SWEEP_ANIMATOR_DURATION = 10000;
    private static final int SWEEP_ANGLE_INIT = -90;

    private final RectF fBounds = new RectF();

    private ObjectAnimator mObjectAnimatorSweep;
    private final Paint mPaint;
    private float mCurrentSweepAngle;
    private final float mBorderWidth;
    private boolean mRunning;

    public CircularProgressDrawable(int color, float borderWidth) {
        mBorderWidth = borderWidth;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setColor(color);

        setupAnimations();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float startAngle = SWEEP_ANGLE_INIT;
        float sweepAngle = mCurrentSweepAngle;
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        fBounds.left =  mBorderWidth / 2f + 1.5f;
        fBounds.top =  mBorderWidth / 2f + 1.5f;
        fBounds.right = bounds.right - mBorderWidth / 2f - 1.5f;
        fBounds.bottom = bounds.bottom - mBorderWidth / 2f - 1.5f;
    }


    private final Property<CircularProgressDrawable, Float> mSweepProperty
            = new Property<CircularProgressDrawable, Float>(Float.class, "arc") {
        @Override
        public Float get(CircularProgressDrawable object) {
            return object.getCurrentSweepAngle();
        }

        @Override
        public void set(CircularProgressDrawable object, Float value) {
            object.setCurrentSweepAngle(value);
        }
    };

    private void setupAnimations() {

        mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, mSweepProperty, 360f );
        mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mObjectAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);
        mObjectAnimatorSweep.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }
        mRunning = true;
        mObjectAnimatorSweep.start();
        invalidateSelf();
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }
        mRunning = false;
        mObjectAnimatorSweep.cancel();
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    private void setCurrentSweepAngle(float currentSweepAngle) {
        mCurrentSweepAngle = currentSweepAngle;
        invalidateSelf();
    }

    private float getCurrentSweepAngle() {
        return mCurrentSweepAngle;
    }

}