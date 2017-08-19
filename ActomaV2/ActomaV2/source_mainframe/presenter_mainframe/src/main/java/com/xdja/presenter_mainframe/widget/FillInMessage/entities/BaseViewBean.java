package com.xdja.presenter_mainframe.widget.FillInMessage.entities;

import android.support.annotation.IdRes;

import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;

/**
 * Created by ldy on 16/4/12.<p/>
 *
 * 供{@link FillInMessageView}使用的基础bean对象,不支持外部拓展<p/>
 * V: view的类型
 */
public abstract class BaseViewBean {
    /**
     * view的id
     */
    @IdRes
    protected int id = -1;

    @IdRes
    public int getId() {
        return id;
    }

    public void setId(@IdRes int id) {
        this.id = id;
    }
}
