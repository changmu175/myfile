package com.xdja.contact.bean;

import android.content.ContentValues;

import com.xdja.contact.database.columns.TableExtended;


/**
 * Created by hkb.
 * 2015/7/8/0008.
 */
public class MemberExt implements BaseContact {

    private String id;//主键、自增、唯一
    private String memberId;//服务器数据id
    private String dataType;
    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData5() {
        return data5;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TableExtended.ID,getMemberId());
        values.put(TableExtended.ACCOUNT,getDataType());
        values.put(TableExtended.TYPE,getDataType());
        values.put(TableExtended.DATA1,getData1());
        values.put(TableExtended.DATA2,getData2());
        values.put(TableExtended.DATA3,getData3());
        values.put(TableExtended.DATA4,getData4());
        values.put(TableExtended.DATA5,getData5());
        return values;
    }

}
