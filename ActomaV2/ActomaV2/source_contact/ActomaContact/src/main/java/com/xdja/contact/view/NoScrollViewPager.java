package com.xdja.contact.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wanghao on 2015/7/8.
 */
public class NoScrollViewPager extends ViewPager {

    private boolean isCanScroll = true;

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean isCanScroll){
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        if(isCanScroll){
            super.scrollTo(x, y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       /* if (isCanScroll)
            return false;
        else
            return super.onTouchEvent(ev);*/
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*if (isCanScroll)
            return false;
        else
            return super.onInterceptTouchEvent(ev);*/
        return super.onInterceptTouchEvent(ev);
    }
}

