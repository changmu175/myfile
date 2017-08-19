package com.xdja.contactcommon.dto;

import com.xdja.contact.usereditor.bean.UserInfo;

/**
 * Created by wanghao on 2015/8/8.
 * Dto ---> data transfer object
 *
 */
public class ContactDto extends UserInfo {

    /**
     * 大头像url
     */
    private String avatarUrl;
    /**
     * 小头像url
     */
    private String thumbnailUrl;

    /**
     * 其他模块调用名称显示
     */
    private String name;
    /**
     * 名称显示简拼
     */
    private String namePY;
    /*
     * 名称显示全拼
     */
    private String namePinYin;








    public String getNamePY() {
        return namePY;
    }

    public void setNamePY(String namePY) {
        this.namePY = namePY;
    }

    public String getNamePinYin() {
        return namePinYin;
    }

    public void setNamePinYin(String namePinYin) {
        this.namePinYin = namePinYin;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
