package com.xdja.imp.domain.interactor.im;

import android.app.Activity;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SendCustomTextMsg;
import com.xdja.imp.domain.interactor.def.SendMessage;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;
import com.xdja.imp.domain.repository.SecurityRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:发送文本消息的用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/21</p>
 * <p>Time:11:39</p>
 */
public class SendCustomTextMsgUseCase extends SendMessageUseCase implements SendCustomTextMsg {

    private SecurityRepository securityRepository;

    private Activity context;

    private String to;

    private String content;

    private boolean isGroup;

    @Inject
    public SendCustomTextMsgUseCase(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    IMProxyRepository imProxyRepository,
                                    SecurityRepository securityRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
        this.securityRepository = securityRepository;
    }

    @Override
    public Observable<TalkMessageBean> buildUseCaseObservable() {

        //先进行加密
        return Observable.just(context)
                .flatMap(new Func1<Activity, Observable<TalkMessageBean>>() {
                    @Override
                    public Observable<TalkMessageBean> call(Activity activity) {
                        return imProxyRepository
                                .sendCustomTextMessage(content, to, isGroup);
                    }
                });

    }


    @Override
    public Interactor<TalkMessageBean> get() {
        return this;
    }

    @Override
    public SendMessage send(Activity context, String to, String content, boolean isGroup) {
        this.context = context;
        this.to = to;
        this.content = content;
        this.isGroup = isGroup;
        return this;
    }
}
