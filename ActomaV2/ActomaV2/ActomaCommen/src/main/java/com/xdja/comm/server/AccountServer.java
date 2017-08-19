package com.xdja.comm.server;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.data.AccountBean;
import com.xdja.comm.data.AccountDao;
import com.xdja.dependence.uitls.LogUtil;

/**
 * <p>Summary:获取账户信息</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.atplus.server</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/7</p>
 * <p>Time:15:35</p>
 */
public class AccountServer {

    private static AccountBean accountBean;
    public static AccountBean accountBeanCompany = new AccountBean();//add by xienana for bug 8526
    /**
     * 获取安通账户信息
     *
     * @return 安通账户信息
     */
    @Nullable
    public static AccountBean getAccount() {
//        Context cxt = context == null ? ActomaController.getApp():context;

        AccountDao accountDao = null;
        try {
            accountDao = AccountDao.instance().open();
            return accountDao.queryFirst();// add by ycm for lint 2017/02/13
        } catch (Exception ex) {
            LogUtil.getUtils(ex.getMessage());
            return null;
        } finally {
            if (accountDao != null) {
                accountDao.close();
            }
        }
    }

    /**
     * 保存账户信息
     *
     * @param accountBean 账户信息实体
     * @return 是否保存成功/出错
     */
    public static boolean saveAccount(@Nullable AccountBean accountBean) {
        if (accountBean == null) {
            LogUtil.getUtils().i("参数不合法");
            return false;
        }
        AccountDao accountDao = null;
        try {
            accountDao = AccountDao.instance().open();
            return accountDao.save(accountBean);
        } catch (Exception ex) {
            LogUtil.getUtils().i(ex.getMessage());
            return false;
        } finally {
            if (accountDao != null) {
                accountDao.close();
            }
        }
    }

    /**
     * 更新账户信息
     *
     * @param accountBean 账户信息实体
     * @return 是否更新成功
     */
    public static boolean updateAccount(@Nullable AccountBean accountBean) {
        if (accountBean == null) {
            LogUtil.getUtils().i("参数不合法");
            return false;
        }
        AccountDao accountDao = null;
        try {
            accountDao = AccountDao.instance().open();
            return accountDao.update(accountBean);
        } catch (Exception ex) {
            LogUtil.getUtils().i(ex.getMessage());
            return false;
        } finally {
            if (accountDao != null) {
                accountDao.close();
            }
        }
    }

    /**
     * 更新账户某个字段
     *
     * @param field   字段名称
     * @param value   字段值
     * @return 是否成功
     */
    public static boolean updateAccountField(@AccountDao.ACCOUNT_FIELD String field, @Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            LogUtil.getUtils().i("参数不合法");
            return false;
        }
        AccountDao accountDao = null;
        try {
            accountDao = AccountDao.instance().open();
            return accountDao.updateField(field, value);
        } catch (Exception ex) {
            LogUtil.getUtils().i(ex.getMessage());
            return false;
        } finally {
            if (accountDao != null) {
                accountDao.close();
            }
        }

    }
}
