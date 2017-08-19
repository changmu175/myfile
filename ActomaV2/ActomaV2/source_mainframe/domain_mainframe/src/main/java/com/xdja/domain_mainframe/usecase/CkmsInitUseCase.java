package com.xdja.domain_mainframe.usecase;

import android.util.Log;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.frame.data.ckms.CkmsManager;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by tangsha on 2016/7/4.
 */
public class CkmsInitUseCase extends Ext1UseCase<Boolean,MultiResult<Object>> {
    public static String REV_CKMS_SIGN_CODE = "signedOpCode";
    private CkmsRepository ckmsRepository;
    private String TAG = "anTongCkmsInitUseCase";
    public static int INIT_OK = 0;
    public static int INIT_FAIL = -1;
    /*[S]modify by tangsha@20160913 for return error code, maybe need input pin code*/
    public static String INIT_FAIL_CODE_TAG = "ckmsInitFailCodeTag";
    /*[E]modify by tangsha@20160913 for return error code, maybe need input pin code*/
    //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. Start
    public static String VALID_HOUR = "valid_hours";
    public static String CKMS_HAS_INIT = "has_init";
    //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. End
    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. Start
    private boolean initForExpire = false;
    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. End

    @Inject
    public CkmsInitUseCase(ThreadExecutor threadExecutor,
                           PostExecutionThread postExecutionThread,
                           CkmsRepository ckmsRepository) {
        super(threadExecutor, postExecutionThread);
        this.ckmsRepository = ckmsRepository;
    }

    @Override
    public Observable<MultiResult<Object>> buildUseCaseObservable() {
        initForExpire = this.p;
        //tangsha@xdja.com 2016-08-03 modify. for config open or close ckms flow . review by self. Start
        if(CkmsGpEnDecryptManager.getCkmsIsOpen() == false){
        //tangsha@xdja.com 2016-08-03 modify. for config open or close ckms flow . review by self. End
            Log.d(TAG,"buildUseCaseObservable has init return");
            MultiResult<Object> result = new MultiResult<>();
            result.setResultStatus(INIT_OK);
            return Observable.just(result);
        }
        //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. Start
        if(initForExpire){
            ckmsRepository.resetCkmsHasInit();
        }
        //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. End
        if(initForExpire == false && ckmsRepository.isCkmsHasInit()){
            return ckmsRepository.ckmsInit("");
        }
         /*[S]modify by xienana for bug 2683 @2016/08/12 [review by] tangsha*/
        return ckmsRepository.getCkmsChallenge()
                .doOnNext(new Action1<Map<String, Object>>() {
                              @Override
                              public void call(Map<String, Object> stringStringMap) {
                                  //[S]modify by xienana for bug 1930 @2016/08/12 [review by] tangsha
                                  int retCode = -1;
                                  if (stringStringMap != null) {
                                      retCode = (int) stringStringMap.get(CkmsManager.CKMS_RETRUN_CODE_TAG);
                                  }
                                  if (retCode != 0) {
                                      if(retCode == CkmsManager.CKMS_INNER_NETWORK_ERROR){
                                          throw new CkmsException("" + CkmsException.CODE_CKMS_INIT_NET_ERROR);
                                      /*[S]modify by tangsha@20161110 for 5836*/
                                      }else if(retCode == CkmsManager.CKMS_VERSION_ERROR){
                                          throw new CkmsException("" + CkmsException.CODE_CKMS_VERSION_ERROR);
                                      }else if(retCode == CkmsManager.CKMS_CURRENT_TIME_ERROR){
                                          throw new CkmsException("" + CkmsException.CODE_CKMS_INIT_TIME_ERROR);
                                      }
									  /*[E]modify by tangsha@20161110 for 5836*/
                                      throw new CkmsException("" + CkmsException.CODE_CKMS_SERVER_ERROR);
                                  }//[E]modify by xienana for bug 1930 @2016/08/12 [review by] tangsha
                              }
                          }
                )
                .flatMap(new Func1<Map<String, Object>, Observable<String>>() {
                    @Override
                    public Observable<String> call(Map<String, Object> stringStringMap) {
                       // Log.d(TAG, "buildUseCaseObservable getCkmsChallenge return aStr " + stringStringMap + " to singn");
                        String aStr = (String)stringStringMap.get(CkmsManager.CKMS_CHALLENGE_TAG);
                        Observable<Map<String, String>> result = ckmsRepository.getSignedChallenge(aStr);
                        /*[E]modify by xienana for bug 2683 @2016/08/12 [review by] tangsha*/
                                return result.map(new Func1<Map<String, String>, String>() {
                                    @Override
                                    public String call(Map<String, String> stringMap) {
                                        //[S]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                                        if (stringMap == null || stringMap.isEmpty() || !stringMap.containsKey(REV_CKMS_SIGN_CODE)) {
                                                  throw new CkmsException(""+CkmsException.CODE_CKMS_SERVER_ERROR);
                                        } //[E]modify by xienana for ckms exception @2016/09/20 [reviewed by tangsha]
                                        String signCode = stringMap.get(REV_CKMS_SIGN_CODE);
                                      //  Log.d(TAG, "buildUseCaseObservable getSignChallenge return " + signCode);
                                        return signCode;
                                    }

                                });
                            }
                        })
                .doOnNext(
                        new Action1<String>() {
                            @Override
                            public void call(String aString) {
                                //[S]modify by xienana for ckms exception @2016/08/17 [reviewed by tangsha]
                                if (aString == null || aString.isEmpty()) {
                                    throw new CkmsException(""+CkmsException.CODE_CKMS_SERVER_ERROR);
                                } //[E]modify by xienana for ckms exception @2016/08/17 [reviewed by tangsha]
                            }
                        }
                ).flatMap(
                        new Func1<String, Observable<MultiResult<Object>>>() {
                            @Override
                            public Observable<MultiResult<Object>> call(String aStr) {
                               // Log.d(TAG, "buildUseCaseObservable prepare init, sign code " + aStr);
                                return ckmsRepository.ckmsInit(aStr);
                            }
                        }
                );
    }
}
