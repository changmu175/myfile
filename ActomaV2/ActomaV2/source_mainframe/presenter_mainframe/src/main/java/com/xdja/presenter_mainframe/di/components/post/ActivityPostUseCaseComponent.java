package com.xdja.presenter_mainframe.di.components.post;

import com.xdja.dependence.annotations.PerActivity;
import com.xdja.frame.di.modules.ActivityModule;
import com.xdja.presenter_mainframe.di.modules.SubActivityModule;
import com.xdja.presenter_mainframe.presenter.activity.FeedBackPresenter;
import com.xdja.presenter_mainframe.presenter.activity.MainFramePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AboutActomaPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AccountSafePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AuthAccountLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.AuthDeviceLoginPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.BindPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.DeviceManagerPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.DropMessagePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.ModifyPhoneNumberPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.NewsRemindPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.NoDisturbPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.OpenGesturePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.OpenSafeLockPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SetAccountPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SetNicknamePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SettingGesturePresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SettingInputNewPasswordPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.SettingSafeLockPresenter;
import com.xdja.presenter_mainframe.presenter.activity.setting.UserDetailPresenter;

import dagger.Subcomponent;

/**
 * Created by xdja-fanjiandong on 2016/3/29.
 */
@PerActivity
@Subcomponent(
        modules = {ActivityModule.class, SubActivityModule.class}
)
public interface ActivityPostUseCaseComponent {

    void inject(UserDetailPresenter userDetailPresenter);

    void inject(SetNicknamePresenter setNicknamePresenter);

    void inject(SetAccountPresenter setAccountPresenter);

    void inject(BindPhoneNumberPresenter bindPhoneNumberPresenter);

    void inject(DeviceManagerPresenter deviceManagerPresenter);
    void inject(SettingInputNewPasswordPresenter settingInputNewPasswordPresenter);
    void inject(AuthDeviceLoginPresenter authDeviceLoginPresenter);
    void inject(ModifyPhoneNumberPresenter modifyPhoneNumberPresenter);
    void inject(AuthAccountLoginPresenter authAccountLoginPresenter);

    void inject(AccountSafePresenter accountSafePresenter);

    void inject(MainFramePresenter mainFramePresenter);

    void inject(NewsRemindPresenter newsRemindPresenter);

    void inject(DropMessagePresenter dropMessagePresenter);

    void inject(NoDisturbPresenter noDisturbPresenter);
    void inject(OpenSafeLockPresenter openSafeLockPresenter);
    void inject(SettingSafeLockPresenter settingSafeLockPresenter);
    void inject(OpenGesturePresenter openGesturePresenter);
    void inject(SettingGesturePresenter settingGesturePresenter);
    void inject(AboutActomaPresenter aboutActomaPresenter);
    void inject(FeedBackPresenter deviceManagerPresenter);
}
