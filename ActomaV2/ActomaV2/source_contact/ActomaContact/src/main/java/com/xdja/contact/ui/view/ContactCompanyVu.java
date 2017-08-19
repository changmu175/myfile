package com.xdja.contact.ui.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.presenter.adapter.CompanyTreeAdapter;
import com.xdja.contact.presenter.fragment.ContactCompanyPresenter;
import com.xdja.contact.ui.def.IContactCompanyVu;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;

import butterknife.ButterKnife;

/**
 * @author hkb.
 * @since 2015/7/16/0016.
 */
public class ContactCompanyVu extends FragmentSuperView<ContactCompanyPresenter> implements IContactCompanyVu,CompanyTreeAdapter.IShowDismissDialog {

    private ListView listView;

    private SwipeRefreshLayout refreshLayout;

    private LinearLayout contact_footer;

    private TextView contact_count;


    @Override
    protected int getLayoutRes() {
        return R.layout.contact_fragment;
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        //通讯录隐藏搜索操作
        RelativeLayout searchLayout = ButterKnife.findById(getView(), R.id.search_layout);
        searchLayout.setVisibility(View.GONE);
        listView = ButterKnife.findById(getView(), R.id.contact_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getCommand().onNodeClick(position);
            }
        });

        refreshLayout = ButterKnife.findById(getView(), R.id.swipe_container);
        refreshLayout.setColorSchemeColors(getColorRes(R.color.base_title_gold));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!ContactModuleService.checkNetWork()) {
                    stopRefush();
                    return;
                }
               // if(TaskManager.getInstance().isEmpty()) {
                    getCommand().updateContactCompanyData();
//                }else{
//                    LogUtil.getUtils().i("线程管理里面还有任务-----拒绝执行下拉刷新");
//                    refreshLayout.setRefreshing(false);
//                    return;
//                }
            }
        });
        //[s]modify by xienana for count department member @20161124 review by tangsha
        contact_footer = (LinearLayout) inflater.inflate(R.layout.contact_count_footer,null);
        contact_count = (TextView)contact_footer.findViewById(R.id.contact_count);
        //[e]modify by xienana for count department member @20161124 review by tangsha
    }

    @Override
    public void setCompanyTreeAdapter(CompanyTreeAdapter treeViewAdapter) {
        treeViewAdapter.setShowDismissDialogCallback(this);
        treeViewAdapter.setListView(listView);
        //[s]modify by xienana for count department member @20161124 review by tangsha
        listView.addFooterView(contact_footer,null,false);
        //[e]modify by xienana for count department member @20161124 review by tangsha
        listView.setAdapter(treeViewAdapter);
    }


    //[s]modify by xienana for count department member @20161124 review by tangsha
    @Override
    public void refreshContactCount() {
        int count;
        int itemSize = listView.getAdapter() == null ? 0 : listView.getAdapter().getCount();
        if(contact_count != null && itemSize > 0){
            count = getCommand().refreshCount();
            if(count > 0){//modify by xienana for bug 7512 @20161228
                contact_count.setText(getContext().getResources().getQuantityString(R.plurals.department_count,count,count));//modify by xnn for pluras 
            }
        }else{
            LogUtil.getUtils().w("ContactCompanyVu refreshContactCount itemSize "+itemSize);
        }
    }
    //[e]modify by xienana for count department member @20161124 review by tangsha

    @Override
    public void stopRefush() {
        if (refreshLayout != null && refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showDialog(boolean flag) {
        if(flag){
            showCommonProgressDialog(getActivity().getString(R.string.contact_opening_safe_dialog));//modify by wal@xdja.com for string 正在开启...
        }else{
            showCommonProgressDialog(getActivity().getString(R.string.contact_closing_safe_dialog));//modify by wal@xdja.com for string 正在关闭...
        }
    }

    @Override
    public void dismissDialog() {
        dismissCommonProgressDialog();
    }

    private CustomDialog progressDialog;
    /**
     * 显示耗时动画
     */
    public void showCommonProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new CustomDialog(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_progress_dialog, null);
            progressDialog.setView(view);
        }
        //填写标题
        View view = progressDialog.getView();
        if (view != null) {
            TextView messageView = (TextView) view.findViewById(R.id.dialog_message);
            messageView.setText(msg);
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 取消耗时动画
     */
    public void dismissCommonProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
