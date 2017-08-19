package com.xdja.comm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.widget.AbsListView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2017/2/13 18:18   </br>
 * <p>Package: com.xdja.imp.widget</br>
 * <p>Description: GridView懒加载模式 </br>
 */
public class LazyLoadGridView extends GridView implements AbsListView.OnScrollListener{

    /**
     * 记录Item的懒加载情况
     * true: 表示为position为1的item已经懒加载过了
     * false: 表示为position为2的item还没有加载过
     */
    private SparseBooleanArray itemsNow;

    /**
     * 不加载回调接口
     * 在ListView滑动停止后回掉
     */
    public interface OnLazyLoadListener {
        /**
         * 应该被加载细节的项
         *
         * @param itemsPos item的位置集合
         */
        void shouldLoad(List<Integer> itemsPos);
    }

    private OnLazyLoadListener onLazyLoadListener;

    private OnScrollListener onScrollListener;

    private int oldVisibleItemCount = 0;

    public LazyLoadGridView(Context context) {
        super(context);
        init();
    }

    public LazyLoadGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LazyLoadGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //调用父类的设置滑动监听事件
        super.setOnScrollListener(this);
        //初始化
        itemsNow = new SparseBooleanArray();
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setOnLazyLoadListener(OnLazyLoadListener onLazyLoadListener) {
        this.onLazyLoadListener = onLazyLoadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        updateShowLoadPosition();
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            calculateWhichShouldLoad(getFirstVisiblePosition(), getLastVisiblePosition());
        }
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (visibleItemCount != oldVisibleItemCount && oldVisibleItemCount != -1) {
            //因为第一次加载，所以回调此方法，保证不见的不加载
            updateShowLoadPosition();
            calculateWhichShouldLoad(firstVisibleItem, firstVisibleItem + visibleItemCount - 1);
            oldVisibleItemCount = -1;
        }
        if (oldVisibleItemCount == -1) {
            oldVisibleItemCount = visibleItemCount;
        }
        if (this.onScrollListener != null) {
            this.onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * 计算已经加载的栏目数
     * @param firstPosition 第一个可见item的位置
     * @param lastPosition 最后一个可见item的位置
     */
    private void calculateWhichShouldLoad(int firstPosition, int lastPosition) {
        List<Integer> itemsPos = new ArrayList<>();
        for (int i = firstPosition; i < lastPosition; i++) {
            if (!itemsNow.get(i, false)) {
                itemsPos.add(i);
                itemsNow.put(i, true);
            }
        }

        if (onLazyLoadListener != null) {
            onLazyLoadListener.shouldLoad(itemsPos);
        }
    }

    /**
     * 更新需要重新加载的项
     * 将不再显示显示范围内的栏目设为未加载
     */
    private void updateShowLoadPosition() {
        int firstVisibleItem = getFirstVisiblePosition();
        int lastVisibleItem = getLastVisiblePosition();
        for (int i = 0; i < firstVisibleItem; i++) {
            itemsNow.put(i, false);
        }

        int itemCount = getCount();
        for (int i = lastVisibleItem; i < itemCount; i++) {
            itemsNow.put(i, false);
        }
    }
}
