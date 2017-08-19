package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.frame.mvp.view.AdapterSuperView;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.IHistoryFileListAdapterCommand;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/14 21:53
 * 修改人：xdjaxa
 * 修改时间：2016/12/14 21:53
 * 修改备注：
 */
public class ViewHistoryFileTitle extends AdapterSuperView<IHistoryFileListAdapterCommand,String>
        implements AdapterVu<IHistoryFileListAdapterCommand, String> {

    private TextView mGroupTv;

    public ViewHistoryFileTitle() {
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.filelist_item_group;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if(view != null) {
            mGroupTv = (TextView) view.findViewById(R.id.tv_group_title);
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull String dataSource) {
        super.bindDataSource(position, dataSource);
        initView();
    }

    private void initView() {
        mGroupTv.setText(dataSource);
    }

}
