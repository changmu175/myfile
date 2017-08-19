package com.xdja.domain_mainframe.usecase.deviceauth;

import android.text.TextUtils;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.dependence.exeptions.CheckException;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.model.Account;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.domain_mainframe.repository.DeviceAuthRepository;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ldy on 16/5/12.
 */
//[S]modify by xienana @2016/08/31 for bug 3363 [review by]tangsha
public class GetAuthInfo extends Ext2UseCase<String, String, Map<String, Object>> {

    private DeviceAuthRepository.PostDeviceAuthRepository deviceAuthRepository;
    private UserInfoRepository userInfoRepository;
    private CkmsRepository ckmsRepository;
    private String ckmsAuthId;
    private String anTongAuthId;
    private String TAG = "anTongckms GetAuthInfo";

    @Inject
    public GetAuthInfo(ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread,
                       DeviceAuthRepository.PostDeviceAuthRepository deviceAuthRepository,
                       UserInfoRepository userInfoRepository,
                       CkmsRepository ckmsRepository) {
        super(threadExecutor, postExecutionThread);
        this.deviceAuthRepository = deviceAuthRepository;
        this.userInfoRepository = userInfoRepository;
        this.ckmsRepository = ckmsRepository;
    }

    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Map<String, Object>> buildUseCaseObservable() {
        this.anTongAuthId = p;
        this.ckmsAuthId = p1;
        if (TextUtils.isEmpty(anTongAuthId)) {
            return Observable.error(
                    new CheckException("auth ID cannot null")
            );
        }
        if (CkmsGpEnDecryptManager.getCkmsIsOpen()) {
            return this.deviceAuthRepository.getAuthInfo(anTongAuthId)
                    .flatMap(new Func1<Map<String, Object>, Observable<Map<String, Object>>>() {
                        @Override
                        public Observable<Map<String, Object>> call(final Map<String, Object> stringObjectMap) {
                            if (stringObjectMap != null) {
                                return userInfoRepository.getCurrentAccountInfo().map(new Func1<Account, Map<String, Object>>() {
                                    @Override
                                    public Map<String, Object> call(Account account) {
                                        if (account != null) {
                                            String strAccount = account.getAccount();
                                            String aliasAccount = account.getAlias();
                                            stringObjectMap.put("strAccount", strAccount);
                                            stringObjectMap.put("aliasAccount", aliasAccount);
                                            LogUtil.getUtils().d(TAG+" deviceAuthRepository.getAuthInfo stringObjectMap =" + stringObjectMap);
                                            return stringObjectMap;
                                        }
                                        throw new CkmsException(CkmsException.CODE_CKMS_AUTH_INFO_INVALID);
                                    }
                                });
                            }
                            return Observable.error(new CkmsException(CkmsException.CODE_CKMS_AUTH_INFO_INVALID));
                        }
                    }).flatMap(new Func1<Map<String, Object>, Observable<Map<String, Object>>>() {
                        @Override
                        public Observable<Map<String, Object>> call(final Map<String, Object> stringObjectMap) {
                            String sn = (String) stringObjectMap.get("sn");
                            String digtalAccount = (String) stringObjectMap.get("strAccount");
                            return ckmsRepository.isAuthedDevAddedToEntity(digtalAccount, sn).map(new Func1<Boolean, Map<String, Object>>() {
                                @Override
                                public Map<String, Object> call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        if (ckmsAuthId != null) {
                                            throw new CkmsException(CkmsException.CODE_CKMS_AUTH_INFO_INVALID);
                                        }
                                        stringObjectMap.put("isCkmsAuthed", true);
                                    } else {
                                        if (ckmsAuthId == null) {
                                            throw new CkmsException(CkmsException.CODE_CKMS_AUTH_INFO_INVALID);
                                        }
                                        stringObjectMap.put("isCkmsAuthed", false);
                                    }
                                    LogUtil.getUtils().d(TAG+" ckmsRepository.isAuthedDevAddedToEntity stringObjectMap =" + stringObjectMap);
                                    return stringObjectMap;
                                }
                            });
                        }
                    }).flatMap(new Func1<Map<String, Object>, Observable<Map<String, Object>>>() {
                        @Override
                        public Observable<Map<String, Object>> call(final Map<String, Object> stringObjectMap) {
                            boolean isCkmsAuthed = (boolean) stringObjectMap.get("isCkmsAuthed");
                            if (!isCkmsAuthed) {
                                return ckmsRepository.checkAddReqId(ckmsAuthId).map(new Func1<String, Map<String, Object>>() {
                                    @Override
                                    public Map<String, Object> call(String s) {
                                        if (s.equals("")) {
                                            throw new CkmsException(CkmsException.CODE_CKMS_AUTH_INFO_INVALID);
                                        } else {
                                            return stringObjectMap;
                                        }
                                    }
                                });
                            }
                            return Observable.just(stringObjectMap);
                        }
                    });
        } else {
            return this.deviceAuthRepository.getAuthInfo(anTongAuthId)
                    .flatMap(new Func1<Map<String, Object>, Observable<Map<String, Object>>>() {
                        @Override
                        public Observable<Map<String, Object>> call(final Map<String, Object> stringObjectMap) {
                            if (stringObjectMap != null) {
                                return userInfoRepository.getCurrentAccountInfo().map(new Func1<Account, Map<String, Object>>() {
                                    @Override
                                    public Map<String, Object> call(Account account) {
                                        if (account != null) {
                                            String strAccount = account.getAccount();
                                            String aliasAccount = account.getAlias();
                                            stringObjectMap.put("strAccount", strAccount);
                                            stringObjectMap.put("aliasAccount", aliasAccount);
                                            LogUtil.getUtils().d(TAG+" deviceAuthRepository.getAuthInfo stringObjectMap =" + stringObjectMap);
                                            return stringObjectMap;
                                        }
                                        throw new CkmsException(CkmsException.CODE_CKMS_AUTH_INFO_INVALID);
                                    }
                                });
                            }
                            return Observable.error(new CkmsException(CkmsException.CODE_CKMS_AUTH_INFO_INVALID));
                        }
                    });
        }
    }
}//[S]modify by xienana @2016/08/31 for bug 3363 [review by]tangsha