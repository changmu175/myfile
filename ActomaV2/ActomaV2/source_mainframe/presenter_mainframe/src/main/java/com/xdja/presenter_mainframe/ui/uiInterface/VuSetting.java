package com.xdja.presenter_mainframe.ui.uiInterface;


import android.graphics.Bitmap;

import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.presenter_mainframe.cmd.SettingCommand;

public interface VuSetting extends FragmentVu<SettingCommand> {

    /**
     * 修改三方加密服务状态
     *
     * @param isOn 是否开启状态
     */
    //void changeThirdServiceValue(boolean isOn);

    /**
     * 修改安全锁状态
     * @param isLockOn 是否开启安全锁
     */
    //void changeSafeLockValue(boolean isLockOn);

    /**
     * 我的设备个数
     *
     * @param count 设备个数
     */
    //void changeDevicesCount(int count);

    /**
     * 展示account信息
     *
     * @param accountInfo
     */
    void setAccountInfo(Account accountInfo);

    /**
     * 设置账号
     * @param account
     */
    void setAccount(String account);

    /**
     * 设置昵称
     *
     * @param nickName
     */
    void setNickName(String nickName);

    /**
     * 设置头像
     *
     * @param avatarId
     * @param thumbnailId
     */
    void setImage(String avatarId, String thumbnailId);

    /**
     * 修改头像后进行头像展示
     * @param bitmap
     */
    void showUserImage(Bitmap bitmap);

    void setUserImageBackground(Bitmap bitmap);

    /**
     * 刷新是否有更新内容
     *
     * @param isHaveNew
     */
    void freshUpdateNew(boolean isHaveNew);
}
