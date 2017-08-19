package com.xdja.comm.data;

import com.xdja.comm.ApplicationTest;
import com.xdja.comm.server.AppInfoServer;

import junit.framework.Assert;

import java.util.List;

/**
 * Created by geyao on 2015/7/25.
 */
public class AppInfoDaoTest extends ApplicationTest {

    private String appId = "1";
    private String packageName = "com.example.xdja";
    private String versionName = "V1.2.3";
    private String versionCode = "123";
    private String state = "true";
    private String isHaveApk = "false";
    private String isDownNow = "2";
    private String downSize = "100";
    private String fileName = "xxx.apk";
    private String appSize = "1000000";
    private String downloadUrl = "http://11.12.110.125/safekeyservice-release-no1.0.0.4.apk";
    private String percentage = String.valueOf((int) (Double.parseDouble(downSize) / Double.parseDouble(appSize) * 100));

    /**
     * 测试-插入操作
     */
    public void testInsert() {

        AppInfoDao.instance().open().clear();

        insertData();

        AppInfoBean bean = AppInfoServer.queryAppInfo(appId);

        Assert.assertEquals(bean.getAppId(), appId);
        Assert.assertEquals(bean.getPackageName(), packageName);
        Assert.assertEquals(bean.getVersionName(), versionName);
        Assert.assertEquals(bean.getVersionCode(), versionCode);
        Assert.assertEquals(bean.getState(), state);
        Assert.assertEquals(bean.getIsHaveApk(), isHaveApk);
        Assert.assertEquals(bean.getIsDownNow(), isDownNow);
        Assert.assertEquals(bean.getDownSize(), downSize);
        Assert.assertEquals(bean.getFileName(), fileName);
        Assert.assertEquals(bean.getAppSize(), appSize);
        Assert.assertEquals(bean.getDownloadUrl(), downloadUrl);
        Assert.assertEquals(bean.getPercentage(), percentage);
    }

    /**
     * 测试-删除操作
     */
    public void testDelete() {

        AppInfoDao.instance().open().clear();

        insertData();

        AppInfoServer.deleteAppInfo(appId);

        AppInfoBean bean = AppInfoServer.queryAppInfo(appId);

        Assert.assertEquals(bean, null);
    }

    /**
     * 测试-查询操作
     */
    public void testQuery() {

        AppInfoDao.instance().open().clear();

        insertData();

        AppInfoBean bean = AppInfoServer.queryAppInfo(appId);

        Assert.assertEquals(bean.getAppId(), appId);
        Assert.assertEquals(bean.getPackageName(), packageName);
        Assert.assertEquals(bean.getVersionName(), versionName);
        Assert.assertEquals(bean.getVersionCode(), versionCode);
        Assert.assertEquals(bean.getState(), state);
        Assert.assertEquals(bean.getIsHaveApk(), isHaveApk);
        Assert.assertEquals(bean.getIsDownNow(), isDownNow);
        Assert.assertEquals(bean.getDownSize(), downSize);
        Assert.assertEquals(bean.getFileName(), fileName);
        Assert.assertEquals(bean.getAppSize(), appSize);
        Assert.assertEquals(bean.getDownloadUrl(), downloadUrl);
        Assert.assertEquals(bean.getPercentage(), percentage);
    }

    /**
     * 测试-查询全部操作
     */
    public void testQueryAll() {

        AppInfoDao.instance().open().clear();

        AppInfoBean bean = new AppInfoBean();
        bean.setAppId("12");
        bean.setPackageName("com.example.xdja2");
        bean.setVersionName("V1.2.32");
        bean.setVersionCode("1232");
        bean.setState("true2");
        bean.setIsHaveApk("false2");
        bean.setIsDownNow("22");
        bean.setDownSize("666");
        bean.setFileName("bbb");
        bean.setAppSize("666666");
        bean.setDownloadUrl("123123");
        bean.setPercentage(String.valueOf((int) (Double.parseDouble("666") / Double.parseDouble("666666") * 100)));

        AppInfoBean bean1 = new AppInfoBean();
        bean1.setAppId("123");
        bean1.setPackageName("com.example.xdja23");
        bean1.setVersionName("V1.2.323");
        bean1.setVersionCode("12323");
        bean1.setState("true23");
        bean1.setIsHaveApk("false23");
        bean1.setIsDownNow("223");
        bean1.setDownSize("6666");
        bean1.setFileName("ccc");
        bean1.setAppSize("666666");
        bean1.setDownloadUrl("321321");
        bean1.setPercentage(String.valueOf((int) (Double.parseDouble("6666") / Double.parseDouble("666666") * 100)));

        AppInfoServer.insertAppInfo(bean);
        AppInfoServer.insertAppInfo(bean1);

        List<AppInfoBean> list = AppInfoServer.queryAllAppInfo();
        Assert.assertNotNull(list);// add by ycm for lint 2017/02/16
        Assert.assertEquals(list.size(), 2);

        AppInfoBean infoBean = AppInfoServer.queryAppInfo("12");

        Assert.assertEquals(infoBean.getAppId(), bean.getAppId());
        Assert.assertEquals(infoBean.getPackageName(), bean.getPackageName());
        Assert.assertEquals(infoBean.getVersionName(), bean.getVersionName());
        Assert.assertEquals(infoBean.getVersionCode(), bean.getVersionCode());
        Assert.assertEquals(infoBean.getState(), bean.getState());
        Assert.assertEquals(infoBean.getIsHaveApk(), bean.getIsHaveApk());
        Assert.assertEquals(infoBean.getIsDownNow(), bean.getIsDownNow());
        Assert.assertEquals(infoBean.getDownSize(), bean.getDownSize());
        Assert.assertEquals(infoBean.getFileName(), bean.getFileName());
        Assert.assertEquals(infoBean.getAppSize(), bean.getAppSize());
        Assert.assertEquals(infoBean.getDownloadUrl(), bean.getDownloadUrl());
        Assert.assertEquals(infoBean.getPercentage(), bean.getPercentage());
    }

