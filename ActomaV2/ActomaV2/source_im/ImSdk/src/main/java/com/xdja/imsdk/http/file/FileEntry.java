package com.xdja.imsdk.http.file;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：上传下载的文件业务实体              <br>
 * 创建时间：2016/12/3 14:29                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class FileEntry {
    /**
     * 文件路径，本地路径
     */
    private String path;

    /**
     * 加密文件路径，本地路径
     */
    private String encryptPath;
    /**
     * 文件服务器生成的文件地址
     */
    private String fid;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件总大小
     */
    private long size;
    /**
     * 文件已传输大小
     */
    private long tSize;

    /**
     * 文件加密后的大小
     */
    private long encryptSize;
    /**
     * 所属消息的数据库id
     */
    private long id;

    /**
     * 所属会话tag
     */
    private String tag;

    /**
     * 上传下载的文件类型:is_show, is_hd, is_raw
     * @see FileType
     */
    private FileType type;

    /**
     * 上传下载所属的消息文件类型：normal, voice, image, video, unknown
     * @see com.xdja.imsdk.constant.ImSdkFileConstant
     */
    private int msgType;

    /**
     * 文件状态
     * @see com.xdja.imsdk.constant.internal.FileTState
     */
    private int state;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEncryptPath() {
        return encryptPath;
    }

    public void setEncryptPath(String encryptPath) {
        this.encryptPath = encryptPath;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long gettSize() {
        return tSize;
    }

    public void settSize(long tSize) {
        this.tSize = tSize;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getEncryptSize() {
        return encryptSize;
    }

    public void setEncryptSize(long encryptSize) {
        this.encryptSize = encryptSize;
    }

    public boolean isNormal() {
        return msgType == ImSdkFileConstant.FILE_NORMAL;
    }

    public boolean isImage() {
        return msgType == ImSdkFileConstant.FILE_IMAGE;
    }

    public boolean isVoice() {
        return msgType == ImSdkFileConstant.FILE_VOICE;
    }

    public boolean isVideo() {
        return msgType == ImSdkFileConstant.FILE_VIDEO;
    }

    public boolean isUnknow() {
        return msgType == ImSdkFileConstant.FILE_UNKNOWN;
    }

    public boolean isShow() {
        return type == FileType.IS_SHOW;
    }

    public boolean isHd() {
        return type == FileType.IS_HD;
    }

    public boolean isRaw() {
        return type == FileType.IS_RAW;
    }

    public int getPercent() {
        if (encryptSize > 0) {
            return (int) (tSize/encryptSize * 100);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "FileEntry{" +
                "path='" + path + '\'' +
                ", encryptPath='" + encryptPath + '\'' +
                ", fid='" + fid + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", tSize=" + tSize +
                ", encryptSize=" + encryptSize +
                ", id=" + id +
                ", tag='" + tag + '\'' +
                ", type=" + type +
                ", msgType=" + msgType +
                ", state=" + state +
                '}';
    }
}
