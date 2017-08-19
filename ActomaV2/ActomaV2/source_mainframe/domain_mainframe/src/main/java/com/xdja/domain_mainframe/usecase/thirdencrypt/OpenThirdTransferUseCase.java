package com.xdja.domain_mainframe.usecase.thirdencrypt;

import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.comm.ckms.ThirdEncryptCach;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.encrypt.ThirdEncryptBean;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.domain_mainframe.repository.AccountRepository;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext4UseCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by tangsha on 2016/7/25.
 * p:current account
 * p1:destAccount(who to open with)
 * p2:package name(third package name)
 * p3:app name(third application name)
 */
public class OpenThirdTransferUseCase extends Ext4UseCase<String,String,String,String,Boolean> {
    String currentAccount;
    String destAccount;
    String thirdPackageName;
    String thirdAppName;
    String secKey;
    byte[] key;
    String TAG = "OpenThirdTransferUseCase";
    private CkmsRepository ckmsRepository;
    private AccountRepository.PostAccountRepository accountRepository;
    private String THIRDEN_TOPIC = "atp_thirden";
    private final int KEY_HAS_EXIST = 1;
    private final int GOURP_CREATE_OK = 2;
    private final int GROUP_CREATE_FAIL = 3;
    private final int BYTE_ARRAY_SIZE = 16;

