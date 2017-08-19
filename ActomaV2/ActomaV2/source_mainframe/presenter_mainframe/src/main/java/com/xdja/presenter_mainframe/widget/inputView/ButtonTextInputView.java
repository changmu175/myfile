package com.xdja.presenter_mainframe.widget.inputView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by ldy on 16/4/15.
 */
public class ButtonTextInputView extends TextInputView {
    public ButtonTextInputView(Context context) {
        this(context,null);
    }

    public ButtonTextInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        btnAssistView.setVisibility(VISIBLE);
        chkAssistView.setVisibility(GONE);
    }


    public void setButtonText(String text) {
         btnAssistView.setText(text);
    }

    public void setButtonOnClickListener(OnClickListener onClickListener) {
        btnAssistView.setOnClickListener(onClickListener);
    }

    public void setButtonEnabled(boolean isEnable){
        btnAssistView.setEnabled(isEnable);
    }
}
