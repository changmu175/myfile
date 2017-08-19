package com.xdja.contact.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.AuthInfo;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.presenter.command.IContactFriendApplyCommand;
import com.xdja.contact.service.ActomAccountService;
import com.xdja.contact.service.AvaterService;
import com.xdja.contact.ui.BaseActivityVu;
import com.xdja.contact.ui.def.IAcceptFriendApplyVu;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yangpeng on 2015/7/16.
 */
public class AcceptFriendApplyVu extends BaseActivityVu<IContactFriendApplyCommand> implements IAcceptFriendApplyVu {
    private CircleImageView avatar;

    private TextView showName;

    private LinearLayout accountLayout;

    private TextView accountText;

    private LinearLayout nickNameLayout;

    //private TextView nicknameText;
    //private LinearLayout bottomLayout;
    //private TextView applyDetail;
    private ListView requestListView;

    private TextView applyButton;

    private AcceptFriendAdapter acceptFriendAdapter;


    @Override
    public void onCreated() {
        super.onCreated();
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        initCommonDetail();
        //initAcceptLayout(inflater);
    }

    private void initCommonDetail(){
        toolbar.setBackgroundColor(getContext().getResources().getColor(R.color.toolbar_alpha));
        avatar = ButterKnife.findById(getView(), R.id.contact_detail_avater);
        showName = ButterKnife.findById(getView(), R.id.contact_person_name);
        accountLayout = ButterKnife.findById(getView(), R.id.account_number_lay);
        accountText = ButterKnife.findById(getView(), R.id.account_number);
        nickNameLayout = ButterKnife.findById(getView(), R.id.ninkname_lay);
        requestListView = ButterKnife.findById(getView(), R.id.accept_listview);

        applyButton = ButterKnife.findById(getView(), R.id.accept_apply_btn);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ContactModuleService.checkNetWork()) return;
                getCommand().agreeFriendApply();
            }
        });
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.accept_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void setFriendData(String account, List<AuthInfo> friendRequestInfos) {
        loadHeadIcon(account);
        loadHeadImage(account);
        ActomaAccount actomaAccount = new ActomAccountService().queryByAccount(account);
        if(ObjectUtil.objectIsEmpty(actomaAccount)){
            LogUtil.getUtils().e("Actoma contact AcceptFriendApplyVu,data is null");
            XToast.show(getContext(), R.string.invalid_account);
            getActivity().finish();
            return;
        }
        String nickname = actomaAccount.getNickname();
        if(!ObjectUtil.stringIsEmpty(nickname)){
            showName.setText(nickname);
            //start:add by wal@xdja.com for 409
//            accountText.setText(account);
            accountText.setText(actomaAccount.showAccount());
            //end:add by wal@xdja.com for 409
            accountLayout.setVisibility(View.VISIBLE);
            nickNameLayout.setVisibility(View.GONE);
        }else{
            //start:add by wal@xdja.com for 409
//            showName.setText(account);
            showName.setText(actomaAccount.showAccount());
            //end:add by wal@xdja.com for 409
            accountLayout.setVisibility(View.GONE);
            nickNameLayout.setVisibility(View.GONE);
        }
        acceptFriendAdapter = new AcceptFriendAdapter(getActivity());
        requestListView.setAdapter(acceptFriendAdapter);
        acceptFriendAdapter.setDataSource(friendRequestInfos);
    }

     private class AcceptFriendAdapter extends BaseAdapter {

        private List<AuthInfo> dataSource ;

        private Context context;

        private AcceptFriendAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            if(ObjectUtil.collectionIsEmpty(dataSource)){
                return 0;
            }
            return dataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.accept_list_item,null);
                //viewHolder.name = (TextView)convertView.findViewById(R.id.request_info_layout_name);
                viewHolder.content = (TextView)convertView.findViewById(R.id.request_info_layout_content);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            AuthInfo info = dataSource.get(position);
            //viewHolder.name.setText(name+"：");
            viewHolder.content.setText(info.getValidateInfo());
            return convertView;
        }

        public void setDataSource(List<AuthInfo> dataSource) {
            this.dataSource = dataSource;
            notifyDataSetChanged();
        }

        class ViewHolder{

            //TextView name;

            TextView content;
        }
    }

    @Override
    public void showDialog() {
        showCommonProgressDialog(getActivity().getString(R.string.contact_accepting_friend_request));//modify by wal@xdja.com for string 正在接受好友请求...
    }

    @Override
    public void dismissDialog() {
        dismissCommonProgressDialog();
    }

    private void loadHeadImage(final String account){
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Avatar avatar = new AvaterService().queryByAccount(account);
                //先从本地查找如果存在直接加载本地
                if (!ObjectUtil.objectIsEmpty(avatar)) {
                    AcceptFriendApplyVu.this.avatar.showImageDetail(avatar.getAvatar());
                } else {
                    AcceptFriendApplyVu.this.avatar.showImageDetail("");
                }
            }
        });
    }

    private void loadHeadIcon(String account){
        Avatar avatar = ContactModuleService.getAvatar(getActivity(), account);
        if(ObjectUtil.objectIsEmpty(avatar))return ;
        if (ObjectUtil.stringIsEmpty(avatar.getThumbnail())) return;
        this.avatar.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
    }

    @Override
    public void dismissCommonProgressDialog() {
        super.dismissCommonProgressDialog();
    }

    @Override
    public void showCommonProgressDialog(String msg) {
        super.showCommonProgressDialog(msg);
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.detail_info_title);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}

