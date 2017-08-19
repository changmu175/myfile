package com.xdja.contact.ui.def;

import android.widget.Adapter;

import com.xdja.contact.presenter.adapter.TreeListViewAdapter;
import com.xdja.contact.presenter.fragment.ChooseCompanyPresenter;
import com.xdja.contact.view.TreeView.adpater.SearchViewAdapter;
import com.xdja.frame.presenter.mvp.view.FragmentVu;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public interface IChooseCompanyVu extends FragmentVu<ChooseCompanyPresenter> {

    void setTreeViewAdapter(TreeListViewAdapter treeViewAdapter);

    void setSearchResultAdapter(SearchViewAdapter searchResultAdapter);

    Adapter getListAdapter();

}
