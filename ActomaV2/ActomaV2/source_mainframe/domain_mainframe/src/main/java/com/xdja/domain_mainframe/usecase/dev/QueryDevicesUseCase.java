package com.xdja.domain_mainframe.usecase.dev;

import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext0UseCase;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 * 查询设备账号授信的设备列表用例
 */
public class QueryDevicesUseCase extends Ext0UseCase<List<Map<String,String>>> {
    private final UserInfoRepository userInfoRepository;

    @Inject
    public QueryDevicesUseCase(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             UserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }
    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<List<Map<String,String>>> buildUseCaseObservable() {
        return userInfoRepository.queryDevices();
    }

}
