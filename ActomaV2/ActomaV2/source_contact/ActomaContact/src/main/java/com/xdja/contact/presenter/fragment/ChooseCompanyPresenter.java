package com.xdja.contact.presenter.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Adapter;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.Member;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.executor.SearchAsyncExecutor;
import com.xdja.contact.presenter.adapter.TreeListViewAdapter;
import com.xdja.contact.presenter.command.IChooseCompanyCommand;
import com.xdja.contact.service.DepartService;
import com.xdja.contact.service.MemberService;
import com.xdja.contact.ui.view.ChooseCompanyVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.view.TreeView.adpater.SearchViewAdapter;
import com.xdja.contact.view.TreeView.bean.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ChooseCompanyPresenter extends FragmentPresenter<ChooseCompanyPresenter, ChooseCompanyVu> implements IChooseCompanyCommand {

    public static final String EXIST_MEMBER_KEY = "existedMembers";

    private List<TreeNode> nodes = new ArrayList<>();

    private TreeListViewAdapter treeAdapter;

    private SearchViewAdapter searchViewAdapter;

    private boolean isSearch;

    private ISelectCallBack callBack;

    private AsyncTask<String, Integer, List<Member>> searchTask;

    private Context context;

    private String shareMark;// Task 2632

    //已选择的账号，不含默认选中的成员
    private List<String> existedMemberAccounts;
    private String messageType = null;// modified by ycm 2016/12/22:[文件转发或分享]
    @Override
    protected Class<? extends ChooseCompanyVu> getVuClass() {
        return ChooseCompanyVu.class;
    }

    @Override
    protected ChooseCompanyPresenter getCommand() {
        return this;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        if (activity instanceof ISelectCallBack) {
            callBack = (ISelectCallBack) activity;
        }
    }


    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.existedMemberAccounts = getArguments().getStringArrayList(EXIST_MEMBER_KEY);
        this.shareMark = getArguments().getString(RegisterActionUtil.SHARE);// Task 2632
        this.messageType = getArguments().getString(RegisterActionUtil.SHARE_MESSAGE_TYPE);// modified by ycm 2016/12/22:[文件转发或分享]获取是否是文件分享或转发
        treeAdapter = new TreeListViewAdapter(getActivity(), callBack,existedMemberAccounts);
        treeAdapter.setCheckBoxStatus(RegisterActionUtil.SHARE_FILE.equals(messageType));// modified by ycm 2016/12/22:[文件转发或分享]
        treeAdapter.setShareMark(shareMark);// Task 2632
        getVu().setTreeViewAdapter(treeAdapter);
        new GetDepartmentData(-1).execute();
    }

    /**
     * 查询部门信息
     */
    private class GetDepartmentData extends AsyncTask<TreeNode, Integer, List<TreeNode>> {

        private int position;

        private TreeNode rootNode;

        private GetDepartmentData(int position) {
            this.position = position;
        }

        @Override
        protected List<TreeNode> doInBackground(TreeNode... params) {
            DepartService departService = new DepartService(ActomaController.getApp());
            String departId = null;
            if (params == null || params.length == 0) {
            } else {
                rootNode = params[0];
                departId = rootNode.getId();
            }
            List<Department> departments = null;
            if (!ObjectUtil.stringIsEmpty(departId)) {
                departments = departService.getChildDepartment(departId);
            } else {
                departments = departService.getRootDepartment();
            }
            //查询部门下成员
            return convertDepart2Node(departments, queryMembersInDepartment(rootNode, departId));
        }

        @Override
        protected void onPostExecute(List<TreeNode> treeNodes) {
            super.onPostExecute(treeNodes);
            //如果根目录为空则证明当前就是根目录,否则为子Node设置父Node
            if (!ObjectUtil.collectionIsEmpty(treeNodes)) {
                if (rootNode != null) {
                    rootNode.addChild(treeNodes);
                }
                for (TreeNode node : treeNodes) {
                    if (node.isLeaf()) {
                        Member member = (Member) node.getSource();
                        if (!ObjectUtil.collectionIsEmpty(existedMemberAccounts) && existedMemberAccounts.contains(member.getAccount())) {
                            node.setIsChecked(true);
                        }
                    }
                }
                /**进入选择成员界面,有时会出现没有数据现象,来回切换会出现奔溃.顾在此加上判断,解决这一问题.add by yangpeng */
                //[s]modify by xienana for bug 8716 @20170216
                if (position == -1 && nodes != null) {
                        nodes.clear();
                        nodes.addAll(position + 1, treeNodes);
                }else if(position>-1 && nodes != null){
                        nodes.addAll(position + 1, treeNodes);
                }
                //[e]modify by xienana for bug 8716 @20170216
                if(!ObjectUtil.objectIsEmpty(treeAdapter)) {
                    treeAdapter.setDataSource(nodes);
                }
            }
        }
    }





    @Override
    public void startSearch(String keyWord) {
        if (isSearch || searchTask != null) {
            searchTask.cancel(true);
        }
        searchViewAdapter = new SearchViewAdapter(getActivity(), callBack,existedMemberAccounts);
        searchViewAdapter.setCheckBoxStatus(RegisterActionUtil.SHARE_FILE.equals(this.messageType));// modified by ycm 2016/12/22:[文件转发或分享]
        getVu().setSearchResultAdapter(searchViewAdapter);
        searchTask = new SearchTask().executeOnExecutor(SearchAsyncExecutor.SEARCH_TASK_POOL, keyWord);
    }

    @Override
    public void endSearch() {
        getVu().setTreeViewAdapter(treeAdapter);
        isSearch = false;
    }

    //Note 这里优化 选择联系人这里和搜索出结果
    @Override
    public void onNodeClick(int position) {
        Adapter listAdapter = getVu().getListAdapter();
        if(ObjectUtil.objectIsEmpty(listAdapter))return;
        if(listAdapter instanceof SearchViewAdapter){
            // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，区别点击事件[start]
            Member node = ((SearchViewAdapter) listAdapter).getDataSource().get(position);
            if (RegisterActionUtil.SHARE_FILE.equals(messageType)) { //没有checkbox时的点击事件，文件支持群发时删除此处
                clickItem(node);
            } else {//有checkbox时的点击事件
                return;
            }
            // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，区别点击事件[end]
//            return ;
            /*searchViewAdapter = (SearchViewAdapter)listAdapter;
            List<Member> dataSource = searchViewAdapter.getDataSource();*/
        }else {
            TreeNode node = nodes.get(position);
            //判断是否是人员
            if (node.isLeaf()) {
                // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，区别点击事件[start]
                if (RegisterActionUtil.SHARE_FILE.equals(messageType)) { //没有checkbox时的点击事件，文件支持群发时删除此处
                    Member member = (Member) node.getSource();
                    clickItem(member);
                } else {//有checkbox时的点击事件
                    return;
                }
                // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，区别点击事件[end]
//                return;
            }
            //是部门
            if (node.isExpand()) {//点之前是展开状态
                closeExpandAndChild(node);
                List<TreeNode> temp = new ArrayList<TreeNode>();
                for (int i = position + 1; i < nodes.size(); i++) {
                    if (node.getLevel() >= nodes.get(i).getLevel()) {
                        break;
                    }
                    temp.add(nodes.get(i));
                }
                nodes.removeAll(temp);
                treeAdapter.notifyDataSetChanged();
            } else {//点之前是关闭状态
                node.setExpand(true);
                //查询部门下的人员和子部门
                if (node.getChildren().size() <= 0) {
                    new GetDepartmentData(position).execute(node);
                    treeAdapter.notifyDataSetChanged();//add by xienana for bug 6343 @20161128 review by self
                } else {
                    nodes.addAll(position + 1, node.getChildren());
                    treeAdapter.notifyDataSetChanged();
                }
            }
        }
    }
	// modified by ycm 2016/12/22:[文件转发或分享][start]
    private void clickItem(Member member) {
        String selectedAccount = member.getAccount();
        if (selectedAccount != null) {
            ContactUtils.startFriendTalkForShare(getActivity(), selectedAccount);
            getActivity().finish();
        }
    }
	// modified by ycm 2016/12/22:[文件转发或分享][start]

    /**
     * 关闭部门和其子部门
     *
     * @param rootNode
     */
    private void closeExpandAndChild(TreeNode rootNode) {
        if (rootNode == null || rootNode.isLeaf()) {
            return;
        }
        if (rootNode.isExpand()) {
            rootNode.setExpand(false);
        }
        if (rootNode.hasChildren()) {
            closeExpandAndChildren(rootNode.getChildren());
        }
    }

    /**
     * 关闭部门下成员
     *
     * @param rootNode
     */
    private void closeExpandAndChildren(List<TreeNode> rootNode) {
        for (TreeNode treeNode : rootNode) {
            closeExpandAndChild(treeNode);
        }
    }


    /**
     * 将部门和部门下成员转换为Node
     *
     * @param departments
     * @param members
     * @return
     */
    private List<TreeNode> convertDepart2Node(List<Department> departments, List<Member> members) {
        List<TreeNode> nodes = new ArrayList<>();
        List<TreeNode> children = convertMember2Node(members);
        if (children != null) {
            nodes.addAll(children);
        }
        for (Department department : departments) {
            TreeNode<Department> treeNode = new TreeNode<>();
            treeNode.setName(department.getDepartmentName());
            treeNode.setLeaf(false);
            treeNode.setSource(department);
            treeNode.setId(department.getDepartmentId());

            nodes.add(treeNode);
        }

        return nodes;
    }

    /**
     * 将Member转换为Node
     *
     * @param members
     * @return
     */
    private List<TreeNode> convertMember2Node(List<Member> members) {
        List<TreeNode> nodes = new ArrayList<>();
        if (ObjectUtil.collectionIsEmpty(members)) return nodes;
        for (Member member : members) {
            //Start:add by wal@xdja.com
            if (ObjectUtil.objectIsEmpty(member)||(!ObjectUtil.objectIsEmpty(member) && ObjectUtil.stringIsEmpty(member.getAccount())))
                continue;
            //End:add by wal@xdja.com
            TreeNode<Member> treeNode = new TreeNode<>();
            treeNode.setName(member.getName());
            treeNode.setLeaf(true);
            Avatar avatarInfo = member.getAvatarInfo();
            treeNode.setIconUrl(avatarInfo.getThumbnail());
            treeNode.setSource(member);
            nodes.add(treeNode);
        }
        return nodes;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        nodes = null;
        searchViewAdapter = null;
        treeAdapter = null;
    }
    /**
     * 查询部门下成员
     */
    private List<Member> queryMembersInDepartment(TreeNode rootNode, String departid) {
        String departmentName = null;
        if (rootNode != null && rootNode.getSource() instanceof Department) {
            departmentName = ((Department) rootNode.getSource()).getDepartmentName();
        }
        MemberService service = new MemberService();
        List<Member> membersInDepart = service.getMembersInDepart(departid, true);
        if (!ListUtils.isEmpty(membersInDepart)) {
            String currentAccount = ContactUtils.getCurrentAccount();
            for (int i = membersInDepart.size() - 1; i >= 0; i--) {
                if (ObjectUtil.stringIsEmpty(currentAccount)) {
                    break;
                }
                Member member = membersInDepart.get(i);

                //如果取不到部门名称则到数据库内查询
                if (TextUtils.isEmpty(departmentName)) {
                    DepartService departService = new DepartService(ActomaController.getApp());
                    Department department = departService.getDepartmentById(member.getDepartId(), false);
                    if (department != null) {
                        departmentName = department.getDepartmentName();
                    }
                }
                member.setDepartmentName(departmentName);

                if (currentAccount.equals(member.getAccount())) {
                    membersInDepart.remove(i);
                }
            }
        }
        return membersInDepart;
    }


    /**
     * 搜索任务
     */
    private class SearchTask extends AsyncTask<String, Integer, List<Member>> {

        private String keyword;

        @Override
        protected List<Member> doInBackground(String... params) {
            isSearch = true;
            MemberService service = new MemberService();
            keyword = params[0].trim();
            return service.searchMember(keyword);
        }

        @Override
        protected void onPostExecute(List<Member> members) {
            super.onPostExecute(members);
            if (ObjectUtil.collectionIsEmpty(members) || ObjectUtil.stringIsEmpty(keyword)) {
                return;
            }
            List<Member> searchCache = new ArrayList<>();
            String currentAccount = ContactUtils.getCurrentAccount();
            for(Member member : members){
                if(ObjectUtil.stringIsEmpty(member.getAccount())){
                    continue;
                }
                if (currentAccount.equals(member.getAccount())) {
                    continue;
                }
                searchCache.add(member);
            }
            searchViewAdapter.setDataSource(searchCache, keyword);
        }
    }
    //start:fix 1543 by wal@xdja.com
    public void updateCompanyPresenterView(){
        if(!ObjectUtil.objectIsEmpty(treeAdapter)){
            treeAdapter.notifyDataSetChanged();
        }
		//[s]modify by xienana for bug 5261 @20161025[review by wangalei]
        if(!ObjectUtil.objectIsEmpty(searchViewAdapter)){
            searchViewAdapter.notifyDataSetChanged();
        }	
		//[e]modify by xienana for bug 5261 @20161025[review by wangalei]
    }
    //end:fix 1543 by wal@xdja.com
}
