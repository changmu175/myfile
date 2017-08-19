package com.xdja.domain_mainframe.repository;


import android.support.annotation.NonNull;

import com.xdja.domain_mainframe.model.SafeLockBean;
import com.xdja.frame.domain.repository.Repository;

import java.util.Map;

import rx.Observable;

/**
 * Created by xdjaxa on 2016/12/6.
 */
public interface SafeLockRepository extends Repository {

    /**
     * 保存安全锁状态
     * @param safeLockBean
     * @return
     */
    Observable<String> saveSafeLockSetting(@NonNull SafeLockBean safeLockBean);


    /**
     * 保存手势密码
     * @param body
     * @return
     */
    Observable<String> saveGesturePwd(@NonNull Map<String,String> body);



    interface PreSafeLockRepository {
        /**
         * 获取安全锁的相关状态
         */
        Observable<String> getSafeLockCloudSettings(@NonNull String account);

    }

}
