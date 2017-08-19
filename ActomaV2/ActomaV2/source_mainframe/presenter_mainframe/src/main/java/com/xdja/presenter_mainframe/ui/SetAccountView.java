package com.xdja.presenter_mainframe.ui;

import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SetAccountCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetAccount;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;
import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

import java.util.List;

/**
 * Created by ldy on 16/4/29.
 */
public class SetAccountView extends AbstractFillMessageView<SetAccountCommand> implements VuSetAccount {

    private final int TEXTSIZE = 12;

    @Override
    public void onCreated() {
        super.onCreated();
        ((TextView)fmvFillMessage.findViewById(R.id.tv_set_account_bottom_text))
                .setTextSize(TEXTSIZE);
        //modify by alh@xdja.com to fix bug: 473 2016-06-23 start (rummager : liuwangle)
        EditText editText = ((TextInputView) fmvFillMessage.findViewById(R.id.inputView_account))
                .getEditText();
        final String accountDigits = getStringRes(R.string.account_digits);
        //modify by alh@xdja.com to fix bug: 473 2016-06-23 end (rummager : liuwangle)
        editText.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars = accountDigits.toCharArray();
                return numberChars;
            }
        });

    }

    @Override
    protected List<BaseViewBean> setView2FillInMessageView(List<BaseViewBean> viewBeanList) {
        viewBeanList.add(new TextViewBean(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG, 0, 0,
                0, getStringRes(R.string.set_account_top_text))));
        TextInputViewBean inputViewBean = new TextInputViewBean(TextUtil.getActomaText(getContext(), TextUtil
                .ActomaImage.IMAGE_VERSION_BIG, 0, 0, 0, getStringRes(R.string.at_account)), TextUtil.getActomaText
                (getContext(), TextUtil.ActomaImage.IMAGE_INPUT_HINT, 0, 0, 0, getStringRes(R.string
                        .please_write_at_account)));
        inputViewBean.setId(R.id.inputView_account);
        viewBeanList.add(inputViewBean);
        viewBeanList.add(new TextViewBean(R.id.tv_set_account_bottom_text, TextUtil.getActomaText(getContext(),
                TextUtil.ActomaImage.IMAGE_VERSION_BIG, 0, 0, 0, getStringRes(R.string.set_account_bottom_text))));
        return viewBeanList;
    }

    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.complete);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_set_account);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
