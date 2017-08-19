package com.xdja.imp.domain.interactor.im;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetHistoryFileList;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.HistoryFileCategory;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/12 17:38
 * 修改人：xdjaxa
 * 修改时间：2016/12/12 17:38
 * 修改备注：
 */
public class GetHistoryFileListUseCase extends IMUseCase<Map<HistoryFileCategory,List<TalkMessageBean>>> implements GetHistoryFileList {

    private String talkId;

    @Inject
    public GetHistoryFileListUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
    }


    @Override
    public Observable<Map<HistoryFileCategory,List<TalkMessageBean>>> buildUseCaseObservable() {
        return imProxyRepository.getAllHistoryFileInfoWithTalkId(talkId);
    }

    @Override
    public Interactor<Map<HistoryFileCategory,List<TalkMessageBean>>> get() {
        return this;
    }

    @Override
    public GetHistoryFileList deliverParams(String talkId) {
        this.talkId = talkId;
        return this;
    }
}
