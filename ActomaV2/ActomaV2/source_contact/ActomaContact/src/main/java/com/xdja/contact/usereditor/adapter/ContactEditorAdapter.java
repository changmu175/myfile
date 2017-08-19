package com.xdja.contact.usereditor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.https.HttpErrorBean;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.contact.presenter.activity.MultiDelPresenter;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.contact.ContactModuleService;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.GroupMember;
import com.xdja.contact.callback.IModuleHttpCallBack;
import com.xdja.contact.convert.GroupConvert;
import com.xdja.contact.http.GroupHttpServiceHelper;
import com.xdja.contact.http.response.group.ResponseCkmsOpSign;
import com.xdja.contact.service.FireEventUtils;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hkb.
 * 2015/7/9/0009.
 * <p/>
 * 2016-01-28 wanghao 重构
 */
public class ContactEditorAdapter extends BaseAdapter {

    private String groupId;

    private Group groupInfo;

    private String curAccount;

    private Context mContext;

    private boolean isEditModel;

    private boolean isGroupOwner;

    private List<UserInfo> dataSource;

    private View.OnClickListener onAddUserBtnClickListener;

    private CustomDialog progressDialog;

    public static final int GROUP_SHOW_MAX_MEMBER_COUNT = 40;

    /**
     * <pre>
     *
     * @param context
     * @param dataSource 函数内不再校验数据是否为空
     * @param groupInfo -当前群信息
     * </pre>
     * <pre>
     * 这里在初始化Adapter的时候初始化数据源,
     * 如果有必要的话将来做到异步查询里面但是必须确定因为这里的查询影响性能之后，
     * 所有目前暂时同步查询数据更新adapter
     * </pre>
     */
    public ContactEditorAdapter(Context context, List<UserInfo> dataSource, Group groupInfo) {
        this.mContext = context;
        this.dataSource = dataSource;
        this.groupInfo = groupInfo;
        this.groupId = groupInfo.getGroupId();
        this.curAccount = GroupUtils.getCurrentAccount(context);
        this.isGroupOwner = GroupUtils.isGroupOwner(context, groupId);
        initButtons();
    }

    /**
     * 当外界数据源发生改变时 填充当前函数通知adapter 刷新
     * @param dataSource
     */
    public void setDataSource(List<UserInfo> dataSource){
        this.dataSource = dataSource;
        initButtons();
        notifyDataSetChanged();
    }


    /**
     * <pre>
     *     根据当前群id和当前用户账号，加载指定的添加和删除按钮
     *     <li>当前用户是群主,群内只有当前1人,加载添加按钮</li>
     *     <li>当前用户是群主，包含其他成员大于1人，加载添加和删除按钮</li>
     *     <li>当前用户<font color=red>不是</font>群主，加载添加按钮</li>
     *
     *     补充：非群主且群成员个数大于最大显示个数限制时，给最大显示最后一个元素加上添加按钮，否则直接加上添加按钮。
     * </pre>
     */
    private void initButtons() {
        if (isGroupOwner && dataSource.size() == 1) {
            dataSource.add(getAddBtn());
        } else if (isGroupOwner && dataSource.size() >= 1) {
            if(dataSource.size() > GROUP_SHOW_MAX_MEMBER_COUNT - 2) {
                dataSource.addAll(GROUP_SHOW_MAX_MEMBER_COUNT - 2,getEditBtn());
            }else{
                dataSource.addAll(getEditBtn());
            }
        } else if (!isGroupOwner) {
            if (dataSource.size() > GROUP_SHOW_MAX_MEMBER_COUNT - 1) {
                dataSource.add(GROUP_SHOW_MAX_MEMBER_COUNT - 1, getAddBtn());
            } else {
                dataSource.add(getAddBtn());
            }
        }
    }

