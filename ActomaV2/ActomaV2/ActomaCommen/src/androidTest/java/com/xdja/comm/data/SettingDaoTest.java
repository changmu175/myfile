package com.xdja.comm.data;

import com.xdja.comm.ApplicationTest;
import com.xdja.comm.server.SettingServer;

import junit.framework.Assert;

/**
 * Created by geyao on 2015/7/20.
 */
public class SettingDaoTest extends ApplicationTest {

    private String key = "sever_key";
    private String value = "false";
    private String column1 = "xxxx_1";
    private String column2 = "xxxx_2";
    private String column3 = "xxxx_3";
    private String column4 = "xxxx_4";
    private String column5 = "xxxx_5";


    /**
     * 测试-插入操作
     */
    public void testInsertSetting() {
        insertSetting();
    }

    /**
     * 测试-更新操作
     */
    public void testUpdateSetting() {
        String value_update = "true";
        String column1_update = "xxxx_1_xg";
        String column2_update = "xxxx_2_xg";
        String column3_update = "xxxx_3_xg";
        String column4_update = "xxxx_4_xg";
        String column5_update = "xxxx_5_xg";

        SettingBean bean = new SettingBean();
        bean.setKey(key);
        bean.setValue(value_update);
        bean.setColumn1(column1_update);
        bean.setColumn2(column2_update);
        bean.setColumn3(column3_update);
        bean.setColumn4(column4_update);
        bean.setColumn5(column5_update);

        SettingServer.updateSetting(bean);

        SettingBean queryBean = SettingServer.querySetting(key);
        Assert.assertNotNull(queryBean);// add by ycm for lint 2017/02/16
        Assert.assertEquals(queryBean.getKey(), key);
        Assert.assertEquals(queryBean.getValue(), value_update);
        Assert.assertEquals(queryBean.getColumn1(), column1_update);
        Assert.assertEquals(queryBean.getColumn2(), column2_update);
        Assert.assertEquals(queryBean.getColumn3(), column3_update);
        Assert.assertEquals(queryBean.getColumn4(), column4_update);
        Assert.assertEquals(queryBean.getColumn5(), column5_update);
    }

    /**
     * 测试-查找操作
     */
    public void testQuerySetting() {

        insertSetting();

        SettingBean queryBean = SettingServer.querySetting(key);
        Assert.assertNotNull(queryBean);// add by ycm for lint 2017/02/16
        Assert.assertEquals(queryBean.getKey(), key);
        Assert.assertEquals(queryBean.getValue(), value);
        Assert.assertEquals(queryBean.getColumn1(), column1);
        Assert.assertEquals(queryBean.getColumn2(), column2);
        Assert.assertEquals(queryBean.getColumn3(), column3);
        Assert.assertEquals(queryBean.getColumn4(), column4);
        Assert.assertEquals(queryBean.getColumn5(), column5);

    }

    /**
     * 测试-查找全部操作
     */
    public void testQueryAllSetting() {

        for (int i = 0; i < 100; i++) {
            SettingBean bean = new SettingBean();

            bean.setKey(key + i);
            bean.setValue(value + i);
            bean.setColumn1(column1 + i);
            bean.setColumn2(column2 + i);
            bean.setColumn3(column3 + i);
            bean.setColumn4(column4 + i);
            bean.setColumn5(column5 + i);

            SettingServer.insertSetting(bean);
        }

        String i = "11";
        SettingBean queryBean = SettingServer.querySetting(key + i);
        Assert.assertNotNull(queryBean);// add by ycm for lint 2017/02/16
        Assert.assertEquals(queryBean.getKey(), key + i);
        Assert.assertEquals(queryBean.getValue(), value + i);
        Assert.assertEquals(queryBean.getColumn1(), column1 + i);
        Assert.assertEquals(queryBean.getColumn2(), column2 + i);
        Assert.assertEquals(queryBean.getColumn3(), column3 + i);
        Assert.assertEquals(queryBean.getColumn4(), column4 + i);
        Assert.assertEquals(queryBean.getColumn5(), column5 + i);

    }


    /**
     * 测试-删除操作
     */
    public void testDeleteSetting() {

        insertSetting();

        SettingServer.deleteSetting(key);

        SettingBean queryBean = SettingServer.querySetting(key);
        Assert.assertEquals(queryBean, null);
    }


    /**
     * 插入操作
     */
    private void insertSetting() {

        SettingBean bean = new SettingBean();

        bean.setKey(key);
        bean.setValue(value);
        bean.setColumn1(column1);
        bean.setColumn2(column2);
        bean.setColumn3(column3);
        bean.setColumn4(column4);
        bean.setColumn5(column5);

        SettingServer.insertSetting(bean);

        SettingBean queryBean = SettingServer.querySetting(key);
        Assert.assertNotNull(queryBean);// add by ycm for lint 2017/02/16
        Assert.assertEquals(queryBean.getKey(), key);
        Assert.assertEquals(queryBean.getValue(), value);
        Assert.assertEquals(queryBean.getColumn1(), column1);
        Assert.assertEquals(queryBean.getColumn2(), column2);
        Assert.assertEquals(queryBean.getColumn3(), column3);
        Assert.assertEquals(queryBean.getColumn4(), column4);
        Assert.assertEquals(queryBean.getColumn5(), column5);

    }
}
