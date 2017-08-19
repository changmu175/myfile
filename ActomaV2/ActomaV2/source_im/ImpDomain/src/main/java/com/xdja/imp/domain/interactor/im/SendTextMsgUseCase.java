package com.xdja.imp.domain.interactor.im;

import android.app.Activity;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SendMessage;
import com.xdja.imp.domain.interactor.def.SendTextMsg;
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
public class SendTextMsgUseCase extends SendMessageUseCase implements SendTextMsg {

    private SecurityRepository securityRepository;

    private Activity context;

    private String to;

    private String content;

    private boolean isShan;

    private boolean isGroup;

    @Inject
    public SendTextMsgUseCase(ThreadExecutor threadExecutor,
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
                                .sendTextMessage(content, to, isShan, isGroup);

                        /*return securityRepository.encryptAsync(context,content,to,isGroup)
                                .flatMap(
                                        new Func1<String, Observable<TalkMessageBean>>() {
                                            @Override
                                            public Observable<TalkMessageBean> call(String s) {
                                                //发送消息
                                                return imProxyRepository
                                                        .sendTextMessage(s, to, isShan, isGroup);
                                            }
                                        }
                                );*/
                    }
                });

    }


    @Override
    public Interactor<TalkMessageBean> get() {
        return this;
    }

    @Override
    public SendMessage send(Activity context, String to, String content, boolean isShan, boolean isGroup) {
        this.context = context;
        this.to = to;
        this.content = content;
        this.isGroup = isGroup;
        this.isShan = isShan;
        return this;
    }
}
