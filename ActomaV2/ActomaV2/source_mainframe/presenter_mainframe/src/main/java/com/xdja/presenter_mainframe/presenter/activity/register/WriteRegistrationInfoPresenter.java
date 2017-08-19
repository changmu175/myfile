package com.xdja.presenter_mainframe.presenter.activity.register;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.xdja.comm.event.UpdateImgBitmap;
import com.xdja.comm.uitl.UniversalUtil;
import com.xdja.dependence.event.BusProvider;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.usecase.userInfo.UploadImgUseCase;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.frame.domain.usecase.Ext4Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.WriteRegistrationInfoCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.WriteRegistrationInfoView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuWriteRegistrationInfo;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

@InjectOption.Options(InjectOption.OPTION_PRECACHEDUSER)
public class WriteRegistrationInfoPresenter extends PresenterActivity<WriteRegistrationInfoCommand, VuWriteRegistrationInfo> implements WriteRegistrationInfoCommand {
    /**
     * 成功
     */
    public final static int RESULT_STATUS_SUCCESS = 0;
    /**
     * 注册账号次数超过当天最大限制
     */
    public final static int RESULT_STATUS_LIMIT_DAY = 1;
    /**
     * 注册账号超过当月最大限制
     */
    public final static int RESULT_STATUS_LIMIT_MONTH = 2;
    /**
     * 注册账号超过当年最大限制
     */
    public final static int RESULT_STATUS_LIMIT_YEAR = 3;


    /**
     * 当天最大次数
     */
    public final static String DAY_COUNT = "dayCount";
    /**
     * 当月最大次数
     */
    public final static String MONTH_COUNT = "monthCount";
    /**
     * 当年最大次数
     */
    public final static String YEAR_COUNT = "yearCount";


    @Inject
    BusProvider busProvider;

    @Inject
    @InteractorSpe(value = DomainConfig.ACCOUNT_REGIST)
    Lazy<Ext4Interactor<String, String, String, String, MultiResult<String>>> accountRegistUseCase;

    @Inject
    @InteractorSpe(value = DomainConfig.UPLOAD_IMG)
    Lazy<Ext1Interactor<Bitmap, Map<String, String>>> uploagImgUseCase;

    private String avatarId;
    private String thumbnailId;

    @NonNull
    @Override
    protected Class<? extends VuWriteRegistrationInfo> getVuClass() {
        return WriteRegistrationInfoView.class;
    }

    @NonNull
    @Override
    protected WriteRegistrationInfoCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        this.getActivityPreUseCaseComponent().inject(this);
        busProvider.register(this);
    }

    @Override
    public void next(String nickName, final String password, String passwordAgain) {
        if (!password.equals(passwordAgain)) {
            getVu().showToast(getString(R.string.different_two_password));
            return;
        }
        if (!TextUtil.isRulePassword(password)) {
            getVu().showToast(getString(R.string.password_format));
            return;
        }
        if (TextUtils.isEmpty(nickName)) {
            nickName = null;
        } else {
            if (!TextUtil.isRuleNickname(nickName)) {
                getVu().showToast(getString(R.string.nickname_format));
                return;
            }
        }
        executeInteractorNoRepeat(accountRegistUseCase.get().fill(nickName, password, avatarId, thumbnailId),
                new LoadingDialogSubscriber<MultiResult<String>>(this,this) {
                    @Override
                    public void onNext(MultiResult<String> stringMultiResult) {
                        super.onNext(stringMultiResult);
                        String toastMsg = null;
                        switch (stringMultiResult.getResultStatus()) {
                            case RESULT_STATUS_SUCCESS:
                                //// TODO: 16/4/26 跳转到选取账号界面
                                Navigator.navigateToChooseAccount(
                                        stringMultiResult.getInfo().get("account"),
                                        stringMultiResult.getInfo().get(Navigator.INNER_AUTH_CODE),
                                        password);
                                break;
                            case RESULT_STATUS_LIMIT_DAY:

                                toastMsg = getString(R.string.register_count) + stringMultiResult.getInfo().get(DAY_COUNT) + getString(R.string.count);
                                break;
                            case RESULT_STATUS_LIMIT_MONTH:
                                toastMsg = getString(R.string.register_count_month) + stringMultiResult.getInfo().get(MONTH_COUNT) + getString(R.string.count);
                                break;
                            case RESULT_STATUS_LIMIT_YEAR:
                                toastMsg = getString(R.string.register_count_year) + stringMultiResult.getInfo().get(YEAR_COUNT) + getString(R.string.count);
                                break;
                        }
                        if (toastMsg != null)
                            getVu().showToast(toastMsg);
                    }
                }.registerLoadingMsg(getString(R.string.register)));
    }

    @Override
    public void atTerms() {
        //modify by xienana for multi language change @20161125 review by self
        String uri = "file:///android_asset/law.html";
        String result_uri = UniversalUtil.changeLanLocalWebUrl(this,uri);
        Navigator.navigateToWebView(result_uri, null);
        //modify by xienana for multi language change @20161125 review by self
    }

    @Override
    public void avatarClicked() {
        Navigator.navigateToSetHeadPortrait();
    }

    @Subscribe
    public void updateImage(final UpdateImgBitmap updateImgBitmap) {
        LogUtil.getUtils().e("收到截图");
        executeInteractorNoRepeat(uploagImgUseCase.get().fill(updateImgBitmap.getBm()),
                new LoadingDialogSubscriber<Map<String, String>>(this, this) {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(Map<String, String> stringStringMap) {
                        super.onNext(stringStringMap);
                        getVu().showUserImage(updateImgBitmap.getBm());
                        avatarId = stringStringMap.get(UploadImgUseCase.AVATAR_ID);
                        thumbnailId = stringStringMap.get(UploadImgUseCase.THUMBNAIL_ID);
                        getVu().loadUserImage(avatarId);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        // 2016/06/22/李晓龙【BUG#570 - 注册页面上传头像成功后无toast提示】
                        getVu().showToast(R.string.upload_user_avatar_success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }.registerLoadingMsg(getString(R.string.upload)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.unregister(this);
    }

}
