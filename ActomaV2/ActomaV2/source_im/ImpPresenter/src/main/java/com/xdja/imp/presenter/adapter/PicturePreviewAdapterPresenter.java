package com.xdja.imp.presenter.adapter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xdja.comm.uitl.ImageLoader;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.domain.model.ScreenInfo;
import com.xdja.imp.presenter.command.PicturePreviewCommand;
import com.xdja.imp.util.Functions;
import com.xdja.simcui.view.PhotoView;

import java.util.LinkedList;
import java.util.List;

/**
 * 图片预览适配器
 */
public class PicturePreviewAdapterPresenter extends PagerAdapter implements PicturePreviewCommand {
    //guorong modify 2016年12月1日 fixed bug 5963 .begin
    private static final int MAX_LENGTH = 1500;
    //guorong modify 2016年12月1日 fixed bug 5963 .begin
    private final Activity mActivity;

    /** 所要预览的图片信息列表*/
    private final List<LocalPictureInfo> mDataSource;
    //[S]modify by lixiaolong on 20160920.
//    /** 所有加载控件集合*/
//    private HashMap<Integer, PhotoView> mImageViewMap = new HashMap<>();
    private final LinkedList<PhotoView> photoViews = new LinkedList<>();
    //[E]modify by lixiaolong on 20160920.
    /** 屏幕相关信息 */
    private final ScreenInfo mScreenInfo;

    public PicturePreviewAdapterPresenter(Activity activity , List<LocalPictureInfo> dataSource) {
        this.mActivity = activity;
        this.mDataSource = dataSource;
        this.mScreenInfo = Functions.getScreenInfo(activity);
    }

    @Override
    public int getCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LocalPictureInfo pictureInfo = mDataSource.get(position);
        if (pictureInfo == null){
            return  null;
        }

        //[S]modify by lixiaolong on 20160920.
        final PhotoView photoView;
        if (photoViews != null && photoViews.size() > 0) {
            photoView = photoViews.getFirst();
            photoViews.removeFirst();
        } else {
            photoView = new PhotoView(mActivity);
        }
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoView.enable();
        loadImage(photoView, pictureInfo.getLocalPath());
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //[S]modify by lixiaolong on 20160920.
        PhotoView photoView = (PhotoView) object;
        container.removeView(photoView);
        photoViews.addLast(photoView);
//        container.removeView(mImageViewMap.get(position));
//        mImageViewMap.remove(position);
        //[E]modify by lixiaolong on 20160920.
    }

    /**
     * 加载图片
     * @param path 图片路径
     * Added by leill 2016/8/3
     */
    private void loadImage(PhotoView photoView, String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //[S]modify by guorong on 20160920.
        //当图片宽或者高大于能直接显示的最大宽高，关闭硬件加速
        if(options.outWidth > MAX_LENGTH || options.outHeight > MAX_LENGTH){
            photoView.setLayerType(View.LAYER_TYPE_SOFTWARE , null);
        }else{
            photoView.setLayerType(View.LAYER_TYPE_HARDWARE , null);
        }
        //加载本地图片，宽铺满或高铺满
        if (options.outWidth > 0 && options.outHeight > 0) {
            int reqWidth, reqHeight;
            //float screenRadio = (float) mScreenInfo.getWidth() / (float) mScreenInfo.getHeight();
            //[S]modify by guorong on 20160920.
            if (options.outHeight / options.outWidth < 3) {
                reqWidth = mScreenInfo.getWidth();
                reqHeight = mScreenInfo.getWidth() * options.outHeight / options.outWidth;
                photoView.setLayoutParams(new ViewGroup.LayoutParams(reqWidth, reqHeight));
                //photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //ImageLoader.getInstance().loadImage(path , photoView ,
                //        mScreenInfo.getWidth() , mScreenInfo.getHeight());
                ImageLoader.getInstance().crateBuilder()
                        .load(path)
                        .preLoad(mScreenInfo.getWidth(), mScreenInfo.getHeight())
                        .error(R.drawable.ic_jpg)
                        .placeholder(R.drawable.loading_image_resource)
                        .fitCenter()
                        .into(photoView)
                        .build();

            } else {
                reqHeight = mScreenInfo.getHeight();
                reqWidth = mScreenInfo.getHeight() * options.outWidth / options.outHeight;
                photoView.setLayoutParams(new ViewGroup.LayoutParams(reqWidth, reqHeight));
                //设置双击时放大的倍数，该倍数不会影响到放大的最大倍数
                photoView.setMaxScale(((float) mScreenInfo.getWidth() - 100) / (float) reqWidth);
                //ImageLoader.getInstance().loadImage(path , photoView ,
                //        mScreenInfo.getWidth() , mScreenInfo.getHeight());
                ImageLoader.getInstance().crateBuilder()
                        .load(path)
                        .preLoad(mScreenInfo.getWidth(), mScreenInfo.getHeight())
                        .error(R.drawable.ic_jpg)
                        .placeholder(R.drawable.loading_image_resource)
                        .into(photoView)
                        .build();
            }

        } else {
            //本地图片已经不存在，直接显示默认图片
            photoView.setImageResource(R.drawable.pic_failed);
        }
    }
}
