package com.xdja.imp.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.data.AccountBean;
import com.xdja.comm.server.AccountServer;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.frame.mvp.view.FragmentSuperView;
import com.xdja.imp.presenter.command.ChatListCommand;
import com.xdja.imp.receiver.NetworkStateBroadcastReceiver;
import com.xdja.imp.ui.vu.ChatListVu;
import com.xdja.imp.util.Functions;
import com.xdja.imp.widget.ChatListPopupWindow;

/**
 * <p>Summary:会话列表视图</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.ui</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:14:00</p>
 */
public class ViewChatList extends FragmentSuperView<ChatListCommand> implements ChatListVu {
    private LinearLayout networkStateView;
    private TextView networkStateTv;

    private ListView listview;

    /**
     * 头像
     */
    private CircleImageView circleImageView;

    @Override
    protected int getLayoutRes() {
        return R.layout.chatlistfragment;
    }

    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            listview = (ListView)view.findViewById(R.id.chatList_fragment);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    getCommand().onListItemClick(i);
                }
            });

            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    return getCommand().onListItemLongClick(i);
                }
            });

            circleImageView = (CircleImageView)getView().findViewById(R.id.self);
            networkStateView = (LinearLayout)view.findViewById(R.id.network_unuseable);
            networkStateTv = (TextView)view.findViewById(R.id.networkstate_tv);
            //根据静态注册的广播接收器中保存的当前网络状态是否可用的值，来确定View是否可见
            changeNetworkViewState(NetworkStateBroadcastReceiver.getState());
        }
    }

    /**
     * 弹出进度提示框
     *
     * @param msgStr
     */
    @Override
    public void showProgressDialog(String msgStr) {
        showCommonProgressDialog(msgStr);
    }

    /**
     * 取消弹框
     */
    @Override
    public void dismissDialog() {
        dismissCommonProgressDialog();
    }

    @Override
    public void initListView(BaseAdapter adapter) {
        if (this.listview != null) {
            this.listview.setAdapter(adapter);
        }
    }

    @Override
    public void popuOptionWindow(TalkListBean talkListBean,
                                 ChatListPopupWindow.PopupWindowEvent<TalkListBean> event) {
        if (talkListBean.getTalkType() != ConstDef.CHAT_TYPE_ACTOMA) {
            ChatListPopupWindow.getInstance().showPopupDialog(getActivity(),
                    event,
                    talkListBean
            );
        }
    }

    @Override
    public void dismissPopuDialog() {
        ChatListPopupWindow.getInstance().dismissDialog();
    }

    @Override
    public ListView getDisplayList() {
        return this.listview;
    }

    /**
     * 加载自己的图像，此图像不显示，为了进入会话详情界面快速加载图像
     */
    @Override
    public void loadSelfImage() {
        AccountBean accountBean = AccountServer.getAccount();
        if (accountBean != null){
            String url = accountBean.getThumbnail();
            circleImageView.loadImage(url, true, R.drawable.corp_user_40dp);
        }
    }

    @Override
    public void changeViewSate(int state) {
        changeNetworkViewState(state);
        //guorong@xdja.com 当应用在后台运行时，不弹出网络不可用的toast .begin
        if(!NetworkStateBroadcastReceiver.isFirstChange
                && Functions.isAppOnForeground(getActivity())){
            //guorong@xdja.com 当应用在后台运行时，不弹出网络不可用的toast .end
            if(state == NetworkStateBroadcastReceiver.NET_DISABLED){
                Toast.makeText(getActivity(), R.string.network_disabled , Toast.LENGTH_LONG).show();
            }else if(state == NetworkStateBroadcastReceiver.NO_SERVER){
                Toast.makeText(getActivity(), R.string.network_no_server , Toast.LENGTH_LONG).show();
            }
        }
    }

    private void changeNetworkViewState(int flag){
        String tips;
        switch (flag){
            case NetworkStateBroadcastReceiver.NET_DISABLED:
                tips = getActivity().getResources().getString(R.string.network_disabled);
                networkStateTv.setText(tips);
                networkStateView.setVisibility(View.VISIBLE);
                break;
            case  NetworkStateBroadcastReceiver.NO_SERVER:
                tips = getActivity().getResources().getString(R.string.network_no_server);
                networkStateTv.setText(tips);
                networkStateView.setVisibility(View.VISIBLE);
                break;
            case NetworkStateBroadcastReceiver.NORMAL:
                networkStateView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
