package com.xdja.imp.data.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.server.ActomaController;
import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.entity.RoamSetter;
import com.xdja.imp.data.entity.SessionParam;
import com.xdja.imp.data.entity.SessionTopSetter;
import com.xdja.imp.data.entity.mapper.DataMapper;
import com.xdja.imp.data.repository.datasource.CloudDataStore;
import com.xdja.imp.data.repository.datasource.DiskDataStore;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.KeyValuePair;
import com.xdja.imp.domain.model.NoDisturbConfig;
import com.xdja.imp.domain.model.RoamConfig;
import com.xdja.imp.domain.model.SessionConfig;
import com.xdja.imp.domain.model.SettingTopConfig;
import com.xdja.imp.domain.repository.UserOperateRepository;
import com.xdja.imp_data.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:用户功能性模块实现</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/3</p>
 * <p>Time:9:34</p>
 */
public class UserOperateImp implements UserOperateRepository {
    /**
     * 获取本地漫游设置的标识
     */
    final String SETTER_KEY_ROAM_SETTING = "roamSetting";

    /**
     * 用户账号标识
     */
    final String SETTER_KEY_USER_ACCOUNT = "userAccount";

    /**
     * 用户获取本地数据库是否为最新数据的标识
     */
    final String KEY_LOCAL_STATE = "localDbState";

    private CloudDataStore cloudDataStore;

    private DiskDataStore diskDataStore;
    /**
     * 用户信息缓存实体
     */
    private UserCache userCache;
    /**
     * 卡信息缓存实体
     */
    private CardCache cardCache;

    private DataMapper dataMapper;

    private Gson gson;

    @Inject
    public UserOperateImp(CloudDataStore cloudDataStore,
                          DiskDataStore diskDataStore,
                          UserCache userCache,
                          CardCache cardCache,
                          DataMapper dataMapper,
                          Gson gson) {
        this.cloudDataStore = cloudDataStore;
        this.diskDataStore = diskDataStore;
        this.userCache = userCache;
        this.cardCache = cardCache;
        this.dataMapper = dataMapper;
        this.gson = gson;
    }


    @Override
    public Observable<Boolean> saveUserAccount(@NonNull String userAccount) {
        KeyValuePair<String, String> roamKVP = new KeyValuePair<>();
        roamKVP.setKey(SETTER_KEY_USER_ACCOUNT);
        roamKVP.setValue(userAccount);
        return diskDataStore.saveKeyValuePairs(roamKVP);
    }

