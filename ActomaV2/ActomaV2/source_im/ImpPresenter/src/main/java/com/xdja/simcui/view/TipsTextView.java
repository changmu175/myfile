package com.xdja.simcui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 听筒模式提示框
 * Created by xdjaxa on 2016/11/1.
 */

public class TipsTextView extends TextView {

    private static final long SHOW_TIME = 1000;   //动画显示时间

    public TipsTextView(Context context) {
        super(context);
    }

    public TipsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TipsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 显示提示框
     */
    public void showTips() {
        setVisibility(VISIBLE);

        doAlphaAnimation();
        /*
        //改变透明度
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(800);
        startAnimation(alphaAnimation);

        //动画监听
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doAlphaAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });*/
    }

    private void doAlphaAnimation() {

        Observable.timer(SHOW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return getVisibility() == VISIBLE;
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {

                        setVisibility(View.GONE);

                        //改变透明度
                        /*AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                        alphaAnimation.setDuration(800);
                        startAnimation(alphaAnimation);//开启动画

                        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setVisibility(View.GONE);
                            }
                        });*/
                    }
                });
    }
}
