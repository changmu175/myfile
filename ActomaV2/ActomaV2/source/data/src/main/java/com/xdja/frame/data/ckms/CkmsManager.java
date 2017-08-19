package com.xdja.frame.data.ckms;

import android.content.Context;

import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.safekeyservice.jarv2.EntityManager;
import com.xdja.safekeyservice.jarv2.SecuritySDKManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by tangsha on 2016/6/29.
 */
public class CkmsManager {

    private Context context;
    private static String TAG = "CkmsManager anTongCkms ";
    private String deviceId = "";


    @Inject
    public CkmsManager(@ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context) {
        this.context = context;
    }

    /*[S]modify by xienana for bug 2683 @2016/08/12 [review by] tangsha*/
    public String CKMS_VERSION_ERROR_INFO = "Binder invocation to an incorrect interface";
    public static String CKMS_RETRUN_CODE_TAG = "ret_code";
    public static String CKMS_CHALLENGE_TAG = "challenge";
    public static final int NO_DEVICE_CODE = 40015;
    public static final int CKMS_INNER_NETWORK_ERROR = 70001;
    public static final int CKMS_CURRENT_TIME_ERROR = 50583;
    public static final int CKMS_VERSION_ERROR = -1000;
    public static final int CKMS_FAIL = -1001;
    public static final int CKMS_JSON_ERROR_CODE = -1002;
    public static String INIT_JSON_ERROR = "ckms init JSONException";

