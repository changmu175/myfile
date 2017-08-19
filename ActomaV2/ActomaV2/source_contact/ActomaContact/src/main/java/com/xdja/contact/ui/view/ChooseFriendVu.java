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
import android.widget.TextView;

import com.xdja.contact.R;
import com.xdja.contact.callback.OnTouchingLetterChangedListener;
import com.xdja.contact.presenter.adapter.ChooseFriendAdapter;
import com.xdja.contact.presenter.adapter.SearchFriendsAdapter;
import com.xdja.contact.presenter.fragment.ChooseFriendsPresenter;
import com.xdja.contact.ui.def.IChooseFriendsVu;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.view.SlidarView;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;

import butterknife.ButterKnife;

/**
 * @author hkb.
 * @since 2015/7/23/0023.
 */
public class ChooseFriendVu extends FragmentSuperView<ChooseFriendsPresenter> implements IChooseFriendsVu, OnTouchingLetterChangedListener, AdapterView.OnItemClickListener {
    private EditText editText;
    private ListView listView;
    private SlidarView slidarView;

    @Override
    protected int getLayoutRes() {
        return R.layout.choose_friends_fragment;
    }
    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater,container);
        RelativeLayout searchLayout = ButterKnife.findById(getView(), R.id.search_layout);
        editText = ButterKnife.findById(searchLayout, R.id.search_ed);
        editText.addTextChangedListener(new OnSearchEditListener());
        listView = ButterKnife.findById(getView(), R.id.friends_contact_list);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                getCommand().onClickItem(position);
//            }
//        });
        listView.setOnItemClickListener(this);
        TextView sliderTv = ButterKnife.findById(getView(), R.id.sliderTv);
        slidarView = (SlidarView)getView().findViewById(R.id.slidarview);
        slidarView.setOnTouchingLetterChangedListener(this);
        slidarView.setTextView(sliderTv);

    }

    @Override
    public void setChooseFriendsAdapter(ChooseFriendAdapter chooseFriendsAdapter) {
        listView.setAdapter(chooseFriendsAdapter);

    }

    @Override
    public void setSearchResultAdapter(SearchFriendsAdapter searchFriendsAdapter) {
        listView.setAdapter(searchFriendsAdapter);

    }

    // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，区别不同适配器的点击事件[start]
    @Override
    public Adapter getListAdapter() {
        return listView.getAdapter();
    }
    // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，区别不同适配器的点击事件[end]

    @Override
    public String getSearchText() {
        return editText.getText().toString();
    }

    @Override
    public void setListSelection(int position) {
        listView.setSelection(position);
    }

    @Override
    public void setSelectedNum(int num) {
        if(num>0){
            String title = getStringRes(R.string.select_member_title);
            getActivity().setTitle(String.format(title, num));
        }else {
            getActivity().setTitle(R.string.default_select_member_title);
        }

    }

    @Override
    public void onTouchingLetterChanged(String key) {
        getCommand().onLetterChanged(key);
    }
	// modified by ycm 2016/12/22:[文件转发或分享][start]
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (getCommand().getCheckBox()) {
                getCommand().clickItem(position);

            }
    }
	// modified by ycm 2016/12/22:[文件转发或分享][end]
    private class OnSearchEditListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable key) {
            String keyWord = key.toString();
            if(ObjectUtil.stringIsEmpty(keyWord)){
                getCommand().endSearch();
            }else{
                getCommand().startSearch(keyWord);
            }

        }

    }
}
