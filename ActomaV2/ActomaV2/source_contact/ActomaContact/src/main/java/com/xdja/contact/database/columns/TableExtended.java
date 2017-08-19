package com.xdja.contact.database.columns;

/**
 * Created by wanghao on 2015/7/10.
 */
public class TableExtended extends AbsTableOperate{

    /**
     * 扩展表表名
     */
    public static final String TABLE_NAME = "t_extended";
    /**
     * 自增id
     */
    public static final String ID = "_id";
    /**
     * 帐号
     */
    public static final String ACCOUNT = "c_account";
    /**
     * 字段类型
     */
    public static final String TYPE = "c_type";
    /**
     * 备用字段1
     */
    public static final String DATA1 = "c_data1";
    /**
     * 备用字段2
     */
    public static final String DATA2 = "c_data2";
    /**
     * 备用字段3
     */
    public static final String DATA3 = "c_data3";
    /**
     * 备用字段4
     */
    public static final String DATA4 = "c_data4";
    /**
     * 备用字段5
     */
    public static final String DATA5 = "c_data5";




    @Override
    protected Class getCls() {
        return TableExtended.class;
    }
}
