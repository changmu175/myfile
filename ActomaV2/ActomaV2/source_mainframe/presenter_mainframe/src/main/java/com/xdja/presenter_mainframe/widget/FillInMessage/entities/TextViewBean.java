package com.xdja.presenter_mainframe.widget.FillInMessage.entities;

import android.support.annotation.IdRes;
import android.text.Spanned;

/**
 * Created by ldy on 16/4/12.
 */
public class TextViewBean extends BaseViewBean {
    private String text = "";
    private Spanned spanned;
    public TextViewBean(){}
    public TextViewBean(String text) {
        this.text = text;
    }
    public TextViewBean(@IdRes int id, String text) {
        this.id = id;
        this.text = text;
    }

    public TextViewBean(@IdRes int id, Spanned spanned) {
        this.id = id;
        this.spanned = spanned;
    }

    public TextViewBean(Spanned spanned) {
        this.spanned = spanned;
    }

    public Spanned getSpanned() {
        return spanned;
    }

    public void setSpanned(Spanned spanned) {
        this.spanned = spanned;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
