package com.xdja.imsdk.model.body;

import com.xdja.imsdk.constant.ImSdkFileConstant;
/**
 * 项目名称：ImSdk            <br>
 * 类描述  ：网页消息内容体      <br>
 * 创建时间：2017/3/27. 16:12  <br>
 * 修改记录：                 <br>
 *
 * @author ycm@xdja.com    <br>
 * @version V1.1.7            <br>
 */
public class IMWebBody extends IMMessageBody{
    /**
     * 网页标题
     */
    private String title;
    /**
     * 网页描述
     */
    private String description;
    /**
     * 网页Url
     */
    private String url;
    /**
     * 分享来源
     */
    private String source;

    /**
     * 网页文件在本地的存储路径
     */
    private String localPath;

    /**
     * 网页文件显示名称
     */
    private String displayName;

    /**
     * 网页文件总大小
     */
    private long fileSize;

    /**
     * 网页文件已传输大小
     */
    private long translateSize;

    /**
     * 网页原始文件后缀名称后缀
     */
    private String suffix;

    /**
     *网页 文件状态
     * @see ImSdkFileConstant.FileState
     */
    private ImSdkFileConstant.FileState state;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public IMWebBody(String title, String description, String url, String source) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取文件在本地的存储路径
     * @return the localPath 文件在本地的存储路径
     */
    public String getLocalPath() {
        return localPath;
    }



    /**
     * 设置 文件在本地的存储路径
     * @param localPath 文件在本地的存储路径
     */
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }



    /**
     * 获取文件显示名称
     * @return the displayName 文件显示名称
     */
    public String getDisplayName() {
        return displayName;
    }



    /**
     * 设置 文件显示名称
     * @param displayName 文件显示名称
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }



    /**
     * 获取文件总大小
     * @return the fileSize 文件总大小
     */
    public long getFileSize() {
        return fileSize;
    }



    /**
     * 设置 文件总大小
     * @param fileSize 文件总大小
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }



    /**
     * 获取文件已传输大小
     * @return the translateSize 文件已传输大小
     */
    public long getTranslateSize() {
        return translateSize;
    }



    /**
     * 设置 文件已传输大小
     * @param translateSize 文件已传输大小
     */
    public void setTranslateSize(long translateSize) {
        this.translateSize = translateSize;
    }



    /**
     * 获取原始文件后缀名称后缀
     * @return the suffix 原始文件后缀名称后缀
     */
    public String getSuffix() {
        return suffix;
    }



    /**
     * 设置 原始文件后缀名称后缀
     * @param suffix 原始文件后缀名称后缀
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 获取文件状态
     * @return the state 文件状态
     */
    public ImSdkFileConstant.FileState getState() {
        return state;
    }



    /**
     * 设置 文件状态
     * @param state 文件状态
     */
    public void setState(ImSdkFileConstant.FileState state) {
        this.state = state;
    }

}
