package com.xdja.imp.presenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.ScreenInfo;
import com.xdja.imp.presenter.command.ChatDetailPicPreviewCommand;
import com.xdja.imp.util.Functions;
import com.xdja.simcui.view.PhotoView;

import java.io.File;
import java.util.List;

/**
 * Created by guorong on 2016/7/5.
 */
public class ChatDetailPictureAdapterPresenter extends PagerAdapter {
    //guorong modify 2016年12月1日 fixed bug 5963 .begin
    private static final int MAX_LENGTH = 1500;
    //guorong modify 2016年12月1日 fixed bug 5963 .end
    //private final String fidHeader = "http://dfs.test.safecenter.com/download/";
    private final List<ChatDetailPicInfo> infos;

    private final Context ctx;

    private final ChatDetailPicPreviewCommand command;

    private final ScreenInfo screenInfo;

    private PhotoView photoView;

    public ChatDetailPictureAdapterPresenter(List<ChatDetailPicInfo> infos, Context ctx,
                                             ChatDetailPicPreviewCommand chatDetailPicPreviewCommand) {
        this.infos = infos;
        this.ctx = ctx;
        this.command = chatDetailPicPreviewCommand;
        screenInfo = Functions.getScreenInfo((Activity) ctx);
    }

    @Override
    public int getCount() {
        if (infos == null || infos.size() == 0) {
            return 0;
        }
        return infos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (infos == null || infos.size() == 0 || infos.get(position) == null) {
            return null;
        }

        /**
         * 修改人 guorong
         * 时间 2016-8-8 16:07:59
         * 解决小图片显示太小的问题
         * bug号 2441
         * 走查人 lll
         * */
        final ChatDetailPicInfo info = infos.get(position);
        //需要显示的图片url，可能是本地路径，也可能是服务器请求地址
        String url;
        //闪信已经销毁
        if (info.isBoom() && !info.isMine()) {
            LinearLayout linearLayout = new LinearLayout(ctx);
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setGravity(Gravity.CENTER);
            ImageView imageView = new ImageView(ctx);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(339, 255));
            imageView.setImageResource(R.drawable.bg_shanxin_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            linearLayout.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    command.popdismiss();
                }
            });
            container.addView(linearLayout);

            //juyingang fix 20160829 begin
            //修改闪现销毁后，依然存在查看原图按钮
            command.hideOriginBtn();
            //juyingang fix 20160829 end

            return linearLayout;
        } else {
            //有原图显示原图，没有原图显示高清缩略图，没有高清缩略图则将url设置为
            //图片下载路径
            //加载过程中显示缩略图以及加载动画
            boolean isRaw = false;
            boolean isHd = false;
            photoView = new PhotoView(ctx);
            if (isFileDownload(info.getRawPath())) {
                url = info.getRawPath();
                isRaw = true;
            } else if (isFileDownload(info.getHdThumPath())) {
                url = info.getHdThumPath();
                isHd = true;
            } else {
                url = info.getThumPath();
            }
            File file = new File(url);
            if (!file.exists()) {
                return null;
            }

            //获取图片大小
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(url, options);
            if (options.outWidth > MAX_LENGTH || options.outHeight > MAX_LENGTH) {
                photoView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            } else {
                photoView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            int reqWidth;
            int reqHeight;

            float screenRadio = (float) screenInfo.getWidth() / (float) screenInfo.getHeight();
            if (options.outWidth > 0 && options.outHeight > 0) {
                if (options.outHeight / options.outWidth >= 3) {
                    //guorong modify 2016-9-14 09:34:38 fixed bug 3878 review by zya
                    reqHeight = screenInfo.getHeight();
                    reqWidth = screenInfo.getHeight() * options.outWidth / options.outHeight;
                    photoView.setLayoutParams(new ViewGroup.LayoutParams(reqWidth, reqHeight));
                    photoView.setMaxScale(((float) screenInfo.getWidth() - 100) / (float) reqWidth);
                    try {
                        if (!isFileDownload(info.getThumPath())) {
                            Glide.with(ctx)
                                    .load(R.drawable.pic_failed)
                                    .into(photoView);
                        } else {
                            //guorong@xdja.com 2016年10月26日 修改图片加载切换过程中的问题 .begin
                            if (isHd) {
                                Glide.with(ctx)
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .dontAnimate()
                                        .into(photoView);
                            } else if (isRaw) {
                                //[S]modify by guorong on 20160920.

                                Glide.with(ctx)
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .override(options.outWidth, options.outHeight)
                                        .dontAnimate()
                                        .into(photoView);
                                //guorong@xdja.com 2016年10月26日 修改图片加载切换过程中的问题 .end
                            } else {
                                Glide.with(ctx)
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .into(photoView);
                            }
                        }
                    } catch (OutOfMemoryError error) {
                        Glide.with(ctx)
                                .load(url)
                                .error(R.drawable.pic_failed)
                                .into(photoView);
                    }
                } else {
                    reqWidth = screenInfo.getWidth();
                    reqHeight = screenInfo.getWidth() * options.outHeight / options.outWidth;
                    photoView.setLayoutParams(new ViewGroup.LayoutParams(reqWidth, reqHeight));
                    try {
                        //juyingang modify 20160825 review by gbc
                        if (!isFileDownload(info.getThumPath())) {
                            Glide.with(ctx)
                                    .load(R.drawable.pic_failed)
                                    .into(photoView);
                        } else {
                            if (isHd) {
                                //guorong modify 2016-9-8 16:04:51 review by zya
                                Glide.with(ctx)
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .dontAnimate()
                                        .fitCenter()
                                        .into(photoView);
                            } else if (isRaw) {
                                //guorong modify 2016-9-8 16:04:51 review by zya

                                Glide.with(ctx)
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .dontAnimate()
                                        .fitCenter()
                                        .into(photoView);

                            } else {
                                Glide.with(ctx)
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .into(photoView);
                            }
                        }
                    } catch (OutOfMemoryError error) {
                        Glide.with(ctx)
                                .load(url)
                                .error(R.drawable.pic_failed)
                                .into(photoView);
                    }
                }
            }

            //图片缩放选项
            photoView.enable();
            container.addView(photoView);
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    command.longClickPic(info, position);
                    return true;
                }
            });

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    command.popdismiss();
                }
            });
            //guorong@xdja.com fix bug 5936 begin
            int state = command.getMsgState(info.getMsgId());
            if(!info.isMine() && state != -1 && state < ConstDef.STATE_READED){
                command.sendReadedState(info.getMsgId());
            }
            //guorong@xdja.com fix bug 5936 end
            return photoView;
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 修改人 guorong
     * 时间 2016-8-2 15:45:14
     * 解决根据相关信息判断文件是否下载完成不准确导致的一些问题
     * bug号 ：无
     */
    private boolean isFileDownload(String path) {
        if (path == null || "".equals(path)) {
            return false;
        }
        //guorong 2016-8-31 18:19:57 fix bug 2897
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options.outWidth > 0 && options.outHeight > 0;
    }

    public void makePhotoViewEnable(boolean isEnable) {
        if (photoView == null) {
            return;
        }
        photoView.setEnabled(isEnable);
    }

    public void setPhotoViewClickadle(boolean isClickable) {
        if (photoView != null) {
            photoView.setClickable(isClickable);
        }
    }
}
