package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.contact.R;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.usereditor.bean.UserInfo;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

/**
 * Created by xdjaxa on 2016/11/2.
 */
public class GroupDelMulMemberAdapter extends BaseSelectAdapter {

    private List<UserInfo> dataSource;

    private int open_Type;  //如果值为0进入批量删除页面，并显示复选框.

    public GroupDelMulMemberAdapter(Context context, ISelectCallBack callBack
            , List<UserInfo> existMember, List<UserInfo> list, int type) {
        super(context, callBack, null);
        dataSource = list;
        open_Type = type;

    }


    @Override
    public int getCount() {
        if(ObjectUtil.collectionIsEmpty(dataSource))
            return 0;
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.choose_contact_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.selCheckbox.setOnCheckedChangeListener(new CheckedListener(position));

        viewHolder.memberName.setText(dataSource.get(position).getShowName());
        if(!ObjectUtil.objectIsEmpty(getSelected(dataSource.get(position).getAccount()))) {
            viewHolder.selCheckbox.setChecked(true);
        } else {
            viewHolder.selCheckbox.setChecked(false);
        }

        if(position == 0) {
            viewHolder.selCheckbox.setVisibility(View.GONE);
        } else {
            viewHolder.selCheckbox.setVisibility(View.VISIBLE);
        }

        if(open_Type == 1) {
            viewHolder.selCheckbox.setVisibility(View.GONE);
        }

        viewHolder.setAccount(dataSource.get(position).getAccount());

        GroupUtils.loadAvatarToImgView(viewHolder.memberAvator, dataSource.get(position).getAvatarBean().getThumbnail(), R.drawable.default_friend_icon);

        return convertView;
    }


    public class ViewHolder {
        private CircleImageView memberAvator;
        private TextView memberName;
        private CheckBox selCheckbox;
        private String account;
        public ViewHolder(View view) {
            memberAvator = (CircleImageView)view.findViewById(R.id.contact_item_avater);
            memberName = (TextView)view.findViewById(R.id.contact_name);
            selCheckbox = ((CheckBox) view.findViewById(R.id.contact_item_checkbox));
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }
    }

    private class CheckedListener implements CompoundButton.OnCheckedChangeListener {

        private int position;

        private CheckedListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                putSelectedAccount(dataSource.get(position).getAccount(), dataSource.get(position));
            } else {
                removeSelectedAccount(dataSource.get(position).getAccount());
            }
            selectedCallBack.callBackCount(SELECTED_ACCOUNT_MAP.size());
        }

    }

    public List<UserInfo> getDataSource() {
        return dataSource;
    }

    public void deleteData(String account) {
        for (UserInfo userInfo : dataSource) {
            if(account.equals(userInfo.getAccount())) {
                dataSource.remove(userInfo);
                break;
            }
        }
        notifyDataSetChanged();
    }

}
