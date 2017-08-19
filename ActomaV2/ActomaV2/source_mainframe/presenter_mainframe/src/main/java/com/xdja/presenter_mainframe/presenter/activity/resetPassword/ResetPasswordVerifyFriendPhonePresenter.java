package com.xdja.presenter_mainframe.presenter.activity.resetPassword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.dependence.exeptions.OkException;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.frame.domain.usecase.Ext2Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.ResetPasswordVerifyFriendPhoneCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ResetPasswordVerifyFriendPhoneView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuResetPasswordVerifyFriendPhone;
import com.xdja.presenter_mainframe.widget.FillInMessage.FillInMessageView;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class ResetPasswordVerifyFriendPhonePresenter extends
        PresenterActivity<ResetPasswordVerifyFriendPhoneCommand, VuResetPasswordVerifyFriendPhone>
        implements ResetPasswordVerifyFriendPhoneCommand {

    /**
     * 成功
     */
    private static final int RESULT_STATUS_SUCCESS = 0;
    /**
     * 验证失败次数达到上线
     */
    private static final int RESULT_STATUS_LIMIT_TIMES = 1;
    public static final String SURPLUS_TIMES = "surplusTimes";
    public static final String TIMES = "times";

    @Inject
    @InteractorSpe(value = DomainConfig.AUTH_FRIEND_PHONE)
    Lazy<Ext2Interactor<String, List<String>, MultiResult<Object>>> authFriendPhoneUseCase;

    @NonNull
    @Override
    protected Class<? extends VuResetPasswordVerifyFriendPhone> getVuClass() {
        return ResetPasswordVerifyFriendPhoneView.class;
    }

    @NonNull
    @Override
    protected ResetPasswordVerifyFriendPhoneCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);

    }

    @Override
    public boolean handleOkException(@Nullable String okCode, @Nullable String userMsg, @Nullable OkException ex, @Nullable String mark) {
        if (okCode == null) {
            return true;
        }
//        if (okCode.equals(ServerException.ACCOUNT_NOT_EXISTS)) {
//            getVu().showToast("未知原因，操作失败（404）");
//        }
        return true;
    }

    /**
     * 完成按钮的点击事件
     *
     * @param stringList {@link FillInMessageView}中的所有inputView的字符串集合
     */
    @Override
    public void complete(List<String> stringList) {
        final String account = stringList.get(0);
        //modify by alh@xdja.com to fix bug: 947 2016-06-29 start (rummager : null)
        if (TextUtils.isEmpty(account)){
            getVu().showToast(getString(R.string.please_write_at_account));
            return;
        }
        List<String> friendPhoneList = stringList.subList(1, 4);
        for (int i = 0, n = friendPhoneList.size(); i < n; i++) {
            if (TextUtils.isEmpty(friendPhoneList.get(i))){
                getVu().showToast(getString(R.string.friend_hint, i + 1));
                return;
            }
        }
        //modify by alh@xdja.com to fix bug: 947 2016-06-29 end (rummager : null)
        executeInteractorNoRepeat(authFriendPhoneUseCase.get().fill(account, friendPhoneList),
                new LoadingDialogSubscriber<MultiResult<Object>>(this,this) {
                    @SuppressWarnings("NumericCastThatLosesPrecision")
                    @Override
                    public void onNext(MultiResult<Object> objectMultiResult) {
                        super.onNext(objectMultiResult);
                        switch (objectMultiResult.getResultStatus()) {
                            case RESULT_STATUS_SUCCESS:
                                String innerAuthCode = (String) objectMultiResult.getInfo().get(Navigator.INNER_AUTH_CODE);
                                Navigator.navigateToResetPwdInputNewPasswordByFriendPhone(account, innerAuthCode);
                                break;
                            case RESULT_STATUS_LIMIT_TIMES:
                                int surplusTimes = (int) (double) objectMultiResult.getInfo().get(SURPLUS_TIMES);
                                int times = (int) (double) objectMultiResult.getInfo().get(TIMES);
                                if (surplusTimes > 0) {
                                    getVu().showAuthFailDialog(String.format(getString(R.string.verify_fail_times),surplusTimes));
                                } else {
                                    getVu().showAuthFailDialog(String.format(getString(R.string.verify_fail_over),times));
                                }
                                break;
                        }
                    }
                }.registerLoadingMsg(getString(R.string.verify)));
    }
}
