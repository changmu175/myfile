package com.xdja.imp.presenter.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.uitl.ImageLoader;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.LocalPictureInfo;

/**
 * 图片选择界面ViewHolder
 * Created by leil on 2016/6/20.
 */
public class PictureListViewHolder {

    /**
     * 当前选中位置
     */
    private int mPosition;

    private View mConvertView;

    private final SparseArray<View> mViews;

    public PictureListViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 获取一个ViewHolder对象
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static PictureListViewHolder get(Context context, View convertView,
                                            ViewGroup parent, int layoutId, int position){
        PictureListViewHolder holder;
        if (convertView == null){
            holder = new PictureListViewHolder(context, parent, layoutId, position);
        } else {
            holder = (PictureListViewHolder) convertView.getTag();
            holder.mPosition = position;
        }
        return holder;
    }

    public View getConvertView(){
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if (view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public PictureListViewHolder setText(int viewId, String text){
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public PictureListViewHolder setImageResource(int viewId, int drawableId){
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public PictureListViewHolder setImageBitmap(int viewId, Bitmap bm){
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView设置图片
     * @param viewId 控件ID
     * @param filePath 文件路径
     * @return
     */
    public PictureListViewHolder setImageByUrl(Activity activity, int viewId, String filePath, int isBoom) {
        if (isBoom == LocalPictureInfo.Statue.STATUE_DESTROY) {
            /*Glide.with(activity)
                    .load(R.drawable.bg_shanxin_image)
                    .asBitmap()
                    .error(R.drawable.pic_failed)
                    .centerCrop()
                    .dontAnimate()
                    .into((ImageView) getView(viewId));*/
            ((ImageView) getView(viewId)).setImageResource(R.drawable.bg_shanxin_image);
        } else {
            ImageLoader.getInstance().crateBuilder()
                    .load(filePath)
                    .preLoad(360, 360)
                    .error(R.drawable.ic_jpg)
                    .centerCrop()
                    .into((ImageView) getView(viewId))
                    .build();

            /*Glide.with(activity)
                    .load(filePath)
                    .asBitmap()
                    .error(R.drawable.pic_failed)
                    .centerCrop()
                    .dontAnimate()
                    .into((ImageView) getView(viewId));*/
        }
        return this;
    }

    public int getPosition(){
        return mPosition;
    }
}
