package com.xdja.imsdk.model.internal;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：                                 <br>
 * 创建时间：2016/12/16 19:12                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class IMRaw {
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件在服务器地址
     */
    private String fid;
    /**
     * 文件加密前大小
     */
    private long size;

    /**
     * 文件加密后的大小
     */
    private long encryptSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getEncryptSize() {
        return encryptSize;
    }

    public void setEncryptSize(long encryptSize) {
        this.encryptSize = encryptSize;
    }
}
