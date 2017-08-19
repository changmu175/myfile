package com.xdja.imp.presenter.adapter;

import android.app.Activity;
import android.widget.ListView;

import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactService;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.frame.mvp.presenter.BasePresenterItemAdapter;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.ChatListAdapterCommand;
import com.xdja.imp.ui.ViewShareGrouoItem;
import com.xdja.imp.ui.ViewShareSingleItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

 /**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，分享界面会话选择列表Adapter
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
public class ChooseIMSessionAdapterPresenter extends BasePresenterItemAdapter<ChatListAdapterCommand, TalkListBean>
        implements ChatListAdapterCommand {
    private final List<TalkListBean> dataSource;
    private Activity activity;
    private ListView listView;

    @Inject
    Lazy<ContactService> contactService;
    private List<Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>>> vuClasses;
    public ChooseIMSessionAdapterPresenter(List<TalkListBean> dataSource) {
        this.dataSource = dataSource;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }
    /**
     * 设置相关绑定的Activity
     * @param activity 目标Activity
     */
    public void setActivity(Activity activity){
        this.activity = activity;
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
    protected List<Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>>> getVuClasses() {
        if (this.vuClasses == null) {
            this.vuClasses = new ArrayList<>();
            this.vuClasses.add(ViewShareSingleItem.class);
            this.vuClasses.add(ViewShareGrouoItem.class);
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
    public int getCount() {
        return this.dataSource != null ? this.dataSource.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return this.dataSource != null ? this.dataSource.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.dataSource != null) {
            switch (this.dataSource.get(position).getTalkType()) {
                case ConstDef.CHAT_TYPE_P2P:
                    return 0;
                case ConstDef.CHAT_TYPE_P2G:
                    return 1;
                case ConstDef.CHAT_TYPE_ACTOMA:
                    break;
                case ConstDef.CHAT_TYPE_DEFAULT:
                    break;
                case ConstDef.CHAT_TYPE_P2M:
                    break;
                case ConstDef.CHAT_TYPE_PIC_PREVIEW:
                    break;
                case ConstDef.CHAT_TYPE_PIC_SELECT:
                    break;
            }
        }
        return 0;
    }

    @Override
    protected Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>> getVuClassByViewType(int itemViewType) {
        Class<? extends AdapterVu<ChatListAdapterCommand, TalkListBean>> vuCls;
        vuCls = this.getVuClasses().get(itemViewType);
        return vuCls;
    }

    @Override
    public ContactInfo getContactInfo(String account) {
        return contactService.get().getContactInfo(account);
    }

    @Override
    public ContactInfo getGroupInfo(String groupId) {
        return contactService.get().getGroupInfo(groupId);
    }

    @Override
    public ContactInfo getGroupMemberInfo(String groupId, String account) {
        return contactService.get().GetGroupMemberInfo(groupId,account);
    }

    @Override
    public int getViewTypeCount() {
        return this.getVuClasses() != null ? this.getVuClasses().size() : 0;
    }

    @Override
    public void notifyDataSetChanged() { //重写通知，拿到配置之后对会话进行排序并去掉置顶的背景颜色
        Collections.sort(this.dataSource);
        int size = dataSource.size();
        for (int i = 0; i < size; i++) {
            if (dataSource.get(i).isShowOnTop()) {
                dataSource.get(i).setShowOnTop(false);
            }
        }
        super.notifyDataSetChanged();
    }
}
