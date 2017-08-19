package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Member;
import com.xdja.contact.callback.group.ISelectCallBack;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.util.PreferenceUtils;
import com.xdja.comm.uitl.RegisterActionUtil;
import com.xdja.contact.view.TreeView.bean.TreeNode;

import java.util.List;


/**
 * Created by hkb on 2015/6/15.
 *
 * wanghao 2016-03-08 修改
 * 修改备注：
 * 1)Bug 5681, modify for share and forward function by ycm at 20161103.
 * 2)modify for share and forward function by ycm at 20161222.
 */
public class TreeListViewAdapter extends BaseSelectAdapter {

    private List<TreeNode> dataSource;


    public TreeListViewAdapter(Context context, ISelectCallBack callBack,List<String> existedMemberAccounts) {
        super(context,callBack,existedMemberAccounts);
    }

    public int getCount() {
        if(ObjectUtil.collectionIsEmpty(dataSource))return 0;
        return dataSource.size();
    }

    public Object getItem(int position) {
        return dataSource.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tree_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkbox.setOnClickListener(new CheckedListener(position, holder));
        final TreeNode node = dataSource.get(position);
        holder.refresh(node);
        return convertView;
    }

    private class ViewHolder {
        private ImageView expandImage; // 左侧图标
        private CircleImageView avatarImage; // 头像图标
        private TextView itemText;//树节点名称
        private CheckBox checkbox;
        private View convertView;


        private ViewHolder(View convertView) {
            this.convertView = convertView.findViewById(R.id.tree_item_layout);
            this.expandImage = (ImageView) convertView.findViewById(R.id.expand_img);
            this.avatarImage = (CircleImageView) convertView.findViewById(R.id.round_img);
            this.itemText = (TextView) convertView.findViewById(R.id.node_value);
            this.checkbox = (CheckBox) convertView.findViewById(R.id.node_checkbox);
        }

        public void refresh(final TreeNode node) {
            //根据级别设置缩进
            int level = node.getLevel();
            convertView.setPadding(16 * level, 20, 0, 20);
            itemText.setText(node.getName());
            //设置左侧展开图片
            if (!node.isLeaf()) {
                checkBoxControler(false, View.GONE);
                avatarImage.setVisibility(View.GONE);
                expandImage.setVisibility(View.VISIBLE);
            } else {
                expandImage.setVisibility(View.GONE);
                avatarImage.setVisibility(View.VISIBLE);
                //如果之前已经是成员，则默认选中，且不能点击
                final String account = ((Member) node.getSource()).getAccount();
                // modified by ycm 2016/12/22:文件转发或分享时隐藏checkbox，禁止创建群聊[start]
                if (getCheckBoxStatus()) {// 显示checkbox
                    checkBoxControler(node.isChecked(), View.VISIBLE);
                    if(!ObjectUtil.mapIsEmpty(EXISTE_ACCOUNT_MAP) && !ObjectUtil.stringIsEmpty(EXISTE_ACCOUNT_MAP.get(account))){
                        checkbox.setEnabled(false);
                        checkbox.setChecked(true);
                    }else{
                        if(!ObjectUtil.objectIsEmpty(getSelected(account))){
                            checkbox.setEnabled(true);
                            checkbox.setChecked(true);
                            node.setIsChecked(true);
                        }else {
                            checkbox.setEnabled(true);
                            checkbox.setChecked(false);
                            node.setIsChecked(false);
                        }
                    }
                } else {// 隐藏checkbox，文件支持群发时删除此处
                    checkBoxControler(false, View.GONE);
                }
                // modified by ycm 2016/12/22:文件转发或分享时隐藏checkbox，禁止创建群聊[end]
            }
            //根据是否是子目录来构建item
            buildSubdiretory(node);
        }
        /**
         * 根据是否是子目录来构建item
         * @param node
         */
        public void buildSubdiretory(TreeNode node){
            if (!node.isLeaf()) {
                itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                setItemHeight(R.dimen.department_item_height);
                switch (node.getLevel()) {
                    case 0:
                        convertView.setBackgroundResource(R.color.base_gray_ed);
                        break;
                    //[s]modify by xienana for bug 5851 review by tangsha
                    default:
                        convertView.setBackgroundResource(R.color.base_gray_f7);
                        break;
                    //[e]modify by xienana for bug 5851 review by tangsha
                }
                if (expandImage.getVisibility() == View.VISIBLE) {
                    expandImage.setBackgroundResource(node.isExpand() ? R.drawable.img_listitem_expand : R.drawable.img_listitem_unexpand);
                }
            } else {
                if (node.getSource() instanceof Member) {
                    Member member = (Member) node.getSource();
                    if(member != null){
                        setItemHeight(R.dimen.member_item_height);
                        itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        //[S]modify by xienan for bug 4275 20161024 review by self
                        //convertView.setBackgroundColor(context.getResources().getColor(R.color.base_white_100));
                        convertView.setBackgroundResource(R.drawable.selector_contact_list_item);
                        //[E]modify by xienan for bug 4275 20161024 review by self
                        Avatar avatarInfo = member.getAvatarInfo();
                        if (avatarInfo != null && !TextUtils.isEmpty(avatarInfo.getThumbnail())) {
                            avatarImage.loadImage(avatarInfo.getThumbnail(),true,R.drawable.img_avater_40);

                        }else {
                            avatarImage.loadImage(R.drawable.img_avater_40, true);
                        }
                    }
                }
            }
        }

