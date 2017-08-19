package com.xdja.presenter_mainframe.ui.uiInterface;

import com.xdja.frame.presenter.mvp.view.ActivityVu;
import com.xdja.presenter_mainframe.cmd.AccountSafeCommand;

/**
 * Created by ldy on 16/4/29.
 */
public interface VuAccountSafe extends ActivityVu<AccountSafeCommand>{
    /**
     * 填充账号信息
     * @param account
     */
    void setAccount(String account);

    /**
     * 填充手机号
     * @param mobile
     */
    void setMobile(String mobile);

    /**
     * 设置账号信息是否可以进行修改
     * @param isModify
     */
    void setAccountIsModify(boolean isModify);

    /**
     * 展示检测原密码的提示框
     */
    void showCheckPasswordDialog();

    /**
     * 清除密码提示框中内容
     */
    void clearPasswordWithDialog();

    /**
     * 清除dialog
     */
    void dismissDialog();

    /**
     * 按钮是否可点击
     */
    //[S]modify by xienana for click more than once caused problem @2016/09/26 [review by] tangsha
    void setEnableClick(boolean enableClick);
    //[E]modify by xienana for click more than once caused problem @2016/09/26 [review by] tangsha

    void setSafeLockState(String s);
}
