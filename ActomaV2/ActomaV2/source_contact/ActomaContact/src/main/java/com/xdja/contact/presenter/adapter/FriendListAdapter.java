package com.xdja.contact.presenter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.uitl.TextUtil;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.StateParams;
import com.xdja.contact.R;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.Friend;
import com.xdja.contact.service.EncryptRecordService;
import com.xdja.contact.service.FriendRequestService;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.comm.uitl.ObjectUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wanghao on 2015/7/20.
 */
public class FriendListAdapter extends ArcBaseAdapter implements SectionIndexer {

    private Context context;

    private List<Friend> dataSource;

    private Map<String, Integer> alphaIndexer;

    private String[] sections;

    private String preCode;

    private EncryptRecordService encryptRecordService;


    private Friend selectedFriend;

    private String appName;

    private HashMap<String,String> hashMap;

    private Map map;

    private String account;

    public FriendListAdapter(Context context,HashMap<String,String> hashMap,Map map) {
        this.context = context;
        this.map = map;
        this.hashMap = hashMap;
        this.encryptRecordService = new EncryptRecordService(context);
    }


    @Override
    public int getCount() {
        if (dataSource == null || dataSource.size() <= 0) return 0;
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSource.get(position).getViewType();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        NewFriendViewHolder newFriendViewHolder;
        IndexViewHolder indexViewHolder;
        int viewType = getItemViewType(position);
        if (viewType == Friend.CONTACT_ITEM) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.friend_list_item, null);
                holder.userPhoto = (CircleImageView) convertView.findViewById(R.id.friend_list_item_head);
                holder.contactName = (TextView) convertView.findViewById(R.id.friend_list_item_name);
                holder.contactNumber = (TextView) convertView.findViewById(R.id.friend_list_item_label);
                holder.openTransfer = (ImageView) convertView.findViewById(R.id.friend_open_transfer);
                holder.clickLayout = (RelativeLayout) convertView.findViewById(R.id.open_tranfer);
                holder.clickLayout.setVerticalGravity(View.GONE);
                holder.openTransfer.setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            selectedFriend = dataSource.get(position);
            if (Friend.AT_OBJECT.equals(selectedFriend.getType())) {
                Spanned str = TextUtil.getActomaText(context, TextUtil.ActomaImage.IMAGE_LIST,
                        0, 0, 0, context.getString(R.string.actoma_team_title));
                //SpannableString ss = formatAnTongSpanContent(str, context, (float) 1.0);
                holder.contactName.setText(str);
				//[S]modify by tangsha@20161228 for 7516
                holder.userPhoto.loadImage(R.drawable.at_teem_40,true);
				//[E]modify by tangsha@20161228 for 7516
            } else {
                Avatar avatar = selectedFriend.getAvatar();
                if (!ObjectUtil.objectIsEmpty(avatar) && !ObjectUtil.stringIsEmpty(avatar.getThumbnail())) {
                    holder.userPhoto.loadImage(avatar.getThumbnail(),true,R.drawable.img_avater_40);
                } else {
				    //[S]modify by tangsha@20161228 for 7516
                    holder.userPhoto.loadImage(R.drawable.img_avater_40,true);
					//[E]modify by tangsha@20161228 for 7516
                }
                holder.contactName.setText(selectedFriend.showName());
            }
            if(!ObjectUtil.mapIsEmpty(hashMap)){
                account =  hashMap.get("destAccount");
            }else {
                account = null;
            }
            if (StateParams.getStateParams().isSeverOpen()) {
                if (!ObjectUtil.objectIsEmpty(account)) {
                    if (account.equals(selectedFriend.getAccount()) && !Friend.AT_OBJECT.equals(selectedFriend.getType())) {
                        holder.contactNumber.setVisibility(View.VISIBLE);
                        holder.contactNumber.setText(IEncryptUtils.getAppName(hashMap.get("appPackage"))+" "+context.getString(R.string.contact_safe_communication));//modify by wal@xdja.com for string 安全通信中...

                        holder.clickLayout.setVerticalGravity(View.VISIBLE);
                        holder.openTransfer.setImageResource(R.drawable.at_selected);
                    } else {
                        holder.contactNumber.setVisibility(View.GONE);
                        holder.openTransfer.setImageResource(R.drawable.at_normal);
                    }
                } else {
                    holder.contactNumber.setVisibility(View.GONE);
                    holder.openTransfer.setImageResource(R.drawable.at_normal);
                }
                if (Friend.AT_OBJECT.equals(selectedFriend.getType())) {
                    holder.clickLayout.setVerticalGravity(View.GONE);
                    holder.openTransfer.setVisibility(View.GONE);
                } else {
                    holder.clickLayout.setVerticalGravity(View.VISIBLE);
                    holder.openTransfer.setVisibility(View.VISIBLE);
//                    holder.clickLayout.setOnClickListener(new SafeButtonListener(position));
                    holder.openTransfer.setOnClickListener(new SafeButtonListener(position));
                }
            } else {
                holder.clickLayout.setVerticalGravity(View.GONE);
                holder.openTransfer.setVisibility(View.GONE);
                holder.contactNumber.setVisibility(View.GONE);
                holder.clickLayout.setOnClickListener(null);
                holder.openTransfer.setOnClickListener(null);
            }
        } else if (viewType == Friend.NEW_FRIEND) {
            if (convertView == null) {
                newFriendViewHolder = new NewFriendViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.new_friend_item, null);
                newFriendViewHolder.tipsLayout = convertView.findViewById(R.id.new_friend_layout);
                newFriendViewHolder.friendNum = (TextView) convertView.findViewById(R.id.new_friend_num);
                convertView.setTag(newFriendViewHolder);
            } else {
                newFriendViewHolder = (NewFriendViewHolder) convertView.getTag();
            }
            FriendRequestService service = new FriendRequestService();
            int count = service.countNewFriend();
            if (count > 0) {
                newFriendViewHolder.tipsLayout.setVisibility(View.VISIBLE);
                newFriendViewHolder.friendNum.setText(String.valueOf(count));
            } else {
                newFriendViewHolder.tipsLayout.setVisibility(View.GONE);
            }
        } else if (viewType == Friend.ALPHA) {
            indexViewHolder = new IndexViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.friend_list_index_layout_2, null);
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

    public static Bitmap getBitmapWithName(String fieldName, Resources resource)
            throws NoSuchFieldException, NumberFormatException,
            IllegalArgumentException, IllegalAccessException {
        Field field = com.xdja.contact.R.drawable.class.getDeclaredField(fieldName);
        int resouseId = Integer.parseInt(field.get(null).toString());
        return BitmapFactory.decodeResource(resource, resouseId);
    }

    public static Bitmap small(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    private class SafeButtonListener implements View.OnClickListener {

        private int position = -1;

        private SafeButtonListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Friend friend = dataSource.get(position);
            if (!ObjectUtil.objectIsEmpty(account) && account.equals(friend.getAccount())) {
                BroadcastManager.sendBroadcastCloseTransfer();
                notifyDataSetChanged();
            } else {
                //打开小盾牌时给主框架发送广播
                BroadcastManager.sendOpenSafeTransferBroadcast(friend.getAccount());
                notifyDataSetChanged();
                //若快速开启第三方应用开启 则点击可显示菜单
                if (StateParams.getStateParams().isQuickOpenThirdAppOpen()) {
                    setCenter(v);
                }
            }
        }
    }


    static class NewFriendViewHolder {

        View tipsLayout;

        TextView friendNum;

    }

    static class ViewHolder {

        CircleImageView userPhoto;

        TextView contactName;

        TextView contactNumber;

        ImageView openTransfer;

        RelativeLayout clickLayout;

    }

    static class IndexViewHolder {
        TextView indexTextView;
    }

    public void setDataSource(List<Friend> dataSource) {
        int length = dataSource.size();
        this.dataSource = dataSource;
        this.sections = new String[length];
        if (alphaIndexer == null) {
            alphaIndexer = new HashMap<String, Integer>();
        }
        if (!alphaIndexer.isEmpty()) {
            alphaIndexer.clear();
        }
        for (int i = 0; i < length; i++) {
            Friend tmp = dataSource.get(i);
            String name = tmp.getIndexChar();
            if (TextUtils.isEmpty(preCode) || !preCode.equals(name)) {
                preCode = name;
                alphaIndexer.put(name, i);// #,A,B,C,D,F,G,Z
            }
            // A,F,Z
            sections[i] = name;
        }
        notifyDataSetChanged();
    }

    public  void setHashMapAndMap(HashMap<String,String> hashMap,Map map){
        this.hashMap = hashMap;
        this.map = map;
    }

    public  void setHashMap(HashMap<String,String> hashMap){
        this.hashMap = hashMap;
    }

    public  void setMap(Map map){
        this.map = map;
    }


    public int getPositionForString(String s) {
        if (TextUtils.isEmpty(s)) {
            return -1;
        }
        //start:modify by wangalei for 1018
//        if (s.equals("#")) {
//            return 0;
//        }
        //end:modify by wangalei for 1018
        if (!ObjectUtil.mapIsEmpty(alphaIndexer) && alphaIndexer.containsKey(s)) {
            return alphaIndexer.get(s);
        }
        return -1;
    }


    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int section) {
        String later = section - 2 >= 0
                ? sections[section - 2]
                : sections[section];
        return alphaIndexer.get(later);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
