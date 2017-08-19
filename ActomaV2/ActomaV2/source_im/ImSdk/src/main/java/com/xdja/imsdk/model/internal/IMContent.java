package com.xdja.imsdk.model.internal;

import com.xdja.imsdk.constant.ImSdkFileConstant;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：发送文件类型的文本消息的内容体        <br>
 * 创建时间：2016/12/16 18:43                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class IMContent {
    /**
     * 文件的类型：0，普通文件 1，语音文件 2，视频文件 3，图片文件
     */
    private int type;
    /**
     * 文件名，加密前的名称
     */
    private String name;
    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 文件原始大小
     */
    private long size;

    /**
     * 文件加密后大小
     */
    private long encryptSize;

    /**
     * 文件在文件服务器的地址
     */
    private String fid;

    /**
     * 文件扩展信息
     */
    private String extraInfo;

    /**
     * 原始文件信息
     */
    private String raw;

    /**
     * 高清缩略图信息
     */
    private String hd;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public long getEncryptSize() {
        return encryptSize;
    }

    public void setEncryptSize(long encryptSize) {
        this.encryptSize = encryptSize;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }

    public boolean isNormal() {
        return type == ImSdkFileConstant.FILE_NORMAL;
    }

    public boolean isVoice() {
        return type == ImSdkFileConstant.FILE_VOICE;
    }

    public boolean isVideo() {
        return type == ImSdkFileConstant.FILE_VIDEO;
    }

    public boolean isImage() {
        return type == ImSdkFileConstant.FILE_IMAGE;
    }

    public boolean isUnknown() {
        return type == ImSdkFileConstant.FILE_UNKNOWN;
    }
}
