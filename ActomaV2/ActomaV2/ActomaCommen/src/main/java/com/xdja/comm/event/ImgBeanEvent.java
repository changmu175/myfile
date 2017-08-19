package com.xdja.comm.event;

import android.graphics.Bitmap;

/**
 * 描述当前类的作用
 *
 * @author LiXiaoLong
 * @version 1.0
 * @since 2016-08-23 16:15
 */
public class ImgBeanEvent {
    private String url;

    private Bitmap bitmap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
