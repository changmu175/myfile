package com.xdja.presenter_mainframe.presenter.fragement;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.xdja.comm.event.FreshUpdateNewEvent;
import com.xdja.comm.event.ImgBeanEvent;
import com.xdja.comm.event.UpdateAccountEvent;
import com.xdja.comm.event.UpdateNickNameEvent;
import com.xdja.comm.zxing.creat.PopupWindowZxing;
import com.xdja.contact.util.BroadcastManager;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.presenter_mainframe.cmd.SettingCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.LogoutHelper;
import com.xdja.presenter_mainframe.presenter.activity.setting.DropMessagePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.NewsRemindPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.NoDisturbPresenter;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PresenterFragment;
import com.xdja.presenter_mainframe.ui.SettingFragmentView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetting;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;

/**
 * Created by ldy on 16/4/27.
 */
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class SettingFragementPresenter extends PresenterFragment<SettingCommand, VuSetting> implements SettingCommand {

    public Account account;

    @Inject
    @InteractorSpe(value = DomainConfig.GET_CURRENT_ACCOUNT_INFO)
    Lazy<Ext0Interactor<Account>> getCurrentAccountInfoUseCase;

    @Inject
    @InteractorSpe(DomainConfig.CHECK_NEW_VERSION)
    Lazy<Ext1Interactor<Context, Boolean>> checkUpdateUseCase;

//    @Inject
//    @InteractorSpe(value = DomainConfig.LOGOUT)
//    Lazy<Ext0Interactor<Void>> logout;

    @Inject
    BusProvider busProvider;

    @Inject
    LogoutHelper logoutHelper;

    @NonNull
    @Override
    protected Class<? extends VuSetting> getVuClass() {
        return SettingFragmentView.class;
    }

    @NonNull
    @Override
    protected SettingCommand getCommand() {
        return this;
    }

    private PopupWindowZxing zxing;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        if (getFragmentPostUseCaseComponent() != null) {
            getFragmentPostUseCaseComponent().inject(this);
        } else {
            LogUtil.getUtils().e("UseCaseComponent为空，重新进入LauncherPresenter！");
            return;
        }
        busProvider.register(this);

        getCurrentAccountInfo();
    }

    /**
     * 获取当前的账户信息
     */
    private void getCurrentAccountInfo(){
        getCurrentAccountInfoUseCase.get().fill().execute(new Action1<Account>() {
            @Override
            public void call(Account account) {
                SettingFragementPresenter.this.account = account;
                if (account.getAvatarId() != null)
                    getVu().setImage(account.getAvatarId(), account.getThumbnailId());
                getVu().setAccountInfo(account);
            }
        });
    }

    // [Strart] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
    /**
     * 检测是否有新版本
     */
    private void checkUpdateNew() {
        checkUpdateUseCase.get().fill(getActivity()).execute(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//                        boolean isShowNewView =
//                                SharePreferceUtil.getPreferceUtil(getActivity()).getIsShowNewView();
//                        if (isShowNewView) {
//                            getVu().freshUpdateNew(aBoolean);
//                        }
                        if (aBoolean != null) {
                            getVu().freshUpdateNew(aBoolean);
                        }
                        // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
                    }
                });
    }
    // [End] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.

    // [Start] modify by YangShaopeng<mailTo:ysp@xdja.com> on 2016-08-12. fix bug #2304 for check update.
    /**
     * 查看个人二维码信息
     */
    @Override
    public void viewZxing() {
        if (account == null) {
            return;
        }
        zxing= new PopupWindowZxing(getActivity(), "1#" + (!TextUtils.isEmpty(account.getAlias()) ?account.getAlias():account.getAccount()));
        zxing.showPopupWindow();
    }

    @Override
    public void onStop() {
        if(zxing!=null)
            zxing.hidePopupWindow();
        super.onStop();
    }
    // [End] Modify by YangShaopeng<mailto: ysp@xdja.com> on 2016-08-12. fix bug #2304. Review By Wangchao1.

    /**
     * 个人信息详情
     */
    @Override
    public void userDetail() {
        Navigator.navigateToUserDetail();
    }

    /**
     * 三方应用加密服务
     */
    @Override
    public void thirdpartService() {
        Navigator.navigateToEncryptServer();
    }

    /**
     * 新消息提醒
     */
    @Override
    public void newsRemind() {
        Intent intent = new Intent(getActivity(), NewsRemindPresenter.class);
        startActivity(intent);
    }

    /**
     * 勿扰模式
     */
    @Override
    public void noDistrub() {
        Intent intent = new Intent(getActivity(), NoDisturbPresenter.class);
        startActivity(intent);
    }

    /**
     * 聊天通话
     */
    @Override
    public void dropMessage() {
        Intent intent = new Intent(getActivity(), DropMessagePresenter.class);
        startActivity(intent);
    }

    /**
     * 修改安全口令
     */
    @Override
    public void changePassword() {

    }

    /**
     * 我的设备
     */
    @Override
    public void allDevices() {

    }

    /**
     * 关于安通+
     */
    @Override
    public void aboutSoft() {
        Navigator.navigateToAboutActoma();
    }


    /**
     * 关于芯片
     */
    @Override
    public void aboutChip() {
      //  Navigator.navigateToAboutChips();
    }

    /**
     * 选择多语言
     */
    @Override
    public void choiceLanguage() {
        Navigator.navigateToChoiceLanguage();
    }

    /**
     * 账号与安全
     */
    @Override
    public void accountSafe() {
        Navigator.navigateToAccountSafe();
    }

    @Override
    public void exitToLogin(final int type) {
        //[S]modify by lixiaolong on 20160909. fix bug 3855. review by wangchao1.
        if (type == SettingFragmentView.EXIT_TYPE_CLOSE) {
            BroadcastManager.sendBroadcastCloseTransfer();//关闭加密通道
        //[E]modify by lixiaolong on 20160909. fix bug 3855. review by wangchao1.
            //弹出所有界面
            ActivityStack.getInstanse().exitApp();
//            Intent intent = new Intent(getContext(), EndActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            getContext().startActivity(intent);
        } else if (type == SettingFragmentView.EXIT_TYPE_EXIT){

//            getVu().showCommonProgressDialog("正在退出...");
            logoutHelper.navigateToLoginWithExit();
            logoutHelper.logout(null);
            //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-09 add. fix bug 1546 . review by wangchao1. Start
            ((NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
            //ysp@xdja.com<mailto://ysp@xdja.com> 2016-08-09 add. fix bug 1546 . review by wangchao1. End
        }
    }

    /**+
     * 修改昵称事件
     *
     * @param event 事件对象
     */
    @Subscribe
    public void updateNickName(UpdateNickNameEvent event) {
        getVu().setNickName(event.getNewNickName());
    }

    /**
     * 修改账号事件
     *
     * @param event
     */
    @Subscribe
    public void updateAccount(UpdateAccountEvent event) {
        getVu().setAccount(event.getNewAccount());
    }

    /**
     * 修改头像事件
     * @param event
     */
    @Subscribe
    public void updateImage(ImgBeanEvent event){
        Bitmap bitmap = event.getBitmap();
        if (bitmap != null){
            getVu().setUserImageBackground(bitmap);
        }
        getCurrentAccountInfo();
    }

    // [Strart] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.
    /**
     * 是否显示有版本的红色NEW标识
     * @param event
     */
    @Subscribe
    public void freshUpdate(FreshUpdateNewEvent event) {
        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
//        boolean isShowNewView = SharePreferceUtil.getPreferceUtil(getActivity()).getIsShowNewView();
//        if (isShowNewView) {
//            getVu().freshUpdateNew(event.isHaveUpdate());
//        }
        if (event != null) {
            getVu().freshUpdateNew(event.isHaveUpdate());
        }
        // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-11. for check update.
    }
    // [End] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-09. add update prompt. Review By Wangchao1.

    @Override
    public void onDestroy() {
        super.onDestroy();
        //modify by alh@xdja.com to fix bug: 539 2016-06-22 start (rummager : wangchao1)
        if (busProvider != null)
            busProvider.unregister(this);
        //modify by alh@xdja.com to fix bug: 539 2016-06-22 end (rummager : wangchao1)
    }

    @Override
    public void onResume() {
        super.onResume();
        // [Strart] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-11. add update prompt. Review By Wangchao1.
        //刷新是否显示有版本的红色NEW标识
        checkUpdateNew();
        // [End] Modify by LiXiaolong<mailto: lxl@xdja.com> on 2016-08-11. add update prompt. Review By Wangchao1.
        //刷新界面信息
//        getCurrentAccountInfo();
    }
}
