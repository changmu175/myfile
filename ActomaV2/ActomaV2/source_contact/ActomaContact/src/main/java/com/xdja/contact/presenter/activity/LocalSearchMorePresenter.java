package com.xdja.contact.presenter.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.xdja.comm.uitl.ListUtils;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.executor.SearchAsyncExecutor;
import com.xdja.contact.presenter.adapter.LocalSearchMoreAdapter;
import com.xdja.contact.presenter.command.IFriendSearchMoreCommand;
import com.xdja.contact.ui.def.IFriendSearchMoreVu;
import com.xdja.contact.ui.view.FriendSearchMoreVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.util.cache.CacheManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghao on 2015/10/23.
 */
public class LocalSearchMorePresenter extends ActivityPresenter<IFriendSearchMoreCommand, IFriendSearchMoreVu> implements IFriendSearchMoreCommand,LocalSearchMoreAdapter.OnItemClickListener {

    private LocalSearchTask searchTask;

    private LocalSearchMoreAdapter searchAdapter;

    private ArrayList<Group> groupsResult;

    private ArrayList<LocalCacheDto> friendMemberResult;



    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if(!isGroupData()){
            searchAdapter = new LocalSearchMoreAdapter(LocalSearchMorePresenter.this,this, LocalSearchMoreAdapter.TYPE_MEMBER);
        }else{
            searchAdapter = new LocalSearchMoreAdapter(LocalSearchMorePresenter.this,this, LocalSearchMoreAdapter.TYPE_GROUP);
        }
        getVu().setAdapter(searchAdapter);
        initAllLocaleData();
        adapterSetKeyWord();
        getVu().setKeyWord(getKeyWord());
    }




    @Override
    public void startSearch(String keyword) {

        //本地搜索好友
        if (searchTask != null && isSearching) {
            searchTask.cancel(true);
        }
        if(ObjectUtil.stringIsEmpty(keyword)){
            searchAdapter.clear();
            return;
        }else{
            //在数据缓存中查找匹配
            searchTask = new LocalSearchTask(keyword);
            searchTask.executeOnExecutor(SearchAsyncExecutor.SEARCH_TASK_POOL);
        }
    }

    private boolean isSearching = false;
    private class LocalSearchTask extends AsyncTask<String, Integer, Map<String, ArrayList>> {

        private String keyword;

        private LocalSearchTask(String keyWord) {
            this.keyword = keyWord;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSearching = true;
        }

        @Override
        protected void onPostExecute(Map<String, ArrayList> data) {
            super.onPostExecute(data);
            isSearching = false;
            searchAdapter.setKeyWord(keyword);
            searchAdapter.setGroupDataSource(data.get("groupData"));
            searchAdapter.setMemberDataSource(data.get("localSearch"));
        }

        @Override
        protected Map<String, ArrayList> doInBackground(String... strings) {
            //根据指定类型搜索对应数据
            Map<String, ArrayList> data = new HashMap<>();
            if(isGroupData()){
                ArrayList<Group> groups = CacheManager.getInstance().filterGroupsByKeyWord(keyword.trim().toLowerCase());
                if (!ListUtils.isEmpty(groups)) {
                    data.put("groupData", groups);
                }
            }else {
                ArrayList<Friend> friends = CacheManager.getInstance().filterFriendsByKeyWord(keyword.trim().toLowerCase());
                ArrayList<Member> members = CacheManager.getInstance().filterMembersByKeyWord(keyword.trim().toLowerCase());
                ArrayList<LocalCacheDto> dataSource = CacheManager.getInstance().getCacheBeans(friends, members);
                if (!ObjectUtil.collectionIsEmpty(dataSource)) {
                    data.put("localSearch", dataSource);
                }
            }
            return data;
        }
    }

    /**
     * 是否是群组数据
     * @return
     */
    private boolean isGroupData(){
        Intent intent = getIntent();
        if(!ObjectUtil.objectIsEmpty(intent)){
//            String type = intent.getStringExtra(FLAG_DATA_SEARCH_TYPE);
            String type = intent.getStringExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_SEARCH_TYPE);
            if(RegisterActionUtil.EXTRA_KEY_TYPE_GROUP.equals(type)){
                return true;
            }
//            if(TYPE_FRIEND_OR_MEMBER.equals(type)){
            if(RegisterActionUtil.EXTRA_KEY_TYPE_FRIEND_OR_MEMBER.equals(type)){
                return false;
            }
        }
        return false;
    }

    private String getKeyWord(){
        Intent intent = getIntent();
        if(!ObjectUtil.objectIsEmpty(intent)){
//            return intent.getStringExtra(FLAG_DATA_TPYE_KEYWORD);
            return intent.getStringExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_TPYE_KEYWORD);
        }
        return "";
    }

    private void initAllLocaleData(){
        Intent intent = getIntent();
        if(isGroupData()) {
            groupsResult = intent.getParcelableArrayListExtra(RegisterActionUtil.EXTRA_KEY_LOCAL_SEARCH_ADAPTER_DATA);
            searchAdapter.setGroupDataSource(groupsResult);
        }else {
            //[s]modify by xienana for bug 5847 @20161115 review by tangsha
            friendMemberResult = intent.getParcelableArrayListExtra(RegisterActionUtil.EXTRA_KEY_LOCAL_SEARCH_ADAPTER_DATASOURCE_DATA);
            searchAdapter.setMemberDataSource(friendMemberResult);
//            searchAdapter.setMemberDataSource(SearchAsyncExecutor.cacheSearchResult);
            //[e]modify by xienana for bug 5847 @20161115 review by tangsha
        }
    }

    private void adapterSetKeyWord(){
        searchAdapter.setKeyWord(getKeyWord());
    }

    @Override
    protected Class<? extends IFriendSearchMoreVu> getVuClass() {
        return FriendSearchMoreVu.class;
    }

    @Override
    protected IFriendSearchMoreCommand getCommand() {
        return this;
    }

    @Override
    public void onGroupClick(Group group) {
        if (group == null) {
            return;
        }
        ContactUtils.startGroupTalk(this, group.getGroupId());
    }

    @Override
    public void onItemClick(LocalCacheDto localSearchBean) {
        Intent intent = new Intent(this, CommonDetailPresenter.class);
        if(!ObjectUtil.stringIsEmpty(localSearchBean.getAccount())){
//            intent.putExtra(CommonDetailPresenter.DATA_TYPE, CommonDetailPresenter.DATA_TYPE_ACCOUNT);
//            intent.putExtra(CommonDetailPresenter.DATA_KEY,localSearchBean.getAccount());
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,localSearchBean.getAccount());
        }
        //start:modify by wal@xdja.com for 4346
        else if (!ObjectUtil.stringIsEmpty(localSearchBean.getWorkdId())){
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,localSearchBean.getWorkdId());
        }else {
            return;
        }
        //end:modify by wal@xdja.com for 4346
        startActivity(intent);
        finish();
    }

    //[s]add by xienana for bug 5847 @20161115 review by tangsha
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (friendMemberResult != null) {
            friendMemberResult.clear();
        }
    }
    //[e]add by xienana for bug 5847 @20161115 review by tangsha
}
