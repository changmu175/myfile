package com.xdja.imp.domain.interactor.mx;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SaveDraft;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_domain.R;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:保存草稿用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.mx</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/19</p>
 * <p>Time:11:37</p>
 */
public class SaveDraftUseCase extends MxUseCase<Boolean> implements SaveDraft {
    /**
     * 会话对象
     */
    private String talkerId;
    /**
     * 草稿
     */
    private CharSequence draft;
    /**
     * 草稿时间
     */
    private long draftTime;

    @Inject
    public SaveDraftUseCase(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public SaveDraft save(@Nullable String talkerId, CharSequence draft, long draftTime) {
        this.talkerId = talkerId;
        this.draft = draft;
        this.draftTime = draftTime;
        return this;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        if (TextUtils.isEmpty(talkerId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_talker_id_null)));
        }

        return userOperateRepository.saveDraft2Local(this.talkerId, this.draft.toString(), this.draftTime);
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }
}
