package com.xdja.imp.domain.interactor.mx;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.DeleteNoDisturbSetting;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_domain.R;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:删除勿扰模式用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/16</p>
 * <p>Time:15:55</p>
 */
public class DeleteNoDisturbSettingUseCase extends MxUseCase<Boolean> implements DeleteNoDisturbSetting {

    private String talkerId;
    private int sessionType;

    @Inject
    public DeleteNoDisturbSettingUseCase(ThreadExecutor threadExecutor,
                                         PostExecutionThread postExecutionThread,
                                         UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread,userOperateRepository);
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {

        if (TextUtils.isEmpty(talkerId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_delete_nodisturb_talker_null)));
        }

        if (sessionType != ConstDef.NODISTURB_SETTING_SESSION_TYPE_SINGLE
                && sessionType != ConstDef.NODISTURB_SETTING_SESSION_TYPE_GROUP) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_delete_nodisturb_type_error)));
        }


        return userOperateRepository
                .deleteNoDisturbAtCloud(talkerId, sessionType)
                .flatMap(
                        new Func1<Boolean, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Boolean aBoolean) {
                                return userOperateRepository
                                        .deleteNoDisturbAtLocal(talkerId);
                            }
                        }
                );
    }

    @Override
    public DeleteNoDisturbSetting delete(@Nullable String talkerId,
                                         @ConstDef.NoDisturbSettingSessionType int sessionType) {
        this.talkerId = talkerId;
        this.sessionType = sessionType;
        return this;
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }
}
