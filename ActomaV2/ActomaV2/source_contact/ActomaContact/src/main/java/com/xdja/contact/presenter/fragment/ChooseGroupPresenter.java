package com.xdja.contact.presenter.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.xdja.contact.bean.Group;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.presenter.adapter.ChooseGroupAdapter;
import com.xdja.contact.presenter.adapter.SearchGroupAdapter;
import com.xdja.contact.presenter.command.IChooseGroupCommand;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.service.GroupService;
import com.xdja.contact.ui.def.IChooseGroupVu;
import com.xdja.contact.ui.view.ChooseGroupVu;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.List;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，选择更多联系人，选择群的界面
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for searching group when choose group for share and forward function by ycm at 20161222.
 */
public class ChooseGroupPresenter  extends FragmentPresenter<IChooseGroupCommand, IChooseGroupVu>
        implements IChooseGroupCommand {
    public static final String EXIST_MEMBER_KEY = "existedMembers";
    private ChooseGroupAdapter chooseGroupAdapter;
    private SearchGroupAdapter searchGroupAdapter;
    private List<Group> dataLocalGroups;
    private String searchKey;
    //已选择的账号，不含默认选中的成员
    private List<String> existedMemberAccounts;
    //回调选中人员
    private ISelectCallBack selectCallBack;

    private boolean isSearching;
    private String shareMark;
    private AsyncTask<String, Integer, List<Group>> task;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISelectCallBack) {
            selectCallBack = (ISelectCallBack) activity;
        }
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.existedMemberAccounts = getArguments().getStringArrayList(EXIST_MEMBER_KEY);
        this.shareMark = getArguments().getString(RegisterActionUtil.SHARE);
        this.chooseGroupAdapter = new ChooseGroupAdapter(getActivity(),selectCallBack,existedMemberAccounts);

        chooseGroupAdapter.setShareMark(shareMark);
        getVu().setAdapter(chooseGroupAdapter);
        new AsyncLoadGroupsTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void startChatActivity(int position) {
        ContactUtils.startGroupTalkForShare(getActivity(), dataLocalGroups.get(position).getGroupId());
        getActivity().finish();
    }

    @Override
    public void startSearch(String keyWord) {
        searchGroupAdapter = new SearchGroupAdapter(getActivity(),selectCallBack,existedMemberAccounts);
        getVu().setSearchResultAdapter(searchGroupAdapter);
        List<Group> result =GroupInternalService.getInstance().queryGroupByKey(keyWord);
        searchGroupAdapter.setDataSource(result, keyWord);
    }

     @Override
     public void endSearch() {
         getVu().setChooseGroupsAdapter(chooseGroupAdapter);
     }
     

    @Override
    protected Class<? extends IChooseGroupVu> getVuClass() {
        return ChooseGroupVu.class;
    }


    @Override
    protected IChooseGroupCommand getCommand() {
        return this;
    }

    private class AsyncLoadGroupsTask extends AsyncTask<String, Integer, List<Group>> {

        @Override
        protected List<Group> doInBackground(String... params) {
            GroupService service = new GroupService();
            List<Group> groups = service.queryGroups();
            if(ObjectUtil.collectionIsEmpty(groups))return
                    null;
            return ContactShowUtil.GroupComparatorDataSource(ContactShowUtil.groupDataSeparate(groups));
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            super.onPostExecute(groups);
            if(ObjectUtil.collectionIsEmpty(groups))
                return;
            dataLocalGroups = groups;
            chooseGroupAdapter.setDataSource(groups);
        }
    }

    public void updateGroupsPresenterView(){
        if(!ObjectUtil.objectIsEmpty(chooseGroupAdapter)){
            chooseGroupAdapter.notifyDataSetChanged();
        }
    }
}
