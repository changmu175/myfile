package com.xdja.contact.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 适配Scroll中使用GridView
 * @author hkb.
 * @since 2015/8/22/0022.
 */
public class GridViewForScroll extends GridView {

    public GridViewForScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GridViewForScroll(Context context) {
        super(context);
    }
    public GridViewForScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}


