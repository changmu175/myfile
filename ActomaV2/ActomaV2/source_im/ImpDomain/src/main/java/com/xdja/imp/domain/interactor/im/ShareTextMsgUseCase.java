package com.xdja.imp.domain.interactor.im;

import android.app.Activity;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.ShareTextMsg;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;
import com.xdja.imp.domain.repository.SecurityRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，发送文本的UserCase的实现
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/3 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Bug 5618, modify for share and forward function by ycm at 20161103.
 */
public class ShareTextMsgUseCase extends IMUseCase<List<TalkListBean>> implements ShareTextMsg {
    private SecurityRepository securityRepository;

    private Activity context;

    private String to;

    private String content;

    private boolean isShan;

    private boolean isGroup;
    private List<TalkListBean> dataSource;

    @Inject
    public ShareTextMsgUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               IMProxyRepository imProxyRepository,
                               SecurityRepository securityRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
        this.securityRepository = securityRepository;
    }

    @Override
    public ShareTextMsg send(Activity context, String content, List<TalkListBean> dataSource) {
        this.context = context;
        this.content = content;
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public Observable<List<TalkListBean>> buildUseCaseObservable() {
        return Observable.from(dataSource).flatMap(new Func1<TalkListBean, Observable<List<TalkListBean>>>() {
            @Override
            public Observable<List<TalkListBean>> call(TalkListBean talkListBean) {
                to = talkListBean.getTalkerAccount();
                isGroup = talkListBean.getTalkType() == ConstDef.CHAT_TYPE_P2G;
                Observable.just(content).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
//                        securityRepository
//                                .encryptAsync(context, content, to, isGroup, true)
//                                .subscribe(new Action1<String>() {
//                                    @Override
//                                    public void call(String s) {
//                                        imProxyRepository
//                                                .sendTextMessage(s, to, false, isGroup).subscribe(new Action1<TalkMessageBean>() {
//                                            @Override
//                                            public void call(TalkMessageBean talkMessageBean) {
//
//                                            }
//                                        });
//                                    }
//                                });
                        imProxyRepository
                                .sendTextMessage(s, to, false, isGroup).subscribe(new Subscriber<TalkMessageBean>() {// modified by ycm 20170103:防止IMSDK出错时发消息崩溃
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(TalkMessageBean talkMessageBean) {

                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });
                return null;
            }
        });

    }

    @Override
    public Interactor<List<TalkListBean>> get() {
        return null;
    }
}
