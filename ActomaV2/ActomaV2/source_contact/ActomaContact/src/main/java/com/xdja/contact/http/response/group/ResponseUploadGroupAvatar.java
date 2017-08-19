package com.xdja.contact.http.response.group;

import java.io.Serializable;

/**
 * Created by XDJA_XA on 2015/7/23.
 *
 * modify wanghao 2016-02-27
 */
public class ResponseUploadGroupAvatar implements Serializable {

    private String avatarUrl;//群头像地址

    private String avatarHash;//原文件头像摘要

    private String thumbnailUrl;//群头像缩略图地址

    private String thumbnailHash;//缩略图地址摘要

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public void setAvatarHash(String avatarHash) {
        this.avatarHash = avatarHash;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnail) {
        this.thumbnailUrl = thumbnail;
    }

    public String getThumbnailHash() {
        return thumbnailHash;
    }

    public void setThumbnailHash(String thumbnailHash) {
        this.thumbnailHash = thumbnailHash;
    }
}
