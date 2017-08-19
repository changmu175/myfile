package com.xdja.presenter_mainframe.ui;

import android.text.InputFilter;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FillMessageCommand;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPwdInputNewPasswordPresenter;
import com.xdja.presenter_mainframe.ui.uiInterface.VuInputNewPassword;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;
import com.xdja.presenter_mainframe.widget.inputView.CheckBoxTextInputView;
import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

import java.util.List;

/**
 * Created by ldy on 16/4/15.
 */
public class InputNewPasswordView extends AbstractFillMessageView<FillMessageCommand> implements VuInputNewPassword {


    private String passwordDigits = "";

    private TextInputView password_text;
    private TextInputView password_again_text;

    @Override
    public void onCreated() {
        super.onCreated();

        passwordDigits = getStringRes(R.string.password_digits);

        initView();

        setNewPasswordDigits();
    }

    /**
     * 设置密码输入框的输入值类型
     */
    public void setNewPasswordDigits(){
        password_text = (TextInputView)fmvFillMessage.findViewById(R.id.password_text);
        password_text.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        password_text.getEditText().setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars = passwordDigits.toCharArray();
                return numberChars;
            }
        });
        //modify by alh@xdja.com to fix bug: 574 2016-06-28 start (rummager : wangchao1)
        password_again_text = (TextInputView) fmvFillMessage.findViewById(R.id.verify_phone_number_verify_code);
        if (password_again_text != null) {
            password_again_text.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            password_again_text.getEditText().setKeyListener(new NumberKeyListener() {
                @Override
                public int getInputType() {
                    return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
                }

                @Override
                protected char[] getAcceptedChars() {
                    char[] numberChars = passwordDigits.toCharArray();
                    return numberChars;
                }
            });
        }
        //modify by alh@xdja.com to fix bug: 574 2016-06-28 end (rummager : wangchao1)
    }

    /**
     * 向{@link FillInMessageView}填充view
     *
     * @param viewBeanList 空的list
     * @return 需要使用的list
     */
    @Override
    protected List<BaseViewBean> setView2FillInMessageView(List<BaseViewBean> viewBeanList) {
        TextViewBean textViewBean = new TextViewBean(getStringRes(R.string.password_format));
        viewBeanList.add(textViewBean);

        TextInputViewBean<CheckBoxTextInputView> phone =
                //新密码在xml文件中无法正确显示空格,所以直接放到代码里
                new TextInputViewBean(getStringRes(R.string.new_pwd), getStringRes(R.string.password_hint));
        phone.setId(R.id.password_text);
        phone.setInputViewType(CheckBoxTextInputView.class);
        phone.setShowAssistView(true);
        viewBeanList.add(phone);
        if (getCommand() instanceof ResetPwdInputNewPasswordPresenter && ((ResetPwdInputNewPasswordPresenter)getCommand()).isHasMobile()){
            return viewBeanList;
        }
        TextInputViewBean<CheckBoxTextInputView> verifyCode =
                new TextInputViewBean(getStringRes(R.string.certain_password), getStringRes(R.string.certain_password_hint));
        verifyCode.setShowAssistView(true);
        verifyCode.setId(R.id.verify_phone_number_verify_code);
        verifyCode.setInputViewType(CheckBoxTextInputView.class);
        viewBeanList.add(verifyCode);
        return viewBeanList;
    }

    /**
     * 获取完成操作按钮的文字
     *
     * @return
     */
    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.certain);
    }

    private void initView() {
        fmvFillMessage.setCompleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> stringList = fmvFillMessage.getInputTextList();
                getCommand().complete(stringList);
            }
        });
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_input_new_password);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
