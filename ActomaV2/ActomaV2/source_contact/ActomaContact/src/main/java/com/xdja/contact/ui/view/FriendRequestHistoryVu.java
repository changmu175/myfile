package com.xdja.contact.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.comm.server.ActomaController;
import com.xdja.contact.R;
import com.xdja.contact.bean.FriendRequestHistory;
import com.xdja.contact.presenter.adapter.FriendRequestHistoryAdapter;
import com.xdja.contact.presenter.command.IFriendRequestCommand;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IFriendRequestHistoryVu;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

/**
 * Created by wanghao on 2015/7/21.
 */
public class FriendRequestHistoryVu extends BaseActivityVu<IFriendRequestCommand> implements IFriendRequestHistoryVu,FriendRequestHistoryAdapter.IShowDismissDialog{

    private FriendRequestHistoryAdapter adapter;

    private ListView listview;

    private TextView searchTopLayout;

    private LinearLayout friendRequestListTop;

    private TextView currentAccount;

    private View listBottomDivider;


    @Override
    protected int getLayoutRes() {
        return R.layout.friend_request_list;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarType() {
        getCommand().backWithResult();
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        toolbar.setTitle(ActomaController.getApp().getString(R.string.contact_new_friend));//modify by wal@xdja.com for string 新的好友
        searchTopLayout = (TextView)getView().findViewById(R.id.search_area);
        listview = (ListView)getView().findViewById(R.id.history_list_view);
        friendRequestListTop = (LinearLayout)getView().findViewById(R.id.friend_request_list_top);
        currentAccount = (TextView)getView().findViewById(R.id.current_account);
        listBottomDivider = getView().findViewById(R.id.list_bottom_divider);
        initListener();
    }

    private void initListener(){

        searchTopLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().startSearch();
            }
        });
        //显示二维码
        friendRequestListTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCommand().showErWeiMa();

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getCommand().showFriendDetail(position);
            }
        });
        currentAccount.setText(ContactUtils.getCurrentAccountAlias());
    }

    @Override
    public void setAdapter(FriendRequestHistoryAdapter adapter) {
        this.adapter = adapter;
        listview.setAdapter(adapter);
    }

    @Override
    public void setDataSource(List<FriendRequestHistory> dataSource) {
        adapter.setDataSource(dataSource);
        if(ObjectUtil.collectionIsEmpty(dataSource)){
            listview.setVisibility(View.GONE);
            listBottomDivider.setVisibility(View.GONE);
        }else{
            listview.setVisibility(View.VISIBLE);
            listBottomDivider.setVisibility(View.VISIBLE);
        }
        adapter.setShowDismissDialogCallback(this);
    }

    @Override
    public int historyListCount() {
        if(listview == null){
            return 0;
        }
        return  listview.getCount();
    }

    @Override
    public void showDialog() {
        showCommonProgressDialog(ActomaController.getApp().getString(R.string.contact_accept_request));//modify by wal@xdja.com for string 正在接受...
    }

    @Override
    public void dismissDialog() {
        dismissCommonProgressDialog();
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.friend_history_title);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}

