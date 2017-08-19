package com.xdja.presenter_mainframe.widget.inputView;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;

import com.xdja.presenter_mainframe.R;

import butterknife.OnCheckedChanged;

/**
 * Created by ldy on 16/4/15.
 */
public class CheckBoxTextInputView extends TextInputView {
    public CheckBoxTextInputView(Context context) {
        this(context, null);
    }

    public CheckBoxTextInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        btnAssistView.setVisibility(GONE);
        chkAssistView.setVisibility(VISIBLE);
        edtTxtInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @OnCheckedChanged(R.id.chk_assist_view)
    void chkChanged(boolean checked) {
        if (checked) {
            edtTxtInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            edtTxtInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        // 2016/06/22/李晓龙【BUG#531 - 设置光标在最后】
        if (edtTxtInput.getText() != null) {
            edtTxtInput.setSelection(edtTxtInput.getText().length());
        }
    }

}
