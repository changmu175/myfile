package com.xdja.presenter_mainframe.widget.inputView;

import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by ldy on 16/4/13.
 * 一个输入组件的接口,左侧为该输入栏需要输入的信息(firstView,一般用文字或图片表达,不可编辑,点击),紧挨着的右侧是一个可输入的EditTextView(edtTxtInput),
 * 输入框右侧覆盖了一个辅助view(assistView),一般用于辅助功能,可以控制显示或隐藏.
 */
public interface InputView {
    /**
     * 设置输入框的hint
     */
    void setInputHint(String text);

    void setInputHint(Spanned spanned);

    /**
     * 设置输入框文本
     */
    void setInputText(String text);

    /**
     * 获取输入框文本
     */
    String getInputText();

    /**
     * 是否显示辅助view
     */
    void setIsShowAssistView(boolean isShow);

    /**
     * 获取editText
     */
    EditText getEditText();

    /**
     * 同{@link EditText#setInputType(int)}
     */
    void setEditInputType(int type);

    /**
     * 是否显示清除按钮，注意：文本变化后设置失效
     * @param isShowClear   是否显示清除按钮
     */
    void isShowClear(boolean isShowClear);
}
