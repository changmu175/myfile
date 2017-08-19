package com.xdja.imp.presenter.holder;

import android.view.View;
import android.widget.TextView;

import com.xdja.imp.R;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/6 11:37   </br>
 * <p>Package: com.appcom.androidview.ui.holder</br>
 * <p>Description:            </br>
 */
public class FileGroupViewHolder extends ViewHolder<String> {

    /**
     * Group标题
     */
    private TextView mGroupTitleTv;

    public FileGroupViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bindViews(View itemView) {
        mGroupTitleTv = (TextView) itemView.findViewById(R.id.tv_group_title);
    }

    @Override
    public void bindData(String dataSource) {
        mGroupTitleTv.setText(dataSource);
    }
}
