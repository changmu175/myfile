package com.xdja.presenter_mainframe.ui;

import android.graphics.Bitmap;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.data_mainframe.util.Util;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.WriteRegistrationInfoCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuWriteRegistrationInfo;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.BaseViewBean;
import com.xdja.presenter_mainframe.widget.FillInMessage.entities.TextInputViewBean;
import com.xdja.presenter_mainframe.widget.PartClickTextView;
import com.xdja.presenter_mainframe.widget.inputView.CheckBoxTextInputView;
import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/4/15.
 */
@ContentView(R.layout.activity_write_registration_info)
public class WriteRegistrationInfoView extends ActivityView<WriteRegistrationInfoCommand> implements VuWriteRegistrationInfo {


    @Bind(R.id.fmv_write_registration_info)
    FillInMessageView fmvWriteRegistrationInfo;
    @Bind(R.id.pctv_write_registration_info_terms)
    PartClickTextView pctvWriteRegistrationInfoTerms;
    @Bind(R.id.circleImg_write_registration_info)
    CircleImageView circleImgWriteRegistrationInfo;
    @Bind(R.id.txt_lable_setphoto)
    TextView txtLableSetphoto;

    private TextInputView nickname_text;
    private TextInputView password_text;
    private TextInputView password_again_text;

    private String passwordDigits = "";

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

    /**
     * 密码框是否已输入
     */
    protected boolean isInputPassword = false;
    /**
     * 重复密码框是否已输入
     */
    protected boolean isInputPasswordAgain = false;

    private final int ET_MAX_LENGTH16 = 16;
    private final int ET_MAX_LENGTH20 = 20;

    @Override
    public void onCreated() {
        super.onCreated();

        passwordDigits = getStringRes(R.string.password_digits);
        initViews();
    }

    private void initViews() {
        setView2FillInMessageView();
        //modify by alh@xdja.com to fix bug: 9248 2017-03-02 start (rummager : wangchao1)
        txtLableSetphoto.setText(null);
        if (CommonUtils.isZH(getActivity())){
            txtLableSetphoto.setText(R.string.setHeaderAvater);
        }
        //modify by alh@xdja.com to fix bug: 9248 2017-03-02 End (rummager : wangchao1)

        //modify by alh@xdja.com to fix bug: 571 2016-06-28 start (rummager : wangchao1)
        nickname_text = (TextInputView)fmvWriteRegistrationInfo.findViewById(R.id.nickname_input);
        //modify by alh@xdja.com to fix bug: 1131 2016-07-05 start (rummager : self)
        nickname_text.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_LENGTH16)});
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

        password_text = (TextInputView)fmvWriteRegistrationInfo.findViewById(R.id.password_text);
        password_text.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_LENGTH20)});
        password_again_text = ((TextInputView) fmvWriteRegistrationInfo.findViewById(R.id.password_again_text));
        password_again_text.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(ET_MAX_LENGTH20)});
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
        password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputPassword = password_text.getInputText() != null && !password_text.getInputText().isEmpty();
                setButtonEnabled();
            }
        });
        password_again_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputPasswordAgain = password_again_text.getInputText() != null && !password_again_text.getInputText().isEmpty();
                setButtonEnabled();
            }
        });

        fmvWriteRegistrationInfo.setCompleteButtonText(getStringRes(R.string.next));
        fmvWriteRegistrationInfo.setCompleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = fmvWriteRegistrationInfo.getInputTextList();
                getCommand().next(list.get(0).trim(), list.get(1), list.get(2));
            }
        });
    }

    private void setButtonEnabled(){
        if (isInputPassword && isInputPasswordAgain) {
            fmvWriteRegistrationInfo.setCompleteButtonEnable(true);
        } else {
            fmvWriteRegistrationInfo.setCompleteButtonEnable(false);
        }
    }

    /**
     * 向{@link FillInMessageView}填充view
     */
    private void setView2FillInMessageView() {
        List<BaseViewBean> viewBeanList = new ArrayList<>();

        TextInputViewBean name = new TextInputViewBean(getStringRes(R.string.your_nick_name), getStringRes(R.string.such_as_xiaoming));
        name.setId(R.id.nickname_input);
        viewBeanList.add(name);

        TextInputViewBean<CheckBoxTextInputView> password = new TextInputViewBean(getStringRes(R.string.pwd), getStringRes(R.string.pwd_format));
        password.setId(R.id.password_text);
        password.setShowAssistView(true);
        password.setInputViewType(CheckBoxTextInputView.class);
        viewBeanList.add(password);



        TextInputViewBean<CheckBoxTextInputView> passwordAgain = new TextInputViewBean(getStringRes(R.string.certain_password), getStringRes(R.string.pwd_format));
        passwordAgain.setShowAssistView(true);
        passwordAgain.setInputViewType(CheckBoxTextInputView.class);
        passwordAgain.setId(R.id.password_again_text);
        viewBeanList.add(passwordAgain);
        fmvWriteRegistrationInfo.setViewList(viewBeanList);
        setButtonEnabled();
    }

    @OnClick(R.id.pctv_write_registration_info_terms)
    void atTerms() {
        getCommand().atTerms();
    }

    @OnClick(R.id.circleImg_write_registration_info)
    void avatarClicked() {
        getCommand().avatarClicked();
    }

    @Override
    public void showUserImage(Bitmap bitmap) {
        if (bitmap != null) {
            circleImgWriteRegistrationInfo.setImageBitmap(bitmap);
            txtLableSetphoto.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadUserImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            circleImgWriteRegistrationInfo.loadImage(url, true);
            txtLableSetphoto.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_write_registration_info);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
