package com.xdja.contact.presenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.bean.Group;
import com.xdja.contact.callback.group.IContactEvent;
import com.xdja.contact.presenter.adapter.GroupListAdapter;
import com.xdja.contact.presenter.command.IGroupListCommand;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.ui.def.IGroupListVu;
import com.xdja.contact.ui.view.GroupListView;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.List;

/**
 * Created by XDJA_XA on 2015/7/17.
 * 群组列表页面
 */
public class GroupListPresenter extends FragmentPresenter<IGroupListCommand, IGroupListVu>
        implements IGroupListCommand, IContactEvent {

    private GroupListAdapter mAdapter;

    private List<Group> dataLocalGroups;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        //注册群组事件监听器
        FireEventUtils.addGroupListener(this);
        //初始化Adapter
        this.mAdapter = new GroupListAdapter(getActivity());
        getVu().setAdapter(mAdapter);
        registerReceiver();
        notifyListView();
    }

    private void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        //filter.addAction(RegisterActionUtil.ACTION_ALARM_NOTIFY);
        filter.addAction(RegisterActionUtil.ACTION_CHANGE_GROUP_NAME);
        filter.addAction(RegisterActionUtil.ACTION_GROUP_DOWNLOAD_SUCCESS);
        filter.addAction(RegisterActionUtil.ACTION_TASK_ALL_REMOVE);
        getActivity().registerReceiver(groupReceiver, filter);
    }


    @Override
    public void onEvent(int event, Object param1, Object param2, Object param3) {
        switch(event){
		   //[S]modify by tangsha@20161101 for 5748
            case EVENT_GROUP_CHANGED:
                //Note: 目前无法区分名称变更还是头像变更 暂且刷新群列表
                /*String groupId = (String)param1;
                String groupName = (String)param2;
                String thumbnail = (String)param3;
                for(Group group : dataLocalGroups){
                    if(group.getGroupId().equals(groupId)){
                        group.setGroupName(groupName);
                        group.setThumbnailId(thumbnail);
                    }
                }
                mAdapter.setDataSource(dataLocalGroups);*/
                notifyListViewOnUi();
                break;
            case EVENT_GROUP_ADDED:
                notifyListViewOnUi();
                break;
            case EVENT_GROUP_INFO_GET:
                notifyListViewOnUi();
                break;
            case DISMISS: //当前用户解散群
            case QUIT: //当前用户退出群
            case EVENT_GROUP_QUIT:
                notifyListViewOnUi();
                break;
            case EVENT_GROUP_LIST_REFRESH:
                notifyListViewOnUi();
                break;
		 //[E]modify by tangsha@20161101 for 5748
        }
    }

    private void notifyListViewOnUi(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyListView();
            }
        });
    }

    //异步处理
    public void notifyListView(){
        dataLocalGroups = GroupInternalService.getInstance().getValidGroups();
        //如果群组列表为空，显示暂无群组
        if(ObjectUtil.collectionIsEmpty(dataLocalGroups)){
            getVu().setListEmpty();
        }
        sortGroupByGroupName();
    }


    private void sortGroupByGroupName(){
        //start:add by wal@xdja.com for 1737
//        if(!ObjectUtil.collectionIsEmpty(dataLocalGroups)){
//            List<Group> emptyNameList = new ArrayList<>();
//            List<Group> specfiNumric = new ArrayList<>();
//            List<Group> normalArray = new ArrayList<>();
//            for(Group localGroup : dataLocalGroups){
//                String key = localGroup.getNamePY();
//                if(!ObjectUtil.stringIsEmpty(key)) {
//                    char c = key.trim().substring(0, 1).charAt(0);
//                    Pattern pattern = Pattern.compile("^[A-Za-z]+$");
//                    if (!pattern.matcher(c + "").matches()) {
//                        specfiNumric.add(localGroup);
//                    } else {
//                        normalArray.add(localGroup);
//                    }
//                }else{
//                    emptyNameList.add(localGroup);
//                }
//            }
//            Collections.sort(specfiNumric, new GroupAscComparator());
//            Collections.sort(normalArray, new GroupAscComparator());
//            dataLocalGroups.clear();
//            dataLocalGroups.addAll(normalArray);
//            dataLocalGroups.addAll(specfiNumric);
//            dataLocalGroups.addAll(emptyNameList);
//        }
        //end:add by wal@xdja.com for 1737
        mAdapter.setDataSource(dataLocalGroups);
        getVu().setGroupCount(dataLocalGroups.size());
    }

    BroadcastReceiver groupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           /* if(RegisterActionUtil.ACTION_ALARM_NOTIFY.equals(intent.getAction())){
                notifyListView();
            }*/
            if(RegisterActionUtil.ACTION_CHANGE_GROUP_NAME.equals(intent.getAction())){
                notifyListView();
            } else if(RegisterActionUtil.ACTION_GROUP_DOWNLOAD_SUCCESS.equals(intent.getAction())){
                notifyListView();
            } else if(RegisterActionUtil.ACTION_TASK_ALL_REMOVE.equals(intent.getAction())){
                LogUtil.getUtils().e("Actoma contact GroupListP----所有线程执行完成通知群组刷新---------");
                notifyListView();
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(groupReceiver);
        FireEventUtils.removeGroupListener(this);
    }


    @Override
    public void startChatActivity(int position) {
        ContactUtils.startGroupTalk(getActivity(), dataLocalGroups.get(position).getGroupId());
    }

    @Override
    protected Class<? extends IGroupListVu> getVuClass() {
        return GroupListView.class;
    }


    @Override
    protected IGroupListCommand getCommand() {
        return this;
    }

}
