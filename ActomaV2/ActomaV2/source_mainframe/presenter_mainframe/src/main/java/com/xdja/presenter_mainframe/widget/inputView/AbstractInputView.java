package com.xdja.presenter_mainframe.widget.inputView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xdja.presenter_mainframe.R;

/**
 * Created by ldy on 16/4/13.
 * 实现了{@link InputView},完成了inputView的基础功能
 */
public abstract class AbstractInputView extends LinearLayout implements InputView {
    protected Context mContext;
    protected View firstView;
    protected EditText edtTxtInput;
    protected View assistView;
    private Button btnInputClear;

    public AbstractInputView(Context context) {
        this(context, null);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public AbstractInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.InputView);
        if (typedArray.hasValue(R.styleable.InputView_inputHint)) {
            edtTxtInput.setHint(typedArray.getString(R.styleable.InputView_inputHint));
        }
        if (typedArray.hasValue(R.styleable.InputView_isShowAssistView)) {
            boolean isShow = typedArray.getBoolean(R.styleable.InputView_isShowAssistView, false);
            if (isShow) {
                assistView.setVisibility(VISIBLE);
            } else {
                assistView.setVisibility(GONE);
            }
        }
        if (typedArray.hasValue(R.styleable.InputView_inputPaddingRight)){
            int dimension = (int) typedArray.getDimension(R.styleable.InputView_inputPaddingRight, 0);
            edtTxtInput.setPadding(edtTxtInput.getPaddingLeft(), edtTxtInput.getPaddingTop(), dimension, edtTxtInput
                    .getPaddingBottom());
        }
        typedArray.recycle();
    }


    private void initView() {
        LayoutInflater.from(mContext).inflate(getLayoutRes(), this);
        firstView = findViewById(R.id.first_view);
        edtTxtInput = (EditText) findViewById(R.id.edtTxt_input);
        assistView = findViewById(R.id.assist_view);
        btnInputClear = (Button) findViewById(R.id.btn_input_clear);
        btnInputClear.setVisibility(GONE);
        edtTxtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setClearState(edtTxtInput);
            }
        });
        edtTxtInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setClearState(edtTxtInput);
            }
        });
        btnInputClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTxtInput.setText("");
            }
        });
    }

    private void setClearState(EditText edtTxtInput) {
        if (!edtTxtInput.getText().toString().equals("") && edtTxtInput.isFocused()) {
            btnInputClear.setVisibility(VISIBLE);
        } else {
            btnInputClear.setVisibility(GONE);
        }
    }

    protected abstract
    @LayoutRes
    int getLayoutRes();

    /**
     * 设置输入框的hint
     *
     * @param text
     */
    @Override
    public void setInputHint(String text) {
        edtTxtInput.setHint(text);
    }

    @Override
    public void setInputHint(Spanned spanned) {
        edtTxtInput.setHint(spanned);
    }

    /**
     * 设置输入框文本
     *
     * @param text
     */
    @Override
    public void setInputText(String text) {
        edtTxtInput.setText(text);
    }

    /**
     * 获取输入框文本
     */
    @Override
    public String getInputText() {
        return edtTxtInput.getText().toString();
    }

    @Override
    public EditText getEditText() {
        return edtTxtInput;
    }


    /**
     * 是否显示辅助view
     *
     * @param isShow
     */
    @Override
    public void setIsShowAssistView(boolean isShow) {
        if (isShow) {
            assistView.setVisibility(VISIBLE);
        } else {
            assistView.setVisibility(GONE);
        }
    }

    @Override
    public void isShowClear(boolean isShowClear) {
        if (isShowClear) {
            btnInputClear.setVisibility(VISIBLE);
        } else {
            btnInputClear.setVisibility(GONE);
        }
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if (edtTxtInput != null)
            edtTxtInput.addTextChangedListener(textWatcher);
    }

    /**
     * 同{@link EditText#setInputType(int)}
     */
    public void setEditInputType(int type) {
        edtTxtInput.setInputType(type);
    }

}
