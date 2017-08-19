package com.xdja.domain_mainframe.usecase.deviceauth;

import android.text.TextUtils;
import android.util.Log;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.exeptions.CheckException;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext5UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Summary:为设备授权</p>
 * <p>Description:fill参数方法为授权ID</p>
 * <p>Package:com.xdja.domain_mainframe.usecase.deviceauth</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/23</p>
 * <p>Time:9:27</p>
 */
  /*[S]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
public class AuthDeviceUseCase extends Ext5UseCase<String, String, String, String, String, Void> {
    private DeviceAuthRepository.PostDeviceAuthRepository deviceAuthRepository;
    /*[S]modify by tangsha for ckms auth add device*/
    private CkmsRepository ckmsRepository;
    private String account;

    private String ckmsAuthId;
    private String anTongAuthId;
    private String cardNo;
    private String sn;
    private String TAG = "anTongAuthDeviceUseCase";
    /*[E]modify by tangsha for ckms auth add device*/

    @Inject
    public AuthDeviceUseCase(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             DeviceAuthRepository.PostDeviceAuthRepository deviceAuthRepository,
                             CkmsRepository ckmsRepository) {
        super(threadExecutor, postExecutionThread);
        this.deviceAuthRepository = deviceAuthRepository;
        this.ckmsRepository = ckmsRepository;
    }

    @Override
    public Observable<Void> buildUseCaseObservable() {
        account = this.p;
        ckmsAuthId = this.p1;
        anTongAuthId = this.p2;
        cardNo = this.p3;
        sn = this.p4;
        Log.d(TAG, "buildUseCaseObservable account " + account + " ckmsAuthId "
                + ckmsAuthId + " anTongAuthId " + anTongAuthId + " cardNo " + cardNo);

        if (TextUtils.isEmpty(this.p2)) {
            return Observable.error(
                    new CheckException("auth id  cannot null")
            );
        }
        if (TextUtils.isEmpty(this.p3)) {
            return Observable.error(
                    new CheckException("cardNo cannot null")
            );
        }
        if (CkmsGpEnDecryptManager.getCkmsIsOpen()) {
            if (TextUtils.isEmpty(ckmsAuthId)) {
                return deviceAuthRepository.authDevice(anTongAuthId, cardNo);
            } else {
                //[S]modify by xienana for 3728 @2016/09/07 [reviewed by tangsha]
                return ckmsRepository.isAuthedDevAddedToEntity(account, sn).flatMap(new Func1<Boolean, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Boolean aBoolean) {
                        if (aBoolean) {
                            return deviceAuthRepository.authDevice(anTongAuthId, cardNo);
                        } else { //[E]modify by xienana for 3728 @2016/09/07 [reviewed by tangsha]
                            return ckmsRepository.checkAddReqId(ckmsAuthId)
                                    .flatMap(new Func1<String, Observable<String>>() {
                                        @Override
                                        public Observable<String> call(String otherDeviceId) {
                                            if (otherDeviceId != null && otherDeviceId.isEmpty() == false) {
                                                Observable<Map<String, String>> signOper = ckmsRepository.getAuthAddDevSignOper(account, otherDeviceId);
                                                return signOper.map(new Func1<Map<String, String>, String>() {
                                                    @Override
                                                    public String call(Map<String, String> signOper) {
                                                        Log.d(TAG, "buildUseCaseObservable get add operation sign return " + signOper);
                                                        if (signOper != null && signOper.isEmpty() == false
                                                                && signOper.containsKey(CkmsInitUseCase.REV_CKMS_SIGN_CODE)) {
                                                            return signOper.get(CkmsInitUseCase.REV_CKMS_SIGN_CODE);
                                                        }
                                                        return "";
                                                    }
                                                });
                                            }
                                            return Observable.just("");
                                        }
                                    }).flatMap(new Func1<String, Observable<Boolean>>() {
                                        @Override
                                        public Observable<Boolean> call(String opSign) {
                                            if (opSign == null || opSign.isEmpty()) {
                                                return Observable.just(Boolean.FALSE);
                                            }
                                            return ckmsRepository.addDevice(account, ckmsAuthId, opSign);
                                        }
                                    }).flatMap(new Func1<Boolean, Observable<Void>>() {
                                        @Override
                                        public Observable<Void> call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                return deviceAuthRepository.authDevice(anTongAuthId, cardNo);
                                            } else {
                                                return Observable.error(new CkmsException("" + CkmsException.CODE_CKMS_AUTH_DEVICE_ERROR));
                                            }
                                        }
                                    });
                        }
                    }
                });

            }
        }
        return deviceAuthRepository.authDevice(anTongAuthId, cardNo);
    }/*[S]modify by xienana@2016/08/31 to fix bug 3363 [review by] tangsha*/
}
