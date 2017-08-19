package com.xdja.imp.ui;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.uitl.HttpUtils;
import com.xdja.imp.R;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.ISingleChatCommand;
import com.xdja.imp.ui.vu.ISingleChatSettingVu;

import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/12/4.
 */
public class SingleChatSettingVu extends ImpActivitySuperView<ISingleChatCommand> implements ISingleChatSettingVu,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{

    private CircleImageView avatarImageView;

    private TextView nickNameTextView;

    private CheckBox topChatCheckBox;

    private CheckBox noDisturbChatCheckBox;

    private Button addUserButton;


    private CustomDialog customDialog;

    @Override
    public void init(@NonNull LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        avatarImageView = ButterKnife.findById(getView(), R.id.single_chat_settings_person_avatar_IV);
        avatarImageView.setOnClickListener(this);
        nickNameTextView = ButterKnife.findById(getView(), R.id.single_chat_settings_person_name_TV);

        addUserButton = ButterKnife.findById(getView(), R.id.single_chat_settings_add_user_btn);
        addUserButton.setOnClickListener(this);

        topChatCheckBox = ButterKnife.findById(getView(), R.id.single_chat_settings_top_chat_checkBox);
        topChatCheckBox.setOnCheckedChangeListener(this);

        noDisturbChatCheckBox = ButterKnife.findById(getView(), R.id.single_chat_settings_messages_no_disturb_checkBox);
        noDisturbChatCheckBox.setOnCheckedChangeListener(this);

        RelativeLayout cleanAllChatMessagesRL = ButterKnife.findById(getView(), R.id.single_chat_settings_part_four_layout);
        cleanAllChatMessagesRL.setOnClickListener(this);

        customDialog=new CustomDialog(getContext());
        customDialog.setTitle(getContext().getString(R.string.confirm_clean_all_chat_messages));
        customDialog.setNegativeButton(getContext().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        customDialog.setPositiveButton(getContext().getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().cleanAllSingleChatMessages();
                customDialog.dismiss();
            }
        });

        //add by zya
        RelativeLayout mHistoryFileLayout = ButterKnife.findById(getView(), R.id.single_chat_settings_part_five_layout);
        mHistoryFileLayout.setOnClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_single_chat_settings;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.single_chat_settings_add_user_btn) {
            //添加联系人
            getCommand().openPersonListActivity();

        } else if (id == R.id.single_chat_settings_person_avatar_IV) {
            //进入联系人详情界面
            getCommand().openChatDetailInterface();

        } else if (id == R.id.single_chat_settings_part_four_layout) {
            //清除聊天记录
            customDialog.show();
        } else if(id == R.id.single_chat_settings_part_five_layout){
            getCommand().openHistoryFileListActivity();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
        int id = compoundButton.getId();
        if (id == R.id.single_chat_settings_top_chat_checkBox) {
            //fix bug 3807 by licong, reView by zya,2016/9/9
            if(!HttpUtils.isNetworkAvailable(getContext())){
                //XToast.show(getContext(), getContext().getResources().getString(R.string.network_error_check_setting));
                topChatCheckBox.setChecked(!topChatCheckBox.isChecked());
                return ;
            }
            //是否置顶聊天选项卡
            getCommand().updateTopChatCheckBoxState(topChatCheckBox.isChecked());

        } else if (id == R.id.single_chat_settings_messages_no_disturb_checkBox) {
            //modify by zya@xdja.com fix bug NACTOMA-403
            if(!HttpUtils.isNetworkAvailable(getContext())){
                //XToast.show(getContext(), getContext().getResources().getString(R.string.network_error_check_setting));
                noDisturbChatCheckBox.setChecked(!noDisturbChatCheckBox.isChecked());
                return ;
            }
            //是否设置免打扰选项卡
            getCommand().updateChatNoDisturbCheckBoxState(noDisturbChatCheckBox.isChecked());
        }
    }

    /**
     * 弹出进度提示框
     *
     * @param msgStr
     */
    @Override
    public void showProgressDialog(String msgStr) {
        showCommonProgressDialog(msgStr);
    }

    /**
     * 取消弹框
     */
    @Override
    public void dismissDialog() {
        dismissCommonProgressDialog();
    }

    @Override
    public void setTopChatCheckBoxState(boolean isTop) {
        topChatCheckBox.setChecked(isTop);
    }

    @Override
    public void setNoDisturbCheckBoxState(boolean isNoDisturb) {
        noDisturbChatCheckBox.setChecked(isNoDisturb);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void setNickName(String name){
        nickNameTextView.setText(name);
    }

    @Override
    public void isShowAddFriendBtn(boolean isShow) {
        if(isShow){
            addUserButton.setVisibility(View.VISIBLE);
        }else{
            addUserButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPartnerImage(String url) {
        if (null != url) {
            //TODO gbc
            /*HeadImgParamsBean imgBean = HeadImgParamsBean.getParams(url);
            avatarImageView.loadImage(
                    imgBean.getHost(), true, imgBean.getFileId(),
                    imgBean.getSize(), R.drawable.chatlist_contact_avatar_40);*/
            avatarImageView.loadImage(url, true,  R.drawable.corp_user_40dp);
        }
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.setting);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
