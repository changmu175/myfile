package com.xdja.imp.domain.interactor.mx;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.interactor.def.SaveRoamSetting;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_domain.R;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:更新漫游设置</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:20:09</p>
 */
public class SetRoamSettingUseCase extends MxUseCase<Boolean> implements SaveRoamSetting {

    private static final int ROAM_TIME_DEFAULT = 3;


    @ConstDef.RoamState
    private int roamState = ConstDef.ROAM_STATE_OPEN;

    private int roamTime = ROAM_TIME_DEFAULT;

    @Inject
    public SetRoamSettingUseCase(ThreadExecutor threadExecutor,
                                 PostExecutionThread postExecutionThread,
                                 UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        return userOperateRepository.saveRoamSetting2Local(roamState,roamTime)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        if (!aBoolean) {
                            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_local_save_roam_data_fail)));
                        }
                        return userOperateRepository
                                .saveRoamSetting2Cloud(roamState,roamTime);
                    }
                });

    }


    @Override
    public SaveRoamSetting save(@ConstDef.RoamState int state, int time) {
        this.roamState = state;
        this.roamTime = time;
        return this;
    }

    @Override
    public Interactor<Boolean> get() {
        return this;
    }
}
