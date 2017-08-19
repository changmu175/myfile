package com.xdja.contact.ui.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.xdja.contact.R;
import com.xdja.contact.presenter.adapter.TreeListViewAdapter;
import com.xdja.contact.presenter.fragment.ChooseCompanyPresenter;
import com.xdja.contact.ui.def.IChooseCompanyVu;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.view.TreeView.adpater.SearchViewAdapter;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;

import butterknife.ButterKnife;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public class ChooseCompanyVu extends FragmentSuperView<ChooseCompanyPresenter> implements IChooseCompanyVu {


    private ListView listView ;
    private EditText searchEd ;

    @Override
    protected int getLayoutRes() {
        return R.layout.choose_contact_fragment;
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        RelativeLayout searchLayout = ButterKnife.findById(getView(), R.id.search_layout);
        searchEd = ButterKnife.findById(searchLayout, R.id.search_ed);
        searchEd.addTextChangedListener(new OnSearchEditListener());

        listView = ButterKnife.findById(getView(), R.id.contact_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getCommand().onNodeClick(position);
            }
        });

    }

    @Override
    public void setTreeViewAdapter(TreeListViewAdapter treeViewAdapter) {
        listView.setAdapter(treeViewAdapter);
    }

    @Override
    public void setSearchResultAdapter(SearchViewAdapter searchResultAdapter) {
        listView.setAdapter(searchResultAdapter);
    }

    @Override
    public Adapter getListAdapter() {
        return listView.getAdapter();
    }

    private class OnSearchEditListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String keyWord = s.toString();

            if(ObjectUtil.stringIsEmpty(keyWord)){
                getCommand().endSearch();
            }else {
                getCommand().startSearch(keyWord);
            }
        }
    }
}
