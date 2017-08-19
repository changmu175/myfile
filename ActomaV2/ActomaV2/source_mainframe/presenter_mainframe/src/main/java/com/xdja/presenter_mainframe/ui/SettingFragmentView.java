package com.xdja.presenter_mainframe.ui;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.cust.CustInfo;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.CloseAppEvent;
import com.xdja.comm.event.CloseAppReceiverMsg;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.CommonUtils;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.presenter.mvp.view.FragmentSuperView;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SettingCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetting;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.SettingBarView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chenbing on 2015/7/7.
 */
@ContentView(R.layout.fragment_setting)
public class SettingFragmentView extends FragmentSuperView<SettingCommand> implements VuSetting {

    @Bind(R.id.user_img)
    CircleImageView userImg;
    @Bind(R.id.user_nickname)
    TextView userNickname;
    @Bind(R.id.user_account)
    TextView userAccount;
    @Bind(R.id.user_account_string)
    TextView userAccountString;
    @Bind(R.id.user_arrow)
    TextView userArrow;
    @Bind(R.id.user_zxing)
    LinearLayout userZxing;
    @Bind(R.id.user_detail)
    RelativeLayout userDetail;
    @Bind(R.id.sb_setting_account_safe)
    SettingBarView sbSettingAccountSafe;
    @Bind(R.id.sb_setting_news_remind)
    SettingBarView sbSettingNewsRemind;
    @Bind(R.id.sb_setting_message_and_call)
    SettingBarView sbSettingMessageAndCall;
    @Bind(R.id.sb_setting_encrypt_layout)
    SettingBarView sbSettingEncryptLayout;
    @Bind(R.id.sb_setting_about_chip)
    SettingBarView sbSettingAboutChip;
    @Bind(R.id.sb_setting_about_actoma)
    SettingBarView sbSettingAboutActoma;
    @Bind(R.id.sb_setting_nodistrub)
    SettingBarView sbSettingNoDistrub;
    // [Strart] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
    @Bind(R.id.sb_setting_updateprompt)
    TextView sbSettingUpdateprompt;
    // [End] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
    @Bind(R.id.sb_setting_language)
    SettingBarView sbLanguage;
    /**
     * 退出类型
     */
    public static final int EXIT_TYPE_EXIT = 0;
    public static final int EXIT_TYPE_CLOSE = 1;

