package com.xdja.domain_mainframe.usecase.userInfo;

import android.text.TextUtils;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext6UseCase;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ldy on 16/4/21.
 *  第三方加密应用策略更新用例
 */
public class UpdateStrategysUseCase extends Ext6UseCase<String, String, String, String, Integer, Integer, Integer> {
    private final UserInfoRepository.PreUserInfoRepository userInfoRepository;

    @Inject
    public UpdateStrategysUseCase(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         UserInfoRepository.PreUserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }
    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Integer> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("bitmap cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("cardNo cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p2)) {
            return Observable.error(
                    new CheckException("model不能为空")
            );
        }
        if (TextUtils.isEmpty(this.p3)) {
            return Observable.error(
                    new CheckException("manufacturer不能为空")
            );
        }

        if (p4 == null) {
            return Observable.error(
                    new CheckException("lastUpdateId cannot null")
            );
        }
        if (p5 == null) {
            return Observable.error(
                    new CheckException("batchSize cannot null")
            );
        }
        return userInfoRepository.queryStrategyByMobile(p,p1,p2,p3,p4,p5);
    }
}
