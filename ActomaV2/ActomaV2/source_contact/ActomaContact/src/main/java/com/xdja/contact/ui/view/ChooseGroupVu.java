package com.xdja.contact.ui.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.callback.IPullCallback;
import com.xdja.contact.presenter.adapter.ChooseGroupAdapter;
import com.xdja.contact.presenter.adapter.SearchGroupAdapter;
import com.xdja.contact.presenter.command.IChooseGroupCommand;
import com.xdja.contact.ui.def.IChooseGroupVu;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;
/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，选择更多联系人，选择群的界面实现
 * 创建人：ycm
 * 创建时间：2016/11/1 19:30
 * 修改人：ycm
 * 修改时间：2016/11/1 19:30
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ChooseGroupVu extends FragmentSuperView<IChooseGroupCommand> implements
        IChooseGroupVu,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        IPullCallback {
    private final String TAG = GroupListView.class.getSimpleName();
    private ListView mListView;
    private View mEmptyView;
    private EditText editText;
    private String keyword;
    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        mListView = (ListView) getView().findViewById(R.id.group_contact_list);
        mListView.setOnItemClickListener(this);
        View search_view = getView().findViewById(R.id.search_layout);
        editText = (EditText) search_view.findViewById(R.id.search_ed);
        editText.requestFocus();
        editText.setFocusable(true);
        editText.addTextChangedListener(new TextChangedListener());
        mEmptyView = getView().findViewById(R.id.empty_list_view);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.choose_group_fragment;
    }

    @Override
    public void setAdapter(ChooseGroupAdapter adapter) {
        if (mListView != null) {
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "mListView must not be null when setAdapter");
        }
    }

    @Override
    public void setChooseGroupsAdapter(ChooseGroupAdapter chooseGroupAdapter) {
        mListView.setAdapter(chooseGroupAdapter);
    }

    @Override
    public void setSearchResultAdapter(SearchGroupAdapter searchGroupAdapter) {
        mListView.setAdapter(searchGroupAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getCommand().startChatActivity(position);
    }

    @Override
    public void onRefresh() {
        if (!ContactModuleService.checkNetWork()) {
            return;
        }
    }

    @Override
    public boolean isSupportLoading() {
        return true;
    }

    @Override
    public void stopRefreshLoading() {
    }

    @Override
    public void onShowErrorToast(HttpErrorBean httpErrorBean) {
        if (!ObjectUtil.objectIsEmpty(httpErrorBean)) {
            XToast.show(getActivity(), httpErrorBean.getMessage());
        } else {
            XToast.show(getActivity(), R.string.contact_net_error);
        }
    }


    class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            keyword = s.toString().trim();
            if (ObjectUtil.stringIsEmpty(keyword)) {
                getCommand().endSearch();
            } else {
                getCommand().startSearch(keyword);
            }
        }
    }
}
