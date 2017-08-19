package com.xdja.comm.data;

import android.test.AndroidTestCase;

import com.xdja.comm.server.AccountServer;

import junit.framework.Assert;

import java.util.List;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.data</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/14</p>
 * <p>Time:15:03</p>
 */
public class AccountDaoTest extends AndroidTestCase {
    private String account = "abc1111111111";
    private String deviceName = "P8-1";
    private String nickName = "nick";
    private String mobile = "13500001111";
    private String mail = "321321@163.com";
    private String avatar = "sdfdffd.png";
    private String thumbnail = "sdfdf.png";
    private String avatarDownloadUrl = "http://10.10.13.41:9080/fileAgent/FileBreakDownload/2015-07-21_f296886d-854e-4e86-a911-84c37481363b";
    private String thumbnailDownloadUrl = "http://10.10.13.41:9080/fileAgent/FileBreakDownload/2015-07-21_f296886d-854e-4e86-a911-84c37481363b";
    private String nicknamePy = "gly";
    private String nicknamePinyin = "guanliyuan";
    private String companyCode = "001";

//    /**
//     * 数据库创建
//     */
//    public void testCreateAccountDb() {
//        boolean isSuc = insertAccount();
//        Assert.assertEquals(isSuc, true);
//    }

    private void insertAccount() {

        AccountBean bean = new AccountBean();
        bean.setAccount(account);
        bean.setDeviceName(deviceName);
        bean.setNickname(nickName);
        bean.setMobile(mobile);
        bean.setMail(mail);
        bean.setAvatar(avatar);
        bean.setThumbnail(thumbnail);
        bean.setAvatarDownloadUrl(avatarDownloadUrl);
        bean.setThumbnailDownloadUrl(thumbnailDownloadUrl);
        bean.setNicknamePy(nicknamePy);
        bean.setNicknamePinyin(nicknamePinyin);
        bean.setCompanyCode(companyCode);

        AccountServer.saveAccount(bean);

        AccountBean accountBean = AccountServer.getAccount();
        if (accountBean == null) {// add by ycm for lint 2017/02/15
            return;
        }
        Assert.assertEquals(accountBean.getAccount(), account);
        Assert.assertEquals(accountBean.getDeviceName(), deviceName);
        Assert.assertEquals(accountBean.getNickname(), nickName);
        Assert.assertEquals(accountBean.getMobile(), mobile);
        Assert.assertEquals(accountBean.getMail(), mail);
        Assert.assertEquals(accountBean.getAvatar(), avatar);
        Assert.assertEquals(accountBean.getThumbnail(), thumbnail);
        Assert.assertEquals(accountBean.getAvatarDownloadUrl(), avatarDownloadUrl);
        Assert.assertEquals(accountBean.getThumbnailDownloadUrl(), thumbnailDownloadUrl);
        Assert.assertEquals(accountBean.getNicknamePy(), nicknamePy);
        Assert.assertEquals(accountBean.getNicknamePinyin(), nicknamePinyin);
        Assert.assertEquals(accountBean.getCompanyCode(), companyCode);

    }

    /**
     * 账户信息插入
     */
    public void testAccountInsert() {

        insertAccount();

        insertAccount();

        List<AccountBean> accountBeanList = AccountDao.instance().open().query();
        if (accountBeanList != null && accountBeanList.size() > 0){// delete by ycm for lint 2017/02/16
            int size = accountBeanList.size();
            AccountDao.instance().close();

            Assert.assertEquals(size, 1);
        }
    }

    /**
     * 账户信息读取
     */
    public void testAccountRead() {

        AccountBean accountBean = AccountServer.getAccount();
        Assert.assertNotNull(accountBean);// add by ycm for lint 2017/02/16
        Assert.assertEquals(accountBean.getAccount(), account);
        Assert.assertEquals(accountBean.getDeviceName(), deviceName);
        Assert.assertEquals(accountBean.getNickname(), nickName);
        Assert.assertEquals(accountBean.getMobile(), mobile);
        Assert.assertEquals(accountBean.getMail(), mail);
        Assert.assertEquals(accountBean.getAvatar(), avatar);
        Assert.assertEquals(accountBean.getThumbnail(), thumbnail);
        Assert.assertEquals(accountBean.getAvatarDownloadUrl(), avatarDownloadUrl);
        Assert.assertEquals(accountBean.getThumbnailDownloadUrl(), thumbnailDownloadUrl);
        Assert.assertEquals(accountBean.getNicknamePy(), nicknamePy);
        Assert.assertEquals(accountBean.getNicknamePinyin(), nicknamePinyin);
        Assert.assertEquals(accountBean.getCompanyCode(), companyCode);
    }