    public void removeItem(int position) {
        if (!ObjectUtil.collectionIsEmpty(dataSource) && position > -1) {
            dataSource.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * 返回包含"添加和删除"按钮的数据集合
     * 群主，并且群成员人数大于最大显示人数限制时，在最大显示人数最后两个位置加上加人和减人按钮
     */
    private List<UserInfo> getEditBtn() {
        List<UserInfo> buttons = new ArrayList<>();
        buttons.add(getAddBtn());
        buttons.add(getDeleteBtn());
        return buttons;
    }

    private UserInfo getAddBtn() {
        return new UserInfo.Builder(UserInfo.ID_ADD_USER).create();
    }

    private UserInfo getDeleteBtn() {
        return new UserInfo.Builder(UserInfo.ID_DEL_USER).create();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_editor_item, null);
        }
        Viewholder viewholder;
        if (convertView.getTag() == null) {
            viewholder = new Viewholder(convertView);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }
        UserInfo userInfo = dataSource.get(position);
        if (userInfo.getId() == UserInfo.ID_DEL_USER || userInfo.getId() == UserInfo.ID_ADD_USER) {
            viewholder.initEditButton(userInfo, position);
        } else {
            viewholder.refresh(userInfo, position);
        }
        return convertView;
    }

    private class ClickImageViewListener implements View.OnClickListener {

        public int position;

        private ClickImageViewListener(int position) {
            this.position = position;
        }


        @Override
        public void onClick(View v) {
            UserInfo userInfo = dataSource.get(position);
            if (ObjectUtil.objectIsEmpty(userInfo)) return;
            String account = userInfo.getAccount();
            String currentAccount = ContactUtils.getCurrentAccount();
            //start:modify by wal@xdja.com for 2001
            if (ObjectUtil.stringIsEmpty(account)) return;
            //start:modify by wal@xdja.com for 2001
            if (account.equals(currentAccount)) return;
            if(ContactModuleService.checkNetWork()) {
                ContactModuleService.startContactDetailActivity(mContext, account);
            }
        }
    }

    public ArrayList<UserInfo> getMembers() {
        ArrayList<UserInfo> result = new ArrayList<>();
        for (UserInfo UserInfo : dataSource) {
            if (UserInfo.getId() >= 0) {
                result.add(UserInfo);
            }
        }
        return result;
    }

    /**
     * 关闭编辑模式
     */
    //remove for modify delete mode
   /* public void exitEditModel() {
        if (isEditModel) {
            isEditModel = false;
            dataSource.addAll(getEditBtn());
            notifyDataSetChanged();
        }
    }

    // 关闭编辑模式
    public void exitEditModel2() {
        if (isEditModel) {
            isEditModel = false;
            dataSource.addAll(getEditBtn());
            int size = dataSource.size();
            if (size >= 4) {
                dataSource.remove(size - 1);
                dataSource.remove(size - 2);
            }
            notifyDataSetChanged();
        }
    }

    //关闭编辑模式3
    //add by lwl 2001
    public void exitEditModel3() {
        if (isEditModel) {
            isEditModel = false;
            int size = dataSource.size();

            dataSource.addAll(getEditBtn());

            if (size >= 4) {
                dataSource.remove(size - 1);
                dataSource.remove(size - 2);
            }
            notifyDataSetChanged();
        }else{
            notifyDataSetChanged();
        }
    }*/

    public boolean isEditMode() {
        return isEditModel;
    }

    private class Viewholder {
        private CircleImageView avatar;
        private ImageButton delBtn;
        private TextView name;
        private ImageButton editBtn;


        private Viewholder(View view) {
            avatar = (CircleImageView) view.findViewById(R.id.contact_eidtor_avater);
            delBtn = (ImageButton) view.findViewById(R.id.contact_eidtor_del_btn);
            name = (TextView) view.findViewById(R.id.contact_editor_name);
            editBtn = (ImageButton) view.findViewById(R.id.contact_eidtor_btn);
        }

        /**
         * 设置编辑按钮
         *
         * @param UserInfo 用户信息
         */
        public void initEditButton(UserInfo UserInfo, int position) {
            editBtn.setVisibility(View.VISIBLE);
            avatar.setVisibility(View.INVISIBLE);
            delBtn.setVisibility(View.GONE);
            name.setText("");
            editBtn.setOnClickListener(null);
            avatar.setOnClickListener(new ClickImageViewListener(position));
            //"增加"按钮
            if (UserInfo.getId() == UserInfo.ID_ADD_USER) {
                editBtn.setBackgroundResource(R.drawable.bg_add_user_btn_selector);
                editBtn.setOnClickListener(onAddUserBtnClickListener);
                //"删除"按钮
            } else if (UserInfo.getId() == UserInfo.ID_DEL_USER) {
                onClickDelBtn();
            }
        }

