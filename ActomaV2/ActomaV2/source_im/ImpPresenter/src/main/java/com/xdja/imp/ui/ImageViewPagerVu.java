package com.xdja.imp.ui;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ChatDetailPicInfo;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.ScreenInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.presenter.command.IChatDetailMediaCommand;
import com.xdja.imp.ui.vu.FilePreviewView;
import com.xdja.imp.util.Functions;
import com.xdja.simcui.view.PhotoView;

/**
 * Created by guorong on 2017/3/1.
 */

public class ImageViewPagerVu extends FilePreviewView<IChatDetailMediaCommand, TalkMessageBean> {
    private static final int MAX_LENGTH = 1500;
    private IChatDetailMediaCommand command;
    private ScreenInfo screenInfo;
    private PhotoView photoView;
    private ImageView shanxinIv;
    private ChatDetailPicInfo info;
    private TalkMessageBean talkMessageBean;

    @Nullable
    @Override
    public int getLayoutRes() {
        return R.layout.image_viewpager_item;
    }

    @Override
    public void setCommand(IChatDetailMediaCommand command) {
        super.setCommand(commanshared);
        this.command = command;
    }


    @Override
    public void injectView() {
        screenInfo = Functions.getScreenInfo(getActivity());
        View view = getView();
        photoView = (PhotoView) view.findViewById(R.id.content_view);
        shanxinIv = (ImageView)view.findViewById(R.id.shanxinIv);
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                command.longClick(talkMessageBean);
                return true;
            }
        });
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command.popdismiss();
            }
        });
    }


    @Override
    public void onViewCreated() {

    }

    @Override
    public void onViewReused() {
        photoView.setImageBitmap(null);
        shanxinIv.setImageBitmap(null);
        photoView.setVisibility(View.GONE);
        shanxinIv.setVisibility(View.GONE);
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean datasource) {
        super.bindDataSource(position, datasource);
        talkMessageBean = datasource;
        FileInfo fileInfo = datasource.getFileInfo();
        if(fileInfo != null && fileInfo instanceof ChatDetailPicInfo){
            info = (ChatDetailPicInfo) fileInfo;
        }
        int state = command.getMsgState(info.getMsgId());
        if (!info.isMine() && state != -1 && state < ConstDef.STATE_READED) {
            command.sendReadedState(info.getMsgId());
        }
        String url;
        if (info.isBoom() && !info.isMine()) {
            shanxinIv.setVisibility(View.VISIBLE);
            photoView.disenable();
            Glide.with(getActivity())
                    .load(R.drawable.bg_shanxin_image)
                    .fitCenter()
                    .into(shanxinIv);
            shanxinIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    command.popdismiss();
                }
            });
            command.hideOriginBtn();
        } else {
            photoView.setVisibility(View.VISIBLE);
            photoView.enable();
            //有原图显示原图，没有原图显示高清缩略图，没有高清缩略图则将url设置为
            //图片下载路径
            //加载过程中显示缩略图以及加载动画
            boolean isRaw = false;
            boolean isHd = false;
            if (isFileDownload(info.getRawPath())) {
                url = info.getRawPath();
                isRaw = true;
            } else if (isFileDownload(info.getHdThumPath())) {
                url = info.getHdThumPath();
                isHd = true;
            } else {
                url = info.getThumPath();
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

            if (options.outWidth > 0 && options.outHeight > 0) {
                if (options.outHeight / options.outWidth >= 3) {
                    reqWidth = screenInfo.getHeight() * options.outWidth / options.outHeight;
                    photoView.setMaxScale(((float) screenInfo.getWidth() - 100) / (float) reqWidth);
                    try {
                        if (!isFileDownload(info.getThumPath())) {
                            photoView.setImageResource(R.drawable.pic_failed);
                        } else {
                            if (isHd) {
                                Glide.with(getActivity())
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .dontAnimate()
                                        .into(photoView);
                            } else if (isRaw) {
                                Glide.with(getActivity())
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .override(options.outWidth, options.outHeight)
                                        .dontAnimate()
                                        .into(photoView);
                            } else {
                                Glide.with(getActivity())
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .into(photoView);
                            }
                        }
                    } catch (OutOfMemoryError error) {
                        Glide.with(getActivity())
                                .load(url)
                                .error(R.drawable.pic_failed)
                                .into(photoView);
                    }
                } else {
                    try {
                        if (!isFileDownload(info.getThumPath())) {
                            photoView.setImageResource(R.drawable.pic_failed);
                        } else {
                            if (isHd) {
                                Glide.with(getActivity())
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .dontAnimate()
                                        .fitCenter()
                                        .into(photoView);
                            } else if (isRaw) {
                                Glide.with(getActivity())
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .placeholder(R.drawable.loading_image_resource)
                                        .dontAnimate()
                                        .fitCenter()
                                        .into(photoView);

                            } else {
                                Glide.with(getActivity())
                                        .load(url)
                                        .error(R.drawable.pic_failed)
                                        .into(photoView);
                            }
                        }
                    } catch (OutOfMemoryError error) {
                        Glide.with(getActivity())
                                .load(url)
                                .error(R.drawable.pic_failed)
                                .into(photoView);
                    }
                }
            }
        }

    }

    private boolean isFileDownload(String path) {
        if (path == null || "".equals(path)) {
            return false;
        }
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
}
