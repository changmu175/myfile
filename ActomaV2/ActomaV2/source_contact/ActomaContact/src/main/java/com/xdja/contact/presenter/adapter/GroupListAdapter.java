package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.contact.R;
import com.xdja.contact.bean.Group;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

/**
 * Created by XDJA_XA on 2015/7/17.
 */
public class GroupListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Group> mGroupList;

    public GroupListAdapter(Context context) {
        mContext = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_list_item, null);
            holder = new ViewHolder();
            holder.mAvatar = (CircleImageView)convertView.findViewById(R.id.avatar);
            holder.mGroupName = (TextView)convertView.findViewById(R.id.group_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Group group = mGroupList.get(position);
        GroupUtils.loadAvatarToImgView(holder.mAvatar, group, R.drawable.group_avatar_40);
        holder.mGroupName.setText(group.getDisplayName(mContext));
        return convertView;
    }

    private class ViewHolder {
        public CircleImageView mAvatar;
        public TextView mGroupName;
    }
}
