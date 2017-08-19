package com.xdja.imp.ui.vu;

import com.xdja.comm.widget.LazyLoadExpandableListView;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.FragmentVu;
import com.xdja.imp.presenter.adapter.BaseFileListAdapter;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/6 20:18   </br>
 * <p>Package: com.xdja.imp.ui.vu</br>
 * <p>Description:            </br>
 */
public interface IFileListVu<P extends Command> extends FragmentVu<P> {

    /**
     * 设置为空显示
     */
    void setEmptyView();

    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    void setListAdapter(BaseFileListAdapter adapter);

    /**
     * 获取列表显示控件
     *
     * @return
     */
    LazyLoadExpandableListView getListView();
}
