package com.xdja.imp.presenter.holder;

import android.view.View;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/6 10:37   </br>
 * <p>Package: com.appcom.androidview.ui.holder</br>
 * <p>Description:            </br>
 */
public abstract class ViewHolder<D> {

    private final View itemView;

    ViewHolder(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("itemView may not be null");
        }
        this.itemView = itemView;
        bindViews(itemView);
    }

    public View getConvertView() {
        return itemView;
    }

    protected abstract void bindViews(View itemView);

    protected abstract void bindData(D dataSource);
}
