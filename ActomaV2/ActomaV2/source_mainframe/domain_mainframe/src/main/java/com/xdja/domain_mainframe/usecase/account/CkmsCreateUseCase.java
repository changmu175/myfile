package com.xdja.domain_mainframe.usecase.account;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by tangsha on 2016/7/7.
 */
public class CkmsCreateUseCase extends Ext1UseCase<String,MultiResult<Object>> {

    public static String ADD_REQID = "addReqId";
    public static int CREATE_OK = 0;
    public static int CREATE_FAIL = -1;
    public static int CREATE_NEED_AUTH = 1;
    private static int CREATE_SIGN = 2;
    private static int CREATE_GET_REQ_ID = 3;
    private CkmsRepository ckmsRepository;
    private final String TAG = "anTongCkmsCreateUseCase";

    @Inject
    public CkmsCreateUseCase(ThreadExecutor threadExecutor,
                           PostExecutionThread postExecutionThread,
                           CkmsRepository ckmsRepository) {
        super(threadExecutor, postExecutionThread);
        this.ckmsRepository = ckmsRepository;
    }


    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        final MultiResult<Object> result = new MultiResult<>();
        final String entityCount = this.p;
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. Start
        if(CkmsGpEnDecryptManager.getCkmsIsOpen() == false){
            result.setResultStatus(CREATE_OK);
            return Observable.just(result);
        }
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. End
        return ckmsRepository.getEntityDevices(this.p)//get bind devices
                .flatMap(new Func1<Integer, Observable<MultiResult<Object>>>() {
                    @Override
                    public Observable<MultiResult<Object>> call(Integer integer) {
                        LogUtil.getUtils().e(TAG+"buildUseCaseObservable getEntityDevices return "+integer);
                        if(integer == -1){//get fail
                            result.setResultStatus(CREATE_FAIL);
                        }else if(integer == 0){//no bind device, so get add sign for create directly
                            Observable<Map<String, String>> signOper = ckmsRepository.getCreateSignOper(entityCount);
                            return signOper.map(new Func1<Map<String, String>, MultiResult<Object>>() {
                                @Override
                                public MultiResult<Object> call(Map<String, String> signOper) {
                                    LogUtil.getUtils().d(TAG+"buildUseCaseObservable get add operation sign return "+signOper);
                                    //get add operation sign code fail
                                    //[S]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                                    if (signOper == null || signOper.isEmpty() || !signOper.containsKey(CkmsInitUseCase.REV_CKMS_SIGN_CODE)) {
                                        result.setResultStatus(CREATE_FAIL);
                                        throw new CkmsException("" + CkmsException.CODE_CKMS_SERVER_ERROR);
                                    } //[E]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                                    //get add operation sign code Ok
                                    result.setResultStatus(CREATE_SIGN);
                                    String signCode = signOper.get(CkmsInitUseCase.REV_CKMS_SIGN_CODE);
                                    Map<String,Object> signCodeMap = new HashMap<String, Object>();
                                    signCodeMap.put(CkmsInitUseCase.REV_CKMS_SIGN_CODE,signCode);
                                    result.setInfo(signCodeMap);
                                    return result;
                                }
                            });

                        }else if(integer > 0){//have bind device, to check if current device in bind devices
                            return ckmsRepository.isCurrentDeviceAdded(entityCount).map(new Func1<Integer, MultiResult<Object>>() {
                                /*[S]modify return type by tangsha@20160921 for check result fail but go to no add flow*/
                                @Override
                                public MultiResult<Object> call(Integer res) {
                                    if(res == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){//current device has be bind, so as create ok
                                        result.setResultStatus(CREATE_OK);
                                    }else if(res == -1){//current device not be bind, need to auth
                                        result.setResultStatus(CREATE_GET_REQ_ID);
                                    }else{
                                        result.setResultStatus(CREATE_FAIL);
                                        throw new CkmsException(""+CkmsException.CODE_CKMS_SERVER_ERROR);
                                    }
                                    return result;
                                }
                            });
                                /*[E]modify return type by tangsha@20160921 for check result fail but go to no add flow*/

                        }
                        return Observable.just(result);
                    }
                }).flatMap(new Func1<MultiResult<Object>, Observable<MultiResult<Object>>>() {
                    @Override
                    public Observable<MultiResult<Object>> call(MultiResult<Object> objectMultiResult) {
                        if(objectMultiResult.getResultStatus() == CREATE_SIGN){//need to create security entity
                            Map<String,Object> infoMap = objectMultiResult.getInfo();
                           return ckmsRepository.createSec(entityCount,(String)infoMap.get(CkmsInitUseCase.REV_CKMS_SIGN_CODE))
                                   .map(new Func1<Boolean, MultiResult<Object>>() {
                                       @Override
                                       public MultiResult<Object> call(Boolean aBoolean) {
                                           if(aBoolean){
                                               result.setResultStatus(CREATE_OK);
                                           }else{
                                               result.setResultStatus(CREATE_FAIL);
                                           }
                                           return result;
                                       }
                                   });
                        }else if(objectMultiResult.getResultStatus() == CREATE_GET_REQ_ID){
                              //need to get add request id
                              return  ckmsRepository.getAddReqId(entityCount)
                                      .map(new Func1<String, MultiResult<Object>>() {
                                          @Override
                                          public MultiResult<Object> call(String s) {
                                              //get add request id fail
                                              if (s == null || s.isEmpty()) {
                                                  result.setResultStatus(CREATE_FAIL);
                                                  //[S]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                                                  throw new CkmsException("" + CkmsException.CODE_CKMS_SERVER_ERROR);
                                              }   //[E]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                                              //get request id ok to set
                                              result.setResultStatus(CREATE_NEED_AUTH);
                                              Map<String,Object> addReqIdMap = new HashMap<String, Object>();
                                              addReqIdMap.put(ADD_REQID,s);
                                              result.setInfo(addReqIdMap);
                                              return result;
                                          }
                                      });
                        }
                        return Observable.just(objectMultiResult);
                    }
                });
    }
}
