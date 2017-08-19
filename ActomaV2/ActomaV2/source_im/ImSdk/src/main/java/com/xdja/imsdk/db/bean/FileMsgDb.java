package com.xdja.imsdk.db.bean;

import com.xdja.imsdk.constant.ImSdkFileConstant;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：file_msg表实体类                  <br>
 * 创建时间：2016/11/26 17:22                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class FileMsgDb {
    private Long id;
    private String file_path;
    private String encrypt_path;
    private String file_name;
    private Long file_size;
    private Long encrypt_size;
    private Long translate_size;
    private String suffix;
    private String fid;
    private Integer file_state;
    private Long msg_id;
    private Integer type;
    private String extra_info;

    public FileMsgDb() {
    }

    public FileMsgDb(Long id, String file_path, String encrypt_path, String file_name,
                     Long file_size, Long encrypt_size, Long translate_size,
                     String suffix, String fid, Integer file_state,
                     Long msg_id, Integer type, String extra_info) {
        this.id = id;
        this.file_path = file_path;
        this.encrypt_path = encrypt_path;
        this.file_name = file_name;
        this.file_size = file_size;
        this.encrypt_size = encrypt_size;
        this.translate_size = translate_size;
        this.suffix = suffix;
        this.fid = fid;
        this.file_state = file_state;
        this.msg_id = msg_id;
        this.type = type;
        this.extra_info = extra_info;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getEncrypt_path() {
        return encrypt_path;
    }

    public void setEncrypt_path(String encrypt_path) {
        this.encrypt_path = encrypt_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Long getFile_size() {
        return file_size;
    }

    public void setFile_size(Long file_size) {
        this.file_size = file_size;
    }

    public Long getEncrypt_size() {
        return encrypt_size;
    }

    public void setEncrypt_size(Long encrypt_size) {
        this.encrypt_size = encrypt_size;
    }

    public Long getTranslate_size() {
        return translate_size;
    }

    public void setTranslate_size(Long translate_size) {
        this.translate_size = translate_size;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public Integer getFile_state() {
        return file_state;
    }

    public void setFile_state(Integer file_state) {
        this.file_state = file_state;
    }

    public Long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Long msg_id) {
        this.msg_id = msg_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExtra_info() {
        return extra_info;
    }

    public void setExtra_info(String extra_info) {
        this.extra_info = extra_info;
    }

    public boolean isVoice() {
        return type == ImSdkFileConstant.FILE_VOICE;
    }

    public boolean isImage() {
        return type == ImSdkFileConstant.FILE_IMAGE;
    }

    public boolean isVideo() {
        return type == ImSdkFileConstant.FILE_VIDEO;
    }

    public boolean isNormal() {
        return type == ImSdkFileConstant.FILE_NORMAL;
    }

    public boolean isUnknow() {
        return type == ImSdkFileConstant.FILE_UNKNOWN;
    }
}
