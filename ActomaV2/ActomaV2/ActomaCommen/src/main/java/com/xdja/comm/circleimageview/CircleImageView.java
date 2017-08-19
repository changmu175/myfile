package com.xdja.comm.circleimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xdja.comm.R;
import com.xdja.dependence.uitls.LogUtil;

public class CircleImageView extends ImageView {
    @DrawableRes
    private int mDefaultImage = R.drawable.circle_head_deafult_56;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public <T> void loadImage(@NonNull T drawable, boolean isNeedFit, int defaultImage) {
        mDefaultImage = defaultImage;
        loadImage(drawable, isNeedFit);
    }

    public <T> void loadImage(@NonNull T drawable, boolean isNeedFit) {
        loadImage(drawable,isNeedFit,true,this , true);
    }

    public <T> void loadImage(@NonNull T drawable, boolean isNeedFit , boolean showDefaultImage) {
        loadImage(drawable,isNeedFit,true,this , showDefaultImage);
    }


    private PopupWindow window;

    /**
     * 使能点击图片后自动展示全屏大图
     *
     * @param drawable 全屏图,如果为空则使用原图像
     */
    public <T> void showImageDetailAble(@NonNull final T drawable) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDetail(drawable);
            }
        });
    }

    public boolean isShow() {
        return window == null || !window.isShowing() ? false : true;
    }

    public void dismiss() {
        if (window == null || !window.isShowing()) return;
        window.dismiss();
    }

    @SuppressLint("InflateParams")
    public <T> void showImageDetail(@NonNull T drawable) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.circle_head_iamge, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.circle_head_image);
        //设置头像大图
        loadImage(drawable, false, false, imageView, true);
        //显示头像大图的弹框
        window = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setOutsideTouchable(true);
        window.showAtLocation(new View(getContext()), Gravity.CENTER, 0, 0);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                if (window.isShowing()) {
                    window.dismiss();
                }
                return false;
            }
        });
    }

    @SuppressWarnings("ConstantConditions")// add by ycm for lint 2017/02/16
    private <T> void loadImage(final @NonNull T drawable, boolean isNeedFit, boolean isCircle, ImageView imageView , boolean showDefaultImage) {
	    //[S]modify by tangsha@20161228 for 7516
        RequestManager requestManager = Glide.with(getContext().getApplicationContext());
        DrawableRequestBuilder builder;
        if(drawable==null||"".equals(drawable)){//add by lwl
            builder = requestManager.load(mDefaultImage);
        }else {
            //start：wangchao for some url no contain "download"
            T drawable2 = drawable;
            if (drawable instanceof String) {
                String url = (String) drawable;
                if (url.length() > 8 && url.startsWith("http") && !url.contains("download")) {
                    int pos = url.indexOf("/", 8);
                    if (pos > 0 && pos < url.length()) {
                        url = url.substring(0, pos) + "/download" + url.substring(pos, url.length());
                        drawable2 = (T) url;
                    }
                }
            }
            //end：wangchao for some url no contain "download"

            //modify by zya@xdja.com,fix bug 1834
            builder = requestManager.load(drawable2);
        }
		//[E]modify by tangsha@20161228 for 7516
        builder.listener(new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                LogUtil.getUtils().e("头像下载错误:"+drawable);
                LogUtil.getUtils().e(e);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        });
        if (isNeedFit) {
            builder = builder.fitCenter();
        }
        builder.crossFade();
        //显示错误图片和占位符
        //modify by alh@xdja.com to fix bug: 1380 2016-07-13 start (rummager : self)
        if (showDefaultImage) builder.placeholder(mDefaultImage);
        //modify by alh@xdja.com to fix bug: 1380 2016-06-13 end (rummager : self)
        builder.error(mDefaultImage);
        if (isCircle){
            builder.transform(new CircleTransform(getContext()));
        }
        builder.dontAnimate();
        builder.into(imageView);
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        //防止view没有渲染测不到宽高
        post(new Runnable() {
            @Override
            public void run() {
                CircleImageView.super.setImageBitmap(new CircleTransform(getContext())
                        .transform(null,bm,getWidth(),getHeight()));
            }
        });
    }


    @Override
    public void setBackground(Drawable background) {
        super.setBackground(new BitmapDrawable(new CircleTransform(getContext())
                .transform(null, ((BitmapDrawable) background).getBitmap(),getWidth(),getHeight())));
    }

}