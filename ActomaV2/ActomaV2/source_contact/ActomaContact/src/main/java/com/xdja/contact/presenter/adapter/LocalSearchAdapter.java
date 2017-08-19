package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.EncryptRecord;
import com.xdja.contact.bean.Group;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.executor.SearchAsyncExecutor;
import com.xdja.contact.presenter.activity.LocalSearchMorePresenter;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.comm.uitl.RegisterActionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 好友搜索适配器
 *
 * @author hkb
 * @since 2015年8月13日08:50:55
 */
public class LocalSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private EncryptRecord encryptRecord;

    private List<Group> groups = new ArrayList<>();

    private ArrayList<Group> allGroups = new ArrayList<>();

    private EncryptRecordService encryptRecordService;

    private OnItemClickListener onItemClickListener;

    private String keyWord;

    private Context context;

    private boolean isShowAllData;



    public LocalSearchAdapter(Context context) {
        this.context = context;
        encryptRecordService = new EncryptRecordService(context);
        this.encryptRecord = encryptRecordService.lastSelectedRecord();
    }

    public void setEncryptRecord(EncryptRecord encryptRecord) {
        this.encryptRecord = encryptRecord;
    }

    public String getKeyWord() {
        return keyWord == null ? "" : keyWord;
    }

    public boolean isShowAllData() {
        return isShowAllData;
    }

    public void setIsShowAllData(boolean isShowAllData) {
        this.isShowAllData = isShowAllData;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void clear() {
        if(!ObjectUtil.collectionIsEmpty(groups)){
            groups.clear();
        }
        if(!ObjectUtil.collectionIsEmpty(dataSource)){
            dataSource.clear();
        }
        if(!ObjectUtil.collectionIsEmpty(origianlDataSource)){
            origianlDataSource.clear();
        }
        notifyDataSetChanged();
    }


    private ArrayList<LocalCacheDto> dataSource;

    private ArrayList<LocalCacheDto> origianlDataSource;

    public void setLocalSearchBeans(ArrayList<LocalCacheDto> params){
        if(ObjectUtil.collectionIsEmpty(dataSource)){
            dataSource = new ArrayList<>();
        }
        if (!isShowAllData && !ObjectUtil.collectionIsEmpty(params) && params.size() > 3) {
            this.dataSource.clear();
            for (int i = 0; i < 3; i++) {
                this.dataSource.add(params.get(i));
            }
        } else {
            this.dataSource = params;
        }
        this.origianlDataSource = params;
        notifyDataSetChanged();
    }


    /**
     * 获取群组数据
     *
     * @return
     */
    public List<Group> getGroups() {
        return groups;
    }

    /**
     * 设置群组数据
     *
     * @param groups
     */
    public void setGroups(List<Group> groups) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        if (!isShowAllData && !ListUtils.isEmpty(groups) && groups.size() > 3) {
            this.groups.clear();
            for (int i = 0; i < 3; i++) {
                this.groups.add(groups.get(i));
            }
        } else {
            this.groups = groups;
        }
        allGroups = (ArrayList<Group>) groups;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = null;
        //ViewHolder类型
        switch (i) {
            case 1://Divider item
                view = ViewGroup.inflate(viewGroup.getContext(), R.layout.item_search_devider, null);
                return new DeviderViewHolder(view);
            case 2://好友item
                view = ViewGroup.inflate(viewGroup.getContext(), R.layout.search_friend_list_item, null);
                return new FriendViewHolder(view);
            case 3://群组item
                view = ViewGroup.inflate(viewGroup.getContext(), R.layout.group_list_item, null);
                return new GroupViewHolder(view);
            default://divider Item
                view = ViewGroup.inflate(viewGroup.getContext(), R.layout.item_search_devider, null);
                return new DeviderViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //第一个分割线
        if (position == 0) {
            return 1;
        }
        //联系人显示区域
        if (isContactArea(position)) {
            return 2;
        }

        //群组显示区域
        if (isGroupArea(position)) {
            return 3;
        }

        //最后一个分割线
        if (position == getItemCount() - 1) {
            return 1;
        }
        //群组与联系人中间线
        if (!isShowAllData() && position == getContactSize() + 1) {
            return 1;
        }
        return 1;
    }

    private int getContactSize() {
        if(ObjectUtil.collectionIsEmpty(dataSource)) return 0;
        return dataSource.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        int itemType = getItemViewType(position);
        switch (itemType) {
            case 1://DividerView
                //设置第一行分割线
                if (position == 0) {
                    if (getContactSize() > 0) {
                        ((DeviderViewHolder) viewHolder).refush(context.getString(R.string.contact_search_friend_title), null, 1);//modify by wal@xdja.com for string 好友、通讯录联系人
                    } else if (getGroups().size() > 0) {
                        ((DeviderViewHolder) viewHolder).refush(context.getString(R.string.contact_search_group_title), null, 1);//modify by wal@xdja.com for string 群组
                    }
                    //设置联系人与群组分割线
                } else if (position == getContactSize() + 1) {
                    String groupDividername = null;
                    if (!ListUtils.isEmpty(getGroups())) {
                        groupDividername = context.getString(R.string.contact_search_group_title);//modify by wal@xdja.com for string 群组
                    }
                    ((DeviderViewHolder) viewHolder).refush(groupDividername,context.getString(R.string.contact_search_more_friend_title), 2);//modify by wal@xdja.com for string 更多好友、通讯录联系人
                    //设置最后一行分割线
                } else if (position == getItemCount() - 1) {
                    ((DeviderViewHolder) viewHolder).refush(null, context.getString(R.string.contact_search_more_group_title), 3);//modify by wal@xdja.com for string 更多群组
                }
                break;
            case 2://联系人item
                FriendViewHolder holder = ((FriendViewHolder) viewHolder);
                holder.refreshUi(dataSource.get(position-1));
                break;
            case 3://群组item
                Group group = getGroups().get(getGroupIndex(position));
                ((GroupViewHolder) viewHolder).refush(group);
                break;
            case 4:
                //最后一行
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (getContactSize() == 0 && getGroups().isEmpty()) {
            return 0;
        }
        int count = 0;
        int dividerCount = 1;
        //如果大于联系人总数则需要增加两个分割线: 1 顶部分隔条和底部更多分隔条
        if (isShowAllData()) {
            dividerCount = 1;
        } else {
            dividerCount = 2;
        }

        //有联系人数据显示联系人列表
        if (getContactSize() > 0) {
            count += getContactSize() + dividerCount;
        }
        //有群组数据显示群组列表
        if (!getGroups().isEmpty()) {
            count += getGroups().size() + dividerCount;
        }
        return count;
    }


    /**
     * 是否在联系人列表范围内
     *
     * @param i
     * @return
     */
    private boolean isContactArea(int i) {
        int pos = i - 1;
        if (pos >= 0 && pos < getContactSize()) {
            return true;
        }
        return false;
    }

    /**
     * 是否是群组区域
     *
     * @param i
     * @return
     */
    private boolean isGroupArea(int i) {
        if (getGroupIndex(i) < getGroups().size()) {
            return true;
        }
        return false;
    }

    /**
     * 计算群组点击索引
     *
     * @param i
     * @return
     */
    private int getGroupIndex(int i) {

        int dividerCount = 1;

        if (getContactSize() != 0) {
            dividerCount = 2;
        }
        if(ObjectUtil.collectionIsEmpty(dataSource)){
            int index = i - dividerCount;
            return index >= 0 ? index : i;
        }else{
            int index = i - (dataSource.size() + dividerCount);
            return index >= 0 ? index : i;
        }
    }

    /**
     * 分隔线布局
     */
    private class DeviderViewHolder extends RecyclerView.ViewHolder {

        private TextView dividerName;
        private TextView moreText;
        private View moreLayout;

        private DeviderViewHolder(View itemView) {
            super(itemView);
            dividerName = ButterKnife.findById(itemView, R.id.search_devider_name);
            moreText = ButterKnife.findById(itemView, R.id.more_text);
            moreLayout = ButterKnife.findById(itemView, R.id.more_info_lay);

        }

        /**
         * 刷新devider数据
         *
         * @param item devider文字
         * @param type 1 好友联系人 2 群组 3 通话
         */
        public void refush(String item, String moreTextStr, int type) {
            if (TextUtils.isEmpty(item)) {
                dividerName.setVisibility(View.GONE);
            } else {
                dividerName.setVisibility(View.VISIBLE);
            }


            switch (type) {
                case 1:
                    dividerName.setText(item);
                    break;
                case 2:
                    if (TextUtils.isEmpty(moreTextStr) || origianlDataSource.size() <= 3) {
                        moreLayout.setVisibility(View.GONE);
                    } else if (origianlDataSource.size()  > 3) {
                        moreLayout.setVisibility(View.VISIBLE);
                    }
                    moreLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, LocalSearchMorePresenter.class);
//                            intent.putExtra(LocalSearchMorePresenter.FLAG_DATA_SEARCH_TYPE, LocalSearchMorePresenter.TYPE_FRIEND_OR_MEMBER);
                            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_SEARCH_TYPE, RegisterActionUtil.EXTRA_KEY_TYPE_FRIEND_OR_MEMBER);
                            SearchAsyncExecutor.cacheSearchResult = origianlDataSource;
//                            intent.putExtra(LocalSearchMorePresenter.FLAG_DATA_TPYE_KEYWORD, keyWord);
                            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_TPYE_KEYWORD, keyWord);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                    dividerName.setText(item);
                    moreText.setText(moreTextStr);
                    break;
                case 3:
                    if (TextUtils.isEmpty(moreTextStr) || allGroups.size() <= 3) {
                        moreLayout.setVisibility(View.GONE);
                    } else if (allGroups.size() > 3) {
                        moreLayout.setVisibility(View.VISIBLE);
                    }
                    moreLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //跳转到群组搜索
                            Intent intent = new Intent(context, LocalSearchMorePresenter.class);
                            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_SEARCH_TYPE, RegisterActionUtil.EXTRA_KEY_TYPE_GROUP);
                            intent.putParcelableArrayListExtra(RegisterActionUtil.EXTRA_KEY_LOCAL_SEARCH_ADAPTER_DATA, allGroups);
                            intent.putExtra(RegisterActionUtil.EXTRA_KEY_FLAG_DATA_TPYE_KEYWORD, keyWord);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                    dividerName.setText(item);
                    moreText.setText(moreTextStr);
                    break;
            }
        }
    }


    private LocalCacheDto clickedItemObject;

    /**
     * 好友Viewholder
     */

    private class FriendViewHolder extends RecyclerView.ViewHolder{

        private TextView safeTxt;

        private TextView number;
        private TextView numberLab;
        private ViewGroup numberLay;
        private CircleImageView avater;
        private ImageView safeBtn;
        private TextView showName;
        private FriendViewHolder(View itemView) {
            super(itemView);
            showName = ButterKnife.findById(itemView, R.id.friend_list_item_name);
            number = ButterKnife.findById(itemView, R.id.friend_list_item_number);
            avater = ButterKnife.findById(itemView, R.id.friend_list_item_head);
            numberLay = ButterKnife.findById(itemView, R.id.friend_list_item_number_lay);
            numberLab = ButterKnife.findById(itemView, R.id.friend_list_item_number_lable);
            numberLay.setVisibility(View.VISIBLE);
            safeBtn = ButterKnife.findById(itemView, R.id.friend_open_transfer);
            safeTxt = ButterKnife.findById(itemView, R.id.friend_list_item_label);
        }

        private void sendBroadcast() {
            Intent intent = new Intent();
            intent.setAction(RegisterActionUtil.ACTION_SELECTED_OPEN_TRANSFER);
            context.sendBroadcast(intent);
        }

        public void refreshUi(final LocalCacheDto localSearchBean){
            clickedItemObject = localSearchBean;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(localSearchBean);
                    }
                }
            });
            if(ObjectUtil.objectIsEmpty(localSearchBean))return;
            //setSafeButtonState(localSearchBean);
            setAvatarInfo();
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
            if(!ObjectUtil.stringIsEmpty(nickName)){
                String nickNamePy = localSearchBean.getNickNamePy();
                String nickNamePinYin = localSearchBean.getNickNamePinYin();
                if(!TextUtils.isEmpty(keyWord)){
                    if(nickName.contains(keyWord) ){
                        showName.setText(getSpanned(nickName));
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(nickNamePy) && nickNamePy.contains(keyWord) && !isMatch){
                        showName.setText(getSpanned(nickName));
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(nickNamePinYin) && nickNamePinYin.contains(keyWord) && !isMatch){
                        showName.setText(getSpanned(nickName));
                        isMatch = true;
                    }
                }
            }
            if(!ObjectUtil.stringIsEmpty(name)){
                String namePy = localSearchBean.getNamePy();
                String namePinYin = localSearchBean.getNamePinYin();
                if(name.contains(keyWord)){
                    showName.setText(getSpanned(name));
                    isMatch = true;
                }
                if(!ObjectUtil.stringIsEmpty(namePy) && namePy.contains(keyWord) && !isMatch){
                    showName.setText(getSpanned(name));
                    isMatch = true;
                }
                if(!ObjectUtil.stringIsEmpty(namePinYin) &&  namePinYin.contains(keyWord) && !isMatch){
                    showName.setText(getSpanned(name));
                    isMatch = true;
                }
            }
            if(!ObjectUtil.stringIsEmpty(remark)){
                String remarkPy = localSearchBean.getRemarkPy();
                String remarkPinYin = localSearchBean.getRemarkPinYin();
                if(remark.contains(keyWord)){
                    if(showNameValue.equals(remark)){
                        showName.setText(getSpanned(remark));
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(remarkPy) && remarkPy.contains(keyWord) && !isMatch){
                        showName.setText(getSpanned(remark));
                        isMatch = true;
                    }
                    if(!ObjectUtil.stringIsEmpty(remarkPy) && remarkPinYin.contains(keyWord) && !isMatch){
                        showName.setText(getSpanned(remark));
                        isMatch = true;
                    }
                }
            }
            if(!isMatch){
                if(!ObjectUtil.stringIsEmpty(account)){
                    if(account.contains(keyWord)){
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
                    numberLab.setText(context.getString(R.string.contact_search_account));//modify by wal@xdja.com for string 手机号:
                    number.setText(getSpanned(showPhone));
                }
            }
        }

        private void setAvatarInfo(){
            avater.loadImage(R.drawable.img_avater_40,true);
            if(!ObjectUtil.objectIsEmpty(clickedItemObject)){
                Avatar avatar = clickedItemObject.getAvatar();
                if(!ObjectUtil.objectIsEmpty(avatar)){
                    if(!ObjectUtil.stringIsEmpty(avatar.getThumbnail())) {
                        avater.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
                    }
                }
            }
        }
    }


    private class GroupViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avater;
        private TextView groupnameTV;

        private GroupViewHolder(View itemView) {
            super(itemView);
            groupnameTV = ButterKnife.findById(itemView, R.id.group_name);
            avater = ButterKnife.findById(itemView, R.id.avatar);

        }

        public void refush(final Group group) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onGroupClick(group);
                    }
                }
            });

            groupnameTV.setText(getSpanned(group.getGroupName()));
            avater.loadImage(group.getAvatar(),true, R.drawable.group_avatar_40);
        }
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

}
