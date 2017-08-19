package com.xdja.imp.domain.interactor.mx;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetRoamSetting;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.RoamConfig;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_domain.R;

import javax.inject.Inject;

import rx.Observable;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;

/**
 * <p>Summary:获取漫游设置的用例</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/11</p>
 * <p>Time:13:54</p>
 */
public class GetRoamSettingUseCase extends MxUseCase<RoamConfig> implements GetRoamSetting {

    @Inject
    public GetRoamSettingUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 UserOperateRepository userOperateRepository) {
        super(threadExecutor,postExecutionThread,userOperateRepository);
    }

    @Override
    public Observable<RoamConfig> buildUseCaseObservable() {

        return userOperateRepository.queryRoamSettingAtLocal()
                .flatMap(new Func1<RoamConfig, Observable<RoamConfig>>() {
                    @Override
                    public Observable<RoamConfig> call(RoamConfig roamConfig) {
                        if (roamConfig != null) {
                            return Observable.just(roamConfig);
                        }
                        return getRoamConfigAtCloundAndSave();
                    }
                });
    }

    private Observable<RoamConfig> getRoamConfigAtCloundAndSave() {
        return userOperateRepository.getRoamSetttingAtCloud()
                .flatMap(new Func1<RoamConfig, Observable<RoamConfig>>() {
                    @Override
                    public Observable<RoamConfig> call(final RoamConfig roamConfig) {
                        if (roamConfig == null) {
                            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_get_roam_configure_null)));
                        }

                        return userOperateRepository.
                                saveRoamSetting2Local
                                        (roamConfig.getStatus(), roamConfig.getTime())
                                .map(new Func1<Boolean, RoamConfig>() {
                                    @Override
                                    public RoamConfig call(Boolean aBoolean) {
                                        if (aBoolean)
                                            return roamConfig;
                                        else
                                            throw OnErrorThrowable
                                                    .from(new Throwable(ActomaController.getApp().getString(R.string.im_local_save_roam_data_fail)));
                                    }
                                });
                    }
                });
    }

    @Override
    public Interactor<RoamConfig> get() {
        return this;
    }
}
