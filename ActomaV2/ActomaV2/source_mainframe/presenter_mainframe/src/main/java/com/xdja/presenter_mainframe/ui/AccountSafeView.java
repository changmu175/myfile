package com.xdja.presenter_mainframe.ui;

import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AccountSafeCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuAccountSafe;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.SettingBarView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/4/29.
 */
@ContentView(R.layout.activity_account_safe)
public class AccountSafeView extends ActivityView<AccountSafeCommand> implements VuAccountSafe {

    private String passwordDigits = "";

    @Override
    protected int getToolbarType() {
        return ActivityView.ToolbarDef.NAVIGATE_BACK;
    }

    @Bind(R.id.sb_account_safe_account)
    SettingBarView sbAccountSafeAccount;
    @Bind(R.id.sb_account_safe_phone)
    SettingBarView sbAccountSafePhone;
    @Bind(R.id.sb_account_safe_safety_lock)
    SettingBarView sbAccountSafeLock;

    private EditText etPassword;

    private XDialog checkPasswordDialog;

    @OnClick(R.id.sb_account_safe_login_device_manager)
    void loginDeviceManager(){
        getCommand().loginDeviceManager();
    }

    @OnClick(R.id.sb_account_safe_account)
    void account(){
        getCommand().setActomaAccount();
    }
    @OnClick(R.id.sb_account_safe_phone)
    void mobile(){
        getCommand().setPhoneNumber();
    }
    @OnClick(R.id.sb_account_safe_modify_password)
    void password(){
        showCheckPasswordDialog();
    }

    @OnClick(R.id.sb_account_safe_safety_lock)
    void setSafeLock(){
        getCommand().setSafeLock();
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2088 . review by wangchao1. Start
    @Override
    public void onCreated() {
        super.onCreated();
        if (sbAccountSafeAccount != null) {
            sbAccountSafeAccount.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_LIST,
                    0, 0, 0, getStringRes(R.string.at_account)));
        }
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2088 . review by wangchao1. End

    @Override
    public void setAccount(String account) {
        if (!TextUtils.isEmpty(account)) {
            sbAccountSafeAccount.setSecondText(account);
        }
    }

    @Override
    public void setMobile(String mobile) {
        if (mobile!=null)
            sbAccountSafePhone.setSecondText(mobile);

    }

    @Override
    public void setAccountIsModify(boolean isModify) {
        sbAccountSafeAccount.setImgIsShow(isModify);
    }

    @Override
    public void showCheckPasswordDialog() {
        if(checkPasswordDialog == null){
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_view, null);
            etPassword = (EditText) view.findViewById(R.id.edt_dialog_input_view);
            TextView edtDialogMessage = (TextView) view.findViewById(R.id.edt_dialog_message);
            edtDialogMessage.setText(getStringRes(R.string.setting_check_password_tips_message));
            edtDialogMessage.setVisibility(View.VISIBLE);
            checkPasswordDialog = new XDialog(getActivity());
            //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-08 add. fix bug 2548 . review by wangchao1. Start
            checkPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    checkPasswordDialog = null;
                }
            });
            //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-08 add. fix bug 2548 . review by wangchao1. End
            //[S]modify by xienana for check password input and click more than once caused problem @2016/09/26 [review by] tangsha
            final EditText edtDialogInput = (EditText) view.findViewById(R.id.edt_dialog_input_view);
            passwordDigits = getStringRes(R.string.password_digits);
            edtDialogInput.setKeyListener(new NumberKeyListener() {
                @Override
                protected char[] getAcceptedChars() {
                    char[] numberChars = passwordDigits.toCharArray();
                    return numberChars;
                }
                @Override
                public int getInputType() {
                    return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
                }
            });
            checkPasswordDialog.setTitle(R.string.setting_check_password_tips_title)
                    .setCustomContentView(view)
                    .setPositiveButton(getStringRes(R.string.certain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setEnableClick(false);
                            if(!TextUtil.isRulePassword(edtDialogInput.getText().toString())){
                                showToast(getStringRes(R.string.password_format));
                                clearPasswordWithDialog();
                                setEnableClick(true);
                            }else{
                                getCommand().checkPassword(edtDialogInput.getText().toString());
                            }
                        }
                    })
                    .setNegativeButton(getStringRes(R.string.cancel),null)
                    .show();
        } //[E]modify by xienana for check password input and click more than once caused problem @2016/09/26 [review by] tangsha
    }

    @Override
    public void clearPasswordWithDialog() {
        //输入框中重置为空
        etPassword.setText("");
        //弹出输入框
        etPassword.setFocusable(true);
        etPassword.setFocusableInTouchMode(true);
        etPassword.requestFocus();
        etPassword.requestFocusFromTouch();
    }

    @Override
    public void dismissDialog() {
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-18 add. fix bug 4176 . review by wangchao1. Start
        if (checkPasswordDialog != null) {
            checkPasswordDialog.dismiss();
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-09-18 add. fix bug 4176 . review by wangchao1. End
    }

    //[S]modify by xienana for click more than once caused problem @2016/09/26 [review by] tangsha
    @Override
    public void setEnableClick(boolean enableClick) {
        if (checkPasswordDialog != null) {
            if (enableClick) {
                checkPasswordDialog.getmPositiveButton().setEnabled(true);
            } else {
                checkPasswordDialog.getmPositiveButton().setEnabled(false);
            }
        }
    }//[E]modify by xienana for click more than once caused problem @2016/09/26 [review by] tangsha

    @Override
    public void setSafeLockState(String s) {
        sbAccountSafeLock.setSecondText(s);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_account_safe);
    }
    /*[E]modify by tangsha@20161011 for multi language*/

}
