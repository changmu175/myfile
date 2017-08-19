package com.xdja.presenter_mainframe.di.components.pre;

import com.xdja.dependence.annotations.PerActivity;
import com.xdja.frame.di.modules.ActivityModule;
import com.xdja.presenter_mainframe.chooseImg.CutImagePresenter;
import com.xdja.presenter_mainframe.di.modules.SubActivityModule;
import com.xdja.presenter_mainframe.presenter.activity.NewEncryptPresenter;
import com.xdja.presenter_mainframe.presenter.activity.ProductIntroductionAnimPresenter;
import com.xdja.presenter_mainframe.presenter.activity.SplashPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.DataMigrationAccountPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.DataMigrationFixPwdPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.EmpowerDeviceLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.LoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.MessageVerifyLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.VerifyFriendPhonePresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.VerifyLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.login.VerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.ChooseAccountPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.RegisterVerifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.SetPreAccountPresenter;
import com.xdja.presenter_mainframe.presenter.activity.register.WriteRegistrationInfoPresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPasswordVerifyFriendPhonePresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPasswordVerifyPhonePresenter;
import com.xdja.presenter_mainframe.presenter.activity.resetPassword.ResetPwdInputNewPasswordPresenter;

import dagger.Subcomponent;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@PerActivity
@Subcomponent(
        modules = {ActivityModule.class, SubActivityModule.class}
)
public interface ActivityPreUseCaseComponent {
    void inject(SplashPresenter launcherPresenter);

    void inject(ProductIntroductionAnimPresenter productIntroductionAnimPresenter);

    void inject(CutImagePresenter cutImagePresenter);

    void inject(SetPreAccountPresenter setPreAccountPresenter);

    //
    void inject(RegisterVerifyPhoneNumberPresenter registerVerifyPhoneNumberPresenter);

    //
    void inject(LoginPresenter loginPresenter);

    //
    void inject(EmpowerDeviceLoginPresenter empowerDeviceLoginPresenter);

    //
    void inject(VerifyFriendPhonePresenter verifyFriendPhonePresenter);

    //
    void inject(VerifyLoginPresenter verifyLoginPresenter);

    //
    void inject(VerifyPhoneNumberPresenter verifyPhoneNumberPresenter);

    //
    void inject(WriteRegistrationInfoPresenter writeRegistrationInfoPresenter);

    //
    void inject(ChooseAccountPresenter chooseAccountPresenter);

    //
    void inject(MessageVerifyLoginPresenter messageVerifyLoginPresenter);

    //
    void inject(ResetPasswordVerifyPhonePresenter resetPasswordVerifyPhonePresenter);

    //
    void inject(ResetPwdInputNewPasswordPresenter inputNewPasswordPresenter);

    void inject(ResetPasswordVerifyFriendPhonePresenter inputNewPasswordPresenter);

    void inject(DataMigrationFixPwdPresenter dataMigrationFixPwdPresenter);

    void inject(DataMigrationAccountPresenter dataMigrationAccountPresenter);

    void inject(NewEncryptPresenter newEncryptPresenter);
//
}
