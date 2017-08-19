package com.xdja.simcui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xdja.imp.R;
import com.xdja.imp.util.BitmapUtils;


class GridViewAdapter extends BaseAdapter {
	private final Context context;
	private final String[] facesName;
	private final Resources r;

	public GridViewAdapter(Context context, String[] facesName) {
		this.context = context;
		this.facesName = facesName;
		this.r = this.context.getResources();
	}

	@Override
	public int getCount() {
		return facesName.length;
	}

	@Override
	public Object getItem(int position) {
		return facesName[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridViewHolder gridViewHolder;
		if (convertView == null) {
			gridViewHolder = new GridViewHolder();
			convertView = gridViewHolder.layoutView;
			convertView.setTag(gridViewHolder);
		} else {
			gridViewHolder = (GridViewHolder) convertView.getTag();
		}
		try {
            if (position==20){
                gridViewHolder.faceIv.setBackground(context.getDrawable(R.drawable.bg_emoji_delete_selector));
            }else {
                gridViewHolder.faceIv.setImageBitmap(BitmapUtils.getBitmapWithName(
						this.facesName[position], this.context.getResources()));
            }
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public class GridViewHolder {
		public final LinearLayout layoutView;
		public final ImageView faceIv;
		private final int faceSize = (int)r.getDimension(R.dimen.faceitem_size);
		private final int faceMargin = (int)r.getDimension(R.dimen.faceitem_margin);

		public GridViewHolder() {
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutView = new LinearLayout(context);
			faceIv = new ImageView(context);
			layoutView.setLayoutParams(layoutParams);
			layoutView.setOrientation(LinearLayout.VERTICAL);
			layoutView.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					faceSize, faceSize);
			params.setMargins(faceMargin, faceMargin, faceMargin, faceMargin);
			faceIv.setLayoutParams(params);
			layoutView.addView(faceIv);
		}
	}
}
