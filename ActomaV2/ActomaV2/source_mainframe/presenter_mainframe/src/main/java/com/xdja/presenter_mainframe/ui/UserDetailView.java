package com.xdja.presenter_mainframe.ui;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.UserDetailCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.VuUserDetail;
import com.xdja.presenter_mainframe.util.TextUtil;
import com.xdja.presenter_mainframe.widget.SettingBarView;
import com.xdja.comm.circleimageview.CircleImageView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/4/29.
 */
@ContentView(R.layout.activity_user_detail)
public class UserDetailView extends ActivityView<UserDetailCommand> implements VuUserDetail {

    @Bind(R.id.sb_user_detail_avater)
    SettingBarView sbUserDetailAvater;
    @Bind(R.id.sb_user_detail_nickname)
    SettingBarView sbUserDetailNickname;
    @Bind(R.id.sb_user_detail_account)
    SettingBarView sbUserDetailAccount;
    @Bind(R.id.sb_user_detail_qr_card)
    SettingBarView sbUserDetailQrCard;
    @Bind(R.id.sb_user_detail_phone_number)
    SettingBarView sbUserDetailPhoneNumber;

    private static final int LAYOUT_PARAMS = 54;

    @OnClick(R.id.sb_user_detail_avater)
    void detailAvater() {
        getCommand().openUpdateUserImg();
    }

    @OnClick(R.id.sb_user_detail_phone_number)
    void phoneNumber() {
        getCommand().modifyMobile();
    }

    @OnClick(R.id.sb_user_detail_account)
    void setAccount() {
        getCommand().setActomaAccount();
    }

    @OnClick(R.id.sb_user_detail_nickname)
    void setNickname() {
        getCommand().openUpdateNickPage();

    }

    @OnClick(R.id.sb_user_detail_qr_card)
    void showQrCard() {
        getCommand().showBigQrImg();
    }


    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2088 . review by wangchao1. Start
    @Override
    public void onCreated() {
        super.onCreated();
        if (sbUserDetailAccount != null) {
            sbUserDetailAccount.setText(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                    0, 0, 0, getStringRes(R.string.at_account)));
        }
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2016-08-02 add. fix bug 2088 . review by wangchao1. End

    /**
     * 设置用户头像
     *
     * @param avatarId
     * @param thumbnailId
     */
    @Override
    public void setUserImg(String avatarId, String thumbnailId, boolean reload , boolean showDefaultImage) {
        if (avatarId != null) {
            CircleImageView circleImageView = sbUserDetailAvater.getRightImage();
            if (reload)
                circleImageView.loadImage(avatarId, true , showDefaultImage);
            if (thumbnailId != null)
                circleImageView.showImageDetailAble(thumbnailId);
        }

    }

    @Override
    public void showUserImage(Bitmap bitmap) {
        if (bitmap != null) {
            CircleImageView circleImageView = sbUserDetailAvater.getRightImage();
            circleImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 设置用户昵称
     *
     * @param userNickName 用户昵称
     */
    @Override
    public void setUserNickName(@Nullable String userNickName) {
        if (!TextUtils.isEmpty(userNickName))
            sbUserDetailNickname.setSecondText(userNickName);
        else
            sbUserDetailNickname.setSecondText(getContext().getString(R.string.no_setting));
    }

    /**
     * 展示安通账号
     * @param at              用户安通账号
     * @param isCustomAccount 是否是自定义的安通账号
     */
    @Override
    public void setAT(@Nullable String at, @Nullable boolean isCustomAccount) {
        if (at != null) {
            sbUserDetailAccount.setSecondText(at);
        }
        if (isCustomAccount) {
            sbUserDetailAccount.setImgIsShow(false);
        } else {
            sbUserDetailAccount.setImgIsShow(true);
        }
    }

    /**
     * 设置用户二维码名片
     */
    @Override
    public void setUserQrImg() {
        //[Start]YangShaoPeng<mailto://ysp@xdja.com> 2016-08-18 add. fix bug #2687 . review by wangchao1. Start
        LinearLayout ll = (LinearLayout) sbUserDetailQrCard.findViewById(R.id.setting_bar);
        if (ll == null || ll.findViewById(R.id.img_local_image) != null) {
            return;
        }
        ImageView imageView = new ImageView(getContext());
        imageView.setId(R.id.img_local_image);
        imageView.setImageResource(R.mipmap.af_setting_ic_ma);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LAYOUT_PARAMS, LAYOUT_PARAMS);
        imageView.setLayoutParams(params);
        ll.addView(imageView, 0);
    }

    /**
     * 设置用户手机号码
     *
     * @param mobileNum 用户手机号码
     */
    @Override
    public void setUserMobileNum(String mobileNum) {
        if (!TextUtils.isEmpty(mobileNum))
            sbUserDetailPhoneNumber.setSecondText(mobileNum);
        else
            sbUserDetailPhoneNumber.setSecondText(getContext().getString(R.string.no_setting));
    }

    /**
     * 显示progress等待框
     *
     * @param msg 要显示的文字
     */
    @SuppressWarnings("UnusedParameters")
    @Override
    public void showProgressDialog(String msg) {
    }

    /**
     * 隐藏progress等待框
     */
    @SuppressWarnings("UnusedParameters")
    @Override
    public void hideProgressDialog() {

    }

    @Override
    public boolean isShow() {
        return sbUserDetailAvater == null || sbUserDetailAvater.getRightImage() == null || !sbUserDetailAvater
                .getRightImage().isShow() ? false : true;
    }

    @Override
    public void dismiss() {
        if (sbUserDetailAvater != null && sbUserDetailAvater.getRightImage() != null) {
            sbUserDetailAvater.getRightImage().dismiss();
        }
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_user_detail);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
