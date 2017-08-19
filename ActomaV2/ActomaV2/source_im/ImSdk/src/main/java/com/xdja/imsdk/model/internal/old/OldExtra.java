package com.xdja.imsdk.model.internal.old;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：老版本的文件扩展信息                 <br>
 * 创建时间：2016/12/20 12:00                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class OldExtra {
    /**
     * 文件消息ID
     */
    private long msgId;

    /**
     * 是否被销毁
     */
    private boolean isBoom;

    /**
     * 原始文件名称
     */
    private String rawFileName;

    /**
     * 原始文件URL
     */
    private String rawFileUrl;

    /**
     * 原始文件大小
     */
    private long rawFileSize;

    /**
     * 原始文件已传输大小
     */
    private long rawFileTranslateSize;

    /**
     * 原始文件fid
     */
    private String rawFid;

    /**
     * 缩略图文件名称
     */
    private String thumbFileName;

    /**
     * 缩略图文件路径
     */
    private String thumbFileUrl;

    /**
     * 缩略图文件大小
     */
    private long thumbFileSize;

    /**
     * 缩略图文件传输大小
     */
    private long thumbFileTranslateSize;

    /**
     * 缩略图文件fid
     */
    private String thumbFid;

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getRawFileName() {
        return rawFileName;
    }

    public void setRawFileName(String rawFileName) {
        this.rawFileName = rawFileName;
    }

    public String getRawFileUrl() {
        return rawFileUrl;
    }

    public void setRawFileUrl(String rawFileUrl) {
        this.rawFileUrl = rawFileUrl;
    }

    public long getRawFileSize() {
        return rawFileSize;
    }

    public void setRawFileSize(long rawFileSize) {
        this.rawFileSize = rawFileSize;
    }

    public long getRawFileTranslateSize() {
        return rawFileTranslateSize;
    }

    public void setRawFileTranslateSize(long rawFileTranslateSize) {
        this.rawFileTranslateSize = rawFileTranslateSize;
    }

    public String getRawFid() {
        return rawFid;
    }

    public void setRawFid(String rawFid) {
        this.rawFid = rawFid;
    }

    public String getThumbFileName() {
        return thumbFileName;
    }

    public void setThumbFileName(String thumbFileName) {
        this.thumbFileName = thumbFileName;
    }

    public String getThumbFileUrl() {
        return thumbFileUrl;
    }

    public void setThumbFileUrl(String thumbFileUrl) {
        this.thumbFileUrl = thumbFileUrl;
    }

    public long getThumbFileSize() {
        return thumbFileSize;
    }

    public void setThumbFileSize(long thumbFileSize) {
        this.thumbFileSize = thumbFileSize;
    }

    public long getThumbFileTranslateSize() {
        return thumbFileTranslateSize;
    }

    public void setThumbFileTranslateSize(long thumbFileTranslateSize) {
        this.thumbFileTranslateSize = thumbFileTranslateSize;
    }

    public String getThumbFid() {
        return thumbFid;
    }

    public void setThumbFid(String thumbFid) {
        this.thumbFid = thumbFid;
    }

    public boolean isBoom() {
        return isBoom;
    }

    public void setBoom(boolean boom) {
        isBoom = boom;
    }
}