        public void checkBoxControler(boolean isChecked,int visible) {
                checkbox.setVisibility(visible);
                checkbox.setChecked(isChecked);
        }

        /**
         * 根据级别设置不同item的高度
         * @param dimenID
         */
        private void setItemHeight(int dimenID) {
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            layoutParams.height = context.getResources().getDimensionPixelOffset(dimenID);
            convertView.setLayoutParams(layoutParams);
        }
    }


    private class CheckedListener implements View.OnClickListener {

        private int position;

        private ViewHolder viewHolder;

        private  CheckedListener(int position,ViewHolder viewHolder){
            this.position = position;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            //Bug 56841 [start]
            int limitCount;
            String tip;
            int maxGroupMemberInt= PreferenceUtils.getGroupMemberLimitConfiguration(); //add by wal@xdja.com for max group member
            if (RegisterActionUtil.SHARE_FOR_MORECONTACT.equals(getShareMark())) {
                limitCount = 9;
                tip = context.getString(R.string.share_max_member);
            } else {
                limitCount = maxGroupMemberInt;
                tip = context.getString(R.string.exceed_max_member_count);
            }

            if(calculateAccounts()>= limitCount){
                TreeNode treeNode = dataSource.get(position);
                boolean isChecked = treeNode.isChecked();
                if(isChecked){
                    viewHolder.checkbox.setEnabled(true);
                    viewHolder.checkbox.setChecked(false);
                    String account = ((Member)treeNode.getSource()).getAccount();
                    //SELECTED_ACCOUNT_MAP.remove(account);
                    removeSelectedAccount(account);
                }else{
                    viewHolder.checkbox.setEnabled(true);
                    viewHolder.checkbox.setChecked(false);
                    //Start:add by wal@xdja.com for max group member
//                    String maxGroupMemberStr = context.getString(R.string.exceed_max_member_count);
                    XToast.show(context, String.format(tip,limitCount));
                    //Bug 56841 [end]
                    //End:add by wal@xdja.com for max group member
                    return;
                }

            }else{
                TreeNode treeNode = dataSource.get(position);
                final boolean isChecked = treeNode.isChecked();
                if(isChecked){
                    viewHolder.checkbox.setEnabled(true);
                    viewHolder.checkbox.setChecked(false);
                    String account = ((Member)treeNode.getSource()).getAccount();
                    //SELECTED_ACCOUNT_MAP.remove(account);
                    removeSelectedAccount(account);
                }else{
                    viewHolder.checkbox.setEnabled(true);
                    treeNode.setIsChecked(true);
                    String account = ((Member)treeNode.getSource()).getAccount();
                    //SELECTED_ACCOUNT_MAP.put(account,treeNode);
                    putSelectedAccount(account,treeNode);
                }
            }
            selectedCallBack.callBackCount(calculateSelected());
            notifyDataSetChanged();
        }
    };



    public void setDataSource(List<TreeNode> dataSource){
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }
	// modified by ycm 2016/12/22:[文件转发或分享][start]
    public List<TreeNode> getDataSource() {
        return dataSource;
    }
	// modified by ycm 2016/12/22:[文件转发或分享][end]
}
