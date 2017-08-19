package com.xdja.imp.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.xdja.imp.util.DisplayUtils;


/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频录制按钮     <br>
 * 创建时间：2017/2/9       <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */

public class CustomView extends View {

  // 设置画笔相关属性
  private static final int BG_STROKE_WIDTH = 51;
  // 设置画笔相关属性
  private static final int CIR_STROKE_WIDTH = 12;
  private final CircularProgressDrawable mDrawable;
  private final Paint mBgPaint;


  public CustomView(Context context) {
    this(context, null);
  }

  public CustomView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mDrawable = new CircularProgressDrawable(Color.argb(0xcd, 0xba, 0xa2, 0x70),
            DisplayUtils.dp2px(context, CIR_STROKE_WIDTH));
    mDrawable.setCallback(this);

    mBgPaint = new Paint();
    mBgPaint.setAntiAlias(true);
    mBgPaint.setStrokeWidth(BG_STROKE_WIDTH);
    mBgPaint.setStyle(Paint.Style.FILL);
    mBgPaint.setColor(Color.argb(0x9a, 0xff, 0xff, 0xff));

  }

  public void startProgressBar (){
      mDrawable.start();
  }

  public void stopProgressBar (){
    mDrawable.stop();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldW, int oldH) {
    super.onSizeChanged(w, h, oldW, oldH);
    mDrawable.setBounds(0, 0, w, h);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    int width = this.getWidth();
    int height = this.getHeight();

    if (width != height) {
      width = Math.min(width, height);
    }
    // 实心圆
    canvas.drawCircle(width/2,width/2,width/2 - 1.5f,mBgPaint);
    mDrawable.draw(canvas);
  }

  @Override
  protected boolean verifyDrawable(@NonNull Drawable who) {
    return who == mDrawable || super.verifyDrawable(who);
  }


  public void startAnim() {
    AnimatorSet set = new AnimatorSet();
    set.playTogether(
            ObjectAnimator.ofFloat(this, "scaleX", 1, 1.6f),
            ObjectAnimator.ofFloat(this, "scaleY", 1, 1.6f)
    );
    set.setDuration(250).start();
  }

  public void stopAnim() {
    AnimatorSet set = new AnimatorSet();
    set.playTogether(
            ObjectAnimator.ofFloat(this, "scaleX", 1.6f, 1f),
            ObjectAnimator.ofFloat(this, "scaleY", 1.6f, 1f)
    );
    set.setDuration(250).start();
  }
}