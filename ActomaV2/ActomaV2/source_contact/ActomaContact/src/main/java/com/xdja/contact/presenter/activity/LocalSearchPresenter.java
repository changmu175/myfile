package com.xdja.contact.presenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.xdja.contact.bean.Friend;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.Member;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.executor.SearchAsyncExecutor;
import com.xdja.contact.presenter.adapter.LocalSearchAdapter2;
import com.xdja.contact.presenter.command.ILocalSearchCommand;
import com.xdja.contact.ui.def.ILocalSearchVu;
import com.xdja.contact.ui.view.LocalSearchVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.util.cache.CacheDataTask;
import com.xdja.contact.util.cache.CacheManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地搜索界面
 *
 * @author hkb
 * @since 2015年8月17日10:19:16
 */
public class LocalSearchPresenter extends ActivityPresenter<ILocalSearchCommand, ILocalSearchVu> implements ILocalSearchCommand,AdapterView.OnItemClickListener {

    private boolean isSearching = false;

    private ArrayList<Group> cacheGroupList = new ArrayList<>();

 	ArrayList<LocalCacheDto> friendMemberSource = new ArrayList<>();
    private LocalSearchTask searchTask;

    private LocalSearchAdapter2 adapter;

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        /**这里进入搜索界面需要删除之前的缓存,以防出现好友已经删除,但是还能搜到问题. by yangpeng **/
        CacheManager.getInstance().clearData();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegisterActionUtil.ACTION_FRIEND_HAS_DELETED);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        getVu().getListView().setOnItemClickListener(this);
        adapter = new LocalSearchAdapter2(this);
        getVu().setAdapter(adapter);
        new CacheDataTask().executeOnExecutor(SearchAsyncExecutor.SEARCH_TASK_POOL);
        getVu().showNonDataView(false);//add by lwl
    }

    @Override
    public void startSearch(String keyword) {
        //本地搜索好友
        if (searchTask != null && isSearching) {
            searchTask.cancel(true);
        }
        if (TextUtils.isEmpty(keyword)) {
            adapter.clear();
            getVu().showNonDataView(false);//add by lwl
            return;
        }
        searchTask = new LocalSearchTask(keyword);
        searchTask.executeOnExecutor(SearchAsyncExecutor.SEARCH_TASK_POOL);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(ObjectUtil.objectIsEmpty(adapter))return;
        List<LocalCacheDto> dataSource = adapter.getDataSource();
        if(ObjectUtil.collectionIsEmpty(dataSource))return;
        LocalCacheDto localSearchBean = dataSource.get(position);
        if(ObjectUtil.objectIsEmpty(localSearchBean))return;
        if(localSearchBean.getViewType() == LocalCacheDto.GROUP_ITEM){
            ContactUtils.startGroupTalk(LocalSearchPresenter.this, localSearchBean.getGroupId());
            finish();
        }else if(localSearchBean.getViewType() == LocalCacheDto.FRIEND_ITEM){
            Intent intent = new Intent(this, CommonDetailPresenter.class);
            if(!ObjectUtil.stringIsEmpty(localSearchBean.getAccount())){
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT);
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,localSearchBean.getAccount());
            }else{
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID);
                intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY,localSearchBean.getWorkdId());
            }
            startActivity(intent);
            finish();
        }else if(localSearchBean.getViewType() == LocalCacheDto.FRIEND_MORE){
            String keyWord = adapter.getKeyWord();
            Intent intent = new Intent(this, LocalSearchMorePresenter.class);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_SEARCH_TYPE, RegisterActionUtil.EXTRA_KEY_TYPE_FRIEND_OR_MEMBER);
            //[s]add by xienana for bug 5847 @20161115 review by tangsha
 			intent.putParcelableArrayListExtra(RegisterActionUtil.EXTRA_KEY_LOCAL_SEARCH_ADAPTER_DATASOURCE_DATA,friendMemberSource);
            //[s]add by xienana for bug 5847 @20161115 review by tangsha
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_TPYE_KEYWORD, keyWord);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(localSearchBean.getViewType() == LocalCacheDto.GROUP_MORE){
            String keyWord = adapter.getKeyWord();
            Intent intent = new Intent(this, LocalSearchMorePresenter.class);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_SEARCH_TYPE, RegisterActionUtil.EXTRA_KEY_TYPE_GROUP);
            intent.putParcelableArrayListExtra(RegisterActionUtil.EXTRA_KEY_LOCAL_SEARCH_ADAPTER_DATA, cacheGroupList);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_TPYE_KEYWORD, keyWord);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            //不做处理
        }
    }


    private class LocalSearchTask extends AsyncTask<String, Integer, Map<String, ArrayList>> {

        private String keyword;

        private LocalSearchTask(String keyWord) {
            this.keyword = keyWord;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSearching = true;
            friendMemberSource.clear();//modify by xienana for bug 5847 @20161115 review by tangsha
        }

        @Override
        protected void onPostExecute(Map<String, ArrayList> data) {
            super.onPostExecute(data);
            getVu().endSearch();
            isSearching = false;
            adapter.setKeyWord(keyword);
            List<LocalCacheDto> groupSource = new ArrayList<>();
            List<LocalCacheDto> contactList = data.get("localSearch");
            List<Group> groupList = data.get("groupData");
            if(!ObjectUtil.collectionIsEmpty(contactList)){
                LocalCacheDto friendAlpha = new LocalCacheDto();
                friendAlpha.setViewType(LocalCacheDto.FRIEND_ALPHA);
                LocalCacheDto friendMore = new LocalCacheDto();
                friendMore.setViewType(LocalCacheDto.FRIEND_MORE);
                //[s]add by xienana for bug 5847 @20161115 review by tangsha
                if (contactList.size() > 10) {
                    for (int i = 0; i < 10; i++) {
                        friendMemberSource.add(contactList.get(i));
                    }
                }//[e]add by xienana for bug 5847 @20161115 review by tangsha
                if(contactList.size()>3){
                    contactList = contactList.subList(0,3);
                    for(LocalCacheDto localCacheDto : contactList){
                        localCacheDto.setViewType(LocalCacheDto.FRIEND_ITEM);
                    }
                    contactList.add(0,friendAlpha);
                    contactList.add(contactList.size(), friendMore);
                }else{
                    for(LocalCacheDto localCacheDto : contactList){
                        localCacheDto.setViewType(LocalCacheDto.FRIEND_ITEM);
                    }
                    contactList.add(0,friendAlpha);
                }
            }
            if(!ObjectUtil.collectionIsEmpty(groupList)){
                LocalCacheDto groupAlpha = new LocalCacheDto();
                groupAlpha.setViewType(LocalCacheDto.GROUP_ALPHA);
                LocalCacheDto groupMore = new LocalCacheDto();
                groupMore.setViewType(LocalCacheDto.GROUP_MORE);
                if(groupList.size()>3){
                    groupList = groupList.subList(0,3);
                    for(Group group : groupList){
                        LocalCacheDto localCacheDto = new LocalCacheDto(group);
                        groupSource.add(localCacheDto);
                    }
                    groupSource.add(0,groupAlpha);
                    groupSource.add(groupSource.size(), groupMore);
                }else{
                    for(Group group : groupList){
                        LocalCacheDto localCacheDto = new LocalCacheDto(group);
                        groupSource.add(localCacheDto);
                    }
                    groupSource.add(0,groupAlpha);
                }
            }
            List<LocalCacheDto> dataSource = new ArrayList<>();
            if(!ObjectUtil.collectionIsEmpty(contactList)){
                dataSource.addAll(contactList);
            }
            if(!ObjectUtil.collectionIsEmpty(groupList)) {
                dataSource.addAll(groupSource);
            }
            adapter.setDataSource(dataSource);
        }

        @Override
        protected Map<String, ArrayList> doInBackground(String... strings) {
            //根据指定类型搜索对应数据
            ArrayList<Group> groups = CacheManager.getInstance().filterGroupsByKeyWord(keyword.trim().toLowerCase());
            ArrayList<Friend> friends = CacheManager.getInstance().filterFriendsByKeyWord(keyword.trim().toLowerCase());
            ArrayList<Member> members = CacheManager.getInstance().filterMembersByKeyWord(keyword.trim().toLowerCase());
            ArrayList<LocalCacheDto> dataSource = CacheManager.getInstance().getCacheBeans(friends, members);
            Map<String, ArrayList> data = new HashMap<>();
            if (!ObjectUtil.collectionIsEmpty(groups)) {
                cacheGroupList = groups;
                data.put("groupData", groups);
            }else{
                cacheGroupList.clear();
            }
            if (!ObjectUtil.collectionIsEmpty(dataSource)) {
                SearchAsyncExecutor.cacheSearchResult = dataSource;
                data.put("localSearch", dataSource);
            }else{
                SearchAsyncExecutor.cacheSearchResult.clear();
            }
            return data;
        }
    }

    @Override
    protected Class<? extends ILocalSearchVu> getVuClass() {
        return LocalSearchVu.class;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        /**这里进入搜索界面需要删除之前的缓存,以防出现好友已经删除,但是还能搜到问题. by yangpeng **/
        CacheManager.getInstance().clearData();
        SearchAsyncExecutor.cacheSearchResult.clear();
    }

    @Override
    protected ILocalSearchCommand getCommand() {
        return this;
    }


    /**
     * 安全通信开启  通知联系人刷新
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RegisterActionUtil.ACTION_FRIEND_HAS_DELETED)) {
                new CacheDataTask().executeOnExecutor(SearchAsyncExecutor.SEARCH_TASK_POOL);
                startSearch(getVu().key());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };
}
