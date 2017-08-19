package com.xdja.imp.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.uitl.HttpUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.GroupChatSettingsCommand;
import com.xdja.imp.ui.vu.GroupChatSettingsVu;

import butterknife.ButterKnife;

/**
 * Created by cxp on 2015/7/27.
 */
public class ViewGroupChatSettings extends ImpActivitySuperView<GroupChatSettingsCommand> implements GroupChatSettingsVu, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private CheckBox topChatCheckBox;
    private CheckBox noDisturbChatCheckBox;
    private Button exitGroupChatButton;
    private CustomDialog customDialog;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        super.init(inflater, container);
        initViews();

    }


    private void initViews(){
        topChatCheckBox = ButterKnife.findById(getView(),R.id.group_chat_settings_top_chat_check_box);
        topChatCheckBox.setOnCheckedChangeListener(this);

        noDisturbChatCheckBox = ButterKnife.findById(getView(),R.id.group_chat_settings_messages_no_disturb_check_box);
        noDisturbChatCheckBox.setOnCheckedChangeListener(this);

        exitGroupChatButton = ButterKnife.findById(getView(),R.id.group_chat_settings_dissolve_group_btn);
        exitGroupChatButton.setOnClickListener(this);

        RelativeLayout cleanAllChatMessagesRL = ButterKnife.findById(getView(), R.id.group_chat_settings_item_four_layout);
        cleanAllChatMessagesRL.setOnClickListener(this);
        //add by zya
        RelativeLayout mHistoryFileLayout = ButterKnife.findById(getView(), R.id.single_chat_settings_part_five_layout);
        mHistoryFileLayout.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.group_chat_settings_dissolve_group_btn) {
            if (getCommand().judgeIsGroupOwner()){
                showConfirmDialog(getContext().getString(R.string.exit_and_disband_group_confirm_content),getContext().getString(R.string.exit_and_disband_group_content_message));
            }else {
                showConfirmDialog(getContext().getString(R.string.exit_group_confirm_content),getContext().getString(R.string.exit_group_content_message));
            }
        } else if (id == R.id.group_chat_settings_item_four_layout) {
            showConfirmDialog();
        } else if(id == R.id.single_chat_settings_part_five_layout){
            LogUtil.getUtils().d("ViewGroupChatSettings:single_chat_settings");
        }
    }

    private void showConfirmDialog() {
        customDialog = new CustomDialog(getContext());
        customDialog.setTitle(getContext().getString(R.string.del_confirm_content)).setCancelable(false).setPositiveButton(getContext().getResources().getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().cleanAllGroupChatMessages();
                customDialog.dismiss();
            }
        }).setNegativeButton(getContext().getResources().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        if (!customDialog.isShowing()){
            customDialog.show();
        }
    }

    private void showConfirmDialog(String title, String contentMessage) {
        customDialog = new CustomDialog(getContext());
        customDialog.setTitle(title).setMessage(contentMessage).setCancelable(false).setPositiveButton(getContext().getResources().getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommand().exitGroupChatAndDissolveGroup();
                customDialog.dismiss();
            }
        }).setNegativeButton(getContext().getResources().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.group_chat_settings_top_chat_check_box) {
            //fix bug 3807 by licong, reView by zya,2016/9/9
            if(!HttpUtils.isNetworkAvailable(getContext())){
                //XToast.show(getContext(), getContext().getResources().getString(R.string.network_error_check_setting));
                topChatCheckBox.setChecked(!topChatCheckBox.isChecked());
                return ;
            }
            //是否置顶聊天选项卡
            //TODO：会话置顶
            if(isChecked != getCommand().getIsShowOnTop()){
                getCommand().updateTopChatState(topChatCheckBox.isChecked());
            }

        } else if (id == R.id.group_chat_settings_messages_no_disturb_check_box) {
            //是否设置免打扰选项卡
            //TODO:免打扰设置
            //modify by zya@xdja.com fix bug NACTOMA-403
            if(!HttpUtils.isNetworkAvailable(getContext())){
                //XToast.show(getContext(), getContext().getResources().getString(R.string.network_error_check_setting));
                noDisturbChatCheckBox.setChecked(!noDisturbChatCheckBox.isChecked());
                return ;
            }

            if(isChecked != getCommand().getNoDisturb()) {
                getCommand().updateChatNoDisturbState(noDisturbChatCheckBox.isChecked());
            }
        }
    }


    @Override
    public void showProgressDialog(String msgStr) {
        showCommonProgressDialog(msgStr);
    }

    @Override
    public void dismissDialog() {
        dismissCommonProgressDialog();
    }

    /**
     * 设置界面聊天置顶状态
     *
     * @param isTopChat
     */
    @Override
    public void setTopChatCheckBoxState(boolean isTopChat) {
        topChatCheckBox.setChecked(isTopChat);
    }

    /**
     * 设置界面免打扰状态
     *
     * @param isNoDisturb
     */
    @Override
    public void setNoDisturbCheckBoxState(boolean isNoDisturb) {
        noDisturbChatCheckBox.setChecked(isNoDisturb);
    }


    /**
     * 设置退出按钮标题
     *
     * @param isGroupOwner
     */
    @Override
    public void setExitButtonTitle(boolean isGroupOwner) {
        if (isGroupOwner){
            exitGroupChatButton.setText(R.string.group_chat_settings_dissolve_group_btn_text);
        }else {
            exitGroupChatButton.setText(R.string.group_chat_settings_exit_group_btn_text);
        }
    }

    /**
     * 初始化群组聊天顶部布局
     */
    @Override
    public void initGroupChatTopLayout(Context context, String groupId, String account) {
        //TODO:联系人适配组件
        Fragment topFragment = getCommand().getGroupInfoDetailManager(groupId);
        final FragmentTransaction transaction = ((AppCompatActivity) getActivity()).getSupportFragmentManager()
                .beginTransaction();
        transaction.add(R.id.group_chat_settings_item_top_layout, topFragment, "group_member_editor");
        transaction.commit();
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_group_chat_settings;

    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.activity_group_chat_settings_presenter);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
