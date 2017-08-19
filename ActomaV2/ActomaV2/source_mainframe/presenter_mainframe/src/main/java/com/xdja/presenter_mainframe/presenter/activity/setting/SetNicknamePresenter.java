package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xdja.comm.event.UpdateNickNameEvent;
import com.xdja.dependence.event.BusProvider;
import com.xdja.domain_mainframe.di.annotations.DomainConfig;
import com.xdja.domain_mainframe.di.annotations.InteractorSpe;
import com.xdja.frame.domain.usecase.Ext1Interactor;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.SetNicknameCommand;
import com.xdja.presenter_mainframe.navigation.Navigator;
import com.xdja.presenter_mainframe.presenter.base.InjectOption;
import com.xdja.presenter_mainframe.presenter.base.LoadingDialogSubscriber;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.SetNicknameView;
import com.xdja.presenter_mainframe.ui.uiInterface.VuSetNickname;
import com.xdja.presenter_mainframe.util.TextUtil;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;
@InjectOption.Options(InjectOption.OPTION_POSTCACHEDUSER)
public class SetNicknamePresenter extends PresenterActivity<SetNicknameCommand,VuSetNickname> implements SetNicknameCommand {

    @Inject
    @InteractorSpe(value = DomainConfig.MODIFY_NIKE_NAME)
    Lazy<Ext1Interactor<String, Map<String, String>>> modifyNickNameUseCase;

    @Inject
    BusProvider busProvider;

    @NonNull
    @Override
    protected Class<? extends VuSetNickname> getVuClass() {
        return SetNicknameView.class;
    }

    @NonNull
    @Override
    protected SetNicknameCommand getCommand() {
        return this;
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (getActivityPostUseCaseComponent() != null) {
            getActivityPostUseCaseComponent().inject(this);
        }

        String nickName = getIntent().getStringExtra(Navigator.NICK_NAME);
        getVu().setNickName(nickName);
    }

    @Override
    public void complete(List<String> stringList) {
        final String nickname = stringList.get(0).trim();
        if (!TextUtil.isRuleNickname(nickname)){
            getVu().showToast(getString(R.string.nickname_format));
            return;
        }
        executeInteractorNoRepeat(modifyNickNameUseCase.get().fill(nickname),
                new LoadingDialogSubscriber<Map<String, String>>(this,this) {
                    @Override
                    public void onNext(Map<String, String> stringStringMap) {
                        super.onNext(stringStringMap);

                        UpdateNickNameEvent event = new UpdateNickNameEvent();
                        event.setNewNickName(nickname);
                        busProvider.post(event);

                        getVu().showToast(getString(R.string.set_nickname_succeed));
                        getVu().dismissCommonProgressDialog();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getVu().dismissCommonProgressDialog();
                    }
                }.registerLoadingMsg(getString(R.string.title_setting)));
    }

//    /**
//     * 更新昵称的事件
//     */
//    public static class UpdateNickNameEvent {
//        private String newNickName;
//
//        public String getNewNickName() {
//            return newNickName;
//        }
//
//        public void setNewNickName(String newNickName) {
//            this.newNickName = newNickName;
//        }
//    }
}