    @Override
    public Observable<String> queryUserAccount() {
        return Observable.just(SETTER_KEY_USER_ACCOUNT)
                .concatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return diskDataStore.queryStringSharePref(s);
                    }
                });
    }

    @Override
    public Observable<Boolean> saveRoamSetting2Cloud(@ConstDef.RoamState int state, int time) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_roam_setting_fail)));
        }
        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_roam_setting_fail)));
        }

        RoamSetter setter = new RoamSetter();
        setter.setAccount(account);
        setter.setCardId(cardId);
        setter.setStatus(state);
        setter.setTime(time);

        return cloudDataStore.saveRoamSetting2Cloud(setter);
    }

    @Override
    public Observable<RoamConfig> getRoamSetttingAtCloud() {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_roam_setting_fail)));
        }
        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_roam_setting_fail)));
        }
        return cloudDataStore.getRoamSettingAtCloud(account, cardId);
    }

    @Override
    public Observable<Boolean> saveRoamSetting2Local(@ConstDef.RoamState int state, int time) {
        RoamConfig roamConfig = new RoamConfig();
        roamConfig.setStatus(state);
        roamConfig.setTime(time);
        return Observable.just(roamConfig)
                .map(new Func1<RoamConfig, String>() {
                    @Override
                    public String call(RoamConfig config) {
                        return gson.toJson(config);
                    }
                })
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String s) {
                        KeyValuePair<String, String> roamKVP = new KeyValuePair<>();
                        roamKVP.setKey(SETTER_KEY_ROAM_SETTING);
                        roamKVP.setValue(s);
                        return diskDataStore.saveKeyValuePairs(roamKVP);
                    }
                });
    }

    @Override
    public Observable<RoamConfig> queryRoamSettingAtLocal() {

        return Observable.just(SETTER_KEY_ROAM_SETTING)
                .concatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return diskDataStore.queryStringSharePref(s);
                    }
                })
                .map(new Func1<String, RoamConfig>() {
                    @Override
                    public RoamConfig call(String strings) {
                        if (TextUtils.isEmpty(strings)) {
                            return null;
                        }
                        return gson.fromJson(strings, RoamConfig.class);
                    }
                });
    }

    @Override
    public Observable<Boolean> addNoDisturb2Cloud(@NonNull String talkerId,
                                                  @ConstDef.NoDisturbSettingSessionType int sessionType) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_nodisturb_setting_fail)));
        }
        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_nodisturb_setting_fail)));
        }
        NoDisturbSetter setter = new NoDisturbSetter();
        setter.setAccount(account);
        setter.setSessionId(talkerId);
        setter.setSessionType(sessionType);
        return cloudDataStore.addNoDisturb2Cloud(setter);
    }

    @Override
    public Observable<Boolean> addNoDisturb2Local(final @NonNull String talkerId) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_nodisturb_save_fail)));
        }
        String cardId = cardCache.get().getCardId();

        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_nodisturb_save_fail)));
        }

        return diskDataStore
                .getLocalSingleSession(talkerId)
                .flatMap(new Func1<SessionParam, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(SessionParam sessionParam) {

                        if (sessionParam == null) {
                            sessionParam = new SessionParam();
                            sessionParam.setFlag(talkerId);
                        }
                        sessionParam.setIsNoDisturb(SessionParam.ISDISTURB_TRUE);
                        return diskDataStore.saveOrUpdateLocalSingleSession(sessionParam);
                    }
                });

    }

    @Override
    public Observable<Boolean> deleteNoDisturbAtCloud(@NonNull String talkerId,
                                                      @ConstDef.NoDisturbSettingSessionType int sessionType) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_nodisturb_delete_fail)));
        }

        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_nodisturb_delete_fail)));
        }

        NoDisturbSetter setter = new NoDisturbSetter();
        setter.setAccount(account);
        setter.setSessionId(talkerId);
        setter.setSessionType(sessionType);
        return cloudDataStore.deleteNoDisturbAtCloud(setter);
    }

    @Override
    public Observable<Boolean> deleteNoDisturbAtLocal(final @NonNull String talkerId) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_nodisturb_delete_fail)));
        }
        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_nodisturb_delete_fail)));
        }

        return diskDataStore.getLocalSingleSession(talkerId)
                .flatMap(new Func1<SessionParam, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(SessionParam sessionParam) {

                        if (sessionParam == null) {
                            sessionParam = new SessionParam();
                            sessionParam.setFlag(talkerId);
                        }
                        sessionParam.setIsNoDisturb(SessionParam.ISDISTURB_FALE);
                        return diskDataStore.saveOrUpdateLocalSingleSession(sessionParam);
                    }
                });
    }

    @Override
    public Observable<Boolean> saveDraft2Local(@NonNull final String talkerId,
                                               final @Nullable String draft,
                                               final @Nullable long draftTime) {
        return diskDataStore.getLocalSingleSession(talkerId)
                .flatMap(
                        new Func1<SessionParam, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(SessionParam sessionParam) {
                                if (sessionParam == null) {
                                    sessionParam = new SessionParam();
                                    sessionParam.setFlag(talkerId);
                                }
                                sessionParam.setDraft(draft);
                                sessionParam.setDraftTime(draftTime);
                                return
                                        diskDataStore.saveOrUpdateLocalSingleSession(sessionParam);
                            }
                        }
                );
    }

    @Override
    public Observable<Boolean> deleteSettingTopAtCloud(@NonNull String talkerId,boolean sessionType) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_top_delete_fail)));
        }

        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_top_delete_fail)));
        }

        SessionTopSetter setter = new SessionTopSetter();
        setter.setAccount(account);
        setter.setSessionId(talkerId);
        return cloudDataStore.deleteSettingTopAtCloud(setter);
    }

    @Override
    public Observable<Boolean> deleteSettingTopAtLocal(@NonNull final String talkerId) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_top_delete_fail)));
        }
        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_top_delete_fail)));
        }

        return diskDataStore.getLocalSingleSession(talkerId)
                .flatMap(new Func1<SessionParam, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(SessionParam sessionParam) {

                        if (sessionParam == null) {
                            sessionParam = new SessionParam();
                            sessionParam.setFlag(talkerId);
                        }
                        sessionParam.setIsTop(SessionParam.ISTOP_FALE);
                        return diskDataStore.saveOrUpdateLocalSingleSession(sessionParam);
                    }
                });
    }



    @Override
    public Observable<List<SessionConfig>> querySessionSettingsAtLocal() {
        return diskDataStore.getLocalSessionParams()
                .flatMap(
                        new Func1<List<SessionParam>, Observable<SessionParam>>() {
                            @Override
                            public Observable<SessionParam> call(List<SessionParam> sessionParams) {
                                return Observable.from(sessionParams);
                            }
                        }
                )
                .map(
                        new Func1<SessionParam, SessionConfig>() {
                            @Override
                            public SessionConfig call(SessionParam sessionParam) {
                                return dataMapper.sessionParamMap2SessionConfig(sessionParam);
                            }
                        }
                )
                .toList();
    }

    @Override
    public Observable<List<SessionConfig>> saveSettingTop2Local(List<SettingTopConfig> configs) {
        return Observable.from(configs)
                .flatMap(
                        new Func1<SettingTopConfig, Observable<SessionConfig>>() {
                            @Override
                            public Observable<SessionConfig> call(final SettingTopConfig settingTopConfig) {
                                return diskDataStore.getLocalSingleSession(settingTopConfig.getSessionId())
                                        .flatMap(
                                                new Func1<SessionParam, Observable<SessionConfig>>() {
                                                    @Override
                                                    public Observable<SessionConfig> call(SessionParam sessionParam) {
                                                        if (sessionParam == null) {
                                                            sessionParam = new SessionParam();
                                                            sessionParam.setFlag(settingTopConfig.getSessionId());
                                                        }
                                                        sessionParam.setIsTop(SessionParam.ISTOP_TRUE);//setIsNoDisturb(SessionParam.ISDISTURB_TRUE);

                                                        final SessionConfig sessionConfig = dataMapper.sessionParamMap2SessionConfig(sessionParam);

                                                        return diskDataStore.saveOrUpdateLocalSingleSession(sessionParam)
                                                                .map(new Func1<Boolean, SessionConfig>() {
                                                                    @Override
                                                                    public SessionConfig call(Boolean aBoolean) {
                                                                        return sessionConfig;
                                                                    }
                                                                });
                                                    }
                                                }
                                        );
                            }
                        }
                )
                .toList();
    }

    @Override
    public Observable<Boolean> queryLocalSessionState() {
        return diskDataStore.queryIntegerSharePref(KEY_LOCAL_STATE)
                .map(
                        new Func1<Integer, Boolean>() {
                            @Override
                            public Boolean call(Integer integer) {
                                return integer > 0;
                            }
                        }
                );
    }

    @Override
    public Observable<SessionConfig> querySingleSessionSettingAtLocal(@NonNull String talkerId) {
        return Observable.just(talkerId).flatMap(new Func1<String, Observable<SessionConfig>>() {
            @Override
            public Observable<SessionConfig> call(String s) {
                return diskDataStore.getLocalSingleSession(s)
                        .map(
                                new Func1<SessionParam, SessionConfig>() {
                                    @Override
                                    public SessionConfig call(SessionParam sessionParam) {
                                        return dataMapper.sessionParamMap2SessionConfig(sessionParam);
                                    }
                                }
                        );
            }
        });
        /*return diskDataStore.getLocalSingleSession(talkerId)
                .map(
                        new Func1<SessionParam, SessionConfig>() {
                            @Override
                            public SessionConfig call(SessionParam sessionParam) {
                                return dataMapper.sessionParamMap2SessionConfig(sessionParam);
                            }
                        }
                );*/

    }

    @Override
    public Observable<Boolean> deleteAllDraft() {
        return diskDataStore.getLocalSessionParams()
                .map(new Func1<List<SessionParam>, List<SessionParam>>() {
                    @Override
                    public List<SessionParam> call(List<SessionParam> sessionParams) {
                        for (SessionParam param : sessionParams) {
                            param.setDraft("");
                        }
                        return sessionParams;
                    }
                })
                .flatMap(new Func1<List<SessionParam>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<SessionParam> sessionParams) {
                        return diskDataStore.saveOrUpdateLocalSession(sessionParams);
                    }
                });
    }

    @Override
    public Observable<Boolean> deleteSingleSessionSettingAtLocal(@NonNull String talkFlag) {
        return Observable.just(talkFlag).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                return diskDataStore.getLocalSingleSession(s)
                        .flatMap(new Func1<SessionParam, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(SessionParam sessionParam) {
                                return diskDataStore.deleteLocalSingleSession(sessionParam);
                            }
                        });
            }
        });
        /*return diskDataStore.getLocalSingleSession(talkFlag)
                .flatMap(new Func1<SessionParam, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(SessionParam sessionParam) {
                        return diskDataStore.deleteLocalSingleSession(sessionParam);
                    }
                });*/
    }

    @Override
    public Observable<Boolean> addSettingTop2Cloud(@NonNull String talkerId, boolean sessionType) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_top_setting_fail)));
        }
        String cardId = cardCache.get().getCardId();
        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_top_setting_fail)));
        }
        SessionTopSetter setter = new SessionTopSetter();
        setter.setAccount(account);
        setter.setSessionId(talkerId);
        return cloudDataStore.addSettingTop2Cloud(setter);
    }

    @Override
    public Observable<Boolean> addSettingTop2Local(final @NonNull String talkerId) {
        String account = userCache.get().getAccount();
        if (TextUtils.isEmpty(account)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_account_null_top_save_fail)));
        }
        String cardId = cardCache.get().getCardId();

        if (TextUtils.isEmpty(cardId)) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_card_null_top_save_fail)));
        }

        return diskDataStore
                .getLocalSingleSession(talkerId)
                .flatMap(new Func1<SessionParam, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(SessionParam sessionParam) {

                        if (sessionParam == null) {
                            sessionParam = new SessionParam();
                            sessionParam.setFlag(talkerId);
                        }
                        sessionParam.setIsTop(SessionParam.ISTOP_TRUE);
                        return diskDataStore.saveOrUpdateLocalSingleSession(sessionParam);
                    }
                });
    }

    @Override
    public Observable<List<NoDisturbConfig>> queryNoDisturbSettingsAtCloud() {
        final String account = userCache.get().getAccount();
        return cloudDataStore.getNoDisturbSettingsAtCloud(account);
    }

    @Override
    public Observable<List<SettingTopConfig>> querySettingTopSettingsAtCloud() {
        final String account = userCache.get().getAccount();
        return cloudDataStore.getSettingTopSettingsAtCloud(account);
    }


    @Override
    public Observable<List<SessionConfig>> saveNoDisturb2Local(List<NoDisturbConfig> configs) {
        return Observable.from(configs)
                .flatMap(
                        new Func1<NoDisturbConfig, Observable<SessionConfig>>() {
                            @Override
                            public Observable<SessionConfig> call(final NoDisturbConfig noDisturbConfig) {
                                return diskDataStore.getLocalSingleSession(noDisturbConfig.getSessionId())
                                        .flatMap(
                                                new Func1<SessionParam, Observable<SessionConfig>>() {
                                                    @Override
                                                    public Observable<SessionConfig> call(SessionParam sessionParam) {
                                                        if (sessionParam == null) {
                                                            sessionParam = new SessionParam();
                                                            sessionParam.setFlag(noDisturbConfig.getSessionId());
                                                        }
                                                        sessionParam.setIsNoDisturb(SessionParam.ISDISTURB_TRUE);

                                                        final SessionConfig sessionConfig = dataMapper.sessionParamMap2SessionConfig(sessionParam);

                                                        return diskDataStore.saveOrUpdateLocalSingleSession(sessionParam)
                                                                .map(new Func1<Boolean, SessionConfig>() {
                                                                    @Override
                                                                    public SessionConfig call(Boolean aBoolean) {
                                                                        return sessionConfig;
                                                                    }
                                                                });
                                                    }
                                                }
                                        );
                            }
                        }
                )
                .toList();
    }

    @Override
    public Observable<Boolean> setLocalSessionState(boolean isDone) {
        KeyValuePair<String,Integer> keyValuePair = new KeyValuePair<>();
        keyValuePair.setKey(KEY_LOCAL_STATE);
        keyValuePair.setValue(1);
        return diskDataStore.saveIntKeyValuePairs(keyValuePair);
    }


    @Override
    public Observable<Boolean> saveSessionTopSetting2Local(final @NonNull String talkerId, final boolean isTop) {
        return diskDataStore.getLocalSingleSession(talkerId)
                .flatMap(
                        new Func1<SessionParam, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(SessionParam sessionParam) {
                                if (sessionParam == null) {
                                    sessionParam = new SessionParam();
                                    sessionParam.setFlag(talkerId);
                                }
                                sessionParam.setIsTop(
                                        isTop ? SessionParam.ISTOP_TRUE : SessionParam.ISTOP_FALE
                                );
                                return
                                        diskDataStore.saveOrUpdateLocalSingleSession(sessionParam);
                            }
                        }
                );
    }

    @Override
    public Observable<List<SessionConfig>> saveSettingTopAndNodisturb2Local(final List<SessionConfig> configs){
        //fix bug 3368 by licong, reView by zya ,2016/9/11
        return querySessionSettingsAtLocal().map(
                new Func1<List<SessionConfig>, List<SessionConfig>>() {
                    @Override
                    public List<SessionConfig> call(List<SessionConfig> localSConfigs) {
                        //当从云端获取的数据为空时，查询本地数据，将置顶，免打扰全部设置为false
                        if (configs.size() == 0) {
                            for (SessionConfig config : localSConfigs) {
                                config.setNoDisturb(false);
                                config.setTop(false);
                            }
                            return localSConfigs;
                        } else {
                            if (localSConfigs.size() > 0) {
                                boolean isMatch = false;
                                SessionConfig config = null;
                                SessionConfig itemConfig;
                                List<SessionConfig> addChange = new ArrayList<>();
                                List<SessionConfig> addClodChange = new ArrayList<>();
                                if (configs != null) {// TODO: 2017/2/16 确认此处需要不
                                    for (int i = 0; i < configs.size(); i++) {
                                        itemConfig = configs.get(i);
                                        for (int j = 0; j < localSConfigs.size(); j++) {
                                            config = localSConfigs.get(j);
                                            if (config.equals(itemConfig)) {
                                                isMatch = true;
                                                break;
                                            }
                                        }
                                        if (isMatch) {
                                            config.setTop(itemConfig.isTop());
                                            config.setNoDisturb(itemConfig.isNoDisturb());
                                            addChange.add(config);
                                            localSConfigs.remove(config);
                                            addClodChange.add(itemConfig);
                                        }
                                        isMatch = false;
                                    }
                                }
                                configs.removeAll(addClodChange);
                                if (configs.size() > 0) {
                                    for (SessionConfig clodConfig : configs) {
                                        SessionConfig sessionConfig = new SessionConfig();
                                        sessionConfig.setNoDisturb(clodConfig.isNoDisturb());
                                        sessionConfig.setTop(clodConfig.isTop());
                                        sessionConfig.setFlag(clodConfig.getFlag());
                                        addChange.add(sessionConfig);
                                    }
                                }

                                if (localSConfigs != null) {
                                    for (SessionConfig localConfig : localSConfigs) {
                                        localConfig.setNoDisturb(false);
                                        localConfig.setTop(false);
                                    }
                                }
                                localSConfigs.addAll(addChange);

                                return localSConfigs;
                            } else {
                                return configs;
                            }

                        }

                    }
                    }

        ).flatMap(
                new Func1<List<SessionConfig>, Observable<List<SessionConfig>>>() {
                    @Override
                    public Observable<List<SessionConfig>> call(List<SessionConfig> configs) {
                        return Observable.from(configs).flatMap(
                                new Func1<SessionConfig, Observable<SessionConfig>>() {
                                    @Override
                                    public Observable<SessionConfig> call(final SessionConfig sessionConfig) {
                                        SessionParam param =  dataMapper.sessionConfigMap2SessionParam(sessionConfig);
                                        return diskDataStore.saveOrUpdateLocalSingleSession(param)
                                                .map(new Func1<Boolean, SessionConfig>() {
                                                    @Override
                                                    public SessionConfig call(Boolean aBoolean) {
                                                        return sessionConfig;
                                                    }});
                                    }
                                }
                        ).toList();
                    }
                }
        );//end
    }

    @Override
    public Observable<Integer> releaseRepository() {
        diskDataStore.releaseDiskDataStore();
        diskDataStore = null;
        return Observable.just(1);
    }


}
