package com.xdja.contact.ui.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.callback.IPullCallback;
import com.xdja.contact.presenter.adapter.GroupListAdapter;
import com.xdja.contact.presenter.command.IGroupListCommand;
import com.xdja.contact.task.group.TaskIncrementGroup;
import com.xdja.contact.ui.def.IGroupListVu;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.view.SwpipeListViewOnScrollListener;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;

/**
 * Created by XDJA_XA on 2015/7/17.
 */
public class GroupListView extends FragmentSuperView<IGroupListCommand> implements
        IGroupListVu,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        IPullCallback {
    private final String TAG = GroupListView.class.getSimpleName();
    private ListView mListView;
    private View mEmptyView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayout groupCountLinearLayout;  //用于显示群组个数
    private TextView groupCountTv;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
		
		mListView = (ListView)getView().findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
		mEmptyView = getView().findViewById(R.id.empty_list_view);

        swipeRefreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swipe_group_container);
        swipeRefreshLayout.setColorSchemeColors(getColorRes(R.color.base_title_gold));
        swipeRefreshLayout.setOnRefreshListener(this);
        mListView.setOnScrollListener(new SwpipeListViewOnScrollListener(swipeRefreshLayout));

        groupCountLinearLayout = (LinearLayout) inflater.inflate(R.layout.contact_count_footer,null);
        groupCountTv = (TextView)groupCountLinearLayout.findViewById(R.id.contact_count);

        mListView.setEmptyView(mEmptyView);
        mListView.addFooterView(groupCountLinearLayout, null, false);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.group_list_fragment;
    }

    @Override
    public void setAdapter(GroupListAdapter adapter) {
        if (mListView != null) {
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "mListView must not be null when setAdapter");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getCommand().startChatActivity(position);
    }

    @Override
    public void onRefresh() {
        if(!ContactModuleService.checkNetWork()) {
            stopRefresh();
            return;
        }
        //if(TaskManager.getInstance().isEmpty()) {
            //getCommand().updateGroupIncrement();
            new TaskIncrementGroup(this).template();
       // }else{
           // LogUtil.getUtils().i("线程管理里面还有任务-----拒绝执行下拉刷新");
           // swipeRefreshLayout.setRefreshing(false);
           // return;
       // }
    }

    @Override
    public void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setListEmpty() {
        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public boolean isSupportLoading() {
        return true;
    }

    @Override
    public void stopRefreshLoading() {
        if(!ObjectUtil.objectIsEmpty(swipeRefreshLayout)) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    //start:add by wal@xdja.com for 3991
    @Override
    public void onShowErrorToast(HttpErrorBean httpErrorBean) {
        if (!ObjectUtil.objectIsEmpty(httpErrorBean)){
            XToast.show(getActivity(), httpErrorBean.getMessage());
        }else{
            XToast.show(getActivity(),R.string.contact_net_error);
        }
    }
    //end:add by wal@xdja.com for 3991


    @Override
    public void setGroupCount(int count) {
        if(count != 0) {
            groupCountTv.setText(getContext().getResources().getQuantityString(R.plurals.group_count,count,count));
        }
    }
}
