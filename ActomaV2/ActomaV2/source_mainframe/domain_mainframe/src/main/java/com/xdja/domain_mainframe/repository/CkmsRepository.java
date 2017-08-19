package com.xdja.domain_mainframe.repository;

import com.xdja.domain_mainframe.model.MultiResult;

import java.util.Map;

import rx.Observable;
/**
 * Created by tangsha on 2016/6/28.
 */
public interface CkmsRepository {
    /**
     * 获取挑战值
     *
     * @return 挑战值
     */
    Observable<Map<String, Object>> getCkmsChallenge();

    /**
     * 获取签名挑战值
     *
     * @return 签名挑战值
     */
    Observable<Map<String, String>> getSignedChallenge(String challenge);


    /**
     * 初始化
     *
     * @return 初始化是否成功
     */
    Observable<MultiResult<Object>> ckmsInit(String signChallenge);


    /**
     * 获取ckms初始化状态
     *
     * @return 释放是否成功
     */
    boolean isCkmsHasInit();

    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. Start
    void resetCkmsHasInit();
    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. End

    Observable<Integer> getEntityDevices(String entity);
	
    /*[S]modify return type by tangsha@20160921 for check result fail but go to no add flow*/
    Observable<Integer> isCurrentDeviceAdded(String entity);
    /*[E]modify return type by tangsha@20160921 for check result fail but go to no add flow*/

    Observable<Boolean> createSec(String entity, String signOper);

    Observable<Map<String, String>> getCreateSignOper(String entity);

    Observable<String> getAddReqId(String entity);

    Observable<String> checkAddReqId(String id);

    Observable<Boolean> addDevice(String currentEntity, String reqId, String opSign);

    Observable<Boolean> addDeviceForcebly(String entity, String opSign);

    Observable<Map<String, String>> getAuthAddDevSignOper(String entity, String needAddDevId);

    Observable<Map<String, String>> getForceAddDevSignOper(String entity);

    Observable<Map<String, String>> getRemoveDevSignOper(String entity,String deviceId);

    Observable<Boolean> removeDev(String entity, String devNo, String opSign);

    Observable<Map<String, String>> ckmsOperSign(String ckmsOperStr);

   /*[S]add by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
    Observable<Boolean> isAuthedDevAddedToEntity(String entity, String sn);
   /*[E]add by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/

    //[S]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.
    Observable<Integer> ckmsRelease();
    //[E]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.

}
