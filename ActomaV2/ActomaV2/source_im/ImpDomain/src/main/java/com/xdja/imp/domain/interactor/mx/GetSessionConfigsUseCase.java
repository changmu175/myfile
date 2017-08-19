package com.xdja.imp.domain.interactor.mx;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.domain.excutor.PostExecutionThread;
import com.xdja.imp.domain.excutor.ThreadExecutor;
import com.xdja.imp.domain.interactor.def.GetSessionConfigs;
import com.xdja.imp.domain.interactor.def.Interactor;
import com.xdja.imp.domain.model.NoDisturbConfig;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.SettingTopConfig;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_domain.R;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.interactor</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/16</p>
 * <p>Time:15:47</p>
 */
public class GetSessionConfigsUseCase extends MxUseCase<List<SessionConfig>>
        implements GetSessionConfigs {



    @Inject
    public GetSessionConfigsUseCase(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    UserOperateRepository userOperateRepository) {
        super(threadExecutor, postExecutionThread, userOperateRepository);
    }

    @Override
    public Observable<List<SessionConfig>> buildUseCaseObservable() {
        //modify by zya@xdja.com,fix bug 1692,review by gbc,20160816
        return userOperateRepository.queryLocalSessionState()
                .flatMap(
                        new Func1<Boolean, Observable<List<SessionConfig>>>() {
                            @Override
                            public Observable<List<SessionConfig>> call(Boolean aBoolean) {
                                    return Observable.zip(
                                            userOperateRepository.queryNoDisturbSettingsAtCloud(),
                                            userOperateRepository.querySettingTopSettingsAtCloud(),
                                            new Func2<List<NoDisturbConfig>, List<SettingTopConfig>, List<SessionConfig>>() {
                                                @Override
                                                public List<SessionConfig> call(List<NoDisturbConfig> noDisturbConfigs, List<SettingTopConfig> settingTopConfigs) {
                                                    List<SessionConfig> results = new ArrayList<>();

                                                    int noDisturbSize = noDisturbConfigs.size();

                                                    for(int i = 0;i < noDisturbSize;i++){
                                                        SessionConfig rConfig = new SessionConfig();
                                                        NoDisturbConfig config = noDisturbConfigs.get(i);

                                                        SettingTopConfig sConfig = new SettingTopConfig();
                                                        sConfig.setSessionId(config.getSessionId());

                                                        rConfig.setFlag(config.getSessionId());
                                                        rConfig.setNoDisturb(true);

                                                        if(settingTopConfigs.contains(sConfig)){
                                                            int index = settingTopConfigs.indexOf(sConfig);
                                                            rConfig.setTop(true);
                                                            settingTopConfigs.remove(index);
                                                        } else {
                                                            rConfig.setTop(false);
                                                        }
                                                        results.add(rConfig);
                                                    }

                                                    int settingSize = settingTopConfigs.size();
                                                    if(settingSize >= 0){
                                                        for(int i = 0;i < settingSize;i++){
                                                            SessionConfig rConfig = new SessionConfig();
                                                            SettingTopConfig config = settingTopConfigs.get(i);
                                                            rConfig.setFlag(config.getSessionId());
                                                            rConfig.setNoDisturb(false);
                                                            rConfig.setTop(true);
                                                            results.add(rConfig);
                                                        }
                                                    }
                                                    return results;
                                                }
                                            }

                                    ).flatMap(
                                            new Func1<List<SessionConfig>, Observable<List<SessionConfig>>>() {
                                                @Override
                                                public Observable<List<SessionConfig>> call(List<SessionConfig> sessionConfigs) {
                                                    return userOperateRepository.saveSettingTopAndNodisturb2Local(sessionConfigs);
                                                }
                                            }
                                    );
                            }

                        }
                )
                .flatMap(new Func1<List<SessionConfig>, Observable<List<SessionConfig>>>() {
                    @Override
                    public Observable<List<SessionConfig>> call(final List<SessionConfig> configs) {
                        return userOperateRepository.setLocalSessionState(true)
                                .flatMap(
                                        new Func1<Boolean, Observable<List<SessionConfig>>>() {
                                            @Override
                                            public Observable<List<SessionConfig>> call(Boolean aBoolean) {
                                                if (aBoolean) {
                                                    return Observable.just(configs);
                                                }else{
                                                    return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_conversation_data_save_fail)));
                                                }
                                            }
                                        }
                                );
                    }
                } );
    }

    @Override
    public Interactor<List<SessionConfig>> get() {
        return this;
    }
}
