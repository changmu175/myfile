package com.xdja.imp.domain.interactor.im;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.ShareFileMsgList;
import com.xdja.imp.domain.model.*;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，发送图片的UserCase的实现
 * 创建人：ycm
 * 创建时间：2016/11/1 19:47
 * 修改人：ycm
 * 修改时间：2016/11/1 19:47
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for share and forward function by ycm at 20161103.
 * 3)Bug 5618, modify for share and forward function by ycm at 20161103.
 * 4)已经找到无法加密后未生成加密的高清缩略图原因，故删除此处的复制高清缩略图的动作 by ycm at 20161110.
 */
public class ShareFileMsgListUseCase extends IMUseCase<List<TalkListBean>> implements ShareFileMsgList {
    private List<TalkListBean> dataSources;

    private List<FileInfo> fileInfoList;

    @Inject
    public ShareFileMsgListUseCase(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public ShareFileMsgList send(List<TalkListBean> dataSources,
                                 List<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
        this.dataSources = dataSources;
        return this;
    }

    @Override
    public Observable<List<TalkListBean>> buildUseCaseObservable() {
        return Observable.from(dataSources)
                .flatMap(new Func1<TalkListBean, Observable<TalkListBean>>() {
                    @Override
                    public Observable<TalkListBean> call(final TalkListBean talkListBean) {
                        for (final FileInfo fileInfo : fileInfoList) {
                            final String to = talkListBean.getTalkerAccount();
                            final boolean isGroup = talkListBean.getTalkType() == ConstDef.CHAT_TYPE_P2G;
                            LogUtil.getUtils().d("------- to:" + to + "isGroup:" + isGroup);
                            if (fileInfo instanceof WebPageInfo) {
                                imProxyRepository.sendWebMessage(to, false, isGroup, (WebPageInfo) fileInfo)
                                        .subscribe(new Action1<TalkMessageBean>() {
                                            @Override
                                            public void call(TalkMessageBean talkMessageBean) {

                                            }
                                        });
                            } else {
                                imProxyRepository
                                        .sendFileMessage(to, false, isGroup, fileInfo)
                                        .subscribe(new Action1<TalkMessageBean>() {
                                            @Override
                                            public void call(TalkMessageBean talkMessageBean) {
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
                            }
                        }
                        return null;
                    }
                }).toList();
    }

    @Override
    public Interactor<List<TalkListBean>> get() {
        return this;
    }
}

