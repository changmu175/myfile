package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.util.ContactShowUtil;
import com.xdja.contact.util.ContactUtils;
import com.xdja.comm.uitl.ObjectUtil;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2016/3/18.
 *
 * 修改名称注意和LocalSearchAdapter比对
 * 2016-03-18 发现本地搜索功能界面功能有问题重写界面功能
 *
 */
public class LocalSearchAdapter2 extends BaseAdapter {

    private String keyWord;

    private Context context;

    private List<LocalCacheDto> dataSource;

    public LocalSearchAdapter2(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        if(ObjectUtil.collectionIsEmpty(dataSource))return 0;
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendViewHolder friendViewHolder;
        GroupViewHolder groupViewHolder;
        DeviderViewHolder deviderViewHolder;
        int viewType = getItemViewType(position);
        if(viewType == LocalCacheDto.FRIEND_ALPHA){ //设置好友显示头、
            if(ObjectUtil.objectIsEmpty(convertView)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search_devider, null);
                deviderViewHolder = new DeviderViewHolder(convertView);
                convertView.setTag(deviderViewHolder);
            }else{
                deviderViewHolder = (DeviderViewHolder)convertView.getTag();
            }
            //((DeviderViewHolder) viewHolder).refush("好友、通讯录联系人", null, 1);
            deviderViewHolder.dividerName.setVisibility(View.VISIBLE);
            deviderViewHolder.dividerName.setText(context.getString(R.string.contact_search_friend_title));//modify by wal@xdja.com for string 好友、通讯录联系人
            deviderViewHolder.moreLayout.setVisibility(View.GONE);
        }
        if(viewType == LocalCacheDto.FRIEND_ITEM){ //设置好友对应的item
            if(ObjectUtil.objectIsEmpty(convertView)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.search_friend_list_item, null);
                friendViewHolder = new FriendViewHolder(convertView);
                convertView.setTag(friendViewHolder);
            }else{
                friendViewHolder = (FriendViewHolder)convertView.getTag();
            }
            LocalCacheDto localCacheDto = dataSource.get(position);
            friendViewHolder.refreshUi(localCacheDto);
        }
        if(viewType == LocalCacheDto.FRIEND_MORE){//设置显示更多好友
            if(ObjectUtil.objectIsEmpty(convertView)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search_devider, null);
                deviderViewHolder = new DeviderViewHolder(convertView);
                convertView.setTag(deviderViewHolder);
            }else{
                deviderViewHolder = (DeviderViewHolder)convertView.getTag();
            }
            deviderViewHolder.dividerName.setVisibility(View.GONE);
            deviderViewHolder.dividerName.setText("");
            deviderViewHolder.moreLayout.setVisibility(View.VISIBLE);
            deviderViewHolder.moreText.setText(context.getString(R.string.contact_search_more_friend_title));//modify by wal@xdja.com for string 更多好友、通讯录联系人
        }
        if(viewType == LocalCacheDto.GROUP_ALPHA){ //设置群组显示头、
            if(ObjectUtil.objectIsEmpty(convertView)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search_devider, null);
                deviderViewHolder = new DeviderViewHolder(convertView);
                convertView.setTag(deviderViewHolder);
            }else{
                deviderViewHolder = (DeviderViewHolder)convertView.getTag();
            }
            deviderViewHolder.dividerName.setVisibility(View.VISIBLE);
            deviderViewHolder.dividerName.setText(context.getString(R.string.contact_search_group_title));//modify by wal@xdja.com for string 群组
            deviderViewHolder.moreLayout.setVisibility(View.GONE);
        }
        if(viewType == LocalCacheDto.GROUP_ITEM){ //设置群组对应的item
            if(ObjectUtil.objectIsEmpty(convertView)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.group_list_item, null);
                groupViewHolder = new GroupViewHolder(convertView);
                convertView.setTag(groupViewHolder);
            }else {
                groupViewHolder = (GroupViewHolder)convertView.getTag();
            }
            LocalCacheDto localCacheDto = dataSource.get(position);
            groupViewHolder.refush(localCacheDto);
        }
        if(viewType == LocalCacheDto.GROUP_MORE){//设置显示更多群组
            if(ObjectUtil.objectIsEmpty(convertView)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search_devider, null);
                deviderViewHolder = new DeviderViewHolder(convertView);
                convertView.setTag(deviderViewHolder);
            }else{
                deviderViewHolder = (DeviderViewHolder)convertView.getTag();
            }
            deviderViewHolder.dividerName.setVisibility(View.GONE);
            deviderViewHolder.dividerName.setText("");
            deviderViewHolder.moreLayout.setVisibility(View.VISIBLE);
            deviderViewHolder.moreText.setText(context.getString(R.string.contact_search_more_group_title));//modify by wal@xdja.com for string 更多群组
        }
        return convertView;
    }

   private class FriendViewHolder {

        TextView safeTxt;
        TextView number;
        TextView numberLab;
        ViewGroup numberLay;
        CircleImageView avater;
        ImageView safeBtn;
        TextView showName;

        private FriendViewHolder(View itemView) {
            showName = ButterKnife.findById(itemView, R.id.friend_list_item_name);
            number = ButterKnife.findById(itemView, R.id.friend_list_item_number);
            avater = ButterKnife.findById(itemView, R.id.friend_list_item_head);
            numberLay = ButterKnife.findById(itemView, R.id.friend_list_item_number_lay);
            numberLab = ButterKnife.findById(itemView, R.id.friend_list_item_number_lable);
            numberLay.setVisibility(View.VISIBLE);
            safeBtn = ButterKnife.findById(itemView, R.id.friend_open_transfer);
            safeTxt = ButterKnife.findById(itemView, R.id.friend_list_item_label);
        }

        public void refreshUi(final LocalCacheDto localSearchBean) {
            if (ObjectUtil.objectIsEmpty(localSearchBean)) return;
            loadingAvatar(localSearchBean);
            //显示名称中命中关键字
            String showNameValue = localSearchBean.showName();
            boolean isMatch = false;
            String account = localSearchBean.getAccount();
            String nickName = localSearchBean.getNickName();
            String name = localSearchBean.getName();
            String remark = localSearchBean.getRemark();
            numberLay.setVisibility(View.GONE);
            numberLab.setVisibility(View.GONE);
            number.setVisibility(View.GONE);


            if (!ObjectUtil.stringIsEmpty(remark)) {
                String remarkPy = localSearchBean.getRemarkPy();
                String remarkPinYin = localSearchBean.getRemarkPinYin();
                if (remark.contains(keyWord)) {
                    if (showNameValue.equals(remark)) {
                        showName.setText(getSpanned(remark));
                        isMatch = true;
                    }
                    if (!ObjectUtil.stringIsEmpty(remarkPy) && remarkPy.contains(keyWord) && !isMatch) {
                        showName.setText(getSpanned(remark));
                        isMatch = true;
                    }
                    if (!ObjectUtil.stringIsEmpty(remarkPy) && remarkPinYin.contains(keyWord) && !isMatch) {
                        showName.setText(getSpanned(remark));
                        isMatch = true;
                    }
                }
            }
            if (!isMatch&&!ObjectUtil.stringIsEmpty(name)) {
                String namePy = localSearchBean.getNamePy();
                String namePinYin = localSearchBean.getNamePinYin();
                if (name.contains(keyWord)) {
                    showName.setText(getSpanned(name));
                    isMatch = true;
                }
                if (!ObjectUtil.stringIsEmpty(namePy) && namePy.contains(keyWord) && !isMatch) {
                    showName.setText(getSpanned(name));
                    isMatch = true;
                }
                if (!ObjectUtil.stringIsEmpty(namePinYin) && namePinYin.contains(keyWord) && !isMatch) {
                    showName.setText(getSpanned(name));
                    isMatch = true;
                }
            }
            if (!isMatch&&!ObjectUtil.stringIsEmpty(nickName)) {
                String nickNamePy = localSearchBean.getNickNamePy();
                String nickNamePinYin = localSearchBean.getNickNamePinYin();
                if (!TextUtils.isEmpty(keyWord)) {
                    if (nickName.contains(keyWord)) {
                        showName.setText(getSpanned(nickName));
                        isMatch = true;
                    }
                    if (!ObjectUtil.stringIsEmpty(nickNamePy) && nickNamePy.contains(keyWord) && !isMatch) {
                        showName.setText(getSpanned(nickName));
                        isMatch = true;
                    }
                    if (!ObjectUtil.stringIsEmpty(nickNamePinYin) && nickNamePinYin.contains(keyWord) && !isMatch) {
                        showName.setText(getSpanned(nickName));
                        isMatch = true;
                    }
                }
            }
            if (!isMatch) {
                if(!ObjectUtil.stringIsEmpty(localSearchBean.getAlias())){//add by lwl 2408
                    if(localSearchBean.getAlias().contains(keyWord)){
                        showName.setText(showNameValue);
                        numberLay.setVisibility(View.VISIBLE);
                        numberLab.setVisibility(View.VISIBLE);
                        number.setVisibility(View.VISIBLE);
                        numberLab.setText(context.getString(R.string.contact_search_account));//modify by wal@xdja.com for string 帐号:
                        number.setText(ContactShowUtil.getSpanned(localSearchBean.getAlias(), keyWord, context));
                        return;
                    }
                } else if (!ObjectUtil.stringIsEmpty(account)) {
                    if (account.contains(keyWord)) {
                        showName.setText(showNameValue);
                        numberLay.setVisibility(View.VISIBLE);
                        numberLab.setVisibility(View.VISIBLE);
                        number.setVisibility(View.VISIBLE);
                        numberLab.setText(context.getString(R.string.contact_search_account));//modify by wal@xdja.com for string 帐号:
                        number.setText(getSpanned(account));
                        return;
                    }
                }
                //手机号匹配到关键字
                showName.setText(showNameValue);
                String phones = localSearchBean.getPhone();
                String[] phoneArray = ContactUtils.getPhones(phones);
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
                    number.setText(getSpanned(showPhone));
                }
            }
        }

        private void loadingAvatar(LocalCacheDto localCacheDto){
            avater.loadImage(R.drawable.img_avater_40,true);
            if(!ObjectUtil.objectIsEmpty(localCacheDto)){
                Avatar avatar = localCacheDto.getAvatar();
                if(!ObjectUtil.objectIsEmpty(avatar)){
                    if(!ObjectUtil.stringIsEmpty(avatar.getThumbnail())) {
                        avater.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
                    }
                }
            }
        }


    }

    private class GroupViewHolder{


        CircleImageView avater;

        TextView groupnameTV;

        private GroupViewHolder(View itemView) {
            groupnameTV = ButterKnife.findById(itemView, R.id.group_name);
            avater = ButterKnife.findById(itemView, R.id.avatar);
        }
        public void refush(final LocalCacheDto localCacheDto) {
            groupnameTV.setText(getSpanned(localCacheDto.getGroupName()));
            //start:modify by wal@xdka.com for 3921
//            avater.loadImage(localCacheDto.getAvatar(),true,R.drawable.group_avatar_40);
            avater.loadImage(localCacheDto.getGroupAvatar(),true,R.drawable.group_avatar_40);
            //end:modify by wal@xdka.com for 3921
        }
    }

    private class DeviderViewHolder{

        TextView dividerName;
        TextView moreText;
        View moreLayout;

        private DeviderViewHolder(View itemView) {
            dividerName = ButterKnife.findById(itemView, R.id.search_devider_name);
            moreText = ButterKnife.findById(itemView, R.id.more_text);
            moreLayout = ButterKnife.findById(itemView, R.id.more_info_lay);
        }
    }


    public List<LocalCacheDto> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<LocalCacheDto> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void clear() {
        if(!ObjectUtil.collectionIsEmpty(dataSource)){
            dataSource.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 设置匹配到的关键字变成红色
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
}
