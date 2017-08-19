package com.xdja.presenter_mainframe.widget.inputView;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xdja.presenter_mainframe.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ldy on 16/4/12.
 * 实现了{@link InputView},firstView是一个文本(textview)
 */
public class TextInputView extends AbstractInputView {
    @Bind(R.id.btn_assist_view)
    Button btnAssistView;
    @Bind(R.id.chk_assist_view)
    CheckBox chkAssistView;

    public TextInputView(Context context) {
        this(context,null);
    }

    public TextInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this);
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.InputView);
        if (typedArray.hasValue(R.styleable.InputView_firstText)) {
            ((TextView) firstView).setText(typedArray.getString(R.styleable.InputView_firstText));
        }
        typedArray.recycle();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.view_text_input_view;
    }



    public void setFirstViewText(String text) {
        ((TextView) firstView).setText(text);
    }

    public void setFirstViewText(Spanned spanned) {
        ((TextView) firstView).setText(spanned);
    }
}
