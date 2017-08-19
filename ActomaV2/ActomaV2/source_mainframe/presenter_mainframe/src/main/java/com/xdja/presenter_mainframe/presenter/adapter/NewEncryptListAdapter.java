package com.xdja.presenter_mainframe.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.encrypt.EncryptAppBean;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.enc3rd.utils.ListUtil;
import com.xdja.presenter_mainframe.enc3rd.utils.ThirdEncAppProperty;
import com.xdja.presenter_mainframe.enc3rd.utils.ThirdEncAppUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by geyao on 2015/7/14.
 * 重构-第三方应用加密服务列表适配器
 */
public class NewEncryptListAdapter extends BaseAdapter {
    private List<EncryptAppBean> list;
    private Context context;
    private LayoutInflater layoutInflater;

    public NewEncryptListAdapter(List<EncryptAppBean> list, Context context) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        ThirdEncAppUtil.resetList(context, list);
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }

    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        } else {
            return position;
        }
    }

    @Override
    public long getItemId(int position) {
        if (list == null) {
            return -1;
        } else {
            return position;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_new_encrypt, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EncryptAppBean bean = list.get(position);
        holder.otherAppEncryptContent.setText(bean.getAppName());
        holder.otherAppEncryptDescripteion1.setText(bean.getDescription());
        holder.otherAppEncryptDescripteion2.setText(bean.getSupportType());
        holder.otherAppEncryptDescripteion3.setText(bean.getSupportVertion());
        String pkgName = bean.getPackageName();
        boolean result = ListUtil.checkPackage(context, pkgName);
        if (pkgName.equals("com.tencent.mm")) {//匹配微信
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_weixin : R.drawable.app_weixin_disable);
        } else if (pkgName.equals("com.tencent.mobileqq")) {//匹配QQ
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_qq : R.drawable.app_qq_disable);
        } else if (pkgName.equals("com.alibaba.android.rimet")) {//匹配钉钉
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_dingding : R.drawable.app_dingding_disable);
        } else if (pkgName.equals("com.immomo.momo")) {//匹配陌陌
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_momo : R.drawable.app_momo_disable);
        } else if (ThirdEncAppProperty.mmsHash.containsKey(pkgName)) {//匹配原生短信、包括第三方短信 modify by thz 2016-6-21
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_message : R.drawable.app_message_disable);
        } else if (pkgName.equals("com.jb.gosms")) {//匹配go短信
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_go : R.drawable.app_go_disable);
        } else if (pkgName.equals("com.hellotext.hello")) {//匹配hello短信
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_hello : R.drawable.app_hello_disable);
        } else if (pkgName.equals("com.snda.youni")) {//匹配youni短信
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_youni : R.drawable.app_youni_disable);
        } else if (pkgName.equals("com.tencent.pb")) {//匹配微信通讯录
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_wxphone : R.drawable.app_wxphone_disable);
        } else if (pkgName.equals("cn.com.fetion")) {//匹配飞信
            holder.otherAppEncryptImg.setImageResource(
                    result ? R.drawable.app_feixin : R.drawable.app_feixin_disable);
        } else {
            holder.otherAppEncryptImg.setImageResource(R.drawable.ic_launcher);
        }
        if (result) {
            holder.otherAppEncryptCheckbox.setChecked(bean.isOpen());
        } else {
            holder.otherAppEncryptCheckbox.setChecked(true);
            holder.otherAppEncryptCheckbox.setEnabled(false);
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.other_app_encrypt_img)
        ImageView otherAppEncryptImg;
        @Bind(R.id.other_app_encrypt_content)
        TextView otherAppEncryptContent;
        @Bind(R.id.other_app_encrypt_descripteion1)
        TextView otherAppEncryptDescripteion1;
        @Bind(R.id.other_app_encrypt_descripteion2)
        TextView otherAppEncryptDescripteion2;
        @Bind(R.id.other_app_encrypt_descripteion3)
        TextView otherAppEncryptDescripteion3;
        @Bind(R.id.other_app_encrypt_checkbox)
        CheckBox otherAppEncryptCheckbox;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
