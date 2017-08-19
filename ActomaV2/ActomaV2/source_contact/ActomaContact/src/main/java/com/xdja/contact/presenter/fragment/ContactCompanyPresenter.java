package com.xdja.contact.presenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.CloseMenuEvent;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.Department;
import com.xdja.contact.bean.Member;
import com.xdja.contact.callback.OnBatchTaskListener;
import com.xdja.contact.http.proxy.DepartMemberHttpTask;
import com.xdja.contact.http.proxy.DepartmentHttpTask;
import com.xdja.contact.presenter.activity.CommonDetailPresenter;
import com.xdja.contact.presenter.adapter.CompanyTreeAdapter;
import com.xdja.contact.presenter.command.IContactCompanyCommand;
import com.xdja.contact.service.DepartService;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.service.MemberService;
import com.xdja.contact.ui.view.ContactCompanyVu;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.view.TreeView.bean.TreeNode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 集团联系人主界面
 *
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public class ContactCompanyPresenter extends FragmentPresenter<ContactCompanyPresenter, ContactCompanyVu> implements IContactCompanyCommand {

    private static final int HANDLER_DEFAULT = 0;

    private static final int HANDLER_OBJECT = 1;

    private List<TreeNode> nodes = new ArrayList<>();

    private CompanyTreeAdapter treeAdapter;

    boolean fresh=false;

    private Map map;

    public static final String ERR_ACCOUNT_NOT_BELONG_EC = "account_not_belong_ec";

    @Override
    protected Class<? extends ContactCompanyVu> getVuClass() {
        return ContactCompanyVu.class;
    }

    @Override
    protected ContactCompanyPresenter getCommand() {
        return this;
    }

    private void skip2Detail(Member member) {
        if(!ObjectUtil.stringIsEmpty(member.getWorkId())){
            Intent intent = new Intent(getActivity(), CommonDetailPresenter.class);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE, RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID);
            intent.putExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY, member.getWorkId());
            startActivity(intent);
        }
    }

    @Override
    public void onNodeClick(int position) {
        Member member;
        TreeNode node = nodes.get(position);
        //判断是否是人员
        if (node.isLeaf()) {
            member = (Member) node.getSource();
            skip2Detail(member);
            return;
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
            //treeAdapter.setNodes(nodes);
            treeAdapter.notifyDataSetChanged();
        } else {//点之前是关闭状态
            node.setExpand(true);
            //查询部门下的人员和子部门
            if (node.getChildren().size() <= 0) {
                new GetDepartmentData(position).execute(node);
            } else {
                nodes.addAll(position + 1, node.getChildren());
                //treeAdapter.setNodes(nodes);
                treeAdapter.notifyDataSetChanged();
            }
        }
    }

    //[s]modify by xienana for count department member @20161124 review by tangsha
    @Override
    public int refreshCount() {
        MemberService memberService = new MemberService();
        int count = memberService.getMembersCount();
        return count-1;
    }
    //[e]modify by xienana for count department member @20161124 review by tangsha

    /**
     * 安全通信开启  通知联系人刷新
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            EncryptRecordService encryptRecordService = new EncryptRecordService(ActomaController.getApp());
            if (intent.getAction().equals(RegisterActionUtil.ACTION_OPEN_TRANSFER)) {
                //打开开关
                treeAdapter.setEncryptRecord(encryptRecordService.lastSelectedRecord());
                //[S] modify by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
                //add by lwl start 要加密的账号
//                String selectAccount=null;
//                Map map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
//                if(!ObjectUtil.mapIsEmpty(map)){
//                    selectAccount=(String) map.get("destAccount");
//                }
                map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
                if(!ObjectUtil.mapIsEmpty(map)){
                    if(!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))){
                        treeAdapter.setSelectAccount((String) map.get("destAccount"));
                    }
                    if(!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))){
                        treeAdapter.setAppPackageName((String) map.get("appPackage"));
                    }
                }
                fresh=true;
//                treeAdapter.setSelectAccount(selectAccount);
                //add by lwl end 要加密的账号
                //[E] modify by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
                handler.sendEmptyMessage(HANDLER_DEFAULT);
            } else if (RegisterActionUtil.ACTION_DELETE_FRIEND_CLOSE_TRANSFER.equals(intent.getAction())
                    || RegisterActionUtil.ACTION_DELETE_DEPARTMEMBER_CLOSE_TRANSFER.equals(intent.getAction())) {//add by xnn for bug 9932
//               删除好友或集团成员时关闭安全通道开关
                treeAdapter.setSelectAccount("");
                treeAdapter.setAppPackageName("");
                treeAdapter.setEncryptRecord(null);
                BusProvider.getMainProvider().post(new CloseMenuEvent());
                handler.sendEmptyMessage(HANDLER_DEFAULT);
            }else if(RegisterActionUtil.ACTION_SELECTED_OPEN_TRANSFER.equals(intent.getAction())){
                treeAdapter.setEncryptRecord(encryptRecordService.lastSelectedRecord());
                handler.sendEmptyMessage(HANDLER_DEFAULT);
                //[s]modify by xienana for bug pull contact but company not refresh sync @20161026[review by wangalei]
            } else if (RegisterActionUtil.ACTION_DEPARTMENT_DOWNLOAD_SUCCESS.equals(intent.getAction())
                    || RegisterActionUtil.ACTION_REFRESH_LIST.equals(intent.getAction())) {
                LogUtil.getUtils().e("Actoma contact ContactCompany ---department contact list download success---refresh");
                BusProvider.getMainProvider().post(new CloseMenuEvent());
                new GetDepartmentData(-1).execute();
                //[e]modify by xienana for bug pull contact but company not refresh sync @20161026[review by waangalei]
            }else if(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS.equals(intent.getAction())){
                //start:add for 2390 by wal@xdja.com revoew by lwl 2016/08/03
                LogUtil.getUtils().e("Actoma contact ContactCompany---department account info download success---refresh");
                String data_type=intent.getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_TYPE);
                String data = intent.getStringExtra(RegisterActionUtil.EXTRA_KEY_DATA_KEY);
                //[s]modify by xienana for bug 5239 @20161026[review by wangalei]
                if (!ObjectUtil.stringIsEmpty(data)&&!ObjectUtil.stringIsEmpty(data_type)) {
                    for (int i = 0; i < nodes.size(); i++) {
                        TreeNode node = nodes.get(i);
                        if (node.getSource() instanceof Member) {
                            Member updateMember = (Member)node.getSource();
                            if ((RegisterActionUtil.EXTRA_KEY_DATA_TYPE_WORK_ID.equals(data_type)&&data.equals(updateMember.getWorkId()))
                                    ||(RegisterActionUtil.EXTRA_KEY_DATA_TYPE_ACCOUNT.equals(data_type)&&data.equals(updateMember.getAccount()))) {
                                MemberService memberService = new MemberService();
                                Member member = memberService.getMemberById(updateMember.getWorkId());
                                node.setSource(member);
                                treeAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }//[e]modify by xienana for bug 5239 @20161026[review by wangalei]
                //end:add for 2390 by wal@xdja.com revoew by lwl 2016/08/03
            }
            // [S] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
            if (intent.getAction().equals(RegisterActionUtil.ACTION_CLOSE_FRAME_SAFETRANSFER)) {
                //关闭安全通道开关
                treeAdapter.setSelectAccount("");
                treeAdapter.setAppPackageName("");
                treeAdapter.setEncryptRecord(null);
                BusProvider.getMainProvider().post(new CloseMenuEvent());
                handler.sendEmptyMessage(HANDLER_DEFAULT);
            }
            if(intent.getAction().equals(RegisterActionUtil.ACTION_CONTACT_GIVE_APP_NAME)){
                treeAdapter.setSelectAccount(intent.getStringExtra("destAccount"));
                treeAdapter.setAppPackageName(intent.getStringExtra("appShowName"));
                handler.sendEmptyMessage(HANDLER_DEFAULT);
            }
            if(intent.getAction().equals(RegisterActionUtil.ACTION_CLOSE_TANSFER)){
                map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
                if(!ObjectUtil.mapIsEmpty(map)){
                    if(!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))){
                        treeAdapter.setSelectAccount((String) map.get("destAccount"));
                    }
                    if(!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))){
                        treeAdapter.setAppPackageName((String) map.get("appPackage"));
                    }
                }else {
                    treeAdapter.setAppPackageName(null);
                }
                handler.sendEmptyMessage(HANDLER_DEFAULT);
            }
            //[E] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(RegisterActionUtil.ACTION_OPEN_TRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_DELETE_FRIEND_CLOSE_TRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_SELECTED_OPEN_TRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_DEPARTMENT_DOWNLOAD_SUCCESS);
        filter.addAction(RegisterActionUtil.ACTION_ACCOUNT_INFO_DOWNLOAD_SUCCESS);//add for 2390 by wal@xdja.com revoew by lwl 2016/08/03
        filter.addAction(RegisterActionUtil.ACTION_REFRESH_LIST);//add by xienana for sync contact and company
        //[S] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
        filter.addAction(RegisterActionUtil.ACTION_CLOSE_FRAME_SAFETRANSFER);
        filter.addAction(RegisterActionUtil.ACTION_CONTACT_GIVE_APP_NAME);
        filter.addAction(RegisterActionUtil.ACTION_CLOSE_TANSFER);
        //[E] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
        filter.addAction(RegisterActionUtil.ACTION_DELETE_DEPARTMEMBER_CLOSE_TRANSFER);//add by xnn for bug 9932
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try{
            if(broadcastReceiver != null){
                getActivity().unregisterReceiver(broadcastReceiver);
            }
        }catch (Exception e){
            LogUtil.getUtils().e("Actoma contact ContactCompany,onDestroyView,unregisterReceiver error");
        }

    }

    @Override
    public void updateContactCompanyData() {
        if(!ContactModuleService.checkNetWork()) return ;
        new DepartmentHttpTask(new OnBatchTaskListener<List<Department>, HttpErrorBean>() {
            @Override
            public void onBatchTaskSuccess(List<Department> result) {
                updateMember();
            }
            @Override
            public void onBatchTaskFailed(HttpErrorBean result) {
                getVu().stopRefush();
                //start:add by wal@xdja.com for 3991
                //[s]modify by xienana for bug 6460 @20161201 review by self
                if (!ObjectUtil.objectIsEmpty(result)){
                    LogUtil.getUtils().e("Actoma contact ContactCompany updateContactCompanyData error ="+result);
                    if(result.getErrCode().equals(ERR_ACCOUNT_NOT_BELONG_EC)){
                        XToast.show(getActivity(),R.string.not_belong_department);
                    }else{
					 	XToast.show(getActivity(), result.getMessage());
					}
                    //[e]modify by xienana for bug 6460 @20161201 review by self
                }else{
                    XToast.show(getActivity(), R.string.contact_net_error);
                }
                //end:add by wal@xdja.com for 3991
            }
        }).template(0);
    }

    public void updateMember() {
        new DepartMemberHttpTask(new OnBatchTaskListener<List<Member>, HttpErrorBean>() {
            @Override
            public void onBatchTaskEnd() {
                getVu().stopRefush();
            }
            @Override
            public void onBatchTaskSuccess(List<Member> result) {
                getVu().refreshContactCount(); //add by xienana for department count @20161125 review by tangsha
                getVu().stopRefush();
                if (!ObjectUtil.collectionIsEmpty(result)) {
                    //Note: 过滤数据如果有删除的集团人员且正好是加密选中的人员则关闭第三方加密模块
                    BroadcastManager.refreshCompanyContact();
                }
            }
            @Override
            public void onBatchTaskFailed(HttpErrorBean result) {getVu().stopRefush();}
        }).template(0);
    }


    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
        treeAdapter = new CompanyTreeAdapter(ActomaController.getApp(), nodes);
        LogUtil.getUtils().e("Actoma contact ContactCompany,-------->>preBind begin<<--------");
        new GetDepartmentData(-1).execute();
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        getVu().setCompanyTreeAdapter(treeAdapter);
    }


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
     * 将部门转换为Node
     *
     * @param departments
     * @return
     */
    private List<TreeNode> convertDepart2Node(List<Department> departments) {

        List<TreeNode> departNodes = new ArrayList<>();
        for (Department department : departments) {
            TreeNode<Department> treeNode = new TreeNode<>();
            treeNode.setName(department.getDepartmentName());
            treeNode.setLeaf(false);
            treeNode.setSource(department);
            treeNode.setId(department.getDepartmentId());
            departNodes.add(treeNode);
        }
        return departNodes;
    }

    /**
     * 将部门和部门下成员转换为Node
     *
     * @param departments
     * @param members
     * @return
     */
    private List<TreeNode> convertDepart2Node(List<Department> departments, List<Member> members) {
        List<TreeNode> departNodes = new ArrayList<>();
        if (!ListUtils.isEmpty(members)) {
            List<TreeNode> children = convertMember2Node(members);
            departNodes.addAll(children);
        }
        for (Department department : departments) {
            TreeNode<Department> treeNode = new TreeNode<>();
            treeNode.setName(department.getDepartmentName());
            treeNode.setLeaf(false);
            treeNode.setSource(department);
            treeNode.setId(department.getDepartmentId());

            departNodes.add(treeNode);
        }
        return departNodes;
    }

    /**
     * 将Member转换为Node
     *
     * @param members
     * @return
     */
    private List<TreeNode> convertMember2Node(List<Member> members) {
        List<TreeNode> memberNodes = new ArrayList<>();
        for (Member member : members) {
            TreeNode<Member> treeNode = new TreeNode<>();
            treeNode.setName(member.getName());
            treeNode.setLeaf(true);
            treeNode.setIconUrl(member.getAvatarInfo().getAvatar());
            treeNode.setSource(member);
            memberNodes.add(treeNode);
        }
        return memberNodes;
    }

    /**
     * 查询部门信息
     */
    private class GetDepartmentData extends AsyncTask<TreeNode, Integer, List<TreeNode>> {

        private int pos;
        private TreeNode rootNode;

        private GetDepartmentData(int pos) {
            this.pos = pos;
        }

        @Override
        protected void onPostExecute(List<TreeNode> treeNodes) {
            super.onPostExecute(treeNodes);
            if(nodes==null)return;//add by lwl 743
            LogUtil.getUtils().e("Actoma contact ContactCompany,-------->>onPost end<<----------");
            if(ObjectUtil.objectIsEmpty(rootNode)){
                nodes.clear();
                if (ObjectUtil.collectionIsEmpty(nodes)) {
                    nodes.addAll(treeNodes);
                } else {
                    nodes.addAll(pos + 1, treeNodes);
                }
                //Start:add by wal@xdja.com for 4424
//                Message msg = handler.obtainMessage();
//                msg.what = HANDLER_OBJECT;
//                msg.obj = nodes;
//                handler.sendMessage(msg);
                treeAdapter.setNodes(nodes);
                //end:add by wal@xdja.com for 4424
            } else {
                rootNode.addChild(treeNodes);
                if (ObjectUtil.collectionIsEmpty(nodes)) {
                    nodes.addAll(treeNodes);
                } else {
                    nodes.addAll(pos + 1, treeNodes);
                }
                //Start:add by wal@xdja.com for 4424
//                Message msg = handler.obtainMessage();
//                msg.what = HANDLER_OBJECT;
//                msg.obj = nodes;
//                handler.sendMessage(msg);
                treeAdapter.setNodes(nodes);
                //End:add by wal@xdja.com for 4424
            }
 			getVu().refreshContactCount(); //add by xienana for department count @20161125 review by tangsha
            getVu().stopRefush();
        }


        @Override
        protected List<TreeNode> doInBackground(TreeNode... params) {
            LogUtil.getUtils().e("Actoma contact ContactCompany,doInBackground-------->>first<<--------");
            DepartService departService = new DepartService(ActomaController.getApp());
            String departid = null;
            String departmentName = null;
            List<Department> departments;
            //如果参数为空查询根目录,否则询部门下子部门和人员
            if (params != null && params.length > 0) {
                rootNode = params[0];
                departid = rootNode.getId();
            }
            //查询部门下成员
            MemberService service = new MemberService();
            List<Member> membersInDepart = service.getMembersInDepart(departid);
            if (!ListUtils.isEmpty(membersInDepart)) {
                String currentAccount = ContactUtils.getCurrentAccount();
                for (int i = membersInDepart.size() - 1; i >= 0; i--) {
                    if (ObjectUtil.stringIsEmpty(currentAccount)) {
                        break;
                    }
                    Member member = membersInDepart.get(i);
                    //如果取不到部门名称则到数据库内查询
                    if (TextUtils.isEmpty(departmentName)) {
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
                LogUtil.getUtils().e("Actoma contact ContactCompany,convertDepart2Node--------------->>>first-first-first<<<------------------");
            }

            if (!TextUtils.isEmpty(departid)) {
                departments = departService.getChildDepartment(departid);
            } else {
                departments = departService.getRootDepartment();
            }

            if (rootNode != null && rootNode.getSource() instanceof Department) {
                departmentName = ((Department) rootNode.getSource()).getDepartmentName();
            }
            LogUtil.getUtils().e("Actoma contact ContactCompany,MemberService-------->>MemberService<<----------");
            return convertDepart2Node(departments, membersInDepart);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.getUtils().e("Actoma contact ContactCompany,onDestroy");
        nodes = null;
        treeAdapter = null;
        try{
            if(broadcastReceiver != null){
                getActivity().unregisterReceiver(broadcastReceiver);
            }
        }catch (Exception e){
            LogUtil.getUtils().e("Actoma contact ContactCompany,onDestroy,unregisterReceiver "+e.getMessage());
        }
    }

    private static class ContactCompanyHandler extends Handler{
        private WeakReference<ContactCompanyPresenter> mActivity;
        ContactCompanyHandler(ContactCompanyPresenter activity){
            mActivity = new WeakReference<ContactCompanyPresenter>(activity);
        }
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            ContactCompanyPresenter activity = mActivity.get();
            if(activity != null){
                activity.processMessage(msg);
            }
        }
    }

    private ContactCompanyHandler handler = new ContactCompanyHandler(this);

    private void processMessage(final Message msg){
        int what = msg.what;
        LogUtil.getUtils().d("Actoma contact ContactCompany,department dispatchMessage processMessage what "+what);
        if(what == HANDLER_DEFAULT){
            if(!ObjectUtil.objectIsEmpty(treeAdapter)) {
                treeAdapter.notifyDataSetChanged();
            }
        }else{
            List<TreeNode> dataSource = (List<TreeNode>) msg.obj;
            treeAdapter.setNodes(dataSource);
        }
    }

//add by lwl 刷新集团通讯录的状态
    @Override
    public void onResume() {
        super.onResume();
        if(treeAdapter!=null&&fresh){
            treeAdapter.notifyDataSetChanged();
            fresh=false;
        }
        // [S] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
        map = IEncryptUtils.queryAccountAppEncryptSwitchStatus();
        if(!ObjectUtil.mapIsEmpty(map)){
            if(!ObjectUtil.stringIsEmpty((String) map.get("destAccount"))){
                treeAdapter.setSelectAccount((String) map.get("destAccount"));
            }
            if(!ObjectUtil.stringIsEmpty((String) map.get("appPackage"))){
                treeAdapter.setAppPackageName((String) map.get("appPackage"));
            }
            treeAdapter.notifyDataSetChanged();
        }else {
            treeAdapter.setAppPackageName(null);
            treeAdapter.notifyDataSetChanged();
        }
        // [E] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
    }
}
