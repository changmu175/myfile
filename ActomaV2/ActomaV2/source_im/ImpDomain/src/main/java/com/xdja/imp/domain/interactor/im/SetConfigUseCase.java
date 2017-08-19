package com.xdja.imp.domain.interactor.im;

import android.text.TextUtils;

import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SetConfig;
import com.xdja.imp.domain.repository.IMProxyRepository;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * <p>Summary:设置配置信息用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor.im</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/20</p>
 * <p>Time:14:51</p>
 */
public class SetConfigUseCase extends IMUseCase<Boolean> implements SetConfig{

    private Map<String,String> config;

    @Inject
    public SetConfigUseCase(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            IMProxyRepository imProxyRepository) {
        super(threadExecutor, postExecutionThread, imProxyRepository);
        this.config = new HashMap<>();
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        return imProxyRepository.setProxyConfig(config);
    }


    @Override
    public SetConfig putConfig(String key, String value) {

        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            this.config.put(key,value);
        }

        return this;
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }
}
