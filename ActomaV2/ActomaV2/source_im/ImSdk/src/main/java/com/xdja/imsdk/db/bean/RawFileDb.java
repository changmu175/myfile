package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  raw_file表实体类                              <br>
 * 创建时间：2016/11/27 上午12:35                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class RawFileDb {
    private Long id;
    private Long raw_msg_id;
    private String raw_file_path;
    private String raw_encrypt_path;
    private String raw_file_name;
    private Long raw_file_size;
    private Long raw_encrypt_size;
    private Long raw_translate_size;
    private String raw_fid;
    private Integer raw_state;

    public RawFileDb() {
    }

    public RawFileDb(Long id, Long raw_msg_id, String raw_file_path, String raw_encrypt_path,
                     String raw_file_name, Long raw_file_size, Long raw_encrypt_size,
                     Long raw_translate_size, String raw_fid, int raw_state) {
        this.id = id;
        this.raw_msg_id = raw_msg_id;
        this.raw_file_path = raw_file_path;
        this.raw_encrypt_path = raw_encrypt_path;
        this.raw_file_name = raw_file_name;
        this.raw_file_size = raw_file_size;
        this.raw_encrypt_size = raw_encrypt_size;
        this.raw_translate_size = raw_translate_size;
        this.raw_fid = raw_fid;
        this.raw_state = raw_state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRaw_msg_id() {
        return raw_msg_id;
    }

    public void setRaw_msg_id(Long raw_msg_id) {
        this.raw_msg_id = raw_msg_id;
    }

    public String getRaw_file_path() {
        return raw_file_path;
    }

    public void setRaw_file_path(String raw_file_path) {
        this.raw_file_path = raw_file_path;
    }

    public String getRaw_encrypt_path() {
        return raw_encrypt_path;
    }

    public void setRaw_encrypt_path(String raw_encrypt_path) {
        this.raw_encrypt_path = raw_encrypt_path;
    }

    public String getRaw_file_name() {
        return raw_file_name;
    }

    public void setRaw_file_name(String raw_file_name) {
        this.raw_file_name = raw_file_name;
    }

    public Long getRaw_file_size() {
        return raw_file_size;
    }

    public Long getRaw_encrypt_size() {
        return raw_encrypt_size;
    }

    public void setRaw_encrypt_size(Long raw_encrypt_size) {
        this.raw_encrypt_size = raw_encrypt_size;
    }

    public void setRaw_file_size(Long raw_file_size) {
        this.raw_file_size = raw_file_size;
    }

    public Long getRaw_translate_size() {
        return raw_translate_size;
    }

    public void setRaw_translate_size(Long raw_translate_size) {
        this.raw_translate_size = raw_translate_size;
    }

    public String getRaw_fid() {
        return raw_fid;
    }

    public void setRaw_fid(String raw_fid) {
        this.raw_fid = raw_fid;
    }

    public Integer getRaw_state() {
        return raw_state;
    }

    public void setRaw_state(Integer raw_state) {
        this.raw_state = raw_state;
    }
}
