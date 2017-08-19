package com.xdja.imp.ui;

import android.view.View;
import android.widget.LinearLayout;

import com.xdja.comm.widget.LazyLoadExpandableListView;
import com.xdja.imp.R;
import com.xdja.imp.frame.mvp.view.FragmentSuperView;
import com.xdja.imp.presenter.adapter.FileListAdapter;
import com.xdja.imp.presenter.command.ILastFileListCommand;
import com.xdja.imp.ui.vu.ILastFileListVu;

/**
 * <p>Author: leiliangliang         </br>
 * <p>Date: 2016/12/2 16:57   </br>
 * <p>Package: com.xdja.imp.ui</br>
 * <p>Description:聊天文件列表fragment </br>
 */
public class ViewLastFileList extends FragmentSuperView<ILastFileListCommand>
        implements ILastFileListVu<ILastFileListCommand> {

    private LazyLoadExpandableListView mFileList;

    private LinearLayout mEmptyLayout;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_last_file;
    }

    @Override
    protected void injectView() {
        super.injectView();

        if (getView() != null) {
            mFileList = (LazyLoadExpandableListView) getView().findViewById(R.id.lv_chat_files);
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
    public void setListAdapter(FileListAdapter adapter) {
        mFileList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public LazyLoadExpandableListView getListView() {
        return mFileList;
    }

}
