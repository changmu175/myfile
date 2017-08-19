package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.contact.R;
import com.xdja.contact.bean.Group;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.List;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，更多联系人中，选择群界面的adapter
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/3 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Bug 5681, modify for share and forward function by ycm at 20161103.
 */
public class ChooseGroupAdapter extends BaseSelectAdapter implements SectionIndexer {

    private List<Group> mGroupList;

    public ChooseGroupAdapter(Context context, ISelectCallBack callBack, List<String> existedMemberAccounts){
        super(context,callBack,existedMemberAccounts);
    }

    public void setDataSource(List<Group> mGroupList){
        this.mGroupList = mGroupList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(ObjectUtil.collectionIsEmpty(mGroupList)){
            return 0;
        }
        return mGroupList.size();
    }

    @Override
    public Object getItem(int position) {
        if (getCount() <= 0) {
            return null;
        }
        return mGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.share_group_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.checkBox.setOnClickListener(new CheckedListener(position, holder));
        Group selectGroup = mGroupList.get(position);
        String selectedAccount = selectGroup.getGroupId();
        if (!ObjectUtil.stringIsEmpty(EXISTE_ACCOUNT_MAP.get(selectedAccount))) {
            holder.checkBox.setChecked(true);
            holder.checkBox.setEnabled(false);
        } else {
            if(!ObjectUtil.objectIsEmpty(getSelected(selectedAccount))){
                holder.checkBox.setChecked(true);
                holder.checkBox.setEnabled(true);
                selectGroup.setIsChecked(true);
            }else{
                holder.checkBox.setChecked(false);
                holder.checkBox.setEnabled(true);
                selectGroup.setIsChecked(false);
            }
        }
        holder.refresh(selectGroup);
        GroupUtils.loadAvatarToImgView(holder.mAvatar, selectGroup, R.drawable.group_avatar_40);
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
            int limitCount;
            String tip;
			//modified by ycm 2016/11/04 [start]
            int maxGroupMemberInt= PreferenceUtils.getGroupMemberLimitConfiguration(); //add by wal@xdja.com for max group member
            if (RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(getShareMark())) {
                limitCount = 9;
                tip = context.getString(R.string.share_max_member);
            } else {
                limitCount = maxGroupMemberInt;
                tip = context.getString(R.string.exceed_max_member_count);
            }
            if(calculateAccounts()>= limitCount){
                Group selectedGroup = mGroupList.get(position);
                boolean isChecked = selectedGroup.isChecked();
                if(isChecked){
                    viewHolder.checkBox.setEnabled(true);
                    viewHolder.checkBox.setChecked(false);
                    removeSelectedAccount(selectedGroup.getGroupId());
                }else{
                    viewHolder.checkBox.setEnabled(true);
                    viewHolder.checkBox.setChecked(false);
                    XToast.show(context, String.format(tip,limitCount));
					//modified by ycm 2016/11/04 [end]
                    return;
                }
            }else{
                Group selectedGroup = mGroupList.get(position);
                boolean isChecked = selectedGroup.isChecked();
                if(isChecked){
                    viewHolder.checkBox.setEnabled(true);
                    viewHolder.checkBox.setChecked(false);
                    removeSelectedAccount(selectedGroup.getGroupId());
                }else{
                    viewHolder.checkBox.setEnabled(true);
                    selectedGroup.setIsChecked(true);
                    putSelectedAccount(selectedGroup.getGroupId(),selectedGroup);
                }
            }
            selectedCallBack.callBackCount(calculateSelected());
            notifyDataSetChanged();
        }
    };


    private class ViewHolder {
        public CircleImageView mAvatar;
        public TextView mGroupName;
        private CheckBox checkBox;
        public ViewHolder(View view){
            checkBox = (CheckBox) view.findViewById(R.id.contact_item_checkbox);
            mGroupName = (TextView) view.findViewById(R.id.group_name);
            mAvatar = (CircleImageView) view.findViewById(R.id.avatar);

        }
        public void refresh(Group group){
            mGroupName.setText(group.getDisplayName(context));
        }
    }

}
