package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：  sync_id表实体类                               <br>
 * 创建时间：2016/11/27 上午12:37                          <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class SyncIdDb {
    private Long id;
    private String id_type;
    private String id_value;

    public SyncIdDb() {
    }

    public SyncIdDb(Long id, String id_type, String id_value) {
        this.id = id;
        this.id_type = id_type;
        this.id_value = id_value;
    }

    public String getId_value() {
        return id_value;
    }

    public void setId_value(String id_value) {
        this.id_value = id_value;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
