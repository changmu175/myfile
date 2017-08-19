package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  hd_thumb_file表实体类                         <br>
 * 创建时间：2016/11/27 上午12:32                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class HdThumbFileDb {
    private Long id;
    private Long hd_msg_id;
    private String hd_file_path;
    private String hd_encrypt_path;
    private String hd_file_name;
    private Long hd_file_size;
    private Long hd_encrypt_size;
    private Long hd_translate_size;
    private String hd_fid;
    private Integer hd_state;

    public HdThumbFileDb() {
    }

    public HdThumbFileDb(Long id, Long hd_msg_id, String hd_file_path, String hd_encrypt_path,
                         String hd_file_name, Long hd_file_size, Long hd_encrypt_size,
                         Long hd_translate_size, String hd_fid, Integer hd_state) {
        this.id = id;
        this.hd_msg_id = hd_msg_id;
        this.hd_file_path = hd_file_path;
        this.hd_encrypt_path = hd_encrypt_path;
        this.hd_file_name = hd_file_name;
        this.hd_file_size = hd_file_size;
        this.hd_encrypt_size = hd_encrypt_size;
        this.hd_translate_size = hd_translate_size;
        this.hd_fid = hd_fid;
        this.hd_state = hd_state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHd_msg_id() {
        return hd_msg_id;
    }

    public void setHd_msg_id(Long hd_msg_id) {
        this.hd_msg_id = hd_msg_id;
    }

    public String getHd_file_path() {
        return hd_file_path;
    }

    public void setHd_file_path(String hd_file_path) {
        this.hd_file_path = hd_file_path;
    }

    public String getHd_encrypt_path() {
        return hd_encrypt_path;
    }

    public void setHd_encrypt_path(String hd_encrypt_path) {
        this.hd_encrypt_path = hd_encrypt_path;
    }

    public String getHd_file_name() {
        return hd_file_name;
    }

    public void setHd_file_name(String hd_file_name) {
        this.hd_file_name = hd_file_name;
    }

    public Long getHd_file_size() {
        return hd_file_size;
    }

    public Long getHd_encrypt_size() {
        return hd_encrypt_size;
    }

    public void setHd_encrypt_size(Long hd_encrypt_size) {
        this.hd_encrypt_size = hd_encrypt_size;
    }

    public void setHd_file_size(Long hd_file_size) {
        this.hd_file_size = hd_file_size;
    }

    public Long getHd_translate_size() {
        return hd_translate_size;
    }

    public void setHd_translate_size(Long hd_translate_size) {
        this.hd_translate_size = hd_translate_size;
    }

    public String getHd_fid() {
        return hd_fid;
    }

    public void setHd_fid(String hd_fid) {
        this.hd_fid = hd_fid;
    }

    public Integer getHd_state() {
        return hd_state;
    }

    public void setHd_state(Integer hd_state) {
        this.hd_state = hd_state;
    }
}
