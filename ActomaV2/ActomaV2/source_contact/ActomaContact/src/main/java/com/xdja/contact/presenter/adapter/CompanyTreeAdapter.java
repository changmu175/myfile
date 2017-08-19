package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.data.HeadImgParamsBean;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.comm.uitl.StateParams;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.EncryptRecord;
import com.xdja.contact.bean.Member;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.view.TreeView.bean.TreeNode;

import java.util.List;

import butterknife.ButterKnife;


/**
 * 集团通讯录树形图
 *
 * @author hkb
 * @since 2015年8月22日14:16:10
 */
public class CompanyTreeAdapter extends ArcBaseAdapter {

    private Context context;
    private List<TreeNode> nodes;
    private EncryptRecord encryptRecord;
    public String SelectAccount=null;

    //[S] add by LiXiaolong on20160824. fix bug 3303. review by WangChao1.
    private String pkgName;
    //[E] add by LiXiaolong on20160824. fix bug 3303. review by WangChao1.

    public CompanyTreeAdapter(Context context, List<TreeNode> nodes) {
        this.context = context;
        this.nodes = nodes;
        EncryptRecordService encryptRecordService = new EncryptRecordService(context);
        this.encryptRecord = encryptRecordService.lastSelectedRecord();
    }

    //[S] add by LiXiaolong on20160824. fix bug 3303. review by WangChao1.
    public  void setAppPackageName(String pkgName){
        this.pkgName = pkgName;
    }
    //[E] add by LiXiaolong on20160824. fix bug 3303. review by WangChao1.

    public int getCount() {
        if (nodes == null || nodes.size() <= 0) {
            return 0;
        }
        return nodes.size();
    }

    public Object getItem(int position) {
        return nodes.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tree_company_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //modify by lwl 1721 start
        if (!ListUtils.isEmpty(nodes)&&(nodes.size()>position)){
        //modify by lwl 1721 end
            final TreeNode node = nodes.get(position);
            holder.refush(node);
        }

        return convertView;
    }

    public void setEncryptRecord(EncryptRecord encryptRecord) {
        this.encryptRecord = encryptRecord;
    }
    public void setSelectAccount(String account) {
        this.SelectAccount = account;
    }

    private class ViewHolder {
        private ImageView expandImage; // 左侧图标
        private CircleImageView avatarImage; // 头像图标
        private TextView itemText;//树节点名称
        private ImageButton checkbox;
        private View convertView;
        private TextView openSafeLable;
        private Member member = null;
        private TextView anTong_Tag;
        private RelativeLayout clickLayout;
        private String account;
        private ViewHolder(View convertView) {
            anTong_Tag = ButterKnife.findById(convertView, R.id.node_antong_tag);
            expandImage = ButterKnife.findById(convertView, R.id.expand_img);
            avatarImage = ButterKnife.findById(convertView, R.id.round_img);
            itemText = ButterKnife.findById(convertView, R.id.node_value);
            checkbox = ButterKnife.findById(convertView, R.id.open_safe_switch);
            openSafeLable = ButterKnife.findById(convertView, R.id.item_open_safe_label);
            this.convertView = ButterKnife.findById(convertView, R.id.tree_item_layout);
            clickLayout = ButterKnife.findById(convertView, R.id.click_layout);
            anTong_Tag.setVisibility(View.GONE);
        }