    /**
     * 账户信息更新
     */
    public void testAccountUpdate() {

        //account不可修改
        String account_update = "abc1111111111";

        String deviceName_update = "P8-1_xg";
        String nickName_update = "nick_xg";
        String mobile_update = "13500001111_xg";
        String mail_update = "321321@163.com_xg";
        String avatar_update = "sdfdffd.png_xg";
        String thumbnail_update = "sdfdf.png_xg";
        String avatarDownloadUrl_update = "http://10.10.13.41:9080/fileAgent/FileBreakDownload/2015-07-21_f296886d-854e-4e86-a911-84c37481363b_xg";
        String thumbnailDownloadUrl_update = "http://10.10.13.41:9080/fileAgent/FileBreakDownload/2015-07-21_f296886d-854e-4e86-a911-84c37481363b_xg";
        String nicknamePy_update = "gly_xg";
        String nicknamePinyin_update = "guanliyuan_xg";
        String companyCode_update = "001_xg";

        insertAccount();

        AccountBean bean = new AccountBean();
        bean.setAccount(account_update);
        bean.setDeviceName(deviceName_update);
        bean.setNickname(nickName_update);
        bean.setMobile(mobile_update);
        bean.setMail(mail_update);
        bean.setAvatar(avatar_update);
        bean.setThumbnail(thumbnail_update);
        bean.setAvatarDownloadUrl(avatarDownloadUrl_update);
        bean.setThumbnailDownloadUrl(thumbnailDownloadUrl_update);
        bean.setNicknamePy(nicknamePy_update);
        bean.setNicknamePinyin(nicknamePinyin_update);
        bean.setCompanyCode(companyCode_update);

        AccountServer.updateAccount(bean);

        AccountBean accountBean = AccountServer.getAccount();
        Assert.assertNotNull(accountBean);// add by ycm for lint 2017/02/16
        Assert.assertEquals(accountBean.getAccount(), account_update);
        Assert.assertEquals(accountBean.getDeviceName(), deviceName_update);
        Assert.assertEquals(accountBean.getNickname(), nickName_update);
        Assert.assertEquals(accountBean.getMobile(), mobile_update);
        Assert.assertEquals(accountBean.getMail(), mail_update);
        Assert.assertEquals(accountBean.getAvatar(), avatar_update);
        Assert.assertEquals(accountBean.getThumbnail(), thumbnail_update);
        Assert.assertEquals(accountBean.getAvatarDownloadUrl(), avatarDownloadUrl_update);
        Assert.assertEquals(accountBean.getThumbnailDownloadUrl(), thumbnailDownloadUrl_update);
        Assert.assertEquals(accountBean.getNicknamePy(), nicknamePy_update);
        Assert.assertEquals(accountBean.getNicknamePinyin(), nicknamePinyin_update);
        Assert.assertEquals(accountBean.getCompanyCode(), companyCode_update);
    }

    public void testAccountFieldUpdate() {

        insertAccount();

        String update_field_mobile = "1223334444";
        String update_field_mail = "321321@163.com";
        String update_field_nickName = "张小三";

        AccountServer.updateAccountField(AccountDao.FIELD_MOBILE, update_field_mobile);
        AccountServer.updateAccountField(AccountDao.FIELD_MAIL, update_field_mail);
        AccountServer.updateAccountField(AccountDao.FIELD_NICKNAME, update_field_nickName);

        AccountBean accountBean = AccountServer.getAccount();
        Assert.assertNotNull(accountBean);// add by ycm for lint 2017/02/16
        Assert.assertEquals(accountBean.getMobile(), update_field_mobile);
        Assert.assertEquals(accountBean.getMail(), update_field_mail);
        Assert.assertEquals(accountBean.getNickname(), update_field_nickName);
    }
}
