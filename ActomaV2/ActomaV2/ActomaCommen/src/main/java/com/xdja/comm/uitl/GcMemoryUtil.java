/**
 * 
 */
package com.xdja.comm.uitl;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.xdja.dependence.uitls.LogUtil;

/**
 * 回收activity控件及其引用资源
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-25 09:55
 */
public class GcMemoryUtil {

	public static void clearMemory(final View view) {
		if (view == null) {
			return;
		}
		LogUtil.getUtils().i("[clearMemory] >>> " + view.getClass().getName());
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				View child = viewGroup.getChildAt(i);
				clearMemory(child);
				viewGroup.removeView(child);
			}
			viewGroup.removeAllViews();
		}
	}
}