        public void refush(TreeNode node) {

            if (node.getSource() instanceof Member) {
                member = (Member) node.getSource();
            } else {
                member = null;
            }

            if (StateParams.getStateParams().isSeverOpen() && member != null && !TextUtils.isEmpty(member.getAccount())) {
                clickLayout.setVisibility(View.VISIBLE);
                checkbox.setVisibility(View.VISIBLE);
                if (encryptRecord != null) {
                    switchBtnController(isSafeOpen());
                    if (isSafeOpen()) {
                        openSafeLable.setVisibility(View.VISIBLE);
                        //[S] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
                        if (!TextUtils.isEmpty(pkgName)) {
                            openSafeLable.setText(context.getString(R.string.selected_open_transfer_top, IEncryptUtils.getAppName(pkgName)));
                        } else {
                            openSafeLable.setText(context.getString(R.string.selected_open_transfer_top, ""));
                        }
                        //[E] add by LiXiaolong on 20160824. fix bug 3303. review by WangChao1.
                    } else {
                        openSafeLable.setVisibility(View.GONE);
                    }
                } else {
                    switchBtnController(false);
                    openSafeLable.setVisibility(View.GONE);
                }
            } else {
                clickLayout.setVisibility(View.GONE);
                checkbox.setVisibility(View.GONE);
                switchBtnController(false);
                openSafeLable.setVisibility(View.GONE);

            }
            int level = node.getLevel();
            //start:fix bug for 5073 by wal@xdja.com
            int padding=(int)context.getResources().getDimension(R.dimen.member_item_show_name_padding);
            int right_padding=(int)context.getResources().getDimension(R.dimen.member_item_show_name_right_padding);
//                convertView.setPadding(16 * levels, 20, 20, 20);
            convertView.setPadding(right_padding * level,padding, padding, padding);
            //end:fix bug for 5073 by wal@xdja.com
            itemText.setText(node.getName());

            //设置左侧展开图片
            if (!node.isLeaf()) {
                avatarImage.setVisibility(View.GONE);
                expandImage.setVisibility(View.VISIBLE);
                anTong_Tag.setVisibility(View.GONE);
            } else {
                if (!ObjectUtil.objectIsEmpty(member) && ObjectUtil.stringIsEmpty(member.getAccount())) {
                    anTong_Tag.setVisibility(View.VISIBLE);
                } else {
                    anTong_Tag.setVisibility(View.GONE);

                }
                avatarImage.setVisibility(View.VISIBLE);
                expandImage.setVisibility(View.GONE);
                //start:modify by wal@xdja.com for 3764
//                clickLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (member == null && ObjectUtil.stringIsEmpty(member.getAccount())) {
//                            return;
//                        }
//                        if (!ObjectUtil.objectIsEmpty(showDismissDialogCallback)) {
//                            if (encryptRecord == null || member == null || TextUtils.isEmpty(member.getAccount())) {
//                                showDismissDialogCallback.showDialog(true);
//                            } else {
//                                showDismissDialogCallback.showDialog(false);
//                            }
//                        }
//                    }
//                });
                //end:modify by wal@xdja.com for 3764
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (member == null && ObjectUtil.stringIsEmpty(member.getAccount())) {
                            return;
                        }
                        //modify  by lwl start  2908
                        if(isSafeOpen()){
                            BroadcastManager.sendBroadcastCloseTransfer();
                            notifyDataSetChanged();
                        }else {
                            String showAccount=member.getAccount();
                            //打开小盾牌时给主框架发送广播
                            BroadcastManager.sendOpenSafeTransferBroadcast(showAccount);
                            notifyDataSetChanged();
                            //若快速开启第三方应用开启 则点击可显示菜单
                            if (StateParams.getStateParams().isQuickOpenThirdAppOpen()) {
                                setCenter(v);
                            }
                            //checkbox.setImageResource(R.drawable.at_selected);
                        }

//                        if (!ObjectUtil.objectIsEmpty(showDismissDialogCallback)) {
//                            if (encryptRecord == null || member == null || TextUtils.isEmpty(member.getAccount())) {
//                                showDismissDialogCallback.showDialog(true);
//                            }else{
//                                showDismissDialogCallback.showDialog(false);
//                            }
//
//
//                        }
                        //modify  by lwl end  2908
                    }
                });

            }
            //判断是否子目录s
            if (!node.isLeaf()) {
                //根据级别设置缩进
                //start:modify by wal@xdja.com for 705
//                itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
               /* itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);*/
                //end:modify by wal@xdja.com for 705
                setItemHeight(R.dimen.department_item_height);
                switch (node.getLevel()) {
                    case 0:
                        convertView.setBackgroundResource(R.color.base_gray_ed);
                        break;
                    default:
                        convertView.setBackgroundResource(R.color.base_gray_f7);
                        break;
                }
                if (expandImage.getVisibility() == View.VISIBLE) {
                    expandImage.setBackgroundResource(node.isExpand() ? R.drawable.img_listitem_expand : R.drawable.img_listitem_unexpand);
                }
            } else {
                int levels = node.getLevel();
                //start:fix bug for 5073 by wal@xdja.com
                int convertView_padding=(int)context.getResources().getDimension(R.dimen.member_item_show_name_padding);
                int convertView_right_padding=(int)context.getResources().getDimension(R.dimen.member_item_show_name_right_padding);
//                convertView.setPadding(16 * levels, 20, 20, 20);
                convertView.setPadding(convertView_right_padding * levels,convertView_padding, convertView_padding, convertView_padding);
                //end:fix bug for 5073 by wal@xdja.com
                setItemHeight(R.dimen.member_item_height);
                /*itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);*/
                //[S]modify by xienan for bug 4275 20161024 review by self
                //convertView.setBackgroundColor(context.getResources().getColor(R.color.base_white_100));
                convertView.setBackgroundResource(R.drawable.selector_contact_list_item);
                //[E]modify by xienan for bug 4275 20161024 review by self
                if (member != null) {
                    Avatar avatarInfo = member.getAvatarInfo();
                    if (avatarInfo != null && !TextUtils.isEmpty(avatarInfo.getThumbnail())) {
                        HeadImgParamsBean imgBean = HeadImgParamsBean.getParams(avatarInfo.getThumbnail());
                        avatarImage.loadImage(avatarInfo.getThumbnail(),true,R.drawable.img_avater_40);
                    } else {
                        avatarImage.loadImage(R.drawable.img_avater_40, true);
                    }
                }
            }

        }

        /**
         * 判断是否可以开启安全通道
         *
         * @return
         */
        private boolean isSafeOpen() {
            if (encryptRecord == null || member == null || TextUtils.isEmpty(member.getAccount())) {
                return false;
            }
            return member.getAccount().equals(SelectAccount);
        }

        private void setItemHeight(@DimenRes int dimenID) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(dimenID));
            convertView.setLayoutParams(layoutParams);
        }

        private void switchBtnController(boolean open) {
            if (open) {
                checkbox.setImageResource(R.drawable.at_selected);
            } else {
                checkbox.setImageResource(R.drawable.at_normal);
            }
        }

    }

    private IShowDismissDialog showDismissDialogCallback;


    public void setShowDismissDialogCallback(IShowDismissDialog showDismissDialogCallback) {
        this.showDismissDialogCallback = showDismissDialogCallback;
    }

    public interface IShowDismissDialog {

        void showDialog(boolean flag);

        void dismissDialog();

    }


}
