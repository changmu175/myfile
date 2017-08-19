package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.ModifyPhoneNumberCommand;

/**
 * Created by ldy on 16/4/29.
 */
public interface VuModifyPhoneNumber extends ActivityVu<ModifyPhoneNumberCommand> {
    void setPhoneNumber(String phoneNumber);

    /**
     * 弹出解绑手机号提示框
     */
    void showUnbindMobileDialog();

    /**
     * 清除验证密码提示框上面的密码
     */
    void clearPasswordWithDialog();

    /**
     * 去掉提示框
     */
    void dismissDialog();
}