    @Override
    public void onCreated() {
        super.onCreated();
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2088 . review by wangchao1. Start
        if (sbSettingAboutActoma != null) {
            sbSettingAboutActoma.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_LIST,
                    0, 0, 0, getStringRes(R.string.setting_about_soft)));
        }
        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2088 . review by wangchao1. End
        //[Start]YangShaoPeng<mailto://ysp@xdja.com> 2016-08-30 add. fix bug #1349 . review by wangchao1.
        if (userAccountString != null) {

            userAccountString.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION, 0,
                    0, 0, getStringRes(R.string.actoma_account)));
        }

        if (sbSettingEncryptLayout != null) {
            //私有化部署版本，隐藏“第三方应用加密服务”菜单项。gbc 2017-02-20
            if ((CommonUtils.isZH(getContext()) && UniversalUtil.isXposed()) && !CustInfo.isCustom()) {
                sbSettingEncryptLayout.setVisibility(View.VISIBLE);
            } else {
                sbSettingEncryptLayout.setVisibility(View.GONE);
            }
        }

        //[End]YangShaoPeng<mailto://ysp@xdja.com> 2016-08-30 add. fix bug #1349 . review by wangchao1.
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (UniversalUtil.getLanguage(getContext())) {
            case UniversalUtil.LANGUAGE_CH_SIMPLE:
                sbLanguage.setSecondText(getStringRes(R.string.simple_chinese));
                break;
            case UniversalUtil.LANGUAGE_EN:
                sbLanguage.setSecondText(getStringRes(R.string.english));
                break;
            default:
                sbLanguage.setSecondText(getStringRes(R.string.with_system));
                break;
        }
    }

    @OnClick(R.id.sb_setting_account_safe)
    public void accountSafe() {
        getCommand().accountSafe();
    }

    @OnClick(R.id.user_detail)
    public void userDetail() {
        getCommand().userDetail();
    }

    @OnClick(R.id.user_zxing)
    public void userZxing(){
        getCommand().viewZxing();
    }

    @OnClick(R.id.exit)
    public void showExitTips(){
        final XDialog exitDialog = new XDialog(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_exit, null);
        view.findViewById(R.id.tv_exit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitDialog.dismiss();
                        showExitDialog();
                    }
                });
        TextView tvClose = (TextView)view.findViewById(R.id.tv_close);
        tvClose.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.setting_exit_tip2)));
        tvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitDialog.dismiss();
                        showCloseDialog();
                    }
                });
        exitDialog.setView(view).show();
        exitDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * 关闭安通+提示框
     */
    public void showCloseDialog(){
        final XDialog xDialog = new XDialog(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_exit_close, null);
        final CheckBox receiveMsg = (CheckBox) view.findViewById(R.id.cb_receiver_msg);
        xDialog.setTitle(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, getStringRes(R.string.setting_exit_tip2)))
                .setMessage(getStringRes(R.string.setting_exit_message2))
                .setCustomContentView(view)
                .setNegativeButton(getStringRes(R.string.cancel),null)
                .setPositiveButton(getStringRes(R.string.setting_close), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-03 add. fix bug 1275 . review by wangchao1. Start
                        //增加上报数据点
                        //add by gbc. 2016-12-01. begin
                        SharePreferceUtil.getPreferceUtil(ActomaController.getApp()).setIsCloseActoma(true);
                        SharePreferceUtil.getPreferceUtil(ActomaController.getApp()).setCloseActomaMode(receiveMsg.isChecked()?1:0);
                        //add by gbc. 2016-12-01. end
                        if (!receiveMsg.isChecked()) {
                            BusProvider.getMainProvider().post(new CloseAppEvent());
                        }else{
                            BusProvider.getMainProvider().post(new CloseAppReceiverMsg());
                        }
                        //alh@xdja.com<mailto://alh@xdja.com> 2016-08-03 add. fix bug 1275 . review by wangchao1. End

                        xDialog.dismiss();
                        getCommand().exitToLogin(EXIT_TYPE_CLOSE);
                    }
                })
                .show();
    }
    /**
     * 退出登录提示框
     */
    public void showExitDialog(){
        final XDialog xDialog = new XDialog(getContext());
        xDialog.setTitle(getStringRes(R.string.setting_exit_tip1))
                .setMessage(getStringRes(R.string.setting_exit_message1))
                .setNegativeButton(getStringRes(R.string.cancel),null)
                .setPositiveButton(getStringRes(R.string.setting_exit_tip1), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        xDialog.dismiss();
                        getCommand().exitToLogin(EXIT_TYPE_EXIT);
                    }
                })
                .show();
    }


    /**
     * 修改三方加密服务状态
     *
     * @param isOn 是否开启状态
     */
    /*@Override
    public void changeThirdServiceValue(boolean isOn) {
    }*/

    /**
     * 修改安全锁状态
     *
     * @param isLockOn 是否开启安全锁
     */
   /* @Override
    public void changeSafeLockValue(boolean isLockOn) {

    }*/

    /**
     * 我的设备个数
     *
     * @param count 设备个数
     */
    /*@Override
    public void changeDevicesCount(int count) {

    }*/

    /**
     * 展示account信息
     *
     * @param account
     */
    @Override
    public void setAccountInfo(Account account) {
        if (!TextUtils.isEmpty(account.getNickName())) {
            if (userNickname != null) userNickname.setText(account.getNickName());
        }
        if (!TextUtils.isEmpty(account.getAlias())){
            if (userAccount != null)
                userAccount.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION, 0, 0, 0,
                        account.getAlias()));
        }else {
            if (!TextUtils.isEmpty(account.getAccount())) {
                if (userAccount != null)
                    userAccount.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION, 0,
                            0, 0, account.getAccount()));
            }
        }
    }

    /**
     * 设置昵称
     *
     * @param nickName
     */
    @Override
    public void setNickName(String nickName) {
        if (!TextUtils.isEmpty(nickName)) {
            userNickname.setText(nickName);
        }
    }

    /**
     * 设置账号
     * @param account
     */
    @Override
    public void setAccount(String account) {
        if (!TextUtils.isEmpty(account)) {
            userAccount.setText(TextUtil.getActomaText(getContext(),
                    TextUtil.ActomaImage.IMAGE_VERSION,
                    0, 0, 0, account));
        }
    }

    /**
     * 设置头像
     *
     * @param avatarId
     * @param thumbnailId
     */
    @Override
    public void setImage(String avatarId, String thumbnailId) {
        //modify by alh@xdja.com to fix bug: 524 2016-07-01 start (rummager : anlihuang)
        if (userImg != null)
            userImg.loadImage(avatarId,true);
        //modify by alh@xdja.com to fix bug: 524 2016-07-01 end (rummager : anlihuang)
    }

    /**
     * 修改头像后进行头像展示
     * @param bitmap
     */
    @Override
    public void showUserImage(Bitmap bitmap){
        if (bitmap != null) {
            CircleImageView circleImageView = userImg;
            circleImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setUserImageBackground(Bitmap bitmap) {
        if (bitmap != null) {
            CircleImageView circleImageView = userImg;
            circleImageView.setBackground(new BitmapDrawable(bitmap));
        }
    }

    /**
     * 刷新是否有更新内容
     *
     * @param isHaveNew
     */
    @Override
    public void freshUpdateNew(boolean isHaveNew) {
        // [Strart] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
        if (isHaveNew) {
            sbSettingUpdateprompt.setVisibility(View.VISIBLE);
        } else {
            sbSettingUpdateprompt.setVisibility(View.GONE);
        }
        // [End] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
    }

    @OnClick(R.id.sb_setting_about_actoma)
    public void aboutActoma(){
        // [Strart] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
        //点击后取消new展示
        freshUpdateNew(false);
        // [End] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
        getCommand().aboutSoft();
    }

    @OnClick(R.id.sb_setting_language)
    public void language() {
        getCommand().choiceLanguage();
    }

    @OnClick(R.id.sb_setting_about_chip)
    public void aboutChip(){
        getCommand().aboutChip();
    }

    @OnClick(R.id.sb_setting_news_remind)
    public void newsRemind() {
        getCommand().newsRemind();
    }

    @OnClick(R.id.sb_setting_message_and_call)
    public void dropMessage() {
        getCommand().dropMessage();
    }
    @OnClick(R.id.sb_setting_encrypt_layout)
    public void thirdpartService() {
        getCommand().thirdpartService();
    }
    @OnClick(R.id.sb_setting_nodistrub)
    public void noDistrub(){
        getCommand().noDistrub();
    }
}
