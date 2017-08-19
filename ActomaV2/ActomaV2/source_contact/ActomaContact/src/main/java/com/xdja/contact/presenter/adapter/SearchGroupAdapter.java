package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.contact.R;
import com.xdja.contact.bean.Group;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;

import java.util.List;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，更多联系人中，选择群，搜索结果界面Adapter
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for share and forward function by ycm at 20161222.
 */
public class SearchGroupAdapter  extends BaseSelectAdapter {

    private List<Group> dataSource;

    private LayoutInflater mInflater;

    private String searchKey;
    protected Context context;

    public SearchGroupAdapter(Context context, ISelectCallBack callBack, List<String> existedMemberAccounts){
        super(context,callBack,existedMemberAccounts);
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(ObjectUtil.collectionIsEmpty(dataSource))return 0;
        return dataSource.size();
    }

    @Override
    public Group getItem(int position) {
        return dataSource.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.share_group_list_item, null);
            holder = new ViewHolder();
            holder.groupItemAvater = (CircleImageView) convertView.findViewById(R.id.avatar);
            holder.groupItemCheckbox = (CheckBox) convertView.findViewById(R.id.contact_item_checkbox);
            holder.groupName = (TextView) convertView.findViewById(R.id.group_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.groupItemCheckbox.setOnClickListener(new CheckedListener(position, holder));
        Group group = dataSource.get(position);
        String account = group.getGroupId();
        if (!ObjectUtil.stringIsEmpty(EXISTE_ACCOUNT_MAP.get(account))) {
            holder.groupItemCheckbox.setChecked(true);
            holder.groupItemCheckbox.setEnabled(false);
        } else {
            if (!ObjectUtil.objectIsEmpty(getSelected(account))) {
                holder.groupItemCheckbox.setEnabled(true);
                holder.groupItemCheckbox.setChecked(true);
                group.setIsChecked(true);
            } else {
                holder.groupItemCheckbox.setEnabled(true);
                holder.groupItemCheckbox.setChecked(false);
                group.setIsChecked(false);
            }
        }
        doSearch(group, holder);
        String avatar = group.getAvatar();
        GroupUtils.loadAvatarToImgView(holder.groupItemAvater, group, R.drawable.group_avatar_40);
        return convertView;
    }


    private class CheckedListener implements View.OnClickListener {

        private int position;

        private ViewHolder viewHolder;

        private CheckedListener(int position,ViewHolder viewHolder){
            this.position = position;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            int maxGroupMemberInt= PreferenceUtils.getGroupMemberLimitConfiguration(); //add by wal@xdja.com for max group member
            if(calculateAccounts() >= maxGroupMemberInt){
                Group selectedFriend = dataSource.get(position);
                boolean isChecked = selectedFriend.isChecked();
                if(isChecked){
                    viewHolder.groupItemCheckbox.setEnabled(true);
                    viewHolder.groupItemCheckbox.setChecked(false);
                    //SELECTED_ACCOUNT_MAP.remove(selectedFriend.getAccount());
                    removeSelectedAccount(selectedFriend.getGroupId());
                }else{
                    viewHolder.groupItemCheckbox.setEnabled(true);
                    viewHolder.groupItemCheckbox.setChecked(false);
                    //Start:add by wal@xdja.com for max group member
                    String maxGroupMemberStr = context.getString(R.string.exceed_max_member_count);
                    XToast.show(context, String.format(maxGroupMemberStr,maxGroupMemberInt));
                    //End:add by wal@xdja.com for max group member
                    return;
                }
            }else{
                Group selectedFriend = dataSource.get(position);
                final boolean isChecked = selectedFriend.isChecked();
                if(isChecked){
                    viewHolder.groupItemCheckbox.setEnabled(true);
                    viewHolder.groupItemCheckbox.setChecked(false);
                    //SELECTED_ACCOUNT_MAP.remove(selectedFriend.getAccount());
                    removeSelectedAccount(selectedFriend.getGroupId());
                }else{
                    viewHolder.groupItemCheckbox.setEnabled(true);
                    selectedFriend.setIsChecked(true);
                    putSelectedAccount(selectedFriend.getGroupId(),selectedFriend);
                    //SELECTED_ACCOUNT_MAP.put(selectedFriend.getAccount(),selectedFriend);
                }
            }
            selectedCallBack.callBackCount(calculateSelected());
            notifyDataSetChanged();
        }
    };
    /**
     * 搜索显示规则
     * @param group
     * @param holder
     */
    private void doSearch(Group group,ViewHolder holder){
        String nickName = group.getGroupName();
        String account = group.getGroupId();
        boolean isSetName=false;
        if(!ObjectUtil.stringIsEmpty(account)){
            if(!ObjectUtil.stringIsEmpty(searchKey)){
                account = account.toLowerCase();
                if(account.toLowerCase().contains(searchKey.toLowerCase())){
                    holder.groupName.setText(ContactShowUtil.getSpanned(account, searchKey, context));
                    isSetName=true;
                }
            }
        }



        if(!ObjectUtil.stringIsEmpty(nickName)){
            String nickNamePy = group.getNamePY();
            String nickNamePinYin = group.getNamePY();
            if(!ObjectUtil.stringIsEmpty(searchKey)){
                if(!ObjectUtil.stringIsEmpty(nickNamePy) && nickNamePy.toLowerCase().contains(searchKey.toLowerCase())
                        || !ObjectUtil.stringIsEmpty(nickNamePinYin) && nickNamePinYin.toLowerCase().contains(searchKey.toLowerCase())){
                    if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
                        holder.groupName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
                    }else {
                        holder.groupName.setText(nickName);
                    }
                    isSetName=true;
                }
                if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
                    holder.groupName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
                    isSetName=true;
                }

            }
        }
        if(!isSetName){
            holder.groupName.setText(group.getGroupName());
        }
    }



    private class ViewHolder{
        CircleImageView groupItemAvater;
        CheckBox groupItemCheckbox;
        TextView groupName;
        TextView contactNickname;
    }

    /**
     * 刷新数据
     * @param groups
     */
    public void setDataSource(List<Group> groups, String searchKey) {
        this.dataSource = groups;
        this.searchKey = searchKey;
        notifyDataSetChanged();
    }

    public List<Group> getDataSource() {
        return dataSource;
    }
}
