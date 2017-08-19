package com.xdja.domain_mainframe.usecase.deviceauth;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
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
 * Created by tangsha on 2016/7/12.
 */
public class CkmsForceAddDeviceUseCase extends Ext1UseCase<String, MultiResult<Object>> {
    private CkmsRepository ckmsRepository;
    private String entity;
    public static int FORCE_ADD_DEV_OK = 0;
    public static int FORCE_ADD_DEV_FAIL = -1;
    private static int DEVICE_HAVE_ADD = 2;
    private static int DEVICE_NEED_TO_ADD = 3;

    @Inject
    public CkmsForceAddDeviceUseCase(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     CkmsRepository ckmsRep) {
        super(threadExecutor, postExecutionThread);
        this.ckmsRepository = ckmsRep;
    }


    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        entity = this.p;
        final MultiResult<Object> result = new MultiResult<>();
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. Start
        if(CkmsGpEnDecryptManager.getCkmsIsOpen() == false){
            result.setResultStatus(FORCE_ADD_DEV_OK);
            return Observable.just(result);
        }
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. End
        return ckmsRepository.isCurrentDeviceAdded(entity)
                /*[S]modify return type by tangsha@20160921 for check result fail but go to no add flow*/
                .flatMap(new Func1<Integer, Observable<Map<String, String>>>() {
                    @Override
                    public Observable<Map<String, String>> call(Integer res) {
                        if(res == -1) {
                            result.setResultStatus(DEVICE_NEED_TO_ADD);
                            return ckmsRepository.getForceAddDevSignOper(entity);
                        }else if(res == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
                            result.setResultStatus(DEVICE_HAVE_ADD);
                            Map<String, String> temRes= new HashMap<>();
                            return Observable.just(temRes);
                        }else{
                            result.setResultStatus(FORCE_ADD_DEV_FAIL);
                            throw new CkmsException(""+CkmsException.CODE_CKMS_SERVER_ERROR);
                        }
                /*[E]modify return type by tangsha@20160921 for check result fail but go to no add flow*/
                    }
                })
                .flatMap(new Func1<Map<String, String>, Observable<MultiResult<Object>>>() {
                    @Override
                    public Observable<MultiResult<Object>> call(Map<String, String> strMap) {
                        if(result.getResultStatus() == DEVICE_HAVE_ADD){
                            result.setResultStatus(FORCE_ADD_DEV_OK);
                            return Observable.just(result);
                        }
                        //[S]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                        if (strMap == null || strMap.isEmpty() || !strMap.containsKey(CkmsInitUseCase.REV_CKMS_SIGN_CODE)) {
                            result.setResultStatus(FORCE_ADD_DEV_FAIL);
                            throw new CkmsException(""+CkmsException.CODE_CKMS_SERVER_ERROR);
                        }//[E]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                        String signOp = strMap.get(CkmsInitUseCase.REV_CKMS_SIGN_CODE);
                        return ckmsRepository.addDeviceForcebly(entity, signOp)
                                .map(new Func1<Boolean, MultiResult<Object>>() {
                                    @Override
                                    public MultiResult<Object> call(Boolean aBoolean) {
                                        if (aBoolean) {
                                            result.setResultStatus(FORCE_ADD_DEV_OK);
                                        } else {
                                            result.setResultStatus(FORCE_ADD_DEV_FAIL);
                                        }
                                        return result;
                                    }
                                });
                    }
                });
    }
}
