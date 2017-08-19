package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.contact.util.ContactUtils;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

/**
 * Created by xdjaxa on 2016/11/2.
 */
public class SearchGroupMemberAdapter extends BaseSelectAdapter {

    private List<UserInfo> dataSource;

    private String searchKey;

    private int OPEN_TYPE; //标注是进入批量删除还是进入全部群成员（可以长按条目删除）

    public SearchGroupMemberAdapter(Context context, ISelectCallBack callBack, List<UserInfo> existMember
            , List<UserInfo> list, int type) {
        super(context, callBack, null);
        dataSource = list;

        OPEN_TYPE = type;

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
        doSearch(dataSource.get(position), viewHolder);
        viewHolder.selCheckbox.setOnCheckedChangeListener(new CheckedListener(position, viewHolder));

        viewHolder.setAccount(dataSource.get(position).getAccount());
        GroupUtils.loadAvatarToImgView(viewHolder.memberAvator, dataSource.get(position).getAvatarBean().getThumbnail(), R.drawable.default_friend_icon);

        if(!ObjectUtil.objectIsEmpty(SELECTED_ACCOUNT_MAP.get(dataSource.get(position).getAccount()))){
            viewHolder.selCheckbox.setChecked(true);
        } else {
            viewHolder.selCheckbox.setChecked(false);
        }

        if(OPEN_TYPE == 1 || viewHolder.getAccount().equals(ContactUtils.getCurrentAccount())) {
            viewHolder.selCheckbox.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 搜索显示规则
     * @param userInfo
     * @param viewHolder
     */
    private void doSearch(UserInfo userInfo, ViewHolder viewHolder) {
        if(ObjectUtil.objectIsEmpty(userInfo) || ObjectUtil.objectIsEmpty(viewHolder)) {
            return;
        }
        String tmpStr = searchKey;
        searchKey = searchKey.toLowerCase();
        //viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getGroupMemberNickname(), searchKey, context));
        if(!TextUtils.isEmpty(userInfo.getGroupMemberNickname()) && userInfo.getGroupMemberNickname().toLowerCase().contains(searchKey)) {
            viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getGroupMemberNickname(), tmpStr, context));
        } else if(!TextUtils.isEmpty(userInfo.getRemark()) && userInfo.getRemark().toLowerCase().contains(searchKey)) {
            viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getRemark(), tmpStr, context));
        }else if(!TextUtils.isEmpty(userInfo.getAccountNickname()) && userInfo.getAccountNickname().toLowerCase().contains(searchKey)) {
            viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getAccountNickname(), tmpStr, context));
        } else if(!TextUtils.isEmpty(userInfo.getDepartmentMemberName()) && userInfo.getDepartmentMemberName().toLowerCase().contains(searchKey)) {
            viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getDepartmentMemberName(), tmpStr, context));
        } else if(!TextUtils.isEmpty(userInfo.getAlias()) && userInfo.getAlias().toLowerCase().contains(searchKey)){
            viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getAlias(), tmpStr, context));
        } else if (!TextUtils.isEmpty(userInfo.getAccount()) && userInfo.getAccount().toLowerCase().contains(searchKey)){
            viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getAccount(), tmpStr, context));
        } else {
            viewHolder.memberName.setText(ContactShowUtil.getSpanned(userInfo.getShowName(), tmpStr, context));
        }
        searchKey = tmpStr;
    }


    public  class ViewHolder {
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
        private ViewHolder viewHolder;

        private CheckedListener(int position, ViewHolder viewHolder) {
            this.position = position;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                putSelectedAccount(dataSource.get(position).getAccount(), dataSource.get(position));
            } else {
                removeSelectedAccount(dataSource.get(position).getAccount());
            }
            viewHolder.selCheckbox.setChecked(isChecked);
            selectedCallBack.callBackCount(SELECTED_ACCOUNT_MAP.size());
            notifyDataSetChanged();
        }
    }

    public void setDataSource(List<UserInfo> groupMembers, String key){
        this.dataSource = groupMembers;
        this.searchKey = key;
        notifyDataSetChanged();
    }

    public List<UserInfo> getDataSource() {
        return dataSource; //需要加判断条件
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
