package com.xdja.comm.data;

import android.graphics.Bitmap;

/**
 * Created by XDJA_XA on 2015/8/9.
 */
public class CommonHeadBitmap {

    private String avatarId;

    private String thumbnailId;

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    private Bitmap bm;

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
}
