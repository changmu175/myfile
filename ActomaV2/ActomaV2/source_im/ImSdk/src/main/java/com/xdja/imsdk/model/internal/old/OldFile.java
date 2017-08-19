package com.xdja.imsdk.model.internal.old;

import com.xdja.imsdk.constant.ImSdkFileConstant;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：老版本的文件文本信息内容             <br>
 * 创建时间：2016/12/20 11:57                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class OldFile {
    /**
     * 文件的类型：0，普通文件 1，语音文件 2，视频文件 3，图片文件
     */
    private int type;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 原文件在文件服务器的地址
     */
    private String fileUrl;

    /**
     * 原文件路径
     */
    private String filePath;

    /**
     * 文件扩展信息
     */
    private String extraInfo;

    /**
     * 获取文件类型
     * @return 文件类型
     */
    public int getType() {
        return type;
    }

    /**
     * 设置文件类型
     * @param type 文件类型
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取文件名
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置文件名
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取文件扩展名
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 设置文件扩展名
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 获取文件大小
     * @return file size
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置文件大小
     * @param size file size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * 获取原文件URL
     * @return the fileUrl
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 获取原文件URL
     * @param fileUrl the fileUrl to set
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 获取文件本路径
     * @return
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 设置本地文件路径
     * @param filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 获取文件扩展信息
     * @return 扩展信息
     */
    public String getExtraInfo() {
        return extraInfo;
    }

    /**
     * 设置文件扩展信息
     * @param extraInfo 扩展信息
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isNormal() {
        return type == ImSdkFileConstant.FILE_NORMAL;
    }

    public boolean isImage() {
        return type == ImSdkFileConstant.FILE_IMAGE;
    }

    public boolean isVoice() {
        return type == ImSdkFileConstant.FILE_VOICE;
    }

}
