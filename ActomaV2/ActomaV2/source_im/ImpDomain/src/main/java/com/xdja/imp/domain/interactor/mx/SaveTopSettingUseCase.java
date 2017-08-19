package com.xdja.imp.domain.interactor.mx;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SaveTopSetting;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_domain.R;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:保存置顶设置用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.mx</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/19</p>
 * <p>Time:11:37</p>
 */
public class SaveTopSettingUseCase extends MxUseCase<Boolean> implements SaveTopSetting {
    /**
     * 会话对象
     */
    private String talkerId;
    /**
     * 是否置顶
     */
    private boolean isTop;

    @Inject
    public SaveTopSettingUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public SaveTopSetting save(@NonNull String talkerId,boolean isTop) {
        this.talkerId = talkerId;
        this.isTop = isTop;
        return this;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        if (TextUtils.isEmpty(talkerId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_talker_id_null)));
        }

        return this.userOperateRepository
                .addSettingTop2Cloud(talkerId, isTop)
                .flatMap(
                        new Func1<Boolean, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Boolean aBoolean) {
                                return userOperateRepository
                                        .addSettingTop2Local(talkerId);
                            }
                        }
                );
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }
}
