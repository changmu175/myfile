package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SendFileMsg;
import com.xdja.imp.domain.interactor.def.SendMessage;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:发送文件用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/3</p>
 * <p>Time:19:58</p>
 */
public class SendFileMsgUseCase extends SendMessageUseCase implements SendFileMsg {

    private String to;

    private boolean isShan;

    private boolean isGroup;

    private FileInfo fileInfo;

    @Inject
    public SendFileMsgUseCase(ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread,
                              IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }

    @Override
    public Observable<TalkMessageBean> buildUseCaseObservable(){

        //发送消息
        return imProxyRepository
                .sendFileMessage(to, isShan, isGroup, fileInfo);
    }


    @Override
    public Interactor<TalkMessageBean> get() {
        return this;
    }

    @Override
    public SendMessage send(String to, boolean isShan, boolean isGroup, FileInfo fileInfo) {
        this.to = to;
        this.isGroup = isGroup;
        this.isShan = isShan;
        this.fileInfo = fileInfo;
        return this;
    }

}
