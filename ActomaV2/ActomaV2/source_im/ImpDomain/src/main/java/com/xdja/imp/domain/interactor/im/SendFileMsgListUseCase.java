package com.xdja.imp.domain.interactor.im;

import android.app.Activity;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SendFileMsgList;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;
import com.xdja.imp.domain.repository.SecurityRepository;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by xdjaxa on 2016/8/11.
 */
public class SendFileMsgListUseCase  extends IMUseCase<TalkMessageBean> implements SendFileMsgList{

    private SecurityRepository securityRepository;

    private Activity context;

    private String to;

    private boolean isShan;

    private boolean isGroup;

    private List<FileInfo> fileInfoList;

    @Inject
    public SendFileMsgListUseCase(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  IMProxyRepository imProxyRepository,
                                  SecurityRepository securityRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
        this.securityRepository = securityRepository;
    }

    @Override
    public SendFileMsgList send(Activity context,
                                String to,
                                boolean isShan,
                                boolean isGroup,
                                List<FileInfo> fileInfoList) {
        this.context = context;
        this.to = to;
        this.isGroup = isGroup;
        this.isShan = isShan;
        this.fileInfoList = fileInfoList;
        return this;
    }

    @Override
    public Observable<TalkMessageBean> buildUseCaseObservable() {
        return Observable.just(context).flatMap(new Func1<Activity, Observable<TalkMessageBean>>() {
            @Override
            public Observable<TalkMessageBean> call(Activity c) {
//                return Observable.from(fileInfoList)
//                        .flatMap(new Func1<FileInfo, Observable<TalkMessageBean>>() {
//                            @Override
//                            public Observable<TalkMessageBean> call(FileInfo fileInfo) {
//                                return securityRepository.encryptAsync(fileInfo,to, isGroup)
//                                        .flatMap(new Func1<FileInfo, Observable<TalkMessageBean>>() {
//                                            @Override
//                                            public Observable<TalkMessageBean> call(FileInfo fileInfo) {
//                                                List<FileInfo> fInfos = new ArrayList<FileInfo>();
//                                                fInfos.add(fileInfo);
//                                                return imProxyRepository
//                                                        .sendFileMessage(to, isShan, isGroup, fInfos);
//                                            }
//                                        });
//                            }
//                        });
                return imProxyRepository
                        .sendFileMessage(to, isShan, isGroup, fileInfoList);
            }
        });
    }

    @Override
    public Interactor<TalkMessageBean> get() {
        return this;
    }
}
