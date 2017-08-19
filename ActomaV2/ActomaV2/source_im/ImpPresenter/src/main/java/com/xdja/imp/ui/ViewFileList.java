package com.xdja.imp.ui;

import android.view.View;
import android.widget.LinearLayout;

import com.xdja.comm.widget.LazyLoadExpandableListView;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;
import com.xdja.imp.R;
import com.xdja.imp.presenter.adapter.BaseFileListAdapter;
import com.xdja.imp.presenter.command.IFileListCommand;
import com.xdja.imp.ui.vu.IFileListVu;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/6 20:19   </br>
 * <p>Package: com.xdja.imp.ui</br>
 * <p>Description:            </br>
 */
public class ViewFileList extends FragmentSuperView<IFileListCommand>
        implements IFileListVu<IFileListCommand> {

    private LazyLoadExpandableListView mFileList;

    private LinearLayout mEmptyLayout;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_file_list;
    }

    @Override
    protected void injectView() {
        super.injectView();
        if (getView() != null) {
            mFileList = (LazyLoadExpandableListView) getView().findViewById(R.id.lv_file_list);
            mFileList.setGroupIndicator(getDrawableRes(R.drawable.item_group_indeicator_selector));
            mEmptyLayout = (LinearLayout) getView().findViewById(R.id.layout_empty);
        }
    }

    @Override
    public void setEmptyView() {
        mEmptyLayout.setVisibility(View.VISIBLE);
        mFileList.setEmptyView(mEmptyLayout);
    }

    @Override
    public void setListAdapter(BaseFileListAdapter adapter) {
        mFileList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public LazyLoadExpandableListView getListView() {
        return mFileList;
    }
}
