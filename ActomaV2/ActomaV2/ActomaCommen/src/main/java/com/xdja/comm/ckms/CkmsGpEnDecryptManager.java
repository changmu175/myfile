package com.xdja.comm.ckms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.xdja.comm.encrypt.IEncryptUtils;
import com.xdja.comm.uitl.ListUtils;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.crypto.XDJACrypto;
import com.xdja.safecenter.ckms.opcode.OpCodeFactory;
import com.xdja.safekeyservice.jarv2.EntityManager;
import com.xdja.safekeyservice.jarv2.SecurityConstants;
import com.xdja.safekeyservice.jarv2.SecurityGroupManager;
import com.xdja.safekeyservice.jarv2.SecuritySDKManager;
import com.xdja.scservice_domain.encrypt.PlainDataBean;
import com.xdja.xdjacrypto.XdjaCrypto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tangsha on 2016/7/15.
 */
public class CkmsGpEnDecryptManager {

    private static String TAG = "anTongCkmsGpEnDecryptManager ";
    public static String ckmsDeviceId = "";
    //退出Entity
    public static final String QUIT_ENTITY = "5";
    //销毁Entity
    public static final String DESTROY_ENTITY = "6";
    //创建Group
    public static final String CREATE_GROUP = "7";
    //Group新增Entity
    public static final String ADD_ENTITY = "8";
    //Group移除Entity
    public static final String REMOVE_ENTITY = "9";
    //退出Group
    public static final String QUIT_GROUP  = "10";
    //销毁Group
    public static final String DESTROY_GROUP = "11";
    //ckms相关操作成功
    public static int CKMS_SUCC_CODE = 0;
    //ckms相关操作失败
    public static int CKMS_FAIL_CODE = 1;
    //账号相关的entity已经在sgroup中
    public static final int ENTITY_IN_GROUP = 2;
    //账号相关的entity不在sgroup中
    public static final int ENTITY_NOT_IN_GROUP = 3;
    //sgroup不存在
    public static final int GROUP_NOT_EXIST = 4;
    //操作中存在未关联安全设备的entity（账号）
    public static final int EXIST_NOT_AUTH_DEVICE = 5;
    //创建sgroup成功
    public static final int CREATE_SGROUP_SUCCEED = 6;
    //创建sgroup失败（除了已经关注原因外的失败）
    public static final int CREATE_SGROUP_FAIL = 7;
    //获取sGroup信息失败
    public static final int GET_GROUP_INFO_FAIL = 8;
    //该版本不支持CKMS
    public static int VERSION_NOT_SUPPORT_CKMS = 9;
    //操作失败，网络错误
    public static int CKMS_NETWORK_ERROR = 10;
    //解密失败，因为数据格式错误
    public static int DECRYPT_DATA_FORMATE_WRONG = 21;
    //参数错误
    public static int CKMS_PARAMETER_WRONG = 22;
    //CKMS操作失败，ckms需要重新初始化（内部已经处理重新初始化，业务层可能需要特别的提示）
    public static final int  CKMS_EXPIRED_TO_INIT = 1000;


    /*[S]add by xienana for CKMS  2016/08/09(rummager:tangsha)*/
    private static final int ENDECRY_ENTITY_NOT_IN_SGROUP = 40001;

    private static final int CKMS_INNER_NETWORK_ERROR = 70001;

    private static final int ENTITY_NOT_IN_SGROUP_CODE = 70513;

    private static final int SGROUP_NOT_EXIST_CODE = 70514;

    private static final int SGROUP_ALREADY_EXIST_CODE = 70515;

    private static final int CURRENT_DEVICE_UNBIND_ENTITY_CODE = 70257;

    private static final int CKMS_DECRYPT_FORMATE_ERROR = 40005;

    private static final int CKMS_NO_DEVICE_ERROR = 40015;

    /*[E]add by xienana for CKMS  2016/08/09(rummager:tangsha)*/

	//start:add by wal for discuss talk 2016/08/03
    public static final int START_FRIEND_TALK = 1001;
    public static final int START_FRIEND_VOIP = 1002;
    public static final int START_GROUP_TALK = 1003;
    //end:add by wal for discuss talk 2016/08/03


    /**
     * 获取groupId(2人群组)
     *
     * @param    currentAccount     用户账号(数字)
     * @param    friend             好友账户（数字）
     * @return   groupId            返回与好友的groupId（为两个人数字账号拼接而成）
     *                              created by tangsha on 2016/07/12
     */
    public static String getGroupIdWithFriend(String currentAccount, String friend) {
        String groupId = "";
        int compareRes = currentAccount.compareTo(friend);
        if(compareRes > 0){
            groupId = friend+"&"+currentAccount;
        }else if(compareRes < 0){
            groupId = currentAccount+"&"+friend;
        }else {
            LogUtil.getUtils().e(TAG+"getGroupIdWithFriend same account, currentAccount "+currentAccount+" friend " + friend);
        }
        return groupId;
    }

