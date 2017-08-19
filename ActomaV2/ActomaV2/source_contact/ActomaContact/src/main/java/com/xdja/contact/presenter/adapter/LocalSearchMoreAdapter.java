package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.uitl.StateParams;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.EncryptRecord;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * 好友搜索适配器
 *
 * @author hkb
 * @since 2015年8月13日08:50:55
 */
public class LocalSearchMoreAdapter extends BaseAdapter {

    private EncryptRecord encryptRecord;

    private EncryptRecordService encryptRecordService;

    private OnItemClickListener onItemClickListener;

    private String keyWord;

    private Context context;

    public static final int TYPE_GROUP = 0;

    public static final int TYPE_MEMBER = 1;

    private static int DEFAULT_TYPE = TYPE_GROUP;

    private ArrayList<Group> groupDataSource = new ArrayList<>();

    private ArrayList<LocalCacheDto> memberDataSource;

    private LocalCacheDto clickedMember;

    public LocalSearchMoreAdapter(Context context,OnItemClickListener onItemClickListener,int type) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.DEFAULT_TYPE = type;
        encryptRecordService = new EncryptRecordService(context);
        this.encryptRecord = encryptRecordService.lastSelectedRecord();
    }

    public void setMemberDataSource(ArrayList<LocalCacheDto> memberDataSource) {
        this.memberDataSource = memberDataSource;
        notifyDataSetChanged();
    }

    public void setGroupDataSource(ArrayList<Group> groupDataSource) {
        this.groupDataSource = groupDataSource;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(DEFAULT_TYPE == TYPE_GROUP){
            if(!ObjectUtil.collectionIsEmpty(groupDataSource)){
                return groupDataSource.size();
            }
        }
        if(DEFAULT_TYPE == TYPE_MEMBER){
            if(!ObjectUtil.collectionIsEmpty(memberDataSource)){
                return memberDataSource.size();
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        MemberViewHolder memberViewHolder;
        if(DEFAULT_TYPE == TYPE_GROUP){
            if(ObjectUtil.objectIsEmpty(convertView)){
                convertView = LayoutInflater.from(context).inflate(R.layout.group_more_list_item, null);
                groupViewHolder = new GroupViewHolder(convertView);
                convertView.setTag(groupViewHolder);
            }else{
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }
            groupViewHolder.refreshUi(groupDataSource.get(position));
        }else{
            if(ObjectUtil.objectIsEmpty(convertView)){
                convertView = LayoutInflater.from(context).inflate(R.layout.search_more_friend_list_item, null);
                memberViewHolder = new MemberViewHolder(convertView);
                convertView.setTag(memberViewHolder);
            }else{
                memberViewHolder = (MemberViewHolder) convertView.getTag();
            }
            memberViewHolder.refreshUi(position);
        }
        return convertView;
    }

    public class GroupViewHolder{

        private View itemView;

        private CircleImageView avatar;

        private TextView groupnameTV;

        public GroupViewHolder(View itemView){
            this.itemView = itemView;
            groupnameTV = ButterKnife.findById(itemView, R.id.group_name);
            avatar = ButterKnife.findById(itemView, R.id.avatar);
        }
        public void refreshUi(final Group group) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onGroupClick(group);
                    }
                }
            });

            groupnameTV.setText(ContactShowUtil.getSpanned(group.getGroupName(), getKeyWord(), context));
            avatar.loadImage(group.getAvatar(),true,R.drawable.group_avatar_40);
        }
    }

    public class MemberViewHolder{
        private View itemView;
        private TextView safeTxt;
        private TextView number;
        private TextView numberLab;
        private ViewGroup numberLay;
        private CircleImageView avater;
        private ImageView safeBtn;
        private TextView showName;
        public MemberViewHolder(View itemView) {
            this.itemView = itemView;
            showName = ButterKnife.findById(itemView, R.id.friend_list_item_name);
            number = ButterKnife.findById(itemView, R.id.friend_list_item_number);
            avater = ButterKnife.findById(itemView, R.id.friend_list_item_head);
            numberLay = ButterKnife.findById(itemView, R.id.friend_list_item_number_lay);
            numberLab = ButterKnife.findById(itemView, R.id.friend_list_item_number_lable);
            numberLay.setVisibility(View.VISIBLE);
            safeBtn = ButterKnife.findById(itemView, R.id.friend_open_transfer);
            safeTxt = ButterKnife.findById(itemView, R.id.friend_list_item_label);
        }

        public void refreshUi(final int position){
            final LocalCacheDto member = memberDataSource.get(position);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(member);
                    }
                }
            });
            //setSafeButtonState(position);
            setAvatarInfo(position);
            //显示名称中命中关键字
            String showNameValue = member.showName();
            boolean isMatch = false;
            String account = member.getAccount();
            String nickName = member.getNickName();
            String name = member.getName();
            String remark = member.getRemark();
            numberLay.setVisibility(View.GONE);
            numberLab.setVisibility(View.GONE);
            number.setVisibility(View.GONE);


            if(!ObjectUtil.stringIsEmpty(remark)){
                String remarkPy = member.getRemarkPy();
                String remarkPinYin = member.getRemarkPinYin();
                if(!ObjectUtil.stringIsEmpty(keyWord)){
                    if(remark.contains(keyWord)){
                        showName.setText(ContactShowUtil.getSpanned(remark, keyWord, context));
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(remarkPy) && remarkPy.contains(keyWord) && !isMatch){
                        //showName.setText(ContactShowUtil.getSpanned(remarkPy, keyWord, context));
                        showName.setText(remark);
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(remarkPinYin) && remarkPinYin.contains(keyWord) && !isMatch){
                        //showName.setText(ContactShowUtil.getSpanned(remarkPinYin, keyWord, context));
                        showName.setText(remark);
                        isMatch = true;
                    }
                }
            }

            if(!isMatch&&!ObjectUtil.stringIsEmpty(name)){
                String namePy = member.getNamePy();
                String namePinYin = member.getNamePinYin();
                if(!ObjectUtil.stringIsEmpty(keyWord)){
                    if(name.contains(keyWord)){
                        showName.setText(ContactShowUtil.getSpanned(name, keyWord, context));
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(namePy) && namePy.contains(keyWord) && !isMatch){
                        //showName.setText(ContactShowUtil.getSpanned(namePy, keyWord, context));
                        showName.setText(name);
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(namePinYin) && namePinYin.contains(keyWord) && !isMatch){
                        //showName.setText(ContactShowUtil.getSpanned(namePinYin, keyWord, context));
                        showName.setText(name);
                        isMatch = true;
                    }

                }
            }

            if(!isMatch&&!ObjectUtil.stringIsEmpty(nickName)){
                String nickNamePy = member.getNickNamePy();
                String nickNamePinYin = member.getNickNamePinYin();
                if(!TextUtils.isEmpty(keyWord)){
                    if(nickName.contains(keyWord)){
                        showName.setText(ContactShowUtil.getSpanned(nickName, keyWord, context));
                        isMatch = true;
                    }

                    if(!ObjectUtil.stringIsEmpty(nickNamePy) && nickNamePy.contains(keyWord) && !isMatch){
                        //showName.setText(ContactShowUtil.getSpanned(nickNamePy, keyWord, context));
                        showName.setText(nickName);
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(nickNamePinYin) && nickNamePinYin.contains(keyWord) && !isMatch){
                        //showName.setText(ContactShowUtil.getSpanned(nickNamePinYin, keyWord, context));
                        showName.setText(nickName);
                        isMatch = true;
                    }
                }
            }
            if(!isMatch){
                String phones = member.getPhone();
                String[] phoneArray = getPhones(phones);
                String showPhone = "";
                if (!ObjectUtil.arrayIsEmpty(phoneArray)) {
                    for (String phone : phoneArray) {
                        if (phone.contains(keyWord)) {
                            showPhone = phone;
                            break;
                        }
                    }
                }
                if (!ObjectUtil.stringIsEmpty(showPhone)) {
                    showName.setText(showNameValue);
                    numberLay.setVisibility(View.VISIBLE);
                    numberLab.setVisibility(View.VISIBLE);
                    number.setVisibility(View.VISIBLE);
                    numberLab.setText(context.getString(R.string.contact_search_phone));//modify by wal@xdja.com for string 手机号:
                    number.setText(ContactShowUtil.getSpanned(showPhone, keyWord, context));
                    return;
                }else{
                    if(!ObjectUtil.stringIsEmpty(member.getAlias())){//add by lwl 2408
                        if(member.getAlias().contains(keyWord)){
                            showName.setText(showNameValue);
                            numberLay.setVisibility(View.VISIBLE);
                            numberLab.setVisibility(View.VISIBLE);
                            number.setVisibility(View.VISIBLE);
                            numberLab.setText(context.getString(R.string.contact_search_account));//modify by wal@xdja.com for string 帐号:
                            number.setText(ContactShowUtil.getSpanned(member.getAlias(), keyWord, context));
                            return;
                        }
                    } else if(!ObjectUtil.stringIsEmpty(account)){
                        if(account.contains(keyWord)){
                            showName.setText(showNameValue);
                            numberLay.setVisibility(View.VISIBLE);
                            numberLab.setVisibility(View.VISIBLE);
                            number.setVisibility(View.VISIBLE);
                            numberLab.setText(context.getString(R.string.contact_search_account));//modify by wal@xdja.com for string 帐号:
                            number.setText(ContactShowUtil.getSpanned(account, keyWord, context));
                            return;
                        }
                    }
                    showName.setText(showNameValue);
                }
            }
        }

        private void setAvatarInfo(final int position){
            LocalCacheDto member = memberDataSource.get(position);
            avater.loadImage(R.drawable.img_avater_40,true);
            if(!ObjectUtil.objectIsEmpty(member)){
                Avatar avatar = member.getAvatar();
                if(!ObjectUtil.objectIsEmpty(avatar)){
                    if(!ObjectUtil.stringIsEmpty(avatar.getThumbnail())) {
                        avater.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
                    }
                }
            }
        }

        private void setSafeButtonState(final int position){
            LocalCacheDto member = memberDataSource.get(position);
            if (StateParams.getStateParams().isSeverOpen() && !ObjectUtil.stringIsEmpty(member.getAccount())) {
                if (!ObjectUtil.objectIsEmpty(encryptRecord)) {
                    if (encryptRecord.getAccount().equals(member.getAccount())) {
                        safeTxt.setVisibility(View.VISIBLE);
                        safeBtn.setVisibility(View.VISIBLE);
                        safeBtn.setImageResource(R.drawable.at_selected);
                    } else {
                        safeTxt.setVisibility(View.GONE);
                        safeBtn.setVisibility(View.VISIBLE);
                        safeBtn.setImageResource(R.drawable.at_normal);
                    }
                } else {
                    safeTxt.setVisibility(View.GONE);
                    safeBtn.setVisibility(View.VISIBLE);
                    safeBtn.setImageResource(R.drawable.at_normal);
                }
            } else {
                safeBtn.setVisibility(View.GONE);
                safeTxt.setVisibility(View.GONE);
            }
            safeBtn.setOnClickListener(null);
        }


    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(RegisterActionUtil.ACTION_SELECTED_OPEN_TRANSFER);
        context.sendBroadcast(intent);
    }

    /**
     * 获取手机号
     *
     * @param phone
     * @return
     */
    private String[] getPhones(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return null;
        } else {
            return phone.split("#");
        }
    }


    /**
     * 设置匹配到的关键字变成红色
     *
     * @param nameOrNumber
     * @return
     */
    private CharSequence getSpanned(String nameOrNumber) {
        if (TextUtils.isEmpty(nameOrNumber)) {
            return Html.fromHtml(nameOrNumber);
        }
        int index = nameOrNumber.indexOf(getKeyWord());
        if (index == -1) {
            return Html.fromHtml(nameOrNumber);
        }
        int keyLength = getKeyWord().length();
        String start = nameOrNumber.substring(0, index);
        String middle = nameOrNumber.substring(index, index + keyLength);
        String end = nameOrNumber.substring(index + keyLength, nameOrNumber.length());
        StringBuffer sb = new StringBuffer();
        sb.append(TextUtils.htmlEncode(start));
        sb.append("<font color = ");
        sb.append(context.getResources().getColor(R.color.high_light_color));
        sb.append(">");
        sb.append(TextUtils.htmlEncode(middle));
        sb.append("</font>");
        sb.append(TextUtils.htmlEncode(end));
        return Html.fromHtml(sb.toString());
    }

    public interface OnItemClickListener {

        void onGroupClick(Group group);

        void onItemClick(LocalCacheDto localSearchBean);
    }


    private String getKeyWord(){
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void clear(){
        if(!ObjectUtil.collectionIsEmpty(groupDataSource)){
            groupDataSource.clear();
        }
        if(!ObjectUtil.collectionIsEmpty(memberDataSource)){
            memberDataSource.clear();
        }
        notifyDataSetChanged();
    }
}
