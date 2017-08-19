package com.xdja.presenter_mainframe.util;

import android.content.Context;

import com.xdja.comm.server.PreferencesServer;

/**
 * Created by chenbing on 2015/7/8.
 */
public class SharePreferceUtil {

    //静态对象
    private static SharePreferceUtil util;

    private PreferencesServer wrapper;

    public static SharePreferceUtil getPreferceUtil(Context context) {
        if (util == null) {
            util = new SharePreferceUtil(context);
        }
        return util;
    }

    private SharePreferceUtil(Context context) {
        wrapper = PreferencesServer.getWrapper(context);
    }

    /**
     * 是否不是首次登录
     */
    private static final String IS_FIRST_USE = "isFirstUse";
    /**
     * 是否展示登录引导页面
     */
    private static final String IS_SHOW_LOGIN_GUIDE = "isShowLoginGuide";
    /**
     * 是否显示隐私条款
     */
    private static final String IS_SHOW_PROTOCAL = "isShowProtocal";
// [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//    /**
//     * 是否有更新
//     */
//    private static final String UPDATE_VERSION = "UpdateVersion";
// [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
    /**
     * 是否是首次进入主框架页面
     */
    private static final String IS_FIRST_IN_MAIN = "isFirstInMain";
    /**
     * 是否是首次打开第三方加密服务
     */
    private static final String IS_FIRST_OPEN_SEVER = "isFirstOpenSever";
    /**
     * 强制更新是否要删除数据库和当前升级后的版本号
     */
    private static final String IS_DELETE_DB = "isDeleteDb";

    private static final String DELETE_VERSION = "deleteVersion";

    public static final String AT_ACCOUNT = "at_account";

    /**
     * 需要弹出的强制退出的提示信息
     */
    public static final String LOGOUT_MESSAGE = "logout_message";

    /**
     * 安全锁开关
     */
    public static final String SAFELOCK = "safe_lock";

    /**
     * 锁屏锁定
     */
    public static final String SAFELOCK_TYPE_LOCKSCREEN = "safeLockType_lockScreen";

    /**
     * 后台运行锁定
     */
    public static final String SAFELOCK_TYPE_BACKGROUND = "safeLockType_backGround";


    /**
     * 设备信息
     */
    public static final String ALL_DEVICES_MESSAGE = "allDevicesMessage";


    /**
     * 绑定设备标识
     */
    public static final String IS_BIND_DEVICE = "bind_device";

    /**
     * 解绑设备
     */
    public static final String IS_UNBIND_DEVICE = "unbind_device";
// [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//    /**
//     * 是否显示版本更新的new字样
//     */
//    public static final String IS_SHOW_NEW_VIEW = "is_show_new_view";
// [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
    /**
     * 用于保存检测到的版本
     */
    public static final String NEW_VERSION = "new_version";


    // [Start] add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. fix bug #2477.
    /**
     * 更新前的版本
     */
    private static final String PREVIOUS_VERSION = "previousVersion";
    // [End] add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. fix bug #2477.

    // [S]add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-30.
    /**
     * 是否清理了V1的数据
     */
    private static final String IS_CLEAN_V1_DATA = "isCleanV1Data";
    // [E]add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-30.

    //add by gbc for statistics skip bind mobile. 2016-11-30 begin
    /**
     * 跳过绑定手机号码
     */
    private static final String IS_SKIP_BINDMOBILE = "isSkipBindMobile";
    //add by gbc for statistics skip bind mobile. 2016-11-30 end
    //add by gbc for close actoma. 2016-12-03 end
    /**
     * 是否选择关闭应用
     */
    private static final String IS_CLOSE_ACTOMA = "isCloseActoma";

    /**
     * 关闭应用选择模式，是否IM继续接收消息
     */
    private static final String CLOSE_APP_MODE = "closeMode";
    //add by gbc for close actoma. 2016-12-03 end
// [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//    public boolean getIsShowNewView() {
//        return wrapper.gPrefBooleanValue(IS_SHOW_NEW_VIEW, true);
//    }
//
//    public void setIsShowNewView(boolean isShow) {
//        wrapper.setPreferenceBooleanValue(IS_SHOW_NEW_VIEW, isShow);
//    }
// [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.

    public String getNewVersion() {
        return wrapper.gPrefStringValue(NEW_VERSION);
    }