    /*[S]add by xienana for check is Gourp exist(rummager:tangsha)*/
    /**
     * 查询某个用户的指定groupId是否存在
     *
     * @param currentAccount 用户账号
     * @param groupId        查询的组id
     * @return  true              用户查询指定sGroup已存在
     *          false             用户查询指定sGroup不存在
     *                       created by xienana on 2016/07/23
     */
    public static boolean isEntitySGroupsExist(String currentAccount, String groupId) {
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. Start
        if(!getCkmsIsOpen()){// modified by ycm for lint 2017/02/13
            return true;
        }
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. End
		/*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        if (TextUtils.isEmpty(currentAccount) || TextUtils.isEmpty(groupId)){
		/*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
            LogUtil.getUtils().e(TAG+"isEntitySGroupsExist check parameter error, currentAccount "+currentAccount+" groupId "+groupId);
            return false;
        }
        List<String> result = getSingleEntitySGroups(currentAccount);
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).equals(groupId)) {
                    LogUtil.getUtils().d(TAG+" isEntitySGroupsExist group has exist");
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 查询单个用户所在的sGroups信息
     *
     * @param currentEntity 当前用户实体
     * @return List<String>     单个用户所在的sGroup的信息
     *                      created by xienana on 2016/07/23
     */
    public static List<String> getSingleEntitySGroups(String currentEntity) {
       /*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        List<String> entities = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
		/*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        entities.add(currentEntity);
        HashMap<String,List<String>> map = getEntitiesSGroups(entities);
        if (map != null && !map.isEmpty()){
            resultList  = map.get(currentEntity);
        }
        return resultList;
    }

    /**
     * 查询多个用户所在的sGroups信息
     *
     * @param entities 需要查询的list
     * @return HashMap<key,value>
     *         key:String             entity（待查询的用户账号）
     *         value:List             与key相对应的entity所在的sGroup的信息
     *                                created by xienana on 2016/07/24
     */
    public static HashMap<String, List<String>> getEntitiesSGroups(List<String> entities) {
        HashMap<String, List<String>> resultMap = new HashMap<>();
        try {
            JSONObject getSGroupResult = SecurityGroupManager.getInstance()
                    .getSGroups(entities);
            LogUtil.getUtils().d(TAG+" getEntitiesSGroups   getSGroupResult :"+ getSGroupResult);
            int retCode = getSGroupResult.getInt("ret_code");
            if (retCode == 0) {
                JSONObject result = getSGroupResult.getJSONObject("result");
                if (result != null) {
                    Iterator iterator = result.keys();
                    while (iterator.hasNext()) {
                        String entity = (String)iterator.next();
                        List<String> valueList = new ArrayList<>();
                        JSONArray valueArray = result.getJSONArray(entity);
                        for (int j = 0; j < valueArray.length(); j++) {
                            valueList.add(valueArray.get(j).toString());
                        }
                        resultMap.put(entity, valueList);
                    }
                }
            }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
            else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                processCkmsExpired();
            }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+"getEntitiesSGroups   JSONException   :"+ e.toString() );
            return null;
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+"getEntitiesSGroups IllegalArgumentException "+e.toString());
            return null;
        }
        return resultMap;
    }
    /*[E]add by xienana for check is Gourp exist*/

    /**
     * 查询返回没有 安全身份的账户信息
     *
     * @param entities 需要查询的数字账户list
     * @return Map<String,Object> 查询结果
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  其他公共错误码
     *                         CKMS_SUCC_CODE 查询成功
     *                         CKMS_FAIL_CODE 查询失败
     *          key:RESULT_INFO_TAG("resultInfo") 没有安全身份的账号信息
     *          value: String[]类型，CKMS_SUCC_CODE时为没有安全身份的账号信息
     *                              null 表示都有安全身份
     *                      created by xienana on 2016/08/19
     */
     public static Map<String,Object> getNoSecEntityAccounts(List<String> entities) {
         Map<String,Object> ret = new HashMap<>();
         ret.put(RESULT_CODE_TAG,CKMS_FAIL_CODE);
        String values = "";
        if (entities != null && !entities.isEmpty()) {
            for(int i = 0; i < entities.size(); i++){
                if(i != entities.size()-1) {
                    values = values +entities.get(i)+ ",";
                }else{
                    values = values +entities.get(i);
                }
            }
            try {
                JSONObject getDevices = EntityManager.getInstance()
                        .getDevices(entities);
                LogUtil.getUtils().d(TAG+" getNoSecEntityAccounts   getDevices:" + getDevices);
                int ret_code = getDevices.getInt("ret_code");
                if (ret_code == 0) {
                    JSONObject resultInfo = getDevices.getJSONObject("result");
                    if (resultInfo != null) {
                        Iterator iterator = resultInfo.keys();
                        while (iterator.hasNext()) {
                            String entity = (String) iterator.next();
                            int index = values.indexOf(entity);
                            if(index != -1){
                                LogUtil.getUtils().d(TAG+" getNoSecEntityAccounts before values "+values+" index "+index+" entity "+entity);
                                if(index == 0){
                                    int flowCharIndex = index+entity.length();
                                    if(flowCharIndex < values.length() && values.charAt(flowCharIndex) == ','){
                                        values = values.replace(entity+",","");
                                    }else if(flowCharIndex == values.length()){
                                        values = "";
                                    }
                                }else if(index > 0){
                                    if(values.charAt(index -1) == ','){
                                        values = values.replace(","+entity, "");
                                    }
                                }
                            }
                        }
                        LogUtil.getUtils().d(TAG+" getNoSecEntityAccounts after values "+values);
                        String[] noSecEntitys = null;
                        if(!TextUtils.isEmpty(values)){// modified by ycm for lint 2017/02/13
                            noSecEntitys = values.split(String.valueOf(','));
                        }
                        ret.put(RESULT_CODE_TAG,CKMS_SUCC_CODE);
                        ret.put(RESULT_INFO_TAG,noSecEntitys);
                    }
                }else if(ret_code == CKMS_NO_DEVICE_ERROR){
                    ret.put(RESULT_CODE_TAG,CKMS_SUCC_CODE);
                    ret.put(RESULT_INFO_TAG,values.split(String.valueOf(',')));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.getUtils().e(TAG+" getNoBindDevices JSONException " + e.toString());
            }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"getEntitiesSGroups IllegalArgumentException "+e.toString());
            }
        }else{
            LogUtil.getUtils().e(TAG+" getNoBindDevices check parameter error, entities :"+entities);
        }
        return ret;
    }

    /*[S]add by xienana for ckms @2016/08/01(rummager:tangsha)*/

    /**
     * 判断当前账号是否在对应的SGroup里
     *
     * @param currentAccount 当前账号
     * @param groupId        待操作的groupId
     * @return ENTITY_IN_GROUP           entity在对应的sGroup里
     *         ENTITY_NOT_IN_GROUP       entity不在对应的sGroup里
     *         GROUP_NOT_EXIST           sGroup不存在
     *         GET_GROUP_INFO_FAIL       获取sGroup信息失败
     *         CKMS_EXPIRED_TO_INIT      添加失败，CKMS需要重新初始化
     *                       created by xienana on 2016/07/29
     */
    public static int isEntityInSGroup(String currentAccount, String groupId) {
		/*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        if (!TextUtils.isEmpty(currentAccount) && !TextUtils.isEmpty(groupId)) {
		/*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        try {
			    JSONObject getEntityResult = SecurityGroupManager.getInstance().getEntities(groupId);
                LogUtil.getUtils().d(TAG+" isEntityInSGroup   currentAccount "+currentAccount +" getEntityResult :" + getEntityResult);
                int retCode = getEntityResult.getInt("ret_code");
                if (retCode == 0) {
                    JSONArray resultArray = getEntityResult.getJSONArray("result");
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject resultJsObject = resultArray.getJSONObject(i);
                        String entityID = resultJsObject.getString("entity_id");
                        if (entityID.equals(currentAccount)) {
                            return ENTITY_IN_GROUP;
                        }
                    }
                    return ENTITY_NOT_IN_GROUP;
                }
                if (retCode == SGROUP_NOT_EXIST_CODE) {
                    return GROUP_NOT_EXIST;
                }else if (retCode == ENTITY_NOT_IN_SGROUP_CODE ){
                    return ENTITY_NOT_IN_GROUP;
                }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                    processCkmsExpired();
                    return CKMS_EXPIRED_TO_INIT;
                }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.getUtils().e(TAG+ " isEntityInSGroup JSONException " + e.toString());
            }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"isEntityInSGroup IllegalArgumentException "+e.toString());
            }
        }else{
            LogUtil.getUtils().e(TAG+" isEntityInSGroup check parameter error, currentAccount "+currentAccount+" groupId "+groupId);
        }
        return GET_GROUP_INFO_FAIL;
    }

    /*Start:add by wal@xdja.com for filter accountInSgroup*/
    public static List<String> filterExistedEntities(List<String> entities,String groupId){
        List<String> accountList=new ArrayList<>();
        Map<String,Boolean> selectAccountMap = new HashMap<>();
        if (!ListUtils.isEmpty(entities) && !TextUtils.isEmpty(groupId)) {
            try {
                JSONObject getEntityResult = SecurityGroupManager.getInstance().getEntities(groupId);
                for (String entity:entities){
                    selectAccountMap.put(entity,true);
                }
                int retCode = getEntityResult.getInt("ret_code");
                if (retCode == 0) {
                    JSONArray resultArray = getEntityResult.getJSONArray("result");
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject resultJsObject = resultArray.getJSONObject(i);
                        String entityID = resultJsObject.getString("entity_id");
                        if (selectAccountMap.get(entityID)!= null && selectAccountMap.get(entityID)){
                            selectAccountMap.remove(entityID);
                        }
                    }
                    Set<String> entityIDs = selectAccountMap.keySet();
                    for (String entityID:entityIDs){
                        accountList.add(entityID);
                    }
                }else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                    processCkmsExpired();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.getUtils().e(TAG+ " isEntityInSGroup JSONException " + e.toString());
            }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"isEntityInSGroup IllegalArgumentException "+e.toString());
            }
        }
        return accountList;
    }
    /*end:add by wal@xdja.com for filter accountInSgroup*/
    /**
     * 群组加人
     *
     * @param currentAccount 当前账号
     * @param groupId        待操作的groupId
     * @param addEntities    加入的entities
     * @param signOp         群组加人的业务操作签名
     *                       created by xienana on 2016/07/28
     * return CKMS_FAIL_CODE 添加失败
     *        CKMS_SUCC_CODE 添加成功
     *        EXIST_NOT_AUTH_DEVICE 添加失败，存在未关联安全设备的Entity
     *        CKMS_EXPIRED_TO_INIT 添加失败，CKMS需要重新初始化
     */
    public static int addEntitiesInSGroup(String currentAccount, String groupId, List<String> addEntities, String signOp) {
        int code = CKMS_FAIL_CODE;
       	/*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        if (TextUtils.isEmpty(currentAccount) || TextUtils.isEmpty(groupId)
                || TextUtils.isEmpty(signOp) || addEntities == null || addEntities.isEmpty()) {
		/*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
            LogUtil.getUtils().e(TAG+" addEntitiesInSGroup check parameter error, currentAccount "+currentAccount+" groupId "+groupId+" signOp "+signOp);
            return code;
        }
        try {
            JSONObject addEntitiesResult = SecurityGroupManager.getInstance()
                    .addEntities(currentAccount, addEntities, groupId, signOp);
            LogUtil.getUtils().d(TAG+" addEntitiesInSGroup addEntitiesResult "+addEntitiesResult);
            int retCode = addEntitiesResult.getInt("ret_code");
            if (retCode == 0) {
                code = CKMS_SUCC_CODE;
            }else if(retCode == CURRENT_DEVICE_UNBIND_ENTITY_CODE){
                code = EXIST_NOT_AUTH_DEVICE;
            }
            //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
            else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                code = CKMS_EXPIRED_TO_INIT;
                processCkmsExpired();
            }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+ "addEntitiesInSGroup JSONException " + e.toString());
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+"addEntitiesInSGroup IllegalArgumentException "+e.toString());
        }
        return code;
    }

    /**
     * 群组减人
     *
     * @param currentAccount 当前账号
     * @param groupId        待操作的groupId
     * @param entities       移除的账号
     * @param signOp         移除账号的业务操作签名
     *                       created by xienana on 2016/08/01
     *  return CKMS_FAIL_CODE 移除失败
     *        CKMS_SUCC_CODE 移除成功
     *        EXIST_NOT_AUTH_DEVICE 移除失败，存在未关联安全设备的Entity
     *        CKMS_EXPIRED_TO_INIT 移除失败，CKMS需要重新初始化
     */
    public static int removeEntities(String currentAccount, List<String> entities, String groupId, String signOp) {
        int code = CKMS_FAIL_CODE;
		/*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        if (TextUtils.isEmpty(currentAccount) || TextUtils.isEmpty(groupId)
                || TextUtils.isEmpty(signOp) || entities == null || entities.isEmpty()) {
            LogUtil.getUtils().e(TAG+ "removeEntities parameter error, currentAccount "+currentAccount+" groupId "+groupId);
            return code;
        }
		/*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
            try {
                JSONObject removeResult = SecurityGroupManager.getInstance()
                        .removeEntities(currentAccount, entities, groupId, signOp);
                int retCode = removeResult.getInt("ret_code");
                if (retCode == 0) {
                    code = CKMS_SUCC_CODE;
                }else if(retCode == CURRENT_DEVICE_UNBIND_ENTITY_CODE){
                    code = EXIST_NOT_AUTH_DEVICE;
                }
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                    code = CKMS_EXPIRED_TO_INIT;
                    processCkmsExpired();
                }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.getUtils().e(TAG+  "removeEntities JSONException " + e.toString());
            }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"removeEntities IllegalArgumentException "+e.toString());
            }
        return code;
    }

    /**
     * 销毁群组
     *
     * @param currentAccount 当前账号
     * @param groupId        群组ID
     * @param signOp         销毁群组的业务操作签名
     *                       created by xienana on 2016/07/28
     *return CKMS_FAIL_CODE 销毁失败
     *        CKMS_SUCC_CODE 销毁成功
     *        EXIST_NOT_AUTH_DEVICE 销毁失败，存在未关联安全设备的Entity
     *        CKMS_EXPIRED_TO_INIT 销毁失败，CKMS需要重新初始化
     */
    public static int destroySGroup(String currentAccount, String groupId, String signOp) {
        int code = CKMS_FAIL_CODE;
		/*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        if (TextUtils.isEmpty(currentAccount) || TextUtils.isEmpty(groupId) || TextUtils.isEmpty(signOp)) {
            LogUtil.getUtils().e(TAG+"destroySGroup parameter error, currentAccount "+currentAccount+" groupId "+groupId);
            return code;
        }
		/*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
            try {
                JSONObject destroySGroup = SecurityGroupManager.getInstance()
                        .destroy(currentAccount, groupId, signOp);
                int retCode = destroySGroup.getInt("ret_code");
                if (retCode == 0 || retCode == 70514) {
                    code = CKMS_SUCC_CODE;
                }else if(retCode == CURRENT_DEVICE_UNBIND_ENTITY_CODE){
                    code = EXIST_NOT_AUTH_DEVICE;
                }
                //tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                    code = CKMS_EXPIRED_TO_INIT;
                    processCkmsExpired();
                }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.getUtils().e(TAG+  "destroySGroup JSONException " + e.toString());
           }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"destroySGroup IllegalArgumentException "+e.toString());
            }
        return code;
    }
    /*[E]add by xienana for ckms @2016/08/01(rummager:tangsha)*/



    /*[S]modify by xienana for ckms get operSignature string @2016/08/01(rummager:tangsha)*/
    /**
     * ckms操作码（创建Group/Group新增Entity/Group移除Entity）
     */
    public static String getGroupOpStr(String currentAccount, String groupId, List<String> groupAccounts, String OpStrType) {
        String deviceId = getDeviceFlag();
        String base64Str = "";
        String opStr = "";
        int size;
        if(groupAccounts != null && (size = groupAccounts.size()) > 0) {
            String[] entities = new String[size];
            for (int i = 0; i < size; i++) {
                entities[i] = groupAccounts.get(i);
            }
            switch (OpStrType) {
                case CREATE_GROUP:
                    opStr = OpCodeFactory.Coder().createGroup(deviceId, groupId, currentAccount, entities);
                    break;
                case ADD_ENTITY:
                    opStr = OpCodeFactory.Coder().addEntity(deviceId, groupId, currentAccount, entities);
                    break;
                case REMOVE_ENTITY:
                    opStr = OpCodeFactory.Coder().removeEntity(deviceId, groupId, currentAccount, entities);
                    break;
            }
            base64Str = getToSignOpStr(opStr);
        }else{
            LogUtil.getUtils().e(TAG+"getGroupOpStr groupAccounts is null----- "+groupAccounts);
        }
        return base64Str;
    }

    /**
     * ckms操作码（设备主动退出Entity/销毁Entity）
     */
    public static String getQuiteOrDestroyEntityOpStr(String currentAccount, String OpStrType) {
        String orgStr = "";
        String deviceId = getDeviceFlag();
        switch (OpStrType) {
            case QUIT_ENTITY:
                orgStr = OpCodeFactory.Coder().quitEntity(deviceId,currentAccount);
                //opList.add(CkmsGpEnDecryptManager.QUIT_ENTITY);
                break;
            case DESTROY_ENTITY:
                orgStr = OpCodeFactory.Coder().destroyEntity(deviceId,currentAccount);
                //opList.add(CkmsGpEnDecryptManager.DESTROY_ENTITY);
                break;
        }
        return getToSignOpStr(orgStr);
    }

    /**
     * ckms操作码（Entity主动从指定Group退出/销毁Group）
     */
    public static String getQuitOrDestroyGroupOpStr(String currentAccount, String groupId, String OpStrType) {
        String orgStr = "";
        String deviceId = getDeviceFlag();
        switch (OpStrType) {
            case QUIT_GROUP:
                //opList.add(CkmsGpEnDecryptManager.QUIT_GROUP);
                orgStr = OpCodeFactory.Coder().quitGroup(deviceId,groupId,currentAccount);
                break;
            case DESTROY_GROUP:
               // opList.add(CkmsGpEnDecryptManager.DESTROY_GROUP);
                orgStr = OpCodeFactory.Coder().destroyGroup(deviceId,groupId,currentAccount);
                break;
        }
        return getToSignOpStr(orgStr);
    }
    /*[E]modify by xienana for ckms get operSignature string @2016/08/01(rummager:tangsha)*/


    /**
     * ckms操作码拼接
     */
    public static String getToSignOpStr(String orgStr) {
        String base64Str = "";
        if (!TextUtils.isEmpty(orgStr)) {// modified by ycm for lint 2017/02/13
            base64Str = Base64.encodeToString(orgStr.getBytes(),Base64.DEFAULT);
        } else {
            LogUtil.getUtils().e(TAG+  "getToSignOpStr opStr is empty " + orgStr);
        }
        //LogUtil.getUtils().d(TAG+" getToSignOpStr orgStr " + orgStr+" base64Str "+base64Str);
        return base64Str;
    }

    /**
     * 获取deviceId
     */
    public static String getDeviceFlag(){
        if(ckmsDeviceId  == null || ckmsDeviceId.isEmpty()){
            try {
                JSONObject result = EntityManager.getInstance().getDeviceID();
                LogUtil.getUtils().d(TAG+" getDeviceFlag result is "+result);
                int retCode = result.getInt("ret_code");
                if(retCode == 0){
                    JSONObject device_id = result.getJSONObject("result");
                    ckmsDeviceId = device_id.getString("device_id");
                }//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. Start
                else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                    processCkmsExpired();
                }else{//tangsha@xdja.com 2016-08-08 modify. for process ckms need init again. review by self. End
                    LogUtil.getUtils().e(TAG+ "getDeviceFlag retCode "+retCode+" errorInfo "+result.getString("err_msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.getUtils().e(TAG+ "getDeviceFlag JSONException "+e.toString());
            }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"getDeviceFlag IllegalArgumentException "+e.toString());
            }
        }
        return ckmsDeviceId;
    }


    /**
     * 创建群组
     *
     * @param currentAccount 当前账号
     * @param groupId        群组ID
     * @param friend        好友账号
     * @param signOp        创建群组的业务操作签名
     *                       created by xienana on 2016/07/28
     *return CREATE_SGROUP_FAIL 创建失败
     *        CREATE_SGROUP_SUCCEED 创建成功
     *        EXIST_NOT_AUTH_DEVICE 创建失败，存在未关联安全设备的Entity
     *        CKMS_EXPIRED_TO_INIT 创建失败，CKMS需要重新初始化
     */
    public static int createSGroupWithFriend(String groupId, String currentAccount, String friend, String signOp) {
	/*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        if (TextUtils.isEmpty(currentAccount) || TextUtils.isEmpty(groupId)
                || TextUtils.isEmpty(friend) || TextUtils.isEmpty(signOp)) {
	/*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
            LogUtil.getUtils().e(TAG+ "createSGroupWithFriend groupId wrong " + groupId);
            return CREATE_SGROUP_FAIL;
        }
        List<String> entities = new ArrayList<>();
        entities.add(currentAccount);
        entities.add(friend);
        return createSgroup(currentAccount, groupId, entities, signOp);// modified by ycm for lint 2017/02/13
    }

    /**
     * 创建群组
     *
     * @param currentAccount 当前账号
     * @param groupId        群组ID
     * @param entities      群组包含的账号信息
     * @param signOp        创建群组的业务操作签名
     *                       created by xienana on 2016/07/28
     *return CREATE_SGROUP_FAIL 创建失败
     *        CREATE_SGROUP_SUCCEED 创建成功
     *        EXIST_NOT_AUTH_DEVICE 创建失败，存在未关联安全设备的Entity
     *        CKMS_EXPIRED_TO_INIT 创建失败，CKMS需要重新初始化
     */
    public static int createSgroup(String currentAccount, String groupId, List<String> entities, String signOp) {
	        /*[S]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
        if (TextUtils.isEmpty(currentAccount) || TextUtils.isEmpty(groupId)
                || TextUtils.isEmpty(signOp) || entities == null || entities.isEmpty()) {
            LogUtil.getUtils().e(TAG+ "createSgroup parameter error, currentAccount "+currentAccount+" groupId "+groupId);
            return CREATE_SGROUP_FAIL;
        }
        try {
            JSONObject createGroupRes = SecurityGroupManager.getInstance()
                    .createSGroup(currentAccount, groupId, entities, signOp);
            LogUtil.getUtils().d(TAG+" createSgroup currentAccount " + currentAccount + " groupId " + groupId + " return " + createGroupRes);
            int retCode = createGroupRes.getInt("ret_code");
            if (retCode == 0 || retCode == SGROUP_ALREADY_EXIST_CODE) {
                return CREATE_SGROUP_SUCCEED;
            } else if (retCode == CURRENT_DEVICE_UNBIND_ENTITY_CODE) {
                /*[E]modify by xienana for CKMS  2016/08/09(rummager:tangsha)*/
                return EXIST_NOT_AUTH_DEVICE;
            }else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                processCkmsExpired();
                return CKMS_EXPIRED_TO_INIT;
            } else {
                LogUtil.getUtils().e(TAG+  "createSgroup retCode " + retCode + " errorInfo " + createGroupRes.getString("err_msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+  "createSgroup JSONException " + e.toString());
        }catch(IllegalArgumentException e){
            LogUtil.getUtils().e(TAG+"createSgroup IllegalArgumentException "+e.toString());
        }
        return CREATE_SGROUP_FAIL;
    }



    /*[S]modify by tangsha for add encrypt byte directly*/
    //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. Start
    private static boolean ckmsOpenTag = false;

    public static void setCkmsOpenTag(boolean open){
        ckmsOpenTag = open;
      //  Log.d(TAG,"anTong setCkmsOpenTag "+ckmsOpenTag);
    }
    public static boolean getCkmsIsOpen() {
      //  Log.d(TAG,"anTong getCkmsIsOpen "+ckmsOpenTag);
        return ckmsOpenTag;
    }

    /**
     *直接用sm4算法加密数据
     *
     * @param sGroupId         groupId
     * @param data             待加密的数据
     * @return  Map<String,Object>  加密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  其他公共错误码
     *                         CKMS_SUCC_CODE 加密成功
     *                         CKMS_FAIL_CODE 加密失败
     *          key:RESULT_INFO_TAG("resultInfo") 加密后内容信息
     *          value: String类型，CKMS_SUCC_CODE时为成功加密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */
    public static Map<String,Object> encryptDataSoft(String sGroupId, String data){
        Map<String,Object> encryptInfo = new HashMap<>();
        if(!getCkmsIsOpen()){// modified by ycm for lint 2017/02/13
            encryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            encryptInfo.put(RESULT_INFO_TAG, data);
            return encryptInfo;
        }
        if(!TextUtils.isEmpty(sGroupId) && !TextUtils.isEmpty(data)) {// modified by ycm for lint 2017/02/13
            long param2 = System.currentTimeMillis();
            byte[] key = getSM4Key(sGroupId,param2);
            SM4EncryptStr(key,data, encryptInfo);
            if(encryptInfo.containsKey(RESULT_CODE_TAG) && (int)encryptInfo.get(RESULT_CODE_TAG) == CKMS_SUCC_CODE){
                String secData = (String)encryptInfo.get(RESULT_INFO_TAG);
                secData = getSoftEncryResult(secData,param2);
                encryptInfo.remove(RESULT_INFO_TAG);
                encryptInfo.put(RESULT_INFO_TAG, secData);
            }
        }else{
            LogUtil.getUtils().e(TAG+ "encryptDataSoft check parameter error, sGroupId "+sGroupId+" data "+data);
            encryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        }
        return encryptInfo;
    }

    /**
     *直接用sm4算法加密数据
     *
     * @param sGroupId         groupId
     * @param dataByte             待加密的数据
     * @return  Map<String,Object>  加密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  其他公共错误码
     *                         CKMS_SUCC_CODE 加密成功
     *                         CKMS_FAIL_CODE 加密失败
     *          key:RESULT_INFO_TAG("resultInfo") 加密后内容信息
     *          value: byte[]类型，CKMS_SUCC_CODE时为成功加密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */

    private static final byte SOFT_BYTE_VERSION_TAG = '1';
    private static final byte SOFT_BYTE_SEPRATOR_TAG = '$';
    public static Map<String, Object> encryptByteToByteSoft(String sGroupId, byte[] dataByte) {
        Map<String, Object> encryptInfo = new HashMap<>();
        if (!getCkmsIsOpen()) {// modified by ycm for lint 2017/02/13
            encryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            encryptInfo.put(RESULT_INFO_TAG, dataByte);
            return encryptInfo;
        }
        if (!TextUtils.isEmpty(sGroupId) && dataByte != null && dataByte.length > 0) {// modified by ycm for lint 2017/02/13
            try {
                byte[] key2 = new byte[8];
                System.arraycopy(long2Bytes(System.currentTimeMillis()), 0, key2, 0, 8);
                byte[] key = generateSM4Key(sGroupId.getBytes("UTF-8"), key2);
                SM4EncryptByte(key,dataByte, encryptInfo);
                if (encryptInfo.containsKey(RESULT_CODE_TAG) && (int) encryptInfo.get(RESULT_CODE_TAG) == CKMS_SUCC_CODE) {
                    byte[] secByte = (byte[]) encryptInfo.get(RESULT_INFO_TAG);
                    byte[] secByteWrap = new byte[secByte.length+11];
                    secByteWrap[0] = SOFT_BYTE_VERSION_TAG;
                    secByteWrap[1] = SOFT_BYTE_SEPRATOR_TAG;
                    System.arraycopy(key2, 0, secByteWrap, 2, 8);
                    secByteWrap[10] = SOFT_BYTE_SEPRATOR_TAG;
                    System.arraycopy(secByte, 0, secByteWrap, 11, secByte.length);
                    encryptInfo.put(RESULT_INFO_TAG, secByteWrap);
                }
            } catch (UnsupportedEncodingException e) {
                LogUtil.getUtils().e(TAG+  "encryptByteToByteSoft UnsupportedEncodingException " + e.toString());
                encryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
            }
        } else {
            LogUtil.getUtils().e(TAG+  "encryptByteToByteSoft check parameter error, sGroupId " + sGroupId + " dataByte " + Arrays.toString(dataByte));// modified by ycm for lint 2017/02/13
            encryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        }
        return encryptInfo;
    }

    public static String RESULT_CODE_TAG = "resultCode";
    public static String RESULT_INFO_TAG = "resultInfo";
    /**
     *直接用sm4算法解密数据
     *
     * @param sGroupId         groupId
     * @param secData          待解密的数据
     * @return  Map<String,Object>  解密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  DECRYPT_DATA_FORMATE_WRONG 数据格式错误
     *                         其他公共错误码
     *                         CKMS_SUCC_CODE 解密成功
     *                         CKMS_FAIL_CODE 解密失败
     *          key:RESULT_INFO_TAG("resultInfo") 解密后内容信息
     *          value: byte[]类型，CKMS_SUCC_CODE时为成功解密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */
    public static Map<String,Object> decryptDataSoft(String sGroupId, String secData){
        Map<String,Object> decryptInfo = new HashMap<>();
        if(!getCkmsIsOpen()){
            decryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            decryptInfo.put(RESULT_INFO_TAG, secData);
            return decryptInfo;
        }
        if(!TextUtils.isEmpty(sGroupId) && !TextUtils.isEmpty(secData)) {
            int versionTagLength = SOFT_ENCRYPT_VERSION.length();
            int keyStartIndex = versionTagLength+1;
            int keyEndIndex;
            String tag = "";
            int secDataLength = secData.length();
            if(secDataLength > versionTagLength) {
                tag = secData.substring(0, versionTagLength);
            }
            if(secDataLength < keyStartIndex || tag.compareTo(SOFT_ENCRYPT_VERSION) != 0
                     || !secData.substring(keyStartIndex).contains(SOFT_ENCRYPT_KEY_TAG)){// modified by ycm for lint 2017/02/13
                LogUtil.getUtils().e(TAG+ "decryptDataSoft DECRYPT_DATA_FORMATE_WRONG");
                decryptInfo.put(RESULT_CODE_TAG, DECRYPT_DATA_FORMATE_WRONG);
                decryptInfo.put(RESULT_INFO_TAG, "");
                return decryptInfo;
            }
            keyEndIndex = keyStartIndex + secData.substring(keyStartIndex).indexOf(SOFT_ENCRYPT_KEY_TAG);
            String keyStr = secData.substring(keyStartIndex,keyEndIndex);
            long param2 = Long.parseLong(keyStr);
            byte[] key = getSM4Key(sGroupId,param2);
            SM4Decrypt(key,secData.substring(keyEndIndex+1),decryptInfo);
        }else{
            LogUtil.getUtils().e(TAG+ "decryptDataSoft check parameter error sGroupId "+sGroupId);
            decryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        }
        return decryptInfo;
    }

    /**
     *直接用sm4算法解密数据
     *
     * @param sGroupId         groupId
     * @param secData          待解密的数据
     * @return  Map<String,Object>  解密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  DECRYPT_DATA_FORMATE_WRONG 数据格式错误
     *                         其他公共错误码
     *                         CKMS_SUCC_CODE 解密成功
     *                         CKMS_FAIL_CODE 解密失败
     *          key:RESULT_INFO_TAG("resultInfo") 解密后内容信息
     *          value: String类型，CKMS_SUCC_CODE时为成功解密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */
    public static Map<String,Object> decryptByteSoft(String sGroupId, byte[] secData){
        Map<String,Object> decryptInfo = new HashMap<>();
        if(!getCkmsIsOpen()){// modified by ycm for lint 2017/02/13
            decryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            decryptInfo.put(RESULT_INFO_TAG, secData);
            return decryptInfo;
        }
        if(!TextUtils.isEmpty(sGroupId) && secData != null && secData.length > 0) {// modified by ycm for lint 2017/02/13
            if(secData.length < 11 || secData[0] != SOFT_BYTE_VERSION_TAG
                    || secData[1] != SOFT_BYTE_SEPRATOR_TAG || secData[10] != SOFT_BYTE_SEPRATOR_TAG){
                LogUtil.getUtils().e(TAG+"decryptByteSoft DECRYPT_DATA_FORMATE_WRONG ");
                decryptInfo.put(RESULT_CODE_TAG, DECRYPT_DATA_FORMATE_WRONG);
                decryptInfo.put(RESULT_INFO_TAG, "");
                return decryptInfo;
            }
            try {
                byte[] key2 = new byte[8];
                System.arraycopy(secData, 2, key2, 0, 8);
                byte[] key = generateSM4Key(sGroupId.getBytes("UTF-8"), key2);
                byte[] needDecryByte = new byte[secData.length-11];
                System.arraycopy(secData, 11, needDecryByte, 0, secData.length-11);
                SM4DecryptByte(key, needDecryByte, decryptInfo);
            }catch (UnsupportedEncodingException e){
                LogUtil.getUtils().e(TAG+"decryptByteSoft UnsupportedEncodingException "+e.toString());
                decryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
            }
        }else{
            LogUtil.getUtils().e(TAG+"decryptByteSoft check parameter error sGroupId "+sGroupId);
            decryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        }
        return decryptInfo;
    }

    /**
     *直接用sm4算法解密数据
     *
     * @param sGroupId         groupId
     * @param encFilePath      待解密的文件
     *        decFilePath      解密后文件放置路径
     * @return  Map<String,Object>  解密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  DECRYPT_DATA_FORMATE_WRONG 数据格式错误
     *                         其他公共错误码
     *                         CKMS_SUCC_CODE 解密成功
     *                         CKMS_FAIL_CODE 解密失败
     *          key:RESULT_INFO_TAG("resultInfo") 解密后内容信息
     *          value: String类型，CKMS_SUCC_CODE时为成功解密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */
    public static int decryptFileSoft(String sGroupId, String encFilePath, String decFilePath) {
       // Log.d(TAG,"decryptFileSoft enter "+System.currentTimeMillis());
        int retCode = VERSION_NOT_SUPPORT_CKMS;
        if (!getCkmsIsOpen()) {
            return retCode;
        }
        if (!TextUtils.isEmpty(sGroupId) && !TextUtils.isEmpty(encFilePath)
                && !TextUtils.isEmpty(decFilePath)) {// modified by ycm for lint 2017/02/13
            File enFile = new File(encFilePath);
            File destFileDir = new File(decFilePath.substring(0, decFilePath.lastIndexOf(File.separator)));
            if (!destFileDir.exists()) {
                if (!destFileDir.mkdirs()) {
                    LogUtil.getUtils().e(TAG+ "decryptFileSoft destFileDir.mkdirs() fail");
                    return CKMS_FAIL_CODE;
                }
            }
            File destFile = new File(decFilePath);
            if (destFile.exists()) {
                destFile.delete();
            }
            if (enFile.exists()) {
                FileInputStream fi = null;
                FileOutputStream fo = null;
                try {
                    fi = new FileInputStream(enFile);
                    if (!destFile.createNewFile()) {
                        return CKMS_FAIL_CODE;
                    }
                    fo = new FileOutputStream(destFile);
                    int count = 8 * 1024;
                    byte[] buffer = new byte[count];
                    byte[] decryptRes = new byte[count];
                    int read;
                    byte[] key2Wrap = new byte[11];
                    read = fi.read(key2Wrap, 0, 11);
                    if(read != -1){
                        if(key2Wrap[0] != SOFT_BYTE_VERSION_TAG || key2Wrap[1] != SOFT_BYTE_SEPRATOR_TAG || key2Wrap[10] != SOFT_BYTE_SEPRATOR_TAG){
                            LogUtil.getUtils().e(TAG+"decryptFileSoft formate error!!! "+encFilePath);
                            retCode = CKMS_DECRYPT_FORMATE_ERROR;
                        }else {
                            byte[] key2 = new byte[8];
                            System.arraycopy(key2Wrap,2,key2,0,8);
                            byte[] key = generateSM4Key(sGroupId.getBytes("UTF-8"), key2);
                            while ((read = fi.read(buffer, 0, count)) != -1) {
                                int smRes = XDJACrypto.getInstance().SM4(key, XdjaCrypto.CBC_DECRYPT, buffer, buffer.length, decryptRes, SM4_VI.getBytes());
                                if (smRes == 0) {
                                    fo.write(decryptRes, 0, decryptRes.length);
                                } else {
                                    LogUtil.getUtils().e(TAG+"decryptFileSoft sm4 error "+encFilePath);
                                    retCode = CKMS_FAIL_CODE;
                                    destFile.delete();
                                    break;
                                }
                            }
                            if (read == -1) {
                             //   Log.d(TAG, "decryptFileSoft end " + System.currentTimeMillis());
                                retCode = CKMS_SUCC_CODE;
                            }
                        }
                    }else{
                        LogUtil.getUtils().e(TAG+"decryptFileSoft get key error "+encFilePath);
                        retCode = CKMS_FAIL_CODE;
                    }
                } catch (FileNotFoundException e) {
                    LogUtil.getUtils().e(TAG+"decryptFileSoft  FileNotFoundException " + e.toString()+" encFilePath "+encFilePath);
                    e.printStackTrace();
                } catch (IOException e) {
                    LogUtil.getUtils().e(TAG+"decryptFileSoft IOException " + e.toString()+" encFilePath "+encFilePath);
                } finally {
                    try {
                        if (fi != null)
                            fi.close();
                        if (fo != null)
                            fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                retCode = CKMS_FAIL_CODE;
            }
        } else {
            LogUtil.getUtils().e(TAG+"decryptDataSoft check parameter error sGroupId " + sGroupId
                    + " encFilePath  " + encFilePath + " decFilePath " + decFilePath);
            retCode = CKMS_FAIL_CODE;
        }
        return retCode;
    }

    /**
     *直接用sm4算法解密数据
     *
     * @param sGroupId         groupId
     * @param srcFilePath      待加密的文件
     *        destFilePath      加密后文件放置路径
     * @return  Map<String,Object>  解密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  DECRYPT_DATA_FORMATE_WRONG 数据格式错误
     *                         其他公共错误码
     *                         CKMS_SUCC_CODE 解密成功
     *                         CKMS_FAIL_CODE 解密失败
     *          key:RESULT_INFO_TAG("resultInfo") 解密后内容信息
     *          value: String类型，CKMS_SUCC_CODE时为成功解密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */
    public static int encryptFileSoft(String sGroupId, String srcFilePath, String destFilePath) {
       // Log.d(TAG,"encryptFileSoft enter "+System.currentTimeMillis());
        int retCode = VERSION_NOT_SUPPORT_CKMS;
        if (!getCkmsIsOpen()) {// modified by ycm for lint 2017/02/13
            return retCode;
        }
        if (!TextUtils.isEmpty(sGroupId) && !TextUtils.isEmpty(srcFilePath)
                && !TextUtils.isEmpty(destFilePath)) {// modified by ycm for lint 2017/02/13
            File enFile = new File(srcFilePath);
            File destFileDir = new File(destFilePath.substring(0, destFilePath.lastIndexOf(File.separator)));
            if (!destFileDir.exists()) {
                if (!destFileDir.mkdirs()) {
                    LogUtil.getUtils().e(TAG+"encryptFileSoft destFileDir.mkdirs() fail "+srcFilePath+" destFilePath "+destFilePath);
                    return CKMS_FAIL_CODE;
                }
            }
            File destFile = new File(destFilePath);
            if (destFile.exists()) {
                destFile.delete();
            }
            if (enFile.exists()) {
                FileInputStream fi = null;
                FileOutputStream fo = null;
                try {
                    fi = new FileInputStream(enFile);
                    if (!destFile.createNewFile()) {
                        return CKMS_FAIL_CODE;
                    }
                    fo = new FileOutputStream(destFile);
                    int count = 8 * 1024;
                    byte[] buffer = new byte[count];
                    byte[] encryptRes = new byte[count];
                    byte[] key2 = new byte[8];
                    System.arraycopy(long2Bytes(System.currentTimeMillis()),0,key2,0,8);
                    byte[] key  = generateSM4Key(sGroupId.getBytes("UTF-8"),key2);
                    byte[] softEncryptByteWrap = new byte[11];
                    softEncryptByteWrap[0] = SOFT_BYTE_VERSION_TAG;
                    softEncryptByteWrap[1] = SOFT_BYTE_SEPRATOR_TAG;
                    System.arraycopy(key2, 0, softEncryptByteWrap, 2, 8);
                    softEncryptByteWrap[10] = SOFT_BYTE_SEPRATOR_TAG;
                    fo.write(softEncryptByteWrap, 0, softEncryptByteWrap.length);
                    int read;
                    while ((read = fi.read(buffer, 0, count)) != -1) {
                        int smRes = XDJACrypto.getInstance().SM4(key,XdjaCrypto.CBC_ENCRYPT,buffer,buffer.length,encryptRes,SM4_VI.getBytes());
                        if(smRes == 0) {
                            fo.write(encryptRes, 0, encryptRes.length);
                        }else{
                            LogUtil.getUtils().e(TAG+"encryptFileSoft sm4 error");
                            retCode = CKMS_FAIL_CODE;
                            destFile.delete();
                            break;
                        }
                        Arrays.fill(buffer,(byte)0);
                    }
                    if(read == -1){
                        retCode = CKMS_SUCC_CODE;
                     //   Log.d(TAG,"encryptFileSoft end "+System.currentTimeMillis());
                    }
                } catch (FileNotFoundException e) {
                    LogUtil.getUtils().e(TAG+"encryptFileSoft  FileNotFoundException " + e.toString()+" srcFilePath "+srcFilePath);
                    e.printStackTrace();
                } catch (IOException e) {
                    LogUtil.getUtils().e(TAG+"encryptFileSoft IOException " + e.toString()+" srcFilePath "+srcFilePath);
                } finally {
                    try {
                        if (fi != null)
                            fi.close();
                        if (fo != null)
                            fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                retCode = CKMS_FAIL_CODE;
            }
        } else {
            LogUtil.getUtils().e(TAG+"encryptFileSoft check parameter error sGroupId " + sGroupId + " srcFilePath  " + srcFilePath + " destFilePath " + destFilePath);
            retCode = CKMS_FAIL_CODE;
        }
        return retCode;
    }

    private static void SM4EncryptStr(byte[] key, String data,Map<String,Object> encryptInfo){
        byte[] dataByte = data.getBytes();
        SM4EncryptByte(key, dataByte,encryptInfo);
        if(encryptInfo != null && encryptInfo.containsKey(RESULT_CODE_TAG) && (int)encryptInfo.get(RESULT_CODE_TAG) == CKMS_SUCC_CODE){
           String result = Base64.encodeToString((byte[])encryptInfo.get(RESULT_INFO_TAG), Base64.NO_WRAP);
            encryptInfo.remove(RESULT_INFO_TAG);
            encryptInfo.put(RESULT_INFO_TAG,result);
        }else{
            LogUtil.getUtils().e(TAG+"SM4EncryptStr error "+encryptInfo);
        }
    }

    private static void SM4EncryptByte(byte[] key,byte[] dataByte,Map<String,Object> encryptInfo){
        byte[] opDataByte;
         int modNum = dataByte.length%16;
            if(modNum != 0){
                int byteNum = (dataByte.length+16 - modNum);
                opDataByte = new byte[byteNum];
                System.arraycopy(dataByte,0,opDataByte,0,dataByte.length);
              //  Log.d(TAG,"SM4 byteNum " +byteNum+" modNum "+modNum);
            }else{
                opDataByte = dataByte;
            }

        byte[] reslutByte = new byte[opDataByte.length];
        int smRes = XDJACrypto.getInstance().SM4(key,XdjaCrypto.CBC_ENCRYPT,opDataByte,opDataByte.length,reslutByte,SM4_VI.getBytes());
        if(smRes == 0) {
            encryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            encryptInfo.put(RESULT_INFO_TAG,reslutByte);
        }else{
            encryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
        }
    }

    private static final String SM4_VI = "0000000000000000";

    private static void SM4Decrypt(byte[] key, String data, Map<String, Object> decryptInfo) {
        String result = "";
        try {
            byte[] secByte = Base64.decode(data, Base64.NO_WRAP);
            if (!TextUtils.isEmpty(data) && (secByte == null || secByte.length == 0)) {
                LogUtil.getUtils().e(TAG+ "SM4Decrypt data base64 error");
                decryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
            } else {
                byte[] decryptRes = new byte[secByte.length];
                int smRes = XDJACrypto.getInstance().SM4(key, XdjaCrypto.CBC_DECRYPT, secByte, secByte.length, decryptRes, SM4_VI.getBytes());
                if (smRes == 0) {
				    //[S]modify by tangsha@20161208 for 6807
                    result = new String(decryptRes);
					//[E]modify by tangsha@20161208 for 6807
                    decryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
                } else {
                    decryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
                }
                LogUtil.getUtils().d(TAG+" SM4 SM4Decrypt  smRes " + smRes + " result " + result);
            }
        } catch (IllegalArgumentException e) {
            LogUtil.getUtils().e(TAG+ "SM4Decrypt IllegalArgumentException " + e.toString());
            decryptInfo.put(RESULT_CODE_TAG, DECRYPT_DATA_FORMATE_WRONG);
        }
        decryptInfo.put(RESULT_INFO_TAG, result);
    }

    private static void SM4DecryptByte(byte[] key, byte[] dataByte, Map<String, Object> decryptInfo) {
        int needDeLength;
        if (dataByte == null || ((needDeLength = dataByte.length) == 0)) {
            LogUtil.getUtils().e(TAG+ "SM4DecryptByte dataByte error "+ Arrays.toString(dataByte));// modified by ycm for lint 2017/02/13
            decryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        } else {
          //  Log.d(TAG,"SM4DecryptByte needDeLength "+needDeLength);
            byte[] decryptRes = new byte[needDeLength];
            int smRes = XDJACrypto.getInstance().SM4(key, XdjaCrypto.CBC_DECRYPT, dataByte, dataByte.length, decryptRes, SM4_VI.getBytes());
            if (smRes == 0) {
                decryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
                decryptInfo.put(RESULT_INFO_TAG, decryptRes);
            } else {
                decryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
            }
        }

    }

    private static String SOFT_ENCRYPT_VERSION = "V1";
    private static String SOFT_ENCRYPT_KEY_TAG = "$";

    private static String getSoftEncryResult(String encryData, long encryptKey){
        //        String strRes = result.toString();
       // Log.d(TAG,"getSoftEncryResult strRes "+strRes);
        return SOFT_ENCRYPT_VERSION + SOFT_ENCRYPT_KEY_TAG + // modified by ycm for lint 2017/02/13
                encryptKey +
                SOFT_ENCRYPT_KEY_TAG +
                encryData;
    }

   /**
     * 将8字节long整数转换为字节数组
     * @param a 待转换long整数
     * @return 转换后的8字节数组
     */
    private static byte[] long2Bytes(long a) {
        return new byte[]{(byte)((int)(a >> 56)), (byte)((int)(a >> 48)), (byte)((int)(a >> 40)), (byte)((int)(a >> 32)), (byte)((int)(a >> 24)), (byte)((int)(a >> 16)), (byte)((int)(a >> 8)), (byte)((int)(a))};
    }

    private static byte[] getSM4Key(String sGourpId, long radomParam){
        byte[] result = null;
        try {
            byte[] param1 = sGourpId.getBytes("UTF-8");
            byte[] param2 = long2Bytes(radomParam);
            result = generateSM4Key(param1,param2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+" getSM4Key sGourpId "+sGourpId+" UnsupportedEncodingException "+e.toString());
        }
        return result;
    }

    private static byte[] generateSM4Key(byte[] src1, byte[] src2){
        byte[] result = new byte[16];
        Arrays.fill(result, (byte)0);
        int length1 = src1.length;
        int length2 = src2.length;
        int copyEndIndex = length1 >= 8? 8:length1;
        System.arraycopy(src1, 0, result, 0, copyEndIndex);
        copyEndIndex = length2 >= 8? 8:length2;
        System.arraycopy(src2, 0, result, 8, copyEndIndex);
        return result;
    }


    /**
     *加密数据
     *
     * @param currentEntity    当前账户（数字）
     * @param sGroupId         groupId
     * @param data             待加密的数据
     * @return  Map<String,Object>  加密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  其他公共错误码
     *                         CKMS_SUCC_CODE 加密成功
     *                         CKMS_FAIL_CODE 加密失败
     *          key:RESULT_INFO_TAG("resultInfo") 加密后内容信息
     *          value: String类型，CKMS_SUCC_CODE时为成功加密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */
    //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. End
    public static Map<String,Object> encryptData(String currentEntity, String sGroupId, String data){
        if(!getCkmsIsOpen()){// modified by ycm for lint 2017/02/13
            Map<String,Object> encryptInfo = new HashMap<>();
            encryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            encryptInfo.put(RESULT_INFO_TAG, data);
            return encryptInfo;
        }
        //tangsha@xdja.com 2016-08-03 add. for config open or close ckms flow . review by self. End
        if(!TextUtils.isEmpty(data)) {// modified by ycm for lint 2017/02/13
           // Log.d(TAG,"encryptData currentEntity "+currentEntity+" sGroupId "+sGroupId+" org data "+data);
            return encryptByteToString(currentEntity, sGroupId, data.getBytes());
        }else {
            LogUtil.getUtils().e(TAG+"encryptData check parameter error "+data);
            Map<String,Object> encryptInfo = new HashMap<>();
            encryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
            encryptInfo.put(RESULT_INFO_TAG, data);
            return encryptInfo;
        }
    }

    public static Map<String,Object> encryptByteToString(String currentEntity, String sGroupId, byte[] data){
        String encriedStr = "";
        Map<String,Object> encriedInfo = encryptByteToByte(currentEntity, sGroupId, data);
        byte[] encriedData = null;
        if(encriedInfo != null && (int)encriedInfo.get(RESULT_CODE_TAG) == CKMS_SUCC_CODE){
            encriedData = (byte[])encriedInfo.get(RESULT_INFO_TAG);
        }
        if (encriedData != null) {
            encriedStr = Base64.encodeToString(encriedData, Base64.NO_WRAP);
        }
        if(encriedInfo != null) {
            encriedInfo.remove(RESULT_INFO_TAG);
            encriedInfo.put(RESULT_INFO_TAG, encriedStr);
        }
        LogUtil.getUtils().d(TAG+" encryptByteToString currentEntity "+currentEntity+" sGroupId "+sGroupId+" encriedStr "+encriedStr);
        return encriedInfo;
    }

    public static Map<String,Object> encryptByteToByte(String currentEntity, String sGroupId, byte[] data){
        Map<String,Object> encryptInfo = new HashMap<>();
        if(!getCkmsIsOpen()){// modified by ycm for lint 2017/02/13
            encryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            encryptInfo.put(RESULT_INFO_TAG, data);
            return encryptInfo;
        }
        if(!TextUtils.isEmpty(currentEntity) && !TextUtils.isEmpty(sGroupId)// modified by ycm for lint 2017/02/13
                && data != null && data.length > 0) {
            try {
                byte[] encriedData = SecuritySDKManager.getInstance().encryptData(currentEntity, sGroupId,
                        SecurityConstants.ENCRYPT_TYPE_STANDARD, data);
                encryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
                encryptInfo.put(RESULT_INFO_TAG, encriedData);
            } catch (SecurityException e) {
                int retCode = processSecuException(e);
                encryptInfo.put(RESULT_CODE_TAG, retCode);
                LogUtil.getUtils().e(TAG+ "encryptByteToByte SecurityException " + e.toString());
            }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"encryptByteToByte IllegalArgumentException "+e.toString());
                encryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
            }
        }else{
            LogUtil.getUtils().e(TAG+"encryptByteToByte parameter wrong, currentEntity "+currentEntity+" sGroupId "+sGroupId);
            encryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        }
        return encryptInfo;
    }

    /**
     *解密数据
     *
     * @param currentEntity    当前账户(数字)
     * @param secData          待解密的数据
     * @return  Map<String,Object>  解密信息
     *          key:RESULT_CODE_TAG("resultCode") 操作结果状态码
     *          value: int类型  DECRYPT_DATA_FORMATE_WRONG 数据格式错误
     *                         其他公共错误码
     *                         CKMS_SUCC_CODE 解密成功
     *                         CKMS_FAIL_CODE 解密失败
     *          key:RESULT_INFO_TAG("resultInfo") 解密后内容信息
     *          value: String类型，CKMS_SUCC_CODE时为成功解密后的信息
     *                            其他错误，值为空字符串
     *                         created by tangsha on 2016/07/13
     */
    public static Map<String,Object> decryptData(String currentEntity,String secData){
        if(!getCkmsIsOpen()){// modified by ycm for lint 2017/02/13
            Map<String,Object> decryptInfo = new HashMap<>();
            decryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            decryptInfo.put(RESULT_INFO_TAG, secData);
            return decryptInfo;
        }
        String decryStr = "";
        Map<String,Object> decryInfo = decryptDataToByte(currentEntity,secData);
        if (decryInfo != null && (int)decryInfo.get(RESULT_CODE_TAG) == CKMS_SUCC_CODE) {
            byte[] decry = (byte[])decryInfo.get(RESULT_INFO_TAG);
            if(decry != null) {
                decryStr = new String(decry);
            }
            decryInfo.remove(RESULT_INFO_TAG);
            decryInfo.put(RESULT_INFO_TAG,decryStr);
        }
        LogUtil.getUtils().d(TAG+" decryptData currentEntity "+currentEntity+" secData "+secData+" decryStr "+decryStr);
        return decryInfo;
    }

    public static Map<String,Object> decryptDataToByte(String currentEntity, String secData){
        Map<String,Object> decryInfo = new HashMap<>();
        if(!TextUtils.isEmpty(secData)) {// modified by ycm for lint 2017/02/13
            try{
                byte[] secDataBytes = Base64.decode(secData, Base64.NO_WRAP);
                if(!TextUtils.isEmpty(secData) && (secDataBytes == null || secDataBytes.length == 0)){// modified by ycm for lint 2017/02/13
                    LogUtil.getUtils().e(TAG+"decryptDataToByte data base64 error");
                    decryInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
                }else {
                    decryInfo = decryptByteToByte(currentEntity, secDataBytes);
                }
            }catch (IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+ "decryptDataToByte IllegalArgumentException "+e.toString() );
                decryInfo.put(RESULT_CODE_TAG, DECRYPT_DATA_FORMATE_WRONG);
            }
        }else{
            LogUtil.getUtils().e(TAG+ "decryptDataToByte check parameter error" );
            decryInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        }
        return decryInfo;
    }

    public static Map<String,Object> decryptByteToByte(String currentEntity, byte[] secDataBytes){
        Map<String,Object> decryptInfo = new HashMap<>();
        if(!getCkmsIsOpen()){// modified by ycm for lint 2017/02/13
            decryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
            decryptInfo.put(RESULT_INFO_TAG, secDataBytes);
            return decryptInfo;
        }
        if(!TextUtils.isEmpty(currentEntity) && secDataBytes != null && secDataBytes.length > 0) {// modified by ycm for lint 2017/02/13
            try {
                PlainDataBean bean = SecuritySDKManager.getInstance().decryptData(currentEntity, secDataBytes);
                if(bean != null) {
                    byte[] decry = bean.getPlainData();
                    decryptInfo.put(RESULT_CODE_TAG, CKMS_SUCC_CODE);
                    decryptInfo.put(RESULT_INFO_TAG, decry);
                }else{
                    LogUtil.getUtils().e(TAG+"decryptByteToByte bean is null!!!");
                    decryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
                }
            } catch (SecurityException e) {
                int retCode = processSecuException(e);
                decryptInfo.put(RESULT_CODE_TAG, retCode);
            }catch(IllegalArgumentException e){
                LogUtil.getUtils().e(TAG+"decryptByteToByte IllegalArgumentException "+e.toString());
                decryptInfo.put(RESULT_CODE_TAG, CKMS_FAIL_CODE);
            }
        }else{
            LogUtil.getUtils().e(TAG+"decryptByteToByte check parameter error currentEntity "+currentEntity+" secDataBytes "+ Arrays.toString(secDataBytes));// modified by ycm for lint 2017/02/13
            decryptInfo.put(RESULT_CODE_TAG, CKMS_PARAMETER_WRONG);
        }
        return decryptInfo;
    }
    /*[E]modify by tangsha for add encrypt byte directly*/
    //[S]add by tangsha for third encrypt
	  public static void parseThirdEnPushInfo(String content, String currentAccount){
        LogUtil.getUtils().d(TAG+" parseThirdEnPushInfo content is "+content);
        ThirdEnPushMessage responseBean =
                new Gson().fromJson(content, ThirdEnPushMessage.class);
        byte[] decryptKey = null;
        Map<String,Object> decryptRes = decryptDataToByte(currentAccount,responseBean.getSecKey());
        if(decryptRes != null && (int)decryptRes.get(RESULT_CODE_TAG) == CKMS_SUCC_CODE){
            decryptKey = (byte[])decryptRes.get(RESULT_INFO_TAG);
        }
        if(decryptKey != null && decryptKey.length > 0) {
            Map<String, Object> decryptInfo = new HashMap<>();
            decryptInfo.put(IEncryptUtils.THIRD_GROUP_ID, responseBean.getGroupId());
            decryptInfo.put(IEncryptUtils.THIRD_SEC_KEY, responseBean.getSecKey());
            decryptInfo.put(IEncryptUtils.THIRD_KEY, decryptKey);
            IEncryptUtils.setDecryptKey(decryptInfo);
        }else{
            LogUtil.getUtils().e(TAG+"parseThirdEnPushInfo decrypt key wrong!!!");
        }
    }

    public static String getSendThirdEnPushMsg(String groupId, String secKey){
        String ret;
        ThirdEnPushMessage message = new ThirdEnPushMessage(groupId,secKey);
        ret = message.toJsonString();
        return ret;
    }
    //[E]add by tangsha for third encrypt
    //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. Start
    public static int ckmsRefresh(){
        if(!getCkmsIsOpen()){
            return CKMS_SUCC_CODE;
        }
        int ret;
        try {
            JSONObject result = SecuritySDKManager.getInstance().refresh();
            LogUtil.getUtils().d(TAG + " refresh return " + result);
            int retCode = result.getInt("ret_code");
            if(retCode == 0){
                ret = CKMS_SUCC_CODE;
            }else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                ret = CKMS_EXPIRED_TO_INIT;
            }else{
                ret = CKMS_FAIL_CODE;
                LogUtil.getUtils().e(TAG+"processNoInfoResult retCode "+retCode+" errorInfo "+result.getString("err_msg"));
            }
        } catch (JSONException e) {
            ret = CKMS_FAIL_CODE;
            e.printStackTrace();
            LogUtil.getUtils().e(TAG+"processNoInfoResult JSONException "+e.toString());
        }catch(IllegalArgumentException e){
            ret = CKMS_FAIL_CODE;
            LogUtil.getUtils().e(TAG+"processNoInfoResult IllegalArgumentException "+e.toString());
        }
        return ret;
    }
    //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. End
    //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. Start
    private static int ONE_HOUR = 3600 * 1000;
    private static int ONE_MINUTE = 60 * 1000;
    private static int ckmsValidTime = 1;

    public static void setCkmsValidTime(int time){
        ckmsValidTime = time;
    }

    public static void ckmsRefreshTask(Context context){
        long delayMs;
        if(ckmsValidTime > 1){
            delayMs = ckmsValidTime * ONE_HOUR / 2;
        }else{
            delayMs = 30 * ONE_MINUTE;
        }
        ckmsRefreshTaskDelayTime(context,delayMs);
    }

    public static void ckmsRefreshTaskDelayTime(Context context, long delayMs){
        PendingIntent pendingIntent = cancelPreRefreshTask(context);
        AlarmManager alermManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        LogUtil.getUtils().d("ckmsRefreshTaskDelayTime "+delayMs);
        alermManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+delayMs, pendingIntent);
    }

    public static String CKMS_REFRESH_ACTION = "com.xdja.actoma.ckms.handler";
    public static String CKMS_REFRESH_EXTRA_EXPIRED = "ckms_expired";
    public static PendingIntent cancelPreRefreshTask(Context context){
        Intent sender = new Intent();
        sender.setAction(CKMS_REFRESH_ACTION);
        sender.putExtra(CKMS_REFRESH_EXTRA_EXPIRED,false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, sender, 0);
        AlarmManager alermManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alermManager.cancel(pendingIntent);
        return pendingIntent;
    }
    //tangsha@xdja.com 2016-08-05 modify. for ckms refresh . review by self. End
    //tangsha@xdja.com 2016-08-08 modify. for ckms refresh . review by self. Start
    private static Context mCkmsContext = null;
    public static int CKMS_EXPIRED_ERROR_CODE = 106871;
    public static int CKMS_ACCESS_NOTEXIST_CODE = 106870;

    public static void setCkmsContext(Context context){
        mCkmsContext = context;
    }

    public static void processCkmsExpired(){
        LogUtil.getUtils().e(TAG+ "processCkmsExpired enter");
        cancelPreRefreshTask(mCkmsContext);
        Intent intent = new Intent(CKMS_REFRESH_ACTION);
        intent.putExtra(CKMS_REFRESH_EXTRA_EXPIRED,true);
        mCkmsContext.sendBroadcast(intent);
    }

    private static int processSecuException(SecurityException e){
        int retCode = -1;
        String exceptionStr = e.getMessage();
        LogUtil.getUtils().e(TAG+ "processSecuException SecurityException " + exceptionStr);
        try {
            JSONObject json = new JSONObject(exceptionStr);
            int ckmscode = json.getInt("ret_code");
            if(ckmscode == CKMS_EXPIRED_ERROR_CODE || ckmscode == CKMS_ACCESS_NOTEXIST_CODE){
                processCkmsExpired();
                retCode = CKMS_EXPIRED_TO_INIT;
            }else if(ckmscode == CKMS_INNER_NETWORK_ERROR){
                retCode = CKMS_NETWORK_ERROR;
            }else if(ckmscode == ENDECRY_ENTITY_NOT_IN_SGROUP || ckmscode == ENTITY_NOT_IN_SGROUP_CODE){
                retCode = ENTITY_NOT_IN_GROUP ;
            }else if(ckmscode == CURRENT_DEVICE_UNBIND_ENTITY_CODE){
                retCode = EXIST_NOT_AUTH_DEVICE;
            }else if(ckmscode == CKMS_DECRYPT_FORMATE_ERROR){
                retCode = DECRYPT_DATA_FORMATE_WRONG;
            }else{
                retCode =CKMS_FAIL_CODE;
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            LogUtil.getUtils().e(TAG+ "processSecuException JSONException ");
        }
        return retCode;
    }
    //tangsha@xdja.com 2016-08-08 modify. for ckms refresh . review by self. End

/*[S]add by xienana for CKMS file endecrypt 2016/08/09(rummager:tangsha)*/	
	/**
     * 加密文件
     *
     * @param currentEntity 当前账户
     * @param sGroupId      组id
     * @param srcPath       待加密的文件路径
     * @param encFilePath   加密后文件的路径
     * @return CKMS_SUCC_CODE  解密成功 ;
     *        CKMS_FAIL_CODE  解密失败;
     *        CKMS_EXPIRED_TO_INIT 解密失败,可能需要特别提示
     *        CKMS_NETWORK_ERROR 网络错误
     *        ENTITY_NOT_IN_GROUP 有两种情况：1.sgroup不存在；2.entity不在sgroup里面
     *        EXIST_NOT_AUTH_DEVICE 存在未关联的设备
     * created by xienana on 2016/08/04
     */
    public static int encrptyFile(String currentEntity, String sGroupId,
                                  String srcPath, String encFilePath) {
        if (!TextUtils.isEmpty(srcPath)
                && new File(srcPath).exists()
                && !TextUtils.isEmpty(encFilePath)) {
            CountDownLatch latch = new CountDownLatch(1);
            EnDecryptFileListener encryptFileListener = new EnDecryptFileListener(latch);
            int retCode;
            try {
                SecuritySDKManager.getInstance().encryptFile(currentEntity,
                        sGroupId, srcPath, encFilePath, encryptFileListener);
            } catch (SecurityException e) {
                int errorCode= processSecuException(e);
                LogUtil.getUtils().e(TAG+"encrptyFile processSecuException errorCode" + errorCode+" srcPath "+srcPath);
                return errorCode;
            }catch(IllegalArgumentException e){
                retCode = CKMS_FAIL_CODE;
                LogUtil.getUtils().e(TAG+"encrptyFile IllegalArgumentException "+e.toString());
                return retCode;
            }
            try {
                latch.await();
                retCode = encryptFileListener.getRetCode();
                if (retCode == 0) {
                    return CKMS_SUCC_CODE;
                }else if(retCode == CKMS_EXPIRED_ERROR_CODE || retCode == CKMS_ACCESS_NOTEXIST_CODE){
                    processCkmsExpired();
                    return CKMS_EXPIRED_TO_INIT;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtil.getUtils().e(TAG+"encrptyFile InterruptedException " + e.toString()+" srcPath "+srcPath);
                return CKMS_FAIL_CODE;
            }
        }else{
            LogUtil.getUtils().e(TAG+"encrptyFile check parameter error, srcPath "+srcPath+" encFilePath "+encFilePath);
        }
        return CKMS_FAIL_CODE;
    }


    /**
     * 解密文件
     *
     * @param currentEntity 当前账户
     * @param encFilePath   待解密的文件路径
     * @param decFilePath   解密后的文件路径
     * @return CKMS_SUCC_CODE  解密成功 ;
     *        CKMS_FAIL_CODE  解密失败;
     *        CKMS_EXPIRED_TO_INIT 解密失败,可能需要特别提示
     *        CKMS_NETWORK_ERROR 网络错误
     *        ENTITY_NOT_IN_GROUP 有两种情况：1.sgroup不存在；2.entity不在sgroup里面
     *        EXIST_NOT_AUTH_DEVICE 存在未关联的设备
     * created by xienana on 2016/08/04
     */
    public static int decFile(String currentEntity, String encFilePath, String decFilePath) {
        if (!TextUtils.isEmpty(encFilePath)
                && new File(encFilePath).exists()
                && !TextUtils.isEmpty(decFilePath)) {
            CountDownLatch decLatch = new CountDownLatch(1);
            EnDecryptFileListener decryptFileListener = new EnDecryptFileListener(decLatch);
            int resCode;
            try {
                SecuritySDKManager.getInstance().decryptFile(currentEntity,
                        encFilePath, decFilePath, decryptFileListener);
            } catch (SecurityException e) {
                int errorCode = processSecuException(e);
                LogUtil.getUtils().e(TAG+"decFile SecurityException " + e.toString()+" errorCode "+errorCode+" enFilePath "+encFilePath);
                return errorCode;
            }catch(IllegalArgumentException e){
                resCode = CKMS_FAIL_CODE;
                LogUtil.getUtils().e(TAG+"decFile IllegalArgumentException "+e.toString());
                return resCode;
            }
            try {
                decLatch.await();
                resCode = decryptFileListener.getRetCode();
                if (resCode == 0) {
                    return CKMS_SUCC_CODE;
                } else if(resCode == CKMS_EXPIRED_ERROR_CODE || resCode == CKMS_ACCESS_NOTEXIST_CODE){
                    processCkmsExpired();
                    return CKMS_EXPIRED_TO_INIT;
                }
            } catch (InterruptedException e) {
                LogUtil.getUtils().e(TAG+"decFile InterruptedException " + e.toString()+" enFilePath "+encFilePath);
                e.printStackTrace();
                return CKMS_FAIL_CODE;
            }
        }else{
            LogUtil.getUtils().e(TAG+"decFile check parameter error, encFilePath "+encFilePath+" decFilePath "+decFilePath);
        }
        return CKMS_FAIL_CODE;
    }
	/*[E]add by xienana for CKMS file endecrypt 2016/08/09(rummager:tangsha)*/
	
}
