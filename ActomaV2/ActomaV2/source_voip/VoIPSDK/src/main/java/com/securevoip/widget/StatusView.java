package com.securevoip.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xdja.voipsdk.R;

import webrelay.VOIPManager;
import webrelay.bean.StatusCode;

/**
 * Created by gouhao on 2016/3/23.
 */
public class StatusView extends LinearLayout {
    private static final int DEFAULT_TEXT_SIZE = 18;
    private static final int DEFAULT_PROGRESS_WIDTH = 50;
    private static final int DEFAULT_PROGRESS_HEIGHT = 50;
    private static final int DISSMISS_SELF = 111;
    private ProgressBar progressBar;
    private TextView textView;

    private float progressMarginRight;
    private Drawable progressDrawable;
    private float textSize;
    private int textColor;
    private CharSequence text;
    private float progressBarWidth, progressBarHeight;
    private int progressVisibility, textVisibility;
    public StatusView(Context context) {
        super(context, null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributeSet(context, attrs);
        createView();
    }

    private void getAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusView);
        progressMarginRight = a.getDimensionPixelSize(R.styleable.StatusView_progressMarinRight, 0);
        progressDrawable = a.getDrawable(R.styleable.StatusView_progressDrawable);
        textSize = a.getDimensionPixelSize(R.styleable.StatusView_textSize, DEFAULT_TEXT_SIZE);
        textColor = a.getColor(R.styleable.StatusView_textColor, Color.BLACK);
        text = a.getText(R.styleable.StatusView_text);
        progressBarWidth = a.getDimensionPixelSize(R.styleable.StatusView_progressBarWidth,
                DEFAULT_PROGRESS_WIDTH);
        progressBarHeight = a.getDimensionPixelSize(R.styleable.StatusView_progressBarHeight,
                DEFAULT_PROGRESS_HEIGHT);
        progressVisibility = a.getInt(R.styleable.StatusView_progressVisibility, VISIBLE);
        textVisibility = a.getInt(R.styleable.StatusView_textVisibility, VISIBLE);
        a.recycle();
    }

    private void createView() {
        progressBar = new ProgressBar(getContext());
        if(progressDrawable != null) {
            progressBar.setProgressDrawable(progressDrawable);
        }
        progressBar.setVisibility(progressVisibility);
        LinearLayout.LayoutParams params = new LayoutParams((int)progressBarWidth,
                (int)progressBarHeight);
        params.rightMargin = (int) progressMarginRight;
        addView(progressBar, params);

        textView = new TextView(getContext());
        textView.getPaint().setTextSize(textSize);
        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setVisibility(textVisibility);
        addView(textView);
    }

    public float getProgressMarginRight() {
        return progressMarginRight;
    }

    public void setProgressMarginRight(float progressMarginRight) {
        this.progressMarginRight = progressMarginRight;
        LinearLayout.LayoutParams layoutParams = (LayoutParams) progressBar.getLayoutParams();
        layoutParams.rightMargin = (int) progressMarginRight;
        progressBar.setLayoutParams(layoutParams);
    }

    public Drawable getProgressDrawable() {
        return progressDrawable;
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        this.progressDrawable = progressDrawable;
        progressBar.setProgressDrawable(progressDrawable);
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textView.setTextSize(textSize);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textView.setTextColor(textColor);
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        textView.setText(text);
    }

    public float getProgressBarWidth() {
        return progressBarWidth;
    }

    public float getProgressBarHeight() {
        return progressBarHeight;
    }

    public void setProgressBarWidthHeight(float progressBarWidth, float progressBarHeight) {
        this.progressBarWidth = progressBarWidth;
        this.progressBarHeight = progressBarHeight;
        LinearLayout.LayoutParams params = (LayoutParams) progressBar.getLayoutParams();
        params.width = (int) progressBarWidth;
        params.height = (int) progressBarHeight;
        progressBar.setLayoutParams(params);

    }

    public int getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(int progressVisibility) {
        this.progressVisibility = progressVisibility;
        progressBar.setVisibility(progressVisibility);
    }

    public int getTextVisibility() {
        return textVisibility;
    }

    public void setTextVisibility(int textVisibility) {
        this.textVisibility = textVisibility;
        textView.setVisibility(textVisibility);
    }
    @SuppressLint("AndroidLintHandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISSMISS_SELF:
                    //wxf@xdja.com 2016-09-18 add. fix bug 3981 . review by mengbo. Start
                    VOIPManager.getInstance().setCallSessionErrCode(StatusCode.SUCCESS);
                    //wxf@xdja.com 2016-09-18 add. fix bug 3981 . review by mengbo. End
                    setVisibility(GONE);
                    break;
            }
        }
    };

    public void dismissSelfDelay(final int delay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    handler.obtainMessage(DISSMISS_SELF).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void setVisibility(int visibility) {
        setProgressVisibility(visibility);
        setTextVisibility(visibility);
        super.setVisibility(visibility);
    }
}
