package com.xdja.simcui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.xdja.imp.R;

import java.util.ArrayList;
import java.util.List;

public class FaceGridView extends LinearLayout {
	private final Context _context;
	private ViewPager _viewPager;
	private LinearLayout _llDot;

	private OnFaceGridViewItemClick onFaceGridViewItemClick;

	private ImageView[] dots;
	/** ViewPager当前页 */
	private int currentIndex;
	/** ViewPager页数 */
	private int viewPager_size;

	/** viewpage高度 */
	private int viewPageHeight = 380;
	/** assets图片名 */
	// public String[] faceNames;
	/** assets图片路径 */
	// private String facePath = "faces";

	private final int emojSize = 60;
   //默认每页个数
	private final int FACE_PAGE_SIZE = 20;

	public FaceGridView(Context context) {
		super(context);
		_context = context;
        LayoutParams allView = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        allView.setMargins(0,20,0,16);
        setLayoutParams(allView);
		initViewPage();
		initFootDots();
	}

	public FaceGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		initViewPage();
		initFootDots();
	}

	@SuppressWarnings("deprecation")
	private void initViewPage() {
		int viewpage_margin = (int) _context.getResources().getDimension(R.dimen.faceitem_margin);
		setOrientation(VERTICAL);
		/* viewpage背景颜色 */
		int backColor = 0x00cccfd0;
		setBackgroundColor(backColor);// 灰色
		_viewPager = new ViewPager(_context);
		_viewPager.setOffscreenPageLimit(4);
		_llDot = new LinearLayout(_context);
		LayoutParams ll = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		ll.weight = 1;
		ll.setMargins(0, viewpage_margin, 0, viewpage_margin);
		_viewPager.setLayoutParams(ll);
		_llDot.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		_llDot.setGravity(Gravity.CENTER_HORIZONTAL);
		_llDot.setOrientation(HORIZONTAL);
		addView(_viewPager);
		addView(_llDot);
	}

	private void initFootDots() {
		/* 默认一页21个item */
		double pageItemCount = 20d;
		viewPager_size = (int) Math.ceil(emojSize / pageItemCount);

		if (0 < viewPager_size) {
			if (viewPager_size == 1) {
				_llDot.setVisibility(View.GONE);
			} else {
				_llDot.setVisibility(View.VISIBLE);
				for (int i = 0; i < viewPager_size; i++) {
					ImageView image = new ImageView(_context);
					image.setTag(i);
					LayoutParams params = new LayoutParams(
							20, 20);
					//fix bug 6231 by zya,20161128
					params.setMargins(5, 5, 5, (int)_context.getResources().getDimension(R.dimen.face_layout_imageview_margin));
					//end by zya
					// 两种调用图片方式
					// image.setBackgroundDrawable(DotFile.setStateDrawable());
					image.setBackgroundResource(R.drawable.dots_set);
					image.setEnabled(false);
					_llDot.addView(image, params);
				}
			}
		}
		if (1 != viewPager_size) {
			dots = new ImageView[viewPager_size];
			for (int i = 0; i < viewPager_size; i++) {
				dots[i] = (ImageView) _llDot.getChildAt(i);
				dots[i].setEnabled(true);
				dots[i].setTag(i);
			}
			currentIndex = 0;
			dots[currentIndex].setEnabled(false);
			_viewPager
					.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

						@Override
						public void onPageSelected(int arg0) {
							setCurDot(arg0);
						}

						@Override
						public void onPageScrolled(int arg0, float arg1,
								int arg2) {

						}

						@Override
						public void onPageScrollStateChanged(int arg0) {
						}
					});
		}
	}

	private void setCurDot(int position) {
		if (position < 0 || position > viewPager_size - 1
				|| currentIndex == position) {
			return;
		}
		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = position;
	}

	public void setAdapter() {
		if (onFaceGridViewItemClick == null) {
			return;
		}
		/* 保存每个页面的GridView视图 , 把这个list暴露出去,用于setOnClickListener */
		List<GridView> list_Views = new ArrayList<>();
		for (int i = 0; i < viewPager_size; i++) {
			list_Views.add(getViewPagerItem(i));
		}
		_viewPager.setAdapter(new ViewPageAdapter(list_Views));

	}

	/** 生成gridView数据 */
	@SuppressLint("DefaultLocale")
	@SuppressWarnings("getGridViewData")
	private String[] getGridViewData(int index) {
		index++;
		int startPos = (index - 1) * FACE_PAGE_SIZE;
		int endPos = index * FACE_PAGE_SIZE;
		int length;

		if (endPos > emojSize) {
			endPos = emojSize - 1;
		}
		length = endPos - startPos + 1;
		String[] tmps = new String[length];

		int num = 0;
		// 表情页面某一页内的表情图片名称集合
		for (int i = startPos; i < endPos; i++) {
			tmps[num] = "emoji_" + String.format("%03d", i);
			num++;
		}
		/* assets 返回按钮图片 */
		String backBtnName = "back_normal";
		tmps[length - 1] = backBtnName;
		return tmps;
	}

	@SuppressWarnings("deprecation")
	private GridView getViewPagerItem(int index) {
		final GridView gridView = new GridView(_context);
		gridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		gridView.setNumColumns(7);
		// gridView.setStretchMode(GridView.STRETCH_SPACING);
		gridView.setSelector(R.drawable.transparent);
		gridView.setVerticalScrollBarEnabled(false);
		gridView.setHorizontalScrollBarEnabled(false);
		gridView.setBackgroundColor(Color.TRANSPARENT);
		gridView.setAdapter(new GridViewAdapter(_context,
				getGridViewData(index)));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击最后一个，清除动作
				if (position == gridView.getChildCount() - 1) {
					onFaceGridViewItemClick.onClear();
				} else {// 添加表情
					onFaceGridViewItemClick.onItemClick(currentIndex
							* FACE_PAGE_SIZE + position);
				}
			}
		});
		return gridView;
	}

	public void setOnFaceGridViewItemClick(
			OnFaceGridViewItemClick onFaceGridViewItemClick) {
		this.onFaceGridViewItemClick = onFaceGridViewItemClick;
	}

	public interface OnFaceGridViewItemClick {
		void onItemClick(int facesPos);

		void onClear();
	}
}
