package com.xdja.presenter_mainframe.presenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.bean.SelectImageBean;
import com.xdja.presenter_mainframe.bean.SelectUploadImageBean;

import java.io.File;
import java.util.List;

/**
 * Created by ALH on 2016/8/12.
 */
public class SelectUploadImageAdapter extends BaseAdapter {
    private List<SelectUploadImageBean> list;
    private Context context;
    private LayoutInflater layoutInflater;

    public SelectUploadImageAdapter(List<SelectUploadImageBean> list, Context context) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 类型为照相机
     */
    public final static int TYPE_CAMERA = 0;
    /**
     * 类型为图片
     */
    public final static int TYPE_IMAGE = 1;

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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).getType()) {
            case TYPE_CAMERA:
                return TYPE_CAMERA;
            case TYPE_IMAGE:
                return TYPE_IMAGE;
            default:
                return TYPE_IMAGE;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CameraHolder cameraHolder = null;
        ImageHolder imageHolder = null;
        if (convertView == null) {
            switch (list.get(position).getType()) {
                case TYPE_CAMERA:
                    convertView = layoutInflater.inflate(R.layout.item_select_upload_image_camera, null);
                    cameraHolder = new CameraHolder();
                    cameraHolder.camera = convertView;
                    convertView.setTag(cameraHolder);
                    break;
                case TYPE_IMAGE:
                    convertView = layoutInflater.inflate(R.layout.item_select_upload_image_images, null);
                    imageHolder = new ImageHolder();
                    imageHolder.selectView = convertView.findViewById(R.id.item_select_upload_layout);
                    imageHolder.img = (ImageView) convertView.findViewById(R.id.item_select_upload_image_imageview);
                    imageHolder.checkBox = (CheckBox) convertView.findViewById(R.id.item_select_upload_image_checkbox);
                    convertView.setTag(imageHolder);
                    break;
            }
        } else {
            switch (list.get(position).getType()) {
                case TYPE_CAMERA:
                    cameraHolder = (CameraHolder) convertView.getTag();
                    break;
                case TYPE_IMAGE:
                    imageHolder = (ImageHolder) convertView.getTag();
                    break;
            }
        }
        switch (list.get(position).getType()) {
            case TYPE_IMAGE:
                final SelectImageBean bean = (SelectImageBean) list.get(position).getObject();
                if (!TextUtils.isEmpty(bean.getInfoBean().getImage_original_url())) {
                    Glide.with(context).load(
                            new File(bean.getInfoBean().getImage_thumb_url())).into(imageHolder.img);
                    imageHolder.checkBox.setChecked(bean.isCheck());
                    if (bean.isCheck()) {
                        imageHolder.selectView.setBackgroundColor(
                                context.getResources().getColor(R.color.base_black_65));
                    } else {
                        imageHolder.selectView.setBackgroundColor(
                                context.getResources().getColor(R.color.transparent));
                    }
                }
                break;
        }
        return convertView;
    }

    class CameraHolder {
        View camera;
    }

    class ImageHolder {
        View selectView;
        CheckBox checkBox;
        ImageView img;
    }
}
