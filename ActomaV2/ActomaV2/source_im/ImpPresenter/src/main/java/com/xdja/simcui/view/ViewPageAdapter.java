package com.xdja.simcui.view;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

public class ViewPageAdapter extends PagerAdapter {
	private final List<GridView> mListViews;

	public ViewPageAdapter(List<GridView> mListViews) {
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
	}

	@Override
	public int getCount() {
		return mListViews.size();// 返回页卡的数量
	}

	@Override  
    public int getItemPosition(Object object) {  
        return super.getItemPosition(object);  
    }  
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		container.addView(mListViews.get(position), 0);// 添加页卡
		return mListViews.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

}
