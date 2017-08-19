package com.xdja.presenter_mainframe.chooseImg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xdja.comm.uitl.ImageLoader;
import com.xdja.presenter_mainframe.R;

import java.util.ArrayList;


/**
 * Created by geyao on 2015/7/10.
 * 设置头像列表适配器
 */
public class SetHeadPortraitAdapter extends BaseAdapter {

    private ArrayList<ImageRelInfoBean> list;

    private Context context;

    private LayoutInflater layoutInflater;

    private int imgWidth;

    private boolean isCamera = true;

    private final int VIEW_TYPE_CAMERA = 0;

    private final int VIEW_TYPE_PHOTO = 1;

    private final int VIEW_TYPE_COUNT = 2;

    public SetHeadPortraitAdapter(ArrayList<ImageRelInfoBean> list, Context context) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imgWidth = metrics.widthPixels / 3;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }

    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        } else {
            return list.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (list == null) {
            return -1;
        } else {
            return position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isCamera)
            return position == 0 ? VIEW_TYPE_CAMERA : VIEW_TYPE_PHOTO;
        else
            return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        if (isCamera)
            return VIEW_TYPE_COUNT;
        else
            return super.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        SetHeadPorTraitHolder holder;

        if (viewType == VIEW_TYPE_CAMERA && isCamera) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_camera, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(imgWidth, imgWidth));
            }
            return convertView;
        }

        if (convertView != null) {
            holder = (SetHeadPorTraitHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.item_set_head_portrait, null);
            holder = new SetHeadPorTraitHolder();
            holder.imageview = (ImageView) convertView.findViewById(R.id.item_img_setheadportrait);
            convertView.setTag(holder);
        }

        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) holder.imageview.getLayoutParams();
        ll.width = imgWidth;
        ll.height = imgWidth;
        holder.imageview.setLayoutParams(ll);
        String path = list.get(position).getImage_thumb_url();
        if (!TextUtils.isEmpty(path)) {
            ImageLoader.getInstance().crateBuilder()
                    .load(path)
                    .preLoad(360, 360)
                    .error(R.drawable.pic_failed)
                    .centerCrop()
                    .into(holder.imageview)
                    .build(Color.parseColor("#3D3D3D"));
        } else {
            holder.imageview.setImageResource(R.drawable.pic_failed);
        }
        /*if (!TextUtils.isEmpty(list.get(position).getImage_thumb_url())) {
            ImageLoader.getInstance().loadImage();
            //alh@xdja.com<mailto://alh@xdja.com> 2016-11-04 add. fix bug 5680 . review by wangchao1. Start
            Glide.with(context).load(list.get(position).getImage_thumb_url()).skipMemoryCache(true).into(holder.imageview);
            //alh@xdja.com<mailto://alh@xdja.com> 2016-11-04 add. fix bug 5680 . review by wangchao1. End
        }*/
        return convertView;
    }

    class SetHeadPorTraitHolder {
        ImageView imageview;
    }
}
