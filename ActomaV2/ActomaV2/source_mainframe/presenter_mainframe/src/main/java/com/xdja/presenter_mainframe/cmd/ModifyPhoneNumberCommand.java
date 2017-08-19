package com.xdja.presenter_mainframe.cmd;

import com.xdja.frame.presenter.mvp.Command;

/**
 * Created by ldy on 16/4/29.
 */
public interface ModifyPhoneNumberCommand extends Command {
    /**
     * 检验密码
     * @param password 密码
     * @param clickType 调起验证的类型 0更改手机号 1解绑手机号
     */
    void checkPassword(String password,int clickType);

    void unbindPhone();
}
