package com.securevoip.ui.incall.locker.slidingtab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by gbc on 2015/7/23.
 */
public class IndicateView extends LinearLayout {


    public IndicateView(Context context) {
        super(context);

    }

    public IndicateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndicateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("AndroidLintNewApi")
    public IndicateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getPaddingLeft();
        int height = getPaddingTop();

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

        }

        width += getPaddingRight();
        height += getChildAt(getChildCount() - 1).getMeasuredHeight()
                + getPaddingBottom();

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
