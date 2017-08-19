package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;

import java.util.List;


/**
 * 搜索联系人结果listview
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161222.
 */
public class SearchFriendsAdapter extends BaseSelectAdapter {

	private List<Friend> dataSource;

    private LayoutInflater mInflater;

	private String searchKey;


	public SearchFriendsAdapter(Context context,ISelectCallBack callBack,List<String> existedMemberAccounts){
		super(context,callBack,existedMemberAccounts);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(ObjectUtil.collectionIsEmpty(dataSource))return 0;
		return dataSource.size();
	}

	@Override
	public Friend getItem(int position) {
		return dataSource.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.choose_contact_item, null);
			holder = new ViewHolder();
			holder.contactItemAvater = (CircleImageView)convertView.findViewById(R.id.contact_item_avater);
			holder.contactItemCheckbox = (CheckBox)convertView.findViewById(R.id.contact_item_checkbox);
			holder.contactName = (TextView)convertView.findViewById(R.id.contact_name);
			holder.contactNickname = (TextView)convertView.findViewById(R.id.contact_nickname);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// modified by ycm 2016/12/22:文件转发或分享时隐藏checkbox，禁止创建群聊[start]
		if (getCheckBoxStatus()) { // 显示checkbox
			holder.contactItemCheckbox.setOnClickListener(new CheckedListener(position, holder));
			Friend friend = dataSource.get(position);
			String account = friend.getAccount();
			if(!ObjectUtil.stringIsEmpty(EXISTE_ACCOUNT_MAP.get(account))){
				holder.contactItemCheckbox.setChecked(true);
				holder.contactItemCheckbox.setEnabled(false);
			}else{
				if(!ObjectUtil.objectIsEmpty(getSelected(account))){
					holder.contactItemCheckbox.setEnabled(true);
					holder.contactItemCheckbox.setChecked(true);
					friend.setIsChecked(true);
				}else {
					holder.contactItemCheckbox.setEnabled(true);
					holder.contactItemCheckbox.setChecked(false);
					friend.setIsChecked(false);
				}
			}
			doSearch(friend, holder);

			if(!ObjectUtil.objectIsEmpty(friend)){
				Avatar avatar = friend.getAvatar();
				if(avatar != null && !TextUtils.isEmpty(avatar.getThumbnail())){
					holder.contactItemAvater.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
				}else {
					holder.contactItemAvater.loadImage(R.drawable.img_avater_40, true);
				}
			}
		} else {//隐藏check，文件支持群发时删除此处
			Friend friend = dataSource.get(position);
			holder.contactItemCheckbox.setVisibility(View.GONE);
			doSearch(friend, holder);
			if(!ObjectUtil.objectIsEmpty(friend)){
				Avatar avatar = friend.getAvatar();
				if(avatar != null && !TextUtils.isEmpty(avatar.getThumbnail())){
					holder.contactItemAvater.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
				}else {
					holder.contactItemAvater.loadImage(R.drawable.img_avater_40, true);
				}
			}
		}
		// modified by ycm 2016/12/22:文件转发或分享时隐藏checkbox，禁止创建群聊[end]
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
			int maxGroupMemberInt=PreferenceUtils.getGroupMemberLimitConfiguration(); //add by wal@xdja.com for max group member
			if(calculateAccounts() >= maxGroupMemberInt){
				Friend selectedFriend = dataSource.get(position);
				boolean isChecked = selectedFriend.isChecked();
				if(isChecked){
					viewHolder.contactItemCheckbox.setEnabled(true);
					viewHolder.contactItemCheckbox.setChecked(false);
					//SELECTED_ACCOUNT_MAP.remove(selectedFriend.getAccount());
					removeSelectedAccount(selectedFriend.getAccount());
				}else{
					viewHolder.contactItemCheckbox.setEnabled(true);
					viewHolder.contactItemCheckbox.setChecked(false);
					//Start:add by wal@xdja.com for max group member
					String maxGroupMemberStr = context.getString(R.string.exceed_max_member_count);
					XToast.show(context, String.format(maxGroupMemberStr,maxGroupMemberInt));
					//End:add by wal@xdja.com for max group member
					return;
				}
			}else{
				Friend selectedFriend = dataSource.get(position);
				final boolean isChecked = selectedFriend.isChecked();
				if(isChecked){
					viewHolder.contactItemCheckbox.setEnabled(true);
					viewHolder.contactItemCheckbox.setChecked(false);
					//SELECTED_ACCOUNT_MAP.remove(selectedFriend.getAccount());
					removeSelectedAccount(selectedFriend.getAccount());
				}else{
					viewHolder.contactItemCheckbox.setEnabled(true);
					selectedFriend.setIsChecked(true);
					putSelectedAccount(selectedFriend.getAccount(),selectedFriend);
					//SELECTED_ACCOUNT_MAP.put(selectedFriend.getAccount(),selectedFriend);
				}
			}
			selectedCallBack.callBackCount(calculateSelected());
			notifyDataSetChanged();
		}
	};


	/**
	 * 搜索显示规则
	 * @param friend
	 * @param holder
	 */
	private void doSearch(Friend friend,ViewHolder holder){
		String remark = friend.getRemark();
		String nickName = friend.getActomaAccount().getNickname();
		String memberName = null;
		if(!ObjectUtil.objectIsEmpty(friend.getMember())){
			memberName = friend.getMember().getName();
		}
		String account = friend.getAccount();
		//modify by lwl start
		boolean isSetName=false;
		if(!ObjectUtil.stringIsEmpty(friend.getAlias())){
			if(!ObjectUtil.stringIsEmpty(searchKey)){
				String alias = friend.getAlias().toLowerCase();
				if(alias.contains(searchKey.toLowerCase())){
					holder.contactName.setText(ContactShowUtil.getSpanned(alias, searchKey, context));
					isSetName=true;
				}
			}
		}else if(!ObjectUtil.stringIsEmpty(account)){
			if(!ObjectUtil.stringIsEmpty(searchKey)){
				account = account.toLowerCase();
				if(account.toLowerCase().contains(searchKey.toLowerCase())){
					holder.contactName.setText(ContactShowUtil.getSpanned(account, searchKey, context));
					isSetName=true;
				}
			}
		}



		if(!ObjectUtil.stringIsEmpty(nickName)){
			String nickNamePy = friend.getActomaAccount().getNicknamePy();
			String nickNamePinYin = friend.getActomaAccount().getNicknamePinyin();
			if(!ObjectUtil.stringIsEmpty(searchKey)){
				if(!ObjectUtil.stringIsEmpty(nickNamePy) && nickNamePy.toLowerCase().contains(searchKey.toLowerCase())
						|| !ObjectUtil.stringIsEmpty(nickNamePinYin) && nickNamePinYin.toLowerCase().contains(searchKey.toLowerCase())){
					if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
						holder.contactName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
					}else {
						holder.contactName.setText(nickName);
					}
					isSetName=true;
				}
				if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
					holder.contactName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
					isSetName=true;
				}

			}
		}
		if(!ObjectUtil.stringIsEmpty(memberName)) {
			String namePy = friend.getMember().getNamePy();
			String namePinYin = friend.getMember().getNameFullPy();
			if (!ObjectUtil.stringIsEmpty(searchKey)) {
				if (!ObjectUtil.stringIsEmpty(namePy) && namePy.toLowerCase().contains(searchKey.toLowerCase())
						|| !ObjectUtil.stringIsEmpty(namePinYin) && namePinYin.toLowerCase().contains(searchKey.toLowerCase())) {
					if (memberName.toLowerCase().contains(searchKey.toLowerCase())) {
						holder.contactName.setText(ContactShowUtil.getSpanned(memberName, searchKey, context));
					}else {
						holder.contactName.setText(memberName);
					}
					isSetName=true;
				}
				if (memberName.toLowerCase().contains(searchKey.toLowerCase())) {
					holder.contactName.setText(ContactShowUtil.getSpanned(memberName, searchKey, context));
					isSetName=true;
				}

			}
		}


		if(!ObjectUtil.stringIsEmpty(remark)){
			String remarkPy = friend.getRemarkPy();
			String remarkPinYin = friend.getRemarkPinyin();
			if(!ObjectUtil.stringIsEmpty(searchKey)){
				if(!ObjectUtil.stringIsEmpty(remarkPy) && remarkPy.toLowerCase().contains(searchKey.toLowerCase())
						|| !ObjectUtil.stringIsEmpty(remarkPinYin) && remarkPinYin.toLowerCase().contains(searchKey.toLowerCase())){
					if(remark.toLowerCase().contains(searchKey.toLowerCase())){
						holder.contactName.setText(ContactShowUtil.getSpanned(remark, searchKey, context));
					}else {
						holder.contactName.setText(remark);
					}
					isSetName=true;
				}
				if(remark.toLowerCase().contains(searchKey.toLowerCase())){
					holder.contactName.setText(ContactShowUtil.getSpanned(remark, searchKey, context));
					isSetName=true;
				}
			}
		}
		if(!isSetName){
			holder.contactName.setText(friend.showName());
		}
		//modify by lwl end
	}



	private class ViewHolder{
		CircleImageView contactItemAvater;
		CheckBox contactItemCheckbox;
		TextView contactName;
		TextView contactNickname;
	}

	/**
	 * 刷新数据
	 * @param friends
	 */
	public void setDataSource(List<Friend> friends, String searchKey) {
		this.dataSource = friends;
        this.searchKey = searchKey;
		notifyDataSetChanged();
	}
	// modified by ycm 2016/12/22:[文件转发或分享]
	public List<Friend> getDataSource() {
		return dataSource;
	}
	// modified by ycm 2016/12/22:[文件转发或分享]
}
