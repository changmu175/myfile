package com.xdja.contact.view.arclayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.xdja.contact.R;
import com.xdja.dependence.uitls.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoyaxin on 2015/11/10.
 */
public class ArcLayoutAnimation implements View.OnClickListener {

    private ArcLayout arcLayout;
    private View menuLayout;
    private View fab;

    private AnimatorSet animSet = new AnimatorSet();


    private ArrayList<View> views;

    private boolean isShown = false;


    public ArcLayoutAnimation(View menuLayout, ArcLayout arcLayout) {
        this.menuLayout = menuLayout;
        this.arcLayout = arcLayout;
    }

    /**
     * 添加指定的Views
     */
    public void addViews(ArrayList<View> views) {

        if (this.menuLayout == null) {
            LogUtil.getUtils().e("ArcLayoutAnimation addViews fail, menuLayout can not be null!");
            return;
        }
        if (this.arcLayout == null){
            LogUtil.getUtils().e("ArcLayoutAnimation addViews fail, arcLayout can not be null!");
            return;
        }
        if (views == null){
            LogUtil.getUtils().e("ArcLayoutAnimation addViews fail, views can not be null!");
            return;
        }

        this.views = views;
        if (this.views.isEmpty())
            return;
        this.menuLayout.setOnClickListener(this);

        this.arcLayout.removeAllViews();


        View center = this.views.get(0);

        this.arcLayout.addView(center);
        this.views.remove(0);


        int size = this.views.size();
        for (int i = size - 1; i >= 0; i--) {
            View view = this.views.get(i);

            this.arcLayout.addView(view);
        }
        this.fab = arcLayout.getChildAt(0);
    }

    private Point center = new Point();

    public void setCenter(Activity activity, Point center) {
        if (isShown)
            return;
        if (views == null)
            return;
        int size = this.views.size();
        if (size == 0)
            return;

        int screenHeight = getScreenHeight(activity);
        int statusHeight = getStatusHeight(activity);
        int navigationBarHeight = getNavigationBarHeight(activity);
        int deltaY = screenHeight - center.y;


        int startAngle = 0;
        int sweepAngle = 0;
        int axisRadius = dip2px(activity, R.dimen.layout_child_offset_path);
        int btnRadius = dip2px(activity, R.dimen.btn_half_width);

        size = this.views.size();

        if (size == 1) {
            startAngle = 180;
            sweepAngle = 0;

        } else if (size == 2) {

            startAngle = 150;
            sweepAngle = 60;

        } else if (size == 3) {

            startAngle = 135;
            sweepAngle = 90;
        } else if (size == 4) {

            startAngle = 105;
            sweepAngle = 150;
            if (deltaY < this.arcLayout.getAxisRadius() + btnRadius) {
                sweepAngle = 110;
            }
        } else {
            startAngle = 90;
            sweepAngle = 180;
            if (deltaY < this.arcLayout.getAxisRadius() + btnRadius) {
                sweepAngle = 110;
                axisRadius = dip2px(activity, R.dimen.layout_child_offset_path1);
            }
        }
        if (deltaY < this.arcLayout.getAxisRadius() + btnRadius && size > 1) {
            startAngle = 170;
        }

        Arc.RIGHT.setStartAngle(startAngle);
        Arc.RIGHT.setSweepAngle(sweepAngle);
        this.arcLayout.setAxisRadius(axisRadius);

        this.arcLayout.setArc(Arc.RIGHT);
        this.center.set(center.x, center.y - statusHeight);
        arcLayout.computeOrigin(this.center.x, this.center.y);
        onFabClick(true);
    }

    private void showMenu() {
        if (animSet.isRunning()) {
            animSet.cancel();
        }
        menuLayout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();

        for (int i = 1, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(i));
        }


        animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();

    }

    private void hideMenu() {
        if (animSet.isRunning()) {
            animSet.cancel();
        }
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 1; i--) {
            animList.add(createHideItemAnimator(i));
        }

        animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
    }

    public boolean hide(){
        hideMenu();
        isShown = false;
        return true;
    }

    private Animator createShowItemAnimator(int index) {
        final View item = this.arcLayout.getChildAt(index);
        Point point = this.arcLayout.getPositionByIndex(index);
        float dx = this.center.x - point.x;
        float dy = this.center.y - point.y;

        Log.e("Animator_IN", "this.center.x=" + this.center.x + " ;; this.center.y=" + this.center.y + " ;; item.getX()=" + item.getX() + " ;; item.getY()=" + item.getY());

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final int index) {

        final View item = this.arcLayout.getChildAt(index);
        Point point = this.arcLayout.getPositionByIndex(index);
        float dx = this.center.x - point.x;
        float dy = this.center.y - point.y;


        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    @Override
    public void onClick(View v) {
        if (v == fab || v == menuLayout) {
//          onFabClick();
            hideMenu();
            isShown = false;
            return;
        }
//        Toast.makeText(v.getContext(), "click", Toast.LENGTH_SHORT).show();
    }

    public void onFabClick(boolean isShow) {
        if (isShow) {
            showMenu();
        } else {
            hideMenu();
        }
        isShown = isShow;
//        if (isShown) {
//            isShown = false;
//            hideMenu();
//            Log.e("FabClick:　", "ArcLayoutAnimation hideMenu==> " + isShown);
//        } else {
//            isShown = true;
//            showMenu();
//            Log.e("FabClick:　", "ArcLayoutAnimation showMenu==> " + isShown);
//        }
    }


    private int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightPixels = dm.heightPixels;//宽度height = dm.heightPixels
        return heightPixels;
    }

    /**
     * 获取顶部状态栏高度
     *
     * @param activity
     * @return
     */
    private int getStatusHeight(Activity activity) {
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.top;
    }

    /**
     * 将dimen中dp转化为px
     *
     * @param context
     * @param dimenId
     * @return
     */
    private int dip2px(Context context, int dimenId) {
        int dipValue = context.getResources().getDimensionPixelSize(dimenId);
        return dipValue;
    }

    /**
     * 获取底部导航栏的高度
     *
     * @param activity
     * @return
     */
    private int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 判断扇形菜单是否打开
     *
     * @return
     */
    public boolean isShown() {
        return isShown;
    }
}
