package com.xdja.data_mainframe.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.xdja.comm.ckms.CkmsGpEnDecryptManager;
import com.xdja.data_mainframe.di.PreRepositoryModule;
import com.xdja.data_mainframe.repository.datastore.CkmsStore;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.annotations.StoreSpe;
import com.xdja.dependence.exeptions.CkmsException;
import com.xdja.domain_mainframe.model.MultiResult;
import com.xdja.domain_mainframe.repository.CkmsRepository;
import com.xdja.domain_mainframe.usecase.CkmsInitUseCase;
import com.xdja.frame.data.ckms.CkmsCallback;
import com.xdja.frame.data.ckms.CkmsManager;
import com.xdja.safecenter.ckms.opcode.OpCodeFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by tangsha on 2016/6/28.
 */
public class CkmsRepImp extends ExtRepositoryImp<CkmsStore> implements CkmsRepository {

    private final Context context;
    private final CkmsManager ckmsManager;
    private String TAG = "CkmsRepImp anTongCkms";
    public static String VALID_HOUR = "valid_hours";
    public static String CKMS_HAS_INIT = "has_init";
    private CountDownLatch latch;
    private boolean hasInit = false;

    //创建Entity
    public static final String CREATE_ENTITY = "1";
    //Entity添加设备
    public static final String ADD_DEVICE = "2";
    //Entity强制添加设备
    public static final String FORCE_ADD_DEVICE = "3";
    //移除设备
    public static final String REMOVE_DEVICE = "4";


    @SuppressWarnings("ConstructorWithTooManyParameters")
    @Inject
    public CkmsRepImp(@StoreSpe(DiConfig.TYPE_DISK) CkmsStore diskStore,
                      @StoreSpe(DiConfig.TYPE_CLOUD) CkmsStore cloudStore,
                      @Named(PreRepositoryModule.NAMED_ERRORSTATUS) Set<Integer> errorStatus,
                      @ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context,
                      CkmsManager ckmsManager,
                      Gson gson
                      ) {
        super(diskStore, cloudStore, errorStatus, gson);
        this.context = context;
        this.ckmsManager = ckmsManager;
    }

    /*[S]modify by xienana for bug 2683 @2016/08/11 [review by] tangsha*/
    @Override
    public Observable<Map<String, Object>> getCkmsChallenge() {
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. Start
        return Observable.just("")
                .flatMap(new Func1<String, Observable<Map<String, Object>>>() {
                    @Override
                    public Observable<Map<String, Object>> call(String s) {
                        Map<String, Object> resultMap = ckmsManager.getChallenge();
                        return Observable.just(resultMap);
                    }
                });
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. End
    }
    /*[E]modify by xienana for bug 2683 @2016/08/11 [review by] tangsha*/


    @Override
    public Observable<Map<String, String>> getSignedChallenge(String challenge){
       // Log.d(TAG,"getSignedChallenge enter");
        /*[S]modify by tangsha for CKMS verify sign code (rummager:self)*/
         String codeString = Base64.encodeToString(challenge.getBytes(),Base64.DEFAULT);
        /*[E]modify by tangsha for CKMS verify sign code*/
        Observable<Map<String, String>> signRet = ckmsOperSign(codeString);
        return signRet;
    }


