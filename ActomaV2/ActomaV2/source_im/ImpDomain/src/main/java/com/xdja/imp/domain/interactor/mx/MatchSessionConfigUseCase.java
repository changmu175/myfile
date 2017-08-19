package com.xdja.imp.domain.interactor.mx;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.MatchSessionConfig;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_domain.R;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.mx</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/4</p>
 * <p>Time:14:51</p>
 */
public class MatchSessionConfigUseCase extends MxUseCase<List<TalkListBean>> implements MatchSessionConfig {

    List<SessionConfig> configs;

    List<TalkListBean> talkListBeans;

    @Inject
    public MatchSessionConfigUseCase(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public Observable<List<TalkListBean>> buildUseCaseObservable() {

        if (configs == null || talkListBeans == null) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_parameter_illegal_mismatch_conversation_setting)));
        }


        return Observable.from(talkListBeans)
                .flatMap(
                        new Func1<TalkListBean, Observable<TalkListBean>>() {
                            @Override
                            public Observable<TalkListBean> call(TalkListBean talkListBean) {
                                for (SessionConfig config : configs) {
                                    if (config != null && talkListBean != null) {
                                        if (talkListBean.getTalkFlag().equals(config.getFlag())) {
                                            talkListBean.setShowOnTop(config.isTop());
                                            if (TextUtils.isEmpty(config.getDraft())) {
                                                talkListBean.setDraft("");
                                                talkListBean.setHasDraft(false);
                                                talkListBean.setDraftTime(0L);
                                            } else {
                                                talkListBean.setDraft(config.getDraft());
                                                talkListBean.setHasDraft(true);
                                                talkListBean.setDraftTime(config.getDraftTime());
                                            }
                                            talkListBean.setNewMessageIsNotify(!config.isNoDisturb());
                                            break;
                                        }
                                    }
                                }

                                return Observable.just(talkListBean);
                            }
                        }
                )
                .toList();
    }

    @Override
    public Interactor<List<TalkListBean>> get() {
        return this;
    }

    @Override
    public MatchSessionConfig setConfigs(@Nullable List<SessionConfig> configs,
                                         @Nullable List<TalkListBean> talkListBeen) {
        this.configs = configs;
        this.talkListBeans = talkListBeen;
        return this;
    }
}
