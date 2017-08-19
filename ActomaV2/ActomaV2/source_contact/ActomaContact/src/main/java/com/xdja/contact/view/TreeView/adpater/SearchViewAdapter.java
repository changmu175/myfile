package com.xdja.contact.view.TreeView.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.contact.R;
import com.xdja.contact.bean.ActomaAccount;
import com.xdja.contact.bean.Member;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.contact.presenter.adapter.BaseSelectAdapter;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;

import java.util.List;


/**
 * 搜索联系人结果listview
 */
public class SearchViewAdapter extends BaseSelectAdapter {

	private List<Member> dataSource;

    private LayoutInflater mInflater;

	private String searchKey;

	public SearchViewAdapter(Context context,ISelectCallBack callBack,List<String> existedMemberAccounts){
		super(context,callBack,existedMemberAccounts);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(ObjectUtil.collectionIsEmpty(dataSource))return 0;
		return dataSource.size();
	}

	@Override
	public Member getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.tree_view_item, null);
			holder = new ViewHolder();
			holder.showName = (TextView) convertView.findViewById(R.id.node_value);
			holder.avatarImageView = (CircleImageView) convertView.findViewById(R.id.round_img);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.node_checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，禁止创建群聊[start]
		if (getCheckBoxStatus()) {// 显示checkbox
			holder.checkBox.setOnClickListener(new CheckedListener(position, holder));
			Member member = dataSource.get(position);
			final String account = member.getAccount();
			if (!ObjectUtil.mapIsEmpty(EXISTE_ACCOUNT_MAP) && !ObjectUtil.stringIsEmpty(EXISTE_ACCOUNT_MAP.get(account))) {
				holder.checkBox.setEnabled(false);
				holder.checkBox.setChecked(true);
			} else {
				if (!ObjectUtil.objectIsEmpty(getSelected(account))) {
					holder.checkBox.setEnabled(true);
					holder.checkBox.setChecked(true);
					member.setIsChecked(true);
				} else {
					holder.checkBox.setEnabled(true);
					holder.checkBox.setChecked(false);
					member.setIsChecked(false);
				}
			}

		//End:add by wal@xdja.com for 3585
//        String name = member.getName();
//		String nickName = null;
//		if(!ObjectUtil.objectIsEmpty(member.getActomaAccount())){
//			 nickName = member.getActomaAccount().getNickname();
//		}
//
//		if(!ObjectUtil.stringIsEmpty(account)){
//			if(!ObjectUtil.stringIsEmpty(searchKey)) {
//				if (account.toLowerCase().contains(searchKey.toLowerCase())) {
//					holder.showName.setText(ContactShowUtil.getSpanned(account, searchKey, context));
//				}
//			}
//		}
//
//		if(!ObjectUtil.stringIsEmpty(nickName)){
//			String nickNamePy = member.getActomaAccount().getNicknamePy();
//			String nickNamePinYin = member.getActomaAccount().getNicknamePinyin();
//			if(!ObjectUtil.stringIsEmpty(searchKey)){
//				if(!ObjectUtil.stringIsEmpty(nickNamePy) && nickNamePy.toLowerCase().contains(searchKey.toLowerCase())
//						|| !ObjectUtil.stringIsEmpty(nickNamePinYin) && nickNamePinYin.toLowerCase().contains(searchKey.toLowerCase())){
//					if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
//						holder.showName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
//					}else {
//						holder.showName.setText(nickName);
//					}
//				}
//				if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
//					holder.showName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
//				}
//			}
//		}
//
//		if(!ObjectUtil.stringIsEmpty(name)){
//			String namePy = member.getNamePy();
//			String namePinYin = member.getNameFullPy();
//			if(!TextUtils.isEmpty(searchKey)){
//				if(!ObjectUtil.stringIsEmpty(namePy) && namePy.toLowerCase().contains(searchKey.toLowerCase())
//						|| !ObjectUtil.stringIsEmpty(namePinYin) && namePinYin.toLowerCase().contains(searchKey.toLowerCase())){
//					if(name.toLowerCase().contains(searchKey.toLowerCase())){
//						holder.showName.setText(ContactShowUtil.getSpanned(name, searchKey, context));
//					}else {
//						holder.showName.setText(name);
//					}
//				}
//				if(name.toLowerCase().contains(searchKey.toLowerCase())){
//					holder.showName.setText(ContactShowUtil.getSpanned(name, searchKey, context));
//				}
//			}
//		}
		doSearch(member,holder);
		//End:add by wal@xdja.com for 3585

			if (member.getAvatarInfo() != null) {
				holder.avatarImageView.loadImage(member.getAvatarInfo().getThumbnail(), true, R.drawable.img_avater_40);
			}
		} else {// 隐藏checkbox，文件支持群发时删除此处
			holder.checkBox.setVisibility(View.GONE);
			Member member = dataSource.get(position);
			doSearch(member, holder);
			if (member.getAvatarInfo() != null) {
				holder.avatarImageView.loadImage(member.getAvatarInfo().getThumbnail(), true, R.drawable.img_avater_40);
			}
		}
		// modified by ycm 2016/12/22:[文件转发或分享]隐藏checkbox，禁止创建群聊[end]
		return convertView;
	}

	//Start:add by wal@xdja.com for 3585
	private void doSearch(Member member,ViewHolder holder){
		if (!ObjectUtil.objectIsEmpty(member)){
			ActomaAccount actomaAccount=null;
			if(!ObjectUtil.objectIsEmpty(member.getActomaAccount())){
				actomaAccount=member.getActomaAccount();
			}
			boolean isSetName=false;
			if(!ObjectUtil.objectIsEmpty(actomaAccount)&&!ObjectUtil.stringIsEmpty(actomaAccount.getAlias())){
				if(!ObjectUtil.stringIsEmpty(searchKey)){
					String alias = actomaAccount.getAlias().toLowerCase();
					if(alias.contains(searchKey.toLowerCase())){
						holder.showName.setText(ContactShowUtil.getSpanned(alias, searchKey, context));
						isSetName=true;
					}
				}
			}else if(!ObjectUtil.objectIsEmpty(actomaAccount)&&!ObjectUtil.stringIsEmpty(actomaAccount.getAccount())){
				if(!ObjectUtil.stringIsEmpty(searchKey)){
					String account = actomaAccount.getAccount().toLowerCase();
					if(account.toLowerCase().contains(searchKey.toLowerCase())){
						holder.showName.setText(ContactShowUtil.getSpanned(account, searchKey, context));
						isSetName=true;
					}
				}
			}
			if(!ObjectUtil.objectIsEmpty(actomaAccount)&&!ObjectUtil.stringIsEmpty(actomaAccount.getNickname())){
				String nickName = actomaAccount.getNickname();
				String nickNamePy = actomaAccount.getNicknamePy();
				String nickNamePinYin = actomaAccount.getNicknamePinyin();
				if(!ObjectUtil.stringIsEmpty(searchKey)){
					if(!ObjectUtil.stringIsEmpty(nickNamePy) && nickNamePy.toLowerCase().contains(searchKey.toLowerCase())
							|| !ObjectUtil.stringIsEmpty(nickNamePinYin) && nickNamePinYin.toLowerCase().contains(searchKey.toLowerCase())){
						if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
							holder.showName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
						}else {
							holder.showName.setText(nickName);
						}
						isSetName=true;
					}
					if(nickName.toLowerCase().contains(searchKey.toLowerCase())){
						holder.showName.setText(ContactShowUtil.getSpanned(nickName, searchKey, context));
						isSetName=true;
					}
				}
			}
			String memberName = member.getName();
			String memberNamePy = member.getNamePy();
			String memberNamePinYin = member.getNameFullPy();
			if (!ObjectUtil.stringIsEmpty(searchKey)) {
				if (!ObjectUtil.stringIsEmpty(memberNamePy) && memberNamePy.toLowerCase().contains(searchKey.toLowerCase())
						|| !ObjectUtil.stringIsEmpty(memberNamePinYin) && memberNamePinYin.toLowerCase().contains(searchKey.toLowerCase())) {
					if (memberName.toLowerCase().contains(searchKey.toLowerCase())) {
						holder.showName.setText(ContactShowUtil.getSpanned(memberName, searchKey, context));
					}else {
						holder.showName.setText(memberName);
					}
					isSetName=true;
				}
				if (memberName.toLowerCase().contains(searchKey.toLowerCase())) {
					holder.showName.setText(ContactShowUtil.getSpanned(memberName, searchKey, context));
					isSetName=true;
				}

			}
			if(!isSetName){
				holder.showName.setText(memberName);
			}
		}
	}
	//End:add by wal@xdja.com for 3585

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
			if(calculateAccounts()>= maxGroupMemberInt){
				Member clickedMember = dataSource.get(position);
				boolean isChecked = clickedMember.isChecked();
				if(isChecked){
					viewHolder.checkBox.setEnabled(true);
					viewHolder.checkBox.setChecked(false);
					//SELECTED_ACCOUNT_MAP.remove(clickedMember.getAccount());
					removeSelectedAccount(clickedMember.getAccount());
				}else{
					viewHolder.checkBox.setEnabled(true);
					viewHolder.checkBox.setChecked(false);
					//Start:add by wal@xdja.com for max group member
					String maxGroupMemberStr = context.getString(R.string.exceed_max_member_count);
					XToast.show(context, String.format(maxGroupMemberStr,maxGroupMemberInt));
					//End:add by wal@xdja.com for max group member
					return;
				}
			}else{
				Member clickedMember = dataSource.get(position);
				final boolean isChecked = clickedMember.isChecked();
				if(isChecked){
					viewHolder.checkBox.setEnabled(true);
					viewHolder.checkBox.setChecked(false);
					//SELECTED_ACCOUNT_MAP.remove(clickedMember.getAccount());
					removeSelectedAccount(clickedMember.getAccount());
				}else{
					viewHolder.checkBox.setEnabled(true);
					clickedMember.setIsChecked(true);
					//SELECTED_ACCOUNT_MAP.put(clickedMember.getAccount(),clickedMember);
					putSelectedAccount(clickedMember.getAccount(),clickedMember);
				}
			}
			selectedCallBack.callBackCount(calculateSelected());
			notifyDataSetChanged();
		}
	};


	private class ViewHolder{
		TextView showName;
        CircleImageView avatarImageView;
		CheckBox checkBox;
	}
	
	/**
	 * 刷新数据
	 * @param searchContactBeans
	 */
	public void setDataSource(List<Member> searchContactBeans, String searchKey) {
		this.dataSource = searchContactBeans;
        this.searchKey = searchKey;
		notifyDataSetChanged();
	}
	// modified by ycm 2016/12/22:[文件转发或分享][start]
	public List<Member> getDataSource() {
		return dataSource;
	}
	// modified by ycm 2016/12/22:[文件转发或分享][end]
}
