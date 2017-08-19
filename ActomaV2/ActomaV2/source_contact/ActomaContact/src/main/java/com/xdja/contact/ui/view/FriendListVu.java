package com.xdja.contact.ui.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.callback.OnTouchingLetterChangedListener;
import com.xdja.contact.presenter.adapter.FriendListAdapter;
import com.xdja.contact.presenter.command.IFriendListCommand;
import com.xdja.contact.ui.def.IFriendListVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.view.SlidarView;
import com.xdja.contact.view.SwpipeListViewOnScrollListener;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/7/21.
 * AdapterView.OnItemLongClickListener,
 */
public class FriendListVu extends FragmentSuperView<IFriendListCommand> implements IFriendListVu,
        OnTouchingLetterChangedListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = FriendListVu.class.getSimpleName();

    private FriendListAdapter adapter;

    private List<Friend> dataSource = new ArrayList<Friend>();

    private ListView listView;

    private SlidarView slidarView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView slideIndexTxt;

    //[S]add by tangsha@20161128 for show friend count
    LinearLayout contact_footer;
    TextView contact_count;
    //[E]add by tangsha@20161128 for show friend count

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        listView = (ListView)getView().findViewById(R.id.friend_listview);
        slidarView = (SlidarView)getView().findViewById(R.id.slidarview);
        swipeRefreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeColors(getColorRes(R.color.base_title_gold));
        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnScrollListener(new SwpipeListViewOnScrollListener(swipeRefreshLayout));

        slideIndexTxt = ButterKnife.findById(getView(), R.id.sliderTv);
        slidarView.setTextView(slideIndexTxt);
        //[S]add by tangsha@20161128 for show friend count
        contact_footer = (LinearLayout) inflater.inflate(R.layout.contact_count_footer,null);
        contact_count = (TextView)contact_footer.findViewById(R.id.contact_count);
        listView.addFooterView(contact_footer,null,false);
        //[E]add by tangsha@20161128 for show friend count
        initListener();
    }

    private void initListener() {
        slidarView.setOnTouchingLetterChangedListener(this);
        listView.setOnItemClickListener(this);
        //listView.setOnItemLongClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.friend_list;
    }

    @Override
    public void setAdapter(FriendListAdapter adapter) {
        this.adapter = adapter;
        this.adapter.setListView(listView);
        listView.setAdapter(adapter);
    }

    @Override
    public void setDataSource(List<Friend> params, int friendSize) {
        if(!ObjectUtil.collectionIsEmpty(dataSource)){
            dataSource.clear();
        }
        if(!ObjectUtil.collectionIsEmpty(params)) {
            dataSource.addAll(params);
            LogUtil.getUtils(TAG).d("setDataSource size=" + dataSource.size());
        }
        //[S]add by tangsha@20161128 for show friend count
        contact_count.setText(getContext().getResources().getQuantityString(R.plurals.friend_count,friendSize,friendSize));
        //[E]add by tangsha@20161128 for show friend count
        adapter.setDataSource(dataSource);
    }

    @Override
    public void stopRefush() {
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onTouchingLetterChanged(String key) {
        int position = adapter.getPositionForString(key);
        if (position > -1) {
            listView.setSelection(position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(ContactUtils.isFastDoubleClick()){
            return;
        }
        Friend friend = dataSource.get(position);
        if(ObjectUtil.objectIsEmpty(friend) )return ;
        if(friend.getViewType() == Friend.ALPHA)return;
        getCommand().startDetailFriend(friend);
        return;
    }

    @Override
    public void onRefresh() {
        if(!ContactModuleService.checkNetWork()) {
            stopRefush();
            return;
        }
        //if(TaskManager.getInstance().isEmpty()) {
            getCommand().updateFriendList();
            getCommand().updateFriendRequestList();
//        }else{
//            LogUtil.getUtils().i("线程管理里面还有任务-----拒绝执行下拉刷新");
//            swipeRefreshLayout.setRefreshing(false);
//            return;
//        }
    }

    @Override
    public int friendListCount() {
        if(listView == null){
            return 0;
        }
		//[S]add by tangsha@20161128 for show friend count
        int footViewCount = listView.getFooterViewsCount();
        LogUtil.getUtils().d(TAG+" friendListCount footViewCount "+footViewCount);
        return listView.getCount() - footViewCount;
		//[E]add by tangsha@20161128 for show friend count
    }


}
