package com.xdja.presenter_mainframe.presenter.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.frame.domain.usecase.Ext3Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ChooseAccountCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ChooseAccountView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuChooseAccount;
import com.xdja.presenter_mainframe.util.ErrorUtil;

import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class ChooseAccountPresenter extends PresenterActivity<ChooseAccountCommand, VuChooseAccount> implements ChooseAccountCommand {

    public static final String NEW_ACCOUNT = "newAccount";
    public static final String SURPLUS_TIMES = "surplusTimes";
    /**
     * 成功
     */
    private final int RESULT_STATUS_SUCCESS = 0;
    /**
     * 获取账号次数超限
     */
    private final int RESULT_STATUS_LIMIT_TIMES = 1;

    /**
     * 第一次生成的账号
     */
    private String account;
    /**
     * 新生成的账号
     */
    private String newAccount;

    /**
     * 内部验证码
     */
    private String innerAuthCode;

    /**
     * 是否自定义账号
     */
    private boolean isCustomaAccount = false;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_REOBTAIN)
    Lazy<Ext2Interactor<String, String, MultiResult<Object>>> reObtainAccountUseCase;

    @Inject
    @InteractorSpe(DomainConfig.ACCOUNT_MODIFY)
    Lazy<Ext3Interactor<String, String, String, Void>> modifyAccountUseCase;

    @Inject
    Intent intent;
    private String password;

    @NonNull
    @Override
    protected Class<? extends VuChooseAccount> getVuClass() {
        return ChooseAccountView.class;
    }

    @NonNull
    @Override
    protected ChooseAccountCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        setDataFromIntent();
        getVu().setAtAccount(account);
    }


    private void setDataFromIntent() {
        this.account = this.intent.getStringExtra(Navigator.ACCOUNT);
        //初始时设置新帐号和老账号拥有相同的值
        this.newAccount = new String(this.account);
        this.innerAuthCode = this.intent.getStringExtra(Navigator.INNER_AUTH_CODE);
        this.password = this.intent.getStringExtra(Navigator.PASSWORD);
    }

    @Override
    public void switchOther() {
        executeInteractorNoRepeat(reObtainAccountUseCase.get().fill(account, innerAuthCode),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this) {
                    @Override
                    public void onNext(MultiResult<Object> result) {
                        super.onNext(result);
                        if (result != null) {
                            if (result.getResultStatus() == RESULT_STATUS_SUCCESS) {
                                Map<String, Object> info = result.getInfo();
                                if (info != null) {
                                    Object newAccountObj = info.get(NEW_ACCOUNT);
                                    if (newAccountObj != null) {
                                        ChooseAccountPresenter.this.newAccount = newAccountObj.toString();
                                        getVu().setAtAccount(newAccountObj.toString());
                                        getVu().modifyCertainButton(getString(R.string.this_the));
                                    }
                                    Object newInnerAuthCodeObj = info.get(Navigator.INNER_AUTH_CODE);
                                    if (newInnerAuthCodeObj != null) {
                                        ChooseAccountPresenter.this.innerAuthCode = newInnerAuthCodeObj.toString();
                                    }
                                    Double surplusTimesObj = (Double) info.get(SURPLUS_TIMES);
                                    int surplusTimes = surplusTimesObj.intValue();
                                    if (surplusTimes < 5) {
                                        getVu().showToast(getString(R.string.remaining_selected_opportunities , surplusTimes));
                                    }
                                }
                            } else if (result.getResultStatus() == RESULT_STATUS_LIMIT_TIMES) {
                                getVu().showToast(getString(R.string.no_selected_opportunities));
                            }
                        }

                    }
                }.registerLoadingMsg(getString(R.string.get_account)));
    }

    @Override
    public void setAccountBySelf() {
        Navigator.navigateToSetPreAccount(account, innerAuthCode, Navigator.REQUEST_SET_ACCOUNT);
    }

    @Override
    public void next() {

        //如果自定义账号成功，或者用户未点击“换一个”更改账号，点击“下一步”直接进入手机号绑定页面
        if (this.isCustomaAccount || this.account.equals(this.newAccount)) {
            //注意：此处传入的帐号为老账号
            Navigator.navigateToRegisterVerifyPhoneNumber(account, innerAuthCode, password, false);
            return;
        }
        //如果未自定义账号，并且更换了账号，确认生成的新账号
        executeInteractorNoRepeat(modifyAccountUseCase.get()
                .fill(this.account, this.newAccount, this.innerAuthCode),
                new LoadingDialogSubscriber<Void>(this,this) {
                    @Override
                    public void onNext(Void aVoid) {
                        super.onNext(aVoid);
                        getVu().removeSetAccountSelfText();
                        //注意：此处传入的是新帐号
                        Navigator.navigateToRegisterVerifyPhoneNumber(newAccount, innerAuthCode, password, false);
                        //标识该业务只能执行一次（配合142行的判断语句），返回到该页面后，该业务就不能再执行了
                        ChooseAccountPresenter.this.account = ChooseAccountPresenter.this.newAccount;
                    }
                }.registerLoadingMsg(getString(R.string.change_account)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Navigator.REQUEST_SET_ACCOUNT) {
            if (data != null) {
                String customedAccount = data.getStringExtra(Navigator.ACCOUNT);
                if (!TextUtils.isEmpty(customedAccount)) {
                    //标识用户自定义过帐号
                    this.isCustomaAccount = true;
                    getVu().setAtAccount(customedAccount);
                    getVu().removeSetAccountSelfText();
                    this.account = customedAccount;
                    Navigator.navigateToRegisterVerifyPhoneNumber(customedAccount, innerAuthCode, password,false);
                }
            }
        }

    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        ErrorUtil.registerPop2First(okCode);
        return true;
    }
}
