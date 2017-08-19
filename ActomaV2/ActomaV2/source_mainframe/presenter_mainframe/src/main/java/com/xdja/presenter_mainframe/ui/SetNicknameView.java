package com.xdja.presenter_mainframe.ui;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import com.xdja.data_mainframe.util.Util;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SetNicknameCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetNickname;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextViewBean;
import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

import java.util.List;

/**
 * Created by ldy on 16/5/3.
 */
public class SetNicknameView extends AbstractFillMessageView<SetNicknameCommand> implements VuSetNickname {
    private TextInputView nickname_text;
    /**
     * 输入表情前的最后一个位置
     */
    private int mSelectionEnd;

    /**
     * 是否重置了Text
     */
    private boolean mResetText;

    /**
     *输入表情前的字符串
     */
    private String mInputAfterText;

    private final int ET_MAX_SIZE = 16;

    @Override
    public void onCreated() {
        super.onCreated();
        //modify by alh@xdja.com to fix bug: 571 2016-06-28 start (rummager : wangchao1)
        nickname_text = (TextInputView) getActivity().findViewById(R.id.nickname_input);
        //modify by alh@xdja.com to fix bug: 1131 2016-07-05 start (rummager : self)
        nickname_text.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_SIZE)});
        //modify by alh@xdja.com to fix bug: 1131 2016-07-05 end (rummager : self)
        //按照要求,昵称不能输入表情符号
        nickname_text.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mResetText) {
                    mSelectionEnd = nickname_text.getEditText().getSelectionEnd();
                    mInputAfterText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mResetText) {
                    if (count >= 2) {// 表情符号的字符长度最小为2
                        int length = mSelectionEnd + count < s.length() ? mSelectionEnd + count : s.length();
                        CharSequence input = s.subSequence(mSelectionEnd < s.length() ? mSelectionEnd : s.length(), length);
                        if (Util.containsEmoji(input.toString())) {
                            mResetText = true;
                            Toast.makeText(getContext(), R.string.nickname_format, Toast.LENGTH_SHORT).show();
                            nickname_text.getEditText().setText(mInputAfterText);
                            CharSequence text = nickname_text.getEditText().getText();
                            if (text instanceof Spannable) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, text.length());
                            }
                        }
                    }
                } else {
                    mResetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //modify by alh@xdja.com to fix bug: 571 2016-06-28 end (rummager : wangchao1)
    }

    @Override
    protected List<BaseViewBean> setView2FillInMessageView(List<BaseViewBean> viewBeanList) {
        viewBeanList.add(new TextViewBean(getStringRes(R.string.nickname_top_text)));

        TextInputViewBean<TextInputView> inputViewBean = new TextInputViewBean(getStringRes(R.string.nickName), getStringRes(R.string.nickname_rule));
        inputViewBean.setId(R.id.nickname_input);
        viewBeanList.add(inputViewBean);

        return viewBeanList;
    }

    @Override
    protected String getCompleteButtonText() {
        return getStringRes(R.string.complete);
    }


    @Override
    public void setNickName(String nickName) {
        if (nickName == null || TextUtils.isEmpty(nickName)) return;
        TextInputView nickInput = (TextInputView) getActivity().findViewById(R.id.nickname_input);
        nickInput.setInputText(nickName);
        //alh@xdja.com<mailto://alh@xdja.com> 2017-01-03 add. fix bug 7760 . review by wangchao1. Start
        if (nickName.length() <= nickInput.getEditText().length()) {
            nickInput.getEditText().setSelection(nickName.length());
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2017-01-03 add. fix bug 7760 . review by wangchao1. End
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_set_nickname);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
