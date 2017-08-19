package com.xdja.contact.presenter.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Adapter;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.executor.SearchAsyncExecutor;
import com.xdja.contact.presenter.adapter.ChooseFriendAdapter;
import com.xdja.contact.presenter.adapter.SearchFriendsAdapter;
import com.xdja.contact.presenter.command.IChooseFriendsCommand;
import com.xdja.contact.service.FriendService;
import com.xdja.contact.ui.def.IChooseFriendsVu;
import com.xdja.contact.ui.view.ChooseFriendVu;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.presenter.BasePresenterFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangpeng on 2015/7/25.
 *
 * modify  wanghao 2016-02-26
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ChooseFriendsPresenter extends BasePresenterFragment<ChooseFriendsPresenter, IChooseFriendsVu> implements IChooseFriendsCommand {

    public static final String EXIST_MEMBER_KEY = "existedMembers";

    private static final int MSG_SEARCH_DELAY = 1000;

    private List<Friend> searchFriends;

    private ChooseFriendAdapter chooseFriendAdapter;

    private SearchFriendsAdapter searchFriendsAdapter;

    private AsyncTask<String, Integer, List<Friend>> task;

    private boolean isSearching;

    private String searchKey;
    //已选择的账号，不含默认选中的成员
    private List<String> existedMemberAccounts;
    //回调选中人员
    private ISelectCallBack selectCallBack;

    private String shareMark;// Task 2632
    private String messageType;// modified by ycm 2016/12/22:[文件转发或分享]
    @Override
    protected Class<? extends ChooseFriendVu> getVuClass() {
        return ChooseFriendVu.class;
    }

    @Override
    protected ChooseFriendsPresenter getCommand() {
        return this;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISelectCallBack) {
            selectCallBack = (ISelectCallBack) activity;
        }
    }


    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        this.existedMemberAccounts = getArguments().getStringArrayList(EXIST_MEMBER_KEY);
        this.shareMark = getArguments().getString(RegisterActionUtil.SHARE);// Task 2632
        this.chooseFriendAdapter = new ChooseFriendAdapter(getActivity(),selectCallBack,existedMemberAccounts);
        this.messageType = getArguments().getString(RegisterActionUtil.SHARE_MESSAGE_TYPE);// modified by ycm 2016/12/22:[文件转发或分享]获取是否是文件分享或转发
        chooseFriendAdapter.setCheckBoxStatus(RegisterActionUtil.SHARE_FILE.equals(this.messageType));// modified by ycm 2016/12/22:[文件转发或分享]
        chooseFriendAdapter.setShareMark(shareMark);// Task 2632
        getVu().setChooseFriendsAdapter(chooseFriendAdapter);
        new AsyncLoadFriendsTask().execute();
    }

    private class AsyncLoadFriendsTask extends AsyncTask<Friend, Integer, List<Friend>> {

        @Override
        protected List<Friend> doInBackground(Friend... params) {
            FriendService service = new FriendService();
            List<Friend> friends = service.queryFriends();
            if(ObjectUtil.collectionIsEmpty(friends))return null;
            return ContactShowUtil.comparatorDataSource(ContactShowUtil.dataSeparate(friends));
        }

        @Override
        protected void onPostExecute(List<Friend> friends) {
            super.onPostExecute(friends);
            if(ObjectUtil.collectionIsEmpty(friends))return;
            chooseFriendAdapter.setDataSource(friends);
        }
    }






    @Override
    public void onLetterChanged(String key) {
        int position = chooseFriendAdapter.getPositionForString(key);
        if (position > -1) {
            getVu().setListSelection(position);
        }
    }

    @Override
    public boolean getCheckBox() {
        return RegisterActionUtil.SHARE_FILE.equals(this.messageType);
    }

    // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，item点击事件[start]
    @Override
    public void clickItem(int position) {
        Friend selectedFriend = null;
        String selectedAccount = null;
        Adapter listAdapter = getVu().getListAdapter();
        if(ObjectUtil.objectIsEmpty(listAdapter)) {
            return;
        }
        if (listAdapter instanceof ChooseFriendAdapter ) {
            selectedFriend =chooseFriendAdapter.getDataSource().get(position);
            selectedAccount = selectedFriend.getAccount();
        } else if (listAdapter instanceof SearchFriendsAdapter) {
            selectedFriend =searchFriendsAdapter.getDataSource().get(position);
            selectedAccount = selectedFriend.getAccount();
        }
        if (selectedAccount != null) {
            ContactUtils.startFriendTalkForShare(getActivity(), selectedAccount);
            getActivity().finish();
        }
    }
    // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，item点击事件[end]


    /**
     * 开始搜索
     *
     * @param keyWord
     */
    @Override
    public void startSearch(String keyWord) {
        if (!TextUtils.equals(keyWord, searchKey)) {
            searchKey = keyWord;
        }
        stopSearch = false;
        searchFriendsAdapter = new SearchFriendsAdapter(getActivity(),selectCallBack,existedMemberAccounts);
        searchFriendsAdapter.setCheckBoxStatus(RegisterActionUtil.SHARE_FILE.equals(this.messageType));// modified by ycm 2016/12/22:[文件转发或分享]
        getVu().setSearchResultAdapter(searchFriendsAdapter);
        mDelayedSearchHandler.removeMessages(MSG_SEARCH_DELAY);
        mDelayedSearchHandler.sendEmptyMessageDelayed(MSG_SEARCH_DELAY, 100);
    }

    boolean stopSearch = false;
    @Override
    public void endSearch() {
        mDelayedSearchHandler.removeMessages(MSG_SEARCH_DELAY);
        getVu().setChooseFriendsAdapter(chooseFriendAdapter);
        stopSearch = true;
    }

    /**
     * 退出搜索
     */
    @Override
    public void exitSearch() {
        endSearch();
    }


    /**
     * 好友搜索异步
     */
    private class SearchFriendsTask extends AsyncTask<String, Integer, List<Friend>> {

        private String searchKey;

        @Override
        protected List<Friend> doInBackground(String... params) {
            if(stopSearch){
                LogUtil.getUtils().e("ChooseFriendPresenter SearchFriendsTask doInBackground  stopSearch!!!");
                return null;
            }
            isSearching = true;
            FriendService service = new FriendService();
            searchKey = params[0].trim();
            return service.searchFriends(searchKey);
        }

        @Override
        protected void onPostExecute(List<Friend> friends) {
            super.onPostExecute(friends);
            isSearching = false;
            searchFriends = new ArrayList<>();
            searchFriends = friends;
            if (ObjectUtil.collectionIsEmpty(searchFriends) || stopSearch) {
                LogUtil.getUtils().e("ChooseFriendPresenter SearchFriendsTask stopSearch "+stopSearch);
                return;
            }
            getVu().setSearchResultAdapter(searchFriendsAdapter);
            searchFriendsAdapter.setDataSource(searchFriends, searchKey);
        }
    }

    private static class ChooseFriendHandler extends Handler{
        private WeakReference<ChooseFriendsPresenter> mActivity;
        ChooseFriendHandler(ChooseFriendsPresenter activity){
            mActivity = new WeakReference<ChooseFriendsPresenter>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChooseFriendsPresenter activity = mActivity.get();
            if(activity != null){
                if ((!activity.isAdded()) || (activity.isRemoving())) {
                    return;
                }
                switch (msg.what) {
                    case MSG_SEARCH_DELAY:
                        activity.doSearch();
                        break;
                }

            }
        }
    }
    public void doSearch(){
        if (isSearching || task != null) {
            task.cancel(true);
        }
        task = new SearchFriendsTask().executeOnExecutor(SearchAsyncExecutor.SEARCH_TASK_POOL, searchKey);
    }

    private ChooseFriendHandler mDelayedSearchHandler = new ChooseFriendHandler(this);


    //start:fix 1543 by wal@xdja.com
    public void updateFriendsPresenterView(){
        if(!ObjectUtil.objectIsEmpty(chooseFriendAdapter)){
            chooseFriendAdapter.notifyDataSetChanged();
        }
		//[s]modify by xienana for bug 5261 @20161025[review by wangalei]
        if(!ObjectUtil.objectIsEmpty(searchFriendsAdapter)){
            searchFriendsAdapter.notifyDataSetChanged();
        }
	   //[e]modify by xienana for bug 5261 @20161025[review by wangalei]
    }
    //end:fix 1543 by wal@xdja.com
}
