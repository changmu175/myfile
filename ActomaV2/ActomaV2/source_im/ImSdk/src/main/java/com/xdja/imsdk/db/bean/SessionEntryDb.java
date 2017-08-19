package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：session_entry表实体类             <br>
 * 创建时间：2016/11/26 17:22                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class SessionEntryDb {
    private Long id;
    private String im_partner;
    private Integer session_type;
    private Long last_msg;
    private Long start_time;
    private Long last_time;
    private Integer reminded;
    private String session_flag;

    public SessionEntryDb() {
    }

    public SessionEntryDb(Long id, String im_partner, Integer session_type,
                          Long last_msg, Long start_time, Long last_time,
                          Integer reminded, String session_flag) {
        this.id = id;
        this.im_partner = im_partner;
        this.session_type = session_type;
        this.last_msg = last_msg;
        this.start_time = start_time;
        this.last_time = last_time;
        this.reminded = reminded;
        this.session_flag = session_flag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIm_partner() {
        return im_partner;
    }

    public void setIm_partner(String im_partner) {
        this.im_partner = im_partner;
    }

    public Integer getSession_type() {
        return session_type;
    }

    public void setSession_type(Integer session_type) {
        this.session_type = session_type;
    }

    public Long getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(Long last_msg) {
        this.last_msg = last_msg;
    }

    public Long getStart_time() {
        return start_time;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public Long getLast_time() {
        return last_time;
    }

    public void setLast_time(Long last_time) {
        this.last_time = last_time;
    }

    public Integer getReminded() {
        return reminded;
    }

    public void setReminded(Integer reminded) {
        this.reminded = reminded;
    }

    public String getSession_flag() {
        return session_flag;
    }

    public void setSession_flag(String session_flag) {
        this.session_flag = session_flag;
    }
}