    @Override
    public Observable<MultiResult<Object>> ckmsInit(String signChallenge)
    {
        MultiResult<Object> result = new MultiResult<>();
        Log.d(TAG,"ckmsInit hasInit "+hasInit);
        if(hasInit){
            //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. start
            ckmsInitOk(result,hasInit);
            return Observable.just(result);
            //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. start
        }
        CkmsCallbackImp ckmsCallbackImp = new CkmsCallbackImp();
        latch = new CountDownLatch(1);
        ckmsManager.init(signChallenge,ckmsCallbackImp);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            result.setResultStatus(CkmsInitUseCase.INIT_FAIL);
            return Observable.just(result);
        }
        if(ckmsCallbackImp.retCode == 0){
            //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. start
            ckmsInitOk(result,false);
            //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. End
            return Observable.just(result);
        }
        result.setResultStatus(CkmsInitUseCase.INIT_FAIL);
        /*[S]modify by tangsha@20160913 for return error code, maybe need input pin code*/
        HashMap<String,Object> failCodeInfo = new HashMap<String, Object>();
        failCodeInfo.put(CkmsInitUseCase.INIT_FAIL_CODE_TAG,ckmsCallbackImp.retCode);
        result.setInfo(failCodeInfo);
        /*[E]modify by tangsha@20160913 for return error code, maybe need input pin code*/
        return Observable.just(result);
    }

    //[S]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.
    @Override
    public Observable<Integer> ckmsRelease() {
        return Observable.just("").flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {
                int retCode = ckmsManager.release();
                if(retCode == 0) {
                    hasInit = false;
                }//not process expired, here want to release
                return Observable.just(retCode);
            }
        });
    }
    //[S]tangsha@xdja.com 2016-08-12 add. for exit and not receive message should release ckms. review by self.

    //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. start
    private void ckmsInitOk(MultiResult<Object> result, boolean hasInit){
        result.setResultStatus(CkmsInitUseCase.INIT_OK);
        Map<String,Object> initResultInfo = new HashMap<>();
        initResultInfo.put(VALID_HOUR,validHourTime);
        initResultInfo.put(CKMS_HAS_INIT,hasInit);
        result.setInfo(initResultInfo);
    }
    //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. End


    @Override
    public boolean isCkmsHasInit() {
        return hasInit;
    }

    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. Start
    @Override
    public void resetCkmsHasInit() {
        hasInit = false;
    }
    //tangsha@xdja.com 2016-08-08 modify. for ckms expired to init. review by self. End

    @Override
    public Observable<Integer> getEntityDevices(String entity) {
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. Start
        return Observable.just(entity).flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {
                Map<String,Integer> deviceNumMap = ckmsManager.getEntityDevices(s);
                int deviceNum = -1;
                if(deviceNumMap != null && deviceNumMap.containsKey(CkmsManager.RET_CODE)){
                    int retCode = deviceNumMap.get(CkmsManager.RET_CODE);
                    if(retCode == 0){
                        deviceNum = deviceNumMap.get(CkmsManager.ENTITY_DEVICES_SIZE_KEY);
                    }else if(retCode == CkmsGpEnDecryptManager.CKMS_EXPIRED_ERROR_CODE || retCode ==CkmsGpEnDecryptManager.CKMS_ACCESS_NOTEXIST_CODE){
                        CkmsGpEnDecryptManager.processCkmsExpired();
                        throw new CkmsException("" + CkmsException.CODE_CKMS_SERVER_ERROR);
                    }else if(retCode == CkmsManager.CKMS_VERSION_ERROR){
                        throw new CkmsException("" + CkmsException.CODE_CKMS_VERSION_ERROR);
                    }else {
                        throw new CkmsException("" + CkmsException.CODE_CKMS_SERVER_ERROR);
                    }
                }
                return Observable.just(deviceNum);
            }
        });
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. End
    }

   /*[S]modify return type by tangsha@20160921 for check result fail but go to no add flow*/
    @Override
    public Observable<Integer> isCurrentDeviceAdded(String entity) {
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. Start
        return Observable.just(entity).flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                int ret = ckmsManager.isCurrentDeviceAdded(s);
                if(ret == CkmsGpEnDecryptManager.CKMS_EXPIRED_ERROR_CODE || ret ==CkmsGpEnDecryptManager.CKMS_ACCESS_NOTEXIST_CODE){
                    CkmsGpEnDecryptManager.processCkmsExpired();
                }
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
                return Observable.just(ret);
            }
        });
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. End
    }
    /*[E]modify return type by tangsha@20160921 for check result fail but go to no add flow*/

    //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
    private boolean processRetCode(int code){
        boolean bRet = code == CkmsGpEnDecryptManager.CKMS_SUCC_CODE? true:false;
        if(code == CkmsGpEnDecryptManager.CKMS_EXPIRED_ERROR_CODE || code ==CkmsGpEnDecryptManager.CKMS_ACCESS_NOTEXIST_CODE){
            CkmsGpEnDecryptManager.processCkmsExpired();
        }
        return bRet;
    }
    //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self.End
    @Override
    public Observable<Boolean> createSec(String entity, String signOper) {
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. Start
        final String signOperCode = signOper;
        return Observable.just(entity).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                int ret = ckmsManager.createSec(s,signOperCode);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                boolean bRet = processRetCode(ret);
                return Observable.just(bRet);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
            }
        });
       //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. Start
    }

    @Override
    public Observable<Map<String, String>> getCreateSignOper(String account) {
//         List<String> keyWordList = new ArrayList<String>();
//         keyWordList.add(CREATE_ENTITY);
//         keyWordList.add(account);
         String deviceId = CkmsGpEnDecryptManager.getDeviceFlag();
         String orgStr = OpCodeFactory.Coder().createEntity(deviceId,account);
         String toSignStr = CkmsGpEnDecryptManager.getToSignOpStr(orgStr);
         return ckmsOperSign(toSignStr);
    }

    @Override
    public Observable<Map<String, String>> getAuthAddDevSignOper(String account, String needAddDevId) {
        /*List<String> keyWordList = new ArrayList<String>();
        keyWordList.add(ADD_DEVICE);
        keyWordList.add(account);
        keyWordList.add(needAddDevId);*/
        String deviceId = CkmsGpEnDecryptManager.getDeviceFlag();
        String strOrg = OpCodeFactory.Coder().addDevice(deviceId,account,needAddDevId);
        String toSignStr = CkmsGpEnDecryptManager.getToSignOpStr(strOrg);
        return ckmsOperSign(toSignStr);
    }

    @Override
    public Observable<Map<String, String>> getForceAddDevSignOper(String entity) {
       /* List<String> keyWordList = new ArrayList<String>();
        keyWordList.add(FORCE_ADD_DEVICE);
        keyWordList.add(entity);*/
        String deviceId = CkmsGpEnDecryptManager.getDeviceFlag();
        String orgStr = OpCodeFactory.Coder().forceAddDevice(deviceId,entity);
        String toSignStr = CkmsGpEnDecryptManager.getToSignOpStr(orgStr);
        return ckmsOperSign(toSignStr);
    }

    @Override
    public Observable<Map<String, String>> getRemoveDevSignOper(String entity, String deviceId) {
        /*List<String> keyWordList = new ArrayList<String>();
        keyWordList.add(REMOVE_DEVICE);
        keyWordList.add(entity);
        //tangsha@xdja.com 2016-08-03 modify. for remove opSign code wrong . review by self. Start
        keyWordList.add("["+deviceId+"]");
        //tangsha@xdja.com 2016-08-03 modify. for remove opSign code wrong . review by self. End
        String toSignStr = CkmsGpEnDecryptManager.getToSignOpStr(keyWordList);*/
        String currentDeviceId = CkmsGpEnDecryptManager.getDeviceFlag();
        String orgStr;
        if(currentDeviceId.compareTo(deviceId) == 0){
            orgStr = OpCodeFactory.Coder().quitEntity(currentDeviceId, entity);
        }else {
            orgStr = OpCodeFactory.Coder().removeDevice(currentDeviceId, entity, new String[]{deviceId});
        }
        String toSignStr = CkmsGpEnDecryptManager.getToSignOpStr(orgStr);
        return ckmsOperSign(toSignStr);
    }

    @Override
    public Observable<Boolean> removeDev(String entity, String devNo, String opSign) {
        //tangsha@xdja.com 2016-08-02 modify. for anr. review by self. Start
        final String fDevNo = devNo;
        final String fOpSign = opSign;
        return Observable.just(entity).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                int removeRes = ckmsManager.removeDevice(s,fDevNo,fOpSign);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                boolean bRet = processRetCode(removeRes);
                return Observable.just(bRet);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
            }
        });
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. End
    }
	
	/*[S]add by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
    @Override
    public Observable<Boolean> isAuthedDevAddedToEntity(String entity, String sn) {
        final String objSn = sn;
        final String account = entity;
        return Observable.just(account).
               flatMap(new Func1<String, Observable<Boolean>>() {
                   @Override
                   public Observable<Boolean> call(String s) {
                   boolean isAuthed = ckmsManager.isAuthedDevAddedToEntity(s,objSn);
                       return Observable.just(isAuthed);
                   }
               });
    }
	/*[E]add by xienana@2016/08/08 to fix bug 2202 [review by] tangsha*/
	
    @Override
    public Observable<String> getAddReqId(String entity) {
        //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
        return Observable.just(entity).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                Map<String,Object> addReqRes = ckmsManager.getAddReqId(s);
                String addReqId = "";
                if(addReqRes != null && addReqRes.containsKey(CkmsManager.RET_CODE)){
                    int retCode = (int)addReqRes.get(CkmsManager.RET_CODE);
                    if(retCode == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
                        addReqId = (String)addReqRes.get(CkmsManager.ADD_REQID);
                    }else{
                        processRetCode(retCode);
                    }
                }
                return Observable.just(addReqId);
            }
        });
        //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
    }

    @Override
    public Observable<String> checkAddReqId(String id) {
        //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
        return Observable.just(id).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                Map<String,Object> checkReqRes = ckmsManager.checkAddReq(s);
                String addDevId = "";
                if(checkReqRes != null && checkReqRes.containsKey(CkmsManager.RET_CODE)){
                    int retCode = (int)checkReqRes.get(CkmsManager.RET_CODE);
                    if(retCode == CkmsGpEnDecryptManager.CKMS_SUCC_CODE){
                        addDevId = (String)checkReqRes.get(CkmsManager.ADD_DEV_ID);
                    }else{
                        processRetCode(retCode);
                    }
                }
                return Observable.just(addDevId);
            }
        });
        //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
    }

    @Override
    public Observable<Boolean> addDevice(String currentEntity, String reqId, String opSign) {
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. Start
        final String fReqId = reqId;
        final String fOpSign = opSign;
        return Observable.just(currentEntity).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                int res = ckmsManager.addDevice(s,fReqId,fOpSign);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                boolean bRet = processRetCode(res);
                return Observable.just(bRet);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
            }
        });
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. End
    }

    @Override
    public Observable<Boolean> addDeviceForcebly(String entity, String opSign) {
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. Start
        final String fOpSign = opSign;
        return Observable.just(entity).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                int res = ckmsManager.addDeviceForcibly(s,fOpSign);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                boolean bRet = processRetCode(res);
                return Observable.just(bRet);
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
            }
        });
        //tangsha@xdja.com 2016-08-02 modify. for anr . review by self. End
    }

    public int validHourTime = 0;
    private class CkmsCallbackImp implements CkmsCallback{
        public int retCode = -1;
        @Override
        public void initCallback(int errorCode, int validHour, String errorInfo) {
            //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. start
            retCode = errorCode;
            if(retCode == 0) {
                hasInit = true;
                validHourTime = validHour;
            }
            //tangsha@xdja.com 2016-08-05 modify. for ckms fail but set has init true . review by self. End
            Log.d(TAG,"CkmsCallbackImp errorCode "+errorCode+" validHour "+validHour+" errorInfo "+errorInfo);
            latch.countDown();
        }
    }

    @Override
    public Observable<Map<String, String>> ckmsOperSign(@NonNull String ckmsOperStr){
        /*[S]remove by tangsha for CKMS verify sign code(rummager:self)*/
      //  String codeString = Base64.encodeToString(ckmsOperStr.getBytes(),Base64.DEFAULT);
        /*[S]remove by tangsha for CKMS verify sign code*/
        Observable<Response<Map<String, String>>> ret =  cloudStore.ckmsOperSign(ckmsOperStr);
           return ret.map(
                        new ResponseFunc1<Map<String, String>>(errorStatus, gson)
                                .setCustomStatus(null)
                );
    }



}
