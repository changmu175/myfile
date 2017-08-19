package com.xdja.imp.ui.vu;

import com.xdja.comm.widget.LazyLoadExpandableListView;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.imp.presenter.adapter.FileListAdapter;

/**
 * <p>Author: xdjaxa         </br>
 * <p>Date: 2016/12/2 16:55   </br>
 * <p>Package: com.xdja.imp.ui.vu</br>
 * <p>Description:            </br>
 */
public interface ILastFileListVu<P extends Command> extends FragmentVu<P> {

    /**
     * 设置为空显示
     */
    void setEmptyView();

    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    void setListAdapter(FileListAdapter adapter);

    /**
     * LazyLoadExpandableListView
     *
     * @return LazyLoadExpandableListView 对象
     */
    LazyLoadExpandableListView getListView();
}
