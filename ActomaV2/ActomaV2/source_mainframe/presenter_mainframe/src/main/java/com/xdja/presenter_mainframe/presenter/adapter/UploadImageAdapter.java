package com.xdja.presenter_mainframe.presenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xdja.presenter_mainframe.R;

import java.io.File;
import java.util.List;

/**
 * Created by ALH on 2016/8/12.
 */
public class UploadImageAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private LayoutInflater layoutInflater;

    public UploadImageAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
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
            return position;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        UploadViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_select_upload_images, null);
            holder = new UploadViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.item_select_upload_image);
            convertView.setTag(holder);
        } else {
            holder = (UploadViewHolder) convertView.getTag();
        }
        if (!TextUtils.isEmpty(list.get(position))) {
            Glide.with(context).load(new File(list.get(position))).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.img_add_selector);
        }
        return convertView;
    }

    class UploadViewHolder {
        ImageView image;
    }
}