    @Inject
    public OpenThirdTransferUseCase(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             CkmsRepository ckmsRepository,
                             AccountRepository.PostAccountRepository accountRepository) {
        super(threadExecutor, postExecutionThread);
        this.ckmsRepository = ckmsRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable() {
        currentAccount = this.p;
        destAccount = this.p1;
        thirdPackageName = this.p2;
        thirdAppName = this.p3;

        if(checkParaEffect(currentAccount) && checkParaEffect(destAccount) && checkParaEffect(thirdPackageName)){
                   //check whether group exist
            final String groupId = CkmsGpEnDecryptManager.getGroupIdWithFriend(currentAccount,destAccount);
            ThirdEncryptBean thirdEncryptBean = ThirdEncryptCach.getInstance().getCacheKeyInfo(groupId);
            boolean keyHasExist = false;
            if(thirdEncryptBean != null){
                keyHasExist = true;
                secKey = thirdEncryptBean.getSecKey();
                key = thirdEncryptBean.getKey();
                LogUtil.getUtils(TAG).d("buildUseCaseObservable key has exist------------");
            }
            return Observable.just(keyHasExist)
                    .flatMap(new Func1<Boolean, Observable<Integer>>() {
                        @Override
                        public Observable<Integer> call(Boolean aBoolean) {
                            //sec key not exist, to check is group exist
                            if(!aBoolean){
                                boolean groupExist = CkmsGpEnDecryptManager.isEntitySGroupsExist(currentAccount,groupId);
                                //group not exist,get sign op,then create group
                                if(!groupExist) {
                                    ArrayList<String> groupEntities = new ArrayList<>();
                                    groupEntities.add(currentAccount);
                                    groupEntities.add(destAccount);
                                    String opStr = CkmsGpEnDecryptManager.getGroupOpStr(currentAccount, groupId, groupEntities,CkmsGpEnDecryptManager.CREATE_GROUP);
                                    return ckmsRepository.ckmsOperSign(opStr)
                                            .map(new Func1<Map<String, String>, Integer>() {
                                                @Override
                                                public Integer call(Map<String, String> stringStringMap) {
                                                    //get add operation sign code fail
                                                    if (stringStringMap == null || stringStringMap.isEmpty()
                                                            || !stringStringMap.containsKey(CkmsInitUseCase.REV_CKMS_SIGN_CODE)) {
                                                        LogUtil.getUtils(TAG).e("group create fail - not have signCode.");
                                                        return GROUP_CREATE_FAIL;
                                                    }
                                                    String signCode = stringStringMap.get(CkmsInitUseCase.REV_CKMS_SIGN_CODE);
                                                    int createRes = CkmsGpEnDecryptManager.createSGroupWithFriend(groupId, currentAccount, destAccount, signCode);
                                                    if(createRes == CkmsGpEnDecryptManager.EXIST_NOT_AUTH_DEVICE){
                                                         throw new CkmsException(new Throwable(CkmsException.CODE_EXIST_NOT_AUTH_DEVICE),CkmsException.CODE_EXIST_NOT_AUTH_DEVICE);
                                                    }
                                                    return createRes == CkmsGpEnDecryptManager.CREATE_SGROUP_SUCCEED ? GOURP_CREATE_OK : GROUP_CREATE_FAIL;
                                                }
                                            });
                                }else{
                                    //group exist,just jump to next
                                    return Observable.just(GOURP_CREATE_OK);
                                }
                            }else{
                                  //key exist,just jump to next
                                  return Observable.just(KEY_HAS_EXIST);
                            }
                        }
                    }).flatMap(new Func1<Integer, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(Integer integer) {
                            switch (integer){
                                case GOURP_CREATE_OK:
                                    key = new byte[BYTE_ARRAY_SIZE];
                                    Random random = new Random(System.currentTimeMillis());
                                    random.nextBytes(key);
                                    //[S]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
                                    Map<String,Object> secKeyInfo = CkmsGpEnDecryptManager.encryptByteToString(currentAccount,groupId,key);
                                    if(secKeyInfo != null && (int)secKeyInfo.get(CkmsGpEnDecryptManager.RESULT_CODE_TAG) == CkmsGpEnDecryptManager.CKMS_SUCC_CODE) {
                                        secKey = (String)secKeyInfo.get(CkmsGpEnDecryptManager.RESULT_INFO_TAG);
                                    }
                                    //[E]tangsha@xdja.com 2016-08-19 modify. for ckms modify return type. review by self.
                                    //no break here,need do same as KEY_HAS_EXIST
                                case KEY_HAS_EXIST:
                                    if(secKey != null && !secKey.isEmpty()) {
                                        //get secKey,send message to dest account
                                        ArrayList<String> destList = new ArrayList<>();
                                        destList.add(destAccount);
                                        return accountRepository.sendThirdEnPushInfo(THIRDEN_TOPIC, destList,
                                                CkmsGpEnDecryptManager.getSendThirdEnPushMsg(groupId, secKey))
                                                .map(new Func1<Void, Boolean>() {
                                                    @Override
                                                    public Boolean call(Void aVoid) {
                                                        return true;
                                                    }
                                                });
                                    }else{
                                        LogUtil.getUtils(TAG).e("secKey is null or is empty");
                                        return Observable.just(Boolean.FALSE);
                                    }
                                case GROUP_CREATE_FAIL:
                                    return Observable.just(Boolean.FALSE);
                                default:
                                    return Observable.just(Boolean.FALSE);
                            }
                        }
                    }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(Boolean aBoolean) {
                            if(aBoolean) {
                                Map<String, Object> info = new HashMap<>();
                                info.put(IEncryptUtils.THIRD_KEY, key);
                                info.put(IEncryptUtils.THIRD_SEC_KEY, secKey);
                                info.put(IEncryptUtils.THIRD_PACKAGE_NAME, thirdPackageName);
                                info.put(IEncryptUtils.THIRD_DEST_ACCOUNT, destAccount);
                                info.put(IEncryptUtils.THIRD_GROUP_ID, groupId);
                                //set info to hook service
                                if (IEncryptUtils.setCurrentKey(info) == 0) {
                                    ThirdEncryptCach.getInstance().putCacheKeyInfo(groupId, key, secKey);
                                    return Observable.just(Boolean.TRUE);
                                } else {
                                    LogUtil.getUtils(TAG).e("setCurrentKey fail");
                                    return Observable.just(Boolean.FALSE);
                                }
                            }else{
                                //group fail, or secKey fail, or send fail
                                return Observable.just(Boolean.FALSE);
                            }
                        }
                    })
                    ;
        }else{
            //check parameters wrong
            return Observable.just(Boolean.FALSE);
        }
    }

    private boolean checkParaEffect(String param){
        boolean effect = false;
        if(param != null && !param.isEmpty()){
            effect = true;
        }else{
            LogUtil.getUtils(TAG).e("checkParaEffect param is empty");
        }
        return effect;
    }
}
