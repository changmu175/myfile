package com.xdja.presenter_mainframe.widget.FillInMessage.entities;

import android.text.Spanned;

import com.xdja.presenter_mainframe.widget.inputView.TextInputView;

/**
 * Created by ldy on 16/4/12.
 */
public class TextInputViewBean<V extends TextInputView> extends BaseViewBean {
    private String firstText = "";
    private String inputText = "";
    private String inputHint = "";

    private Spanned firstSpanned = null;
    private Spanned hintSpanned = null;

    private boolean isShowAssistView = false;

    private Class<V> inputViewType;

    public TextInputViewBean() {
    }

    public TextInputViewBean(String firstText, String inputHint) {
        this.firstText = firstText;
        this.inputHint = inputHint;
    }

    public TextInputViewBean(Spanned firstSpanned, Spanned hintSpanned) {
        this.firstSpanned = firstSpanned;
        this.hintSpanned = hintSpanned;
    }

    public String getFirstText() {
        return firstText;
    }

    public Spanned getFirstSpanned() {
        return firstSpanned;
    }

    public void setFirstSpanned(Spanned firstSpanned) {
        this.firstSpanned = firstSpanned;
    }

    public Spanned getHintSpanned() {
        return hintSpanned;
    }

    public void setHintSpanned(Spanned hintSpanned) {
        this.hintSpanned = hintSpanned;
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getInputHint() {
        return inputHint;
    }

    public void setInputHint(String inputHint) {
        this.inputHint = inputHint;
    }

    public boolean isShowAssistView() {
        return isShowAssistView;
    }

    public void setShowAssistView(boolean showAssistView) {
        isShowAssistView = showAssistView;
    }

    public Class<V> getInputViewType() {
        return inputViewType;
    }

    public void setInputViewType(Class<V> inputViewType) {
        this.inputViewType = inputViewType;
    }
}
