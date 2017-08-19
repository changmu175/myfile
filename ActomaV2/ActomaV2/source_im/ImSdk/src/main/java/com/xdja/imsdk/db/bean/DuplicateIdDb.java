package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  duplicate_id表实体类                          <br>
 * 创建时间：2016/11/27 上午12:30                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class DuplicateIdDb {
    private Long id;
    private String send_time;
    private String server_id;

    public DuplicateIdDb() {
    }

    public DuplicateIdDb(Long id, String send_time, String server_id) {
        this.id = id;
        this.send_time = send_time;
        this.server_id = server_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }
}
