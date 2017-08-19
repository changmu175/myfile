package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.xdja.comm.event.UpdateBindMobileEvent;
import com.xdja.comm.server.SettingServer;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.exeptions.ServerException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.frame.data.persistent.PreferencesUtil;
import com.xdja.frame.domain.usecase.Ext0Interactor;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AccountSafeCommand;
import com.xdja.presenter_mainframe.global.obs.UnBindDeviceObservable;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.PerSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.AccountSafeView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuAccountSafe;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.functions.Action1;

@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class AccountSafePresenter extends PresenterActivity<AccountSafeCommand,VuAccountSafe> implements AccountSafeCommand {

    //是否是第一次或者清空数据进入安通+，安全锁的设置
    public final static String IS_SAFE_LOCK = "-1";
    private static final String IS_FORGET_PWD = "-2";
    @Inject
    @InteractorSpe(value = DomainConfig.GET_CURRENT_ACCOUNT_INFO)
    Lazy<Ext0Interactor<Account>> getCurrentAccountInfoUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.AUTH_PASSWD)
    Lazy<Ext1Interactor<String,Void>> authPasswordUseCase;

    @Inject
    BusProvider busProvider;

    @Inject
    PreferencesUtil preferencesUtil;

    public Account account;

    @NonNull
    @Override
    protected Class<? extends VuAccountSafe> getVuClass() {
        return AccountSafeView.class;
    }

    @NonNull
    @Override
    protected AccountSafeCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
            busProvider.register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //modify by alh@xdja.com to fix bug: 573 2016-06-27 start (rummager : wangchao1)
        getCurrentAccountInfoUseCase.get().fill().execute(new Action1<Account>() {
            @Override
            public void call(Account account) {
                AccountSafePresenter.this.account = account;
                getVu().setAccountIsModify(!isHaveAlias());

                //填充信息
                if (isHaveAlias()){
                    getVu().setAccount(account.getAlias());
                }else {
                    getVu().setAccount(account.getAccount());
                }
                if (isHaveMobile())
                    getVu().setMobile(account.getMobiles().get(0));

                //fix bug 7351 by licong for safeLock
                //添加安全锁的状态，当第一次进入或者忘记密码，进来都是未设置状态
                if (SettingServer.getSafeLock().equals(IS_FORGET_PWD) || SettingServer.getSafeLock().equals(IS_SAFE_LOCK)) {
                    getVu().setSafeLockState(getString(R.string.safe_no_setting));
                } else if (SettingServer.getSafeLock().equals("true")) {
                    getVu().setSafeLockState(getString(R.string.safe_open));
                } else {
                    getVu().setSafeLockState(getString(R.string.safe_off));
                }

            }
        });
        //modify by alh@xdja.com to fix bug: 573 2016-06-27 end (rummager : wangchao1)
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void onReceiveUnbindMobileEvent(UnBindDeviceObservable.UnBindMobileEvent event) {
        getCurrentAccountInfoUseCase.get().fill().execute(new Action1<Account>() {
            @Override
            public void call(Account account) {
                AccountSafePresenter.this.account = account;
                if (isHaveMobile()) {
                    getVu().setMobile(account.getMobiles().get(0));
                } else {
                    getVu().setMobile("");
                }
            }
        });
    }

    /**
     * 是否有自定义账号
     * @return
     */
    public boolean isHaveAlias(){
        if (!TextUtils.isEmpty(account.getAlias())){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 是否有绑定的手机号
     * @return
     */
    private boolean isHaveMobile() {
        if (account == null)
            return false;
        List<String> mobiles = account.getMobiles();
        if (mobiles == null || mobiles.isEmpty()){
            return false;
        }else{
            //判断内容是否为空
            String mobile = mobiles.get(0);
            if (TextUtils.isEmpty(mobile)){
                return false;
            }
            return true;
        }
    }


    @Override
    public void setActomaAccount() {
        if (!isHaveAlias()){
            Navigator.navigateToSetAccount();
        }
        getVu().setAccountIsModify(!isHaveAlias());
    }

    @Override
    public void setPhoneNumber() {
        if (isHaveMobile()){
            Navigator.navigateToModifyPhoneNumber(account.getMobiles().get(0));
        }else {
            Navigator.navigateToBindPhoneNumber(BindPhoneNumberPresenter.CURRENT_TYPE,
                    BindPhoneNumberPresenter.BIND_PHONE_TYPE);
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
                AccountSafePresenter.this.account = account;
                if (isHaveMobile()) {
                    getVu().setMobile(account.getMobiles().get(0));
                } else {
                    getVu().setMobile("");
                }
            }
        });
    }

    /**
     * 跳转到修改密码界面
     */
    public void modifyPassword() {
        Navigator.navigateToSettingInputNewPassword();
    }

    @Override
    public void loginDeviceManager() {
        Navigator.navigateToDeviceManager();
    }

    /**
     * 校验密码是否正确
     * @param password 密码
     */
    @Override
    public void checkPassword(String password) {
        addInteractor2Queue(authPasswordUseCase.get()).fill(password)
                .execute(new PerSubscriber<Void>(this){
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        getVu().dismissDialog();
                        modifyPassword();
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getVu().clearPasswordWithDialog();
                        //[S]modify by xienana for click more than once caused problem @2016/09/26 [review by] tangsha
                        getVu().setEnableClick(true);
                        //[E]modify by xienana for click more than once caused problem @2016/09/26 [review by] tangsha
                    }
                }.registUserMsg(ServerException.class,ServerException.ACCOUNT_OR_PWD_ERROR,getString(R.string.pwd_error)));
    }

    /**
     * 跳到安全锁设置界面
     */
    @Override
    public void setSafeLock() {
        Navigator.navigateToSettingInputSafeLock(SettingServer.getSafeLock().equals(IS_SAFE_LOCK),SettingServer.getSafeLock().equals(IS_FORGET_PWD));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (busProvider != null) {
            busProvider.unregister(this);
        }
    }

    //add  by licong ,安全锁忘记密码按返回键
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(this,SettingActivityPresenter.class);
//            startActivity(intent);
//            finish();
//            overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
//            return true;
//        }
//        return false;
//    }

}