    public void setNewVersion(String version) {
        wrapper.setPreferenceStringValue(NEW_VERSION, version);
    }

    // [S]add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-30.
    public boolean getIsCleanV1Data() {
        return wrapper.gPrefBooleanValue(IS_CLEAN_V1_DATA, false);
    }

    public void setIsCleanV1Data(boolean isClean) {
        wrapper.setPreferenceBooleanValue(IS_CLEAN_V1_DATA, isClean);
    }
    // [E]add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-30.

    //add by gbc for statistics skip bind mobile. 2016-11-30 begin
    public boolean getIsSkipBindMobile() {
        return wrapper.gPrefBooleanValue(IS_SKIP_BINDMOBILE, false);
    }

    public void setIsSkipBindMobile(boolean isSkip) {
        wrapper.setPreferenceBooleanValue(IS_SKIP_BINDMOBILE, isSkip);
    }
    //add by gbc for statistics skip bind mobile. 2016-11-30 end

    //add by gbc for close actoma. 2016-12-03 begin
    public boolean getIsCloseActoma() {
        return wrapper.gPrefBooleanValue(IS_CLOSE_ACTOMA, false);
    }

    public void setIsCloseActoma(boolean isCloseActoma) {
        wrapper.setPreferenceBooleanValue(IS_CLOSE_ACTOMA, isCloseActoma);
    }

    public int getCloseActomaMode() {
        return wrapper.gPrefIntValue(CLOSE_APP_MODE);
    }

    public void setCloseActomaMode(int mode) {
        wrapper.setPreferenceIntValue(CLOSE_APP_MODE, mode);
    }
    //add by gbc for close actoma. 2016-12-03 end
    // [Start] add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. fix bug #2477.
    public String getPreviousVersion() {
        return wrapper.gPrefStringValue(PREVIOUS_VERSION);
    }

    public void setPreviousVersion(String version) {
        wrapper.setPreferenceStringValue(PREVIOUS_VERSION, version);
    }
    // [End] add by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. fix bug #2477.

    public String getLogoutMessage() {
        return wrapper.gPrefStringValue(LOGOUT_MESSAGE);
    }

    public void setLogoutMessage(String logoutMessage) {
        wrapper.setPreferenceStringValue(LOGOUT_MESSAGE, logoutMessage);
    }

    public String getAccount() {
        return wrapper.gPrefStringValue(AT_ACCOUNT);
    }

    public void setAccount(String account) {
        wrapper.setPreferenceStringValue(AT_ACCOUNT, account);
    }

    /**
     * 是否在登录界面之前的界面收到绑定设备通知
     */
    public void setIsBindDevice(boolean isFirstUse) {
        wrapper.setPreferenceBooleanValue(IS_BIND_DEVICE, isFirstUse);
    }

    public boolean getIsBindDevice() {
        return wrapper.gPrefBooleanValue(IS_BIND_DEVICE, false);
    }


    /**
     * 设备信息
     *
     * @param allDevicesMessage
     */
    public void setAllDevicesMessage(String allDevicesMessage) {
        wrapper.setPreferenceStringValue(ALL_DEVICES_MESSAGE, allDevicesMessage);
    }

    public String getAllDevicesMessage() {
        return wrapper.gPrefStringValue(ALL_DEVICES_MESSAGE);
    }

    /**
     * 是否首次使用
     */
    public void setIsFirstUse(boolean isFirstUse) {
        wrapper.setPreferenceBooleanValue(IS_FIRST_USE, isFirstUse);
    }

    public boolean getIsFirstUse() {
        return wrapper.gPrefBooleanValue(IS_FIRST_USE, true);
    }

    /**
     * 是否展示登录引导界面
     */
    public void setIsShowLoginGuide(boolean isShowLoginGuide) {
        wrapper.setPreferenceBooleanValue(IS_SHOW_LOGIN_GUIDE, isShowLoginGuide);
    }

    public boolean getIsShowLoginGuide() {
        return wrapper.gPrefBooleanValue(IS_SHOW_LOGIN_GUIDE, true);
    }

    /**
     * 是否在登录页面显示隐私条款的内容
     *
     * @param isShowProtocal
     */
    public void setIsShowProtocal(boolean isShowProtocal) {
        wrapper.setPreferenceBooleanValue(IS_SHOW_PROTOCAL, isShowProtocal);
    }

