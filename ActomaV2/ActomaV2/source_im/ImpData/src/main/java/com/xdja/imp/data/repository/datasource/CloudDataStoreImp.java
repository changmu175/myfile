package com.xdja.imp.data.repository.datasource;

import android.support.annotation.NonNull;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.data.entity.NoDisturbSetter;
import com.xdja.imp.data.entity.RoamSetter;
import com.xdja.imp.data.entity.SessionTopSetter;
import com.xdja.imp.data.net.ApiFactoryMe;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.domain.model.NoDisturbConfig;
import com.xdja.imp.domain.model.RoamConfig;
import com.xdja.imp.domain.model.SettingTopConfig;
import com.xdja.imp_data.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:数据网络存储</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/3</p>
 * <p>Time:13:56</p>
 */
public class CloudDataStoreImp implements CloudDataStore {

    private ApiFactoryMe apiFactory;

    @Inject
    public CloudDataStoreImp(ApiFactoryMe apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public Observable<Boolean> saveRoamSetting2Cloud(final RoamSetter setter) {

        if (setter == null) {
            return Observable.error(new Throwable(ActomaController.getApp().getString(R.string.im_save_roam_data_null)));
        }

        return apiFactory.getUserSettingApi().saveRoamSettings(setter)
                .map(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object o) {
                        LogUtil.getUtils().i("缓存漫游信息到云端成功," + setter.toString());
                        return Boolean.TRUE;
                    }
                });
    }

    @Override
    public Observable<RoamConfig> getRoamSettingAtCloud(String account, String cardId) {
        return  apiFactory.getUserSettingApi().getRoamSettings(account,cardId);
    }

    @Override
    public Observable<Boolean> addNoDisturb2Cloud(final NoDisturbSetter noDisturbSetter) {
        return apiFactory
                .getUserSettingApi()
                .saveNoDisturbSettings(noDisturbSetter)
                .map(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object o) {
                        LogUtil.getUtils().i("缓存免打扰设置到云端成功," + noDisturbSetter.toString());
                        return Boolean.TRUE;
                    }
                });
    }

    @Override
    public Observable<Boolean> deleteNoDisturbAtCloud(final NoDisturbSetter noDisturbSetter) {
        return apiFactory.getUserSettingApi().deleteNoDisturbSettings(noDisturbSetter)
                .map(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object o) {
                        LogUtil.getUtils().i("从云端删除免打扰设置成功," + noDisturbSetter.toString());
                        return Boolean.TRUE;
                    }
                });
    }

    @Override
    public Observable<List<NoDisturbConfig>> getNoDisturbSettingsAtCloud(String account) {
        return apiFactory.getUserSettingApi().getNoDisturbSettings(account);
    }

    @Override
    public Observable<Boolean> addSettingTop2Cloud(@NonNull SessionTopSetter sessionTopSetter) {

        return apiFactory
                .getUserSettingApi()
                .saveSessionTopSettings(sessionTopSetter)
                .map(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object o) {
                        return Boolean.TRUE;
                    }
                });
    }


    @Override
    public Observable<Boolean> deleteSettingTopAtCloud(@NonNull SessionTopSetter sessionTopSetter) {
        return apiFactory.getUserSettingApi().deleteSessionTopSettings(sessionTopSetter)
                .map(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object b) {
                        return Boolean.TRUE;
                    }
                });
    }

    @Override
    public Observable<List<SettingTopConfig>> getSettingTopSettingsAtCloud(String account) {
        return apiFactory.getUserSettingApi()
                .getSettingTopSettings(account)
                .map(new Func1<List<String>, List<SettingTopConfig>>() {
            @Override
            public List<SettingTopConfig> call(List<String> strings) {
                List<SettingTopConfig> results = new ArrayList<>();
                for(String str : strings){
                    SettingTopConfig sConfig = new SettingTopConfig();
                    sConfig.setSessionId(str);

                    if(results.size() == 0 || (results.size() != 0 && results.indexOf(sConfig) < 0)){
                        results.add(sConfig);
                    }
                }
                return results;
            }
        });
    }
}
