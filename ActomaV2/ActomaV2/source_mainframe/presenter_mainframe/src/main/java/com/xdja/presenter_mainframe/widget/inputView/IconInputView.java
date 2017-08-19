package com.xdja.presenter_mainframe.widget.inputView;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.presenter_mainframe.R;

/**
 * Created by ldy on 16/4/12.
 * 左侧是一个图标,右侧的辅助view是一个checkBox,有一个下划线的输入框
 */
public class IconInputView extends AbstractInputView {

    public IconInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.InputView);
        if (typedArray.hasValue(R.styleable.InputView_firstIcon)) {
            firstView.setBackgroundResource(typedArray.getResourceId(R.styleable.InputView_firstIcon, -1));
        }
        ((CheckBox) assistView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtTxtInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    edtTxtInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                // 2016/06/22/李晓龙【BUG#549 - 设置光标在最后】
                if (edtTxtInput.getText() != null) {
                    edtTxtInput.setSelection(edtTxtInput.getText().length());
                }
            }
        });
        if (assistView.getVisibility() == VISIBLE) {
            edtTxtInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        if (!ObjectUtil.objectIsEmpty(typedArray)) {
            typedArray.recycle();
        }
     }

    @Override
    protected int getLayoutRes() {
        return R.layout.view_icon_input_view;
    }

}
