package com.xdja.presenter_mainframe.ui;

import android.text.InputFilter;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.FillMessageCommand;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.ui.uiInterface.VuDataMigrationFixPwd;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;
import com.xdja.presenter_mainframe.widget.inputView.CheckBoxTextInputView;
import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

import java.util.List;

/**
 * Created by ALH on 2016/8/15.
 */
public class DataMigrationFixPwdView extends AbstractFillMessageView<FillMessageCommand> implements
        VuDataMigrationFixPwd {
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

    private void initView() {
        fmvFillMessage.setCompleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> stringList = fmvFillMessage.getInputTextList();
                getCommand().complete(stringList);
            }
        });
    }

    /**
     * 设置密码输入框的输入值类型
     */
    public void setNewPasswordDigits() {
        password_text = (TextInputView) fmvFillMessage.findViewById(R.id.password_text);
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
        password_again_text = (TextInputView) fmvFillMessage.findViewById(R.id.password_again_text);
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

    @Override
    protected List<BaseViewBean> setView2FillInMessageView(List<BaseViewBean> viewBeanList) {
        TextViewBean textViewBean = new TextViewBean(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage
                .IMAGE_VERSION_BIG, 0, 0, 0, getStringRes(R.string.old_account_fix_pwd_prompt)));
        viewBeanList.add(textViewBean);
        TextInputViewBean<CheckBoxTextInputView> phone = new TextInputViewBean(getStringRes(R.string.login_pwd),
                getStringRes(R.string.password_hint));
        phone.setId(R.id.password_text);
        phone.setInputViewType(CheckBoxTextInputView.class);
        phone.setShowAssistView(true);
        viewBeanList.add(phone);

        TextInputViewBean<CheckBoxTextInputView> verifyCode = new TextInputViewBean(getStringRes(R.string
                .certain_password), getStringRes(R.string.certain_password_hint));
        verifyCode.setShowAssistView(true);
        verifyCode.setId(R.id.password_again_text);
        verifyCode.setInputViewType(CheckBoxTextInputView.class);
        viewBeanList.add(verifyCode);
        return viewBeanList;
    }

    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.next);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_DEFAULT;
    }

    @Override
    public void maxLoginCount(int maxLoginCount) {
        LoginHelper.maxLoginDialog(getActivity(), maxLoginCount);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_set_login_pwd);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
