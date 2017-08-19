package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.squareup.otto.Subscribe;
import com.xdja.comm.event.ImgBeanEvent;
import com.xdja.comm.event.UpdateAccountEvent;
import com.xdja.comm.event.UpdateBindMobileEvent;
import com.xdja.comm.event.UpdateImgBitmap;
import com.xdja.comm.event.UpdateNickNameEvent;
import com.xdja.comm.zxing.creat.PopupWindowZxing;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.UserDetailCommand;
import com.xdja.presenter_mainframe.global.obs.UnBindDeviceObservable;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.UserDetailView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuUserDetail;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class UserDetailPresenter extends PresenterActivity<UserDetailCommand, VuUserDetail> implements
        UserDetailCommand {
    public Account account;

    @Inject
    BusProvider busProvider;

    //是否有自定义账号信息
    private boolean isHaveAlias = false;

    private PopupWindowZxing zxing;

    @Inject
    @InteractorSpe(value = DomainConfig.GET_CURRENT_ACCOUNT_INFO)
    Lazy<Ext0Interactor<Account>> getCurrentAccountInfoUseCase;

    @Inject
    @InteractorSpe(DomainConfig.MODIFY_AVATAR)
    Lazy<Ext1Interactor<Bitmap, Void>> modifyAvatarUseCase;

    @NonNull
    @Override
    protected Class<? extends VuUserDetail> getVuClass() {
        return UserDetailView.class;
    }

    //alh@xdja.com<mailto://alh@xdja.com> 2017-02-28 add. fix bug 9114 . review by wangchao1. Start
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onReceiveUnbindMobileEvent(UnBindDeviceObservable.UnBindMobileEvent event) {
        getCurrentAccountInfoUseCase.get().fill().execute(new Action1<Account>() {
            @Override
            public void call(Account account) {
                UserDetailPresenter.this.account = account;
                if (isHaveMobile()) {
                    getVu().setUserMobileNum(account.getMobiles().get(0));
                } else {
                    getVu().setUserMobileNum("");
                }
            }
        });
    }
    //alh@xdja.com<mailto://alh@xdja.com> 2017-02-28 add. fix bug 9114 . review by wangchao1. End

    @NonNull
    @Override
    protected UserDetailCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        } else {
            LogUtil.getUtils().e("UseCaseComponent为空，重新进入LauncherPresenter！");
            return;
        }

        busProvider.register(this);

        initializeAccount(true , true);
    }

    //modify by alh@xdja.com to fix bug: 613 2016-06-21 start(rummager:wangchao1)
    private void initializeAccount(final boolean reload , final boolean showDefaultImage){
        getCurrentAccountInfoUseCase.get().fill().execute(new Action1<Account>() {
            @Override
            public void call(Account account) {
                UserDetailPresenter.this.account = account;
                //alh@xdja.com<mailto://alh@xdja.com> 2017-04-07 add. fix bug 11159 . review by gbc. Start
                getVu().setUserImg(account.getAvatarId(), account.getThumbnailId(), reload , showDefaultImage);
                //alh@xdja.com<mailto://alh@xdja.com> 2017-04-07 add. fix bug 11159 . review by gbc. End
                getVu().setUserNickName(account.getNickName());
                getVu().setUserQrImg();
                getVu().setUserMobileNum(account.getMobiles().get(0));
                if (!TextUtils.isEmpty(account.getAlias())) {
                    getVu().setAT(account.getAlias(), true);
                    isHaveAlias = true;
                } else {
                    getVu().setAT(account.getAccount(), false);
                    isHaveAlias = false;
                }
            }
        });
        //modify by alh@xdja.com to fix bug: 613 2016-06-21 end(rummager:wangchao1)
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (getVu().isShow()) {
                getVu().dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 修改头像
     */
    @Override
    public void openUpdateUserImg() {
        Navigator.navigateToSetHeadPortrait();
    }

    /**
     * 打开修改昵称页面
     */
    @Override
    public void openUpdateNickPage() {
        Navigator.navigateToSetNickNameWithName(account.getNickName());
    }

    /**
     * 显示二维码大图
     */
    @Override
    public void showBigQrImg() {
        if (account == null) {
            return;
        }
        zxing= new PopupWindowZxing(this, "1#" + (isHaveAlias?account.getAlias():account.getAccount()));//modify by lwl  is have alias
        zxing.showPopupWindow();
    }

    //add by lwl start
    @Override
    protected void onStop() {
        if(zxing!=null)
            zxing.hidePopupWindow();
        super.onStop();
    }
    //add by lwl end

    @Override
    public void modifyMobile() {
        if (isHaveMobile()) {
            Navigator.navigateToModifyPhoneNumber(account.getMobiles().get(0));
        } else {
            Navigator.navigateToBindPhoneNumber(BindPhoneNumberPresenter.CURRENT_TYPE, BindPhoneNumberPresenter
                    .BIND_PHONE_TYPE);
        }
    }

    /**
     * 设置安通+帐号
     */
    @Override
    public void setActomaAccount() {
        if (isHaveAlias) {
            return;
        } else {
            Navigator.navigateToSetAccount();
        }
    }

    private boolean isHaveMobile() {
        if (account == null) return false;
        List<String> mobiles = account.getMobiles();
        if (mobiles == null || mobiles.isEmpty()) {
            return false;
        } else {
            //判断内容是否为空
            String mobile = mobiles.get(0);
            if (TextUtils.isEmpty(mobile)) {
                return false;
            }
            return true;
        }
    }


    /**
     * 设置头像后上传
     *
     * @param updateImgBitmap
     */
    @Subscribe
    public void updateImage(final UpdateImgBitmap updateImgBitmap) {
        LogUtil.getUtils().d("收到截图");

        executeInteractorNoRepeat(modifyAvatarUseCase.get().fill(updateImgBitmap.getBm()), new
                LoadingDialogSubscriber<Void>(this, this) {
            @Override
            public void onNext(Void aVoid) {
                super.onNext(aVoid);
                getVu().showToast(getString(R.string.upload_user_avatar_success));

                getVu().showUserImage(updateImgBitmap.getBm());

                ImgBeanEvent event = new ImgBeanEvent();
                event.setBitmap(updateImgBitmap.getBm());
                busProvider.post(event);

                initializeAccount(true, false);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }.registerLoadingMsg(getString(R.string.upload_user_avatar)));
    }

//    /**
//     * 设置页面修改头像信息bean
//     */
//    public static class ImgBeanEvent {
//        private String url;
//
//        private Bitmap bitmap;
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
//
//        public Bitmap getBitmap() {
//            return bitmap;
//        }
//
//        public void setBitmap(Bitmap bitmap) {
//            this.bitmap = bitmap;
//        }
//    }

    /**
     * 修改昵称事件
     *
     * @param event 事件对象
     */
    @Subscribe
    public void updateNickName(UpdateNickNameEvent event) {
        if (!event.getNewNickName().isEmpty()) {
            getVu().setUserNickName(event.getNewNickName());
            account.setNickName(event.getNewNickName());
        }
    }

    /**
     * 修改账号事件
     *
     * @param event
     */
    @Subscribe
    public void updateAccount(UpdateAccountEvent event) {
        if (!event.getNewAccount().isEmpty()) {
            isHaveAlias = true;
            getVu().setAT(event.getNewAccount(), true);
            account.setAlias(event.getNewAccount());
        }
    }

    /**
     * 修改绑定手机号码
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void updateMobile(UpdateBindMobileEvent event) {
        getCurrentAccountInfoUseCase.get().fill().execute(new Action1<Account>() {
            @Override
            public void call(Account account) {
                UserDetailPresenter.this.account = account;
                if (isHaveMobile()) {
                    getVu().setUserMobileNum(account.getMobiles().get(0));
                } else {
                    getVu().setUserMobileNum("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (busProvider != null) {
            busProvider.unregister(this);
        }
    }


//    /**
//     * 更新绑定手机号的事件
//     */
//    public static class UpdateBindMobileEvent {
//        private String newMobile;
//
//        public String getNewMobile() {
//            return newMobile;
//        }
//
//        public void setNewMobile(String newMobile) {
//            this.newMobile = newMobile;
//        }
//    }
}
