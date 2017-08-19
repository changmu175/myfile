package com.xdja.presenter_mainframe.ui.uiInterface;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.UserDetailCommand;


/**
 * Created by geyao on 2015/7/7.
 */
public interface VuUserDetail extends ActivityVu<UserDetailCommand> {
    /**
     * 设置用户头像
     *
     * @param avatarId
     * @param thumbnailId
     */
    //modify by alh@xdja.com to fix bug: 613 2016-06-21 start(rummager:wangchao1)
    void setUserImg(String avatarId, String thumbnailId , boolean reload , boolean showDefaultImage);
    //modify by alh@xdja.com to fix bug: 613 2016-06-21 end(rummager:wangchao1)
    /**
     * 修改头像后进行头像展示
     * @param bitmap
     */
    void showUserImage(Bitmap bitmap);

    /**
     * 设置用户昵称
     *
     * @param userNickName 用户昵称
     */
    void setUserNickName(@Nullable String userNickName);

    /**
     * 设置安通账号
     * @param at 用户安通账号
     * @param isCustomAccount 是否是自定义的安通账号
     */
    void setAT(@Nullable String at,@Nullable boolean isCustomAccount);

    /**
     * 设置用户二维码名片
     */
    void setUserQrImg();

    /**
     * 设置用户手机号码
     *
     * @param mobileNum 用户手机号码
     */
    void setUserMobileNum(String mobileNum);

    /**
     * 显示progress等待框
     *
     * @param msg 要显示的文字
     */
    void showProgressDialog(String msg);

    /**
     * 隐藏progress等待框
     */
    void hideProgressDialog();

    /**
     * Pop是否显示
     * @return
     */
    boolean isShow();

    void dismiss();
}
