package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：deleted_msg表实体类               <br>
 * 创建时间：2016/11/26 17:21                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class DeletedMsgDb {
    private Long id;
    private Long msg_id;
    private String server_id;

    public DeletedMsgDb() {
    }

    public DeletedMsgDb(Long id, Long msg_id, String server_id) {
        this.id = id;
        this.msg_id = msg_id;
        this.server_id = server_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Long msg_id) {
        this.msg_id = msg_id;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }
}
