package com.xdja.domain_mainframe.usecase.dev;

import android.text.TextUtils;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.exeptions.CheckException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext3UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ldy on 16/4/21.
 * 解除授信设备与账号的关系用例
 * first para:card num;
 * second para:entity(account)
 */
public class RelieveDeviceUseCase extends Ext3UseCase<String,String,String,Boolean> {
    private final UserInfoRepository userInfoRepository;
    /*[S]add by tangsha@20160713 for ckms remove device*/
    private final CkmsRepository ckmsRepository;
    private String cardNo;
    private String entity;
    private String ckmsDevId;
    private String TAG = "anTongRelieveDeviceUseCase";
    /*[E]add by tangsha@20160713 for ckms remove device*/

    @Inject
    public RelieveDeviceUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               UserInfoRepository userInfoRepository,
                                CkmsRepository ckmsRepository
                                ) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
        this.ckmsRepository = ckmsRepository;
    }
    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        if (TextUtils.isEmpty(this.p)) {
            return Observable.error(
                    new CheckException("cardNo cannot null")
            );
        }
        /*[S]add by tangsha@20160713 for ckms remove device*/
        cardNo = this.p;
        if (TextUtils.isEmpty(this.p1)) {
            return Observable.error(
                    new CheckException("account cannot null")
            );
        }
        entity = this.p1;
        ckmsDevId = this.p2;
        if(CkmsGpEnDecryptManager.getCkmsIsOpen() == false){
            return userInfoRepository.relieveDevice(cardNo)
                    .flatMap(new Func1<Void, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(Void aVoid) {
                            return Observable.just(Boolean.TRUE);
                        }
                    });
        }
        return userInfoRepository.relieveDevice(p)
                .flatMap(new Func1<Void, Observable<Map<String, String>>>() {
            @Override
            public Observable<Map<String, String>> call(Void aVoid) {
                return ckmsRepository.getRemoveDevSignOper(entity,ckmsDevId);
            }
        }).flatMap(new Func1<Map<String, String>, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Map<String, String> strMap) {
                if (strMap == null || strMap.isEmpty()
                        || strMap.containsKey(CkmsInitUseCase.REV_CKMS_SIGN_CODE) == false) {
                    LogUtil.getUtils().e(TAG+" getRemoveDevSignOper strMap error");
                    return Observable.just(Boolean.FALSE);
                }
                String opSign = strMap.get(CkmsInitUseCase.REV_CKMS_SIGN_CODE);
                LogUtil.getUtils().d(TAG+" getRemoveDevSignOper opSign " + opSign);
                if (opSign != null && opSign.isEmpty() == false) {
                    return ckmsRepository.removeDev(entity, ckmsDevId, opSign);
                } else {
                    return Observable.just(Boolean.FALSE);
                }
            }
        });
    }
}
