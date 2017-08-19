package com.xdja.contact.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.contact.R;
import com.xdja.contact.bean.Member;
import com.xdja.contact.util.AlphaUtils;
import com.xdja.contact.util.GroupUtils;

import java.util.List;

/**
 * @author hkb.
 * @since 2015/7/22/0022.
 */
public class ChooseMemberAdapter extends BaseAdapter {
    private Context mContext;
    private List<Member> members;


    public ChooseMemberAdapter(Context mContext, List<Member> members) {
        this.mContext = mContext;
        this.members = members;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.choose_contact_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(convertView);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Member member = members.get(position);
        // 判断是否显示拼音字母
        String currentStr = AlphaUtils.getAlpha(member.getNamePy());
        String previewStr = (position - 1) >= 0 ? AlphaUtils.getAlpha(members.get(
                position - 1).getNamePy()) : " ";

        holder.refush(member,!previewStr.equals(currentStr));
        GroupUtils.loadAvatarToImgView(holder.avater, member, R.drawable.default_friend_icon);
        return convertView;
    }

    private class ViewHolder{

        private View alphaLay;
        private CircleImageView avater;
        private TextView alpha;
        private TextView name;

        private CheckBox checkBox;
        private ViewHolder(View view) {
            checkBox = (CheckBox) view.findViewById(R.id.contact_item_checkbox);
            alphaLay = view.findViewById(R.id.contact_name_alpha_lay);
            alpha = (TextView) view.findViewById(R.id.contact_name_alpha);
            name = (TextView) view.findViewById(R.id.contact_name);
            avater = (CircleImageView) view.findViewById(R.id.contact_item_avater);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });
        }

        public void refush(Member member,boolean isShowaAlpha){
            if(isShowaAlpha){
                alphaLay.setVisibility(View.VISIBLE);
                alpha.setText(AlphaUtils.getAlpha(member.getNamePy()));
            }else {
                alphaLay.setVisibility(View.GONE);
            }
            name.setText(member.getName());
            checkBox.setChecked(member.isChecked());
            //Note: 头像暂时不解析
//            avater

        }
    }
}