    public Map<String, Object> getChallenge() {
        Map<String, Object> challengeRetMap = new HashMap<>();
        try {
            JSONObject challengeResult = SecuritySDKManager.getInstance().getChallenge(context);
            LogUtil.getUtils().d(TAG+" getChallenge challengeResult " + challengeResult);
            int ret_code = challengeResult.getInt("ret_code");
            challengeRetMap.put(CKMS_RETRUN_CODE_TAG, ret_code);
            if (ret_code == 0) {
                JSONObject result = challengeResult.getJSONObject("result");
                String retChallenge = result.getString("challenge");
                challengeRetMap.put(CKMS_CHALLENGE_TAG, retChallenge);
            } else {
			    /*[S]modify by tangsha@20161110 for 5836*/
			    if(ret_code != CKMS_INNER_NETWORK_ERROR && ret_code != CKMS_CURRENT_TIME_ERROR){
			        challengeRetMap.put(CKMS_RETRUN_CODE_TAG, CKMS_FAIL);
			    }
				/*[E]modify by tangsha@20161110 for 5836*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+ "getChallenge JSONException " + e.toString());
            challengeRetMap.put(CKMS_RETRUN_CODE_TAG, CKMS_JSON_ERROR_CODE);
            //[S]modify by xienana for bug 1930 @2016/08/23 [review by] tangsha
        } catch (SecurityException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+ "getChallenge SecurityException " + e.toString());
            if (e.getMessage().contains(CKMS_VERSION_ERROR_INFO)) {
                challengeRetMap.put(CKMS_RETRUN_CODE_TAG, CKMS_VERSION_ERROR);
            } else {
                challengeRetMap.put(CKMS_RETRUN_CODE_TAG, CKMS_FAIL);
            }
            //[E]modify by xienana for bug 1930 @2016/08/23 [review by] tangsha
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+ "getChallenge IllegalArgumentException " + e.toString());
            challengeRetMap.put(CKMS_RETRUN_CODE_TAG, CKMS_FAIL);
        }
        return challengeRetMap;
    }
    /*[E]modify by xienana for bug 2683 @2016/08/12 [review by] tangsha*/

    public void init(String signChallenge, final CkmsCallback callback) {
        SecuritySDKManager.getInstance()
                .init(context, signChallenge, new SecuritySDKManager.InitCallBack() {
                    @Override
                    public void onInitComplete(JSONObject result) {
                        LogUtil.getUtils().d(TAG+" onInitComplete validHour result " + result);
                        int validHour = -1;
                        String errorInfo = "";
                        try {
                            int retCode = result.getInt("ret_code");
                            if (retCode == 0) {
                                JSONObject validTime = result.getJSONObject("result");
                                validHour = validTime.getInt("valid_hours");
                            } else {
                                errorInfo = result.getString("err_msg");
                            }
                            callback.initCallback(retCode, validHour, errorInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.initCallback(CKMS_JSON_ERROR_CODE, 0, INIT_JSON_ERROR);
                        }
                    }
                });
    }


   /*return: -1, can not getDevices;
               0,no device;
              >0,device size
    * */
    public static int CKMS_INTERFACE_EXEC_FAIL = 50001;
    public static String ENTITY_DEVICES_SIZE_KEY = "entity_devices_size_key";
    public Map<String,Integer> getEntityDevices(String entity){
        int size = -1;
        List<String> entitys = new ArrayList<String>();
        entitys.add(entity);
        Map<String,Integer> retMap = new HashMap<>();
        try {
            JSONObject result = EntityManager.getInstance().getDevices(entitys);
            LogUtil.getUtils().d(TAG+" getEntityDevices entity "+entity+" result "+result);
            int ret_code = result.getInt("ret_code");
            retMap.put(RET_CODE,ret_code);
            if(ret_code == 0){
                JSONObject infos = result.getJSONObject("result");
                JSONArray listInfos = infos.optJSONArray(entity);
                if(listInfos != null) {
                    size = listInfos.length();
                }
                retMap.put(ENTITY_DEVICES_SIZE_KEY,size);
            }else if(ret_code == NO_DEVICE_CODE){
                size = 0;
                retMap.put(RET_CODE,0);
                retMap.put(ENTITY_DEVICES_SIZE_KEY,size);
               //[S]modify by xienana for bug 1947 @2016/08/23 [review by] tangsha
            }else if (ret_code == CKMS_INTERFACE_EXEC_FAIL){
                retMap.put(RET_CODE,CKMS_VERSION_ERROR);
            }
			 //[E]modify by xienana for bug 1947 @2016/08/23 [review by] tangsha
        } catch (JSONException e) {
            e.printStackTrace();
            retMap.put(RET_CODE,CKMS_JSON_ERROR_CODE);
            LogUtil.getUtils().e(TAG+"getEntityDevices JSONException "+e.toString());
        }catch(IllegalArgumentException e){
            retMap.put(RET_CODE,CKMS_FAIL);
            LogUtil.getUtils().e(TAG+ "getEntityDevices IllegalArgumentException " + e.toString());
        }
        return retMap;
    }



    /**
     * 查询被授权设备是否已经通过ckms授权成功
     *
     * @param entity 当前账号
     * @param sn     被授权设备的sn号（相当于deviceId）
     *               created by xienana on 2016/08/05
     */
    public boolean isAuthedDevAddedToEntity(String entity, String sn) {
        List<String> entities = new ArrayList<>();
        entities.add(entity);
        try {
            JSONObject result = EntityManager.getInstance().getDevices(entities);
            int ret_code = result.getInt("ret_code");
            if (ret_code == 0) {
                JSONObject resultInfo = result.getJSONObject("result");
                LogUtil.getUtils().d(TAG+" isAuthedDevAddedToEntity SN "+sn +" resultInfo " + resultInfo);
                if (resultInfo != null) {
                    JSONArray resultArray = resultInfo.getJSONArray(entity);
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject resultJs = resultArray.getJSONObject(i);
                        for (int j = 0; j < resultJs.length(); j++) {
                            if (resultJs.getString("device_id").equals(sn)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+ "isAuthedDevAddedToEntity JSONException "+e.toString());
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+ "isAuthedDevAddedToEntity IllegalArgumentException " + e.toString());
        }
        return false;
    }


    public int isCurrentDeviceAdded(String entity){
       try{
           JSONObject result = EntityManager.getInstance().isCurrentDevAdded2Entity(entity);
           LogUtil.getUtils().d(TAG+" isCurrentDeviceAdded result "+result);
           return processNoInfoResult(result);
       }catch(IllegalArgumentException e){
           LogUtil.getUtils().e(TAG+ "isCurrentDeviceAdded IllegalArgumentException " + e.toString());
           return CKMS_FAIL;
       }
    }

    public int createSec(String entity, String signOper){
        try{
            JSONObject result = EntityManager.getInstance().create(entity,signOper);
            LogUtil.getUtils().d(TAG+" createSec entity "+entity+" signOper "+signOper+" result "+result);
            return processNoInfoResult(result);
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+ "createSec IllegalArgumentException " + e.toString());
            return CKMS_FAIL;
        }

    }

    //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
    public static String RET_CODE = "retCode";
    public static String ADD_REQID = "adding_dev_req_id";
    public Map<String,Object> getAddReqId(String entity){
        String reqId = "";
        Map<String,Object> retMap = null;
        try {
            JSONObject result = EntityManager.getInstance().getAddingDeviceRequest(entity);
            LogUtil.getUtils().d(TAG+" getAddReqId "+result);
            int retCode = result.getInt("ret_code");
            retMap = new HashMap<>();
            retMap.put(RET_CODE,retCode);
            if(retCode == 0){
                JSONObject reqIdObj = result.getJSONObject("result");
                reqId = reqIdObj.getString(ADD_REQID);
                retMap.put(ADD_REQID,reqId);
            }else{
                LogUtil.getUtils().e(TAG+"getAddReqId retCode "+retCode+" errorInfo "+result.getString("err_msg"));
            }
        } catch (JSONException e) {
            retMap = null;
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+"getAddReqId JSONException "+e.toString());
        }catch(IllegalArgumentException e){
            retMap = null;
            LogUtil.getUtils().e(TAG+ "getAddReqId IllegalArgumentException " + e.toString());
        }
        return retMap;
    }

    public static String ADD_DEV_ID = "add_dev_id";
    public Map<String, Object> checkAddReq(String reqId){
        Map<String,Object> retMap = null;
        try {
            JSONObject result = EntityManager.getInstance().checkAddingDeviceReq(reqId);
            LogUtil.getUtils().d(TAG+" checkAddReq "+result);
            int retCode = result.getInt("ret_code");
            retMap = new HashMap<>();
            retMap.put(RET_CODE, retCode);
            if(retCode == 0){
                JSONObject reqIdObj = result.getJSONObject("result");
                String otherDevId = reqIdObj.getString(ADD_DEV_ID);
                retMap.put(ADD_DEV_ID,otherDevId);
            }else{
                LogUtil.getUtils().e(TAG+"checkAddReq retCode "+retCode+" errorInfo "+result.getString("err_msg"));
            }
        } catch (JSONException e) {
            retMap = null;
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+"checkAddReq JSONException "+e.toString());
        }catch(IllegalArgumentException e){
            retMap = null;
            LogUtil.getUtils().e(TAG+ "checkAddReq IllegalArgumentException " + e.toString());
        }
        return retMap;
    }
    //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End

    public int addDevice(String currentEntity, String addingDevReqId,String operSignature){
        try{
            JSONObject result = EntityManager.getInstance().addDevice(currentEntity,addingDevReqId,operSignature);
            LogUtil.getUtils().d(TAG+" addDevice currentEntity "+currentEntity+" addingDevReqId "+addingDevReqId+" result "+result);
            return processNoInfoResult(result);
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+ "addDevice IllegalArgumentException " + e.toString());
            return CKMS_FAIL;
        }
    }

    public int addDeviceForcibly(String entity, String operSignature){
       try{
           JSONObject result = EntityManager.getInstance().addDeviceForcibly(entity,operSignature);
           LogUtil.getUtils().d(TAG+" addDeviceForcibly entity "+entity+" result "+result);
           return processNoInfoResult(result);
       }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+ "addDeviceForcibly IllegalArgumentException " + e.toString());
            return CKMS_FAIL;
        }
    }

    public int removeDevice(String entity, String devNo, String opSign){
        try{
            JSONObject result = EntityManager.getInstance().removeDevice(entity,devNo,opSign);
            LogUtil.getUtils().d(TAG+" removeDevice entity "+entity+" devNo "+devNo +" result "+result);
            return processNoInfoResult(result);
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+ "removeDevice IllegalArgumentException " + e.toString());
            return CKMS_FAIL;
        }
    }
    private int processNoInfoResult(JSONObject result){
        int retCode = CKMS_FAIL;
        try {
            retCode = result.getInt("ret_code");
            if(retCode != 0){
                LogUtil.getUtils().e(TAG+"processNoInfoResult retCode "+retCode+" errorInfo "+result.getString("err_msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+"processNoInfoResult JSONException "+e.toString());
        }
        return retCode;
    }

    public int release() {
        int retCode = CKMS_FAIL;
        try {
            JSONObject res = SecuritySDKManager.getInstance().release();
            retCode = res.getInt("ret_code");
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+ "release JSONException " + e.toString());
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+ "release IllegalArgumentException " + e.toString());
        }
        return retCode;
    }

}
