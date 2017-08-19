package com.xdja.presenter_mainframe.ui;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ModifyPhoneNumberCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuModifyPhoneNumber;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/4/29.
 */
@ContentView(R.layout.activity_modify_phone_number)
public class ModifyPhoneNumberView extends ActivityView<ModifyPhoneNumberCommand> implements VuModifyPhoneNumber {
    @Bind(R.id.tv_modify_phone_number_phone)
    TextView tvPhoneNumber;

    //验证密码输入框
    EditText etPassword;
    XDialog checkPasswordDialog;

    //更改手机号
    public static final int MODIFY_MOBILE_TYPE = 0;
    //解绑手机号
    public static final int UNBIND_MOBILE_TYPE = 1;

    @Override
    protected int getToolbarType() {
        return ActivityView.ToolbarDef.NAVIGATE_BACK;
    }

    @OnClick(R.id.btn_modify_phone_number_modify)
    public void modifyPhone() {
        checkPassword(MODIFY_MOBILE_TYPE);
    }

    @OnClick(R.id.pctv_modify_phone_number_unbind)
    public void unbindPhone() {
        checkPassword(UNBIND_MOBILE_TYPE);
    }

    /**
     * 弹出验证密码的弹出框
     * @param type 0 更换手机号 1 解绑手机号
     */
    public void checkPassword(final int type){
        if(checkPasswordDialog != null){
            return;
        }
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_view, null);
        etPassword = (EditText) view.findViewById(R.id.edt_dialog_input_view);
        checkPasswordDialog = new XDialog(getActivity());
        //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-10 add. fix bug 2624 . review by wangchao1. Start
        checkPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                checkPasswordDialog = null;
            }
        });
        //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-10 add. fix bug 2624 . review by wangchao1. End
        checkPasswordDialog.setTitle(R.string.check_password_tip)
                .setCustomContentView(view)
                .setPositiveButton(getStringRes(R.string.certain), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getCommand().checkPassword(
                                ((EditText)view.findViewById(R.id.edt_dialog_input_view)).getText().toString()
                        ,type);
                    }
                })
                .setNegativeButton(getStringRes(R.string.cancel),null)
                .show();
    }



    @Override
    public void setPhoneNumber(String phoneNumber) {
        tvPhoneNumber.setText(phoneNumber);
    }

    @Override
    public void showUnbindMobileDialog() {
        final XDialog dialog = new XDialog(getContext());
        dialog.setTitle(R.string.unbind_phone_tips_title).setMessage(R.string.unbind_phone_tips_message)
                .setPositiveButton(getStringRes(R.string.certain), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        getCommand().unbindPhone();
                    }
                }).setNegativeButton(getStringRes(R.string.cancel),null)
                .show();
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
        if (checkPasswordDialog != null && checkPasswordDialog.isShowing()){
            checkPasswordDialog.dismiss();
        }
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_modify_phone_number);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
