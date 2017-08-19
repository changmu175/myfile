package com.xdja.presenter_mainframe.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.presenter_mainframe.R;


/**
 * <p>summary:</p>
 * <p>description:</p>
 * <p>author:fanjiandong</p>
 * <p>time:2015/3/30 16:55</p>
 */
public class FlipPoint extends LinearLayout {
    /**
     * 当前显示的所引致
     */
    private int index;
    /**
     * 总共多少个点
     */
    private int total;
    /**
     * 每个点的大小
     */
    private int onSize;

    /**
     * 每个点的大小
     */
    private int offSize;
    /**
     * 点与点之间的间距
     */
    private int divider;
    /**
     * 每个点选中的显示内容
     */
    private Drawable point;
    /**
     * 未选中的点的显示内容
     */
    private Drawable pointNoneSelected;

    private ImageView[] points;

    public FlipPoint(Context context) {
        super(context);
        this.setOrientation(HORIZONTAL);
    }

    public FlipPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public FlipPoint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        this.setOrientation(HORIZONTAL);
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.FlipPoint);
        index = a.getInteger(R.styleable.FlipPoint_index, 0);
        total = a.getInteger(R.styleable.FlipPoint_total, 0);
        onSize = a.getDimensionPixelOffset(R.styleable.FlipPoint_pointOnSize, 32);
        offSize = a.getDimensionPixelOffset(R.styleable.FlipPoint_pointOffSize, 32);
        divider = a.getDimensionPixelOffset(R.styleable.FlipPoint_pointMargin, 32);
        point = a.getDrawable(R.styleable.FlipPoint_pointSelcted);
        pointNoneSelected = a.getDrawable(R.styleable.FlipPoint_pointNoneSelected);
        if (!ObjectUtil.objectIsEmpty(a)) {
            a.recycle();
        }
        refreshView();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void refreshView() {
        if (this.getChildCount() != 0) {
            this.removeAllViews();
        }
        points = new ImageView[total];
        for (int i = 0; i < total; i++) {
            ImageView view = new ImageView(getContext());
//            view.setBackground(pointNoneSelected);

            LinearLayout.LayoutParams onll = new LinearLayout.LayoutParams(onSize, onSize);
            LinearLayout.LayoutParams offll = new LinearLayout.LayoutParams(offSize, offSize);
            if (i != 0){
                onll.setMargins(divider, 0, 0, 0);
                offll.setMargins(divider, 0, 0, 0);
            }
            else{
                onll.setMargins(0, 0, 0, 0);
                offll.setMargins(0, 0, 0, 0);
            }
            if (i == index){
                view.setLayoutParams(onll);
                view.setBackground(point);
            }
            else{
                view.setBackground(pointNoneSelected);
                view.setLayoutParams(offll);
            }
            this.addView(view);
            this.setGravity(Gravity.CENTER);
            points[i] = view;
        }
        invalidate();
    }

    /**
     * @param index {@link}
     */
    public void setIndex(int index) {
        this.index = index;
        try {
            refreshView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getIndex() {
        return index;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        refreshView();
    }

    public int getOffSize() {
        return offSize;
    }

    public void setOffSize(int offSize) {
        this.offSize = offSize;
    }

    public int getOnSize() {
        return onSize;
    }

    public void setOnSize(int onSize) {
        this.onSize = onSize;
    }

    public int getDivider() {
        return divider;
    }

    public void setDivider(int divider) {
        this.divider = divider;
    }

    public Drawable getPoint() {
        return point;
    }

    public void setPoint(Drawable point) {
        this.point = point;
    }

    public Drawable getPointNoneSelected() {
        return pointNoneSelected;
    }

    public void setPointNoneSelected(Drawable pointNoneSelected) {
        this.pointNoneSelected = pointNoneSelected;
    }
}
