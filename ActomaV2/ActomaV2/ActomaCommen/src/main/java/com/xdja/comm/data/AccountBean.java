package com.xdja.comm.data;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.data</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/7</p>
 * <p>Time:20:01</p>
 */
public class AccountBean {
    //            “account” : “123456”, // 安通账号
//            “deviceName” : “Phone-1”, // 设备名称
//            “nickname” : “望眼欲穿”, //昵称
//            “nicknamePy” : “wyyc”, //昵称的拼音简拼
//            “nicknamePinyin” : “wangyanyuchuan”, //昵称的拼音全拼
//            “mobile” : “13623658510”, // 手机
//            “mail” : “test@xdja.com”, // 邮箱
//            “avatar” : “lkaje23lakjf.png”, //头像文件名称
//            “thumbnail” : “usdflkjsdf.png”, //缩略图文件名称
//            “avatarDownloadUrl” : “http://10.10.13.41:9080/fileAgent/FileBre
//    akDownload/2015-07-21_f296886d-854e-4e86-a911-84c37481363b/343432”, //头像下载地址/文件标识/文件大小
//            “thumbnailDownloadUrl” : “http://10.10.13.41:9080/fileAgent/FileBreak
//    Download/2015-07-21_f296886d-854e-4e86-a911-84c37481363b/77866”, //头像缩略图下载地址/文件标识/文件大小
//            “companyCode” : “1”, //集团通讯录code

    /**
     * 帐号
     */
    private String account;
    /**
     * 账号别称
     */
    private String alias;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String mail;
    /**
     * 头像文件名称
     */
    private String avatar;
    /**
     * 缩略图文件名称
     */
    private String thumbnail;
    /**
     * 头像下载地址
     */
    private String avatarDownloadUrl;
    /**
     * 头像缩略图下载地址
     */
    private String thumbnailDownloadUrl;
    /**
     * 昵称的拼音简拼
     */
    private String nicknamePy;
    /**
     * 昵称的拼音全拼
     */
    private String nicknamePinyin;
    /**
     * 集团通讯录code
     */
    private String companyCode;

    /**
     * 集团通讯录是否变更（绑定的集团是否有变化）
     * add by xnn for 20170308
     */
    private boolean isCompanyCodeChanged;

    public boolean isCompanyCodeChanged() {
        return isCompanyCodeChanged;
    }

    public void setCompanyCodeChanged(boolean companyCodeChanged) {
        isCompanyCodeChanged = companyCodeChanged;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAvatarDownloadUrl() {
        return avatarDownloadUrl;
    }

    public void setAvatarDownloadUrl(String avatarDownloadUrl) {
        this.avatarDownloadUrl = avatarDownloadUrl;
    }

    public String getThumbnailDownloadUrl() {
        return thumbnailDownloadUrl;
    }

    public void setThumbnailDownloadUrl(String thumbnailDownloadUrl) {
        this.thumbnailDownloadUrl = thumbnailDownloadUrl;
    }

    public String getNicknamePy() {
        return nicknamePy;
    }

    public void setNicknamePy(String nicknamePy) {
        this.nicknamePy = nicknamePy;
    }

    public String getNicknamePinyin() {
        return nicknamePinyin;
    }

    public void setNicknamePinyin(String nicknamePinyin) {
        this.nicknamePinyin = nicknamePinyin;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "AccountBean{" +
                "account='" + account + '\'' +
                ", alias='" + alias + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mail='" + mail + '\'' +
                ", avatar='" + avatar + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", avatarDownloadUrl='" + avatarDownloadUrl + '\'' +
                ", thumbnailDownloadUrl='" + thumbnailDownloadUrl + '\'' +
                ", nicknamePy='" + nicknamePy + '\'' +
                ", nicknamePinyin='" + nicknamePinyin + '\'' +
                ", companyCode='" + companyCode + '\'' +
                '}';
    }
}