    /**
     * 测试-更新操作
     */
    public void testUpdate() {

        String packageName_update = "com.example.xdja_xg";
        String versionName_update = "V1.2.3_xg";
        String versionCode_update = "123_xg";
        String state_update = "true_xg";
        String isHaveApk_update = "false_xg";
        String isDownNow_update = "2_xg";
        String downSize_update = "66";
        String fileName_update = "xxx.apk_xg";
        String appSize_update = "666666";
        String downloadUrl_update = "http://11.12.110.125/safekeyservice-release-no1.0.0.4.apk_xg";
        String percentage = String.valueOf((int) (Double.parseDouble("66") / Double.parseDouble("666666") * 100));

        insertData();

        AppInfoBean bean = new AppInfoBean();
        bean.setAppId(appId);
        bean.setPackageName(packageName_update);
        bean.setVersionName(versionName_update);
        bean.setVersionCode(versionCode_update);
        bean.setState(state_update);
        bean.setIsHaveApk(isHaveApk_update);
        bean.setIsDownNow(isDownNow_update);
        bean.setDownSize(downSize_update);
        bean.setFileName(fileName_update);
        bean.setAppSize(appSize_update);
        bean.setDownloadUrl(downloadUrl_update);
        bean.setPercentage(percentage);

        AppInfoServer.updateAppInfo(bean);

        AppInfoBean appInfoBean = AppInfoServer.queryAppInfo(appId);

        Assert.assertEquals(appInfoBean.getAppId(), appId);
        Assert.assertEquals(appInfoBean.getPackageName(), packageName_update);
        Assert.assertEquals(appInfoBean.getVersionName(), versionName_update);
        Assert.assertEquals(appInfoBean.getVersionCode(), versionCode_update);
        Assert.assertEquals(appInfoBean.getState(), state_update);
        Assert.assertEquals(appInfoBean.getIsHaveApk(), isHaveApk_update);
        Assert.assertEquals(appInfoBean.getIsDownNow(), isDownNow_update);
        Assert.assertEquals(appInfoBean.getDownSize(), downSize_update);
        Assert.assertEquals(appInfoBean.getFileName(), fileName_update);
        Assert.assertEquals(appInfoBean.getAppSize(), appSize_update);
        Assert.assertEquals(appInfoBean.getDownloadUrl(), downloadUrl_update);
        Assert.assertEquals(appInfoBean.getPercentage(), percentage);
    }

    public void testUpdateFile() {
        insertData();
        AppInfoServer.updateAppInfoField(appId, AppInfoDao.FIELD_ISDOWNNOW, "3");
        AppInfoBean bean = AppInfoServer.queryAppInfo(appId);
        Assert.assertNotNull(bean);
        Assert.assertEquals(bean.getIsDownNow(), "3");
    }

    /**
     * 插入数据
     */
    private void insertData() {

        AppInfoBean bean = new AppInfoBean();
        bean.setAppId(appId);
        bean.setPackageName(packageName);
        bean.setVersionName(versionName);
        bean.setVersionCode(versionCode);
        bean.setState(state);
        bean.setIsHaveApk(isHaveApk);
        bean.setIsDownNow(isDownNow);
        bean.setDownSize(downSize);
        bean.setFileName(fileName);
        bean.setAppSize(appSize);
        bean.setDownloadUrl(downloadUrl);
        bean.setPercentage(percentage);

        AppInfoServer.insertAppInfo( bean);

        AppInfoBean appInfoBean = AppInfoServer.queryAppInfo(appId);

        Assert.assertEquals(appInfoBean.getAppId(), appId);
        Assert.assertEquals(appInfoBean.getPackageName(), packageName);
        Assert.assertEquals(appInfoBean.getVersionName(), versionName);
        Assert.assertEquals(appInfoBean.getVersionCode(), versionCode);
        Assert.assertEquals(appInfoBean.getState(), state);
        Assert.assertEquals(appInfoBean.getIsHaveApk(), isHaveApk);
        Assert.assertEquals(appInfoBean.getIsDownNow(), isDownNow);
        Assert.assertEquals(appInfoBean.getDownSize(), downSize);
        Assert.assertEquals(appInfoBean.getFileName(), fileName);
        Assert.assertEquals(appInfoBean.getAppSize(), appSize);
        Assert.assertEquals(appInfoBean.getDownloadUrl(), downloadUrl);
        Assert.assertEquals(appInfoBean.getPercentage(), percentage);
    }
}
