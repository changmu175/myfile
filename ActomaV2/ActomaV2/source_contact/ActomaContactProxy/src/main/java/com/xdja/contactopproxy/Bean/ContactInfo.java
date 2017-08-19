package com.xdja.contactopproxy.Bean;

/**
 * Created by liyingqing on 16-3-22.
 */
public class ContactInfo {

    /**
     * 账号
     */
    private String account;

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


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