    public boolean getIsShowProtocal() {
        return wrapper.gPrefBooleanValue(IS_SHOW_PROTOCAL, true);
    }
// [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//    /**
//     * 是否有更新
//     */
//    public void setUpdateVersion(String updateVersion) {
//        wrapper.setPreferenceStringValue(UPDATE_VERSION, updateVersion);
//    }
//
//    public String getUpdateVersion() {
//        return wrapper.gPrefStringValue(UPDATE_VERSION);
//    }
// [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
    /**
     * 设置是否是首次进入主框架界面
     *
     * @param isFirst 是否是首次进入主框架界面
     */
    public void setIsFirstInMain(boolean isFirst) {
        wrapper.setPreferenceBooleanValue(IS_FIRST_IN_MAIN, isFirst);
    }

    /**
     * 获取是否是首次进入主框架界面
     *
     * @return 是否是首次进入主框架界面
     */
    public boolean getIsFirstInMain() {
        return wrapper.gPrefBooleanValue(IS_FIRST_IN_MAIN, true);
    }

    /**
     * 设置是否是首次打开第三方加密服务
     *
     * @param isFirst 是否是首次打开第三方加密服务
     */
    public void setIsFirstOpenSever(boolean isFirst) {
        wrapper.setPreferenceBooleanValue(IS_FIRST_OPEN_SEVER, isFirst);
    }

    /**
     * 获取是否是首次打开第三方加密服务
     *
     * @return 是否是首次打开第三方加密服务
     */
    public boolean getIsFirstOpenSever() {
        return wrapper.gPrefBooleanValue(IS_FIRST_OPEN_SEVER, true);
    }

    /**
     * 更新后是否删除数据库
     *
     * @param isDeleteDb
     */
    public void setIsDeleteDb(boolean isDeleteDb) {
        wrapper.setPreferenceBooleanValue(IS_DELETE_DB, isDeleteDb);
    }

    public boolean getIsDeleteDb() {
        return wrapper.gPrefBooleanValue(IS_DELETE_DB, false);
    }

    /**
     * 获取应该升级到的版本号
     *
     * @param deleteVersion
     */
    public void setDeleteVersion(String deleteVersion) {
        wrapper.setPreferenceStringValue(DELETE_VERSION, deleteVersion);
    }

    public String getDeleteVersion() {
        return wrapper.gPrefStringValue(DELETE_VERSION);
    }

    /**
     * 设置安全锁开关
     *
     * @param safeLockIsOpen 是否开启
     */
    public void setSafeLock(boolean safeLockIsOpen) {
        wrapper.setPreferenceBooleanValue(SAFELOCK, safeLockIsOpen);
    }

    /**
     * 获取安全锁开关
     *
     * @return
     */
    public boolean getSafeLock() {
        return wrapper.gPrefBooleanValue(SAFELOCK, false);
    }

    /**
     * 设置锁屏锁定开关
     *
     * @param safeLockIsOpen 是否开启
     */
    public void setSafeLockType_lockScreen(boolean safeLockIsOpen) {
        wrapper.setPreferenceBooleanValue(SAFELOCK_TYPE_LOCKSCREEN, safeLockIsOpen);
    }

    /**
     * 获取锁屏锁定开关
     *
     * @return
     */
    public boolean getSafeLockType_lockScreen() {
        return wrapper.gPrefBooleanValue(SAFELOCK_TYPE_LOCKSCREEN, false);
    }

    /**
     * 设置后台运行开关
     *
     * @param safeLockIsOpen 是否开启
     */
    public void setSafeLockType_backGround(boolean safeLockIsOpen) {
        wrapper.setPreferenceBooleanValue(SAFELOCK_TYPE_BACKGROUND, safeLockIsOpen);
    }

    /**
     * 获取后台运行开关
     *
     * @return
     */
    public boolean getSafeLockType_backGround() {
        return wrapper.gPrefBooleanValue(SAFELOCK_TYPE_BACKGROUND, true);
    }

	//add by mengbo. 2016-09-27. begin
    public void clearPreference() {
        if (null != wrapper) {
            wrapper.clearPreference();
        }
    }
	//add by mengbo. 2016-09-27. end
}
