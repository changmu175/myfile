package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.contact.R;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.util.GroupUtils;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择好友列表
 * @author hkb.
 * @since 2015/7/22/0022.
 * modify wanghao 2016-02-26
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)modify for share and forward function by ycm at 20161103.
 * 3)modify for share and forward file function by ycm at 20161222.
 */
public class  ChooseFriendAdapter extends BaseSelectAdapter implements SectionIndexer {

    private List<Friend> dataSource;

    private Map<String,Integer> alphaIndexer;

    private String preCode;

    private String[] sections;

    public ChooseFriendAdapter(Context context,ISelectCallBack callBack,List<String> existedMemberAccounts){
        super(context,callBack,existedMemberAccounts);
    }

    @Override
    public int getCount() {
        if(ObjectUtil.collectionIsEmpty(dataSource))return 0;
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSource.get(position).getViewType();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        IndexViewHolder indexViewHolder;
        int viewType = getItemViewType(position);
        if (viewType == Friend.CONTACT_ITEM) {
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.choose_contact_item,null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，禁止创建群聊[start]
            if (getCheckBoxStatus()) { // 显示checkbox
                holder.checkBox.setOnClickListener(new CheckedListener(position, holder));
                Friend selectedFriend = dataSource.get(position);
                String selectedAccount = selectedFriend.getAccount();
                if (!ObjectUtil.stringIsEmpty(EXISTE_ACCOUNT_MAP.get(selectedAccount))) {
                    holder.checkBox.setChecked(true);
                    holder.checkBox.setEnabled(false);
                } else {
                    if(!ObjectUtil.objectIsEmpty(getSelected(selectedAccount))){
                        holder.checkBox.setChecked(true);
                        holder.checkBox.setEnabled(true);
                        selectedFriend.setIsChecked(true);
                    }else{
                        holder.checkBox.setChecked(false);
                        holder.checkBox.setEnabled(true);
                        selectedFriend.setIsChecked(false);
                    }
                }
                holder.refresh(selectedFriend);
                //头像解析
                GroupUtils.loadAvatarToImgView(holder.avatar, selectedFriend, R.drawable.default_friend_icon);
            } else { //隐藏checkbox，文件支持群发时删除此处
                Friend selectedFriend = dataSource.get(position);
                String selectedAccount = selectedFriend.getAccount();
                holder.refresh(selectedFriend);
                //头像解析
                GroupUtils.loadAvatarToImgView(holder.avatar, selectedFriend, R.drawable.default_friend_icon);
                holder.checkBox.setVisibility(View.GONE);
            }
            // modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，禁止创建群聊[end]
        }else if(viewType == Friend.ALPHA){
            indexViewHolder = new IndexViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.friend_list_index_layout, null);
                indexViewHolder.indexTextView = (TextView) convertView.findViewById(R.id.alpha);
                convertView.setTag(indexViewHolder);
            } else {
                indexViewHolder = (IndexViewHolder) convertView.getTag();
            }
            Friend contactBean = dataSource.get(position);
            indexViewHolder.indexTextView.setText(contactBean.getIndexChar());
        }
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
		    // Task 2632 [Begin]
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
                Friend selectedFriend = dataSource.get(position);
                boolean isChecked = selectedFriend.isChecked();
                if(isChecked){
                    viewHolder.checkBox.setEnabled(true);
                    viewHolder.checkBox.setChecked(false);
                    removeSelectedAccount(selectedFriend.getAccount());
                }else{
                    viewHolder.checkBox.setEnabled(true);
                    viewHolder.checkBox.setChecked(false);
//                    //Start:add by wal@xdja.com for max group member
//                    String maxGroupMemberStr = context.getString(R.string.exceed_max_member_count);
                    XToast.show(context, String.format(tip,limitCount));
//                    //End:add by wal@xdja.com for max group member
//                    XToast.show(context, tip);
					//modified by ycm 2016/11/04 [end]
                    return;
                }
            // Task 2632 [End]
            }else{
                Friend selectedFriend = dataSource.get(position);
                boolean isChecked = selectedFriend.isChecked();
                if(isChecked){
                    viewHolder.checkBox.setEnabled(true);
                    viewHolder.checkBox.setChecked(false);
                    //SELECTED_ACCOUNT_MAP.remove(selectedFriend.getAccount());
                    removeSelectedAccount(selectedFriend.getAccount());
                }else{
                    viewHolder.checkBox.setEnabled(true);
                    selectedFriend.setIsChecked(true);
                    //SELECTED_ACCOUNT_MAP.put(selectedFriend.getAccount(),selectedFriend);
                    putSelectedAccount(selectedFriend.getAccount(),selectedFriend);
                }
            }
            selectedCallBack.callBackCount(calculateSelected());
            notifyDataSetChanged();
        }
    };


    @Override
    public Object[] getSections() {
        return new Object[0];
    }


    public int getPositionForString(String s){
        if (TextUtils.isEmpty(s)) {
            return -1;
        }
        if (s.equals("#")) {
            return 0;
        }

        if (!ObjectUtil.mapIsEmpty(alphaIndexer) && alphaIndexer.containsKey(s)) {
            return alphaIndexer.get(s);
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
    @Override
    public int getPositionForSection(int section) {
        String later = section - 2 >= 0
                ? sections[section - 2]
                : sections[section];
        return alphaIndexer.get(later);
    }


    static class IndexViewHolder {
        TextView indexTextView;
    }

    private class ViewHolder{
        private CircleImageView avatar;
        private TextView name;
        private CheckBox checkBox;

        public ViewHolder(View view){
            checkBox = (CheckBox) view.findViewById(R.id.contact_item_checkbox);
            name = (TextView) view.findViewById(R.id.contact_name);
            avatar = (CircleImageView) view.findViewById(R.id.contact_item_avater);

        }
        public void refresh(Friend friend){
            name.setText(friend.showName());
        }
    }

    public void setDataSource(List<Friend> friends){
        int length = friends.size();
        this.dataSource = friends;
        this.sections = new String[length];
        if(alphaIndexer==null){
            alphaIndexer = new HashMap<>();
        }
        if(!alphaIndexer.isEmpty()){
            alphaIndexer.clear();
        }
        for (int i = 0; i < length; i++) {
            Friend tmp = friends.get(i);
            String name = tmp.getIndexChar();
            if (TextUtils.isEmpty(preCode) || !preCode.equals(name)){
                preCode = name;
                alphaIndexer.put(name, i);// #,A,B,C,D,F,G,Z
            }
            // A,F,Z
            sections[i] = name;
        }
        notifyDataSetChanged();
    }

    // modified by ycm 2016/12/22:[文件转发或分享]获取联系人数据源[start]
    public List<Friend> getDataSource() {
        return dataSource;
    }
    // modified by ycm 2016/12/22:[文件转发或分享]获取联系人数据源[end]
}


