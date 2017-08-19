package com.xdja.imp.presenter.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.xdja.comm.event.UpdateContactShowNameEvent;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactProxyEvent;
import com.xdja.contactopproxy.ContactService;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.frame.mvp.presenter.BasePresenterItemAdapter;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.ChatListAdapterCommand;
import com.xdja.imp.ui.ViewActomaItem;
import com.xdja.imp.ui.ViewGroupItem;
import com.xdja.imp.ui.ViewSingleItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.presenter.adapter</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/24</p>
 * <p>Time:15:11</p>
 */
public class ChatListAdapterPresenter
        extends BasePresenterItemAdapter<ChatListAdapterCommand, TalkListBean>
        implements ChatListAdapterCommand {

    private final List<TalkListBean> dataSource;

    private List<Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>>> vuClasses;

    private Activity activity;

    private ListView listView;

    @Inject
    Lazy<ContactService> contactService;

    private final BusProvider busProvider;

    public ChatListAdapterPresenter(List<TalkListBean> dataSource,BusProvider busProvider) {
        this.dataSource = dataSource;
        //初始化事件总线
        this.busProvider = busProvider;
        this.busProvider.register(this);
    }

    /**
     * 设置相关绑定的Activity
     * @param activity 目标Activity
     */
    public void setActivity(Activity activity){
        this.activity = activity;
    }

    /**
     * 设置适配器绑定的ListView
     * @param listView 目标ListView
     */
    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    protected List<Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>>> getVuClasses() {
        if (this.vuClasses == null) {
            this.vuClasses = new ArrayList<>();
            this.vuClasses.add(ViewSingleItem.class);
            this.vuClasses.add(ViewGroupItem.class);
            this.vuClasses.add(ViewActomaItem.class);
        }
        return vuClasses;
    }

    @Override
    protected ChatListAdapterCommand getCommand() {
        return this;
    }

    @Override
    protected TalkListBean getDataSource(int position) {
        return this.dataSource != null ? this.dataSource.get(position) : null;
    }

    @Override
    public int getViewTypeCount() {
        return this.getVuClasses() != null ? this.getVuClasses().size() : 0;
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public int getItemViewType(int position) {
        if (this.dataSource != null) {
            switch (this.dataSource.get(position).getTalkType()) {
                case ConstDef.CHAT_TYPE_P2P:
                    return 0;
                case ConstDef.CHAT_TYPE_P2G:
                    return 1;
                case ConstDef.CHAT_TYPE_ACTOMA:
                    return 2;
            }
        }
        return 0;
    }

    @Override
    protected Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>> getVuClassByViewType(int itemViewType) {
       /* Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>> vuCls;
        vuCls = this.getVuClasses().get(itemViewType);*/
        return this.getVuClasses().get(itemViewType);
    }

    @Override
    public int getCount() {
        return this.dataSource != null ? this.dataSource.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return this.dataSource != null ? this.dataSource.get(position) : null;
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(this.dataSource);
        //add by zya@xdja.com bug NACTOMA-338
        LogUtil.getUtils().i("ChatListAdapterPresenter:notifyDataSetChanged->dataSource:" + dataSource.toString());
        super.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected ListView getListView() {
        return this.listView;
    }

    @Override
    protected Activity getActivity() {
        return this.activity;
    }

    @Override
    public ContactInfo getContactInfo(String account) {
        return contactService.get().getContactInfo(account);
    }

    @Override
    public ContactInfo getGroupInfo(String groupId) {
        return contactService.get().getGroupInfo(groupId);
    }

    /**
     * 获取群成员信息
     *@param groupId
     * @param account
     * @return
     */
    @Override
    public ContactInfo getGroupMemberInfo(String groupId,String account) {
        return contactService.get().GetGroupMemberInfo(groupId,account);
    }

    public void refreshItem(String talkFlag, int chatType) {

        // 当前listView显示的第一个元素的未知
        int firstVisPosition = listView.getFirstVisiblePosition();
        // 当前listView显示的最后一个元素的位置
        int lastVisPosition = listView.getLastVisiblePosition();

        int position = getPosition(talkFlag,firstVisPosition, lastVisPosition);
        // 如果要更新的元素不在当前屏幕显示中，阻止界面更新操作
        if (position < 0) {
            return;
        }
        updateItem(position);

    }

    private int getPosition(String talkFlag,int firstVisPosition, int lastVisPosition) {
        int position = -1;
        if(dataSource.size()==0){
            return position;
        }
        for (int i = firstVisPosition; i <= lastVisPosition; i++) {
            if(dataSource.size()==0){
                break;
            }
            if (talkFlag.equals(dataSource.get(i).getTalkFlag())) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void unregister() {
        if (busProvider != null) {
            busProvider.unregister(this);
        }
    }

}