        private void onClickDelBtn() {
            editBtn.setBackgroundResource(R.drawable.bg_remove_user_btn_selector);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*isEditModel = true;
                    int size = dataSource.size();
                    dataSource.remove(size - 1);
                    dataSource.remove(size - 2);
                    notifyDataSetChanged();*/
                    mContext.startActivity(new Intent(mContext, MultiDelPresenter.class)
                            .putExtra(MultiDelPresenter.GROUP_ID, groupId)
                            .putExtra(MultiDelPresenter.OPEN_TYPE, MultiDelPresenter.MULTI_DEL_MEMBER));
                }
            });
        }


        /**
         * 刷新用户信息
         *
         * @param userInfo   用户信息
         * @param currentPos 当前用户索引
         */
        public void refresh(UserInfo userInfo, int currentPos) {
            avatar.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
            Avatar picture = userInfo.getAvatarBean();
            GroupUtils.loadAvatarToImgView(avatar, picture.getThumbnail(), R.drawable.img_avater_40);
            //是否编辑模式
            if (isEditModel) {
                //编辑模式显示删除按钮,并相应点击事件
                //除了自身，群主可以删除其他所有人
                if (userInfo.getAccount().equals(curAccount)) {
                    delBtn.setVisibility(View.GONE);
                    delBtn.setOnClickListener(null);//add by wal@xdja.com for 3029
                    avatar.setOnClickListener(null);
                } else {
                    delBtn.setVisibility(View.VISIBLE);
                    delBtn.setOnClickListener(new RemoveItemClickListener(currentPos));//add by wal@xdja.com for 3029
                    avatar.setOnClickListener(new RemoveItemClickListener(currentPos));
                }
            } else {
                delBtn.setVisibility(View.GONE);
                delBtn.setOnClickListener(null);//add by wal@xdja.com for 3029
                avatar.setOnClickListener(new ClickImageViewListener(currentPos));
            }
            name.setText(userInfo.getShowName());
        }
    }


    private class RemoveItemClickListener implements View.OnClickListener {
        private int currentPos;

        private RemoveItemClickListener(int currentPos) {
            this.currentPos = currentPos;
        }

        @Override
        public void onClick(View v) {
            if (!ContactModuleService.checkNetWork()) return;
            //移除群组成员
            final UserInfo deleteUserInfo = dataSource.get(currentPos);
            final String deleteAccount = deleteUserInfo.getAccount();
            showCommonProgressDialog(mContext.getString(R.string.deleting_group_member), false, true);
            GroupHttpServiceHelper.delMemberFromGroup(groupId, Arrays.asList(deleteAccount), new IModuleHttpCallBack() {
                @Override
                public void onFail(HttpErrorBean errorBean) {
                    dismissCommonProgressDialog();
                    LogUtil.getUtils().e("Actoma contact ContactEditorAdapter onClick" + ActomaController.getApp().getString(R.string.deleting_group_failed));
                    XToast.show(ActomaController.getApp(), ActomaController.getApp().getString(R.string.deleting_group_failed));
                }

                @Override
                public void onSuccess(String body) {
                   List<GroupMember> delMembers = new ArrayList<GroupMember>();
                    GroupMember groupMember = new GroupMember();
                    groupMember.setAccount(deleteAccount);
                    groupMember.setGroupId(groupId);
                    groupMember.setIsDeleted(GroupConvert.DELETED);
                    delMembers.add(groupMember);
                    removeItem(findMemberPosition(groupMember));
                    boolean bool = GroupInternalService.getInstance().deleteGroupMember(groupMember);
                    if (bool) {
                        dataSource.remove(deleteUserInfo);
                        FireEventUtils.fireDeleteMemberEvent(delMembers);
                    }
                    dismissCommonProgressDialog();
                   // exitEditModel3();//remove for modify delete mode
                    /*start:add by wal@xdja.com for ckms add group child 2016/08/02*/
                    //开会确认群主移除成员不要移除CMKS中的成员了
//                    if (CkmsGpEnDecryptManager.getCkmsIsOpen()){
//                        List<String> accountList= new ArrayList<String>();
//                        accountList.add(deleteAccount);
//                        GroupHttpServiceHelper.getCkmsGroupOpSign(new CkmsGroupHttpCallback(groupId,accountList),groupId,accountList, CkmsGpEnDecryptManager.REMOVE_ENTITY);
//                    }
                     /*end:add by wal@xdja.com for ckms add group child 2016/08/02*/
                }

                @Override
                public void onErr() {
                    dismissCommonProgressDialog();
                }
            });
        }
    }

    /*start:add by wal@xdja.com for ckms create group 2016/08/02*/
    private class CkmsGroupHttpCallback implements IModuleHttpCallBack{
        private String groupID;
        private List<String> accountList;
        private CkmsGroupHttpCallback(String groupID,List<String> accountList){
            this.groupID=groupID;
            this.accountList=accountList;
        }
        @Override
        public void onFail(HttpErrorBean httpErrorBean) {
            if(!ObjectUtil.objectIsEmpty(httpErrorBean)) {
                LogUtil.getUtils().e("Actoma contact ContactEditorAdapter CkmsGroupHttpCallback.onFail Ckms create group httpErrorBean.getMessage:"+httpErrorBean.getMessage());
            }else{
                LogUtil.getUtils().e("Actoma contact ContactEditorAdapter CkmsGroupHttpCallback.onFail Ckms create group exception");
            }
        }

        @Override
        public void onSuccess(String s) {
            String currentAccount = ContactUtils.getCurrentAccount();
            ResponseCkmsOpSign response = JSON.parseObject(s, ResponseCkmsOpSign.class);
            String opSign = response.getSignedOpCode();
            LogUtil.getUtils().d("CKMS_Contact CkmsGroupHttpCallback onSuccess opSign "+opSign);
            CkmsGpEnDecryptManager.removeEntities(currentAccount,accountList,groupID,opSign);
        }

        @Override
        public void onErr() {
            LogUtil.getUtils().e("Actoma contact ContactEditorAdapter CkmsGroupHttpCallback.onErr");
        }
    }
    /*end:add by wal@xdja.com for ckms create group 2016/08/02*/
    /*
   * 获取要删除的成员的position
   * */
    private int findMemberPosition(GroupMember member) {
        if (member == null || ListUtils.isEmpty(dataSource)) {
            return -1;
        }
        for (int i = 0; i < dataSource.size(); i++) {
            String memberId = member.getAccount();
            if (memberId.equals(dataSource.get(i).getAccount())) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 显示耗时动画
     */
    public void showCommonProgressDialog(String msg, boolean isTouch, boolean onKey) {
        if (progressDialog == null) {
            progressDialog = new CustomDialog(mContext);
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_progress_dialog, null);
            progressDialog.setView(view);
        }
        //填写标题
        View view = progressDialog.getView();
        if (view != null) {
            TextView messageView = (TextView) view.findViewById(R.id.dialog_message);
            messageView.setText(msg);
        }
        progressDialog.setCanceledOnTouchOutside(isTouch);
        progressDialog.setCancelable(onKey);
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


    public void setOnAddUserBtnClickListener(View.OnClickListener onAddUserBtnClickListener) {
        this.onAddUserBtnClickListener = onAddUserBtnClickListener;
    }

    public List<UserInfo> getDataSource(){
        return dataSource;
    }


    @Override
    public int getCount() {
        //return dataSource.size();
        return dataSource.size() > GROUP_SHOW_MAX_MEMBER_COUNT ? GROUP_SHOW_MAX_MEMBER_COUNT : dataSource.size();  //限制群成员显示人数（加两个按钮）最多为40
    }

    @Override
    public UserInfo getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
