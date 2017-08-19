package com.xdja.comm.uitl;

import com.xdja.comm.data.QuickOpenAppBean;
import java.util.List;

/**
 * Created by geyao on 2015/8/11.
 * 状态参数
 */
public class StateParams {
    private static StateParams stateParams;
    /**
     * 第三方应用加密服务是否开启(默认false 值随设置变化而变化)
     */
    private boolean isSeverOpen = false;
    /**
     * 获取网络状态是否开启
     */
    private boolean isNetWorkOpen = false;
    /**
     * 获取选中的安通账号(有可能是群组ID)
     */
    private String encryptAccount;
    /**
     * 安全口令是否开启状态
     */
    private boolean isPassWordVertifyOpen = true;
    /**
     * 快速开启第三方应用开启状态
     */
    private boolean isQuickOpenThirdAppOpen = true;
    /**
     * pin码
     */
    private String pinCode;
    /**
     * 快速开启第三方应用列表
     */
    private List<QuickOpenAppBean> quickOpenAppBeanList;

    //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. Start
    /**
     * 当前选择支持加密的应用名
     */
    private String appName;
    //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. End

    /**
     * 当前选择支持加密的应用包名
     */
    private String pkgName;



    /**
     * 联系人通知关闭加密服务的时间
     */
    private long clickArcTime = -1;

    public long getClickArcTime() {
        return clickArcTime;
    }

    public void setClickArcTime(long clickArcTime) {
        this.clickArcTime = clickArcTime;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. Start
    public String getAppName() {
        return appName;
    }

    public void setAppName(String pkgName) {
        this.appName = pkgName;
    }

    //tangsha@xdja.com 2016-08-11 add. for get third encrypt app name to show in notification . review by self. End


    public List<QuickOpenAppBean> getQuickOpenAppBeanList() {
        return quickOpenAppBeanList;
    }

    public void setQuickOpenAppBeanList(List<QuickOpenAppBean> quickOpenAppBeanList) {
        this.quickOpenAppBeanList = quickOpenAppBeanList;
    }

    public void setIsPassWordVertifyOpen(boolean isPassWordVertifyOpen) {
        this.isPassWordVertifyOpen = isPassWordVertifyOpen;
    }

    public static StateParams getStateParams() {
        if (stateParams == null) {
            stateParams = new StateParams();
        }
        return stateParams;
    }

    public boolean isSeverOpen() {
        return isSeverOpen;
    }

    public void setIsSeverOpen(boolean isSeverOpen) {
        this.isSeverOpen = isSeverOpen;
//        //将大开关状态保存到本地服务中
//        IEncryptUtils.setEncryptSwitch(isSeverOpen);
    }

    public boolean isNetWorkOpen() {
        return isNetWorkOpen;
    }

    public void setIsNetWorkOpen(boolean isNetWorkOpen) {
        this.isNetWorkOpen = isNetWorkOpen;
    }

    public String getEncryptAccount() {
        return encryptAccount;
    }

    public void setEncryptAccount(String encryptAccount) {
        this.encryptAccount = encryptAccount;
        //发送事件通知加解密模块加密对象安通账号已修改
//        EncryptAccountBean bean = new EncryptAccountBean();
//        bean.setAccount(encryptAccount);
//        BusProvider.getMainProvider().post(bean);
    }

    public boolean isQuickOpenThirdAppOpen() {
        return isQuickOpenThirdAppOpen;
    }

    public void setIsQuickOpenThirdAppOpen(boolean isQuickOpenThirdAppOpen) {
        this.isQuickOpenThirdAppOpen = isQuickOpenThirdAppOpen;
    }

//    /**
//     * 用于通知加解密模块加密对象安通账号已修改
//     */
//    public static class EncryptAccountBean {
//        private String account;
//
//        public String getAccount() {
//            return account;
//        }
//
//        public void setAccount(String account) {
//            this.account = account;
//        }
//    }

//    /**
//     * 用于通知主框架关闭扇形菜单
//     */
//    public static class CloseMenu {
//
//    }
}
