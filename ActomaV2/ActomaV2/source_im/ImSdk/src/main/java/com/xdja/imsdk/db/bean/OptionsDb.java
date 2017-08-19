package com.xdja.imsdk.db.bean;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：options表实体类                   <br>
 * 创建时间：2016/11/26 17:22                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class OptionsDb {
    private Long id;
    private String property;
    private String value;

    public OptionsDb() {
    }

    public OptionsDb(Long id, String property, String value) {
        this.id = id;
        this.property = property;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
